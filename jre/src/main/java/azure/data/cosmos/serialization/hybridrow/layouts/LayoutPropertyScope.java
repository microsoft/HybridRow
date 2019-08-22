//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

public abstract class LayoutPropertyScope extends LayoutScope {
    protected LayoutPropertyScope(LayoutCode code, boolean immutable) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        //  following line:
        //  base(code, immutable, isSizedScope: false, isIndexedScope: false, isFixedArity: false, isUniqueScope: false, isTypedScope: false);
        super(code, immutable, false, false, false, false, false);
    }
}