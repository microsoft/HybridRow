// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema
{
    internal sealed class PostalCode
    {
        public int Zip;
        public short? Plus4;

        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(null, obj))
            {
                return false;
            }

            if (object.ReferenceEquals(this, obj))
            {
                return true;
            }

            return obj is PostalCode && this.Equals((PostalCode)obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return (this.Zip * 397) ^ this.Plus4.GetHashCode();
            }
        }

        private bool Equals(PostalCode other)
        {
            return this.Zip == other.Zip && this.Plus4 == other.Plus4;
        }
    }
}
