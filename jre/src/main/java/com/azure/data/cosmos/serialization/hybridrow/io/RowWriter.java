//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.RowCursorExtensions;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public ref struct RowWriter
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class RowWriter {
    private RowCursor cursor = new RowCursor();
    private RowBuffer row = new RowBuffer();

    /**
     * Initializes a new instance of the <see cref="RowWriter" /> struct.
     *
     * @param row   The row to be read.
     * @param scope The scope into which items should be written.
     *              <p>
     *              A <see cref="RowWriter" /> instance writes the fields of a given scope from left to right
     *              in a forward only manner. If the root scope is provided then all top-level fields in the row can be
     *              written.
     */
    public RowWriter() {
    }

    private RowWriter(RefObject<RowBuffer> row, RefObject<RowCursor> scope) {
        this.row = row.get().clone();
        this.cursor = scope.get().clone();
    }

    /**
     * The active layout of the current writer scope.
     */
    public Layout getLayout() {
        return this.cursor.layout;
    }

    /**
     * The length of row in bytes.
     */
    public int getLength() {
        return this.row.getLength();
    }

    /**
     * The resolver for UDTs.
     */
    public LayoutResolver getResolver() {
        return this.row.getResolver();
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteBinary(UtfAnyString path, byte[] value)
    public Result WriteBinary(UtfAnyString path, byte[] value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.Binary, (ref RowWriter w, byte[] v) => w
        // .row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.Binary,
            (ref RowWriter w, byte[] v) -> w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteBinary(UtfAnyString path, ReadOnlySpan<byte> value)
    public Result WriteBinary(UtfAnyString path, ReadOnlySpan<Byte> value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.Binary, (ref RowWriter w,
        // ReadOnlySpan<byte> v) => w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.Binary,
            (ref RowWriter w, ReadOnlySpan<Byte> v) -> w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a variable length, sequence of bytes.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteBinary(UtfAnyString path, ReadOnlySequence<byte> value)
    public Result WriteBinary(UtfAnyString path, ReadOnlySequence<Byte> value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.Binary, (ref RowWriter w,
        // ReadOnlySequence<byte> v) => w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.Binary,
            (ref RowWriter w, ReadOnlySequence<Byte> v) -> w.row.WriteSparseBinary(ref w.cursor, v,
                UpdateOptions.Upsert));
    }

    /**
     * Write a field as a <see cref="bool" />.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteBool(UtfAnyString path, boolean value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Boolean,
            (ref RowWriter w, boolean v) -> w.row.WriteSparseBool(ref w.cursor, v, UpdateOptions.Upsert));
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
    public static <TContext> Result WriteBuffer(RefObject<RowBuffer> row, TContext context,
                                                WriterFunc<TContext> func) {
        RowCursor scope = RowCursor.Create(row);
        RefObject<RowCursor> tempRef_scope =
            new RefObject<RowCursor>(scope);
        RowWriter writer = new RowWriter(row, tempRef_scope);
        scope = tempRef_scope.get();
        TypeArgument typeArg = new TypeArgument(LayoutType.UDT,
            new TypeArgumentList(scope.layout.getSchemaId().clone()));
        RefObject<RowWriter> tempRef_writer =
            new RefObject<RowWriter>(writer);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        Result result = func(ref writer, typeArg, context);
        writer = tempRef_writer.get();
        row.set(writer.row.clone());
        return result;
    }

    /**
     * Write a field as a fixed length <see cref="DateTime" /> value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteDateTime(UtfAnyString path, LocalDateTime value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.DateTime,
            (ref RowWriter w, LocalDateTime v) -> w.row.WriteSparseDateTime(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a fixed length <see cref="decimal" /> value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteDecimal(UtfAnyString path, BigDecimal value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Decimal,
            (ref RowWriter w, BigDecimal v) -> w.row.WriteSparseDecimal(ref w.cursor, v, UpdateOptions.Upsert));
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
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value.clone(), LayoutType.Float128,
            (ref RowWriter w, Float128 v) -> w.row.WriteSparseFloat128(ref w.cursor, v.clone(), UpdateOptions.Upsert));
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
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Float32,
            (ref RowWriter w, float v) -> w.row.WriteSparseFloat32(ref w.cursor, v, UpdateOptions.Upsert));
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
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Float64,
            (ref RowWriter w, double v) -> w.row.WriteSparseFloat64(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a fixed length <see cref="Guid" /> value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteGuid(UtfAnyString path, UUID value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Guid,
            (ref RowWriter w, UUID v) -> w.row.WriteSparseGuid(ref w.cursor, v, UpdateOptions.Upsert));
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
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Int16,
            (ref RowWriter w, short v) -> w.row.WriteSparseInt16(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a fixed length, 32-bit, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteInt32(UtfAnyString path, int value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Int32,
            (ref RowWriter w, int v) -> w.row.WriteSparseInt32(ref w.cursor, v, UpdateOptions.Upsert));
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
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Int64,
            (ref RowWriter w, long v) -> w.row.WriteSparseInt64(ref w.cursor, v, UpdateOptions.Upsert));
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
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Int8,
            (ref RowWriter w, byte v) -> w.row.WriteSparseInt8(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a fixed length <see cref="MongoDbObjectId" /> value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteMongoDbObjectId(UtfAnyString path, MongoDbObjectId value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value.clone(), LayoutType.MongoDbObjectId, (ref RowWriter w,
                                                                                     MongoDbObjectId v) -> w.row.WriteSparseMongoDbObjectId(ref w.cursor, v.clone(), UpdateOptions.Upsert));
    }

    /**
     * Write a field as a <see cref="t:null"/>.
     *
     * @param path The scope-relative path of the field to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteNull(UtfAnyString path) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, NullValue.Default, LayoutType.Null,
            (ref RowWriter w, NullValue v) -> w.row.WriteSparseNull(ref w.cursor, v.clone(), UpdateOptions.Upsert));
    }

    public <TContext> Result WriteScope(UtfAnyString path, TypeArgument typeArg, TContext context,
                                        WriterFunc<TContext> func) {
        LayoutType type = typeArg.getType();
        Result result = this.PrepareSparseWrite(path, typeArg.clone());
        if (result != Result.Success) {
            return result;
        }

        RowCursor nestedScope = new RowCursor();
        switch (type) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutObject scopeType:
            case LayoutObject
                scopeType:
                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope =
                    new OutObject<RowCursor>();
                this.row.WriteSparseObject(tempRef_cursor, scopeType, UpdateOptions.Upsert, tempOut_nestedScope);
                nestedScope = tempOut_nestedScope.get();
                this.cursor = tempRef_cursor.argValue;
                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutArray scopeType:
            case LayoutArray
                scopeType:
                RefObject<RowCursor> tempRef_cursor2 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope2 =
                    new OutObject<RowCursor>();
                this.row.WriteSparseArray(tempRef_cursor2, scopeType, UpdateOptions.Upsert, tempOut_nestedScope2);
                nestedScope = tempOut_nestedScope2.get();
                this.cursor = tempRef_cursor2.argValue;
                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedArray scopeType:
            case LayoutTypedArray
                scopeType:
                RefObject<RowCursor> tempRef_cursor3 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope3 =
                    new OutObject<RowCursor>();
                this.row.WriteTypedArray(tempRef_cursor3, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope3);
                nestedScope = tempOut_nestedScope3.get();
                this.cursor = tempRef_cursor3.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTuple scopeType:
            case LayoutTuple
                scopeType:
                RefObject<RowCursor> tempRef_cursor4 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope4 =
                    new OutObject<RowCursor>();
                this.row.WriteSparseTuple(tempRef_cursor4, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope4);
                nestedScope = tempOut_nestedScope4.get();
                this.cursor = tempRef_cursor4.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedTuple scopeType:
            case LayoutTypedTuple
                scopeType:
                RefObject<RowCursor> tempRef_cursor5 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope5 =
                    new OutObject<RowCursor>();
                this.row.WriteTypedTuple(tempRef_cursor5, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope5);
                nestedScope = tempOut_nestedScope5.get();
                this.cursor = tempRef_cursor5.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged scopeType:
            case LayoutTagged
                scopeType:
                RefObject<RowCursor> tempRef_cursor6 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope6 =
                    new OutObject<RowCursor>();
                this.row.WriteTypedTuple(tempRef_cursor6, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope6);
                nestedScope = tempOut_nestedScope6.get();
                this.cursor = tempRef_cursor6.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged2 scopeType:
            case LayoutTagged2
                scopeType:
                RefObject<RowCursor> tempRef_cursor7 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope7 =
                    new OutObject<RowCursor>();
                this.row.WriteTypedTuple(tempRef_cursor7, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope7);
                nestedScope = tempOut_nestedScope7.get();
                this.cursor = tempRef_cursor7.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNullable scopeType:
            case LayoutNullable
                scopeType:
                RefObject<RowCursor> tempRef_cursor8 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope8 =
                    new OutObject<RowCursor>();
                this.row.WriteNullable(tempRef_cursor8, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, func != null, tempOut_nestedScope8);
                nestedScope = tempOut_nestedScope8.get();
                this.cursor = tempRef_cursor8.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUDT scopeType:
            case LayoutUDT
                scopeType:
                Layout udt = this.row.getResolver().Resolve(typeArg.getTypeArgs().getSchemaId().clone());
                RefObject<RowCursor> tempRef_cursor9 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope9 =
                    new OutObject<RowCursor>();
                this.row.WriteSparseUDT(tempRef_cursor9, scopeType, udt, UpdateOptions.Upsert, tempOut_nestedScope9);
                nestedScope = tempOut_nestedScope9.get();
                this.cursor = tempRef_cursor9.get();
                break;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedSet scopeType:
            case LayoutTypedSet
                scopeType:
                RefObject<RowCursor> tempRef_cursor10 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope10 =
                    new OutObject<RowCursor>();
                this.row.WriteTypedSet(tempRef_cursor10, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope10);
                nestedScope = tempOut_nestedScope10.get();
                this.cursor = tempRef_cursor10.argValue;

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedMap scopeType:
            case LayoutTypedMap
                scopeType:
                RefObject<RowCursor> tempRef_cursor11 =
                    new RefObject<RowCursor>(this.cursor);
                OutObject<RowCursor> tempOut_nestedScope11 =
                    new OutObject<RowCursor>();
                this.row.WriteTypedMap(tempRef_cursor11, scopeType, typeArg.getTypeArgs().clone(),
                    UpdateOptions.Upsert, tempOut_nestedScope11);
                nestedScope = tempOut_nestedScope11.get();
                this.cursor = tempRef_cursor11.argValue;

                break;

            default:
                return Result.Failure;
        }

        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(this.row);
        RefObject<RowCursor> tempRef_nestedScope =
            new RefObject<RowCursor>(nestedScope);
        RowWriter nestedWriter = new RowWriter(tempRef_row, tempRef_nestedScope);
        nestedScope = tempRef_nestedScope.get();
        this.row = tempRef_row.get();
        RefObject<RowWriter> tempRef_nestedWriter =
            new RefObject<RowWriter>(nestedWriter);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        result = func == null ? null : func.Invoke(ref nestedWriter, typeArg, context) ??Result.Success;
        nestedWriter = tempRef_nestedWriter.get();
        this.row = nestedWriter.row.clone();
        nestedScope.count = nestedWriter.cursor.count;

        if (result != Result.Success) {
            // TODO: what about unique violations here?
            return result;
        }

        if (type instanceof LayoutUniqueScope) {
            RefObject<RowCursor> tempRef_nestedScope2 =
                new RefObject<RowCursor>(nestedScope);
            result = this.row.TypedCollectionUniqueIndexRebuild(tempRef_nestedScope2);
            nestedScope = tempRef_nestedScope2.get();
            if (result != Result.Success) {
                // TODO: If the index rebuild fails then the row is corrupted.  Should we automatically clean up here?
                return result;
            }
        }

        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(this.row);
        RefObject<RowCursor> tempRef_cursor12 =
            new RefObject<RowCursor>(nestedWriter.cursor);
        RowCursorExtensions.MoveNext(this.cursor.clone(), tempRef_row2
            , tempRef_cursor12);
        nestedWriter.cursor = tempRef_cursor12.get();
        this.row = tempRef_row2.get();
        return Result.Success;
    }

    /**
     * Write a field as a variable length, UTF8 encoded, string value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteString(UtfAnyString path, String value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Utf8,
            (ref RowWriter w, String v) -> w.row.WriteSparseString(ref w.cursor, Utf8Span.TranscodeUtf16(v),
                UpdateOptions.Upsert));
    }

    /**
     * Write a field as a variable length, UTF8 encoded, string value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteString(UtfAnyString path, Utf8Span value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.Utf8,
            (ref RowWriter w, Utf8Span v) -> w.row.WriteSparseString(ref w.cursor, v, UpdateOptions.Upsert));
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
    public Result WriteUInt16(UtfAnyString path, short value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.UInt16, (ref RowWriter w, ushort v) => w
        // .row.WriteSparseUInt16(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.UInt16,
            (ref RowWriter w, short v) -> w.row.WriteSparseUInt16(ref w.cursor, v, UpdateOptions.Upsert));
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
    public Result WriteUInt32(UtfAnyString path, int value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.UInt32, (ref RowWriter w, uint v) => w
        // .row.WriteSparseUInt32(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.UInt32,
            (ref RowWriter w, int v) -> w.row.WriteSparseUInt32(ref w.cursor, v, UpdateOptions.Upsert));
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
    public Result WriteUInt64(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.UInt64, (ref RowWriter w, ulong v) => w
        // .row.WriteSparseUInt64(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.UInt64,
            (ref RowWriter w, long v) -> w.row.WriteSparseUInt64(ref w.cursor, v, UpdateOptions.Upsert));
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
    public Result WriteUInt8(UtfAnyString path, byte value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.UInt8, (ref RowWriter w, byte v) => w.row
        // .WriteSparseUInt8(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.UInt8,
            (ref RowWriter w, byte v) -> w.row.WriteSparseUInt8(ref w.cursor, v, UpdateOptions.Upsert));
    }

    /**
     * Write a field as a fixed length <see cref="UnixDateTime" /> value.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteUnixDateTime(UtfAnyString path, UnixDateTime value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value.clone(), LayoutType.UnixDateTime,
            (ref RowWriter w, UnixDateTime v) -> w.row.WriteSparseUnixDateTime(ref w.cursor, v.clone(),
                UpdateOptions.Upsert));
    }

    /**
     * Write a field as a variable length, 7-bit encoded, signed integer.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    public Result WriteVarInt(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        return this.WritePrimitive(path, value, LayoutType.VarInt,
            (ref RowWriter w, long v) -> w.row.WriteSparseVarInt(ref w.cursor, v, UpdateOptions.Upsert));
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
    public Result WriteVarUInt(UtfAnyString path, long value) {
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WritePrimitive(path, value, LayoutType.VarUInt, (ref RowWriter w, ulong v) => w
        // .row.WriteSparseVarUInt(ref w.cursor, v, UpdateOptions.Upsert));
        return this.WritePrimitive(path, value, LayoutType.VarUInt,
            (ref RowWriter w, long v) -> w.row.WriteSparseVarUInt(ref w.cursor, v, UpdateOptions.Upsert));
    }

    public RowWriter clone() {
        RowWriter varCopy = new RowWriter();

        varCopy.row = this.row.clone();
        varCopy.cursor = this.cursor.clone();

        return varCopy;
    }

    /**
     * Helper for preparing the write of a sparse field.
     *
     * @param path    The path identifying the field to write.
     * @param typeArg The (optional) type constraints.
     * @return Success if the write is permitted, the error code otherwise.
     */
    private Result PrepareSparseWrite(UtfAnyString path, TypeArgument typeArg) {
        if (this.cursor.scopeType.IsFixedArity && !(this.cursor.scopeType instanceof LayoutNullable)) {
            if ((this.cursor.index < this.cursor.scopeTypeArgs.getCount()) && !typeArg.equals(this.cursor.scopeTypeArgs.get(this.cursor.index).clone())) {
                return Result.TypeConstraint;
            }
        } else if (this.cursor.scopeType instanceof LayoutTypedMap) {
            RefObject<RowCursor> tempRef_cursor =
                new RefObject<RowCursor>(this.cursor);
            if (!typeArg.equals(this.cursor.scopeType.<LayoutUniqueScope>TypeAs().FieldType(tempRef_cursor).clone())) {
                this.cursor = tempRef_cursor.get();
                return Result.TypeConstraint;
            } else {
                this.cursor = tempRef_cursor.get();
            }
        } else if (this.cursor.scopeType.IsTypedScope && !typeArg.equals(this.cursor.scopeTypeArgs.get(0).clone())) {
            return Result.TypeConstraint;
        }

        this.cursor.writePath = path;
        return Result.Success;
    }

    /**
     * Helper for writing a primitive value.
     * <typeparam name="TLayoutType">The type of layout type.</typeparam>
     *
     * @param path   The scope-relative path of the field to write.
     * @param value  The value to write.
     * @param type   The layout type.
     * @param sparse The <see cref="RowBuffer" /> access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TLayoutType extends LayoutType<String> & ILayoutUtf8SpanWritable> Result WritePrimitive(UtfAnyString path, Utf8Span value, TLayoutType type, AccessUtf8SpanMethod sparse) {
        Result result = Result.NotFound;
        if (this.cursor.scopeType instanceof LayoutUDT) {
            result = this.WriteSchematizedValue(path, value);
        }

        if (result == Result.NotFound) {
            // Write sparse value.
            result = this.PrepareSparseWrite(path, type.getTypeArg().clone());
            if (result != Result.Success) {
                return result;
            }

            RefObject<RowWriter> tempRef_this =
                new RefObject<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempRef_this.get();
            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(this.row);
            RowCursorExtensions.MoveNext(this.cursor.clone(),
                tempRef_row);
            this.row = tempRef_row.get();
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
     * @param sparse The <see cref="RowBuffer" /> access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TLayoutType extends LayoutType<TElement[]> & ILayoutSpanWritable<TElement>, TElement> Result WritePrimitive(UtfAnyString path, ReadOnlySpan<TElement> value, TLayoutType type, AccessReadOnlySpanMethod<TElement> sparse) {
        Result result = Result.NotFound;
        if (this.cursor.scopeType instanceof LayoutUDT) {
            result = this.WriteSchematizedValue(path, value);
        }

        if (result == Result.NotFound) {
            // Write sparse value.
            result = this.PrepareSparseWrite(path, type.getTypeArg().clone());
            if (result != Result.Success) {
                return result;
            }

            RefObject<RowWriter> tempRef_this =
                new RefObject<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempRef_this.get();
            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(this.row);
            RowCursorExtensions.MoveNext(this.cursor.clone(),
                tempRef_row);
            this.row = tempRef_row.get();
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
     * @param sparse The <see cref="RowBuffer" /> access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TLayoutType extends LayoutType<TElement[]> & ILayoutSequenceWritable<TElement>, TElement> Result WritePrimitive(UtfAnyString path, ReadOnlySequence<TElement> value, TLayoutType type, AccessMethod<ReadOnlySequence<TElement>> sparse) {
        Result result = Result.NotFound;
        if (this.cursor.scopeType instanceof LayoutUDT) {
            result = this.WriteSchematizedValue(path, value);
        }

        if (result == Result.NotFound) {
            // Write sparse value.
            result = this.PrepareSparseWrite(path, type.getTypeArg().clone());
            if (result != Result.Success) {
                return result;
            }

            RefObject<RowWriter> tempRef_this =
                new RefObject<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempRef_this.get();
            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(this.row);
            RowCursorExtensions.MoveNext(this.cursor.clone(),
                tempRef_row);
            this.row = tempRef_row.get();
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
     * @param sparse The <see cref="RowBuffer" /> access method for <paramref name="type" />.
     * @return Success if the write is successful, an error code otherwise.
     */
    private <TValue> Result WritePrimitive(UtfAnyString path, TValue value, LayoutType<TValue> type,
                                           AccessMethod<TValue> sparse) {
        Result result = Result.NotFound;
        if (this.cursor.scopeType instanceof LayoutUDT) {
            result = this.WriteSchematizedValue(path, value);
        }

        if (result == Result.NotFound) {
            // Write sparse value.

            result = this.PrepareSparseWrite(path, type.getTypeArg().clone());
            if (result != Result.Success) {
                return result;
            }

            RefObject<RowWriter> tempRef_this =
                new RefObject<RowWriter>(this);
            // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
            sparse(ref this, value)
            this = tempRef_this.get();
            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(this.row);
            RowCursorExtensions.MoveNext(this.cursor.clone(),
                tempRef_row);
            this.row = tempRef_row.get();
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
    private <TValue> Result WriteSchematizedValue(UtfAnyString path, TValue value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (!this.cursor.layout.TryFind(path, out col)) {
            return Result.NotFound;
        }

        boolean tempVar = col.Type instanceof LayoutType<TValue>;
        LayoutType<TValue> t = tempVar ? (LayoutType<TValue>)col.Type : null;
        if (!(tempVar)) {
            return Result.NotFound;
        }

        switch (col.Storage) {
            case StorageKind.Fixed:
                RefObject<RowBuffer> tempRef_row =
                    new RefObject<RowBuffer>(this.row);
                Result tempVar2 = t.WriteFixed(ref this.row, ref this.cursor, col, value)
                this.row = tempRef_row.get();
                return tempVar2;

            case StorageKind.Variable:
                RefObject<RowBuffer> tempRef_row2 =
                    new RefObject<RowBuffer>(this.row);
                Result tempVar3 = t.WriteVariable(ref this.row, ref this.cursor, col, value)
                this.row = tempRef_row2.get();
                return tempVar3;

            default:
                return Result.NotFound;
        }

        return Result.NotFound;
    }

    /**
     * Write a generic schematized field value via the scope's layout.
     *
     * @param path  The scope-relative path of the field to write.
     * @param value The value to write.
     * @return Success if the write is successful, an error code otherwise.
     */
    private Result WriteSchematizedValue(UtfAnyString path, Utf8Span value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (!this.cursor.layout.TryFind(path, out col)) {
            return Result.NotFound;
        }

        LayoutType t = col.Type;
        if (!(t instanceof ILayoutUtf8SpanWritable)) {
            return Result.NotFound;
        }

        switch (col.Storage) {
            case StorageKind.Fixed:
                RefObject<RowBuffer> tempRef_row =
                    new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutUtf8SpanWritable>TypeAs().WriteFixed(tempRef_row, tempRef_cursor, col,
                    value);
                this.cursor = tempRef_cursor.get();
                this.row = tempRef_row.get();
                return tempVar;
            case StorageKind.Variable:
                RefObject<RowBuffer> tempRef_row2 =
                    new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor2 =
                    new RefObject<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutUtf8SpanWritable>TypeAs().WriteVariable(tempRef_row2, tempRef_cursor2,
                    col, value);
                this.cursor = tempRef_cursor2.get();
                this.row = tempRef_row2.get();
                return tempVar2;
            default:
                return Result.NotFound;
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
    private <TElement> Result WriteSchematizedValue(UtfAnyString path, ReadOnlySpan<TElement> value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (!this.cursor.layout.TryFind(path, out col)) {
            return Result.NotFound;
        }

        LayoutType t = col.Type;
        if (!(t instanceof ILayoutSpanWritable<TElement>)) {
            return Result.NotFound;
        }

        switch (col.Storage) {
            case StorageKind.Fixed:
                RefObject<RowBuffer> tempRef_row = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor = new RefObject<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSpanWritable<TElement>>TypeAs().WriteFixed(tempRef_row, tempRef_cursor, col, value);
                this.cursor = tempRef_cursor.get();
                this.row = tempRef_row.get();
                return tempVar;
            case StorageKind.Variable:
                RefObject<RowBuffer> tempRef_row2 = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor2 = new RefObject<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSpanWritable<TElement>>TypeAs().WriteVariable(tempRef_row2, tempRef_cursor2, col, value);
                this.cursor = tempRef_cursor2.get();
                this.row = tempRef_row2.get();
                return tempVar2;
            default:
                return Result.NotFound;
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
    private <TElement> Result WriteSchematizedValue(UtfAnyString path, ReadOnlySequence<TElement> value) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (!this.cursor.layout.TryFind(path, out col)) {
            return Result.NotFound;
        }

        LayoutType t = col.Type;
        if (!(t instanceof ILayoutSequenceWritable<TElement>)) {
            return Result.NotFound;
        }

        switch (col.Storage) {
            case StorageKind.Fixed:
                RefObject<RowBuffer> tempRef_row = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor = new RefObject<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSequenceWritable<TElement>>TypeAs().WriteFixed(tempRef_row, tempRef_cursor, col, value);
                this.cursor = tempRef_cursor.get();
                this.row = tempRef_row.get();
                return tempVar;
            case StorageKind.Variable:
                RefObject<RowBuffer> tempRef_row2 = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor2 = new RefObject<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSequenceWritable<TElement>>TypeAs().WriteVariable(tempRef_row2, tempRef_cursor2, col, value);
                this.cursor = tempRef_cursor2.get();
                this.row = tempRef_row2.get();
                return tempVar2;
            default:
                return Result.NotFound;
        }
    }

    @FunctionalInterface
    private interface AccessMethod<TValue> {
        void invoke(RefObject<RowWriter> writer, TValue value);
    }

    @FunctionalInterface
    private interface AccessReadOnlySpanMethod<T> {
        void invoke(RefObject<RowWriter> writer, ReadOnlySpan value);
    }

    @FunctionalInterface
    private interface AccessUtf8SpanMethod {
        void invoke(RefObject<RowWriter> writer, Utf8Span value);
    }

    /**
     * A function to write content into a <see cref="RowBuffer" />.
     * <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
     *
     * @param writer  A forward-only cursor for writing content.
     * @param typeArg The type of the current scope.
     * @param context A context value provided by the caller.
     * @return The result.
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        Result invoke(RefObject<RowWriter> writer, TypeArgument typeArg, TContext context);
    }
}