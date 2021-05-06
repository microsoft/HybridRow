// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutArray;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutListWritable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutNullable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutObject;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTagged;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTagged2;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypePrimitive;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypeScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedArray;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedMap;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedSet;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypes;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUDT;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUniqueScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUtf8Writable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;

public final class RowWriter {

    private RowCursor cursor;
    private RowBuffer row;

    /**
     * Initializes a new instance of the {@link RowWriter} class.
     *
     * @param row   The row to be read.
     * @param scope The scope into which items should be written.
     *              <p>
     *              A {@link RowWriter} instance writes the fields of a given scope from left to right in a forward only
     *              manner. If the root scope is provided then all top-level fields in the row can be
     */
    private RowWriter(RowBuffer row, RowCursor scope) {
        this.row = row;
        this.cursor = scope;
    }

    /**
     * The active layout of the current writer scope.
     *
     * @return layout of the current writer scope.
     */
    public Layout layout() {
        return this.cursor.layout();
    }

    /**
     * The length of row in bytes.
     *
     * @return length of the row in bytes.
     */
    public int length() {
        return this.row.length();
    }

    /**
     * The resolver for UDTs.
     *
     * @return the resolver of UDTs.
     */
    public LayoutResolver resolver() {
        return this.row.resolver();
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeBinary(UtfAnyString path, byte[] value) {
        return this.writeBinary(path, Unpooled.wrappedBuffer(value));
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeBinary(UtfAnyString path, ByteBuf value) {
        return this.writePrimitive(path, value, LayoutTypes.BINARY,
            field -> this.row.writeSparseBinary(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a {@link Boolean}.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeBoolean(UtfAnyString path, boolean value) {
        return this.writePrimitive(path, value, LayoutTypes.BOOLEAN,
            field -> this.row.writeSparseBoolean(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write an entire buffer in a streaming left-to-right way.
     *
     * @param <TContext> The type of the context value to pass to {@code func}.
     * @param buffer        The buffer to write.
     * @param context    A context value to pass to {@code func}.
     * @param func       A function to write the entire buffer.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public static <TContext> Result writeBuffer(
        @Nonnull final RowBuffer buffer, final @Nonnull TContext context, @Nonnull final WriterFunc<TContext> func) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(context, "expected non-null context");
        checkNotNull(func, "expected non-null func");

        RowCursor scope = RowCursor.create(buffer);
        RowWriter writer = new RowWriter(buffer, scope);
        TypeArgument typeArg = new TypeArgument(LayoutTypes.UDT, new TypeArgumentList(scope.layout().schemaId()));

        return func.invoke(writer, typeArg, context);
    }

    /**
     * Write a field as a fixed length {@code DateTime} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeDateTime(UtfAnyString path, OffsetDateTime value) {
        return this.writePrimitive(path, value, LayoutTypes.DATE_TIME,
            field -> this.row.writeSparseDateTime(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length {@code Decimal} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeDecimal(UtfAnyString path, BigDecimal value) {
        return this.writePrimitive(path, value, LayoutTypes.DECIMAL,
            field -> this.row.writeSparseDecimal(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 128-bit, IEEE-encoded floating point value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeFloat128(UtfAnyString path, Float128 value) {
        return this.writePrimitive(path, value, LayoutTypes.FLOAT_128,
            field -> this.row.writeSparseFloat128(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 32-bit, IEEE-encoded floating point value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeFloat32(UtfAnyString path, float value) {
        return this.writePrimitive(path, value, LayoutTypes.FLOAT_32,
            field -> this.row.writeSparseFloat32(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 64-bit, IEEE-encoded floating point value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeFloat64(UtfAnyString path, double value) {
        return this.writePrimitive(path, value, LayoutTypes.FLOAT_64,
            field -> this.row.writeSparseFloat64(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length {@code Guid} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeGuid(UtfAnyString path, UUID value) {
        return this.writePrimitive(path, value, LayoutTypes.GUID,
            field -> this.row.writeSparseGuid(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 16-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeInt16(UtfAnyString path, short value) {
        return this.writePrimitive(path, value, LayoutTypes.INT_16,
            field -> this.row.writeSparseInt16(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 32-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeInt32(UtfAnyString path, int value) {
        return this.writePrimitive(path, value, LayoutTypes.INT_32,
            field -> this.row.writeSparseInt32(this.cursor, field, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 64-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeInt64(UtfAnyString path, long value) {
        return this.writePrimitive(path, value, LayoutTypes.INT_64,
            field -> this.row.writeSparseInt64(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 8-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeInt8(UtfAnyString path, byte value) {
        return this.writePrimitive(path, value, LayoutTypes.INT_8,
            field -> this.row.writeSparseInt8(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    // TODO: DANOBLE: Resurrect this method
    //    /**
    //     * Write a field as a fixed length {@link MongoDbObjectId} value.
    //     *
    //     * @param path  The scope-relative path of the field to write.
    //     * @param value The value to write.
    //     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
    //     */
    //    public Result WriteMongoDbObjectId(UtfAnyString path, MongoDbObjectId value) {
    //        throw new UnsupportedOperationException();
    //        // return this.writePrimitive(path, value, LayoutTypes.MongoDbObjectId, (ref RowWriter w, MongoDbObjectId v) -> w.row.writeSparseMongoDbObjectId(ref w.cursor, v, UpdateOptions.UPSERT));
    //    }

    /**
     * Write a field as a {@code null}.
     *
     * @param path The scope-relative path of the field to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeNull(UtfAnyString path) {
        return this.writePrimitive(path, NullValue.DEFAULT, LayoutTypes.NULL,
            field -> this.row.writeSparseNull(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    public <TContext> Result writeScope(
        @Nonnull final UtfAnyString path,
        @Nonnull final TypeArgument typeArg,
        @Nullable final TContext context,
        @Nullable final WriterFunc<TContext> func) {

        checkNotNull(path, "expected non-null path");
        checkNotNull(typeArg, "expected non-null typeArg");

        Result result = this.prepareSparseWrite(path, typeArg);

        if (result != Result.SUCCESS) {
            return result;
        }

        final UpdateOptions options = UpdateOptions.UPSERT;
        final LayoutType type = typeArg.type();
        final RowCursor nestedScope;

        if (type instanceof LayoutObject) {

            nestedScope = this.row.writeSparseObject(this.cursor, (LayoutObject) type, options);

        } else if (type instanceof LayoutArray) {

            nestedScope = this.row.writeSparseArray(this.cursor, (LayoutArray) type, options);

        } else if (type instanceof LayoutTypedArray) {

            nestedScope = this.row.writeTypedArray(this.cursor, (LayoutTypedArray) type, typeArg.typeArgs(), options);

        } else if (type instanceof LayoutTuple) {

            nestedScope = this.row.writeSparseTuple(this.cursor, (LayoutTuple) type, typeArg.typeArgs(), options);

        } else if (type instanceof LayoutTypedTuple) {

            nestedScope = this.row.writeTypedTuple(this.cursor, (LayoutTypedTuple) type, typeArg.typeArgs(), options);

        } else if (type instanceof LayoutTagged) {

            nestedScope = this.row.writeTypedTuple(this.cursor, (LayoutTagged) type, typeArg.typeArgs(), options);

        } else if (type instanceof LayoutTagged2) {

            nestedScope = this.row.writeTypedTuple(this.cursor, (LayoutTagged2) type, typeArg.typeArgs(), options);

        } else if (type instanceof LayoutNullable) {

            nestedScope = this.row.writeNullable(this.cursor, (LayoutNullable) type, typeArg.typeArgs(), options,
                func != null);

        } else if (type instanceof LayoutUDT) {

            LayoutUDT scopeType = (LayoutUDT) type;
            Layout udt = this.row.resolver().resolve(typeArg.typeArgs().schemaId());
            nestedScope = this.row.writeSparseUDT(this.cursor, scopeType, udt, options);

        } else if (type instanceof LayoutTypedSet) {

            LayoutTypedSet scopeType = (LayoutTypedSet) type;
            nestedScope = this.row.writeTypedSet(this.cursor, scopeType, typeArg.typeArgs(), options);

        } else if (type instanceof LayoutTypedMap) {

            LayoutTypedMap scopeType = (LayoutTypedMap) type;
            nestedScope = this.row.writeTypedMap(this.cursor, scopeType, typeArg.typeArgs(), options);

        } else {

            throw new IllegalStateException(lenientFormat("expected type argument of %s, not %s",
                LayoutTypeScope.class,
                type.getClass()));
        }

        RowWriter nestedWriter = new RowWriter(this.row, nestedScope);
        result = func == null ? null : func.invoke(nestedWriter, typeArg, context);

        if (result == null) {
            result = Result.SUCCESS;
        }

        this.row = nestedWriter.row;
        nestedScope.count(nestedWriter.cursor.count());

        if (result != Result.SUCCESS) {
            // TODO: what about unique violations here?
            return result;
        }

        if (type instanceof LayoutUniqueScope) {
            result = this.row.typedCollectionUniqueIndexRebuild(nestedScope);
            if (result != Result.SUCCESS) {
                // TODO: If the index rebuild fails then the row is corrupted.  Should we automatically clean up here?
                return result;
            }
        }

        RowCursors.moveNext(this.cursor, this.row, nestedWriter.cursor);
        return Result.SUCCESS;
    }

    /**
     * Write a field as a variable length, UTF8 encoded, string value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeString(UtfAnyString path, String value) {

        // TODO: DANOBLE: RowBuffer should support writing String values directly (without conversion to Utf8String)

        Utf8String string = Utf8String.transcodeUtf16(value);
        assert string != null;

        try {
            return this.writePrimitive(path, value, LayoutTypes.UTF_8,
                field -> this.row.writeSparseString(this.cursor, string, UpdateOptions.UPSERT)
            );
        } finally {
            string.release();
        }
    }

    /**
     * Write a field as a variable length, UTF8 encoded, string value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeString(UtfAnyString path, Utf8String value) {
        // TODO: DANOBLE: BUG FIX: this.writePrimitive should write Utf8String as well as String
        //   note incorrect use of string "value" as the value argument
        return this.writePrimitive(path, "value", LayoutTypes.UTF_8,
            field -> this.row.writeSparseString(this.cursor, value, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 16-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeUInt16(UtfAnyString path, short value) {
        return this.writePrimitive(path, (int) value, LayoutTypes.UINT_16,
            field -> this.row.writeSparseUInt16(this.cursor, field.shortValue(), UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 32-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeUInt32(UtfAnyString path, long value) {
        return this.writePrimitive(path, value, LayoutTypes.UINT_32, field ->
            this.row.writeSparseUInt32(this.cursor, field.intValue(), UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 64-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeUInt64(UtfAnyString path, long value) {
        return this.writePrimitive(path, value, LayoutTypes.UINT_64, field ->
            this.row.writeSparseUInt64(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length, 8-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeUInt8(UtfAnyString path, byte value) {
        return this.writePrimitive(path, (short) value, LayoutTypes.UINT_8,
            field -> this.row.writeSparseUInt8(this.cursor, field.byteValue(), UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a fixed length {@link UnixDateTime} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeUnixDateTime(UtfAnyString path, UnixDateTime value) {
        return this.writePrimitive(path, value, LayoutTypes.UNIX_DATE_TIME,
            field -> this.row.writeSparseUnixDateTime(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a variable length, 7-bit encoded, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeVarInt(UtfAnyString path, long value) {
        return this.writePrimitive(path, value, LayoutTypes.VAR_INT,
            field -> this.row.writeSparseVarInt(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Write a field as a variable length, 7-bit encoded, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    public Result writeVarUInt(UtfAnyString path, long value) {
        return this.writePrimitive(path, value, LayoutTypes.VAR_UINT,
            field -> this.row.writeSparseVarUInt(this.cursor, field, UpdateOptions.UPSERT)
        );
    }

    /**
     * Helper for preparing the write of a sparse field.
     *
     * @param path    The path identifying the field to write.
     * @param typeArg The (optional) type constraints.
     * @return Success if the write is permitted, the error code otherwise.
     */
    private Result prepareSparseWrite(UtfAnyString path, TypeArgument typeArg) {

        if (this.cursor.scopeType().isFixedArity() && !(this.cursor.scopeType() instanceof LayoutNullable)) {
            if ((this.cursor.index() < this.cursor.scopeTypeArgs().count()) && !typeArg.equals(this.cursor.scopeTypeArgs().get(this.cursor.index()))) {
                return Result.TYPE_CONSTRAINT;
            }
        } else if (this.cursor.scopeType() instanceof LayoutTypedMap) {
            if (!typeArg.equals(this.cursor.scopeType().<LayoutUniqueScope>typeAs().fieldType(this.cursor))) {
                return Result.TYPE_CONSTRAINT;
            }
        } else if (this.cursor.scopeType().isTypedScope() && !typeArg.equals(this.cursor.scopeTypeArgs().get(0))) {
            return Result.TYPE_CONSTRAINT;
        }

        this.cursor.writePath(path);
        return Result.SUCCESS;
    }

    // TODO: DANOBLE: Does Java implementation need this method?
    /**
     * Helper for writing a primitive value.
     *
     * @param <TLayoutType> The type of layout type.
     * @param path          The scope-relative path of the field to write.
     * @param value         The value to write.
     * @param type          The layout type.
     * @param sparse        The {@link RowBuffer} access method for {@code type}.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    private <TLayoutType extends LayoutType & LayoutUtf8Writable>
    Result writePrimitive(UtfAnyString path, Utf8String value, TLayoutType type, Consumer<Utf8String> sparse) {

        Result result = Result.NOT_FOUND;

        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {

            result = this.prepareSparseWrite(path, type.typeArg());

            if (result != Result.SUCCESS) {
                return result;
            }

            sparse.accept(value);
            RowCursors.moveNext(this.cursor, this.row);
        }

        return result;
    }

    // TODO: DANOBLE: Does Java implementation need this method?
    /**
     * Helper for writing a primitive value.
     *
     * @param <TLayoutType> The type of layout type.
     * @param <TValue>      The sub-element type of the field.
     * @param path          The scope-relative path of the field to write.
     * @param value         The value to write.
     * @param type          The layout type.
     * @param sparse        The {@link RowBuffer} access method for {@code type}.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    private <TLayoutType extends LayoutType & LayoutListWritable<TValue>, TValue>
    Result writePrimitiveList(UtfAnyString path, List<TValue> value, TLayoutType type, Consumer<List<TValue>> sparse) {

        Result result = Result.NOT_FOUND;

        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {

            result = this.prepareSparseWrite(path, type.typeArg());

            if (result != Result.SUCCESS) {
                return result;
            }

            sparse.accept(value);
            RowCursors.moveNext(this.cursor, this.row);
        }

        return result;
    }

    /**
     * Helper for writing a primitive value.
     *
     * @param <TValue> The type of the primitive value.
     * @param path     The scope-relative path of the field to write.
     * @param value    The value to write.
     * @param type     The layout type.
     * @param sparse   The {@link RowBuffer} access method for {@code type}.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    private <TValue> Result writePrimitive(
        UtfAnyString path, TValue value, LayoutTypePrimitive<TValue> type, Consumer<TValue> sparse) {

        Result result = Result.NOT_FOUND;

        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {

            result = this.prepareSparseWrite(path, type.typeArg());

            if (result != Result.SUCCESS) {
                return result;
            }

            sparse.accept(value);
            RowCursors.moveNext(this.cursor, this.row);
        }

        return result;
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     *
     * @param <TValue> The expected type of the field.
     * @param path     The scope-relative path of the field to write.
     * @param value    The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    private <TValue> Result writeSchematizedValue(UtfAnyString path, TValue value) {

        final Optional<LayoutColumn> column = this.cursor.layout().tryFind(path);

        if (!column.isPresent()) {
            return Result.NOT_FOUND;
        }

        // TODO: DANOBLE: Add a mechanism for performing the equivalent of this type check
        //   if (!(column.Type is LayoutTypePrimitive<TValue> t)) {
        //       return Result.NotFound;
        //   }
        //   Type erasure prevents this test:
        //     column.type instanceof LayoutTypePrimitive<TValue>
        //   Reason: Runtime does not instantiate or otherwise represent or identify instances of a generic type.

        if (!(column.get().type() instanceof LayoutTypePrimitive)) {
            return Result.NOT_FOUND;
        }

        @SuppressWarnings("unchecked")
        LayoutTypePrimitive<TValue> type = (LayoutTypePrimitive<TValue>)column.get().type();

        switch (column.get().storage()) {
            case FIXED:
                return type.writeFixed(this.row, this.cursor, column.get(), value);
            case VARIABLE:
                return type.writeVariable(this.row, this.cursor, column.get(), value);
        }

        return Result.NOT_FOUND;
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    private Result writeSchematizedValue(UtfAnyString path, Utf8String value) {

        final Optional<LayoutColumn> column = this.cursor.layout().tryFind(path);

        if (!column.isPresent()) {
            return Result.NOT_FOUND;
        }

        final LayoutType type = column.get().type();

        if (!(type instanceof LayoutUtf8Writable)) {
            return Result.NOT_FOUND;
        }

        switch (column.get().storage()) {
            case FIXED:
                return type.<LayoutUtf8Writable>typeAs().writeFixed(this.row, this.cursor, column.get(), value);
            case VARIABLE:
                return type.<LayoutUtf8Writable>typeAs().writeVariable(this.row, this.cursor, column.get(), value);
        }

        return Result.NOT_FOUND;
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     *
     * @param <TValue> The sub-element type of the field.
     * @param path     The scope-relative path of the field to write.
     * @param value    The value to write.
     * @return {@link Result#SUCCESS} if the write is successful, an error {@link Result} otherwise.
     */
    private <TValue> Result writeSchematizedValue(UtfAnyString path, List<TValue> value) {

        final Optional<LayoutColumn> column = this.cursor.layout().tryFind(path);

        if (!column.isPresent()) {
            return Result.NOT_FOUND;
        }

        final LayoutType type = column.get().type();

        if (!(type instanceof LayoutListWritable)) {
            return Result.NOT_FOUND;
        }

        switch (column.get().storage()) {
            case FIXED:
                return type.<LayoutListWritable<TValue>>typeAs().writeFixedList(this.row, this.cursor, column.get(), value);
            case VARIABLE:
                return type.<LayoutListWritable<TValue>>typeAs().writeVariableList(this.row, this.cursor, column.get(), value);
        }

        return Result.NOT_FOUND;
    }

    /**
     * Functional interface for writing content to a {@link RowBuffer}.
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        /**
         * Write content using the specified writer, type argument, and context.
         *
         * @param writer  writes content.
         * @param typeArg specifies a type argument.
         * @param context provides context for the write operation.
         * @return a result code
         */
        Result invoke(RowWriter writer, TypeArgument typeArg, TContext context);
    }
}