// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import azure.data.cosmos.serialization.hybridrow.unit.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public final class Guest {
    public Map<String, Address> Addresses;
    public String ConfirmNumber;
    public Set<String> Emails;
    public String FirstName;
    public UUID Id;
    public String LastName;
    public List<String> PhoneNumbers;
    public String Title;

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        return obj instanceof Guest && this.equals((Guest)obj);
    }

    @Override
    public int hashCode() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            int hashCode = this.Id.hashCode();
            hashCode = (hashCode * 397) ^ (this.FirstName != null ? this.FirstName.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.LastName != null ? this.LastName.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.Title != null ? this.Title.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.Emails != null ? this.Emails.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.PhoneNumbers != null ? this.PhoneNumbers.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.Addresses != null ? this.Addresses.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.ConfirmNumber != null ? this.ConfirmNumber.hashCode() : 0);
            return hashCode;
        }
    }

    private static <TKey, TValue> boolean DictionaryEquals(Map<TKey, TValue> left, Map<TKey, TValue> right) {
        if (left == right) {
            return true;
        }

        if ((left == null) || (right == null)) {
            return false;
        }

        if (left.size() != right.size()) {
            return false;
        }

        for (Map.Entry<TKey, TValue> p : left.entrySet()) {
            TValue value;
            if (!(right.containsKey(p.getKey()) && (value = right.get(p.getKey())) == value)) {
                return false;
            }

            if (!p.getValue().equals(value)) {
                return false;
            }
        }

        return true;
    }

    private boolean equals(Guest other) {
        //C# TO JAVA CONVERTER WARNING: Java AbstractList 'equals' is not always identical to LINQ 'SequenceEqual':
        //ORIGINAL LINE: return this.Id.Equals(other.Id) && string.Equals(this.FirstName, other.FirstName) && string
        // .Equals(this.LastName, other.LastName) && string.Equals(this.Title, other.Title) && string.Equals(this
        // .ConfirmNumber, other.ConfirmNumber) && ((this.Emails == other.Emails) || ((this.Emails != null) && (other
        // .Emails != null) && this.Emails.SetEquals(other.Emails))) && ((this.PhoneNumbers == other.PhoneNumbers) ||
        // ((this.PhoneNumbers != null) && (other.PhoneNumbers != null) && this.PhoneNumbers.SequenceEqual(other
        // .PhoneNumbers))) && Guest.DictionaryEquals(this.Addresses, other.Addresses);
        return this.Id.equals(other.Id) && this.FirstName.equals(other.FirstName) && this.LastName.equals(other.LastName) && this.Title.equals(other.Title) && this.ConfirmNumber.equals(other.ConfirmNumber) && ((this.Emails == other.Emails) || ((this.Emails != null) && (other.Emails != null) && this.Emails.equals(other.Emails))) && ((this.PhoneNumbers == other.PhoneNumbers) || ((this.PhoneNumbers != null) && (other.PhoneNumbers != null) && this.PhoneNumbers.equals(other.PhoneNumbers))) && Guest.DictionaryEquals(this.Addresses, other.Addresses);
    }
}