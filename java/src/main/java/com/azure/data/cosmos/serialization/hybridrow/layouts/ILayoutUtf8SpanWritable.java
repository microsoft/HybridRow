// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also write using a {@link Utf8Span}.
 */
public interface ILayoutUtf8SpanWritable extends ILayoutType {

    Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Utf8Span value);

    Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, Utf8Span value);

    // C# TO JAVA CONVERTER NOTE:
    // Java does not support optional parameters, hence overloaded method(s) are created.
    // ORIGINAL LINE:
    // Result WriteSparse(ref RowBuffer b, ref RowCursor edit, Utf8Span value, UpdateOptions options = UpdateOptions.Upsert);

    Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, Utf8Span value, UpdateOptions options);

    Result WriteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Utf8Span value);
}