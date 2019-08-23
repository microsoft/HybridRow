//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.unit.*;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;

public final class AddressSerializer {
    public static Result Read(RefObject<RowReader> reader, OutObject<Address> obj) {
        obj.set(new Address());
        while (reader.get().Read()) {
            Result r;
            switch (reader.get().getPath()) {
                case "street":
                    OutObject<String> tempOut_Street = new OutObject<String>();
                    r = reader.get().ReadString(tempOut_Street);
                    obj.get().argValue.Street = tempOut_Street.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "city":
                    OutObject<String> tempOut_City = new OutObject<String>();
                    r = reader.get().ReadString(tempOut_City);
                    obj.get().argValue.City = tempOut_City.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "state":
                    OutObject<String> tempOut_State = new OutObject<String>();
                    r = reader.get().ReadString(tempOut_State);
                    obj.get().argValue.State = tempOut_State.get();
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "postal_code":
                    RefObject<RowReader> tempRef_child =
                        new RefObject<RowReader>(child);
                    OutObject<PostalCode> tempOut_PostalCode = new OutObject<PostalCode>();
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                    r = reader.get().ReadScope(obj.get(), (ref RowReader child, Address parent) -> PostalCodeSerializer.Read(tempRef_child, tempOut_PostalCode));
                    parent.PostalCode = tempOut_PostalCode.get();
                    child = tempRef_child.get();

                    if (r != Result.Success) {
                        return r;
                    }

                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(RefObject<RowWriter> writer, TypeArgument typeArg, Address obj) {
        Result r;
        if (obj.Street != null) {
            r = writer.get().WriteString("street", obj.Street);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.City != null) {
            r = writer.get().WriteString("city", obj.City);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.State != null) {
            r = writer.get().WriteString("state", obj.State);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.PostalCode != null) {
            r = writer.get().WriteScope("postal_code", PostalCodeSerializer.TypeArg, obj.PostalCode,
                PostalCodeSerializer.Write);
            return r;
        }

        return Result.Success;
    }
}