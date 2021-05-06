// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    internal sealed class LayoutBuilder
    {
        private readonly string name;
        private readonly SchemaId schemaId;
        private LayoutBit.Allocator bitallocator;
        private List<LayoutColumn> fixedColumns;
        private int fixedCount;
        private int fixedSize;
        private List<LayoutColumn> varColumns;
        private int varCount;
        private List<LayoutColumn> sparseColumns;
        private int sparseCount;
        private Stack<LayoutColumn> scope;

        // [ <present bits>
        //   <bool bits>
        //   <fixed_1> <fixed_2> ... <fixed_n>
        //   <var_1> <var_2> ... <var_n>
        //   <sparse_1> <sparse_2> ... <sparse_o>
        // ]
        public LayoutBuilder(string name, SchemaId schemaId)
        {
            this.name = name;
            this.schemaId = schemaId;
            this.Reset();
        }

        private LayoutColumn Parent
        {
            get
            {
                if (this.scope.Count == 0)
                {
                    return null;
                }

                return this.scope.Peek();
            }
        }

        public void AddFixedColumn(string path, LayoutType type, bool nullable, int length = 0)
        {
            Contract.Requires(length >= 0);
            Contract.Requires(!type.IsVarint);

            LayoutColumn col;
            if (type.IsNull)
            {
                Contract.Requires(nullable);
                LayoutBit nullbit = this.bitallocator.Allocate();
                col = new LayoutColumn(
                    path,
                    type,
                    TypeArgumentList.Empty,
                    StorageKind.Fixed,
                    this.Parent,
                    this.fixedCount,
                    0,
                    nullbit,
                    boolBit: LayoutBit.Invalid);
            }
            else if (type.IsBool)
            {
                LayoutBit nullbit = nullable ? this.bitallocator.Allocate() : LayoutBit.Invalid;
                LayoutBit boolbit = this.bitallocator.Allocate();
                col = new LayoutColumn(
                    path,
                    type,
                    TypeArgumentList.Empty,
                    StorageKind.Fixed,
                    this.Parent,
                    this.fixedCount,
                    0,
                    nullbit,
                    boolBit: boolbit);
            }
            else
            {
                LayoutBit nullbit = nullable ? this.bitallocator.Allocate() : LayoutBit.Invalid;
                col = new LayoutColumn(
                    path,
                    type,
                    TypeArgumentList.Empty,
                    StorageKind.Fixed,
                    this.Parent,
                    this.fixedCount,
                    this.fixedSize,
                    nullbit,
                    boolBit: LayoutBit.Invalid,
                    length: length);

                this.fixedSize += type.IsFixed ? type.Size : length;
            }

            this.fixedCount++;
            this.fixedColumns.Add(col);
        }

        public void AddVariableColumn(string path, LayoutType type, int length = 0)
        {
            Contract.Requires(length >= 0);
            Contract.Requires(type.AllowVariable);

            LayoutColumn col = new LayoutColumn(
                path,
                type,
                TypeArgumentList.Empty,
                StorageKind.Variable,
                this.Parent,
                this.varCount,
                this.varCount,
                nullBit: this.bitallocator.Allocate(),
                boolBit: LayoutBit.Invalid,
                length: length);

            this.varCount++;
            this.varColumns.Add(col);
        }

        public void AddSparseColumn(string path, LayoutType type)
        {
            LayoutColumn col = new LayoutColumn(
                path,
                type,
                TypeArgumentList.Empty,
                StorageKind.Sparse,
                this.Parent,
                this.sparseCount,
                -1,
                nullBit: LayoutBit.Invalid,
                boolBit: LayoutBit.Invalid);

            this.sparseCount++;
            this.sparseColumns.Add(col);
        }

        public void AddObjectScope(string path, LayoutType type)
        {
            LayoutColumn col = new LayoutColumn(
                path,
                type,
                TypeArgumentList.Empty,
                StorageKind.Sparse,
                this.Parent,
                this.sparseCount,
                -1,
                nullBit: LayoutBit.Invalid,
                boolBit: LayoutBit.Invalid);

            this.sparseCount++;
            this.sparseColumns.Add(col);
            this.scope.Push(col);
        }

        public void EndObjectScope()
        {
            Contract.Requires(this.scope.Count > 0);
            this.scope.Pop();
        }

        public void AddTypedScope(string path, LayoutType type, TypeArgumentList typeArgs)
        {
            LayoutColumn col = new LayoutColumn(
                path,
                type,
                typeArgs,
                StorageKind.Sparse,
                this.Parent,
                this.sparseCount,
                -1,
                nullBit: LayoutBit.Invalid,
                boolBit: LayoutBit.Invalid);

            this.sparseCount++;
            this.sparseColumns.Add(col);
        }

        public Layout Build()
        {
            // Compute offset deltas.  Offset bools by the present byte count, and fixed fields by the sum of the present and bool count.
            int fixedDelta = this.bitallocator.NumBytes;
            int varIndexDelta = this.fixedCount;

            // Update the fixedColumns with the delta before freezing them.
            List<LayoutColumn> updatedColumns = new List<LayoutColumn>(this.fixedColumns.Count + this.varColumns.Count);

            foreach (LayoutColumn c in this.fixedColumns)
            {
                c.SetOffset(c.Offset + fixedDelta);
                updatedColumns.Add(c);
            }

            foreach (LayoutColumn c in this.varColumns)
            {
                // Adjust variable column indexes such that they begin immediately following the last fixed column.
                c.SetIndex(c.Index + varIndexDelta);
                updatedColumns.Add(c);
            }

            updatedColumns.AddRange(this.sparseColumns);

            Layout layout = new Layout(this.name, this.schemaId, this.bitallocator.NumBytes, this.fixedSize + fixedDelta, updatedColumns);
            this.Reset();
            return layout;
        }

        private void Reset()
        {
            this.bitallocator = new LayoutBit.Allocator();
            this.fixedSize = 0;
            this.fixedCount = 0;
            this.fixedColumns = new List<LayoutColumn>();
            this.varCount = 0;
            this.varColumns = new List<LayoutColumn>();
            this.sparseCount = 0;
            this.sparseColumns = new List<LayoutColumn>();
            this.scope = new Stack<LayoutColumn>();
        }
    }
}
