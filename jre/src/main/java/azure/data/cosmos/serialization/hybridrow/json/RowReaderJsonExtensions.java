//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.json;

import azure.data.cosmos.serialization.hybridrow.Float128;
import azure.data.cosmos.serialization.hybridrow.NullValue;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import azure.data.cosmos.serialization.hybridrow.io.RowReader;

public final class RowReaderJsonExtensions {
    /**
     * Project a JSON document from a HybridRow <see cref="RowReader"/>.
     *
     * @param reader The reader to project to JSON.
     * @param str    If successful, the JSON document that corresponds to the <paramref name="reader"/>.
     * @return The result.
     */
    public static Result ToJson(tangible.RefObject<RowReader> reader, tangible.OutObject<String> str) {
        return azure.data.cosmos.serialization.hybridrow.json.RowReaderJsonExtensions.ToJson(reader.argValue.clone(), new RowReaderJsonSettings("  "), str);
    }

    /**
     * Project a JSON document from a HybridRow <see cref="RowReader"/>.
     *
     * @param reader   The reader to project to JSON.
     * @param settings Settings that control how the JSON document is formatted.
     * @param str      If successful, the JSON document that corresponds to the <paramref name="reader"/>.
     * @return The result.
     */
    public static Result ToJson(tangible.RefObject<RowReader> reader, RowReaderJsonSettings settings,
                                tangible.OutObject<String> str) {
        ReaderStringContext ctx = new ReaderStringContext(new StringBuilder(),
            new RowReaderJsonSettings(settings.IndentChars, settings.QuoteChar == '\'' ? '\'' : '"'), 1);

        ctx.Builder.append("{");
        Result result = RowReaderJsonExtensions.ToJson(reader, ctx.clone());
        if (result != Result.Success) {
            str.argValue = null;
            return result;
        }

        ctx.Builder.append(ctx.NewLine);
        ctx.Builder.append("}");
        str.argValue = ctx.Builder.toString();
        return Result.Success;
    }

    private static Result ToJson(tangible.RefObject<RowReader> reader, ReaderStringContext ctx) {
        int index = 0;
        while (reader.argValue.Read()) {
            String path = !reader.argValue.getPath().IsNull ? String.format("%1$s%2$s%3$s:", ctx.Settings.QuoteChar,
                reader.argValue.getPath(), ctx.Settings.QuoteChar) : null;
            if (index != 0) {
                ctx.Builder.append(',');
            }

            index++;
            ctx.Builder.append(ctx.NewLine);
            ctx.WriteIndent();
            if (path != null) {
                ctx.Builder.append(path);
                ctx.Builder.append(ctx.Separator);
            }

            Result r;
            char scopeBracket = '\0';
            char scopeCloseBracket = '\0';
            switch (reader.argValue.getType().LayoutCode) {
                case Null: {
                    NullValue _;
                    tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.NullValue> tempOut__ =
                        new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.NullValue>();
                    r = reader.argValue.ReadNull(tempOut__);
                    _ = tempOut__.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append("null");
                    break;
                }

                case Boolean: {
                    boolean value;
                    tangible.OutObject<Boolean> tempOut_value = new tangible.OutObject<Boolean>();
                    r = reader.argValue.ReadBool(tempOut_value);
                    value = tempOut_value.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int8: {
                    byte value;
                    tangible.OutObject<Byte> tempOut_value2 = new tangible.OutObject<Byte>();
                    r = reader.argValue.ReadInt8(tempOut_value2);
                    value = tempOut_value2.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int16: {
                    short value;
                    tangible.OutObject<Short> tempOut_value3 = new tangible.OutObject<Short>();
                    r = reader.argValue.ReadInt16(tempOut_value3);
                    value = tempOut_value3.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int32: {
                    int value;
                    tangible.OutObject<Integer> tempOut_value4 = new tangible.OutObject<Integer>();
                    r = reader.argValue.ReadInt32(tempOut_value4);
                    value = tempOut_value4.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int64: {
                    long value;
                    tangible.OutObject<Long> tempOut_value5 = new tangible.OutObject<Long>();
                    r = reader.argValue.ReadInt64(tempOut_value5);
                    value = tempOut_value5.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt8: {
                    byte value;
                    tangible.OutObject<Byte> tempOut_value6 = new tangible.OutObject<Byte>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt8(out byte value);
                    r = reader.argValue.ReadUInt8(tempOut_value6);
                    value = tempOut_value6.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt16: {
                    short value;
                    tangible.OutObject<Short> tempOut_value7 = new tangible.OutObject<Short>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt16(out ushort value);
                    r = reader.argValue.ReadUInt16(tempOut_value7);
                    value = tempOut_value7.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt32: {
                    int value;
                    tangible.OutObject<Integer> tempOut_value8 = new tangible.OutObject<Integer>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt32(out uint value);
                    r = reader.argValue.ReadUInt32(tempOut_value8);
                    value = tempOut_value8.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt64: {
                    long value;
                    tangible.OutObject<Long> tempOut_value9 = new tangible.OutObject<Long>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt64(out ulong value);
                    r = reader.argValue.ReadUInt64(tempOut_value9);
                    value = tempOut_value9.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case VarInt: {
                    long value;
                    tangible.OutObject<Long> tempOut_value10 = new tangible.OutObject<Long>();
                    r = reader.argValue.ReadVarInt(tempOut_value10);
                    value = tempOut_value10.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case VarUInt: {
                    long value;
                    tangible.OutObject<Long> tempOut_value11 = new tangible.OutObject<Long>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadVarUInt(out ulong value);
                    r = reader.argValue.ReadVarUInt(tempOut_value11);
                    value = tempOut_value11.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float32: {
                    float value;
                    tangible.OutObject<Float> tempOut_value12 = new tangible.OutObject<Float>();
                    r = reader.argValue.ReadFloat32(tempOut_value12);
                    value = tempOut_value12.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float64: {
                    double value;
                    tangible.OutObject<Double> tempOut_value13 = new tangible.OutObject<Double>();
                    r = reader.argValue.ReadFloat64(tempOut_value13);
                    value = tempOut_value13.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float128: {
                    Float128 _;
                    tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Float128> tempOut__2 =
                        new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Float128>();
                    r = reader.argValue.ReadFloat128(tempOut__2);
                    _ = tempOut__2.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    // ctx.Builder.AppendFormat("High: {0}, Low: {1}\n", value.High, value.Low);
                    Contract.Assert(false, "Float128 are not supported.");
                    break;
                }

                case Decimal: {
                    java.math.BigDecimal value;
                    tangible.OutObject<BigDecimal> tempOut_value14 = new tangible.OutObject<BigDecimal>();
                    r = reader.argValue.ReadDecimal(tempOut_value14);
                    value = tempOut_value14.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case DateTime: {
                    java.time.LocalDateTime value;
                    tangible.OutObject<LocalDateTime> tempOut_value15 = new tangible.OutObject<LocalDateTime>();
                    r = reader.argValue.ReadDateTime(tempOut_value15);
                    value = tempOut_value15.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value);
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case UnixDateTime: {
                    UnixDateTime value;
                    tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.UnixDateTime> tempOut_value16 =
                        new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.UnixDateTime>();
                    r = reader.argValue.ReadUnixDateTime(tempOut_value16);
                    value = tempOut_value16.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value.Milliseconds);
                    break;
                }

                case Guid: {
                    java.util.UUID value;
                    tangible.OutObject<UUID> tempOut_value17 = new tangible.OutObject<UUID>();
                    r = reader.argValue.ReadGuid(tempOut_value17);
                    value = tempOut_value17.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value.toString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case MongoDbObjectId: {
                    MongoDbObjectId value;
                    tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.MongoDbObjectId> tempOut_value18 = new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.MongoDbObjectId>();
                    r = reader.argValue.ReadMongoDbObjectId(tempOut_value18);
                    value = tempOut_value18.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: ReadOnlyMemory<byte> bytes = value.ToByteArray();
                    ReadOnlyMemory<Byte> bytes = value.ToByteArray();
                    ctx.Builder.append(bytes.Span.ToHexString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case Utf8: {
                    Utf8Span value;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'OutObject' helper class unless the method is within the
                    // code being modified:
                    r = reader.argValue.ReadString(out value);
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value.toString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case Binary: {
                    ReadOnlySpan<Byte> value;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadBinary(out ReadOnlySpan<byte> value);
                    r = reader.argValue.ReadBinary(out value);
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value.ToHexString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case NullableScope:
                case ImmutableNullableScope: {
                    if (!reader.argValue.getHasValue()) {
                        ctx.Builder.append("null");
                        break;
                    }

                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case LayoutCode.TypedTupleScope
                }

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
                case ImmutableTagged2Scope:
                    scopeBracket = '[';
                    scopeCloseBracket = ']';
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case LayoutCode.EndScope
                case ObjectScope:
                case ImmutableObjectScope:
                case Schema:
                case ImmutableSchema:
                    scopeBracket = '{';
                    scopeCloseBracket = '}';

                case EndScope: {
                    ctx.Builder.append(scopeBracket);
                    int snapshot = ctx.Builder.length();
                    r = reader.argValue.ReadScope(new ReaderStringContext(ctx.Builder, ctx.Settings.clone(), ctx.Indent + 1), RowReaderJsonExtensions.ToJson);
                    if (r != Result.Success) {
                        return r;
                    }

                    if (ctx.Builder.length() != snapshot) {
                        ctx.Builder.append(ctx.NewLine);
                        ctx.WriteIndent();
                    }

                    ctx.Builder.append(scopeCloseBracket);
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

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ from the original:
    //ORIGINAL LINE: private readonly struct ReaderStringContext
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
    private final static class ReaderStringContext {
        public StringBuilder Builder;
        public int Indent;
        public String NewLine;
        public String Separator;
        public RowReaderJsonSettings Settings = new RowReaderJsonSettings();

        public ReaderStringContext() {
        }

        public ReaderStringContext(StringBuilder builder, RowReaderJsonSettings settings, int indent) {
            this.Settings = settings.clone();
            this.Separator = settings.IndentChars == null ? "" : " ";
            this.NewLine = settings.IndentChars == null ? "" : "\n";
            this.Indent = indent;
            this.Builder = builder;
        }

        public void WriteIndent() {
            String indentChars = this.Settings.IndentChars != null ? this.Settings.IndentChars : "";
            for (int i = 0; i < this.Indent; i++) {
                this.Builder.append(indentChars);
            }
        }

        public ReaderStringContext clone() {
            ReaderStringContext varCopy = new ReaderStringContext();

            varCopy.Indent = this.Indent;
            varCopy.Builder = this.Builder;
            varCopy.Settings = this.Settings.clone();
            varCopy.Separator = this.Separator;
            varCopy.NewLine = this.NewLine;

            return varCopy;
        }
    }
}