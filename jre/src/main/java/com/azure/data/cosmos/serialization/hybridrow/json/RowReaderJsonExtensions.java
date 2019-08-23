//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.json;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;

import java.util.UUID;

public final class RowReaderJsonExtensions {
    /**
     * Project a JSON document from a HybridRow <see cref="RowReader"/>.
     *
     * @param reader The reader to project to JSON.
     * @param str    If successful, the JSON document that corresponds to the <paramref name="reader"/>.
     * @return The result.
     */
    public static Result ToJson(RefObject<RowReader> reader, OutObject<String> str) {
        return RowReaderJsonExtensions.ToJson(reader.get().clone(), new RowReaderJsonSettings("  "), str);
    }

    /**
     * Project a JSON document from a HybridRow <see cref="RowReader"/>.
     *
     * @param reader   The reader to project to JSON.
     * @param settings Settings that control how the JSON document is formatted.
     * @param str      If successful, the JSON document that corresponds to the <paramref name="reader"/>.
     * @return The result.
     */
    public static Result ToJson(RefObject<RowReader> reader, RowReaderJsonSettings settings,
                                OutObject<String> str) {
        ReaderStringContext ctx = new ReaderStringContext(new StringBuilder(),
            new RowReaderJsonSettings(settings.IndentChars, settings.QuoteChar == '\'' ? '\'' : '"'), 1);

        ctx.Builder.append("{");
        Result result = RowReaderJsonExtensions.ToJson(reader, ctx.clone());
        if (result != Result.Success) {
            str.set(null);
            return result;
        }

        ctx.Builder.append(ctx.NewLine);
        ctx.Builder.append("}");
        str.set(ctx.Builder.toString());
        return Result.Success;
    }

    private static Result ToJson(RefObject<RowReader> reader, ReaderStringContext ctx) {
        int index = 0;
        while (reader.get().Read()) {
            String path = !reader.get().getPath().IsNull ? String.format("%1$s%2$s%3$s:", ctx.Settings.QuoteChar,
                reader.get().getPath(), ctx.Settings.QuoteChar) : null;
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
            switch (reader.get().getType().LayoutCode) {
                case Null: {
                    NullValue _;
                    OutObject<NullValue> tempOut__ =
                        new OutObject<NullValue>();
                    r = reader.get().ReadNull(tempOut__);
                    _ = tempOut__.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append("null");
                    break;
                }

                case Boolean: {
                    boolean value;
                    OutObject<Boolean> tempOut_value = new OutObject<Boolean>();
                    r = reader.get().ReadBool(tempOut_value);
                    value = tempOut_value.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int8: {
                    byte value;
                    OutObject<Byte> tempOut_value2 = new OutObject<Byte>();
                    r = reader.get().ReadInt8(tempOut_value2);
                    value = tempOut_value2.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int16: {
                    short value;
                    OutObject<Short> tempOut_value3 = new OutObject<Short>();
                    r = reader.get().ReadInt16(tempOut_value3);
                    value = tempOut_value3.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int32: {
                    int value;
                    OutObject<Integer> tempOut_value4 = new OutObject<Integer>();
                    r = reader.get().ReadInt32(tempOut_value4);
                    value = tempOut_value4.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int64: {
                    long value;
                    OutObject<Long> tempOut_value5 = new OutObject<Long>();
                    r = reader.get().ReadInt64(tempOut_value5);
                    value = tempOut_value5.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt8: {
                    byte value;
                    OutObject<Byte> tempOut_value6 = new OutObject<Byte>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt8(out byte value);
                    r = reader.get().ReadUInt8(tempOut_value6);
                    value = tempOut_value6.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt16: {
                    short value;
                    OutObject<Short> tempOut_value7 = new OutObject<Short>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt16(out ushort value);
                    r = reader.get().ReadUInt16(tempOut_value7);
                    value = tempOut_value7.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt32: {
                    int value;
                    OutObject<Integer> tempOut_value8 = new OutObject<Integer>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt32(out uint value);
                    r = reader.get().ReadUInt32(tempOut_value8);
                    value = tempOut_value8.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt64: {
                    long value;
                    OutObject<Long> tempOut_value9 = new OutObject<Long>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt64(out ulong value);
                    r = reader.get().ReadUInt64(tempOut_value9);
                    value = tempOut_value9.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case VarInt: {
                    long value;
                    OutObject<Long> tempOut_value10 = new OutObject<Long>();
                    r = reader.get().ReadVarInt(tempOut_value10);
                    value = tempOut_value10.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case VarUInt: {
                    long value;
                    OutObject<Long> tempOut_value11 = new OutObject<Long>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadVarUInt(out ulong value);
                    r = reader.get().ReadVarUInt(tempOut_value11);
                    value = tempOut_value11.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float32: {
                    float value;
                    OutObject<Float> tempOut_value12 = new OutObject<Float>();
                    r = reader.get().ReadFloat32(tempOut_value12);
                    value = tempOut_value12.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float64: {
                    double value;
                    OutObject<Double> tempOut_value13 = new OutObject<Double>();
                    r = reader.get().ReadFloat64(tempOut_value13);
                    value = tempOut_value13.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float128: {
                    Float128 _;
                    OutObject<Float128> tempOut__2 =
                        new OutObject<Float128>();
                    r = reader.get().ReadFloat128(tempOut__2);
                    _ = tempOut__2.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    // ctx.Builder.AppendFormat("High: {0}, Low: {1}\n", value.High, value.Low);
                    checkState(false, "Float128 are not supported.");
                    break;
                }

                case Decimal: {
                    java.math.BigDecimal value;
                    OutObject<BigDecimal> tempOut_value14 = new OutObject<BigDecimal>();
                    r = reader.get().ReadDecimal(tempOut_value14);
                    value = tempOut_value14.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case DateTime: {
                    java.time.LocalDateTime value;
                    OutObject<LocalDateTime> tempOut_value15 = new OutObject<LocalDateTime>();
                    r = reader.get().ReadDateTime(tempOut_value15);
                    value = tempOut_value15.get();
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
                    OutObject<UnixDateTime> tempOut_value16 =
                        new OutObject<UnixDateTime>();
                    r = reader.get().ReadUnixDateTime(tempOut_value16);
                    value = tempOut_value16.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    ctx.Builder.append(value.Milliseconds);
                    break;
                }

                case Guid: {
                    java.util.UUID value;
                    OutObject<UUID> tempOut_value17 = new OutObject<UUID>();
                    r = reader.get().ReadGuid(tempOut_value17);
                    value = tempOut_value17.get();
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
                    OutObject<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempOut_value18 = new OutObject<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>();
                    r = reader.get().ReadMongoDbObjectId(tempOut_value18);
                    value = tempOut_value18.get();
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
                    r = reader.get().ReadString(out value);
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
                    r = reader.get().ReadBinary(out value);
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
                    if (!reader.get().getHasValue()) {
                        ctx.Builder.append("null");
                        break;
                    }

                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					//   goto case LayoutCode.TypedTupleScope;
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
					//   goto case LayoutCode.EndScope;
                case ObjectScope:
                case ImmutableObjectScope:
                case Schema:
                case ImmutableSchema:
                    scopeBracket = '{';
                    scopeCloseBracket = '}';

                case EndScope: {
                    ctx.Builder.append(scopeBracket);
                    int snapshot = ctx.Builder.length();
                    r = reader.get().ReadScope(new ReaderStringContext(ctx.Builder, ctx.Settings.clone(), ctx.Indent + 1), RowReaderJsonExtensions.ToJson);
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
                    throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", reader.get().getType().LayoutCode));
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