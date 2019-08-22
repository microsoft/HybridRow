//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.SchemaId;

public abstract class LayoutResolver {
    public abstract Layout Resolve(SchemaId schemaId);
}