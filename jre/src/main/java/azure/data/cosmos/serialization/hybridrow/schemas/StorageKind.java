//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes the storage placement for primitive properties.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonConverter(typeof(StringEnumConverter), true)] public enum StorageKind
public enum StorageKind {
    /**
     * The property defines a sparse column.
     * <p>
     * Columns marked <see cref="Sparse" /> consume no space in the row when not present.  When
     * present they appear in an unordered linked list at the end of the row.  Access time for
     * <see cref="Sparse" /> columns is proportional to the number of <see cref="Sparse" /> columns in the
     * row.
     */
    Sparse(0),

    /**
     * The property is a fixed-length, space-reserved column.
     * <p>
     * The column will consume 1 null-bit, and its byte-width regardless of whether the value is
     * present in the row.
     */
    Fixed(1),

    /**
     * The property is a variable-length column.
     * <p>
     * The column will consume 1 null-bit regardless of whether the value is present. When the value is
     * present it will also consume a variable number of bytes to encode the length preceding the actual
     * value.
     * <p>
     * When a <em>long</em> value is marked <see cref="Variable" /> then a null-bit is reserved and
     * the value is optionally encoded as <see cref="Variable" /> if small enough to fit, otherwise the
     * null-bit is set and the value is encoded as <see cref="Sparse" />.
     * </p>
     */
    Variable(2);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, StorageKind> mappings;
    private int intValue;

    StorageKind(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static StorageKind forValue(int value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Integer, StorageKind> getMappings() {
        if (mappings == null) {
            synchronized (StorageKind.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, StorageKind>();
                }
            }
        }
        return mappings;
    }
}