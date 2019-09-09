// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

public final class Segment {

    private final String comment;
    private final int length;
    private final String sdl;

    public Segment(String comment, String sdl) {
        this.comment = comment;
        this.sdl = sdl;
        this.length = 0;
    }

    public String comment() {
        return this.comment;
    }

    public int length() {
        return this.length;
    }

    public String sdl() {
        return this.sdl;
    }
}