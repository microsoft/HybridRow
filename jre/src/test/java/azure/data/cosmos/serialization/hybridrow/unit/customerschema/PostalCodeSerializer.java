//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.SchemaId;
import azure.data.cosmos.serialization.hybridrow.Tests.Unit.*;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public final class PostalCodeSerializer {
    public static TypeArgument TypeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(new SchemaId(1)));

    public static Result Read(tangible.RefObject<RowReader> reader, tangible.OutObject<PostalCode> obj) {
        obj.argValue = new PostalCode();
        while (reader.argValue.Read()) {
            Result r;
            switch (reader.argValue.getPath()) {
                case "zip":
                    tangible.OutObject<Integer> tempOut_Zip = new tangible.OutObject<Integer>();
                    r = reader.argValue.ReadInt32(tempOut_Zip);
                    obj.argValue.argValue.Zip = tempOut_Zip.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "plus4":
                    short value;
                    tangible.OutObject<Short> tempOut_value = new tangible.OutObject<Short>();
                    r = reader.argValue.ReadInt16(tempOut_value);
                    value = tempOut_value.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    obj.argValue.Plus4 = value;
                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg, PostalCode obj) {
        Result r;
        r = writer.argValue.WriteInt32("zip", obj.Zip);
        if (r != Result.Success) {
            return r;
        }

        if (obj.Plus4.HasValue) {
            r = writer.argValue.WriteInt16("plus4", obj.Plus4.Value);
            return r;
        }

        return Result.Success;
    }
}