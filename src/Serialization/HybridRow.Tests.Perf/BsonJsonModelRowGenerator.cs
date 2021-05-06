// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using MongoDB.Bson.IO;

    internal sealed class BsonJsonModelRowGenerator : IDisposable
    {
        private readonly MemoryStream stream;
        private readonly BsonWriter writer;

        public BsonJsonModelRowGenerator(int capacity)
        {
            this.stream = new MemoryStream(capacity);
            this.writer = new BsonBinaryWriter(this.stream);
        }

        public int Length => (int)this.stream.Position;

        public byte[] ToArray()
        {
            return this.stream.ToArray();
        }

        public void Reset()
        {
            this.stream.SetLength(0);
            this.stream.Position = 0;
        }

        public void WriteBuffer(Dictionary<Utf8String, object> dict)
        {
            this.writer.WriteStartDocument();
            foreach ((Utf8String propPath, object propValue) in dict)
            {
                this.JsonModelSwitch(propPath, propValue);
            }

            this.writer.WriteEndDocument();
        }

        public void Dispose()
        {
            this.writer.Dispose();
            this.stream.Dispose();
        }

        private void JsonModelSwitch(Utf8String path, object value)
        {
            if (path != null)
            {
                this.writer.WriteName(path.ToString());
            }

            switch (value)
            {
                case null:
                    this.writer.WriteNull();
                    return;
                case bool x:
                    this.writer.WriteBoolean(x);
                    return;
                case long x:
                    this.writer.WriteInt64(x);
                    return;
                case double x:
                    this.writer.WriteDouble(x);
                    return;
                case string x:
                    this.writer.WriteString(x);
                    return;
                case Utf8String x:
                    this.writer.WriteString(x.ToString());
                    return;
                case byte[] x:
                    this.writer.WriteBytes(x);
                    return;
                case Dictionary<Utf8String, object> x:
                    this.writer.WriteStartDocument();
                    foreach ((Utf8String propPath, object propValue) in x)
                    {
                        this.JsonModelSwitch(propPath, propValue);
                    }

                    this.writer.WriteEndDocument();
                    return;
                case List<object> x:
                    this.writer.WriteStartArray();
                    foreach (object item in x)
                    {
                        this.JsonModelSwitch(null, item);
                    }

                    this.writer.WriteEndArray();

                    return;
                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {value.GetType().Name}");
                    return;
            }
        }
    }
}
