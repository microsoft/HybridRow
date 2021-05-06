// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes the set of options that apply to the entire schema and the way it is validated.
 */
public class SchemaOptions {

    private boolean disableSystemPrefix;
    private boolean disallowUnschematized;
    private boolean enablePropertyLevelTimestamp;

    /**
     * {@code true} if prefixing system properties with a prefix of {@code "__sys_"} is disabled.
     * <p>
     * The system property prefix is required to distinguish properties owned by the store layer.
     *
     * @return {@code true} if prefixing system properties with a prefix of {@code "__sys_"} is disabled.
     */
    public final boolean disableSystemPrefix() {
        return this.disableSystemPrefix;
    }

    public final void disableSystemPrefix(boolean value) {
        this.disableSystemPrefix = value;
    }

    /**
     * {@code true} if structural schema validation is enabled.
     * <p>
     * When structural schema validation is enabled then attempting to store an unschematized
     * path in the row, or a value whose type does not conform to the type constraints defined for that
     * path within the schema will lead to a schema validation error. When structural schema validation is
     * NOT enabled, then storing an unschematized path or non-confirming value will lead to a sparse
     * column override of the path.  The value will be stored (and any existing value at that path will be
     * overwritten).  No error will be given.
     *
     * @return {@code true} if structural schema validation is enabled.
     */
    public final boolean disallowUnschematized() {
        return this.disallowUnschematized;
    }

    public final void disallowUnschematized(boolean value) {
        this.disallowUnschematized = value;
    }

    /**
     * {@code true} if behavior in the Schema that acts based on property level timestamps is triggered.
     * <p>
     * In Cassandra, this means that new columns are added for each top level property that has values of the client
     * side timestamp. This is then used in conflict resolution to independently resolve each property based on the
     * timestamp value of that property.
     *
     * @return {@code true} if behavior in the Schema that acts based on property level timestamps is triggered.
     */
    public final boolean enablePropertyLevelTimestamp() {
        return this.enablePropertyLevelTimestamp;
    }

    public final void enablePropertyLevelTimestamp(boolean value) {
        this.enablePropertyLevelTimestamp = value;
    }
}