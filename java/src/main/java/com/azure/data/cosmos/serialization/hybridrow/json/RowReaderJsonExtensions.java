// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.json;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;

import java.util.UUID;

public final class RowReaderJsonExtensions {
    /**
     * Project a JSON document from a HybridRow {@link RowReader}.
     *
     * @param reader The reader to project to JSON.
     * @param str    If successful, the JSON document that corresponds to the <paramref name="reader"/>.
     * @return The result.
     */
    public static Result ToJson(Reference<RowReader> reader, Out<String> str) {
        return RowReaderJsonExtensions.ToJson(reader.get().clone(), new RowReaderJsonSettings("  "), str);
    }

    /**
     * Project a JSON document from a HybridRow {@link RowReader}.
     *
     * @param reader   The reader to project to JSON.
     * @param settings Settings that control how the JSON document is formatted.
     * @param str      If successful, the JSON document that corresponds to the <paramref name="reader"/>.
     * @return The result.
     */
    public static Result ToJson(Reference<RowReader> reader, RowReaderJsonSettings settings,
                                Out<String> str) {
        ReaderStringContext ctx = new ReaderStringContext(new StringBuilder(),
            new RowReaderJsonSettings(settings.IndentChars, settings.QuoteChar == '\'' ? '\'' : '"'), 1);

        ctx.Builder.append("{");
        Result result = RowReaderJsonExtensions.ToJson(reader, ctx.clone());
        if (result != Result.SUCCESS) {
            str.setAndGet(null);
            return result;
        }

        ctx.Builder.append(ctx.NewLine);
        ctx.Builder.append("}");
        str.setAndGet(ctx.Builder.toString());
        return Result.SUCCESS;
    }

    private static Result ToJson(Reference<RowReader> reader, ReaderStringContext ctx) {
        int index = 0;
        while (reader.get().read()) {
            String path = !reader.get().path().IsNull ? String.format("%1$s%2$s%3$s:", ctx.Settings.QuoteChar,
                reader.get().path(), ctx.Settings.QuoteChar) : null;
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
            switch (reader.get().type().LayoutCode) {
                case Null: {
                    NullValue _;
                    Out<NullValue> tempOut__ =
                        new Out<NullValue>();
                    r = reader.get().readNull(tempOut__);
                    _ = tempOut__.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append("null");
                    break;
                }

                case Boolean: {
                    boolean value;
                    Out<Boolean> tempOut_value = new Out<Boolean>();
                    r = reader.get().readBoolean(tempOut_value);
                    value = tempOut_value.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int8: {
                    byte value;
                    Out<Byte> tempOut_value2 = new Out<Byte>();
                    r = reader.get().readInt8(tempOut_value2);
                    value = tempOut_value2.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int16: {
                    short value;
                    Out<Short> tempOut_value3 = new Out<Short>();
                    r = reader.get().readInt16(tempOut_value3);
                    value = tempOut_value3.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int32: {
                    int value;
                    Out<Integer> tempOut_value4 = new Out<Integer>();
                    r = reader.get().readInt32(tempOut_value4);
                    value = tempOut_value4.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Int64: {
                    long value;
                    Out<Long> tempOut_value5 = new Out<Long>();
                    r = reader.get().readInt64(tempOut_value5);
                    value = tempOut_value5.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt8: {
                    byte value;
                    Out<Byte> tempOut_value6 = new Out<Byte>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt8(out byte value);
                    r = reader.get().readUInt8(tempOut_value6);
                    value = tempOut_value6.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt16: {
                    short value;
                    Out<Short> tempOut_value7 = new Out<Short>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt16(out ushort value);
                    r = reader.get().readUInt16(tempOut_value7);
                    value = tempOut_value7.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt32: {
                    int value;
                    Out<Integer> tempOut_value8 = new Out<Integer>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt32(out uint value);
                    r = reader.get().readUInt32(tempOut_value8);
                    value = tempOut_value8.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case UInt64: {
                    long value;
                    Out<Long> tempOut_value9 = new Out<Long>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt64(out ulong value);
                    r = reader.get().readUInt64(tempOut_value9);
                    value = tempOut_value9.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case VarInt: {
                    long value;
                    Out<Long> tempOut_value10 = new Out<Long>();
                    r = reader.get().readVarInt(tempOut_value10);
                    value = tempOut_value10.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case VarUInt: {
                    long value;
                    Out<Long> tempOut_value11 = new Out<Long>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadVarUInt(out ulong value);
                    r = reader.get().readVarUInt(tempOut_value11);
                    value = tempOut_value11.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float32: {
                    float value;
                    Out<Float> tempOut_value12 = new Out<Float>();
                    r = reader.get().readFloat32(tempOut_value12);
                    value = tempOut_value12.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float64: {
                    double value;
                    Out<Double> tempOut_value13 = new Out<Double>();
                    r = reader.get().readFloat64(tempOut_value13);
                    value = tempOut_value13.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case Float128: {
                    Float128 _;
                    Out<Float128> tempOut__2 =
                        new Out<Float128>();
                    r = reader.get().readFloat128(tempOut__2);
                    _ = tempOut__2.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    // ctx.Builder.AppendFormat("High: {0}, Low: {1}\n", value.High, value.Low);
                    checkState(false, "Float128 are not supported.");
                    break;
                }

                case Decimal: {
                    java.math.BigDecimal value;
                    Out<BigDecimal> tempOut_value14 = new Out<BigDecimal>();
                    r = reader.get().readDecimal(tempOut_value14);
                    value = tempOut_value14.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value);
                    break;
                }

                case DateTime: {
                    java.time.LocalDateTime value;
                    Out<LocalDateTime> tempOut_value15 = new Out<LocalDateTime>();
                    r = reader.get().readDateTime(tempOut_value15);
                    value = tempOut_value15.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value);
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case UnixDateTime: {
                    UnixDateTime value;
                    Out<UnixDateTime> tempOut_value16 =
                        new Out<UnixDateTime>();
                    r = reader.get().readUnixDateTime(tempOut_value16);
                    value = tempOut_value16.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(value.Milliseconds);
                    break;
                }

                case Guid: {
                    java.util.UUID value;
                    Out<UUID> tempOut_value17 = new Out<UUID>();
                    r = reader.get().readGuid(tempOut_value17);
                    value = tempOut_value17.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value.toString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case MongoDbObjectId: {
                    MongoDbObjectId value;
                    Out<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempOut_value18 = new Out<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>();
                    r = reader.get().ReadMongoDbObjectId(tempOut_value18);
                    value = tempOut_value18.get();
                    if (r != Result.SUCCESS) {
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
                    // - these cannot be converted using the 'Out' helper class unless the method is within the
                    // code being modified:
                    r = reader.get().ReadString(out value);
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value.toString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case Binary: {
                    ReadOnlySpan<Byte> value;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadBinary(out ReadOnlySpan<byte> value);
                    r = reader.get().ReadBinary(out value);
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    ctx.Builder.append(value.ToHexString());
                    ctx.Builder.append(ctx.Settings.QuoteChar);
                    break;
                }

                case NullableScope:
                case ImmutableNullableScope: {
                    if (!reader.get().hasValue()) {
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
                    r = reader.get().readScope(new ReaderStringContext(ctx.Builder, ctx.Settings.clone(), ctx.Indent + 1), RowReaderJsonExtensions.ToJson);
                    if (r != Result.SUCCESS) {
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
                    throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", reader.get().type().LayoutCode));
                    break;
                }
            }
        }

        return Result.SUCCESS;
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