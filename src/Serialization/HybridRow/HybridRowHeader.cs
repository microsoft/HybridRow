// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System.Runtime.InteropServices;

    /// <summary>Describes the header the precedes all valid Hybrid Rows.</summary>
    [StructLayout(LayoutKind.Sequential, Pack = 1)]
    public readonly struct HybridRowHeader
    {
        /// <summary>Size (in bytes) of a serialized header.</summary>
        public const int Size = sizeof(HybridRowVersion) + SchemaId.Size;

        /// <summary>
        /// Initializes a new instance of the <see cref="HybridRowHeader"/> struct.
        /// </summary>
        /// <param name="version">The version of the HybridRow library used to write this row.</param>
        /// <param name="schemaId">The unique identifier of the schema whose layout was used to write this row.</param>
        public HybridRowHeader(HybridRowVersion version, SchemaId schemaId)
        {
            this.Version = version;
            this.SchemaId = schemaId;
        }

        /// <summary>The version of the HybridRow library used to write this row.</summary>
        public HybridRowVersion Version { get; }

        /// <summary>The unique identifier of the schema whose layout was used to write this row.</summary>
        public SchemaId SchemaId { get; }
    }
}
