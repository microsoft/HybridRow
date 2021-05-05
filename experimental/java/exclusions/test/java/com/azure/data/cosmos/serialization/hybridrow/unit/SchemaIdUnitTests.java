// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import Newtonsoft.Json.*;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public class SchemaIdUnitTests
public class SchemaIdUnitTests {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void SchemaIdTest()
    public final void SchemaIdTest() {
        SchemaId a = new SchemaId(1);
        SchemaId b = new SchemaId(2);
        SchemaId c = new SchemaId();

        assert 1 == a.value();
        assert 2 == b.value();
        assert SchemaId.INVALID == c.clone();
        assert 2 != a.value();
        assert a.clone() != b.clone();
        assert SchemaId.opEquals(a.clone(), a.clone());
        assert SchemaId.opNotEquals(a.clone(), b.clone());
        assert !a.equals(null);
        assert a.hashCode() == (new SchemaId(1)).hashCode();
        assert a.hashCode() != (new SchemaId(-1)).hashCode();

        String json = JsonConvert.SerializeObject(a.clone());
        assert "1" == json;
        assert "1" == a.toString();

        assert a.clone() == JsonConvert.<SchemaId>DeserializeObject(json);
        json = JsonConvert.SerializeObject(b.clone());
        assert "2" == json;
        assert "2" == b.toString();
        assert b.clone() == JsonConvert.<SchemaId>DeserializeObject(json);
        json = JsonConvert.SerializeObject(c.clone());
        assert "0" == json;
        assert "0" == c.toString();
        assert c.clone() == JsonConvert.<SchemaId>DeserializeObject(json);
    }
}