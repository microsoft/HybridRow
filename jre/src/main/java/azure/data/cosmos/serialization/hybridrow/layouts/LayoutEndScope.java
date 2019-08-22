//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutEndScope extends LayoutScope {
    public LayoutEndScope() {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(LayoutCode.EndScope, false, isSizedScope: false, isIndexedScope: false, isFixedArity:
        // false, isUniqueScope: false, isTypedScope: false);
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.EndScope, false, isSizedScope:false, isIndexedScope:false, isFixedArity:false, isUniqueScope:
        false, isTypedScope:false)
    }

    @Override
    public String getName() {
        return "end";
    }


    @Override
    public Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                             TypeArgumentList typeArgs, tangible.OutObject<RowCursor> value) {
        return WriteScope(b, scope, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor scope, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                             TypeArgumentList typeArgs, tangible.OutObject<RowCursor> value, UpdateOptions options) {
        Contract.Fail("Cannot write an EndScope directly");
        value.argValue = null;
        return Result.Failure;
    }
}