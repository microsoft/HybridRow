// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A path/type/value triplet representing a field in a HybridRow.
 */
public class DataItem {

    @JsonProperty
    private final List<String> nodes;

    @JsonProperty
    private final LayoutCode type;

    @JsonProperty
    private final Object value;

    private final Supplier<String> name;
    private final Supplier<String> path;

    @SuppressWarnings("UnstableApiUsage")
    DataItem(
        @Nonnull final Collection<Utf8String> nodes,
        @Nonnull final Utf8String name,
        @Nonnull final LayoutCode type,
        @Nonnull final Object value) {

        checkNotNull(nodes, "expected non-null nodes");
        checkNotNull(name, "expected non-null name");
        checkNotNull(type, "expected non-null type");
        checkNotNull(value, "expected non-null value");

        //noinspection ConstantConditions
        this.nodes = ImmutableList.<String>builderWithExpectedSize(nodes.size() + 1)
            .addAll(nodes.stream().map(Utf8String::toUtf16).iterator())
            .add(name.toUtf16())
            .build();

        this.type = type;
        this.value = value;

        this.name = Suppliers.memoize(() -> this.nodes.get(this.nodes.size() - 1));

        this.path = Suppliers.memoize(() -> {

            if (this.nodes.size() == 1) {
                return this.nodes.get(0);
            }

            StringBuilder builder = new StringBuilder(this.nodes.stream()
                .map(String::length)
                .reduce(this.nodes.size() - 1, Integer::sum)
            );

            int i;

            for (i = 0; i < this.nodes.size() - 1; ++i) {
                builder.append(this.nodes.get(i));
                if (this.nodes.get(i + 1).charAt(0) != '[') {
                    builder.append('.');
                }
            }

            return builder.append(this.nodes.get(i)).toString();
        });
    }

    public String name() {
        return this.name.get();
    }

    public List<String> nodes() {
        return this.nodes;
    }

    public String path() {
        return this.path.get();
    }

    @Override
    public String toString() {
        return Json.toString(this);
    }

    public LayoutCode type() {
        return this.type;
    }

    public Object value() {
        return this.value;
    }
}
