//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf;

import MongoDB.Bson.*;
import MongoDB.Bson.IO.*;

public final class BsonReaderExtensions {
    public static void VisitBsonDocument(BsonReader bsonReader) {
        bsonReader.ReadStartDocument();
        BsonType type;
        while ((type = bsonReader.ReadBsonType()) != BsonType.EndOfDocument) {
            String path = bsonReader.ReadName();
            switch (type) {
                case BsonType.Array:
                    Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.BsonReaderExtensions.VisitBsonArray(bsonReader);
                    break;

                case BsonType.Document:
                    Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.BsonReaderExtensions.VisitBsonDocument(bsonReader);
                    break;

                default:
                    bsonReader.SkipValue();
                    break;
            }
        }

        bsonReader.ReadEndDocument();
    }

    private static void VisitBsonArray(BsonReader bsonReader) {
        bsonReader.ReadStartArray();
        BsonType type;
        while ((type = bsonReader.ReadBsonType()) != BsonType.EndOfDocument) {
            switch (type) {
                case BsonType.Array:
                    Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.BsonReaderExtensions.VisitBsonArray(bsonReader);
                    break;

                case BsonType.Document:
                    Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.BsonReaderExtensions.VisitBsonDocument(bsonReader);
                    break;

                default:
                    bsonReader.SkipValue();
                    break;
            }
        }

        bsonReader.ReadEndArray();
    }
}