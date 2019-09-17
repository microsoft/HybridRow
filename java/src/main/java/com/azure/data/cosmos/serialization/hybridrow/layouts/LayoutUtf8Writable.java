// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

/**
 * An optional interface that indicates a {@link LayoutType} can also write using a {@link Utf8String}
 */
public interface LayoutUtf8Writable extends ILayoutType {

    @Nonnull
    Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value);

    @Nonnull
    Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value);

    @Nonnull
    Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value, UpdateOptions options);

    @Nonnull
    Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value);
}