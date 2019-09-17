// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;

/**
 * An optional interface that indicates a {@link LayoutType} can also write using a {@link List}
 *
 * @param <TElement> The sub-element type to be written
 */
public interface LayoutListWritable<TElement> extends ILayoutType {

    Result writeFixedList(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<TElement> value);

    Result writeSparseList(RowBuffer buffer, RowCursor edit, TElement value);

    Result writeSparseList(RowBuffer buffer, RowCursor edit, List<TElement> value, UpdateOptions options);

    Result writeVariableList(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<TElement> value);
}