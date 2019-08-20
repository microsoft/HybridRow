// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1307 // Accessible fields should begin with upper-case letter

// ReSharper disable InconsistentNaming
namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System.Diagnostics;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    // ReSharper disable UseNameofExpression
    [DebuggerDisplay("{ToString()}")]
    public struct RowCursor
    {
        /// <summary>The layout describing the contents of the scope, or null if the scope is unschematized.</summary>
        internal Layout layout;

        /// <summary>The kind of scope within which this edit was prepared.</summary>
        internal LayoutScope scopeType;

        /// <summary>The type parameters of the scope within which this edit was prepared.</summary>
        internal TypeArgumentList scopeTypeArgs;

        /// <summary>If true, this scope's nested fields cannot be updated individually.</summary>
        /// <remarks>The entire scope can still be replaced.</remarks>
        internal bool immutable;

        /// <summary>If true, this scope is an unique index scope whose index will be built after its items are written.</remarks>
        internal bool deferUniqueIndex;

        /// <summary>
        /// The 0-based byte offset from the beginning of the row where the first sparse field within
        /// the scope begins.
        /// </summary>
        internal int start;

        /// <summary>True if an existing field matching the search criteria was found.</summary>
        internal bool exists;

        /// <summary>If existing, the scope relative path for writing.</summary>
        internal UtfAnyString writePath;

        /// <summary>If WritePath is tokenized, then its token.</summary>
        internal StringToken writePathToken;

        /// <summary>If existing, the offset scope relative path for reading.</summary>
        internal int pathOffset;

        /// <summary>If existing, the layout string token of scope relative path for reading.</summary>
        internal int pathToken;

        /// <summary>
        /// If existing, the offset to the metadata of the existing field, otherwise the location to
        /// insert a new field.
        /// </summary>
        internal int metaOffset;

        /// <summary>If existing, the layout code of the existing field, otherwise undefined.</summary>
        internal LayoutType cellType;

        /// <summary>If existing, the offset to the value of the existing field, otherwise undefined.</summary>
        internal int valueOffset;

        /// <summary>
        /// If existing, the offset to the end of the existing field. Used as a hint when skipping
        /// forward.
        /// </summary>
        internal int endOffset;

        /// <summary>For sized scopes (e.g. Typed Array), the number of elements.</summary>
        internal int count;

        /// <summary>For indexed scopes (e.g. Array), the 0-based index into the scope of the sparse field.</summary>
        internal int index;

        /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
        internal TypeArgumentList cellTypeArgs;

        public static RowCursor Create(ref RowBuffer row)
        {
            SchemaId schemaId = row.ReadSchemaId(1);
            Layout layout = row.Resolver.Resolve(schemaId);
            int sparseSegmentOffset = row.ComputeVariableValueOffset(layout, HybridRowHeader.Size, layout.NumVariable);
            return new RowCursor
            {
                layout = layout,
                scopeType = LayoutType.UDT,
                scopeTypeArgs = new TypeArgumentList(schemaId),
                start = HybridRowHeader.Size,
                metaOffset = sparseSegmentOffset,
                valueOffset = sparseSegmentOffset,
            };
        }

        public static ref RowCursor Create(ref RowBuffer row, out RowCursor cursor)
        {
            SchemaId schemaId = row.ReadSchemaId(1);
            Layout layout = row.Resolver.Resolve(schemaId);
            int sparseSegmentOffset = row.ComputeVariableValueOffset(layout, HybridRowHeader.Size, layout.NumVariable);
            cursor = new RowCursor
            {
                layout = layout,
                scopeType = LayoutType.UDT,
                scopeTypeArgs = new TypeArgumentList(schemaId),
                start = HybridRowHeader.Size,
                metaOffset = sparseSegmentOffset,
                valueOffset = sparseSegmentOffset,
            };

            return ref cursor;
        }

        public static ref RowCursor CreateForAppend(ref RowBuffer row, out RowCursor cursor)
        {
            SchemaId schemaId = row.ReadSchemaId(1);
            Layout layout = row.Resolver.Resolve(schemaId);
            cursor = new RowCursor
            {
                layout = layout,
                scopeType = LayoutType.UDT,
                scopeTypeArgs = new TypeArgumentList(schemaId),
                start = HybridRowHeader.Size,
                metaOffset = row.Length,
                valueOffset = row.Length,
            };

            return ref cursor;
        }

        /// <summary>For indexed scopes (e.g. Array), the 0-based index into the scope of the next insertion.</summary>
        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
        public int Index
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.index;
        }

        /// <summary>If true, this scope's nested fields cannot be updated individually.</summary>
        /// <remarks>The entire scope can still be replaced.</remarks>
        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
        public bool Immutable
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.immutable;
        }

        /// <summary>The kind of scope.</summary>
        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
        public LayoutType ScopeType
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.scopeType;
        }

        /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
        public TypeArgumentList ScopeTypeArgs
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.scopeTypeArgs;
        }

        /// <summary>The layout describing the contents of the scope, or null if the scope is unschematized.</summary>
        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
        public Layout Layout
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.layout;
        }

        /// <summary>The full logical type.</summary>
        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
        public TypeArgument TypeArg
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => new TypeArgument(this.cellType, this.cellTypeArgs);
        }

        public override string ToString()
        {
            try
            {
                if (this.scopeType == null)
                {
                    return "<Invalid>";
                }

                TypeArgument scopeTypeArg = (this.scopeType == null) || (this.scopeType is LayoutEndScope)
                    ? default
                    : new TypeArgument(this.scopeType, this.scopeTypeArgs);

                TypeArgument typeArg = (this.cellType == null) || (this.cellType is LayoutEndScope)
                    ? default
                    : new TypeArgument(this.cellType, this.cellTypeArgs);

                string pathOrIndex = !this.writePath.IsNull ? this.writePath.ToString() : this.index.ToString();
                return $"{scopeTypeArg}[{pathOrIndex}] : {typeArg}@{this.metaOffset}/{this.valueOffset}" +
                       (this.immutable ? " immutable" : string.Empty);
            }
            catch
            {
                return "<???>";
            }
        }
    }

    public static class RowCursorExtensions
    {
        /// <summary>Makes a copy of the current cursor.</summary>
        /// <remarks>
        /// The two cursors will have independent and unconnected lifetimes after cloning.  However,
        /// mutations to a <see cref="RowBuffer" /> can invalidate any active cursors over the same row.
        /// </remarks>
        public static ref RowCursor Clone(this in RowCursor src, out RowCursor dest)
        {
            dest = src;
            return ref dest;
        }

        /// <summary>Returns an equivalent scope that is read-only.</summary>
        public static ref RowCursor AsReadOnly(this in RowCursor src, out RowCursor dest)
        {
            dest = src;
            dest.immutable = true;
            return ref dest;
        }

        public static ref RowCursor Find(this ref RowCursor edit, ref RowBuffer row, UtfAnyString path)
        {
            Contract.Requires(!edit.scopeType.IsIndexedScope);

            if (!(edit.cellType is LayoutEndScope))
            {
                while (row.SparseIteratorMoveNext(ref edit))
                {
                    if (path.Equals(row.ReadSparsePath(ref edit)))
                    {
                        edit.exists = true;
                        break;
                    }
                }
            }

            edit.writePath = path;
            edit.writePathToken = default;
            return ref edit;
        }

        public static ref RowCursor Find(this ref RowCursor edit, ref RowBuffer row, in StringToken pathToken)
        {
            Contract.Requires(!edit.scopeType.IsIndexedScope);

            if (!(edit.cellType is LayoutEndScope))
            {
                while (row.SparseIteratorMoveNext(ref edit))
                {
                    if (pathToken.Id == (ulong)edit.pathToken)
                    {
                        edit.exists = true;
                        break;
                    }
                }
            }

            edit.writePath = pathToken.Path;
            edit.writePathToken = pathToken;
            return ref edit;
        }

        public static bool MoveNext(this ref RowCursor edit, ref RowBuffer row)
        {
            edit.writePath = default;
            edit.writePathToken = default;
            return row.SparseIteratorMoveNext(ref edit);
        }

        public static bool MoveTo(this ref RowCursor edit, ref RowBuffer row, int index)
        {
            Contract.Assert(edit.index <= index);
            edit.writePath = default;
            edit.writePathToken = default;
            while (edit.index < index)
            {
                if (!row.SparseIteratorMoveNext(ref edit))
                {
                    return false;
                }
            }

            return true;
        }

        public static bool MoveNext(this ref RowCursor edit, ref RowBuffer row, ref RowCursor childScope)
        {
            if (childScope.scopeType != null)
            {
                edit.Skip(ref row, ref childScope);
            }

            return edit.MoveNext(ref row);
        }

        public static void Skip(this ref RowCursor edit, ref RowBuffer row, ref RowCursor childScope)
        {
            Contract.Requires(childScope.start == edit.valueOffset);
            if (!(childScope.cellType is LayoutEndScope))
            {
                while (row.SparseIteratorMoveNext(ref childScope))
                {
                }
            }

            if (childScope.scopeType.IsSizedScope)
            {
                edit.endOffset = childScope.metaOffset;
            }
            else
            {
                edit.endOffset = childScope.metaOffset + sizeof(LayoutCode); // Move past the end of scope marker.
            }

#if DEBUG
            childScope = default;
#endif
        }
    }
}
