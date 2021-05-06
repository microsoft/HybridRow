// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.IO
{
    using System.Collections.Generic;

    public static class RowReaderExtensions
    {
        /// <summary>A function to read content from a <see cref="RowReader" />.</summary>
        /// <typeparam name="TItem">The type of the item to read.</typeparam>
        /// <param name="reader">A forward-only cursor for reading the item.</param>
        /// <param name="item">On success, the item read.</param>
        /// <returns>The result.</returns>
        public delegate Result DeserializerFunc<TItem>(ref RowReader reader, out TItem item);

        /// <summary>Read the current field as a nested, structured, sparse scope containing a linear collection of zero or more items.</summary>
        /// <typeparam name="TItem">The type of the items within the collection.</typeparam>
        /// <param name="reader">A forward-only cursor for reading the collection.</param>
        /// <param name="deserializer">A function that reads one item from the collection.</param>
        /// <param name="list">On success, the collection of materialized items.</param>
        /// <returns>The result.</returns>
        public static Result ReadList<TItem>(this ref RowReader reader, DeserializerFunc<TItem> deserializer, out List<TItem> list)
        {
            // Pass the context as a struct by value to avoid allocations.
            ListContext<TItem> ctx = new ListContext<TItem>
            {
                List = new List<TItem>(),
                Deserializer = deserializer,
            };

            // All lambda's here are static.
            Result r = reader.ReadScope(
                ctx,
                (ref RowReader arrayReader, ListContext<TItem> ctx1) =>
                {
                    while (arrayReader.Read())
                    {
                        Result r2 = arrayReader.ReadScope(
                            ctx1,
                            (ref RowReader itemReader, ListContext<TItem> ctx2) =>
                            {
                                Result r3 = ctx2.Deserializer(ref itemReader, out TItem op);
                                if (r3 != Result.Success)
                                {
                                    return r3;
                                }

                                ctx2.List.Add(op);
                                return Result.Success;
                            });

                        if (r2 != Result.Success)
                        {
                            return r2;
                        }
                    }

                    return Result.Success;
                });

            if (r != Result.Success)
            {
                list = default;
                return r;
            }

            list = ctx.List;
            return Result.Success;
        }

        private struct ListContext<TItem>
        {
            public List<TItem> List;
            public DeserializerFunc<TItem> Deserializer;
        }
    }
}
