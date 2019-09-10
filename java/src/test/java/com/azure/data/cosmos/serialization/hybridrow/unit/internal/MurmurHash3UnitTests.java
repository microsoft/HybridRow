// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit.internal;

import com.azure.data.cosmos.serialization.hybridrow.internal.Murmur3Hash;

import java.util.Random;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public class MurmurHash3UnitTests
public class MurmurHash3UnitTests {

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static readonly(ulong low, ulong high)[] Expected = new[] { (0x56F1549659CBEE1AUL,
    // 0xCEB3EE124C3E3855UL), (0xFE84B58886F9D717UL, 0xD24C5DE024F5EA6BUL), (0x89F6250648BB11BFUL,
    // 0x95595FB9D4CF58B0UL), (0xC76AFDB39EDC6262UL, 0xB9286AF4FADAF497UL), (0xC2CB4D9B3C9C247EUL,
    // 0xB465D40116B8B7A2UL), (0x317178F5B26D0B35UL, 0x1D564F53E2E468ADUL), (0xE8D75F7C05F43F09UL,
    // 0xA81CEA052AE92D6FUL), (0x8F837665508C08A8UL, 0x2A74E6E47E5497BCUL), (0x609778FDA1AFD731UL,
    // 0x3EB1A0E3BFC653E4UL), (0x0F59B8965FA49D1AUL, 0xCB3BC158243A5DEEUL), (0x7A6D0AC9C98F5908UL,
    // 0xBC93D3042C3E7178UL), (0x863FE5AEBA9A3DFAUL, 0xDF42416658CB87C5UL), (0xDB4C82337C8FB216UL,
    // 0xCA7616B64ABF6B3DUL), (0x0049223177425B48UL, 0x25510D7246BC3C2CUL), (0x31AC129B24F82CABUL,
    // 0xCD7174C2040E9834UL), (0xCE39465288116345UL, 0x1CE6A26BA2E9E67DUL), (0xD2BE55791E13DB17UL,
    // 0xCF30BF3D93B3A9FAUL), (0x43E323DD0F079145UL, 0xF06721555571ABBAUL), (0xB0CE9F170A96F5BCUL,
    // 0x18EE95960369D702UL), (0xBFFAF6BEBC84A2A9UL, 0xE0612B6FC0C9D502UL), (0x33E2D699697BC2DAUL,
    // 0xB7E9CD6313DE05EEUL), (0xCBFD7D8DA2A962BFUL, 0xCF4C281A7750E88AUL), (0xBD8D863F83863088UL,
    // 0x01AFFBDE3D405D35UL), (0xBA2E05DF3328C7DBUL, 0x9620867ADDFE6579UL), (0xC57BD1FB63CA0947UL,
    // 0xE1391F8454D4EA9FUL), (0x6AB710460A5BF9BAUL, 0x11D7E13FBEF63775UL), (0x55C2C7C95F41C483UL,
    // 0xA4DCC9F547A89563UL), (0x8AA5A2031027F216UL, 0x1653FC7AD6CC6104UL), (0xAD8A899FF093D9A5UL,
    // 0x0EB26F6D1CCEB258UL), (0xA3B6D57EBEB965D1UL, 0xE8078FCC5D8C2E3EUL), (0x91ABF587B38224F6UL,
    // 0x35899665A8A9252CUL), (0xF05B1AF0487EE2D4UL, 0x5D7496C1665DDE12UL)};

    private static final Murmur3Hash.HashCode128[] EXPECTED_VALUES = new Murmur3Hash.HashCode128[] {
        new Murmur3Hash.HashCode128(0x56F1549659CBEE1AL, 0xCEB3EE124C3E3855L),
        new Murmur3Hash.HashCode128(0xFE84B58886F9D717L, 0xD24C5DE024F5EA6BL),
        new Murmur3Hash.HashCode128(0x89F6250648BB11BFL, 0x95595FB9D4CF58B0L),
        new Murmur3Hash.HashCode128(0xC76AFDB39EDC6262L, 0xB9286AF4FADAF497L),
        new Murmur3Hash.HashCode128(0xC2CB4D9B3C9C247EL, 0xB465D40116B8B7A2L),
        new Murmur3Hash.HashCode128(0x317178F5B26D0B35L, 0x1D564F53E2E468ADL),
        new Murmur3Hash.HashCode128(0xE8D75F7C05F43F09L, 0xA81CEA052AE92D6FL),
        new Murmur3Hash.HashCode128(0x8F837665508C08A8L, 0x2A74E6E47E5497BCL),
        new Murmur3Hash.HashCode128(0x609778FDA1AFD731L, 0x3EB1A0E3BFC653E4L),
        new Murmur3Hash.HashCode128(0x0F59B8965FA49D1AL, 0xCB3BC158243A5DEEL),
        new Murmur3Hash.HashCode128(0x7A6D0AC9C98F5908L, 0xBC93D3042C3E7178L),
        new Murmur3Hash.HashCode128(0x863FE5AEBA9A3DFAL, 0xDF42416658CB87C5L),
        new Murmur3Hash.HashCode128(0xDB4C82337C8FB216L, 0xCA7616B64ABF6B3DL),
        new Murmur3Hash.HashCode128(0x0049223177425B48L, 0x25510D7246BC3C2CL),
        new Murmur3Hash.HashCode128(0x31AC129B24F82CABL, 0xCD7174C2040E9834L),
        new Murmur3Hash.HashCode128(0xCE39465288116345L, 0x1CE6A26BA2E9E67DL),
        new Murmur3Hash.HashCode128(0xD2BE55791E13DB17L, 0xCF30BF3D93B3A9FAL),
        new Murmur3Hash.HashCode128(0x43E323DD0F079145L, 0xF06721555571ABBAL),
        new Murmur3Hash.HashCode128(0xB0CE9F170A96F5BCL, 0x18EE95960369D702L),
        new Murmur3Hash.HashCode128(0xBFFAF6BEBC84A2A9L, 0xE0612B6FC0C9D502L),
        new Murmur3Hash.HashCode128(0x33E2D699697BC2DAL, 0xB7E9CD6313DE05EEL),
        new Murmur3Hash.HashCode128(0xCBFD7D8DA2A962BFL, 0xCF4C281A7750E88AL),
        new Murmur3Hash.HashCode128(0xBD8D863F83863088L, 0x01AFFBDE3D405D35L),
        new Murmur3Hash.HashCode128(0xBA2E05DF3328C7DBL, 0x9620867ADDFE6579L),
        new Murmur3Hash.HashCode128(0xC57BD1FB63CA0947L, 0xE1391F8454D4EA9FL),
        new Murmur3Hash.HashCode128(0x6AB710460A5BF9BAL, 0x11D7E13FBEF63775L),
        new Murmur3Hash.HashCode128(0x55C2C7C95F41C483L, 0xA4DCC9F547A89563L),
        new Murmur3Hash.HashCode128(0x8AA5A2031027F216L, 0x1653FC7AD6CC6104L),
        new Murmur3Hash.HashCode128(0xAD8A899FF093D9A5L, 0x0EB26F6D1CCEB258L),
        new Murmur3Hash.HashCode128(0xA3B6D57EBEB965D1L, 0xE8078FCC5D8C2E3EL),
        new Murmur3Hash.HashCode128(0x91ABF587B38224F6L, 0x35899665A8A9252CL),
        new Murmur3Hash.HashCode128(0xF05B1AF0487EE2D4L, 0x5D7496C1665DDE12L) };

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //   ORIGINAL LINE:
    //   [TestMethod][Owner("jthunter")] public void Hash128Check()
    public final void Hash128Check() {
        // Generate deterministic data for which the Murmur3Hash is known (see Expected).
        Random rand = new Random(42);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: byte[][] samples = new byte[MurmurHash3UnitTests.Expected.Length][];
        byte[][] samples = new byte[MurmurHash3UnitTests.EXPECTED_VALUES.length][];
        for (int i = 0; i < samples.length; i++) {
            int sampleLength = rand.nextInt(10 * 1024);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: samples[i] = new byte[sampleLength];
            samples[i] = new byte[sampleLength];
            rand.nextBytes(samples[i]);
        }

        // Warm up the loop and verify correctness.
        for (int i = 0; i < samples.length; i++) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: byte[] sample = samples[i];
            byte[] sample = samples[i];
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
            ( long low, long high) = Murmur3Hash.Hash128(sample, (0, 0))
            System.out.println(String.format("(0x%016XUL, 0x%1.16XUL),", high, low));
            assert MurmurHash3UnitTests.EXPECTED_VALUES[i].high == high;
            assert MurmurHash3UnitTests.EXPECTED_VALUES[i].low == low;
        }

        // Measure performance.
        long ticks = MurmurHash3UnitTests.MeasureLoop(samples);
        System.out.println(String.format("Murmur3Hash: %1$s", ticks));
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.NoInlining)][SuppressMessage("Microsoft.Reliability",
    // "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")] private static long MeasureLoop
    // (byte[][] samples)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.NoInlining)][SuppressMessage("Microsoft.Reliability",
    // "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")] private static long MeasureLoop
    // (byte[][] samples)
    private static long MeasureLoop(byte[][] samples) {
        final int outerLoopCount = 10000;
        System.Diagnostics.Stopwatch watch = new System.Diagnostics.Stopwatch();

        System.gc();
        watch.Start();
        for (int j = 0; j < outerLoopCount; j++) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: foreach (byte[] sample in samples)
            for (byte[] sample : samples) {
                Murmur3Hash.Hash128(sample, (0, 0))
            }
        }

        watch.Stop();
        return watch.ElapsedTicks;
    }
}