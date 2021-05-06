// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public static class DiagnosticConverter
    {
        public static Result ReaderToString(ref RowReader reader, out string str)
        {
            ReaderStringContext ctx = new ReaderStringContext(new StringBuilder());
            Result result = DiagnosticConverter.ReaderToString(ref reader, ctx);
            if (result != Result.Success)
            {
                str = null;
                return result;
            }

            str = ctx.Builder.ToString();
            return Result.Success;
        }

        public static Result ReaderToDynamic(ref RowReader reader, out Dictionary<Utf8String, object> scope)
        {
            scope = new Dictionary<Utf8String, object>(SamplingUtf8StringComparer.Default);
            return DiagnosticConverter.ReaderToDynamic(ref reader, scope);
        }

        private static Result ReaderToDynamic(ref RowReader reader, object scope)
        {
            while (reader.Read())
            {
                Result r;
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.Null:
                    {
                        r = reader.ReadNull(out NullValue value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Boolean:
                    {
                        r = reader.ReadBool(out bool value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Int8:
                    {
                        r = reader.ReadInt8(out sbyte value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Int16:
                    {
                        r = reader.ReadInt16(out short value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Int32:
                    {
                        r = reader.ReadInt32(out int value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Int64:
                    {
                        r = reader.ReadInt64(out long value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.UInt8:
                    {
                        r = reader.ReadUInt8(out byte value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.UInt16:
                    {
                        r = reader.ReadUInt16(out ushort value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.UInt32:
                    {
                        r = reader.ReadUInt32(out uint value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.UInt64:
                    {
                        r = reader.ReadUInt64(out ulong value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.VarInt:
                    {
                        r = reader.ReadVarInt(out long value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.VarUInt:
                    {
                        r = reader.ReadVarUInt(out ulong value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Float32:
                    {
                        r = reader.ReadFloat32(out float value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Float64:
                    {
                        r = reader.ReadFloat64(out double value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Float128:
                    {
                        r = reader.ReadFloat128(out Float128 value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Decimal:
                    {
                        r = reader.ReadDecimal(out decimal value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.DateTime:
                    {
                        r = reader.ReadDateTime(out DateTime value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.UnixDateTime:
                    {
                        r = reader.ReadUnixDateTime(out UnixDateTime value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Guid:
                    {
                        r = reader.ReadGuid(out Guid value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.MongoDbObjectId:
                    {
                        r = reader.ReadMongoDbObjectId(out MongoDbObjectId value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Utf8:
                    {
                        r = reader.ReadString(out Utf8String value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.Binary:
                    {
                        r = reader.ReadBinary(out byte[] value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, value);
                        break;
                    }

                    case LayoutCode.NullableScope:
                    case LayoutCode.ImmutableNullableScope:
                    {
                        if (!reader.HasValue)
                        {
                            break;
                        }

                        goto case LayoutCode.TypedTupleScope;
                    }

                    case LayoutCode.ObjectScope:
                    case LayoutCode.ImmutableObjectScope:
                    case LayoutCode.Schema:
                    case LayoutCode.ImmutableSchema:
                    {
                        object childScope = new Dictionary<Utf8String, object>(SamplingUtf8StringComparer.Default);
                        r = reader.ReadScope(childScope, DiagnosticConverter.ReaderToDynamic);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, childScope);
                        break;
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
                    {
                        object childScope = new List<object>();
                        r = reader.ReadScope(childScope, DiagnosticConverter.ReaderToDynamic);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        DiagnosticConverter.AddToScope(scope, reader.Path, childScope);
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

        private static void AddToScope(object scope, UtfAnyString path, object value)
        {
            if (scope is List<object> linearScope)
            {
                Contract.Assert(path.IsNull);
                linearScope.Add(value);
            }
            else
            {
                Contract.Assert(!string.IsNullOrWhiteSpace(path.ToString()));
                ((Dictionary<Utf8String, object>)scope)[path.ToUtf8String()] = value;
            }
        }

        private static Result ReaderToString(ref RowReader reader, ReaderStringContext ctx)
        {
            while (reader.Read())
            {
                string path = !reader.Path.IsNull ? $"\"{reader.Path}\"" : "null";
                ctx.Builder.Append(
                    $"{new string(' ', ctx.Indent * 2)}Storage: {reader.Storage}, Path: {path}, Index: {reader.Index}, Type: {reader.Type.Name + reader.TypeArgs.ToString()}, Value: ");

                Result r;
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.Null:
                    {
                        r = reader.ReadNull(out NullValue _);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.AppendLine("null");
                        break;
                    }

                    case LayoutCode.Boolean:
                    case LayoutCode.BooleanFalse:
                    {
                        r = reader.ReadBool(out bool value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
                        break;
                    }

                    case LayoutCode.Float128:
                    {
                        r = reader.ReadFloat128(out Float128 value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.AppendFormat("High: {0}, Low: {1}\n", value.High, value.Low);
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
                        ctx.Builder.AppendLine();
                        break;
                    }

                    case LayoutCode.DateTime:
                    {
                        r = reader.ReadDateTime(out DateTime value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.Append(value);
                        ctx.Builder.AppendLine();
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
                        ctx.Builder.AppendLine();
                        break;
                    }

                    case LayoutCode.Guid:
                    {
                        r = reader.ReadGuid(out Guid value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.AppendLine(value.ToString());
                        break;
                    }

                    case LayoutCode.MongoDbObjectId:
                    {
                        r = reader.ReadMongoDbObjectId(out MongoDbObjectId value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ReadOnlyMemory<byte> bytes = value.ToByteArray();
                        ctx.Builder.AppendLine(ByteConverter.ToHex(bytes.Span));
                        break;
                    }

                    case LayoutCode.Utf8:
                    {
                        r = reader.ReadString(out Utf8String value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.AppendLine(value.ToString());
                        break;
                    }

                    case LayoutCode.Binary:
                    {
                        r = reader.ReadBinary(out ReadOnlySpan<byte> value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.AppendLine(ByteConverter.ToHex(value));
                        break;
                    }

                    case LayoutCode.NullableScope:
                    case LayoutCode.ImmutableNullableScope:
                    {
                        if (!reader.HasValue)
                        {
                            ctx.Builder.AppendLine("null");
                            break;
                        }

                        goto case LayoutCode.TypedTupleScope;
                    }

                    case LayoutCode.ObjectScope:
                    case LayoutCode.ImmutableObjectScope:
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
                    case LayoutCode.Schema:
                    case LayoutCode.ImmutableSchema:
                    {
                        ctx.Builder.AppendLine("{");
                        r = reader.ReadScope(new ReaderStringContext(ctx.Builder, ctx.Indent + 1), DiagnosticConverter.ReaderToString);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        ctx.Builder.AppendLine($"{new string(' ', ctx.Indent * 2)}}}");
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

            public ReaderStringContext(StringBuilder builder, int indent = 0)
            {
                this.Indent = indent;
                this.Builder = builder;
            }
        }
    }
}
