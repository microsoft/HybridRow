//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringToken;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;

import static com.google.common.base.Strings.lenientFormat;

public final class RowCursor {

    /**
     * If existing, the layout code of the existing field, otherwise undefined.
     */
    public LayoutType cellType;
    /**
     * For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     */
    public TypeArgumentList cellTypeArgs = new TypeArgumentList();
    /**
     * If true, this scope is an unique index scope whose index will be built after its items are written.
     */
    public boolean deferUniqueIndex;
    /**
     * If existing, the offset to the end of the existing field. Used as a hint when skipping
     * forward.
     */
    public int endOffset;
    /**
     * True if an existing field matching the search criteria was found.
     */
    public boolean exists;
    /**
     * If existing, the offset scope relative path for reading.
     */
    public int pathOffset;
    /**
     * If existing, the layout string token of scope relative path for reading.
     */
    public int pathToken;
    private int count;
    private boolean immutable;
    private int index;
    private Layout layout;
    private int metaOffset;
    private LayoutScope scopeType;
    private TypeArgumentList scopeTypeArgs = new TypeArgumentList();
    private int start;
    private int valueOffset;
    private UtfAnyString writePath;
    private StringToken writePathToken = new StringToken();

    public static RowCursor Create(Reference<RowBuffer> row) {

        final SchemaId schemaId = row.get().ReadSchemaId(1);
        final Layout layout = row.get().resolver().Resolve(schemaId);
        final int sparseSegmentOffset = row.get().computeVariableValueOffset(layout, HybridRowHeader.SIZE,
            layout.numVariable());

        final RowCursor cursor = new RowCursor()
            .layout(layout)
            .scopeType(LayoutType.UDT)
            .scopeTypeArgs(new TypeArgumentList(schemaId))
            .start(HybridRowHeader.SIZE)
            .metaOffset(sparseSegmentOffset)
            .valueOffset(sparseSegmentOffset);

        return cursor;
    }

    public static RowCursor Create(RowBuffer row) {

        final SchemaId schemaId = row.ReadSchemaId(1);
        final Layout layout = row.Resolver.Resolve(schemaId);
        final int sparseSegmentOffset = row.computeVariableValueOffset(layout, HybridRowHeader.Size,
            layout.NumVariable);

        return new RowCursor()
            .layout(layout)
            .scopeType(LayoutType.UDT)
            .scopeTypeArgs(new TypeArgumentList(schemaId, HybridRowHeader.SIZE, sparseSegmentOffset, sparseSegmentOffset);
    }

    /**
     * For sized scopes (e.g. Typed Array), the number of elements.
     */
    public int count() {
        return count;
    }

    public RowCursor count(int count) {
        this.count = count;
        return this;
    }

    // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
    //	public static ref RowCursor CreateForAppend(ref RowBuffer row, out RowCursor cursor)
    //		{
    //			SchemaId schemaId = row.ReadSchemaId(1);
    //			Layout layout = row.Resolver.Resolve(schemaId);
    //			cursor = new RowCursor { layout = layout, scopeType = LayoutType.UDT, scopeTypeArgs = new
    //			TypeArgumentList(schemaId), start = HybridRowHeader.Size, metaOffset = row.Length, valueOffset = row
    //			.Length};
    //
    //			return ref cursor;
    //		}

    /**
     * If {@code true}, this scope's nested fields cannot be updated individually
     * <p>
     * The entire scope can still be replaced.
     */
    public boolean immutable() {
        return immutable;
    }

    public RowCursor immutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /**
     * For indexed scopes (e.g. an Array scope), the zero-based index into the scope of the sparse field
     */
    public int index() {
        return index;
    }

    public RowCursor index(int index) {
        this.index = index;
        return this;
    }

    /**
     * The layout describing the contents of the scope, or {@code null} if the scope is unschematized.
     */
    public Layout layout() {
        return layout;
    }

    public RowCursor layout(Layout layout) {
        this.layout = layout;
        return this;
    }

    /**
     * If existing, the offset to the metadata of the existing field, otherwise the location to
     * insert a new field.
     */
    public int metaOffset() {
        return metaOffset;
    }

    public RowCursor metaOffset(int metaOffset) {
        this.metaOffset = metaOffset;
        return this;
    }

    /**
     * The kind of scope within which this edit was prepared
     */
    public LayoutScope scopeType() {
        return scopeType;
    }

    public RowCursor scopeType(LayoutScope scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    /**
     * The type parameters of the scope within which this edit was prepared
     */
    public TypeArgumentList scopeTypeArgs() {
        return scopeTypeArgs;
    }

    public RowCursor scopeTypeArgs(TypeArgumentList scopeTypeArgs) {
        this.scopeTypeArgs = scopeTypeArgs;
        return this;
    }

    /**
     * The 0-based byte offset from the beginning of the row where the first sparse field within
     * the scope begins.
     */
    public int start() {
        return start;
    }

    public RowCursor start(int start) {
        this.start = start;
        return this;
    }

    @Override
    public String toString() {

        try {

            if (this.scopeType() == null) {
                return "<Invalid>";
            }

            TypeArgument scopeTypeArg = (this.scopeType() instanceof LayoutEndScope)
                ? new TypeArgument()
                : new TypeArgument(this.scopeType(), this.scopeTypeArgs().clone());

            TypeArgument typeArg = (this.cellType == null) || (this.cellType instanceof LayoutEndScope)
                ? new TypeArgument()
                : new TypeArgument(this.cellType, this.cellTypeArgs.clone());

            String pathOrIndex = this.writePath().isNull() ? String.valueOf(this.index()) : this.writePath().toString();

            return lenientFormat("%s[%s] : %s@%s/%s%s",
                scopeTypeArg.clone(),
                pathOrIndex,
                typeArg.clone(),
                this.metaOffset(),
                this.valueOffset(),
                this.immutable() ? " immutable" : "");

        } catch (Exception ignored) {
            return "<???>";
        }
    }

    /**
     * If existing, the offset to the value of the existing field, otherwise undefined.
     */
    public int valueOffset() {
        return valueOffset;
    }

    public RowCursor valueOffset(int valueOffset) {
        this.valueOffset = valueOffset;
        return this;
    }

    /**
     * If existing, the scope relative path for writing.
     */
    public UtfAnyString writePath() {
        return writePath;
    }

    public void writePath(UtfAnyString writePath) {
        this.writePath = writePath;
    }

    /**
     * If WritePath is tokenized, then its token.
     */
    public StringToken writePathToken() {
        return writePathToken;
    }

    public void writePathToken(StringToken writePathToken) {
        this.writePathToken = writePathToken;
    }
}