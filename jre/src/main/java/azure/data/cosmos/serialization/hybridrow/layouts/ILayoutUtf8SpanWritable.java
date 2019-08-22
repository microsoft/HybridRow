//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a <see cref="LayoutType{T}" /> can also write using a
 * <see cref="Utf8Span" />.
 */
public interface ILayoutUtf8SpanWritable extends ILayoutType {
    Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                      Utf8Span value);

    Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, Utf8Span value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: Result WriteSparse(ref RowBuffer b, ref RowCursor edit, Utf8Span value, UpdateOptions options =
    // UpdateOptions.Upsert);
    Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, Utf8Span value, UpdateOptions options);

    Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                         Utf8Span value);
}