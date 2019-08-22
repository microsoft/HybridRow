//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.recordio;

import azure.data.cosmos.serialization.hybridrow.Result;

public final class RecordSerializer {
    public static Result Read(tangible.RefObject<RowReader> reader, tangible.OutObject<Record> obj) {
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

                    break;
                case "crc32":
                    tangible.OutObject<Integer> tempOut_Crc32 = new tangible.OutObject<Integer>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt32(out obj.Crc32);
                    r = reader.argValue.ReadUInt32(tempOut_Crc32);
                    obj.argValue.argValue.Crc32 = tempOut_Crc32.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg, Record obj) {
        Result r;
        r = writer.argValue.WriteInt32("length", obj.Length);
        if (r != Result.Success) {
            return r;
        }

        r = writer.argValue.WriteUInt32("crc32", obj.Crc32);
        return r;
    }
}