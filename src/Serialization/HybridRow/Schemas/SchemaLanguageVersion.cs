// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1028 // Enum Storage should be Int32

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>Versions of the HybridRow Schema Description Language.</summary>
    [JsonConverter(typeof(StringEnumConverter), true)] // camelCase:true
    public enum SchemaLanguageVersion : byte
    {
        /// <summary>Initial version of the HybridRow Schema Description Language.</summary>
        V1 = 0,

        /// <summary>Introduced Enums, Inheritance.</summary>
        V2 = 2,

        /// <summary>The latest version.</summary>
        Latest = SchemaLanguageVersion.V2,

        /// <summary>No version is specified.</summary>
        /// <remarks>
        /// When applied to a <see cref="Namespace"/>, unspecified will map to <see cref="Latest"/>.
        /// When applied to a <see cref="Schema"/>, unspecified will map to the version given in the namespace.
        /// </remarks>
        Unspecified = 255,
    }
}
