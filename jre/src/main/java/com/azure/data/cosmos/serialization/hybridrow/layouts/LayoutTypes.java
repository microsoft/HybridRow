//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

/**
 * Layout type definitions
 */
public abstract class LayoutTypes {
    public static final LayoutArray Array = new LayoutArray(false);
    public static final LayoutBinary Binary = new LayoutBinary();
    public static final int BitsPerByte = 8;
    public static final LayoutBoolean Boolean = new LayoutBoolean(true);
    public static final LayoutBoolean BooleanFalse = new LayoutBoolean(false);
    public static final LayoutDateTime DateTime = new LayoutDateTime();
    public static final LayoutDecimal Decimal = new LayoutDecimal();
    public static final LayoutEndScope EndScope = new LayoutEndScope();
    public static final LayoutFloat128 Float128 = new LayoutFloat128();
    public static final LayoutFloat32 Float32 = new LayoutFloat32();
    public static final LayoutFloat64 Float64 = new LayoutFloat64();
    public static final LayoutGuid Guid = new LayoutGuid();
    public static final LayoutArray ImmutableArray = new LayoutArray(true);
    public static final LayoutNullable ImmutableNullable = new LayoutNullable(true);
    public static final LayoutObject ImmutableObject = new LayoutObject(true);
    public static final LayoutTagged ImmutableTagged = new LayoutTagged(true);
    public static final LayoutTagged2 ImmutableTagged2 = new LayoutTagged2(true);
    public static final LayoutTuple ImmutableTuple = new LayoutTuple(true);
    public static final LayoutTypedArray ImmutableTypedArray = new LayoutTypedArray(true);
    public static final LayoutTypedMap ImmutableTypedMap = new LayoutTypedMap(true);
    public static final LayoutTypedSet ImmutableTypedSet = new LayoutTypedSet(true);
    public static final LayoutTypedTuple ImmutableTypedTuple = new LayoutTypedTuple(true);
    public static final LayoutUDT ImmutableUDT = new LayoutUDT(true);
    public static final LayoutInt16 Int16 = new LayoutInt16();
    public static final LayoutInt32 Int32 = new LayoutInt32();
    public static final LayoutInt64 Int64 = new LayoutInt64();
    public static final LayoutInt8 Int8 = new LayoutInt8();
    public static final LayoutMongoDbObjectId MongoDbObjectId = new LayoutMongoDbObjectId();
    public static final LayoutNull Null = new LayoutNull();
    public static final LayoutNullable Nullable = new LayoutNullable(false);
    public static final LayoutObject Object = new LayoutObject(false);
    public static final LayoutTagged Tagged = new LayoutTagged(false);
    public static final LayoutTagged2 Tagged2 = new LayoutTagged2(false);
    public static final LayoutTuple Tuple = new LayoutTuple(false);
    public static final LayoutTypedArray TypedArray = new LayoutTypedArray(false);
    public static final LayoutTypedMap TypedMap = new LayoutTypedMap(false);
    public static final LayoutTypedSet TypedSet = new LayoutTypedSet(false);
    public static final LayoutTypedTuple TypedTuple = new LayoutTypedTuple(false);
    public static final LayoutUDT UDT = new LayoutUDT(false);
    public static final LayoutUInt16 UInt16 = new LayoutUInt16();
    public static final LayoutUInt32 UInt32 = new LayoutUInt32();
    public static final LayoutUInt64 UInt64 = new LayoutUInt64();
    public static final LayoutUInt8 UInt8 = new LayoutUInt8();
    public static final LayoutUnixDateTime UnixDateTime = new LayoutUnixDateTime();
    public static final LayoutUtf8 Utf8 = new LayoutUtf8();
    public static final LayoutVarInt VarInt = new LayoutVarInt();
    public static final LayoutVarUInt VarUInt = new LayoutVarUInt();
}
