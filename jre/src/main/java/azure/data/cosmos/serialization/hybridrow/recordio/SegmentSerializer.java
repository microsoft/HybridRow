//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.recordio;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;

public final class SegmentSerializer {
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public static Result Read(Span<byte> span, LayoutResolver resolver, out Segment obj)
    public static Result Read(Span<Byte> span, LayoutResolver resolver, tangible.OutObject<Segment> obj) {
        RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowReader reader = new RowReader(tempRef_row);
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
        Result tempVar = SegmentSerializer.Read(tempRef_reader, obj.clone());
        reader = tempRef_reader.argValue;
        return tempVar;
    }

    public static Result Read(tangible.RefObject<RowReader> reader, tangible.OutObject<Segment> obj) {
        obj.argValue = null;
        while (reader.argValue.Read()) {
            Result r;

            // TODO: use Path tokens here.
            switch (reader.argValue.getPath().toString()) {
                case "length":
                    tangible.OutObject<Integer> tempOut_Length = new tangible.OutObject<Integer>();
                    r = reader.argValue.ReadInt32(tempOut_Length);
                    obj.argValue.argValue.Length = tempOut_Length.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    // If the RowBuffer isn't big enough to contain the rest of the header, then just
                    // return the length.
                    if (reader.argValue.getLength() < obj.argValue.Length) {
                        return Result.Success;
                    }

                    break;
                case "comment":
                    tangible.OutObject<String> tempOut_Comment = new tangible.OutObject<String>();
                    r = reader.argValue.ReadString(tempOut_Comment);
                    obj.argValue.argValue.Comment = tempOut_Comment.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "sdl":
                    tangible.OutObject<String> tempOut_SDL = new tangible.OutObject<String>();
                    r = reader.argValue.ReadString(tempOut_SDL);
                    obj.argValue.argValue.SDL = tempOut_SDL.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg, Segment obj) {
        Result r;
        if (obj.Comment != null) {
            r = writer.argValue.WriteString("comment", obj.Comment);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.SDL != null) {
            r = writer.argValue.WriteString("sdl", obj.SDL);
            if (r != Result.Success) {
                return r;
            }
        }

        // Defer writing the length until all other fields of the segment header are written.
        // The length is then computed based on the current size of the underlying RowBuffer.
        // Because the length field is itself fixed, writing the length can never change the length.
        int length = writer.argValue.getLength();
        r = writer.argValue.WriteInt32("length", length);
        if (r != Result.Success) {
            return r;
        }

        Contract.Assert(length == writer.argValue.getLength());
        return Result.Success;
    }
}