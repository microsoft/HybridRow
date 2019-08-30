// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public abstract class LayoutIndexedScope extends LayoutScope {

    protected LayoutIndexedScope(
        LayoutCode code, boolean immutable, boolean isSizedScope, boolean isFixedArity, boolean isUniqueScope,
        boolean isTypedScope
    ) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(code, immutable, isSizedScope, isIndexedScope: true, isFixedArity: isFixedArity,
        // isUniqueScope: isUniqueScope, isTypedScope: isTypedScope);
        super(code, immutable, isSizedScope, true, isFixedArity, isUniqueScope, isTypedScope);
    }

    @Override
    public void ReadSparsePath(Reference<RowBuffer> row, Reference<RowCursor> edit) {
        edit.get().pathToken = 0;
        edit.get().pathOffset = 0;
    }
}