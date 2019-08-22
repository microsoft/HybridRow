//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a <see cref="LayoutType{T}" /> can also read using a
 * <see cref="Utf8Span" />.
 */
public interface ILayoutUtf8SpanReadable extends ILayoutType {
    Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                     tangible.OutObject<Utf8Span> value);

    Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, tangible.OutObject<Utf8Span> value);

    Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                        tangible.OutObject<Utf8Span> value);
}