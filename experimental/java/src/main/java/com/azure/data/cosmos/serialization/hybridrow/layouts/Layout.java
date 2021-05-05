// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Schema;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Layout describes the structure of a Hybrid Row.
 * <p>
 * A layout indicates the number, order, and type of all schematized columns to be stored within a hybrid row. The
 * order and type of columns defines the physical ordering of bytes used to encode the row and impacts the cost of
 * updating the row.
 * <p>
 * A layout is created by compiling a {@link Schema} through {@link Schema#compile(Namespace)} or by constructor through
 * a {@link LayoutBuilder}.
 *
 * {@link Layout} is immutable.
 */
public final class Layout {

    public static final Layout EMPTY = SystemSchema.layoutResolver().resolve(SystemSchema.EMPTY_SCHEMA_ID);

    private final String name;
    private final int numBitmaskBytes;
    private final int numFixed;
    private final int numVariable;
    private final HashMap<Utf8String, LayoutColumn> pathMap;
    private final HashMap<String, LayoutColumn> pathStringMap;
    private final SchemaId schemaId;
    private final int size;
    private final StringTokenizer tokenizer;
    private final ImmutableList<LayoutColumn> topColumns;

    @SuppressWarnings("UnstableApiUsage")
    public Layout(
        @Nonnull final String name,
        @Nonnull final SchemaId schemaId,
        final int numBitmaskBytes,
        final int minRequiredSize,
        @Nonnull final ArrayList<LayoutColumn> columns) {

        checkNotNull(name, "expected non-null name");
        checkNotNull(schemaId, "expected non-null schemaId");
        checkNotNull(columns, "expected non-null columns");

        checkArgument(numBitmaskBytes >= 0, "expected non-negative numBitmaskBytes, not %s", numBitmaskBytes);
        checkArgument(minRequiredSize >= 0, "expected non-negative minRequiredSize", minRequiredSize);

        this.name = name;
        this.schemaId = schemaId;
        this.numBitmaskBytes = numBitmaskBytes;
        this.size = minRequiredSize;
        this.tokenizer = new StringTokenizer();
        this.pathMap = new HashMap<>(columns.size());
        this.pathStringMap = new HashMap<>(columns.size());

        ImmutableList.Builder<LayoutColumn> builder = ImmutableList.builderWithExpectedSize(columns.size());
        int numFixed = 0;
        int numVariable = 0;

        for (LayoutColumn column : columns) {

            this.tokenizer().add(column.path());
            this.pathMap.put(column.fullPath(), column);
            this.pathStringMap.put(column.fullPath().toString(), column);

            if (column.storage() == StorageKind.FIXED) {
                numFixed++;
            } else if (column.storage() == StorageKind.VARIABLE) {
                numVariable++;
            }

            if (column.parent() == null) {
                builder.add(column);
            }
        }

        this.numFixed = numFixed;
        this.numVariable = numVariable;
        this.topColumns = builder.build();
    }

    /**
     * Finds a column specification for a column with a matching path.
     *
     * @param path path of the column to find
     * @return {@link LayoutColumn}, if a column with the {@code path} is found, {@link Optional#empty()}
     */
    public Optional<LayoutColumn> tryFind(@Nonnull UtfAnyString path) {

        checkNotNull(path);

        if (path.isNull()) {
            return Optional.empty();
        }

        if (path.isUtf8()) {
            return Optional.ofNullable(this.pathMap.get(path.toUtf8()));
        }

        return Optional.ofNullable(this.pathStringMap.get(path.toUtf16()));
    }

    /**
     * Finds a column specification for a column with a matching path.
     *
     * @param path   The path of the column to find.
     * @return True if a column with the path is found, otherwise false.
     */
    public Optional<LayoutColumn> tryFind(@Nonnull String path) {
        checkNotNull(path);
        return Optional.ofNullable(this.pathStringMap.get(path));
    }

    /**
     * Top level columns defined by the current {@link Layout} in left-to-right order.
     *
     * @return Top level columns defined by the current {@link Layout} in left-to-right order
     */
    public List<LayoutColumn> columns() {
        return this.topColumns;
    }

    /**
     * Name of the layout.
     * <p>
     * Usually this is the name of the {@link Schema} from which this {@link Layout} was generated.
     *
     * @return name of the layout.
     */
    public String name() {
        return this.name;
    }

    /**
     * The number of bit mask bytes allocated within the layout.
     * <p>
     * A presence bit is allocated for each fixed and variable-length field.  Sparse columns never have presence bits.
     * Fixed boolean allocate an additional bit from the bitmask to store their value.
     *
     * @return the number of bit mask bytes allocated with the layout.
     */
    public int numBitmaskBytes() {
        return this.numBitmaskBytes;
    }

    /**
     * The number of fixed columns.
     *
     * @return the number of fixed columns.
     */
    public int numFixed() {
        return this.numFixed;
    }

    /**
     * The number of variable-length columns.
     *
     * @return the number of variable-length columns.
     */
    public int numVariable() {
        return this.numVariable;
    }

    /**
     * Unique identifier of the schema from which this {@link Layout} was generated.
     *
     * @return the unique identifier of the schema from which this {@link Layout} was generated.
     */
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * The minimum required size of a row with this layout.
     * <p>
     * This size excludes all sparse columns, and assumes all columns (including variable) are
     * null.
     *
     * @return the minimum required size of a row with this layout.
     */
    public int size() {
        return this.size;
    }

    /**
     * A human readable diagnostic string representing this {@link Layout}.
     * <p>
     * This representation should only be used for debugging and diagnostic purposes.
     *
     * @return a human readable diagnostic string representing this {@link Layout}.
     */
    @Override
    @Nonnull
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Layout:\n");
        sb.append(String.format("\tCount: %1$s\n", this.topColumns.size()));
        sb.append(String.format("\tFixedSize: %1$s\n", this.size()));

        for (LayoutColumn column : this.topColumns) {
            if (column.type().isFixed()) {
                if (column.type().isBoolean()) {
                    sb.append(String.format("\t%1$s: %2$s @ %3$s:%4$s:%5$s\n", column.fullPath(), column.type().name(), column.offset(), column.nullBit(), column.booleanBit()));
                } else {
                    sb.append(String.format("\t%1$s: %2$s @ %3$s\n", column.fullPath(), column.type().name(), column.offset()));
                }
            } else {
                sb.append(String.format("\t%1$s: %2$s[%4$s] @ %3$s\n", column.fullPath(), column.type().name(), column.offset(), column.size()));
            }
        }

        return sb.toString();
    }

    /**
     * A {@linkplain StringTokenizer tokenizer} for path strings.
     *
     * @return a {@linkplain StringTokenizer tokenizer} for path strings.
     */
    public StringTokenizer tokenizer() {
        return this.tokenizer;
    }
}