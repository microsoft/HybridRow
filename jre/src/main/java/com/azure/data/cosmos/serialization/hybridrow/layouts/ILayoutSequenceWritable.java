//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

/**
 * An optional interface that indicates a <see cref="LayoutType{T}" /> can also write using a
 * <see cref="ReadOnlySequence{T}" />.
 *
 * <typeparam name="TElement">The sub-element type to be written.</typeparam>
 */
public interface ILayoutSequenceWritable<TElement> extends ILayoutType {
    Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                      ReadOnlySequence<TElement> value);

    Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                       ReadOnlySequence<TElement> value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySequence<TElement> value,
    // UpdateOptions options = UpdateOptions.Upsert);
    Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                       ReadOnlySequence<TElement> value, UpdateOptions options);

    Result WriteVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                         ReadOnlySequence<TElement> value);
}