// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1051 // Do not declare visible instance fields


//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public struct Segment
public final class Segment {
    public String Comment;
    public int Length;
    public String SDL;

    public Segment() {
    }

    public Segment(String comment, String sdl) {
        this.Length = 0;
        this.Comment = comment;
        this.SDL = sdl;
    }

    public Segment clone() {
        Segment varCopy = new Segment();

        varCopy.Length = this.Length;
        varCopy.Comment = this.Comment;
        varCopy.SDL = this.SDL;

        return varCopy;
    }
}