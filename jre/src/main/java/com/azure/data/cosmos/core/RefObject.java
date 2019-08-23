//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.core;

public final class RefObject<T> {

    private T value;

    public RefObject(T value) {
        this.set(value);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}