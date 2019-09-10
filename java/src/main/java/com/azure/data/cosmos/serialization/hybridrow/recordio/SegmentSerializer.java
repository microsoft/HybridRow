// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import io.netty.buffer.ByteBuf;

public final class SegmentSerializer {

    public static Result read(ByteBuf buffer, LayoutResolver resolver, Out<Segment> segment) {
        RowBuffer row = new RowBuffer(buffer, HybridRowVersion.V1, resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowReader reader = new RowReader(tempReference_row);
        row = tempReference_row.get();
        Reference<RowReader> tempReference_reader =
            new Reference<RowReader>(reader);
        Result tempVar = SegmentSerializer.read(tempReference_reader, segment.clone());
        reader = tempReference_reader.get();
        return tempVar;
    }

    public static Result read(RowReader reader, Out<Segment> segment) {
        segment.setAndGet(null);
        while (reader.read()) {
            Result r;

            // TODO: use Path tokens here.
            switch (reader.path().toString()) {
                case "length":
                    Out<Integer> tempOut_Length = new Out<Integer>();
                    r = reader.readInt32(tempOut_Length);
                    segment.get().argValue.Length = tempOut_Length.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    // If the RowBuffer isn't big enough to contain the rest of the header, then just
                    // return the length.
                    if (reader.length() < segment.get().length()) {
                        return Result.SUCCESS;
                    }

                    break;
                case "comment":
                    Out<String> tempOut_Comment = new Out<String>();
                    r = reader.readString(tempOut_Comment);
                    segment.get().argValue.Comment = tempOut_Comment.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
                case "sdl":
                    Out<String> tempOut_SDL = new Out<String>();
                    r = reader.readString(tempOut_SDL);
                    segment.get().argValue.SDL = tempOut_SDL.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
            }
        }

        return Result.SUCCESS;
    }

    public static Result write(RowWriter writer, TypeArgument typeArg, Segment segment) {
        Result r;
        if (segment.comment() != null) {
            r = writer.WriteString("comment", segment.comment());
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (segment.sdl() != null) {
            r = writer.WriteString("sdl", segment.sdl());
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        // Defer writing the length until all other fields of the segment header are written.
        // The length is then computed based on the current size of the underlying RowBuffer.
        // Because the length field is itself fixed, writing the length can never change the length.
        int length = writer.getLength();
        r = writer.WriteInt32("length", length);
        if (r != Result.SUCCESS) {
            return r;
        }

        checkState(length == writer.getLength());
        return Result.SUCCESS;
    }
}