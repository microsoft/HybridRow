//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.recordio;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1051 // Do not declare visible instance fields


//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public struct Record
public final class Record {
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public uint Crc32;
    public int Crc32;
    public int Length;

    public Record() {
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Record(int length, uint crc32)
    public Record(int length, int crc32) {
        this.Length = length;
        this.Crc32 = crc32;
    }

    public Record clone() {
        Record varCopy = new Record();

        varCopy.Length = this.Length;
        varCopy.Crc32 = this.Crc32;

        return varCopy;
    }
}