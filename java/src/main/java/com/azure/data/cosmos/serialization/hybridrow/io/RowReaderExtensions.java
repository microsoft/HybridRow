// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;

import java.util.ArrayList;

public final class RowReaderExtensions {
    /**
     * Read the current field as a nested, structured, sparse scope containing a linear collection of zero or more
     * items.
     * <typeparam name="TItem">The type of the items within the collection.</typeparam>
     *
     * @param reader       A forward-only cursor for reading the collection.
     * @param deserializer A function that reads one item from the collection.
     * @param list         On success, the collection of materialized items.
     * @return The result.
     */
    public static <TItem> Result ReadList(Reference<RowReader> reader, DeserializerFunc<TItem> deserializer,
                                          Out<ArrayList<TItem>> list) {
        // Pass the context as a struct by value to avoid allocations.
        ListContext<TItem> ctx = new ListContext<TItem>();
        ctx.List = new ArrayList<>();
        ctx.Deserializer =
            (Reference<RowReader> reader.argValue, Out<TItem> item) -> deserializer.invoke(reader.get().clone(), item);

        // All lambda's here are static.
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        Result r = reader.get().ReadScope(ctx.clone(), (ref RowReader arrayReader, ListContext<TItem> ctx1) ->
        {
            while (arrayReader.Read()) {
                Result r2 = arrayReader.ReadScope(ctx1.clone(), (ref RowReader itemReader, ListContext<TItem> ctx2) ->
                {
                    Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowReader> tempReference_itemReader = new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowReader>(itemReader);
                    TItem op;
                    Out<TItem> tempOut_op = new Out<TItem>();
                    Result r3 = ctx2.Deserializer.invoke(tempReference_itemReader, tempOut_op);
                    op = tempOut_op.get();
                    itemReader = tempReference_itemReader.get();
                    if (r3 != Result.Success) {
                        return r3;
                    }

                    ctx2.List.add(op);
                    return Result.Success;
                });

                if (r2 != Result.Success) {
                    return r2;
                }
            }

            return Result.Success;
        });

        if (r != Result.Success) {
            list.setAndGet(null);
            return r;
        }

        list.setAndGet(ctx.List);
        return Result.Success;
    }

    /**
     * A function to read content from a {@link RowReader}.
     * <typeparam name="TItem">The type of the item to read.</typeparam>
     *
     * @param reader A forward-only cursor for reading the item.
     * @param item   On success, the item read.
     * @return The result.
     */
    @FunctionalInterface
    public interface DeserializerFunc<TItem> {
        Result invoke(Reference<RowReader> reader, Out<TItem> item);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: private struct ListContext<TItem>
    private final static class ListContext<TItem> {
        public DeserializerFunc<TItem> Deserializer;
        public ArrayList<TItem> List;

        public ListContext clone() {
            ListContext varCopy = new ListContext();

            varCopy.List = this.List;
            varCopy.Deserializer = this.Deserializer;

            return varCopy;
        }
    }
}