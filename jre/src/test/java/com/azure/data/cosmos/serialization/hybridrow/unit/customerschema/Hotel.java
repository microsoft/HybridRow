//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import azure.data.cosmos.serialization.hybridrow.unit.*;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public final class Hotel {
    public Address Address;
    public String Id;
    public String Name;
    public String Phone;

    public boolean equals(Hotel other) {
        return this.Id.equals(other.Id) && this.Name.equals(other.Name) && this.Phone.equals(other.Phone) && this.Address.equals(other.Address);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        return obj instanceof Hotel && this.equals((Hotel)obj);
    }

    @Override
    public int hashCode() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            int hashCode = this.Id == null ? null : this.Id.hashCode() != null ? this.Id.hashCode() : 0;
            hashCode = (hashCode * 397) ^ (this.Name == null ? null : this.Name.hashCode() != null ?
                this.Name.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.Phone == null ? null : this.Phone.hashCode() != null ?
                this.Phone.hashCode() : 0);
            int tempVar = this.Address.GetHashCode();
            hashCode = (hashCode * 397) ^ (this.Address == null ? null : tempVar != null ? tempVar : 0);
            return hashCode;
        }
    }
}