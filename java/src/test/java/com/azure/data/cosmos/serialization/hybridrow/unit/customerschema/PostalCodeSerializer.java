// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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

    public static Result Read(Reference<RowReader> reader, Out<PostalCode> obj) {
        obj.setAndGet(new PostalCode());
        while (reader.get().read()) {
            Result r;
            switch (reader.get().path()) {
                case "zip":
                    Out<Integer> tempOut_Zip = new Out<Integer>();
                    r = reader.get().readInt32(tempOut_Zip);
                    obj.get().argValue.Zip = tempOut_Zip.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
                case "plus4":
                    short value;
                    Out<Short> tempOut_value = new Out<Short>();
                    r = reader.get().readInt16(tempOut_value);
                    value = tempOut_value.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    obj.get().Plus4 = value;
                    break;
            }
        }

        return Result.SUCCESS;
    }

    public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg, PostalCode obj) {
        Result r;
        r = writer.get().writeInt32("zip", obj.Zip);
        if (r != Result.SUCCESS) {
            return r;
        }

        if (obj.Plus4.HasValue) {
            r = writer.get().WriteInt16("plus4", obj.Plus4.Value);
            return r;
        }

        return Result.SUCCESS;
    }
}