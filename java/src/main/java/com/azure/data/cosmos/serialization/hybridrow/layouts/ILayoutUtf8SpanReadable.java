// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also read using a {@link Utf8String}.
 */
public interface ILayoutUtf8SpanReadable extends ILayoutType {

    Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<Utf8String> value);

    Result readSparse(RowBuffer buffer, RowCursor scope, Out<Utf8String> value);

    Result ReadVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<Utf8String> value);
}