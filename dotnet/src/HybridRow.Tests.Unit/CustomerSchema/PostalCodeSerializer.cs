// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    internal static class PostalCodeSerializer
    {
        public static TypeArgument TypeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(new SchemaId(1)));

        public static Result Write(ref RowWriter writer, TypeArgument typeArg, PostalCode obj)
        {
            Result r;
            r = writer.WriteInt32("zip", obj.Zip);
            if (r != Result.Success)
            {
                return r;
            }

            if (obj.Plus4.HasValue)
            {
                r = writer.WriteInt16("plus4", obj.Plus4.Value);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        public static Result Read(ref RowReader reader, out PostalCode obj)
        {
            obj = new PostalCode();
            while (reader.Read())
            {
                Result r;
                switch (reader.Path)
                {
                    case "zip":
                        r = reader.ReadInt32(out obj.Zip);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        break;
                    case "plus4":
                        r = reader.ReadInt16(out short value);
                        if (r != Result.Success)
                        {
                            return r;
                        }

                        obj.Plus4 = value;
                        break;
                }
            }

            return Result.Success;
        }
    }
}
