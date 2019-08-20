// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    internal static class AddressSerializer
    {
        public static Result Write(ref RowWriter writer, TypeArgument typeArg, Address obj)
        {
            Result r;
            if (obj.Street != null)
            {
                r = writer.WriteString("street", obj.Street);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (obj.City != null)
            {
                r = writer.WriteString("city", obj.City);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (obj.State != null)
            {
                r = writer.WriteString("state", obj.State);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (obj.PostalCode != null)
            {
                r = writer.WriteScope("postal_code", PostalCodeSerializer.TypeArg, obj.PostalCode, PostalCodeSerializer.Write);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        public static Result Read(ref RowReader reader, out Address obj)
        {
            obj = new Address();
            while (reader.Read())
            {
                Result r;
                switch (reader.Path)
                {
                    case "street":
                        r = reader.ReadString(out obj.Street);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        break;
                    case "city":
                        r = reader.ReadString(out obj.City);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        break;
                    case "state":
                        r = reader.ReadString(out obj.State);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        break;
                    case "postal_code":
                        r = reader.ReadScope(
                            obj,
                            (ref RowReader child, Address parent) =>
                                PostalCodeSerializer.Read(ref child, out parent.PostalCode));

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
