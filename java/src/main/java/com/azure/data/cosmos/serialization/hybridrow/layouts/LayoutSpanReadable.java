// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also read using a read-only {@link List{T}}
 *
 * @param <TElement> The sub-element type to be written
 */
public interface LayoutSpanReadable<TElement> extends ILayoutType {

    Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<List<TElement>> value);

    Result readSparse(RowBuffer buffer, RowCursor scope, Out<List<TElement>> value);

    Result readVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<List<TElement>> value);
}