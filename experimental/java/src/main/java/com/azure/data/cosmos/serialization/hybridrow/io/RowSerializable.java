// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;

/**
 * A type may implement this interface to support serialization into a HybridRow.
 */
public interface RowSerializable {
    /**
     * Writes the current instance into the row
     *
     * @param writer  A writer for the current row scope
     * @param typeArg The schematized layout type, if a schema is available
     * @return Success if the write is successful, the error code otherwise
     */
    Result write(RowWriter writer, TypeArgument typeArg);
}