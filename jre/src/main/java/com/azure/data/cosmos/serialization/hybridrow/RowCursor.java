//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1307 // Accessible fields should begin with upper-case letter

// ReSharper disable InconsistentNaming

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringToken;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;

// ReSharper disable UseNameofExpression
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{ToString()}")] public struct RowCursor
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [DebuggerDisplay("{ToString()}")] public struct RowCursor
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
     * For sized scopes (e.g. Typed Array), the number of elements.
     */
    public int count;
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
     * If true, this scope's nested fields cannot be updated individually.
     * The entire scope can still be replaced.
     */
    public boolean immutable;
    /**
     * For indexed scopes (e.g. Array), the 0-based index into the scope of the sparse field.
     */
    public int index;
    /**
     * The layout describing the contents of the scope, or null if the scope is unschematized.
     */
    public Layout layout;
    /**
     * If existing, the offset to the metadata of the existing field, otherwise the location to
     * insert a new field.
     */
    public int metaOffset;
    /**
     * If existing, the offset scope relative path for reading.
     */
    public int pathOffset;
    /**
     * If existing, the layout string token of scope relative path for reading.
     */
    public int pathToken;
    /**
     * The kind of scope within which this edit was prepared.
     */
    public LayoutScope scopeType;
    /**
     * The type parameters of the scope within which this edit was prepared.
     */
    public TypeArgumentList scopeTypeArgs = new TypeArgumentList();
    /**
     * The 0-based byte offset from the beginning of the row where the first sparse field within
     * the scope begins.
     */
    public int start;
    /**
     * If existing, the offset to the value of the existing field, otherwise undefined.
     */
    public int valueOffset;
    /**
     * If existing, the scope relative path for writing.
     */
    public UtfAnyString writePath;
    /**
     * If WritePath is tokenized, then its token.
     */
    public StringToken writePathToken = new StringToken();

    public static RowCursor Create(Reference<RowBuffer> row) {
        SchemaId schemaId = row.get().ReadSchemaId(1).clone();
        Layout layout = row.get().getResolver().Resolve(schemaId.clone());
        int sparseSegmentOffset = row.get().ComputeVariableValueOffset(layout, HybridRowHeader.Size,
            layout.getNumVariable());
        RowCursor tempVar = new RowCursor();
        tempVar.layout = layout;
        tempVar.scopeType = LayoutType.UDT;
        tempVar.scopeTypeArgs = new TypeArgumentList(schemaId.clone());
        tempVar.start = HybridRowHeader.Size;
        tempVar.metaOffset = sparseSegmentOffset;
        tempVar.valueOffset = sparseSegmentOffset;
        return tempVar.clone();
    }

    // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
    //	public static ref RowCursor Create(ref RowBuffer row, out RowCursor cursor)
    //		{
    //			SchemaId schemaId = row.ReadSchemaId(1);
    //			Layout layout = row.Resolver.Resolve(schemaId);
    //			int sparseSegmentOffset = row.ComputeVariableValueOffset(layout, HybridRowHeader.Size, layout
    //			.NumVariable);
    //			cursor = new RowCursor { layout = layout, scopeType = LayoutType.UDT, scopeTypeArgs = new
    //			TypeArgumentList(schemaId), start = HybridRowHeader.Size, metaOffset = sparseSegmentOffset,
    //			valueOffset = sparseSegmentOffset};
    //
    //			return ref cursor;
    //		}

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

    public RowCursor clone() {
        RowCursor varCopy = new RowCursor();

        varCopy.layout = this.layout;
        varCopy.scopeType = this.scopeType;
        varCopy.scopeTypeArgs = this.scopeTypeArgs.clone();
        varCopy.immutable = this.immutable;
        varCopy.deferUniqueIndex = this.deferUniqueIndex;
        varCopy.start = this.start;
        varCopy.exists = this.exists;
        varCopy.writePath = this.writePath;
        varCopy.writePathToken = this.writePathToken.clone();
        varCopy.pathOffset = this.pathOffset;
        varCopy.pathToken = this.pathToken;
        varCopy.metaOffset = this.metaOffset;
        varCopy.cellType = this.cellType;
        varCopy.valueOffset = this.valueOffset;
        varCopy.endOffset = this.endOffset;
        varCopy.count = this.count;
        varCopy.index = this.index;
        varCopy.cellTypeArgs = this.cellTypeArgs.clone();

        return varCopy;
    }

    @Override
    public String toString() {
        try {
            if (this.scopeType == null) {
                return "<Invalid>";
            }

            TypeArgument scopeTypeArg = (this.scopeType == null) || (this.scopeType instanceof LayoutEndScope) ?
            default:
                new TypeArgument(this.scopeType, this.scopeTypeArgs.clone());

                TypeArgument typeArg = (this.cellType == null) || (this.cellType instanceof LayoutEndScope) ?
            default:
                new TypeArgument(this.cellType, this.cellTypeArgs.clone());

                String pathOrIndex = !this.writePath.IsNull ? this.writePath.toString() : String.valueOf(this.index);
                return String.format("%1$s[%2$s] : %3$s@%4$s/%5$s", scopeTypeArg.clone(), pathOrIndex,
                    typeArg.clone(), this.metaOffset, this.valueOffset) + (this.immutable ? " immutable" : "");
        } catch (java.lang.Exception e) {
            return "<???>";
        }
    }

    /**
     * If true, this scope's nested fields cannot be updated individually.
     * The entire scope can still be replaced.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerBrowsable(DebuggerBrowsableState.Never)] public bool Immutable
    boolean getImmutable()

    /**
     * For indexed scopes (e.g. Array), the 0-based index into the scope of the next insertion.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerBrowsable(DebuggerBrowsableState.Never)] public int Index
    int getIndex()

    /**
     * The layout describing the contents of the scope, or null if the scope is unschematized.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerBrowsable(DebuggerBrowsableState.Never)] public Layout Layout
    Layout getLayout()

    /**
     * The kind of scope.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerBrowsable(DebuggerBrowsableState.Never)] public LayoutType ScopeType
    LayoutType getScopeType()

    /**
     * For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerBrowsable(DebuggerBrowsableState.Never)] public TypeArgumentList ScopeTypeArgs
    TypeArgumentList getScopeTypeArgs()

    /**
     * The full logical type.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerBrowsable(DebuggerBrowsableState.Never)] public TypeArgument TypeArg
    TypeArgument getTypeArg()
}