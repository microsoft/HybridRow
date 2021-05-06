// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    internal static class SchemaUtil
    {
        private const int InitialCapacity = 2 * 1024 * 1024;

        public static Namespace LoadFromHrSchema(string filename)
        {
            using (Stream stm = new FileStream(filename, FileMode.Open))
            {
                RowBuffer row = new RowBuffer(SchemaUtil.InitialCapacity);
                row.ReadFrom(stm, (int)stm.Length, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                Result r = Namespace.Read(ref row, out Namespace ns);
                ResultAssert.IsSuccess(r);
                return ns;
            }
        }
    }
}
