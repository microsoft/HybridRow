// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    public struct Segment
    {
        public int Length;
        public string Comment;
        public string SDL;

        public Segment(string comment, string sdl)
        {
            this.Length = 0;
            this.Comment = comment;
            this.SDL = sdl;
        }
    }
}
