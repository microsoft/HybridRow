// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypeScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypes;
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringToken;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;

import static com.google.common.base.Strings.lenientFormat;

public final class RowCursor implements Cloneable {

    private LayoutType cellType;
    private TypeArgumentList cellTypeArgs;
    private int count;
    private boolean deferUniqueIndex;
    private int endOffset;
    private boolean exists;
    private boolean immutable;
    private int index;
    private Layout layout;
    private int metaOffset;
    private int pathOffset;
    private int pathToken;
    private LayoutTypeScope scopeType;
    private TypeArgumentList scopeTypeArgs;
    private int start;
    private int valueOffset;
    private UtfAnyString writePath;
    private StringToken writePathToken;

    RowCursor() {
    }

    /**
     * If existing, the layout code of the existing field, otherwise undefined.
     *
     * @return layout code.
     */
    public LayoutType cellType() {
        return this.cellType;
    }

    /**
     * Sets the layout type of an existing field.
     *
     * @param value a {@link LayoutType}.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor cellType(LayoutType value) {
        this.cellType = value;
        return this;
    }

    /**
     * For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     *
     * @return a {@link TypeArgumentList} or {@code null}.
     */
    public TypeArgumentList cellTypeArgs() {
        return this.cellTypeArgs;
    }

    /**
     * Sets the layout type arguments of an existing field.
     *
     * @param value a {@link TypeArgumentList} or {@code null}.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor cellTypeArgs(TypeArgumentList value) {
        this.cellTypeArgs = value;
        return this;
    }

    public RowCursor clone() {
        try {
            return (RowCursor) super.clone();
        } catch (CloneNotSupportedException error) {
            throw new IllegalStateException(error);
        }
    }

    /**
     * For sized scopes (e.g. Typed Array), the number of elements.
     *
     * @return the number of elements or zero.
     */
    public int count() {
        return this.count;
    }

    /**
     * Sets the number of elements for a sized scope.
     *
     * @param count the number of elements for a sized scope.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor count(int count) {
        this.count = count;
        return this;
    }

    public static RowCursor create(RowBuffer row) {

        final SchemaId schemaId = row.readSchemaId(1);
        final Layout layout = row.resolver().resolve(schemaId);
        final int offset = row.computeVariableValueOffset(layout, HybridRowHeader.BYTES, layout.numVariable());

        return new RowCursor()
            .layout(layout)
            .scopeType(LayoutTypes.UDT)
            .scopeTypeArgs(new TypeArgumentList(schemaId))
            .start(HybridRowHeader.BYTES)
            .metaOffset(offset)
            .valueOffset(offset);
    }

    public static RowCursor createForAppend(RowBuffer row) {

        final SchemaId schemaId = row.readSchemaId(1);
        final Layout layout = row.resolver().resolve(schemaId);

        return new RowCursor()
            .layout(layout)
            .scopeType(LayoutTypes.UDT)
            .scopeTypeArgs(new TypeArgumentList(schemaId))
            .start(HybridRowHeader.BYTES)
            .metaOffset(row.length())
            .valueOffset(row.length());
    }

    /**
     * If true, this scope is a unique index scope whose index will be built after its items are written.
     *
     * @return {@code true}, if this cursor identifies a unique index scope, otherwise {@code false}.
     */
    public boolean deferUniqueIndex() {
        return this.deferUniqueIndex;
    }

    /**
     * Sets a value that indicates whether this cursor identifies a unique index scope.
     *
     * @param value {@code true}, if this cursor identifies a unique index scope, otherwise {@code false}.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor deferUniqueIndex(boolean value) {
        this.deferUniqueIndex = value;
        return this;
    }

    /**
     * If existing, the offset to the end of the existing field.
     * <p>
     * This value is used as a hint when skipping forward.
     *
     * @return offset of the end of an existing field.
     */
    public int endOffset() {
        return this.endOffset;
    }

    /**
     * Sets a value that indicates whether this cursor identifies a unique index scope.
     *
     * @param value {@code true}, if this cursor identifies a unique index scope, otherwise {@code false}.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor endOffset(int value) {
        this.endOffset = value;
        return this;
    }

    /**
     * {@code true} if an existing field matching the search criteria was found.
     *
     * @return {@code true} if an existing field matching the search criteria was found, otherwise {@code false}.
     */
    public boolean exists() {
        return this.exists;
    }

    /**
     * Sets a value that indicates whether this cursor identifies a field matching search criteria.
     *
     * @param value {@code true}, if this cursor identifies a field matching search criteria, otherwise {@code false}.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor exists(boolean value) {
        this.exists = value;
        return this;
    }

    /**
     * If {@code true}, this scope's nested fields cannot be updated individually.
     * <p>
     * The entire scope can still be replaced.
     *
     * @return {@code true} if this scope's nested fields cannot be updated individually, otherwise {@code false}.
     */
    public boolean immutable() {
        return this.immutable;
    }

    /**
     * Sets a flag indicated whether this scope's nested fields cannot be updated individually.
     * <p>
     * The entire scope can still be replaced.
     *
     * @param value {@code true} if this scope's nested fields cannot be updated individually, otherwise {@code false}.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor immutable(boolean value) {
        this.immutable = value;
        return this;
    }

    /**
     * For indexed scopes (e.g. an Array scope), the zero-based index into the scope of the sparse field.
     *
     * @return the zero-based index into the scope of the sparse field.
     */
    public int index() {
        return this.index;
    }

    /**
     * Sets the zero-based index into the scope of a sparse field in an indexed scope (e.g. an Array scope).
     *
     * @param value the zero-based index into the scope of the sparse field.
     * @return a reference to this {@link RowCursor}.
     */
    public RowCursor index(int value) {
        this.index = value;
        return this;
    }

    /**
     * The layout describing the contents of the scope, or {@code null} if the scope is unschematized.
     *
     * @return layout describing the context of the scope, or {@code null} if the scope is unschematized.
     */
    public Layout layout() {
        return this.layout;
    }

    public RowCursor layout(Layout value) {
        this.layout = value;
        return this;
    }

    /**
     * If existing, offset to the metadata of the existing field, otherwise the location to insert a new field.
     *
     * @return offset to the metadata of an existing field or the location to insert a new field.
     */
    public int metaOffset() {
        return this.metaOffset;
    }

    public RowCursor metaOffset(final int value) {
        this.metaOffset = value;
        return this;
    }

    /**
     * If existing, the offset scope relative path for reading.
     *
     * @return If existing, the offset scope relative path for reading.
     */
    public int pathOffset() {
        return this.pathOffset;
    }

    public RowCursor pathOffset(final int value) {
        this.pathOffset = value;
        return this;
    }

    /**
     * If existing, the layout string token of scope relative path for reading.
     *
     * @return If existing, the layout string token of scope relative path for reading.
     */
    public int pathToken() {
        return this.pathToken;
    }

    public RowCursor pathToken(int value) {
        this.pathToken = value;
        return this;
    }

    /**
     * The kind of scope within which this edit was prepared.
     *
     * @return The kind of scope within which this edit was prepared.
     */
    public LayoutTypeScope scopeType() {
        return this.scopeType;
    }

    public RowCursor scopeType(LayoutTypeScope scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    /**
     * The type parameters of the scope within which this edit was prepared.
     *
     * @return The type parameters of the scope within which this edit was prepared.
     */
    public TypeArgumentList scopeTypeArgs() {
        return this.scopeTypeArgs;
    }

    public RowCursor scopeTypeArgs(TypeArgumentList scopeTypeArgs) {
        this.scopeTypeArgs = scopeTypeArgs;
        return this;
    }

    /**
     * The 0-based offset from the beginning of the row where the first sparse field within the scope begins.
     *
     * @return 0-based offset from the beginning of the row where the first sparse field within the scope begins.
     */
    public int start() {
        return this.start;
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
                ? TypeArgument.NONE
                : new TypeArgument(this.scopeType(), this.scopeTypeArgs());

            TypeArgument typeArg = (this.cellType() == null) || (this.cellType() instanceof LayoutEndScope)
                ? TypeArgument.NONE
                : new TypeArgument(this.cellType(), this.cellTypeArgs());

            String pathOrIndex = this.writePath().isNull() ? String.valueOf(this.index()) : this.writePath().toString();

            return lenientFormat("%s[%s] : %s@%s/%s%s",
                scopeTypeArg,
                pathOrIndex,
                typeArg,
                this.metaOffset(),
                this.valueOffset(),
                this.immutable() ? " immutable" : "");

        } catch (Exception ignored) {
            return "<???>";
        }
    }

    /**
     * If existing, the offset to the value of the existing field, otherwise undefined.
     *
     * @return If existing, the offset to the value of the existing field, otherwise undefined.
     */
    public int valueOffset() {
        return this.valueOffset;
    }

    public RowCursor valueOffset(int valueOffset) {
        this.valueOffset = valueOffset;
        return this;
    }

    /**
     * If existing, the scope relative path for writing.
     *
     * @return If existing, the scope relative path for writing.
     */
    public UtfAnyString writePath() {
        return this.writePath;
    }

    public void writePath(UtfAnyString writePath) {
        this.writePath = writePath;
    }

    /**
     * If {@link #writePath} is tokenized, then its token.
     *
     * @return if {@link #writePath} is tokenized, then its token.
     */
    public StringToken writePathToken() {
        return this.writePathToken;
    }

    public void writePathToken(StringToken writePathToken) {
        this.writePathToken = writePathToken;
    }
}