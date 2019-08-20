// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Json
{
    using System;
    using System.Text;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public static class RowReaderJsonExtensions
    {
        /// <summary>
        /// Project a JSON document from a HybridRow <see cref="RowReader"/>.
        /// </summary>
        /// <param name="reader">The reader to project to JSON.</param>
        /// <param name="str">If successful, the JSON document that corresponds to the <paramref name="reader"/>.</param>
        /// <returns>The result.</returns>
        public static Result ToJson(this ref RowReader reader, out string str)
        {
            return reader.ToJson(new RowReaderJsonSettings("  "), out str);
        }

        /// <summary>
        /// Project a JSON document from a HybridRow <see cref="RowReader"/>.
        /// </summary>
        /// <param name="reader">The reader to project to JSON.</param>
        /// <param name="settings">Settings that control how the JSON document is formatted.</param>
        /// <param name="str">If successful, the JSON document that corresponds to the <paramref name="reader"/>.</param>
        /// <returns>The result.</returns>
        public static Result ToJson(this ref RowReader reader, RowReaderJsonSettings settings, out string str)
        {
            ReaderStringContext ctx = new ReaderStringContext(
                new StringBuilder(),
                new RowReaderJsonSettings(settings.IndentChars, settings.QuoteChar == '\'' ? '\'' : '"'),
                1);

            ctx.Builder.Append("{");
            Result result = RowReaderJsonExtensions.ToJson(ref reader, ctx);
            if (result != Result.Success)
            {
                str = null;
                return result;
            }

            ctx.Builder.Append(ctx.NewLine);
            ctx.Builder.Append("}");
            str = ctx.Builder.ToString();
            return Result.Success;
        }

        private static Result ToJson(ref RowReader reader, ReaderStringContext ctx)
        {
            int index = 0;
            while (reader.Read())
            {
                string path = !reader.Path.IsNull ? $"{ctx.Settings.QuoteChar}{reader.Path}{ctx.Settings.QuoteChar}:" : null;
                if (index != 0)
                {
                    ctx.Builder.Append(',');
                }

                index++;
                ctx.Builder.Append(ctx.NewLine);
                ctx.WriteIndent();
                if (path != null)
                {
                    ctx.Builder.Append(path);
                    ctx.Builder.Append(ctx.Separator);
                }

                Result r;
                char scopeBracket = default;
                char scopeCloseBracket = default;
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.Null:
                    {
                        r = reader.ReadNull(out NullValue _);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append("null");
                        break;
                    }

                    case LayoutCode.Boolean:
                    {
                        r = reader.ReadBool(out bool value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Int8:
                    {
                        r = reader.ReadInt8(out sbyte value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Int16:
                    {
                        r = reader.ReadInt16(out short value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Int32:
                    {
                        r = reader.ReadInt32(out int value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Int64:
                    {
                        r = reader.ReadInt64(out long value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.UInt8:
                    {
                        r = reader.ReadUInt8(out byte value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.UInt16:
                    {
                        r = reader.ReadUInt16(out ushort value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.UInt32:
                    {
                        r = reader.ReadUInt32(out uint value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.UInt64:
                    {
                        r = reader.ReadUInt64(out ulong value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.VarInt:
                    {
                        r = reader.ReadVarInt(out long value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.VarUInt:
                    {
                        r = reader.ReadVarUInt(out ulong value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Float32:
                    {
                        r = reader.ReadFloat32(out float value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Float64:
                    {
                        r = reader.ReadFloat64(out double value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.Float128:
                    {
                        r = reader.ReadFloat128(out Float128 _);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        // ctx.Builder.AppendFormat("High: {0}, Low: {1}\n", value.High, value.Low);
                        Contract.Assert(false, "Float128 are not supported.");
                        break;
                    }

                    case LayoutCode.Decimal:
                    {
                        r = reader.ReadDecimal(out decimal value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        break;
                    }

                    case LayoutCode.DateTime:
                    {
                        r = reader.ReadDateTime(out DateTime value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        ctx.Builder.Append(value);
                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        break;
                    }

                    case LayoutCode.UnixDateTime:
                    {
                        r = reader.ReadUnixDateTime(out UnixDateTime value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value.Milliseconds);
                        break;
                    }

                    case LayoutCode.Guid:
                    {
                        r = reader.ReadGuid(out Guid value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        ctx.Builder.Append(value.ToString());
                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        break;
                    }

                    case LayoutCode.MongoDbObjectId:
                    {
                        r = reader.ReadMongoDbObjectId(out MongoDbObjectId value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        ReadOnlyMemory<byte> bytes = value.ToByteArray();
                        ctx.Builder.Append(bytes.Span.ToHexString());
                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        break;
                    }

                    case LayoutCode.Utf8:
                    {
                        r = reader.ReadString(out Utf8Span value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        ctx.Builder.Append(value.ToString());
                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        break;
                    }

                    case LayoutCode.Binary:
                    {
                        r = reader.ReadBinary(out ReadOnlySpan<byte> value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        ctx.Builder.Append(value.ToHexString());
                        ctx.Builder.Append(ctx.Settings.QuoteChar);
                        break;
                    }

                    case LayoutCode.NullableScope:
                    case LayoutCode.ImmutableNullableScope:
                    {
                        if (!reader.HasValue)
                        {
                            ctx.Builder.Append("null");
                            break;
                        }

                        goto case LayoutCode.TypedTupleScope;
                    }

                    case LayoutCode.ArrayScope:
                    case LayoutCode.ImmutableArrayScope:
                    case LayoutCode.TypedArrayScope:
                    case LayoutCode.ImmutableTypedArrayScope:
                    case LayoutCode.TypedSetScope:
                    case LayoutCode.ImmutableTypedSetScope:
                    case LayoutCode.TypedMapScope:
                    case LayoutCode.ImmutableTypedMapScope:
                    case LayoutCode.TupleScope:
                    case LayoutCode.ImmutableTupleScope:
                    case LayoutCode.TypedTupleScope:
                    case LayoutCode.ImmutableTypedTupleScope:
                    case LayoutCode.TaggedScope:
                    case LayoutCode.ImmutableTaggedScope:
                    case LayoutCode.Tagged2Scope:
                    case LayoutCode.ImmutableTagged2Scope:
                        scopeBracket = '[';
                        scopeCloseBracket = ']';
                        goto case LayoutCode.EndScope;
                    case LayoutCode.ObjectScope:
                    case LayoutCode.ImmutableObjectScope:
                    case LayoutCode.Schema:
                    case LayoutCode.ImmutableSchema:
                        scopeBracket = '{';
                        scopeCloseBracket = '}';
                        goto case LayoutCode.EndScope;

                    case LayoutCode.EndScope:
                    {
                        ctx.Builder.Append(scopeBracket);
                        int snapshot = ctx.Builder.Length;
                        r = reader.ReadScope(new ReaderStringContext(ctx.Builder, ctx.Settings, ctx.Indent + 1), RowReaderJsonExtensions.ToJson);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        if (ctx.Builder.Length != snapshot)
                        {
                            ctx.Builder.Append(ctx.NewLine);
                            ctx.WriteIndent();
                        }

                        ctx.Builder.Append(scopeCloseBracket);
                        break;
                    }

                    default:
                    {
                        Contract.Assert(false, $"Unknown type will be ignored: {reader.Type.LayoutCode}");
                        break;
                    }
                }
            }

            return Result.Success;
        }

        private readonly struct ReaderStringContext
        {
            public readonly int Indent;
            public readonly StringBuilder Builder;
            public readonly RowReaderJsonSettings Settings;
            public readonly string Separator;
            public readonly string NewLine;

            public ReaderStringContext(StringBuilder builder, RowReaderJsonSettings settings, int indent)
            {
                this.Settings = settings;
                this.Separator = settings.IndentChars == null ? "" : " ";
                this.NewLine = settings.IndentChars == null ? "" : "\n";
                this.Indent = indent;
                this.Builder = builder;
            }

            public void WriteIndent()
            {
                string indentChars = this.Settings.IndentChars ?? "";
                for (int i = 0; i < this.Indent; i++)
                {
                    this.Builder.Append(indentChars);
                }
            }
        }
    }
}
