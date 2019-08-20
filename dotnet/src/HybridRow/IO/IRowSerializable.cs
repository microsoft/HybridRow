// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.IO
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    /// <summary>
    /// A type may implement this interface to support serialization into a HybridRow.
    /// </summary>
    public interface IRowSerializable
    {
        /// <summary>
        /// Writes the current instance into the row.
        /// </summary>
        /// <param name="writer">A writer for the current row scope.</param>
        /// <param name="typeArg">The schematized layout type, if a schema is available.</param>
        /// <returns>Success if the write is successful, the error code otherwise.</returns>
        Result Write(ref RowWriter writer, TypeArgument typeArg);
    }
}
