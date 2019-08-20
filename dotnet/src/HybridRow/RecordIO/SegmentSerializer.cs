// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public static class SegmentSerializer
    {
        public static Result Write(ref RowWriter writer, TypeArgument typeArg, Segment obj)
        {
            Result r;
            if (obj.Comment != null)
            {
                r = writer.WriteString("comment", obj.Comment);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (obj.SDL != null)
            {
                r = writer.WriteString("sdl", obj.SDL);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            // Defer writing the length until all other fields of the segment header are written.
            // The length is then computed based on the current size of the underlying RowBuffer.
            // Because the length field is itself fixed, writing the length can never change the length.
            int length = writer.Length;
            r = writer.WriteInt32("length", length);
            if (r != Result.Success)
            {
                return r;
            }

            Contract.Assert(length == writer.Length);
            return Result.Success;
        }

        public static Result Read(Span<byte> span, LayoutResolver resolver, out Segment obj)
        {
            RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, resolver);
            RowReader reader = new RowReader(ref row);
            return SegmentSerializer.Read(ref reader, out obj);
        }

        public static Result Read(ref RowReader reader, out Segment obj)
        {
            obj = default;
            while (reader.Read())
            {
                Result r;

                // TODO: use Path tokens here.
                switch (reader.Path.ToString())
                {
                    case "length":
                        r = reader.ReadInt32(out obj.Length);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        // If the RowBuffer isn't big enough to contain the rest of the header, then just
                        // return the length.
                        if (reader.Length < obj.Length)
                        {
                            return Result.Success;
                        }

                        break;
                    case "comment":
                        r = reader.ReadString(out obj.Comment);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        break;
                    case "sdl":
                        r = reader.ReadString(out obj.SDL);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        break;
                }
            }

            return Result.Success;
        }
    }
}
