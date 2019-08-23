//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import azure.data.cosmos.serialization.hybridrow.unit.*;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public final class PostalCodeSerializer {
    public static TypeArgument TypeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(new SchemaId(1)));

    public static Result Read(RefObject<RowReader> reader, OutObject<PostalCode> obj) {
        obj.set(new PostalCode());
        while (reader.get().Read()) {
            Result r;
            switch (reader.get().getPath()) {
                case "zip":
                    OutObject<Integer> tempOut_Zip = new OutObject<Integer>();
                    r = reader.get().ReadInt32(tempOut_Zip);
                    obj.get().argValue.Zip = tempOut_Zip.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "plus4":
                    short value;
                    OutObject<Short> tempOut_value = new OutObject<Short>();
                    r = reader.get().ReadInt16(tempOut_value);
                    value = tempOut_value.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    obj.get().Plus4 = value;
                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(RefObject<RowWriter> writer, TypeArgument typeArg, PostalCode obj) {
        Result r;
        r = writer.get().WriteInt32("zip", obj.Zip);
        if (r != Result.Success) {
            return r;
        }

        if (obj.Plus4.HasValue) {
            r = writer.get().WriteInt16("plus4", obj.Plus4.Value);
            return r;
        }

        return Result.Success;
    }
}