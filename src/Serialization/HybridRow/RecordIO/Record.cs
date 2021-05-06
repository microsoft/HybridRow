// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    public struct Record
    {
        public int Length;
        public uint Crc32;

        public Record(int length, uint crc32)
        {
            this.Length = length;
            this.Crc32 = crc32;
        }
    }
}
