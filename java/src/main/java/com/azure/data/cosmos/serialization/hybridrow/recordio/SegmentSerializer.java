// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import io.netty.buffer.ByteBuf;

import static com.google.common.base.Preconditions.checkState;

public final class SegmentSerializer {

    private static final UtfAnyString COMMENT = new UtfAnyString("comment");
    private static final UtfAnyString LENGTH = new UtfAnyString("length");
    private static final UtfAnyString SDL = new UtfAnyString("sdl");

    public static Result read(ByteBuf buffer, LayoutResolver resolver, Out<Segment> segment) {
        RowReader reader = new RowReader(new RowBuffer(buffer, HybridRowVersion.V1, resolver));
        return SegmentSerializer.read(reader, segment);
    }

    public static Result read(RowReader reader, Out<Segment> segment) {

        segment.set(new Segment(null, null));

        final Out<Utf8String> comment = new Out<>();
        final Out<Integer> length = new Out<>();
        final Out<Utf8String> sdl = new Out<>();

        while (reader.read()) {

            // TODO: Use Path tokens here.

            switch (reader.path().toString()) {

                case "length": {

                    Result result = reader.readInt32(length);
                    segment.get().length(length.get());

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    if (reader.length() < segment.get().length()) {
                        // RowBuffer isn't big enough to contain the rest of the header so just return the length
                        return Result.SUCCESS;
                    }

                    break;
                }
                case "comment": {

                    Result result = reader.readString(comment);
                    segment.get().comment(comment.get().toUtf16());

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    break;
                }
                case "sdl": {

                    Result result = reader.readString(sdl);
                    segment.get().sdl(sdl.get().toUtf16());

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    break;
                }
            }
        }

        return Result.SUCCESS;
    }

    public static Result write(RowWriter writer, TypeArgument typeArg, Segment segment) {

        Result result;

        if (segment.comment() != null) {
            result = writer.writeString(COMMENT, segment.comment());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        if (segment.sdl() != null) {
            result = writer.writeString(SDL, segment.sdl());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        // Defer writing the length until all other fields of the segment header are written.
        // The length is then computed based on the current size of the underlying RowBuffer.
        // Because the length field is itself fixed, writing the length can never change the length.

        int length = writer.length();
        result = writer.writeInt32(LENGTH, length);
        if (result != Result.SUCCESS) {
            return result;
        }

        checkState(length == writer.length());
        return Result.SUCCESS;
    }
}