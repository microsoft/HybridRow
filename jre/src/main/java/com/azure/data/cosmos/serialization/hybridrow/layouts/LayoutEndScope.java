//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutEndScope extends LayoutScope {
    public LayoutEndScope() {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(LayoutCode.EndScope, false, isSizedScope: false, isIndexedScope: false, isFixedArity:
        // false, isUniqueScope: false, isTypedScope: false);
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.END_SCOPE, false, isSizedScope():false, isIndexedScope():false, isFixedArity():false, isUniqueScope():
        false, isTypedScope():false)
    }

    public String name() {
        return "end";
    }


    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> scope,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return WriteScope(b, scope, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor scope, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> scope,
                             TypeArgumentList typeArgs, Out<RowCursor> value, UpdateOptions options) {
        Contract.Fail("Cannot write an EndScope directly");
        value.setAndGet(null);
        return Result.Failure;
    }
}