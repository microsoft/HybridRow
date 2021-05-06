// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.json;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.Strings.lenientFormat;

public final class RowReaderJsonExtensions {
    /**
     * Project a JSON document from a HybridRow {@link RowReader}.
     *
     * @param reader The reader to project to JSON.
     * @param string If {@link Result#SUCCESS}, the JSON document that corresponds to the {@code reader}.
     * @return The result.
     */
    @Nonnull
    public static Result toJson(@Nonnull final RowReader reader, @Nonnull final Out<String> string) {
        return RowReaderJsonExtensions.toJson(reader, new RowReaderJsonSettings("  "), string);
    }

    /**
     * Project a JSON document from a HybridRow {@link RowReader}.
     *
     * @param reader   The reader to project to JSON.
     * @param settings Settings that control how the JSON document is formatted.
     * @param string   If {@link Result#SUCCESS}, the JSON document that corresponds to the {@code reader}.
     * @return The result.
     */
    @Nonnull
    public static Result toJson(
        @Nonnull final RowReader reader,
        @Nonnull final RowReaderJsonSettings settings,
        @Nonnull final Out<String> string) {

        final ReaderStringContext context = new ReaderStringContext(
            new StringBuilder(),
            new RowReaderJsonSettings(
                settings.indentChars(),
                settings.quoteChar() == '\'' ? '\'' : '"'),
            1);

        context.builder().append("{");
        Result result = RowReaderJsonExtensions.toJson(reader, context);

        if (result != Result.SUCCESS) {
            string.set(null);
            return result;
        }

        context.builder().append(context.newline());
        context.builder().append("}");

        string.set(context.builder().toString());
        return Result.SUCCESS;
    }

    @Nonnull
    private static Result toJson(@Nonnull final RowReader reader, @Nonnull final ReaderStringContext context) {

        int index = 0;

        while (reader.read()) {
            String path = !reader.path().isNull()
                ? lenientFormat("%s%s%s:", context.settings().quoteChar(), reader.path(), context.settings().quoteChar())
                : null;
            if (index != 0) {
                context.builder().append(',');
            }

            index++;
            context.builder().append(context.newline());
            context.writeIndent();

            if (path != null) {
                context.builder().append(path);
                context.builder().append(context.separator());
            }

            final Out out = new Out<>();
            Result result;
            char scopeBracket = '\0';
            char scopeCloseBracket = '\0';

            switch (Objects.requireNonNull(reader.type()).layoutCode()) {

                case NULL: {
                    result = reader.readNull(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append("null");
                    break;
                }

                case BOOLEAN: {
                    result = reader.readBoolean(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case INT_8: {
                    result = reader.readInt8(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case INT_16: {
                    result = reader.readInt16(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case INT_32: {
                    result = reader.readInt32(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case INT_64: {
                    result = reader.readInt64(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case UINT_8: {
                    result = reader.readUInt8(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case UINT_16: {
                    result = reader.readUInt16(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case UINT_32: {
                    result = reader.readUInt32(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case UINT_64: {
                    result = reader.readUInt64(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case VAR_INT: {
                    result = reader.readVarInt(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case VAR_UINT: {
                    result = reader.readVarUInt(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case FLOAT_32: {
                    result = reader.readFloat32(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case FLOAT_64: {
                    result = reader.readFloat64(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case FLOAT_128: {
                    result = reader.readFloat128(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    // context.Builder.AppendFormat("High: {0}, Low: {1}\n", value.High, value.Low);
                    throw new UnsupportedOperationException("Float128 values are not supported.");
                }

                case DECIMAL: {
                    result = reader.readDecimal(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(out.get());
                    break;
                }

                case DATE_TIME: {
                    result = reader.readDateTime(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(context.settings().quoteChar());
                    context.builder().append(out.get());
                    context.builder().append(context.settings().quoteChar());
                    break;
                }

                case UNIX_DATE_TIME: {
                    result = reader.readUnixDateTime(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(((UnixDateTime)out.get()).milliseconds());
                    break;
                }

                case GUID: {
                    result = reader.readGuid(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(context.settings().quoteChar());
                    context.builder().append(out.get());
                    context.builder().append(context.settings().quoteChar());
                    break;
                }

                case MONGODB_OBJECT_ID: {
                    // TODO: DANOBLE: Resurrect this code block
                    //                    MongoDbObjectId value;
                    //                    Out<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempOut_value18 =
                    //                        new Out<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>();
                    //                    result = reader.ReadMongoDbObjectId(tempOut_value18);
                    //                    value = tempOut_value18.get();
                    //                    if (result != Result.SUCCESS) {
                    //                        return result;
                    //                    }
                    //
                    //                    context.builder().append(context.settings().quoteChar());
                    //                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //                    //ORIGINAL LINE: ReadOnlyMemory<byte> bytes = value.ToByteArray();
                    //                    ReadOnlyMemory<Byte> bytes = value.ToByteArray();
                    //                    context.builder().append(bytes.Span.ToHexString());
                    //                    context.builder().append(context.settings().quoteChar());
                    break;
                }

                case UTF_8: {
                    result = reader.readString(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(context.settings().quoteChar());
                    context.builder().append(Json.toString(out));
                    context.builder().append(context.settings().quoteChar());
                    break;
                }

                case BINARY: {
                    result = reader.readBinary(out);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    context.builder().append(context.settings().quoteChar());
                    context.builder().append(out.get());
                    context.builder().append(context.settings().quoteChar());
                    break;
                }

                case NULLABLE_SCOPE:
                case IMMUTABLE_NULLABLE_SCOPE:
                    if (!reader.hasValue()) {
                        context.builder().append("null");
                        break;
                    }

                case ARRAY_SCOPE:
                case IMMUTABLE_ARRAY_SCOPE:
                case TYPED_ARRAY_SCOPE:
                case IMMUTABLE_TYPED_ARRAY_SCOPE:
                case TYPED_SET_SCOPE:
                case IMMUTABLE_TYPED_SET_SCOPE:
                case TYPED_MAP_SCOPE:
                case IMMUTABLE_TYPED_MAP_SCOPE:
                case TUPLE_SCOPE:
                case IMMUTABLE_TUPLE_SCOPE:
                case TYPED_TUPLE_SCOPE:
                case IMMUTABLE_TYPED_TUPLE_SCOPE:
                case TAGGED_SCOPE:
                case IMMUTABLE_TAGGED_SCOPE:
                case TAGGED2_SCOPE:
                case IMMUTABLE_TAGGED2_SCOPE:
                    result = endScope(reader, context, scopeBracket = '[', scopeCloseBracket = ']');
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;
                case OBJECT_SCOPE:
                case IMMUTABLE_OBJECT_SCOPE:
                case SCHEMA:
                case IMMUTABLE_SCHEMA:
                    result = endScope(reader, context, scopeBracket = '{', scopeCloseBracket = '}');
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;

                case END_SCOPE: {
                    result = endScope(reader, context, scopeBracket, scopeCloseBracket);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;
                }

                default: {
                    throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s",
                        Objects.requireNonNull(reader.type()).layoutCode())
                    );
                }
            }
        }

        return Result.SUCCESS;
    }

    @NonNull
    private static Result endScope(
        @Nonnull RowReader reader, @Nonnull ReaderStringContext context, char scopeBracket, char scopeCloseBracket) {

        Result result;
        context.builder().append(scopeBracket);
        int snapshot = context.builder().length();

        result = reader.readScope(
            new ReaderStringContext(
                context.builder(),
                context.settings(),
                context.indent() + 1),
            RowReaderJsonExtensions::toJson);

        if (result != Result.SUCCESS) {
            return result;
        }

        if (context.builder().length() != snapshot) {
            context.builder().append(context.newline());
            context.writeIndent();
        }

        context.builder().append(scopeCloseBracket);
        return result;
    }

    private final static class ReaderStringContext {

        private StringBuilder builder;
        private int indent;
        private String newline;
        private String separator;
        private RowReaderJsonSettings settings = new RowReaderJsonSettings();

        public ReaderStringContext(StringBuilder builder, RowReaderJsonSettings settings, int indent) {
            this.settings = settings;
            this.separator = settings.indentChars() == null ? "" : " ";
            this.newline = settings.indentChars() == null ? "" : "\n";
            this.indent = indent;
            this.builder = builder;
        }

        public StringBuilder builder() {
            return this.builder;
        }

        public int indent() {
            return this.indent;
        }

        public String newline() {
            return this.newline;
        }

        public String separator() {
            return this.separator;
        }

        public RowReaderJsonSettings settings() {
            return this.settings;
        }

        public void writeIndent() {
            String indentChars = this.settings().indentChars() != null ? this.settings().indentChars() : "";
            for (int i = 0; i < this.indent(); i++) {
                this.builder().append(indentChars);
            }
        }
    }
}