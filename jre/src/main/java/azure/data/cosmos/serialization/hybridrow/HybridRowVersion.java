//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1028 // Enum Storage should be Int32

/**
 * Versions of HybridRow.
 * A version from this list MUST be inserted in the version BOM at the beginning of all rows.
 */
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public enum HybridRowVersion : byte
public enum HybridRowVersion {
    Invalid((byte)0),

    /**
     * Initial version of the HybridRow format.
     */
    V1((byte)0x81);

    public static final int SIZE = java.lang.Byte.SIZE;
    private static java.util.HashMap<Byte, HybridRowVersion> mappings;
    private byte byteValue;

    HybridRowVersion(byte value) {
        byteValue = value;
        getMappings().put(value, this);
    }

    public byte getValue() {
        return byteValue;
    }

    public static HybridRowVersion forValue(byte value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Byte, HybridRowVersion> getMappings() {
        if (mappings == null) {
            synchronized (HybridRowVersion.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Byte, HybridRowVersion>();
                }
            }
        }
        return mappings;
    }
}