// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Globalization;
    using System.IO;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Extensions.CommandLineUtils;

    public class Csv2HybridRowCommand
    {
        private const int InitialCapacity = 2 * 1024 * 1024;

        private bool verbose;
        private long limit;
        private string csvFile;
        private string outputFile;
        private string namespaceFile;
        private string tableName;

        private Csv2HybridRowCommand()
        {
        }

        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "csv2hr",
                command =>
                {
                    command.Description = "Convert a CSV data set to hybrid row.";
                    command.ExtendedHelpText =
                        "Convert textual Comma-Separated-Values data set into a HybridRow.\n\n" +
                        "The csv2hr command accepts CSV files in the following format:\n" +
                        "\t* First line contains column names, followed by 0 or more blank rows.\n" +
                        "\t* All subsequent lines contain rows. A row with too few values treats \n" +
                        "\t  all missing values as the empty string. Lines consistent entirely of \n" +
                        "\t  whitespace are omitted. Columns without a name are discarded.";

                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.", CommandOptionType.NoValue);

                    CommandOption limitOpt = command.Option("--limit", "Limit the number of input rows processed.", CommandOptionType.SingleValue);

                    CommandOption namespaceOpt = command.Option(
                        "-n|--namespace",
                        "File containing the schema namespace.",
                        CommandOptionType.SingleValue);

                    CommandOption tableNameOpt = command.Option(
                        "-tn|--tablename",
                        "The table schema (when using -namespace).  Default: null.",
                        CommandOptionType.SingleValue);

                    CommandArgument csvOpt = command.Argument("csv", "File containing csv to convert.");
                    CommandArgument outputOpt = command.Argument("output", "Output file to contain the HybridRow conversion.");

                    command.OnExecute(
                        () =>
                        {
                            Csv2HybridRowCommand config = new Csv2HybridRowCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                limit = !limitOpt.HasValue() ? long.MaxValue : long.Parse(limitOpt.Value()),
                                namespaceFile = namespaceOpt.Value().Trim(),
                                tableName = tableNameOpt.Value().Trim(),
                                csvFile = csvOpt.Value.Trim(),
                                outputFile = outputOpt.Value.Trim()
                            };

                            if (string.IsNullOrWhiteSpace(config.csvFile))
                            {
                                throw new CommandParsingException(command, "Error: Input file MUST be provided.");
                            }

                            if (string.IsNullOrWhiteSpace(config.outputFile))
                            {
                                throw new CommandParsingException(command, "Error: Output file MUST be provided.");
                            }

                            if (config.csvFile == config.outputFile)
                            {
                                throw new CommandParsingException(command, "Error: Input and Output files MUST be different.");
                            }

                            return config.OnExecuteAsync().Result;
                        });
                });
        }

        private static Result ProcessLine(
            Utf8String[] paths,
            string line,
            LayoutResolver resolver,
            Layout layout,
            MemorySpanResizer<byte> resizer,
            out ReadOnlyMemory<byte> record)
        {
            Contract.Requires(paths != null);
            Contract.Requires(line != null);

            RowBuffer row = new RowBuffer(resizer.Memory.Length, resizer);
            row.InitLayout(HybridRowVersion.V1, layout, resolver);
            WriterContext ctx = new WriterContext { Paths = paths, Line = line };
            Result r = RowWriter.WriteBuffer(ref row, ctx, Csv2HybridRowCommand.WriteLine);
            if (r != Result.Success)
            {
                record = default;
                return r;
            }

            record = resizer.Memory.Slice(0, row.Length);
            return Result.Success;
        }

        private static Result WriteLine(ref RowWriter writer, TypeArgument typeArg, WriterContext ctx)
        {
            ReadOnlySpan<char> remaining = ctx.Line.AsSpan();
            foreach (Utf8String path in ctx.Paths)
            {
                int comma = remaining.IndexOf(',');
                ReadOnlySpan<char> fieldValue = comma == -1 ? remaining : remaining.Slice(0, comma);
                remaining = comma == -1 ? ReadOnlySpan<char>.Empty : remaining.Slice(comma + 1);
                Result r = Csv2HybridRowCommand.WriteField(ref writer, path, fieldValue);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        private static Result WriteField(ref RowWriter writer, Utf8String path, ReadOnlySpan<char> fieldValue)
        {
            writer.Layout.TryFind(path, out LayoutColumn col);
            switch (col?.Type?.LayoutCode ?? LayoutCode.Invalid)
            {
                case LayoutCode.Boolean:
                {
                    if (!bool.TryParse(fieldValue.AsString(), out bool value))
                    {
                        goto default;
                    }

                    return writer.WriteBool(path, value);
                }

                case LayoutCode.Int8:
                {
                    if (!sbyte.TryParse(fieldValue.AsString(), out sbyte value))
                    {
                        goto default;
                    }

                    return writer.WriteInt8(path, value);
                }

                case LayoutCode.Int16:
                {
                    if (!short.TryParse(fieldValue.AsString(), out short value))
                    {
                        goto default;
                    }

                    return writer.WriteInt16(path, value);
                }

                case LayoutCode.Int32:
                {
                    if (!int.TryParse(fieldValue.AsString(), out int value))
                    {
                        goto default;
                    }

                    return writer.WriteInt32(path, value);
                }

                case LayoutCode.Int64:
                {
                    if (!long.TryParse(fieldValue.AsString(), out long value))
                    {
                        goto default;
                    }

                    return writer.WriteInt64(path, value);
                }

                case LayoutCode.UInt8:
                {
                    if (!byte.TryParse(fieldValue.AsString(), out byte value))
                    {
                        goto default;
                    }

                    return writer.WriteUInt8(path, value);
                }

                case LayoutCode.UInt16:
                {
                    if (!ushort.TryParse(fieldValue.AsString(), out ushort value))
                    {
                        goto default;
                    }

                    return writer.WriteUInt16(path, value);
                }

                case LayoutCode.UInt32:
                {
                    if (!uint.TryParse(fieldValue.AsString(), out uint value))
                    {
                        goto default;
                    }

                    return writer.WriteUInt32(path, value);
                }

                case LayoutCode.UInt64:
                {
                    if (!ulong.TryParse(fieldValue.AsString(), out ulong value))
                    {
                        goto default;
                    }

                    return writer.WriteUInt64(path, value);
                }

                case LayoutCode.VarInt:
                {
                    if (!long.TryParse(fieldValue.AsString(), out long value))
                    {
                        goto default;
                    }

                    return writer.WriteVarInt(path, value);
                }

                case LayoutCode.VarUInt:
                {
                    if (!ulong.TryParse(fieldValue.AsString(), out ulong value))
                    {
                        goto default;
                    }

                    return writer.WriteVarUInt(path, value);
                }

                case LayoutCode.Float32:
                {
                    if (!float.TryParse(fieldValue.AsString(), out float value))
                    {
                        goto default;
                    }

                    return writer.WriteFloat32(path, value);
                }

                case LayoutCode.Float64:
                {
                    if (!double.TryParse(fieldValue.AsString(), out double value))
                    {
                        goto default;
                    }

                    return writer.WriteFloat64(path, value);
                }

                case LayoutCode.Decimal:
                {
                    if (!decimal.TryParse(fieldValue.AsString(), out decimal value))
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

                    if (!DateTime.TryParse(fieldValue.AsString(), provider, style, out DateTime value))
                    {
                        goto default;
                    }

                    return writer.WriteDateTime(path, value);
                }

                case LayoutCode.Guid:
                {
                    string s = fieldValue.AsString();

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
                    try
                    {
                        byte[] newBytes = Convert.FromBase64String(fieldValue.AsString());
                        return writer.WriteBinary(path, newBytes.AsSpan());
                    }
                    catch (Exception)
                    {
                        // fall through and try hex.
                    }

                    if (fieldValue.TryParseHexString(out byte[] fromHexBytes))
                    {
                        return writer.WriteBinary(path, fromHexBytes);
                    }

                    goto default;
                }

                case LayoutCode.UnixDateTime:
                {
                    if (!long.TryParse(fieldValue.AsString(), out long value))
                    {
                        goto default;
                    }

                    return writer.WriteUnixDateTime(path, new UnixDateTime(value));
                }

                case LayoutCode.MongoDbObjectId:
                {
                    string s = fieldValue.AsString();
                    try
                    {
                        byte[] newBytes = Convert.FromBase64String(s);
                        return writer.WriteMongoDbObjectId(path, new MongoDbObjectId(newBytes.AsSpan()));
                    }
                    catch (Exception)
                    {
                        // fall through and try hex.
                    }

                    if (fieldValue.TryParseHexString(out byte[] fromHexBytes))
                    {
                        return writer.WriteMongoDbObjectId(path, new MongoDbObjectId(fromHexBytes));
                    }

                    goto default;
                }

                default:
                    return writer.WriteString(path, fieldValue.AsString());
            }
        }

        private async Task<int> OnExecuteAsync()
        {
            LayoutResolver resolver = await SchemaUtil.CreateResolverAsync(this.namespaceFile, this.verbose);
            string sdl = string.IsNullOrWhiteSpace(this.namespaceFile) ? null : await File.ReadAllTextAsync(this.namespaceFile);
            MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(Csv2HybridRowCommand.InitialCapacity);
            using (Stream stm = new FileStream(this.csvFile, FileMode.Open))
            using (TextReader txt = new StreamReader(stm))
            using (Stream outputStm = new FileStream(this.outputFile, FileMode.Create))
            {
                // Read the CSV "schema".
                string fieldNamesLine = await txt.ReadLineAsync();
                while (fieldNamesLine != null && string.IsNullOrWhiteSpace(fieldNamesLine))
                {
                    fieldNamesLine = await txt.ReadLineAsync();
                }

                if (fieldNamesLine == null)
                {
                    await Console.Error.WriteLineAsync($"Input file contains no schema: {this.csvFile}");
                    return -1;
                }

                string[] fieldNames = fieldNamesLine.Split(',');
                if (!(from c in fieldNames where !string.IsNullOrWhiteSpace(c) select c).Any())
                {
                    await Console.Error.WriteLineAsync($"All columns are ignored. Does this file have field headers?: {this.csvFile}");
                    return -1;
                }

                Utf8String[] paths = (from c in fieldNames select Utf8String.TranscodeUtf16(c)).ToArray();

                SchemaId tableId = SystemSchema.EmptySchemaId;
                if (!string.IsNullOrWhiteSpace(this.tableName))
                {
                    tableId = (resolver as LayoutResolverNamespace)?.Namespace?.Schemas?.Find(s => s.Name == this.tableName)?.SchemaId ??
                              SchemaId.Invalid;

                    if (tableId == SchemaId.Invalid)
                    {
                        await Console.Error.WriteLineAsync($"Error: schema {this.tableName} could not be found in {this.namespaceFile}.");
                        return -1;
                    }
                }

                Layout layout = resolver.Resolve(tableId);

                long totalWritten = 0;
                Segment segment = new Segment("CSV conversion from HybridRowCLI csv2hr", sdl);
                Result r = await outputStm.WriteRecordIOAsync(
                    segment,
                    async index =>
                    {
                        if (totalWritten >= this.limit)
                        {
                            return (Result.Success, default);
                        }

                        string line = await txt.ReadLineAsync();
                        while (line != null && string.IsNullOrWhiteSpace(line))
                        {
                            line = await txt.ReadLineAsync();
                        }

                        if (line == null)
                        {
                            return (Result.Success, default);
                        }

                        Result r2 = Csv2HybridRowCommand.ProcessLine(paths, line, resolver, layout, resizer, out ReadOnlyMemory<byte> record);
                        if (r2 != Result.Success)
                        {
                            return (r2, default);
                        }

                        totalWritten++;
                        return (r2, record);
                    });

                if (r != Result.Success)
                {
                    Console.WriteLine($"Error while writing record: {totalWritten} to HybridRow(s): {this.outputFile}");
                    return -1;
                }

                Console.WriteLine($"Wrote ({totalWritten}) HybridRow(s): {this.outputFile}");
                return 0;
            }
        }

        private struct WriterContext
        {
            public Utf8String[] Paths;
            public string Line;
        }
    }
}
