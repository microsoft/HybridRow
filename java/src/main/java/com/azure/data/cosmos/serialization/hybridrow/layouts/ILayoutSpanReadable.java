// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also read using a {@link ReadOnlySpan{T}}
 *
 * <typeparam name="TElement">The sub-element type to be written.</typeparam>
 */
public interface ILayoutSpanReadable<TElement> extends ILayoutType {
    Result ReadFixed(
        Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Out<ReadOnlySpan<TElement>> value);

    Result ReadSparse(
        Reference<RowBuffer> b, Reference<RowCursor> scope, Out<ReadOnlySpan<TElement>> value);

    Result ReadVariable(
        Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Out<ReadOnlySpan<TElement>> value);
}