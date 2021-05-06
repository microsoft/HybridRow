// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class UpdateOptionsUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void UpdateOptionsTest()
        {
            Assert.AreEqual((int)RowOptions.None, (int)UpdateOptions.None);
            Assert.AreEqual((int)RowOptions.Update, (int)UpdateOptions.Update);
            Assert.AreEqual((int)RowOptions.Insert, (int)UpdateOptions.Insert);
            Assert.AreEqual((int)RowOptions.Upsert, (int)UpdateOptions.Upsert);
            Assert.AreEqual((int)RowOptions.InsertAt, (int)UpdateOptions.InsertAt);
        }
    }
}
