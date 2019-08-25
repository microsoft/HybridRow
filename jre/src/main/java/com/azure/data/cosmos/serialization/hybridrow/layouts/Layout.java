//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Schema;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Layout describes the structure of a Hybrid Row
 * <p>
 * A layout indicates the number, order, and type of all schematized columns to be stored within a hybrid row. The
 * order and type of columns defines the physical ordering of bytes used to encode the row and impacts the cost of
 * updating the row.
 * <p>
 * A layout is created by compiling a {@link Schema} through {@link Schema#Compile(Namespace)} or by constructor through
 * a {@link LayoutBuilder}.
 *
 * {@link Layout} is immutable.
 */
public final class Layout {

    public static final Layout EMPTY = SystemSchema.LayoutResolver.Resolve(SystemSchema.EmptySchemaId);

    private final String name;
    private final int numBitmaskBytes;
    private final int numFixed;
    private final int numVariable;
    private final HashMap<Utf8String, LayoutColumn> pathMap;
    private final HashMap<String, LayoutColumn> pathStringMap;
    private final SchemaId schemaId = new SchemaId();
    private final int size;
    private final StringTokenizer tokenizer;
    private final LayoutColumn[] topColumns;

    public Layout(String name, SchemaId schemaId, int numBitmaskBytes, int minRequiredSize, ArrayList<LayoutColumn> columns) {

        this.name = name;
        this.schemaId = schemaId.clone();
        this.numBitmaskBytes = numBitmaskBytes;
        this.size = minRequiredSize;
        this.tokenizer = new StringTokenizer();
        this.pathMap = new HashMap<Utf8String, LayoutColumn>(columns.size(), SamplingUtf8StringComparer.Default);
        this.pathStringMap = new HashMap<String, LayoutColumn>(columns.size());

        final ArrayList<LayoutColumn> top = new ArrayList<LayoutColumn>(columns.size());
        int numFixed = 0;
        int numVariable = 0;

        for (LayoutColumn c : columns) {

            this.tokenizer().add(c.getPath());
            this.pathMap.put(c.getFullPath(), c);
            this.pathStringMap.put(c.getFullPath().toString(), c);

            if (c.getStorage() == StorageKind.Fixed) {
                numFixed++;
            } else if (c.getStorage() == StorageKind.Variable) {
                numVariable++;
            }

            if (c.getParent() == null) {
                top.add(c);
            }
        }

        this.numFixed = numFixed;
        this.numVariable = numVariable;
        this.topColumns = top.toArray(new LayoutColumn[0]);
    }

    /**
     * Finds a column specification for a column with a matching path.
     *
     * @param path   The path of the column to find.
     * @param column If found, the column specification, otherwise {@code null}.
     * @return {@code true} if a column with the path is found, otherwise {@code false}.
     */
    public boolean TryFind(UtfAnyString path, Out<LayoutColumn> column) {

        if (path.isNull()) {
            column.setAndGet(null);
            return false;
        }

        if (path.isUtf8()) {
            return (this.pathMap.containsKey(path.toUtf8()) && (column.setAndGet(this.pathMap.get(path.toUtf8()))) == column.get());
        }

        String value = path.toUtf16();
        return (this.pathStringMap.containsKey(value) && (column.setAndGet(this.pathStringMap.get(value))) == column.get());
    }

    /**
     * Finds a column specification for a column with a matching path.
     *
     * @param path   The path of the column to find.
     * @param column If found, the column specification, otherwise null.
     * @return True if a column with the path is found, otherwise false.
     */
    public boolean TryFind(String path, Out<LayoutColumn> column) {
        return (this.pathStringMap.containsKey(path) && (column.setAndGet(this.pathStringMap.get(path))) == column.get());
    }

    /**
     * The set of top level columns defined in the layout (in left-to-right order).
     */
    public ReadOnlySpan<LayoutColumn> columns() {
        return this.topColumns.AsSpan();
    }

    /**
     * Name of the layout.
     * <p>
     * Usually this is the name of the {@link Schema} from which this {@link Layout} was generated.
     */
    public String name() {
        return this.name;
    }

    /**
     * The number of bit mask bytes allocated within the layout.
     * <p>
     * A presence bit is allocated for each fixed and variable-length field.  Sparse columns never have presence bits.
     * Fixed boolean allocate an additional bit from the bitmask to store their value.
     */
    public int numBitmaskBytes() {
        return this.numBitmaskBytes;
    }

    /**
     * The number of fixed columns.
     */
    public int numFixed() {
        return this.numFixed;
    }

    /**
     * The number of variable-length columns.
     */
    public int numVariable() {
        return this.numVariable;
    }

    /**
     * Unique identifier of the schema from which this {@link Layout} was generated.
     */
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * Minimum required size of a row of this layout.
     * <p>
     * This size excludes all sparse columns, and assumes all columns (including variable) are
     * null.
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns a human readable diagnostic string representation of this {@link Layout}
     * <p>
     * This representation should only be used for debugging and diagnostic purposes.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Layout:\n");
        sb.append(String.format("\tCount: %1$s\n", this.topColumns.length));
        sb.append(String.format("\tFixedSize: %1$s\n", this.size()));

        for (LayoutColumn c : this.topColumns) {
            if (c.getType().getIsFixed()) {
                if (c.getType().getIsBool()) {
                    sb.append(String.format("\t%1$s: %2$s @ %3$s:%4$s:%5$s\n", c.getFullPath(), c.getType().getName(), c.getOffset(), c.getNullBit().clone(), c.getBoolBit().clone()));
                } else {
                    sb.append(String.format("\t%1$s: %2$s @ %3$s\n", c.getFullPath(), c.getType().getName(), c.getOffset()));
                }
            } else {
                sb.append(String.format("\t%1$s: %2$s[%4$s] @ %3$s\n", c.getFullPath(), c.getType().getName(), c.getOffset(), c.getSize()));
            }
        }

        return sb.toString();
    }

    /**
     * A tokenizer for path strings.
     */
    public StringTokenizer tokenizer() {
        return this.tokenizer;
    }
}