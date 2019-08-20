// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System.Diagnostics.CodeAnalysis;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class RowBufferUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        [SuppressMessage("StyleCop.CSharp.ReadabilityRules", "SA1139:UseLiteralsSuffixNotationInsteadOfCasting", Justification = "Explicit")]
        public void VarIntTest()
        {
            // Brute force test all signed 16-bit values.
            for (int i = short.MinValue; i <= short.MaxValue; i++)
            {
                short s = (short)i;
                this.RoundTripVarInt(s);
            }

            // Test boundary conditions for larger values.
            this.RoundTripVarInt(0);
            this.RoundTripVarInt(int.MinValue);
            this.RoundTripVarInt(unchecked((int)0x80000000ul));
            this.RoundTripVarInt(unchecked((int)0x7FFFFFFFul));
            this.RoundTripVarInt(int.MaxValue);
            this.RoundTripVarInt(long.MinValue);
            this.RoundTripVarInt(unchecked((long)0x8000000000000000ul));
            this.RoundTripVarInt(unchecked((long)0x7FFFFFFFFFFFFFFFul));
            this.RoundTripVarInt(long.MaxValue);
        }

        private void RoundTripVarInt(short s)
        {
            ulong encoded = RowBuffer.RotateSignToLsb(s);
            long decoded = RowBuffer.RotateSignToMsb(encoded);
            short t = unchecked((short)decoded);
            Assert.AreEqual(s, t, "Value: {0}", s);
        }

        private void RoundTripVarInt(int s)
        {
            ulong encoded = RowBuffer.RotateSignToLsb(s);
            long decoded = RowBuffer.RotateSignToMsb(encoded);
            int t = unchecked((int)decoded);
            Assert.AreEqual(s, t, "Value: {0}", s);
        }

        private void RoundTripVarInt(long s)
        {
            ulong encoded = RowBuffer.RotateSignToLsb(s);
            long decoded = RowBuffer.RotateSignToMsb(encoded);
            Assert.AreEqual(s, decoded, "Value: {0}", s);
        }
    }
}
