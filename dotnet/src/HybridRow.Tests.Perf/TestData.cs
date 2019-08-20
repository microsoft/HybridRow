// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------
namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    /// <summary>
    /// Names of assets in the TestData folder.
    /// </summary>
    internal class TestData
    {
        /// <summary>
        /// The folder to which TestData assets should be copied during deployment.
        /// </summary>
        public const string Target = "TestData";

        public const string SchemaFile = @"TestData\CassandraHotelSchema.json";
        public const string HotelExpected = @"TestData\HotelSchemaExpected.hr";
        public const string RoomsExpected = @"TestData\RoomsSchemaExpected.hr";
        public const string GuestsExpected = @"TestData\GuestsSchemaExpected.hr";
        public const string Messages1KExpected = @"TestData\Messages1KExpected.hr";
    }
}
