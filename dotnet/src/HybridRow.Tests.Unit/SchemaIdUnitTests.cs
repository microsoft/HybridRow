// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft.Json;

    [TestClass]
    public class SchemaIdUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void SchemaIdTest()
        {
            SchemaId a = new SchemaId(1);
            SchemaId b = new SchemaId(2);
            SchemaId c = default(SchemaId);

            Assert.AreEqual(1, a.Id);
            Assert.AreEqual(2, b.Id);
            Assert.AreEqual(SchemaId.Invalid, c);
            Assert.AreNotEqual(2, a.Id);
            Assert.AreNotEqual(a, b);
            Assert.IsTrue(a == a);
            Assert.IsTrue(a != b);
            Assert.IsFalse(a.Equals(null));
            Assert.AreEqual(a.GetHashCode(), new SchemaId(1).GetHashCode());
            Assert.AreNotEqual(a.GetHashCode(), new SchemaId(-1).GetHashCode());

            string json = JsonConvert.SerializeObject(a);
            Assert.AreEqual("1", json);
            Assert.AreEqual("1", a.ToString());

            Assert.AreEqual(a, JsonConvert.DeserializeObject<SchemaId>(json));
            json = JsonConvert.SerializeObject(b);
            Assert.AreEqual("2", json);
            Assert.AreEqual("2", b.ToString());
            Assert.AreEqual(b, JsonConvert.DeserializeObject<SchemaId>(json));
            json = JsonConvert.SerializeObject(c);
            Assert.AreEqual("0", json);
            Assert.AreEqual("0", c.ToString());
            Assert.AreEqual(c, JsonConvert.DeserializeObject<SchemaId>(json));
        }
    }
}
