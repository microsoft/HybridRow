//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

import java.io.Serializable;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Serializable][ExcludeFromCodeCoverage] public sealed class SchemaException : Exception
public final class SchemaException extends RuntimeException implements Serializable {
    public SchemaException() {
    }

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, RuntimeException innerException) {
        super(message, innerException);
    }

    private SchemaException(SerializationInfo info, StreamingContext context) {
        super(info, context);
    }
}