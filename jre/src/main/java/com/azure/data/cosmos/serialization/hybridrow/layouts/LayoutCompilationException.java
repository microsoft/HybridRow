//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import java.io.Serializable;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Serializable][ExcludeFromCodeCoverage] public sealed class LayoutCompilationException : Exception
public final class LayoutCompilationException extends RuntimeException implements Serializable {
    public LayoutCompilationException() {
    }

    public LayoutCompilationException(String message) {
        super(message);
    }

    public LayoutCompilationException(String message, RuntimeException innerException) {
        super(message, innerException);
    }

    private LayoutCompilationException(SerializationInfo info, StreamingContext context) {
        super(info, context);
    }
}