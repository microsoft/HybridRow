// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    internal sealed class Guest
    {
        public Guid Id;
        public string FirstName;
        public string LastName;
        public string Title;
        public ISet<string> Emails;
        public IList<string> PhoneNumbers;
        public IDictionary<string, Address> Addresses;
        public string ConfirmNumber;

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

            return obj is Guest && this.Equals((Guest)obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hashCode = this.Id.GetHashCode();
                hashCode = (hashCode * 397) ^ (this.FirstName != null ? this.FirstName.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.LastName != null ? this.LastName.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Title != null ? this.Title.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Emails != null ? this.Emails.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.PhoneNumbers != null ? this.PhoneNumbers.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Addresses != null ? this.Addresses.GetHashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.ConfirmNumber != null ? this.ConfirmNumber.GetHashCode() : 0);
                return hashCode;
            }
        }

        private static bool DictionaryEquals<TKey, TValue>(IDictionary<TKey, TValue> left, IDictionary<TKey, TValue> right)
        {
            if (left == right)
            {
                return true;
            }

            if ((left == null) || (right == null))
            {
                return false;
            }

            if (left.Count != right.Count)
            {
                return false;
            }

            foreach (KeyValuePair<TKey, TValue> p in left)
            {
                TValue value;
                if (!right.TryGetValue(p.Key, out value))
                {
                    return false;
                }

                if (!p.Value.Equals(value))
                {
                    return false;
                }
            }

            return true;
        }

        private bool Equals(Guest other)
        {
            return this.Id.Equals(other.Id) &&
                   string.Equals(this.FirstName, other.FirstName) &&
                   string.Equals(this.LastName, other.LastName) &&
                   string.Equals(this.Title, other.Title) &&
                   string.Equals(this.ConfirmNumber, other.ConfirmNumber) &&
                   ((this.Emails == other.Emails) ||
                    ((this.Emails != null) && (other.Emails != null) && this.Emails.SetEquals(other.Emails))) &&
                   ((this.PhoneNumbers == other.PhoneNumbers) ||
                    ((this.PhoneNumbers != null) && (other.PhoneNumbers != null) && this.PhoneNumbers.SequenceEqual(other.PhoneNumbers))) &&
                   Guest.DictionaryEquals(this.Addresses, other.Addresses);
        }
    }
}
