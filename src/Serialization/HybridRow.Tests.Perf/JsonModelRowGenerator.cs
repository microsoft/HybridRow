// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public ref struct JsonModelRowGenerator
    {
        private RowBuffer row;

        public JsonModelRowGenerator(int capacity, Layout layout, LayoutResolver resolver, ISpanResizer<byte> resizer = default)
        {
            this.row = new RowBuffer(capacity, resizer);
            this.row.InitLayout(HybridRowVersion.V1, layout, resolver);
        }

        public int Length => this.row.Length;

        public byte[] ToArray() => this.row.ToArray();

        public void WriteTo(Stream stream)
        {
            this.row.WriteTo(stream);
        }

        public bool ReadFrom(Stream stream, int length)
        {
            return this.row.ReadFrom(stream, length, HybridRowVersion.V1, this.row.Resolver);
        }

        public void Reset()
        {
            Layout layout = this.row.Resolver.Resolve(this.row.Header.SchemaId);
            this.row.InitLayout(HybridRowVersion.V1, layout, this.row.Resolver);
        }

        public RowReader GetReader()
        {
            return new RowReader(ref this.row);
        }

        public Result WriteBuffer(Dictionary<Utf8String, object> value)
        {
            return RowWriter.WriteBuffer(
                ref this.row,
                value,
                (ref RowWriter writer, TypeArgument typeArg, Dictionary<Utf8String, object> dict) =>
                {
                    foreach ((Utf8String propPath, object propValue) in dict)
                    {
                        Result result = JsonModelRowGenerator.JsonModelSwitch(ref writer, propPath, propValue);
                        if (result != Result.Success)
                        {
                            return result;
                        }
                    }

                    return Result.Success;
                });
        }

        private static Result JsonModelSwitch(ref RowWriter writer, Utf8String path, object value)
        {
            switch (value)
            {
                case null:
                    return writer.WriteNull(path);
                case bool x:
                    return writer.WriteBool(path, x);
                case long x:
                    return writer.WriteInt64(path, x);
                case double x:
                    return writer.WriteFloat64(path, x);
                case string x:
                    return writer.WriteString(path, x);
                case Utf8String x:
                    return writer.WriteString(path, x.Span);
                case byte[] x:
                    return writer.WriteBinary(path, x);
                case ReadOnlyMemory<byte> x:
                    return writer.WriteBinary(path, x.Span);
                case Dictionary<Utf8String, object> x:
                    return writer.WriteScope(
                        path,
                        new TypeArgument(LayoutType.Object),
                        x,
                        (ref RowWriter writer2, TypeArgument typeArg, Dictionary<Utf8String, object> dict) =>
                        {
                            foreach ((Utf8String propPath, object propValue) in dict)
                            {
                                Result result = JsonModelRowGenerator.JsonModelSwitch(ref writer2, propPath, propValue);
                                if (result != Result.Success)
                                {
                                    return result;
                                }
                            }

                            return Result.Success;
                        });
                case List<object> x:
                    return writer.WriteScope(
                        path,
                        new TypeArgument(LayoutType.Array),
                        x,
                        (ref RowWriter writer2, TypeArgument typeArg, List<object> list) =>
                        {
                            foreach (object elm in list)
                            {
                                Result result = JsonModelRowGenerator.JsonModelSwitch(ref writer2, null, elm);
                                if (result != Result.Success)
                                {
                                    return result;
                                }
                            }

                            return Result.Success;
                        });
                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {value.GetType().Name}");
                    return Result.Failure;
            }
        }
    }
}
