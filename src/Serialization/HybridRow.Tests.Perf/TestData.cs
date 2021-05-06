// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------
namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    /// <summary>
    /// Names of assets in the TestData folder.
    /// </summary>
    internal class TestData
    {
        /// <summary>
        /// The folder to which TestData assets should be copied during deployment.
        /// </summary>
        public const string Target = "TestData";

        public const string SchemaFile = @"TestData\CassandraHotelSchema.hrschema";
        public const string HotelExpected = @"TestData\HotelSchemaExpected.hr";
        public const string RoomsExpected = @"TestData\RoomsSchemaExpected.hr";
        public const string GuestsExpected = @"TestData\GuestsSchemaExpected.hr";
        public const string Messages1KExpected = @"TestData\Messages1KExpected.hr";

        private const int InitialCapacity = 2 * 1024 * 1024;

        public static Namespace LoadFromHrSchema(string filename)
        {
            using (Stream stm = new FileStream(filename, FileMode.Open))
            {
                RowBuffer row = new RowBuffer(InitialCapacity);
                row.ReadFrom(stm, (int)stm.Length, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                Result r = Namespace.Read(ref row, out Namespace ns);
                Assert.AreEqual(Result.Success, r);
                return ns;
            }
        }
    }
}
