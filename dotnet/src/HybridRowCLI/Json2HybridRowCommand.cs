// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Globalization;
    using System.IO;
    using System.Runtime.CompilerServices;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.Extensions.CommandLineUtils;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Linq;

    public class Json2HybridRowCommand
    {
        private const int InitialCapacity = 2 * 1024 * 1024;

        private Json2HybridRowCommand()
        {
        }

        public bool Verbose { get; set; }

        public string JsonFile { get; set; }

        public string OutputFile { get; set; }

        public string NamespaceFile { get; set; }

        public string TableName { get; set; }

        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "json2hr",
                command =>
                {
                    command.Description = "Convert a JSON document to hybrid row.";
                    command.ExtendedHelpText =
                        "Convert textual JSON document into a HybridRow.\n\n" +
                        "The json2hr command accepts files in two formats:\n" +
                        "\t* A JSON file whose top-level element is a JSON Object. This file is converted to a HybridRow binary " +
                        "\t  file containing exactly 1 record.\n" +
                        "\t* A JSON file whose top-level element is a JSON Array. This file is convert to a A HybridRow RecordIO " +
                        "\t  file containing 0 or more records, one for each element of the JSON Array.";

                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.", CommandOptionType.NoValue);

                    CommandOption namespaceOpt = command.Option(
                        "-n|--namespace",
                        "File containing the schema namespace.",
                        CommandOptionType.SingleValue);

                    CommandOption tableNameOpt = command.Option(
                        "-tn|--tablename",
                        "The table schema (when using -namespace).  Default: null.",
                        CommandOptionType.SingleValue);

                    CommandArgument jsonOpt = command.Argument("json", "File containing json to convert.");
                    CommandArgument outputOpt = command.Argument("output", "Output file to contain the HybridRow conversion.");

                    command.OnExecute(
                        () =>
                        {
                            Json2HybridRowCommand config = new Json2HybridRowCommand
                            {
                                Verbose = verboseOpt.HasValue(),
                                NamespaceFile = namespaceOpt.Value(),
                                TableName = tableNameOpt.Value(),
                                JsonFile = jsonOpt.Value,
                                OutputFile = outputOpt.Value
                            };

                            return config.OnExecuteAsync().Result;
                        });
                });
        }

        public async Task<int> OnExecuteAsync()
        {
            LayoutResolver globalResolver = await SchemaUtil.CreateResolverAsync(this.NamespaceFile, this.Verbose);
            string sdl = string.IsNullOrWhiteSpace(this.NamespaceFile) ? null : await File.ReadAllTextAsync(this.NamespaceFile);
            MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(Json2HybridRowCommand.InitialCapacity);
            using (Stream stm = new FileStream(this.JsonFile, FileMode.Open))
            using (TextReader txt = new StreamReader(stm))
            using (JsonReader reader = new JsonTextReader(txt))
            {
                // Turn off any special parsing conversions.  Just raw JSON tokens.
                reader.DateParseHandling = DateParseHandling.None;
                reader.FloatParseHandling = FloatParseHandling.Double;

                if (!await reader.ReadAsync())
                {
                    await Console.Error.WriteLineAsync("Error: file is empty.");
                    return -1;
                }

                switch (reader.TokenType)
                {
                    case JsonToken.StartObject:
                    {
                        JObject obj = await JObject.LoadAsync(reader);
                        Result r = this.ProcessObject(obj, globalResolver, resizer, out ReadOnlyMemory<byte> record);
                        if (r != Result.Success)
                        {
                            Console.WriteLine($"Error while writing record: 0 to HybridRow(s): {this.OutputFile}");
                            return -1;
                        }

                        // Write the output.
                        using (Stream outputStm = new FileStream(this.OutputFile, FileMode.Create))
                        {
                            await outputStm.WriteAsync(record);
                            Console.WriteLine($"Wrote (1) HybridRow: {this.OutputFile}");
                        }

                        return 0;
                    }

                    case JsonToken.StartArray:
                    {
                        using (Stream outputStm = new FileStream(this.OutputFile, FileMode.Create))
                        {
                            long totalWritten = 0;
                            Segment segment = new Segment("JSON conversion from HybridRowCLI json2hr", sdl);
                            Result r = await outputStm.WriteRecordIOAsync(
                                segment,
                                async index =>
                                {
                                    if (!await reader.ReadAsync() || reader.TokenType == JsonToken.EndArray)
                                    {
                                        return (Result.Success, default);
                                    }

                                    JObject obj = await JObject.LoadAsync(reader);
                                    Result r2 = this.ProcessObject(obj, globalResolver, resizer, out ReadOnlyMemory<byte> record);
                                    if (r2 != Result.Success)
                                    {
                                        return (r2, default);
                                    }

                                    totalWritten++;
                                    return (r2, record);
                                });

                            if (r != Result.Success)
                            {
                                Console.WriteLine($"Error while writing record: {totalWritten} to HybridRow(s): {this.OutputFile}");
                                return -1;
                            }

                            Console.WriteLine($"Wrote ({totalWritten}) HybridRow(s): {this.OutputFile}");
                        }

                        return 0;
                    }

                    default:
                        await Console.Error.WriteLineAsync("Error: Only JSON documents with top-level Object/Array supported.");
                        return -1;
                }
            }
        }

        private static string TrimPath(JContainer parent, string path)
        {
            if (string.IsNullOrEmpty(parent.Path))
            {
                return path;
            }

            return path.Substring(parent.Path.Length + 1);
        }

        /// <summary>Returns true if the type code indicates a linear scope (i.e. array like).</summary>
        /// <param name="code">The scope type code.</param>
        /// </remarks>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static bool IsLinearScope(LayoutCode code)
        {
            if (code < LayoutCode.ObjectScope || code >= LayoutCode.EndScope)
            {
                return false;
            }

            const ulong bitmask =
                    (0x1UL << (int)(LayoutCode.ArrayScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableArrayScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.TypedArrayScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableTypedArrayScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.TypedSetScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableTypedSetScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.TypedMapScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableTypedMapScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.TypedTupleScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableTypedTupleScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.TaggedScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableTaggedScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.Tagged2Scope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableTagged2Scope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.NullableScope - LayoutCode.ObjectScope)) |
                    (0x1UL << (int)(LayoutCode.ImmutableNullableScope - LayoutCode.ObjectScope))
                ;

            return ((0x1UL << (code - LayoutCode.ObjectScope)) & bitmask) != 0;
        }

        private Result ProcessObject(JObject obj, LayoutResolver resolver, MemorySpanResizer<byte> resizer, out ReadOnlyMemory<byte> record)
        {
            Contract.Requires(obj.Type == JTokenType.Object);
            SchemaId tableId = SystemSchema.EmptySchemaId;
            if (!string.IsNullOrWhiteSpace(this.TableName))
            {
                tableId = (resolver as LayoutResolverNamespace)?.Namespace?.Schemas?.Find(s => s.Name == this.TableName)?.SchemaId ??
                          SchemaId.Invalid;

                if (tableId == SchemaId.Invalid)
                {
                    Console.Error.WriteLine($"Error: schema {this.TableName} could not be found in {this.NamespaceFile}.");
                    record = default;
                    return Result.Failure;
                }
            }

            Layout layout = resolver.Resolve(tableId);
            RowBuffer row = new RowBuffer(resizer.Memory.Length, resizer);
            row.InitLayout(HybridRowVersion.V1, layout, resolver);
            WriterContext ctx = new WriterContext { Token = obj, PathPrefix = "" };
            Result r = RowWriter.WriteBuffer(ref row, ctx, this.WriteObject);
            if (r != Result.Success)
            {
                record = default;
                return r;
            }

            record = resizer.Memory.Slice(0, row.Length);
            return Result.Success;
        }

        private Result WriteArray(ref RowWriter writer, TypeArgument typeArg, WriterContext ctx)
        {
            JArray obj = ctx.Token as JArray;
            TypeArgument propTypeArgument = typeArg.TypeArgs.Count > 0 ? typeArg.TypeArgs[0] : default;
            foreach (JToken token in obj.Children())
            {
                Result r = this.WriteJToken(ref writer, ctx.PathPrefix, null, propTypeArgument, token);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        private Result WriteObject(ref RowWriter writer, TypeArgument typeArg, WriterContext ctx)
        {
            JObject obj = ctx.Token as JObject;
            Layout layout = writer.Layout;
            if (typeArg.TypeArgs.SchemaId != SchemaId.Invalid)
            {
                layout = writer.Resolver.Resolve(typeArg.TypeArgs.SchemaId);
                ctx.PathPrefix = "";
            }

            foreach (JProperty p in obj.Properties())
            {
                if (!p.HasValues)
                {
                    continue;
                }

                string path = Json2HybridRowCommand.TrimPath(obj, p.Path);
                TypeArgument propTypeArg = default;
                string propFullPath = ctx.PathPrefix + path;
                if (layout.TryFind(propFullPath, out LayoutColumn col))
                {
                    propTypeArg = col.TypeArg;
                }

                Result r = this.WriteJToken(ref writer, ctx.PathPrefix, path, propTypeArg, p.Value);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        private Result WriteJToken(ref RowWriter writer, string pathPrefix, string path, TypeArgument typeArg, JToken token)
        {
            TypeArgument propTypeArg;
            LayoutCode code = typeArg.Type?.LayoutCode ?? LayoutCode.Invalid;
            switch (token.Type)
            {
                case JTokenType.Object:
                    propTypeArg = typeArg.Type is LayoutUDT ? typeArg : new TypeArgument(LayoutType.Object);
                    WriterContext ctx1 = new WriterContext { Token = token.Value<JObject>(), PathPrefix = $"{pathPrefix}{path}." };
                    return writer.WriteScope(path, propTypeArg, ctx1, this.WriteObject);
                case JTokenType.Array:
                    propTypeArg = Json2HybridRowCommand.IsLinearScope(typeArg.Type?.LayoutCode ?? LayoutCode.Invalid)
                        ? typeArg
                        : new TypeArgument(LayoutType.Array);

                    WriterContext ctx2 = new WriterContext { Token = token.Value<JArray>(), PathPrefix = $"{pathPrefix}{path}[]." };
                    return writer.WriteScope(path, propTypeArg, ctx2, this.WriteArray);
                case JTokenType.Integer:
                {
                    long value = token.Value<long>();
                    try
                    {
                        switch (code)
                        {
                            case LayoutCode.Int8:
                                return writer.WriteInt8(path, (sbyte)value);
                            case LayoutCode.Int16:
                                return writer.WriteInt16(path, (short)value);
                            case LayoutCode.Int32:
                                return writer.WriteInt32(path, (int)value);
                            case LayoutCode.Int64:
                                return writer.WriteInt64(path, value);
                            case LayoutCode.UInt8:
                                return writer.WriteUInt8(path, (byte)value);
                            case LayoutCode.UInt16:
                                return writer.WriteUInt16(path, (ushort)value);
                            case LayoutCode.UInt32:
                                return writer.WriteUInt32(path, (uint)value);
                            case LayoutCode.UInt64:
                                return writer.WriteUInt64(path, (ulong)value);
                            case LayoutCode.VarInt:
                                return writer.WriteVarInt(path, value);
                            case LayoutCode.VarUInt:
                                return writer.WriteVarUInt(path, (ulong)value);
                            case LayoutCode.Float32:
                                return writer.WriteFloat32(path, value);
                            case LayoutCode.Float64:
                                return writer.WriteFloat64(path, value);
                            case LayoutCode.Decimal:
                                return writer.WriteDecimal(path, value);
                            case LayoutCode.Utf8:
                                return writer.WriteString(path, value.ToString());
                        }
                    }
                    catch (OverflowException)
                    {
                        // Ignore overflow, and just write value as a long.
                    }

                    return writer.WriteInt64(path, value);
                }

                case JTokenType.Float:
                {
                    double value = token.Value<double>();
                    try
                    {
                        switch (code)
                        {
                            case LayoutCode.Int8:
                                return writer.WriteInt8(path, (sbyte)value);
                            case LayoutCode.Int16:
                                return writer.WriteInt16(path, (short)value);
                            case LayoutCode.Int32:
                                return writer.WriteInt32(path, (int)value);
                            case LayoutCode.Int64:
                                return writer.WriteInt64(path, (long)value);
                            case LayoutCode.UInt8:
                                return writer.WriteUInt8(path, (byte)value);
                            case LayoutCode.UInt16:
                                return writer.WriteUInt16(path, (ushort)value);
                            case LayoutCode.UInt32:
                                return writer.WriteUInt32(path, (uint)value);
                            case LayoutCode.UInt64:
                                return writer.WriteUInt64(path, (ulong)value);
                            case LayoutCode.VarInt:
                                return writer.WriteVarInt(path, (long)value);
                            case LayoutCode.VarUInt:
                                return writer.WriteVarUInt(path, (ulong)value);
                            case LayoutCode.Float32:
                                return writer.WriteFloat32(path, (float)value);
                            case LayoutCode.Float64:
                                return writer.WriteFloat64(path, value);
                            case LayoutCode.Decimal:
                                return writer.WriteDecimal(path, (decimal)value);
                            case LayoutCode.Utf8:
                                return writer.WriteString(path, value.ToString(CultureInfo.InvariantCulture));
                        }
                    }
                    catch (OverflowException)
                    {
                        // Ignore overflow, and just write value as a double.
                    }

                    return writer.WriteFloat64(path, value);
                }

                case JTokenType.String:
                    switch (code)
                    {
                        case LayoutCode.Boolean:
                        {
                            if (!bool.TryParse(token.Value<string>(), out bool value))
                            {
                                goto default;
                            }

                            return writer.WriteBool(path, value);
                        }

                        case LayoutCode.Int8:
                        {
                            if (!sbyte.TryParse(token.Value<string>(), out sbyte value))
                            {
                                goto default;
                            }

                            return writer.WriteInt8(path, value);
                        }

                        case LayoutCode.Int16:
                        {
                            if (!short.TryParse(token.Value<string>(), out short value))
                            {
                                goto default;
                            }

                            return writer.WriteInt16(path, value);
                        }

                        case LayoutCode.Int32:
                        {
                            if (!int.TryParse(token.Value<string>(), out int value))
                            {
                                goto default;
                            }

                            return writer.WriteInt32(path, value);
                        }

                        case LayoutCode.Int64:
                        {
                            if (!long.TryParse(token.Value<string>(), out long value))
                            {
                                goto default;
                            }

                            return writer.WriteInt64(path, value);
                        }

                        case LayoutCode.UInt8:
                        {
                            if (!byte.TryParse(token.Value<string>(), out byte value))
                            {
                                goto default;
                            }

                            return writer.WriteUInt8(path, value);
                        }

                        case LayoutCode.UInt16:
                        {
                            if (!ushort.TryParse(token.Value<string>(), out ushort value))
                            {
                                goto default;
                            }

                            return writer.WriteUInt16(path, value);
                        }

                        case LayoutCode.UInt32:
                        {
                            if (!uint.TryParse(token.Value<string>(), out uint value))
                            {
                                goto default;
                            }

                            return writer.WriteUInt32(path, value);
                        }

                        case LayoutCode.UInt64:
                        {
                            if (!ulong.TryParse(token.Value<string>(), out ulong value))
                            {
                                goto default;
                            }

                            return writer.WriteUInt64(path, value);
                        }

                        case LayoutCode.VarInt:
                        {
                            if (!long.TryParse(token.Value<string>(), out long value))
                            {
                                goto default;
                            }

                            return writer.WriteVarInt(path, value);
                        }

                        case LayoutCode.VarUInt:
                        {
                            if (!ulong.TryParse(token.Value<string>(), out ulong value))
                            {
                                goto default;
                            }

                            return writer.WriteVarUInt(path, value);
                        }

                        case LayoutCode.Float32:
                        {
                            if (!float.TryParse(token.Value<string>(), out float value))
                            {
                                goto default;
                            }

                            return writer.WriteFloat32(path, value);
                        }

                        case LayoutCode.Float64:
                        {
                            if (!double.TryParse(token.Value<string>(), out double value))
                            {
                                goto default;
                            }

                            return writer.WriteFloat64(path, value);
                        }

                        case LayoutCode.Decimal:
                        {
                            if (!decimal.TryParse(token.Value<string>(), out decimal value))
                            {
                                goto default;
                            }

                            return writer.WriteDecimal(path, value);
                        }

                        case LayoutCode.DateTime:
                        {
                            IFormatProvider provider = CultureInfo.CurrentCulture;
                            DateTimeStyles style = DateTimeStyles.AdjustToUniversal |
                                                   DateTimeStyles.AllowWhiteSpaces |
                                                   DateTimeStyles.AssumeUniversal;

                            if (!DateTime.TryParse(token.Value<string>(), provider, style, out DateTime value))
                            {
                                goto default;
                            }

                            return writer.WriteDateTime(path, value);
                        }

                        case LayoutCode.Guid:
                        {
                            string s = token.Value<string>();

                            // If the guid is quoted then remove the quotes.
                            if (s.Length > 2 && s[0] == '"' && s[s.Length - 1] == '"')
                            {
                                s = s.Substring(1, s.Length - 2);
                            }

                            if (!Guid.TryParse(s, out Guid value))
                            {
                                goto default;
                            }

                            return writer.WriteGuid(path, value);
                        }

                        case LayoutCode.Binary:
                        {
                            string s = token.Value<string>();
                            try
                            {
                                byte[] newBytes = Convert.FromBase64String(s);
                                return writer.WriteBinary(path, newBytes.AsSpan());
                            }
                            catch (Exception)
                            {
                                // fall through and try hex.
                            }

                            try
                            {
                                byte[] newBytes = ByteConverter.ToBytes(s);
                                return writer.WriteBinary(path, newBytes.AsSpan());
                            }
                            catch (Exception)
                            {
                                goto default;
                            }
                        }

                        case LayoutCode.UnixDateTime:
                        {
                            if (!long.TryParse(token.Value<string>(), out long value))
                            {
                                goto default;
                            }

                            return writer.WriteUnixDateTime(path, new UnixDateTime(value));
                        }

                        case LayoutCode.MongoDbObjectId:
                        {
                            string s = token.Value<string>();
                            try
                            {
                                byte[] newBytes = Convert.FromBase64String(s);
                                return writer.WriteMongoDbObjectId(path, new MongoDbObjectId(newBytes.AsSpan()));
                            }
                            catch (Exception)
                            {
                                // fall through and try hex.
                            }

                            try
                            {
                                byte[] newBytes = ByteConverter.ToBytes(s);
                                return writer.WriteMongoDbObjectId(path, new MongoDbObjectId(newBytes.AsSpan()));
                            }
                            catch (Exception)
                            {
                                goto default;
                            }
                        }

                        default:
                            return writer.WriteString(path, token.Value<string>());
                    }

                case JTokenType.Boolean:
                {
                    bool value = token.Value<bool>();
                    switch (code)
                    {
                        case LayoutCode.Int8:
                            return writer.WriteInt8(path, (sbyte)(value ? 1 : 0));
                        case LayoutCode.Int16:
                            return writer.WriteInt16(path, (short)(value ? 1 : 0));
                        case LayoutCode.Int32:
                            return writer.WriteInt32(path, value ? 1 : 0);
                        case LayoutCode.Int64:
                            return writer.WriteInt64(path, value ? 1L : 0L);
                        case LayoutCode.UInt8:
                            return writer.WriteUInt8(path, (byte)(value ? 1 : 0));
                        case LayoutCode.UInt16:
                            return writer.WriteUInt16(path, (ushort)(value ? 1 : 0));
                        case LayoutCode.UInt32:
                            return writer.WriteUInt32(path, (uint)(value ? 1 : 0));
                        case LayoutCode.UInt64:
                            return writer.WriteUInt64(path, value ? 1U : 0U);
                        case LayoutCode.VarInt:
                            return writer.WriteVarInt(path, value ? 1L : 0L);
                        case LayoutCode.VarUInt:
                            return writer.WriteVarUInt(path, value ? 1U : 0U);
                        case LayoutCode.Float32:
                            return writer.WriteFloat32(path, value ? 1 : 0);
                        case LayoutCode.Float64:
                            return writer.WriteFloat64(path, value ? 1 : 0);
                        case LayoutCode.Decimal:
                            return writer.WriteDecimal(path, value ? 1 : 0);
                        case LayoutCode.Utf8:
                            return writer.WriteString(path, value ? "true" : "false");
                        default:
                            return writer.WriteBool(path, value);
                    }
                }

                case JTokenType.Null:
                    switch (code)
                    {
                        case LayoutCode.Null:
                        case LayoutCode.Invalid:
                            return writer.WriteNull(path);
                        default:

                            // Any other schematized type then just don't write it.
                            return Result.Success;
                    }

                case JTokenType.Comment:
                    return Result.Success;
                default:
                    Console.Error.WriteLine($"Error: Unexpected JSON token type: {token.Type}.");
                    return Result.InvalidRow;
            }
        }

        private struct WriterContext
        {
            public JToken Token;
            public string PathPrefix;
        }
    }
}
