// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import java.util.Random;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public class RandomGeneratorUnitTests
public class RandomGeneratorUnitTests {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void RangeTest()
    public final void RangeTest() {
        int seed = 42;
        RandomGenerator rand = new RandomGenerator(new Random(seed));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong l1 = rand.NextUInt64();
        long l1 = rand.NextUInt64();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong l2 = rand.NextUInt64();
        long l2 = rand.NextUInt64();
        assert l1 != l2;

        System.out.println("Check full range of min/max for ushort.");
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: for (int min = 0; min <= ushort.MaxValue; min++)
        for (int min = 0; min <= Short.MAX_VALUE; min++) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ushort i1 = rand.NextUInt16((ushort)min, ushort.MaxValue);
            short i1 = rand.NextUInt16((short)min, Short.MAX_VALUE);
            assert i1 >= min;
        }

        System.out.println("Check ushort range of min/max for uint.");
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: for (uint min = 0; min <= (uint)ushort.MaxValue; min++)
        for (int min = 0; min <= (int)Short.MAX_VALUE; min++) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: uint i1 = rand.NextUInt32(min, (uint)ushort.MaxValue);
            int i1 = rand.NextUInt32(min, (int)Short.MAX_VALUE);
            assert i1 >= min;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: Assert.IsTrue(i1 <= ushort.MaxValue);
            assert i1 <= Short.MAX_VALUE;
        }

        boolean seenMax = false;
        boolean seenMin = false;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: const ushort maxUShortRange = 10;
        final short maxUShortRange = 10;
        System.out.println("Check inclusivity for ushort.");
        while (!(seenMax && seenMin)) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ushort i1 = rand.NextUInt16(ushort.MinValue, maxUShortRange);
            short i1 = rand.NextUInt16(Short.MIN_VALUE, maxUShortRange);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: seenMin = seenMin || i1 == ushort.MinValue;
            seenMin = seenMin || i1 == Short.MIN_VALUE;
            seenMax = seenMax || i1 == maxUShortRange;
            assert i1 <= maxUShortRange;
        }

        seenMax = false;
        seenMin = false;
        System.out.println("Check inclusivity for short.");
        final short minShortRange = -10;
        final short maxShortRange = 10;
        while (!(seenMax && seenMin)) {
            short i1 = rand.NextInt16(minShortRange, maxShortRange);
            seenMin = seenMin || i1 == -10;
            seenMax = seenMax || i1 == 10;
            assert i1 >= minShortRange;
            assert i1 <= maxShortRange;
        }
    }
}