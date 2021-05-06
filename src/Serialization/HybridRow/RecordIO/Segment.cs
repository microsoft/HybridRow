// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using System;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public struct Segment
    {
        public int Length;
        public string Comment;
        public string SDL;
        public Namespace Schema;

        [Obsolete("Use object-model constructor instead.")]
        public Segment(string comment, string sdl)
        {
            this.Length = 0;
            this.Comment = comment;
            this.SDL = sdl;
            this.Schema = null;
        }

        public Segment(string comment, Namespace ns)
        {
            this.Length = 0;
            this.Comment = comment;
            this.SDL = null;
            this.Schema = ns;
        }
    }
}
