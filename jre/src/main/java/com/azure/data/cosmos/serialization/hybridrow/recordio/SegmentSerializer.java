//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;

public final class SegmentSerializer {
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public static Result Read(Span<byte> span, LayoutResolver resolver, out Segment obj)
    public static Result Read(Span<Byte> span, LayoutResolver resolver, Out<Segment> obj) {
        RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowReader reader = new RowReader(tempReference_row);
        row = tempReference_row.get();
        Reference<RowReader> tempReference_reader =
            new Reference<RowReader>(reader);
        Result tempVar = SegmentSerializer.Read(tempReference_reader, obj.clone());
        reader = tempReference_reader.get();
        return tempVar;
    }

    public static Result Read(Reference<RowReader> reader, Out<Segment> obj) {
        obj.set(null);
        while (reader.get().Read()) {
            Result r;

            // TODO: use Path tokens here.
            switch (reader.get().getPath().toString()) {
                case "length":
                    Out<Integer> tempOut_Length = new Out<Integer>();
                    r = reader.get().ReadInt32(tempOut_Length);
                    obj.get().argValue.Length = tempOut_Length.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    // If the RowBuffer isn't big enough to contain the rest of the header, then just
                    // return the length.
                    if (reader.get().getLength() < obj.get().Length) {
                        return Result.Success;
                    }

                    break;
                case "comment":
                    Out<String> tempOut_Comment = new Out<String>();
                    r = reader.get().ReadString(tempOut_Comment);
                    obj.get().argValue.Comment = tempOut_Comment.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "sdl":
                    Out<String> tempOut_SDL = new Out<String>();
                    r = reader.get().ReadString(tempOut_SDL);
                    obj.get().argValue.SDL = tempOut_SDL.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg, Segment obj) {
        Result r;
        if (obj.Comment != null) {
            r = writer.get().WriteString("comment", obj.Comment);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.SDL != null) {
            r = writer.get().WriteString("sdl", obj.SDL);
            if (r != Result.Success) {
                return r;
            }
        }

        // Defer writing the length until all other fields of the segment header are written.
        // The length is then computed based on the current size of the underlying RowBuffer.
        // Because the length field is itself fixed, writing the length can never change the length.
        int length = writer.get().getLength();
        r = writer.get().WriteInt32("length", length);
        if (r != Result.Success) {
            return r;
        }

        checkState(length == writer.get().getLength());
        return Result.Success;
    }
}