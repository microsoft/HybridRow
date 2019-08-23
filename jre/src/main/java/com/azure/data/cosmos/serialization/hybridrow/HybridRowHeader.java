//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * Describes the header the precedes all valid Hybrid Rows.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [StructLayout(LayoutKind.Sequential, Pack = 1)] public readonly struct HybridRowHeader
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [StructLayout(LayoutKind.Sequential, Pack = 1)] public readonly struct HybridRowHeader
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class HybridRowHeader {
    /**
     * Size (in bytes) of a serialized header.
     */
    public static final int Size = (HybridRowVersion.SIZE / Byte.SIZE) + com.azure.data.cosmos.serialization.hybridrow.SchemaId.Size;
    /**
     * The unique identifier of the schema whose layout was used to write this row.
     */
    private SchemaId SchemaId = new SchemaId();
    /**
     * The version of the HybridRow library used to write this row.
     */
    private HybridRowVersion Version = HybridRowVersion.values()[0];

    /**
     * Initializes a new instance of the <see cref="HybridRowHeader"/> struct.
     *
     * @param version  The version of the HybridRow library used to write this row.
     * @param schemaId The unique identifier of the schema whose layout was used to write this row.
     */
    public HybridRowHeader() {
    }

    public HybridRowHeader(HybridRowVersion version, SchemaId schemaId) {
        this.Version = version;
        this.SchemaId = schemaId.clone();
    }

    public SchemaId getSchemaId() {
        return SchemaId;
    }

    public HybridRowVersion getVersion() {
        return Version;
    }
}