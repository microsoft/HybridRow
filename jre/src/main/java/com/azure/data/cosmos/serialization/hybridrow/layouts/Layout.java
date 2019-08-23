//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Layout describes the structure of a Hybrd Row.
 * <p>
 * A layout indicates the number, order, and type of all schematized columns to be stored
 * within a hybrid row.  The order and type of columns defines the physical ordering of bytes used to
 * encode the row and impacts the cost of updating the row.
 * <para />
 * A layout is created by compiling a <see cref="Schema" /> through <see cref="Schema.Compile" /> or
 * by constructor through a <see cref="LayoutBuilder" />.
 * <para />
 * <see cref="Layout" /> is immutable.
 */
public final class Layout {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly Layout Empty = SystemSchema.LayoutResolver
    // .Resolve(SystemSchema.EmptySchemaId);
    public static final Layout Empty = SystemSchema.LayoutResolver.Resolve(SystemSchema.EmptySchemaId);
    /**
     * Name of the layout.
     * <p>
     * Usually this is the name of the <see cref="Schema" /> from which this
     * <see cref="Layout" /> was generated.
     */
    private String Name;
    /**
     * The number of bitmask bytes allocated within the layout.
     * <p>
     * A presence bit is allocated for each fixed and variable-length field.  Sparse columns
     * never have presence bits.  Fixed boolean allocate an additional bit from the bitmask to store their
     * value.
     */
    private int NumBitmaskBytes;
    /**
     * The number of fixed columns.
     */
    private int NumFixed;
    /**
     * The number of variable-length columns.
     */
    private int NumVariable;
    /**
     * Unique identifier of the schema from which this <see cref="Layout" /> was generated.
     */
    private com.azure.data.cosmos.serialization.hybridrow.SchemaId SchemaId = new SchemaId();
    /**
     * Minimum required size of a row of this layout.
     * <p>
     * This size excludes all sparse columns, and assumes all columns (including variable) are
     * null.
     */
    private int Size;
    private HashMap<Utf8String, LayoutColumn> pathMap;
    private HashMap<String, LayoutColumn> pathStringMap;
    private LayoutColumn[] topColumns;

    public Layout(String name, SchemaId schemaId, int numBitmaskBytes, int minRequiredSize,
                  ArrayList<LayoutColumn> columns) {
        this.Name = name;
        this.SchemaId = schemaId.clone();
        this.NumBitmaskBytes = numBitmaskBytes;
        this.Size = minRequiredSize;
        this.Tokenizer = new StringTokenizer();
        this.pathMap = new HashMap<Utf8String, LayoutColumn>(columns.size(), SamplingUtf8StringComparer.Default);
        this.pathStringMap = new HashMap<String, LayoutColumn>(columns.size());
        this.NumFixed = 0;
        this.NumVariable = 0;

        ArrayList<LayoutColumn> top = new ArrayList<LayoutColumn>(columns.size());
        for (LayoutColumn c : columns) {
            this.getTokenizer().Add(c.getPath());
            this.pathMap.put(c.getFullPath(), c);
            this.pathStringMap.put(c.getFullPath().toString(), c);
            if (c.getStorage() == StorageKind.Fixed) {
                this.NumFixed = this.getNumFixed() + 1;
            } else if (c.getStorage() == StorageKind.Variable) {
                this.NumVariable = this.getNumVariable() + 1;
            }

            if (c.getParent() == null) {
                top.add(c);
            }
        }

        this.topColumns = top.toArray(new LayoutColumn[0]);
    }

    /**
     * The set of top level columns defined in the layout (in left-to-right order).
     */
    public ReadOnlySpan<LayoutColumn> getColumns() {
        return this.topColumns.AsSpan();
    }

    public String getName() {
        return Name;
    }

    public int getNumBitmaskBytes() {
        return NumBitmaskBytes;
    }

    public int getNumFixed() {
        return NumFixed;
    }

    public int getNumVariable() {
        return NumVariable;
    }

    public SchemaId getSchemaId() {
        return SchemaId;
    }

    public int getSize() {
        return Size;
    }

    /**
     * Finds a column specification for a column with a matching path.
     *
     * @param path   The path of the column to find.
     * @param column If found, the column specification, otherwise null.
     * @return True if a column with the path is found, otherwise false.
     */
    public boolean TryFind(UtfAnyString path, OutObject<LayoutColumn> column) {
        if (path.IsNull) {
            column.set(null);
            return false;
        }

        if (path.IsUtf8) {
            return (this.pathMap.containsKey(path.ToUtf8String()) && (column.set(this.pathMap.get(path.ToUtf8String()))) == column.get());
        }

        return (this.pathStringMap.containsKey(path) && (column.set(this.pathStringMap.get(path))) == column.get());
    }

    /**
     * Finds a column specification for a column with a matching path.
     *
     * @param path   The path of the column to find.
     * @param column If found, the column specification, otherwise null.
     * @return True if a column with the path is found, otherwise false.
     */
    public boolean TryFind(String path, OutObject<LayoutColumn> column) {
        return (this.pathStringMap.containsKey(path) && (column.set(this.pathStringMap.get(path))) == column.get());
    }

    /**
     * Returns a human readable diagnostic string representation of this <see cref="Layout" />.
     * This representation should only be used for debugging and diagnostic purposes.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Layout:\n");
        sb.append(String.format("\tCount: %1$s\n", this.topColumns.length));
        sb.append(String.format("\tFixedSize: %1$s\n", this.getSize()));
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
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Performance", "CA1822:MarkMembersAsStatic", Justification = "Bug in
    // Analyzer. This is an auto-property not a method.")] public StringTokenizer Tokenizer
    private StringTokenizer getTokenizer() {
    }
}