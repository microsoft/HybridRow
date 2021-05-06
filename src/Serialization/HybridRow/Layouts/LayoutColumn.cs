// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System.Diagnostics;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    [DebuggerDisplay("{FullPath + \": \" + Type.Name + TypeArgs.ToString()}")]
    public sealed class LayoutColumn
    {
        /// <summary>
        /// If <see cref="LayoutType.IsBool" /> then the 0-based extra index within the bool byte
        /// holding the value of this type, otherwise must be 0.
        /// </summary>
        private readonly int size;

        /// <summary>The relative path of the field within its parent scope.</summary>
        private readonly Utf8String path;

        /// <summary>The full logical path of the field within the row.</summary>
        private readonly Utf8String fullPath;

        /// <summary>The physical layout type of the field.</summary>
        private readonly LayoutType type;

        /// <summary>The physical layout type of the field.</summary>
        private readonly TypeArgument typeArg;

        /// <summary>The storage kind of the field.</summary>
        private readonly StorageKind storage;

        /// <summary>The layout of the parent scope, if a nested column, otherwise null.</summary>
        private readonly LayoutColumn parent;

        /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
        private readonly TypeArgumentList typeArgs;

        /// <summary>For nullable fields, the 0-based index into the bit mask for the null bit.</summary>
        private readonly LayoutBit nullBit;

        /// <summary>For bool fields, 0-based index into the bit mask for the bool value.</summary>
        private readonly LayoutBit boolBit;

        /// <summary>
        /// 0-based index of the column within the structure.  Also indicates which presence bit
        /// controls this column.
        /// </summary>
        private int index;

        /// <summary>
        /// If <see cref="storage" /> equals <see cref="StorageKind.Fixed" /> then the byte offset to
        /// the field location.
        /// <para />
        /// If <see cref="storage" /> equals <see cref="StorageKind.Variable" /> then the 0-based index of the
        /// field from the beginning of the variable length segment.
        /// <para />
        /// For all other values of <see cref="storage" />, <see cref="Offset" /> is ignored.
        /// </summary>
        private int offset;

        /// <summary>Initializes a new instance of the <see cref="LayoutColumn" /> class.</summary>
        /// <param name="path">The path to the field relative to parent scope.</param>
        /// <param name="type">Type of the field.</param>
        /// <param name="storage">Storage encoding of the field.</param>
        /// <param name="parent">The layout of the parent scope, if a nested column.</param>
        /// <param name="index">0-based column index.</param>
        /// <param name="offset">0-based Offset from beginning of serialization.</param>
        /// <param name="nullBit">0-based index into the bit mask for the null bit.</param>
        /// <param name="boolBit">For bool fields, 0-based index into the bit mask for the bool value.</param>
        /// <param name="length">For variable length types the length, otherwise 0.</param>
        /// <param name="typeArgs">
        /// For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type
        /// parameters.
        /// </param>
        internal LayoutColumn(
            string path,
            LayoutType type,
            TypeArgumentList typeArgs,
            StorageKind storage,
            LayoutColumn parent,
            int index,
            int offset,
            LayoutBit nullBit,
            LayoutBit boolBit,
            int length = 0)
        {
            this.path = Utf8String.TranscodeUtf16(path);
            this.fullPath = Utf8String.TranscodeUtf16(LayoutColumn.GetFullPath(parent, path));
            this.type = type;
            this.typeArgs = typeArgs;
            this.typeArg = new TypeArgument(type, typeArgs);
            this.storage = storage;
            this.parent = parent;
            this.index = index;
            this.offset = offset;
            this.nullBit = nullBit;
            this.boolBit = boolBit;
            this.size = this.typeArg.Type.IsFixed ? type.Size : length;
        }

        /// <summary>The relative path of the field within its parent scope.</summary>
        /// <remarks>
        /// Paths are expressed in dotted notation: e.g. a relative <see cref="Path" /> of 'b.c'
        /// within the scope 'a' yields a <see cref="FullPath" /> of 'a.b.c'.
        /// </remarks>
        public Utf8String Path => this.path;

        /// <summary>The full logical path of the field within the row.</summary>
        /// <remarks>
        /// Paths are expressed in dotted notation: e.g. a relative <see cref="Path" /> of 'b.c'
        /// within the scope 'a' yields a <see cref="FullPath" /> of 'a.b.c'.
        /// </remarks>
        public Utf8String FullPath => this.fullPath;

        /// <summary>The physical layout type of the field.</summary>
        public LayoutType Type => this.type;

        /// <summary>The storage kind of the field.</summary>
        public StorageKind Storage => this.storage;

        /// <summary>The layout of the parent scope, if a nested column, otherwise null.</summary>
        public LayoutColumn Parent => this.parent;

        /// <summary>The full logical type.</summary>
        public TypeArgument TypeArg => this.typeArg;

        /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
        public TypeArgumentList TypeArgs => this.typeArgs;

        /// <summary>
        /// 0-based index of the column within the structure.  Also indicates which presence bit
        /// controls this column.
        /// </summary>
        public int Index => this.index;

        /// <summary>
        /// If <see cref="storage" /> equals <see cref="StorageKind.Fixed" /> then the byte offset to
        /// the field location.
        /// <para />
        /// If <see cref="storage" /> equals <see cref="StorageKind.Variable" /> then the 0-based index of the
        /// field from the beginning of the variable length segment.
        /// <para />
        /// For all other values of <see cref="storage" />, <see cref="Offset" /> is ignored.
        /// </summary>
        public int Offset
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.offset;
        }

        /// <summary>For nullable fields, the the bit in the layout bitmask for the null bit.</summary>
        public LayoutBit NullBit
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.nullBit;
        }

        /// <summary>For bool fields, 0-based index into the bit mask for the bool value.</summary>
        public LayoutBit BoolBit
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.boolBit;
        }

        /// <summary>
        /// If <see cref="storage" /> equals <see cref="StorageKind.Fixed" /> then the fixed number of
        /// bytes reserved for the value.
        /// <para />
        /// If <see cref="storage" /> equals <see cref="StorageKind.Variable" /> then the maximum number of
        /// bytes allowed for the value.
        /// </summary>
        public int Size
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.size;
        }

        /// <summary>The physical layout type of the field cast to the specified type.</summary>
        [DebuggerHidden]
        public T TypeAs<T>()
            where T : ILayoutType
        {
            return this.type.TypeAs<T>();
        }

        internal void SetIndex(int index)
        {
            this.index = index;
        }

        internal void SetOffset(int offset)
        {
            this.offset = offset;
        }

        /// <summary>Computes the full logical path to the column.</summary>
        /// <param name="parent">The layout of the parent scope, if a nested column, otherwise null.</param>
        /// <param name="path">The path to the field relative to parent scope.</param>
        /// <returns>The full logical path.</returns>
        private static string GetFullPath(LayoutColumn parent, string path)
        {
            if (parent != null)
            {
                switch (LayoutCodeTraits.ClearImmutableBit(parent.type.LayoutCode))
                {
                    case LayoutCode.ObjectScope:
                    case LayoutCode.Schema:
                        return parent.FullPath.ToString() + "." + path;
                    case LayoutCode.ArrayScope:
                    case LayoutCode.TypedArrayScope:
                    case LayoutCode.TypedSetScope:
                    case LayoutCode.TypedMapScope:
                        return parent.FullPath.ToString() + "[]" + path;
                    default:
                        Contract.Fail($"Parent scope type not supported: {parent.type.LayoutCode}");
                        return default;
                }
            }

            return path;
        }
    }
}
