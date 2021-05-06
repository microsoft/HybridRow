// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    internal static class RowReaderExtensions
    {
        public static Result VisitReader(this ref RowReader reader)
        {
            while (reader.Read())
            {
                Utf8Span path = reader.PathSpan;
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.Null:
                    case LayoutCode.Boolean:
                    case LayoutCode.Int8:
                    case LayoutCode.Int16:
                    case LayoutCode.Int32:
                    case LayoutCode.Int64:
                    case LayoutCode.UInt8:
                    case LayoutCode.UInt16:
                    case LayoutCode.UInt32:
                    case LayoutCode.UInt64:
                    case LayoutCode.VarInt:
                    case LayoutCode.VarUInt:
                    case LayoutCode.Float32:
                    case LayoutCode.Float64:
                    case LayoutCode.Float128:
                    case LayoutCode.Decimal:
                    case LayoutCode.DateTime:
                    case LayoutCode.UnixDateTime:
                    case LayoutCode.Guid:
                    case LayoutCode.MongoDbObjectId:
                    case LayoutCode.Utf8:
                    case LayoutCode.Binary:
                        break;

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
                        Result r = reader.ReadScope(null, (ref RowReader child, object _) => child.VisitReader());
                        if (r != Result.Success)
                        {
                            return r;
                        }

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
    }
}
