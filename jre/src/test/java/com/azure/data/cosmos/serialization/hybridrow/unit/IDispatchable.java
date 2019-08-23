//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public interface IDispatchable {
    void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope);
}