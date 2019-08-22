//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import azure.data.cosmos.serialization.hybridrow.Tests.Unit.*;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public final class Address {
    public String City;
    public PostalCode PostalCode;
    public String State;
    public String Street;

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        return obj instanceof Address && this.equals((Address)obj);
    }

    @Override
    public int hashCode() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            int hashCode = this.Street != null ? this.Street.hashCode() : 0;
            hashCode = (hashCode * 397) ^ (this.City != null ? this.City.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.State != null ? this.State.hashCode() : 0);
            hashCode = (hashCode * 397) ^ (this.PostalCode != null ? this.PostalCode.hashCode() : 0);
            return hashCode;
        }
    }

    private boolean equals(Address other) {
        return this.Street.equals(other.Street) && this.City.equals(other.City) && this.State.equals(other.State) && this.PostalCode.equals(other.PostalCode);
    }
}