//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a <see cref="LayoutType{T}" /> can also read using a
 * <see cref="Utf8Span" />.
 */
public interface ILayoutUtf8SpanReadable extends ILayoutType {
    Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                     OutObject<Utf8Span> value);

    Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> scope, OutObject<Utf8Span> value);

    Result ReadVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                        OutObject<Utf8Span> value);
}