//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

public abstract class LayoutIndexedScope extends LayoutScope {
    protected LayoutIndexedScope(LayoutCode code, boolean immutable, boolean isSizedScope, boolean isFixedArity,
                                 boolean isUniqueScope, boolean isTypedScope) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(code, immutable, isSizedScope, isIndexedScope: true, isFixedArity: isFixedArity,
        // isUniqueScope: isUniqueScope, isTypedScope: isTypedScope);
        super(code, immutable, isSizedScope, isIndexedScope:true, isFixedArity:isFixedArity, isUniqueScope:
        isUniqueScope, isTypedScope:isTypedScope)
    }

    @Override
    public void ReadSparsePath(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> edit) {
        edit.argValue.pathToken = 0;
        edit.argValue.pathOffset = 0;
    }
}