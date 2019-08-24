//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import static com.google.common.base.Strings.lenientFormat;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{FullPath + \": \" + Type.Name + TypeArgs.ToString()}")] public sealed class
// LayoutColumn
public final class LayoutColumn {
    /**
     * For bool fields, 0-based index into the bit mask for the bool value.
     */
    private LayoutBit boolBit = new LayoutBit();
    /**
     * The full logical path of the field within the row.
     */
    private Utf8String fullPath;
    /**
     * 0-based index of the column within the structure.  Also indicates which presence bit
     * controls this column.
     */
    private int index;
    /**
     * For nullable fields, the 0-based index into the bit mask for the null bit.
     */
    private LayoutBit nullBit = new LayoutBit();
    /**
     * If {@link storage} equals {@link StorageKind.Fixed} then the byte offset to
     * the field location.
     * <para />
     * If {@link storage} equals {@link StorageKind.Variable} then the 0-based index of the
     * field from the beginning of the variable length segment.
     * <para />
     * For all other values of {@link storage}, {@link Offset} is ignored.
     */
    private int offset;
    /**
     * The layout of the parent scope, if a nested column, otherwise null.
     */
    private LayoutColumn parent;
    /**
     * The relative path of the field within its parent scope.
     */
    private Utf8String path;
    /**
     * If {@link LayoutType.IsBool} then the 0-based extra index within the bool byte
     * holding the value of this type, otherwise must be 0.
     */
    private int size;
    /**
     * The storage kind of the field.
     */
    private StorageKind storage = StorageKind.values()[0];
    /**
     * The physical layout type of the field.
     */
    private LayoutType type;
    /**
     * The physical layout type of the field.
     */
    private TypeArgument typeArg = new TypeArgument();
    /**
     * For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     */
    private TypeArgumentList typeArgs = new TypeArgumentList();

    /**
     * Initializes a new instance of the {@link LayoutColumn} class.
     *
     * @param path     The path to the field relative to parent scope.
     * @param type     Type of the field.
     * @param storage  Storage encoding of the field.
     * @param parent   The layout of the parent scope, if a nested column.
     * @param index    0-based column index.
     * @param offset   0-based Offset from beginning of serialization.
     * @param nullBit  0-based index into the bit mask for the null bit.
     * @param boolBit  For bool fields, 0-based index into the bit mask for the bool value.
     * @param length   For variable length types the length, otherwise 0.
     * @param typeArgs For types with generic parameters (e.g. {@link LayoutTuple}, the type
     *                 parameters.
     */

    public LayoutColumn(String path, LayoutType type, TypeArgumentList typeArgs, StorageKind storage,
                        LayoutColumn parent, int index, int offset, LayoutBit nullBit, LayoutBit boolBit) {
        this(path, type, typeArgs, storage, parent, index, offset, nullBit, boolBit, 0);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: internal LayoutColumn(string path, LayoutType type, TypeArgumentList typeArgs, StorageKind
    // storage, LayoutColumn parent, int index, int offset, LayoutBit nullBit, LayoutBit boolBit, int length = 0)
    public LayoutColumn(String path, LayoutType type, TypeArgumentList typeArgs, StorageKind storage,
                        LayoutColumn parent, int index, int offset, LayoutBit nullBit, LayoutBit boolBit, int length) {
        this.path = Utf8String.TranscodeUtf16(path);
        this.fullPath = Utf8String.TranscodeUtf16(LayoutColumn.GetFullPath(parent, path));
        this.type = type;
        this.typeArgs = typeArgs.clone();
        this.typeArg = new TypeArgument(type, typeArgs.clone());
        this.storage = storage;
        this.parent = parent;
        this.index = index;
        this.offset = offset;
        this.nullBit = nullBit.clone();
        this.boolBit = boolBit.clone();
        this.size = this.typeArg.getType().getIsFixed() ? type.Size : length;
    }

    /**
     * The full logical path of the field within the row.
     * <p>
     * Paths are expressed in dotted notation: e.g. a relative {@link Path} of 'b.c'
     * within the scope 'a' yields a {@link FullPath} of 'a.b.c'.
     */
    public Utf8String getFullPath() {
        return this.fullPath;
    }

    /**
     * 0-based index of the column within the structure.  Also indicates which presence bit
     * controls this column.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * The layout of the parent scope, if a nested column, otherwise null.
     */
    public LayoutColumn getParent() {
        return this.parent;
    }

    /**
     * The relative path of the field within its parent scope.
     * <p>
     * Paths are expressed in dotted notation: e.g. a relative {@link Path} of 'b.c'
     * within the scope 'a' yields a {@link FullPath} of 'a.b.c'.
     */
    public Utf8String getPath() {
        return this.path;
    }

    /**
     * The storage kind of the field.
     */
    public StorageKind getStorage() {
        return this.storage;
    }

    /**
     * The physical layout type of the field.
     */
    public LayoutType getType() {
        return this.type;
    }

    /**
     * The full logical type.
     */
    public TypeArgument getTypeArg() {
        return this.typeArg.clone();
    }

    /**
     * For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     */
    public TypeArgumentList getTypeArgs() {
        return this.typeArgs.clone();
    }

    public void SetIndex(int index) {
        this.index = index;
    }

    public void SetOffset(int offset) {
        this.offset = offset;
    }

    /**
     * The physical layout type of the field cast to the specified type.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerHidden] public T TypeAs<T>() where T : ILayoutType
    public <T extends ILayoutType> T TypeAs() {
        return this.type.TypeAs();
    }

    /**
     * For bool fields, 0-based index into the bit mask for the bool value.
     */
    LayoutBit getBoolBit()

    /**
     * For nullable fields, the the bit in the layout bitmask for the null bit.
     */
    LayoutBit getNullBit()

    /**
     * If {@link storage} equals {@link StorageKind.Fixed} then the byte offset to
     * the field location.
     * <para />
     * If {@link storage} equals {@link StorageKind.Variable} then the 0-based index of the
     * field from the beginning of the variable length segment.
     * <para />
     * For all other values of {@link storage}, {@link Offset} is ignored.
     */
    int getOffset()

    /**
     * If {@link storage} equals {@link StorageKind.Fixed} then the fixed number of
     * bytes reserved for the value.
     * <para />
     * If {@link storage} equals {@link StorageKind.Variable} then the maximum number of
     * bytes allowed for the value.
     */
    int getSize()

    /**
     * Computes the full logical path to the column.
     *
     * @param parent The layout of the parent scope, if a nested column, otherwise null.
     * @param path   The path to the field relative to parent scope.
     * @return The full logical path.
     */
    private static String GetFullPath(LayoutColumn parent, String path) {
        if (parent != null) {
            switch (LayoutCodeTraits.ClearImmutableBit(parent.type.LayoutCode)) {
                case ObjectScope:
                case Schema:
                    return parent.getFullPath().toString() + "." + path;
                case ArrayScope:
                case TypedArrayScope:
                case TypedSetScope:
                case TypedMapScope:
                    return parent.getFullPath().toString() + "[]" + path;
                default:
                    throw new IllegalStateException(lenientFormat("Parent scope type not supported: %s", parent.type.LayoutCode));
                    return null;
            }
        }

        return path;
    }
}