// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

public abstract class LayoutPropertyScope extends LayoutTypeScope {
    protected LayoutPropertyScope(LayoutCode code, boolean immutable) {
        super(code, immutable, false, false, false, false, false);
    }
}