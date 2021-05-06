// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using MongoDB.Bson;
    using MongoDB.Bson.IO;

    internal static class BsonReaderExtensions
    {
        public static void VisitBsonDocument(this BsonReader bsonReader)
        {
            bsonReader.ReadStartDocument();
            BsonType type;
            while ((type = bsonReader.ReadBsonType()) != BsonType.EndOfDocument)
            {
                string path = bsonReader.ReadName();
                switch (type)
                {
                    case BsonType.Array:
                        bsonReader.VisitBsonArray();
                        break;

                    case BsonType.Document:
                        bsonReader.VisitBsonDocument();
                        break;

                    default:
                        bsonReader.SkipValue();
                        break;
                }
            }

            bsonReader.ReadEndDocument();
        }

        private static void VisitBsonArray(this BsonReader bsonReader)
        {
            bsonReader.ReadStartArray();
            BsonType type;
            while ((type = bsonReader.ReadBsonType()) != BsonType.EndOfDocument)
            {
                switch (type)
                {
                    case BsonType.Array:
                        bsonReader.VisitBsonArray();
                        break;

                    case BsonType.Document:
                        bsonReader.VisitBsonDocument();
                        break;

                    default:
                        bsonReader.SkipValue();
                        break;
                }
            }

            bsonReader.ReadEndArray();
        }
    }
}
