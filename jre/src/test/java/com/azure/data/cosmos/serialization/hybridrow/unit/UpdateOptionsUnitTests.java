// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.serialization.hybridrow.RowOptions;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public class UpdateOptionsUnitTests
public class UpdateOptionsUnitTests {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void UpdateOptionsTest()
    public final void UpdateOptionsTest() {
        assert RowOptions.None.getValue() == UpdateOptions.None.getValue();
        assert RowOptions.Update.getValue() == UpdateOptions.Update.getValue();
        assert RowOptions.Insert.getValue() == UpdateOptions.Insert.getValue();
        assert RowOptions.Upsert.getValue() == UpdateOptions.Upsert.getValue();
        assert RowOptions.InsertAt.getValue() == UpdateOptions.InsertAt.getValue();
    }
}