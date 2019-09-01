// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public class LayoutTypeUnitTests
public class LayoutTypeUnitTests {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void LayoutTypeTest()
    public final void LayoutTypeTest() {
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

    private static void TestLayoutTypeApi(LayoutType t) {
        assert t.getName() != null;
        assert !tangible.StringHelper.isNullOrWhiteSpace(t.getName());
        Assert.AreNotSame(null, t.getIsFixed(), t.getName());
        Assert.AreNotSame(null, t.getAllowVariable(), t.getName());
        Assert.AreNotSame(null, t.getIsBool(), t.getName());
        Assert.AreNotSame(null, t.getIsNull(), t.getName());
        Assert.AreNotSame(null, t.getIsVarint(), t.getName());
        Assert.IsTrue(t.Size >= 0, t.getName());
        Assert.AreNotEqual(LayoutCode.Invalid, t.LayoutCode, t.getName());
    }
}