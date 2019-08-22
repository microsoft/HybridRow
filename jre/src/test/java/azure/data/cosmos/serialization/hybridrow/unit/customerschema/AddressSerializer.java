//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.Tests.Unit.*;

public final class AddressSerializer {
    public static Result Read(tangible.RefObject<RowReader> reader, tangible.OutObject<Address> obj) {
        obj.argValue = new Address();
        while (reader.argValue.Read()) {
            Result r;
            switch (reader.argValue.getPath()) {
                case "street":
                    tangible.OutObject<String> tempOut_Street = new tangible.OutObject<String>();
                    r = reader.argValue.ReadString(tempOut_Street);
                    obj.argValue.argValue.Street = tempOut_Street.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "city":
                    tangible.OutObject<String> tempOut_City = new tangible.OutObject<String>();
                    r = reader.argValue.ReadString(tempOut_City);
                    obj.argValue.argValue.City = tempOut_City.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "state":
                    tangible.OutObject<String> tempOut_State = new tangible.OutObject<String>();
                    r = reader.argValue.ReadString(tempOut_State);
                    obj.argValue.argValue.State = tempOut_State.argValue;
                    if (r != Result.Success) {
                        return r;
                    }

                    break;
                case "postal_code":
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_child =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(child);
                    tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema.PostalCode> tempOut_PostalCode = new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema.PostalCode>();
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                    r = reader.argValue.ReadScope(obj.argValue, (ref RowReader child, Address parent) -> PostalCodeSerializer.Read(tempRef_child, tempOut_PostalCode));
                    parent.PostalCode = tempOut_PostalCode.argValue;
                    child = tempRef_child.argValue;

                    if (r != Result.Success) {
                        return r;
                    }

                    break;
            }
        }

        return Result.Success;
    }

    public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg, Address obj) {
        Result r;
        if (obj.Street != null) {
            r = writer.argValue.WriteString("street", obj.Street);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.City != null) {
            r = writer.argValue.WriteString("city", obj.City);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.State != null) {
            r = writer.argValue.WriteString("state", obj.State);
            if (r != Result.Success) {
                return r;
            }
        }

        if (obj.PostalCode != null) {
            r = writer.argValue.WriteScope("postal_code", PostalCodeSerializer.TypeArg, obj.PostalCode,
                PostalCodeSerializer.Write);
            return r;
        }

        return Result.Success;
    }
}