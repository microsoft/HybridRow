// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

public final class Segment {

    private String comment;
    private int length;
    private String sdl;

    public Segment(String comment, String sdl) {
        this.comment = comment;
        this.sdl = sdl;
        this.length = 0;
    }

    public String comment() {
        return this.comment;
    }

    public Segment comment(String value) {
        this.comment = value;
        return this;
    }

    public int length() {
        return this.length;
    }

    public Segment length(int value) {
        this.length = value;
        return this;
    }
    public String sdl() {
        return this.sdl;
    }

    public Segment sdl(String value) {
        this.sdl = value;
        return this;
    }
}