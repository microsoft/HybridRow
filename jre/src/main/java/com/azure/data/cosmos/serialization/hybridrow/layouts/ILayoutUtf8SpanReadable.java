//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a {@link LayoutType{T}} can also read using a {@link Utf8Span}.
 */
public interface ILayoutUtf8SpanReadable extends ILayoutType {

    Result ReadFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Out<Utf8Span> value);

    Result ReadSparse(Reference<RowBuffer> b, Reference<RowCursor> scope, Out<Utf8Span> value);

    Result ReadVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Out<Utf8Span> value);
}