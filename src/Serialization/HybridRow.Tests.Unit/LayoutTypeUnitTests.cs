// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class LayoutTypeUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void LayoutTypeTest()
        {
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Boolean);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Int8);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Int16);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Int32);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Int64);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.UInt8);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.UInt16);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.UInt32);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.UInt64);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.VarInt);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.VarUInt);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Float32);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Float64);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Decimal);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Null);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Boolean);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.DateTime);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Guid);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Utf8);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Binary);
            LayoutTypeUnitTests.TestLayoutTypeApi(LayoutType.Object);
        }

        private static void TestLayoutTypeApi(LayoutType t)
        {
            Assert.IsNotNull(t.Name);
            Assert.IsFalse(string.IsNullOrWhiteSpace(t.Name));
            Assert.AreNotSame(null, t.IsFixed, t.Name);
            Assert.AreNotSame(null, t.AllowVariable, t.Name);
            Assert.AreNotSame(null, t.IsBool, t.Name);
            Assert.AreNotSame(null, t.IsNull, t.Name);
            Assert.AreNotSame(null, t.IsVarint, t.Name);
            Assert.IsTrue(t.Size >= 0, t.Name);
            Assert.AreNotEqual(LayoutCode.Invalid, t.LayoutCode, t.Name);
        }
    }
}
