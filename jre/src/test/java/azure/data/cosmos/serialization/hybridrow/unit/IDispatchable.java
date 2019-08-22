//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.RowCursor;

public interface IDispatchable {
    void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope);
}