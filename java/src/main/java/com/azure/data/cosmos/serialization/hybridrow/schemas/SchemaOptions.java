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
     * If the is value true, then disables prefixing the system properties with a prefix __sys_
     * for reserved properties owned by the store layer.
     */
    public final boolean disableSystemPrefix() {
        return this.disableSystemPrefix;
    }

    public final void disableSystemPrefix(boolean value) {
        this.disableSystemPrefix = value;
    }

    /**
     * If true then structural schema validation is enabled.
     * <p>
     * When structural schema validation is enabled then attempting to store an unschematized
     * path in the row, or a value whose type does not conform to the type constraints defined for that
     * path within the schema will lead to a schema validation error. When structural schema validation is
     * NOT enabled, then storing an unschematized path or non-confirming value will lead to a sparse
     * column override of the path.  The value will be stored (and any existing value at that path will be
     * overwritten).  No error will be given.
     */
    public final boolean disallowUnschematized() {
        return this.disallowUnschematized;
    }

    public final void disallowUnschematized(boolean value) {
        this.disallowUnschematized = value;
    }

    /**
     * If set and has the value true, then triggers behavior in the Schema that acts based on property
     * level timestamps. In Cassandra, this means that new columns are added for each top level property
     * that has values of the client side timestamp. This is then used in conflict resolution to independently
     * resolve each property based on the timestamp value of that property.
     */
    public final boolean enablePropertyLevelTimestamp() {
        return this.enablePropertyLevelTimestamp;
    }

    public final void enablePropertyLevelTimestamp(boolean value) {
        this.enablePropertyLevelTimestamp = value;
    }
}