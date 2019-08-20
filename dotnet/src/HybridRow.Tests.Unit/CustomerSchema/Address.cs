// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema
{
    internal sealed class Address
    {
        public string Street;
        public string City;
        public string State;
        public PostalCode PostalCode;

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

            return obj is Address && this.Equals((Address)obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hashCode = this.Street != null ? this.Street.GetHashCode() : 0;
                hashCode = (hashCode * 397) ^ (this.City != null ? this.City.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.State != null ? this.State.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.PostalCode != null ? this.PostalCode.GetHashCode() : 0);
                return hashCode;
            }
        }

        private bool Equals(Address other)
        {
            return string.Equals(this.Street, other.Street) &&
                   string.Equals(this.City, other.City) &&
                   string.Equals(this.State, other.State) &&
                   object.Equals(this.PostalCode, other.PostalCode);
        }
    }
}
