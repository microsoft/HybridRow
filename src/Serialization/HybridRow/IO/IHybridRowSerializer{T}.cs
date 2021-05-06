// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.IO
{
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public interface IHybridRowSerializer<T>
    {
        /// <summary>
        /// A comparer for items of type T.
        /// </summary>
        IEqualityComparer<T> Comparer { get; }

        /// <summary>Write the object to a row.</summary>
        /// <param name="row">The row to write into.</param>
        /// <param name="scope">The position in the row at which to write.</param>
        /// <param name="isRoot">
        /// True if this object is the top-most element within the row such that the row's
        /// layout is the object, or false if object is a nested UDT within the column of some other object.
        /// </param>
        /// <param name="typeArgs">Type arguments if the object is a generic collection.</param>
        /// <param name="value">The object to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, T value);

        /// <summary>Read and materialize and object from a row.</summary>
        /// <param name="row">The row to read from.</param>
        /// <param name="scope">The position in the row to read at.</param>
        /// <param name="isRoot">
        /// True if this object is the top-most element within the row such that the row's
        /// layout is the object, or false if object is a nested UDT within the column of some other object.
        /// </param>
        /// <param name="value">If successful, the new materialized object based on the read content.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out T value);
    }
}
