// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

/**
 * Layout type definitions
 */
public abstract class LayoutTypes {
    public static final LayoutArray ARRAY = new LayoutArray(false);
    public static final LayoutBinary BINARY = new LayoutBinary();
    public static final LayoutBoolean BOOLEAN = new LayoutBoolean(true);
    public static final LayoutBoolean BOOLEAN_FALSE = new LayoutBoolean(false);
    public static final LayoutDateTime DATE_TIME = new LayoutDateTime();
    public static final LayoutDecimal DECIMAL = new LayoutDecimal();
    public static final LayoutEndScope END_SCOPE = new LayoutEndScope();
    public static final LayoutFloat128 FLOAT_128 = new LayoutFloat128();
    public static final LayoutFloat32 FLOAT_32 = new LayoutFloat32();
    public static final LayoutFloat64 FLOAT_64 = new LayoutFloat64();
    public static final LayoutGuid GUID = new LayoutGuid();
    public static final LayoutArray IMMUTABLE_ARRAY = new LayoutArray(true);
    public static final LayoutNullable IMMUTABLE_NULLABLE = new LayoutNullable(true);
    public static final LayoutObject IMMUTABLE_OBJECT = new LayoutObject(true);
    public static final LayoutTagged IMMUTABLE_TAGGED = new LayoutTagged(true);
    public static final LayoutTagged2 IMMUTABLE_TAGGED_2 = new LayoutTagged2(true);
    public static final LayoutTuple ImmutableTuple = new LayoutTuple(true);
    public static final LayoutTypedArray IMMUTABLE_TYPED_ARRAY = new LayoutTypedArray(true);
    public static final LayoutTypedMap IMMUTABLE_TYPED_MAP = new LayoutTypedMap(true);
    public static final LayoutTypedSet IMMUTABLE_TYPED_SET = new LayoutTypedSet(true);
    public static final LayoutTypedTuple IMMUTABLE_TYPED_TUPLE = new LayoutTypedTuple(true);
    public static final LayoutUDT IMMUTABLE_UDT = new LayoutUDT(true);
    public static final LayoutInt16 INT_16 = new LayoutInt16();
    public static final LayoutInt32 INT_32 = new LayoutInt32();
    public static final LayoutInt64 INT_64 = new LayoutInt64();
    public static final LayoutInt8 INT_8 = new LayoutInt8();
    public static final LayoutMongoDbObjectId MONGODB_OBJECT_ID = new LayoutMongoDbObjectId();
    public static final LayoutNull NULL = new LayoutNull();
    public static final LayoutNullable NULLABLE = new LayoutNullable(false);
    public static final LayoutObject OBJECT = new LayoutObject(false);
    public static final LayoutTagged TAGGED = new LayoutTagged(false);
    public static final LayoutTagged2 TAGGED_2 = new LayoutTagged2(false);
    public static final LayoutTuple TUPLE = new LayoutTuple(false);
    public static final LayoutTypedArray TYPED_ARRAY = new LayoutTypedArray(false);
    public static final LayoutTypedMap TYPED_MAP = new LayoutTypedMap(false);
    public static final LayoutTypedSet TYPED_SET = new LayoutTypedSet(false);
    public static final LayoutTypedTuple TYPED_TUPLE = new LayoutTypedTuple(false);
    public static final LayoutUDT UDT = new LayoutUDT(false);
    public static final LayoutUInt16 UINT_16 = new LayoutUInt16();
    public static final LayoutUInt32 UINT_32 = new LayoutUInt32();
    public static final LayoutUInt64 UINT_64 = new LayoutUInt64();
    public static final LayoutUInt8 UINT_8 = new LayoutUInt8();
    public static final LayoutUnixDateTime UNIX_DATE_TIME = new LayoutUnixDateTime();
    public static final LayoutUtf8 UTF_8 = new LayoutUtf8();
    public static final LayoutVarInt VAR_INT = new LayoutVarInt();
    public static final LayoutVarUInt VAR_UINT = new LayoutVarUInt();
}
