// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public static class RecordSerializer
    {
        public static Result Write(ref RowWriter writer, TypeArgument typeArg, Record obj)
        {
            Result r;
            r = writer.WriteInt32("length", obj.Length);
            if (r != Result.Success)
            {
                return r;
            }

            r = writer.WriteUInt32("crc32", obj.Crc32);
            if (r != Result.Success)
            {
                return r;
            }

            return Result.Success;
        }

        public static Result Read(ref RowReader reader, out Record obj)
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

                        break;
                    case "crc32":
                        r = reader.ReadUInt32(out obj.Crc32);
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
