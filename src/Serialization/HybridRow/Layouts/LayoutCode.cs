// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1028 // Enum Storage should be Int32

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
                              /// <summary>Type coded used in the binary encoding to indicate the formatting of succeeding bytes.</summary>
    public enum LayoutCode : byte
    {
        Invalid = 0,

        Null = 1,
        BooleanFalse = 2,
        Boolean = 3,

        Int8 = 5,
        Int16 = 6,
        Int32 = 7,
        Int64 = 8,
        UInt8 = 9,
        UInt16 = 10,
        UInt32 = 11,
        UInt64 = 12,
        VarInt = 13,
        VarUInt = 14,

        Float32 = 15,
        Float64 = 16,
        Decimal = 17,

        DateTime = 18,
        Guid = 19,

        Utf8 = 20,
        Binary = 21,

        Float128 = 22,
        UnixDateTime = 23,
        MongoDbObjectId = 24,

        ObjectScope = 30,
        ImmutableObjectScope = 31,
        ArrayScope = 32,
        ImmutableArrayScope = 33,
        TypedArrayScope = 34,
        ImmutableTypedArrayScope = 35,
        TupleScope = 36,
        ImmutableTupleScope = 37,
        TypedTupleScope = 38,
        ImmutableTypedTupleScope = 39,
        MapScope = 40,
        ImmutableMapScope = 41,
        TypedMapScope = 42,
        ImmutableTypedMapScope = 43,
        SetScope = 44,
        ImmutableSetScope = 45,
        TypedSetScope = 46,
        ImmutableTypedSetScope = 47,
        NullableScope = 48,
        ImmutableNullableScope = 49,
        TaggedScope = 50,
        ImmutableTaggedScope = 51,
        Tagged2Scope = 52,
        ImmutableTagged2Scope = 53,

        /// <summary>Nested row.</summary>
        Schema = 68,
        ImmutableSchema = 69,

        EndScope = 70,
    }
}
