// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.perf;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TAGGED2_SCOPE;

public final class RowReaderExtensions {
    public static Result VisitReader(Reference<RowReader> reader) {
        while (reader.get().Read()) {
            Utf8Span path = reader.get().getPathSpan();
            switch (reader.get().getType().LayoutCode) {
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
                    if (!reader.get().getHasValue()) {
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
                case IMMUTABLE_TAGGED2_SCOPE: {
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                    Result r = reader.get().ReadScope(null, (ref RowReader child, Object _) -> child.VisitReader());
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
                }

                default: {
                    throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", reader.get().getType().LayoutCode));
                    break;
                }
            }
        }

        return Result.SUCCESS;
    }
}