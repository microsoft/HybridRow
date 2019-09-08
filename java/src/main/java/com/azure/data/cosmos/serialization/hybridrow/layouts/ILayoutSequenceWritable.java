// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also write using a read-only {@link List{T}}.
 *
 * @param <TElement> The sub-element type to be written
 */
public interface ILayoutSequenceWritable<TElement> extends ILayoutType {

    Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn col, List<TElement> value);

    Result writeSparse(RowBuffer buffer, RowCursor edit, List<TElement> value);

    Result writeSparse(RowBuffer buffer, RowCursor edit, List<TElement> value, UpdateOptions options);

    Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<TElement> value);
}