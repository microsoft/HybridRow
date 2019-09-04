// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.unit.*;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;

public final class AddressSerializer {
    public static Result Read(Reference<RowReader> reader, Out<Address> obj) {
        obj.setAndGet(new Address());
        while (reader.get().Read()) {
            Result r;
            switch (reader.get().getPath()) {
                case "street":
                    Out<String> tempOut_Street = new Out<String>();
                    r = reader.get().ReadString(tempOut_Street);
                    obj.get().argValue.Street = tempOut_Street.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
                case "city":
                    Out<String> tempOut_City = new Out<String>();
                    r = reader.get().ReadString(tempOut_City);
                    obj.get().argValue.City = tempOut_City.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
                case "state":
                    Out<String> tempOut_State = new Out<String>();
                    r = reader.get().ReadString(tempOut_State);
                    obj.get().argValue.State = tempOut_State.get();
                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
                case "postal_code":
                    Reference<RowReader> tempReference_child =
                        new Reference<RowReader>(child);
                    Out<PostalCode> tempOut_PostalCode = new Out<PostalCode>();
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                    r = reader.get().ReadScope(obj.get(), (ref RowReader child, Address parent) -> PostalCodeSerializer.Read(tempReference_child, tempOut_PostalCode));
                    parent.PostalCode = tempOut_PostalCode.get();
                    child = tempReference_child.get();

                    if (r != Result.SUCCESS) {
                        return r;
                    }

                    break;
            }
        }

        return Result.SUCCESS;
    }

    public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg, Address obj) {
        Result r;
        if (obj.Street != null) {
            r = writer.get().WriteString("street", obj.Street);
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (obj.City != null) {
            r = writer.get().WriteString("city", obj.City);
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (obj.State != null) {
            r = writer.get().WriteString("state", obj.State);
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (obj.PostalCode != null) {
            r = writer.get().WriteScope("postal_code", PostalCodeSerializer.TypeArg, obj.PostalCode,
                PostalCodeSerializer.Write);
            return r;
        }

        return Result.SUCCESS;
    }
}