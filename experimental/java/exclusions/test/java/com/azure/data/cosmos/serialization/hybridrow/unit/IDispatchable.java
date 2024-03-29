// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public interface IDispatchable {
    void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope);
}