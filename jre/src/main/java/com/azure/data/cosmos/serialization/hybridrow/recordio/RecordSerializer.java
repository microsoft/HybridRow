//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;

public final class RecordSerializer {
    public static Result Read(Reference<RowReader> reader, Out<Record> obj) {
        obj.setAndGet(null);
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

                    break;
                case "crc32":
                    Out<Integer> tempOut_Crc32 = new Out<Integer>();
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: r = reader.ReadUInt32(out obj.Crc32);
                    r = reader.get().ReadUInt32(tempOut_Crc32);
                    obj.get().argValue.Crc32 = tempOut_Crc32.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg, Record obj) {
        Result r;
        r = writer.get().WriteInt32("length", obj.Length);
        if (r != Result.Success) {
            return r;
        }

        r = writer.get().WriteUInt32("crc32", obj.Crc32);
        return r;
    }
}