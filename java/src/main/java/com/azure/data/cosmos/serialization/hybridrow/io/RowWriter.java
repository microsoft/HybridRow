// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutNullable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutSpanWritable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedMap;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypes;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUDT;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUniqueScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUtf8SpanWritable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;
import io.netty.buffer.ByteBuf;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class RowWriter {

    private RowCursor cursor;
    private RowBuffer row;

    /**
     * Initializes a new instance of the {@link RowWriter} class
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
     */
    public Layout layout() {
        return this.cursor.layout();
    }

    /**
     * The length of row in bytes.
     */
    public int length() {
        return this.row.length();
    }

    /**
     * The resolver for UDTs.
     */
    public LayoutResolver resolver() {
        return this.row.resolver();
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteBinary(UtfAnyString path, byte[] value) {
        return this.writePrimitive(path, value, LayoutTypes.BINARY,
            (ref RowWriter w, byte[] v) -> w.row.writeSparseBinary(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteBinary(UtfAnyString path, ByteBuf value) {
        return this.writePrimitive(path, value, LayoutTypes.BINARY,
            (ref RowWriter w, ByteBuf v) -> w.row.writeSparseBinary(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteBinary(UtfAnyString path, ReadOnlySequence<Byte> value) {
        return this.writePrimitive(path, value, LayoutTypes.BINARY,
            (ref RowWriter w, ReadOnlySequence<Byte> v) -> w.row.writeSparseBinary(ref w.cursor, v,
                UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a {@link Boolean}.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteBoolean(UtfAnyString path, boolean value) {
        return this.writePrimitive(path, value, LayoutTypes.BOOLEAN,
            (ref RowWriter w, boolean v) -> w.row.writeSparseBool(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write an entire row in a streaming left-to-right way.
     * <typeparam name="TContext">The type of the context value to pass to <paramref name="func" />.</typeparam>
     *
     * @param row     The row to write.
     * @param context A context value to pass to <paramref name="func" />.
     * @param func    A function to write the entire row.
     * @return Success if the write is successful, an error code otherwise.
     */
    public static <TContext> Result WriteBuffer(
        Reference<RowBuffer> row, TContext context, WriterFunc<TContext> func) {
        RowCursor scope = RowCursor.create(row);
        Reference<RowCursor> tempReference_scope =
            new Reference<RowCursor>(scope);
        RowWriter writer = new RowWriter(row, tempReference_scope);
        scope = tempReference_scope.get();
        TypeArgument typeArg = new TypeArgument(LayoutTypes.UDT,
            new TypeArgumentList(scope.layout().schemaId()));
        Reference<RowWriter> tempReference_writer =
            new Reference<RowWriter>(writer);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        Result result = func(ref writer, typeArg, context);
        writer = tempReference_writer.get();
        row.setAndGet(writer.row);
        return result;
    }

    /**
     * Write a field as a fixed length {@link DateTime} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteDateTime(UtfAnyString path, LocalDateTime value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.DATE_TIME,
            (ref RowWriter w, LocalDateTime v) -> w.row.writeSparseDateTime(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length {@link decimal} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteDecimal(UtfAnyString path, BigDecimal value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.DECIMAL,
            (ref RowWriter w, BigDecimal v) -> w.row.writeSparseDecimal(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 128-bit, IEEE-encoded floating point value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteFloat128(UtfAnyString path, Float128 value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.FLOAT_128,
            (ref RowWriter w, Float128 v) -> w.row.writeSparseFloat128(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 32-bit, IEEE-encoded floating point value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteFloat32(UtfAnyString path, float value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.FLOAT_32,
            (ref RowWriter w, float v) -> w.row.writeSparseFloat32(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 64-bit, IEEE-encoded floating point value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteFloat64(UtfAnyString path, double value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.FLOAT_64,
            (ref RowWriter w, double v) -> w.row.writeSparseFloat64(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length {@link Guid} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteGuid(UtfAnyString path, UUID value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.GUID,
            (ref RowWriter w, UUID v) -> w.row.writeSparseGuid(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 16-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteInt16(UtfAnyString path, short value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.INT_16,
            (ref RowWriter w, short v) -> w.row.writeSparseInt16(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 32-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result writeInt32(UtfAnyString path, int value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.INT_32,
            (ref RowWriter w, int v) -> w.row.writeSparseInt32(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 64-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteInt64(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.INT_64,
            (ref RowWriter w, long v) -> w.row.writeSparseInt64(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 8-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteInt8(UtfAnyString path, byte value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.INT_8,
            (ref RowWriter w, byte v) -> w.row.writeSparseInt8(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    // TODO: DANOBLE: Resurrect this method
    //    /**
    //     * Write a field as a fixed length {@link MongoDbObjectId} value.
    //     *
    //     * @param path  The scope-relative path of the field to write.
    //     * @param value The value to write.
    //     * @return Success if the write is successful, an error code otherwise.
    //     */
    //    public Result WriteMongoDbObjectId(UtfAnyString path, MongoDbObjectId value) {
    //        throw new UnsupportedOperationException();
    //        // return this.writePrimitive(path, value, LayoutTypes.MongoDbObjectId, (ref RowWriter w, MongoDbObjectId v) -> w.row.writeSparseMongoDbObjectId(ref w.cursor, v, UpdateOptions.UPSERT));
    //    }

    /**
     * Write a field as a {@link t:null}.
     *
     * @param path The scope-relative path of the field to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteNull(UtfAnyString path) {
        return this.writePrimitive(path, NullValue.Default, LayoutTypes.NULL,
            (ref RowWriter w, NullValue v) -> w.row.writeSparseNull(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    public <TContext> Result WriteScope(UtfAnyString path, TypeArgument typeArg, TContext context,
                                        WriterFunc<TContext> func) {
        LayoutType type = typeArg.type();
        Result result = this.prepareSparseWrite(path, typeArg);
        if (result != Result.SUCCESS) {
            return result;
        }

        RowCursor nestedScope = new RowCursor();
        switch (type) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutObject scopeType:
            case LayoutObject
                scopeType:
                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope =
                    new Out<RowCursor>();
                this.row.writeSparseObject(tempRef_cursor, scopeType, UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope.get();
                this.cursor = tempRef_cursor.argValue;
                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutArray scopeType:
            case LayoutArray
                scopeType:
                Reference<RowCursor> tempReference_cursor2 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope2 =
                    new Out<RowCursor>();
                this.row.writeSparseArray(tempRef_cursor2, scopeType, UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope2.get();
                this.cursor = tempRef_cursor2.argValue;
                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedArray scopeType:
            case LayoutTypedArray
                scopeType:
                Reference<RowCursor> tempReference_cursor3 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope3 =
                    new Out<RowCursor>();
                this.row.writeTypedArray(tempRef_cursor3, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT, tempOut_nestedScope3);
                nestedScope = tempOut_nestedScope3.get();
                this.cursor = tempRef_cursor3.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTuple scopeType:
            case LayoutTuple
                scopeType:
                Reference<RowCursor> tempReference_cursor4 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope4 =
                    new Out<RowCursor>();
                this.row.writeSparseTuple(tempRef_cursor4, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT, tempOut_nestedScope4);
                nestedScope = tempOut_nestedScope4.get();
                this.cursor = tempRef_cursor4.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedTuple scopeType:
            case LayoutTypedTuple
                scopeType:
                Reference<RowCursor> tempReference_cursor5 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope5 =
                    new Out<RowCursor>();
                this.row.writeTypedTuple(tempRef_cursor5, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope5.get();
                this.cursor = tempRef_cursor5.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged scopeType:
            case LayoutTagged
                scopeType:
                Reference<RowCursor> tempReference_cursor6 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope6 =
                    new Out<RowCursor>();
                this.row.writeTypedTuple(tempRef_cursor6, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope6.get();
                this.cursor = tempRef_cursor6.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged2 scopeType:
            case LayoutTagged2
                scopeType:
                Reference<RowCursor> tempReference_cursor7 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope7 =
                    new Out<RowCursor>();
                this.row.writeTypedTuple(tempRef_cursor7, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope7.get();
                this.cursor = tempRef_cursor7.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNullable scopeType:
            case LayoutNullable
                scopeType:
                Reference<RowCursor> tempReference_cursor8 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope8 =
                    new Out<RowCursor>();
                this.row.writeNullable(tempRef_cursor8, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT, func != null);
                nestedScope = tempOut_nestedScope8.get();
                this.cursor = tempRef_cursor8.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUDT scopeType:
            case LayoutUDT
                scopeType:
                Layout udt = this.row.resolver().resolve(typeArg.typeArgs().schemaId());
                Reference<RowCursor> tempReference_cursor9 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope9 =
                    new Out<RowCursor>();
                this.row.writeSparseUDT(tempReference_cursor9, scopeType, udt, UpdateOptions.UPSERT, tempOut_nestedScope9);
                nestedScope = tempOut_nestedScope9.get();
                this.cursor = tempReference_cursor9.get();
                break;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedSet scopeType:
            case LayoutTypedSet
                scopeType:
                Reference<RowCursor> tempReference_cursor10 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope10 =
                    new Out<RowCursor>();
                this.row.writeTypedSet(tempRef_cursor10, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope10.get();
                this.cursor = tempRef_cursor10.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedMap scopeType:
            case LayoutTypedMap
                scopeType:
                Reference<RowCursor> tempReference_cursor11 =
                    new Reference<RowCursor>(this.cursor);
                Out<RowCursor> tempOut_nestedScope11 =
                    new Out<RowCursor>();
                this.row.writeTypedMap(tempRef_cursor11, scopeType, typeArg.typeArgs(),
                    UpdateOptions.UPSERT);
                nestedScope = tempOut_nestedScope11.get();
                this.cursor = tempRef_cursor11.argValue;

                break;

            default:
                return Result.FAILURE;
        }

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(this.row);
        Reference<RowCursor> tempReference_nestedScope =
            new Reference<RowCursor>(nestedScope);
        RowWriter nestedWriter = new RowWriter(tempReference_row, tempReference_nestedScope);
        nestedScope = tempReference_nestedScope.get();
        this.row = tempReference_row.get();
        Reference<RowWriter> tempReference_nestedWriter =
            new Reference<RowWriter>(nestedWriter);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        result = func == null ? null : func.Invoke(ref nestedWriter, typeArg, context) ??Result.SUCCESS;
        nestedWriter = tempReference_nestedWriter.get();
        this.row = nestedWriter.row;
        nestedScope.count(nestedWriter.cursor.count());

        if (result != Result.SUCCESS) {
            // TODO: what about unique violations here?
            return result;
        }

        if (type instanceof LayoutUniqueScope) {
            Reference<RowCursor> tempReference_nestedScope2 =
                new Reference<RowCursor>(nestedScope);
            result = this.row.typedCollectionUniqueIndexRebuild(tempReference_nestedScope2);
            nestedScope = tempReference_nestedScope2.get();
            if (result != Result.SUCCESS) {
                // TODO: If the index rebuild fails then the row is corrupted.  Should we automatically clean up here?
                return result;
            }
        }

        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(this.row);
        Reference<RowCursor> tempReference_cursor12 =
            new Reference<RowCursor>(nestedWriter.cursor);
        RowCursors.moveNext(this.cursor, tempReference_row2
            , tempReference_cursor12);
        nestedWriter.cursor = tempReference_cursor12.get();
        this.row = tempReference_row2.get();
        return Result.SUCCESS;
    }

    /**
     * Write a field as a variable length, UTF8 encoded, string value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result writeString(UtfAnyString path, String value) {
        return this.writePrimitive(path, value, LayoutTypes.UTF_8,
            (ref RowWriter w, String v) -> w.row.writeSparseString(ref w.cursor, Utf8Span.TranscodeUtf16(v),
                UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a variable length, UTF8 encoded, string value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result writeString(UtfAnyString path, Utf8String value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.UTF_8,
            (ref RowWriter w, Utf8Span v) -> w.row.writeSparseString(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 16-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteUInt16(UtfAnyString path, ushort value)
    public Result writeUInt16(UtfAnyString path, short value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.writePrimitive(path, value, LayoutTypes.UInt16, (ref RowWriter w, ushort v) => w
        // .row.writeSparseUInt16(ref w.cursor, v, UpdateOptions.Upsert));
        return this.writePrimitive(path, value, LayoutTypes.UINT_16,
            (ref RowWriter w, short v) -> w.row.writeSparseUInt16(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 32-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteUInt32(UtfAnyString path, uint value)
    public Result writeUInt32(UtfAnyString path, int value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.writePrimitive(path, value, LayoutTypes.UInt32, (ref RowWriter w, uint v) => w
        // .row.writeSparseUInt32(ref w.cursor, v, UpdateOptions.Upsert));
        return this.writePrimitive(path, value, LayoutTypes.UINT_32,
            (ref RowWriter w, int v) -> w.row.writeSparseUInt32(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 64-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteUInt64(UtfAnyString path, ulong value)
    public Result writeUInt64(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.writePrimitive(path, value, LayoutTypes.UInt64, (ref RowWriter w, ulong v) => w
        // .row.writeSparseUInt64(ref w.cursor, v, UpdateOptions.Upsert));
        return this.writePrimitive(path, value, LayoutTypes.UINT_64,
            (ref RowWriter w, long v) -> w.row.writeSparseUInt64(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length, 8-bit, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteUInt8(UtfAnyString path, byte value)
    public Result writeUInt8(UtfAnyString path, byte value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.writePrimitive(path, value, LayoutTypes.UInt8, (ref RowWriter w, byte v) => w.row
        // .writeSparseUInt8(ref w.cursor, v, UpdateOptions.Upsert));
        return this.writePrimitive(path, value, LayoutTypes.UINT_8,
            (ref RowWriter w, byte v) -> w.row.writeSparseUInt8(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a fixed length {@link UnixDateTime} value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result writeUnixDateTime(UtfAnyString path, UnixDateTime value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.UNIX_DATE_TIME,
            (ref RowWriter w, UnixDateTime v) -> w.row.writeSparseUnixDateTime(ref w.cursor, v,
                UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a variable length, 7-bit encoded, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result writeVarInt(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        return this.writePrimitive(path, value, LayoutTypes.VAR_INT,
            (ref RowWriter w, long v) -> w.row.writeSparseVarInt(ref w.cursor, v, UpdateOptions.UPSERT));
    }

    /**
     * Write a field as a variable length, 7-bit encoded, unsigned integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVarUInt(UtfAnyString path, ulong value)
    public Result writeVarUInt(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.writePrimitive(path, value, LayoutTypes.VarUInt, (ref RowWriter w, ulong v) => w
        // .row.writeSparseVarUInt(ref w.cursor, v, UpdateOptions.Upsert));
        return this.writePrimitive(path, value, LayoutTypes.VAR_UINT,
            (ref RowWriter w, long v) -> w.row.writeSparseVarUInt(ref w.cursor, v, UpdateOptions.UPSERT));
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
            Reference<RowCursor> tempReference_cursor =
                new Reference<RowCursor>(this.cursor);
            if (!typeArg.equals(this.cursor.scopeType().<LayoutUniqueScope>typeAs().fieldType(tempReference_cursor))) {
                this.cursor = tempReference_cursor.get();
                return Result.TYPE_CONSTRAINT;
            } else {
                this.cursor = tempReference_cursor.get();
            }
        } else if (this.cursor.scopeType().isTypedScope() && !typeArg.equals(this.cursor.scopeTypeArgs().get(0))) {
            return Result.TYPE_CONSTRAINT;
        }

        this.cursor.writePath(path);
        return Result.SUCCESS;
    }

    /**
     * Helper for writing a primitive value.
     * <typeparam name="TLayoutType">The type of layout type.</typeparam>
     *
     * @param path   The scope-relative path of the field to write.
     * @param value  The value to write.
     * @param type   The layout type.
     * @param sparse The {@link RowBuffer} access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TLayoutType extends LayoutType<String> & LayoutUtf8SpanWritable> Result writePrimitive(UtfAnyString path, Utf8String value, TLayoutType type, AccessUtf8SpanMethod sparse) {
        Result result = Result.NOT_FOUND;
        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {
            // Write sparse value.
            result = this.prepareSparseWrite(path, type.typeArg());
            if (result != Result.SUCCESS) {
                return result;
            }

            Reference<RowWriter> tempReference_this =
                new Reference<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempReference_this.get();
            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(this.row);
            RowCursors.moveNext(this.cursor,
                tempReference_row);
            this.row = tempReference_row.get();
        }

        return result;
    }

    /**
     * Helper for writing a primitive value.
     * <typeparam name="TLayoutType">The type of layout type.</typeparam>
     * <typeparam name="TElement">The sub-element type of the field.</typeparam>
     *
     * @param path   The scope-relative path of the field to write.
     * @param value  The value to write.
     * @param type   The layout type.
     * @param sparse The {@link RowBuffer} access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TLayoutType extends LayoutType<TElement[]> & LayoutSpanWritable<TElement>, TElement> Result writePrimitive(UtfAnyString path, ReadOnlySpan<TElement> value, TLayoutType type, AccessReadOnlySpanMethod<TElement> sparse) {
        Result result = Result.NOT_FOUND;
        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {
            // Write sparse value.
            result = this.prepareSparseWrite(path, type.typeArg());
            if (result != Result.SUCCESS) {
                return result;
            }

            Reference<RowWriter> tempReference_this =
                new Reference<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempReference_this.get();
            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(this.row);
            RowCursors.moveNext(this.cursor,
                tempReference_row);
            this.row = tempReference_row.get();
        }

        return result;
    }

    /**
     * Helper for writing a primitive value.
     * <typeparam name="TLayoutType">The type of layout type.</typeparam>
     * <typeparam name="TElement">The sub-element type of the field.</typeparam>
     *
     * @param path   The scope-relative path of the field to write.
     * @param value  The value to write.
     * @param type   The layout type.
     * @param sparse The {@link RowBuffer} access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TLayoutType extends LayoutType<TElement[]> & ILayoutSequenceWritable<TElement>, TElement> Result writePrimitive(UtfAnyString path, ReadOnlySequence<TElement> value, TLayoutType type, AccessMethod<ReadOnlySequence<TElement>> sparse) {
        Result result = Result.NOT_FOUND;
        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {
            // Write sparse value.
            result = this.prepareSparseWrite(path, type.typeArg());
            if (result != Result.SUCCESS) {
                return result;
            }

            Reference<RowWriter> tempReference_this =
                new Reference<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempReference_this.get();
            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(this.row);
            RowCursors.moveNext(this.cursor,
                tempReference_row);
            this.row = tempReference_row.get();
        }

        return result;
    }

    /**
     * Helper for writing a primitive value.
     * <typeparam name="TValue">The type of the primitive value.</typeparam>
     *
     * @param path   The scope-relative path of the field to write.
     * @param value  The value to write.
     * @param type   The layout type.
     * @param sparse The {@link RowBuffer} access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TValue> Result writePrimitive(UtfAnyString path, TValue value, LayoutType<TValue> type,
                                           AccessMethod<TValue> sparse) {
        Result result = Result.NOT_FOUND;
        if (this.cursor.scopeType() instanceof LayoutUDT) {
            result = this.writeSchematizedValue(path, value);
        }

        if (result == Result.NOT_FOUND) {
            // Write sparse value.

            result = this.prepareSparseWrite(path, type.typeArg());
            if (result != Result.SUCCESS) {
                return result;
            }

            Reference<RowWriter> tempReference_this =
                new Reference<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempReference_this.get();
            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(this.row);
            RowCursors.moveNext(this.cursor,
                tempReference_row);
            this.row = tempReference_row.get();
        }

        return result;
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     * <typeparam name="TValue">The expected type of the field.</typeparam>
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TValue> Result writeSchematizedValue(UtfAnyString path, TValue value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        if (!this.cursor.layout().tryFind(path, out col)) {
            return Result.NOT_FOUND;
        }

        boolean tempVar = col.type() instanceof LayoutType<TValue>;
        LayoutType<TValue> t = tempVar ? (LayoutType<TValue>)col.type() : null;
        if (!(tempVar)) {
            return Result.NOT_FOUND;
        }

        switch (col.storage()) {
            case StorageKind.FIXED:
                Reference<RowBuffer> tempReference_row =
                    new Reference<RowBuffer>(this.row);
                Result tempVar2 = t.writeFixed(ref this.row, ref this.cursor, col, value)
                this.row = tempReference_row.get();
                return tempVar2;

            case StorageKind.VARIABLE:
                Reference<RowBuffer> tempReference_row2 =
                    new Reference<RowBuffer>(this.row);
                Result tempVar3 = t.writeVariable(ref this.row, ref this.cursor, col, value)
                this.row = tempReference_row2.get();
                return tempVar3;

            default:
                return Result.NOT_FOUND;
        }

        return Result.NOT_FOUND;
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    private Result writeSchematizedValue(UtfAnyString path, Utf8String value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        if (!this.cursor.layout().tryFind(path, out col)) {
            return Result.NOT_FOUND;
        }

        LayoutType t = col.type();
        if (!(t instanceof ILayoutUtf8SpanWritable)) {
            return Result.NOT_FOUND;
        }

        switch (col.storage()) {
            case StorageKind.FIXED:
                Reference<RowBuffer> tempReference_row =
                    new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutUtf8SpanWritable>typeAs().writeFixed(tempReference_row, tempReference_cursor, col,
                    value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case StorageKind.VARIABLE:
                Reference<RowBuffer> tempReference_row2 =
                    new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 =
                    new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutUtf8SpanWritable>typeAs().writeVariable(tempReference_row2,
                    tempReference_cursor2,
                    col, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                return Result.NOT_FOUND;
        }
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     * <typeparam name="TElement">The sub-element type of the field.</typeparam>
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TElement> Result writeSchematizedValue(UtfAnyString path, ReadOnlySpan<TElement> value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        if (!this.cursor.layout().tryFind(path, out col)) {
            return Result.NOT_FOUND;
        }

        LayoutType t = col.type();
        if (!(t instanceof ILayoutSpanWritable<TElement>)) {
            return Result.NOT_FOUND;
        }

        switch (col.storage()) {
            case StorageKind.FIXED:
                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor = new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSpanWritable<TElement>>typeAs().writeFixed(tempReference_row, tempReference_cursor, col, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case StorageKind.VARIABLE:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSpanWritable<TElement>>typeAs().writeVariable(tempReference_row2, tempReference_cursor2, col, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                return Result.NOT_FOUND;
        }
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     * <typeparam name="TElement">The sub-element type of the field.</typeparam>
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TElement> Result writeSchematizedValue(UtfAnyString path, ReadOnlySequence<TElement> value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        if (!this.cursor.layout().tryFind(path, out col)) {
            return Result.NOT_FOUND;
        }

        LayoutType t = col.type();
        if (!(t instanceof ILayoutSequenceWritable<TElement>)) {
            return Result.NOT_FOUND;
        }

        switch (col.storage()) {
            case StorageKind.FIXED:
                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor = new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSequenceWritable<TElement>>typeAs().writeFixed(tempReference_row, tempReference_cursor, col, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case StorageKind.VARIABLE:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSequenceWritable<TElement>>typeAs().writeVariable(tempReference_row2, tempReference_cursor2, col, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                return Result.NOT_FOUND;
        }
    }

    @FunctionalInterface
    private interface AccessMethod<TValue> {
        void invoke(Reference<RowWriter> writer, TValue value);
    }

    @FunctionalInterface
    private interface AccessReadOnlySpanMethod<T> {
        void invoke(Reference<RowWriter> writer, ReadOnlySpan value);
    }

    @FunctionalInterface
    private interface AccessUtf8SpanMethod {
        void invoke(Reference<RowWriter> writer, Utf8String value);
    }

    /**
     * A function to write content into a {@link RowBuffer}.
     * <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
     *
     * @param writer  A forward-only cursor for writing content.
     * @param typeArg The type of the current scope.
     * @param context A context value provided by the caller.
     * @return The result.
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        Result invoke(Reference<RowWriter> writer, TypeArgument typeArg, TContext context);
    }
}