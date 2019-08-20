// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema
{
    internal sealed class Hotel
    {
        public string Id;
        public string Name;
        public string Phone;
        public Address Address;

        public bool Equals(Hotel other)
        {
            return string.Equals(this.Id, other.Id) &&
                   string.Equals(this.Name, other.Name) &&
                   string.Equals(this.Phone, other.Phone) &&
                   object.Equals(this.Address, other.Address);
        }

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

            return obj is Hotel && this.Equals((Hotel)obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hashCode = this.Id?.GetHashCode() ?? 0;
                hashCode = (hashCode * 397) ^ (this.Name?.GetHashCode() ?? 0);
                hashCode = (hashCode * 397) ^ (this.Phone?.GetHashCode() ?? 0);
                hashCode = (hashCode * 397) ^ (this.Address?.GetHashCode() ?? 0);
                return hashCode;
            }
        }
    }
}
