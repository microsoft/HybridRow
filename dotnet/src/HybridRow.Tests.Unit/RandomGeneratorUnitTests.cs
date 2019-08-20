// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class RandomGeneratorUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void RangeTest()
        {
            int seed = 42;
            RandomGenerator rand = new RandomGenerator(new Random(seed));
            ulong l1 = rand.NextUInt64();
            ulong l2 = rand.NextUInt64();
            Assert.AreNotEqual(l1, l2);

            Console.WriteLine("Check full range of min/max for ushort.");
            for (int min = 0; min <= ushort.MaxValue; min++)
            {
                ushort i1 = rand.NextUInt16((ushort)min, ushort.MaxValue);
                Assert.IsTrue(i1 >= min);
            }

            Console.WriteLine("Check ushort range of min/max for uint.");
            for (uint min = 0; min <= (uint)ushort.MaxValue; min++)
            {
                uint i1 = rand.NextUInt32(min, (uint)ushort.MaxValue);
                Assert.IsTrue(i1 >= min);
                Assert.IsTrue(i1 <= ushort.MaxValue);
            }

            bool seenMax = false;
            bool seenMin = false;
            const ushort maxUShortRange = 10;
            Console.WriteLine("Check inclusivity for ushort.");
            while (!(seenMax && seenMin))
            {
                ushort i1 = rand.NextUInt16(ushort.MinValue, maxUShortRange);
                seenMin = seenMin || i1 == ushort.MinValue;
                seenMax = seenMax || i1 == maxUShortRange;
                Assert.IsTrue(i1 <= maxUShortRange);
            }

            seenMax = false;
            seenMin = false;
            Console.WriteLine("Check inclusivity for short.");
            const short minShortRange = -10;
            const short maxShortRange = 10;
            while (!(seenMax && seenMin))
            {
                short i1 = rand.NextInt16(minShortRange, maxShortRange);
                seenMin = seenMin || i1 == -10;
                seenMax = seenMax || i1 == 10;
                Assert.IsTrue(i1 >= minShortRange);
                Assert.IsTrue(i1 <= maxShortRange);
            }
        }
    }
}
