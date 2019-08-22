//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf;

import azure.data.cosmos.serialization.hybridrow.Float128;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.UnixDateTime;

public final class RowReaderExtensions {
    public static Result VisitReader(tangible.RefObject<RowReader> reader) {
        while (reader.argValue.Read()) {
            Utf8Span path = reader.argValue.getPathSpan();
            switch (reader.argValue.getType().LayoutCode) {
                case Null:
                case Boolean:
                case Int8:
                case Int16:
                case Int32:
                case Int64:
                case UInt8:
                case UInt16:
                case UInt32:
                case UInt64:
                case VarInt:
                case VarUInt:
                case Float32:
                case Float64:
                case Float128:
                case Decimal:
                case DateTime:
                case UnixDateTime:
                case Guid:
                case MongoDbObjectId:
                case Utf8:
                case Binary:
                    break;

                case NullableScope:
                case ImmutableNullableScope: {
                    if (!reader.argValue.getHasValue()) {
                        break;
                    }

                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case LayoutCode.TypedTupleScope
                }

                case ObjectScope:
                case ImmutableObjectScope:
                case Schema:
                case ImmutableSchema:
                case ArrayScope:
                case ImmutableArrayScope:
                case TypedArrayScope:
                case ImmutableTypedArrayScope:
                case TypedSetScope:
                case ImmutableTypedSetScope:
                case TypedMapScope:
                case ImmutableTypedMapScope:
                case TupleScope:
                case ImmutableTupleScope:
                case TypedTupleScope:
                case ImmutableTypedTupleScope:
                case TaggedScope:
                case ImmutableTaggedScope:
                case Tagged2Scope:
                case ImmutableTagged2Scope: {
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                    Result r = reader.argValue.ReadScope(null, (ref RowReader child, Object _) -> child.VisitReader());
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                }

                default: {
                    Contract.Assert(false, String.format("Unknown type will be ignored: %1$s", reader.argValue.getType().LayoutCode));
                    break;
                }
            }
        }

        return Result.Success;
    }
}