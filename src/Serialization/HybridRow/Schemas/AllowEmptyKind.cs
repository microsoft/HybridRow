// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>Describes the empty canonicalization for properties.</summary>
    [JsonConverter(typeof(StringEnumConverter), true)] // camelCase:true
    [Flags]
    [SuppressMessage("Naming", "CA1714:Flags enums should have plural names", Justification = "Consistency")]
    public enum AllowEmptyKind : byte
    {
        /// <summary>Empty and null are treated as distinct.</summary>
        None = 0,

        /// <summary>Empty values are converted to null when written.</summary>
        EmptyAsNull = 1,

        /// <summary>Null values are converted to empty when read.</summary>
        NullAsEmpty = 2,

        /// <summary>
        /// Empty values are converted to null when written, and null values are converted to empty
        /// when read.
        /// </summary>
        Both = AllowEmptyKind.EmptyAsNull | AllowEmptyKind.NullAsEmpty,
    }
}
