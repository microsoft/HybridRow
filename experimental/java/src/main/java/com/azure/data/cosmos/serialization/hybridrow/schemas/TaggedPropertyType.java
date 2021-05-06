// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.ArrayList;
import java.util.List;

/**
 * Tagged properties pair one or more typed values with an API-specific uint8 type code.
 * <p>
 * The {@code UInt8} type code is implicitly in position 0 within the resulting tagged and should not be specified in
 * {@link #items()}.
 */
public class TaggedPropertyType extends ScopePropertyType {

    public static final int MAX_TAGGED_ARGUMENTS = 2;
    public static final int MIN_TAGGED_ARGUMENTS = 1;

    private List<PropertyType> items;

    /**
     * Initializes a new instance of the {@link TaggedPropertyType} class.
     */
    public TaggedPropertyType() {
        this.items = new ArrayList<>();
    }

    /**
     * Types of the elements of the tagged in element order.
     *
     * @return a list of property types.
     */
    public final List<PropertyType> items() {
        return this.items;
    }

    public final TaggedPropertyType items(List<PropertyType> value) {
        this.items = value != null ? value : new ArrayList<>();
        return this;
    }
}