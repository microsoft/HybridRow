// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also write using a {@link Utf8String}
 */
public interface ILayoutUtf8SpanWritable extends ILayoutType {

    Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value);

    Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value);

    Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value, UpdateOptions options);

    Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value);
}