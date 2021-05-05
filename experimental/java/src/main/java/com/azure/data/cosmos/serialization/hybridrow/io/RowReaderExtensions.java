// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class RowReaderExtensions {
    /**
     * Read the current field as a nested, structured, sparse scope containing a linear collection of zero or more
     * items.
     * @param <TItem> The type of the items within the collection.
     *
     * @param reader       A forward-only cursor for reading the collection.
     * @param deserializer A function that reads one item from the collection.
     * @param list         On success, the collection of materialized items.
     * @return The result.
     */
    @Nonnull
    public static <TItem> Result readList(RowReader reader, DeserializerFunc<TItem> deserializer, Out<List<TItem>> list) {

        // Pass the context as a struct by value to avoid allocations

        final ListContext<TItem> context = new ListContext<TItem>(deserializer, new ArrayList<TItem>());
        final Out<TItem> item = new Out<>();

        Result result = reader.readScope(context, (arrayReader, arrayContext) -> {
            while (arrayReader.read()) {
                Result arrayResult = arrayReader.readScope(arrayContext, (itemReader, itemContext) -> {
                    Result itemResult = itemContext.deserializer().invoke(itemReader, item);
                    if (itemResult != Result.SUCCESS) {
                        return itemResult;
                    }
                    itemContext.items().add(item.get());
                    return Result.SUCCESS;
                });
                if (arrayResult != Result.SUCCESS) {
                    return arrayResult;
                }
            }
            return Result.SUCCESS;
        });

        if (result != Result.SUCCESS) {
            list.set(null);
            return result;
        }

        list.set(context.items());
        return Result.SUCCESS;
    }

    /**
     * A functional interface to read content from a {@link RowReader}
     *
     * @param <TItem> The type of item to read
     *
     */
    @FunctionalInterface
    public interface DeserializerFunc<TItem> {
        /**
         * Read a row from a {@link RowReader}
         *
         * @param reader A forward-only cursor for reading the item
         * @param item   On success, the item read
         * @return The result
         */
        @Nonnull
        Result invoke(@Nonnull RowReader reader, @Nonnull Out<TItem> item);
    }

    private final static class ListContext<TItem> {

        private final DeserializerFunc<TItem> deserializer;
        private final List<TItem> items;

        ListContext(DeserializerFunc<TItem> deserializer, List<TItem> items) {
            this.deserializer = deserializer;
            this.items = items;
        }

        public DeserializerFunc<TItem> deserializer() {
            return this.deserializer;
        }

        public List<TItem> items() {
            return this.items;
        }
    }
}