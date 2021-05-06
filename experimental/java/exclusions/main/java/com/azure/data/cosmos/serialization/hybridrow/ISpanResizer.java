// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

public interface ISpanResizer<T> {
    /**
     * Resizes an existing a buffer.
     * <typeparam name="T">The type of the elements of the memory.</typeparam>
     *
     * @param minimumLength The minimum required length (in elements) of the memory.
     * @param buffer        Optional existing memory to be copied to the new buffer.  Ownership of <paramref
     *                      name="buffer" /> is
     *                      transferred as part of this call and it should not be used by the caller after this call
     *                      completes.
     * @return A new memory whose size is <em>at least as big</em> as <paramref name="minimumLength" />
     * and containing the content of <paramref name="buffer" />.
     */

    Span<T> Resize(int minimumLength);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: Span<T> Resize(int minimumLength, Span<T> buffer = default);
    Span<T> Resize(int minimumLength, Span<T> buffer);
}