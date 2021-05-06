// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    internal static class ArrayAssert
    {
        public static void AreEqual<T>(T[] expected, T[] actual)
        {
            if (expected == null)
            {
                Assert.IsNull(actual);
                return;
            }

            Assert.IsNotNull(actual);
            Assert.AreEqual(expected.Length, actual.Length);
            for (int i = 0; i < expected.Length; i++)
            {
                Assert.AreEqual(expected[i], actual[i]);
            }
        }

        public static void AreEqual<T>(T[] expected, T[] actual, string message)
        {
            if (expected == null)
            {
                Assert.IsNull(actual, message);
                return;
            }

            Assert.IsNotNull(actual, message);
            Assert.AreEqual(expected.Length, actual.Length, message);
            for (int i = 0; i < expected.Length; i++)
            {
                Assert.AreEqual(expected[i], actual[i], message);
            }
        }

        public static void AreEqual<T>(T[] expected, T[] actual, string message, params object[] parameters)
        {
            if (expected == null)
            {
                Assert.IsNull(actual, message, parameters);
                return;
            }

            Assert.IsNotNull(actual, message, parameters);
            Assert.AreEqual(expected.Length, actual.Length, message, parameters);
            for (int i = 0; i < expected.Length; i++)
            {
                Assert.AreEqual(expected[i], actual[i], message, parameters);
            }
        }
    }
}
