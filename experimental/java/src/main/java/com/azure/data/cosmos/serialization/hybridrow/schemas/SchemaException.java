// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.io.Serializable;

public final class SchemaException extends RuntimeException implements Serializable {

    public SchemaException() {
    }

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, RuntimeException innerException) {
        super(message, innerException);
    }
}
