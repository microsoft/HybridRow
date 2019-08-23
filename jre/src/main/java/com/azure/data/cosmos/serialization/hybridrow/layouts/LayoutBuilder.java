//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import java.util.ArrayList;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutBuilder {
    private LayoutBit.Allocator bitallocator;
    private ArrayList<LayoutColumn> fixedColumns;
    private int fixedCount;
    private int fixedSize;
    private String name;
    private SchemaId schemaId = new SchemaId();
    private Stack<LayoutColumn> scope;
    private ArrayList<LayoutColumn> sparseColumns;
    private int sparseCount;
    private ArrayList<LayoutColumn> varColumns;
    private int varCount;

    // [ <present bits>
    //   <bool bits>
    //   <fixed_1> <fixed_2> ... <fixed_n>
    //   <var_1> <var_2> ... <var_n>
    //   <sparse_1> <sparse_2> ... <sparse_o>
    // ]
    public LayoutBuilder(String name, SchemaId schemaId) {
        this.name = name;
        this.schemaId = schemaId.clone();
        this.Reset();
    }

    public void AddFixedColumn(String path, LayoutType type, boolean nullable) {
        AddFixedColumn(path, type, nullable, 0);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void AddFixedColumn(string path, LayoutType type, bool nullable, int length = 0)
    public void AddFixedColumn(String path, LayoutType type, boolean nullable, int length) {
        checkArgument(length >= 0);
        checkArgument(!type.getIsVarint());

        LayoutColumn col;
        if (type.getIsNull()) {
            checkArgument(nullable);
            LayoutBit nullbit = this.bitallocator.Allocate().clone();
            col = new LayoutColumn(path, type, TypeArgumentList.Empty, StorageKind.Fixed, this.getParent(),
                this.fixedCount, 0, nullbit.clone(), LayoutBit.Invalid, 0);
        } else if (type.getIsBool()) {
            LayoutBit nullbit = nullable ? this.bitallocator.Allocate() : LayoutBit.Invalid;
            LayoutBit boolbit = this.bitallocator.Allocate().clone();
            col = new LayoutColumn(path, type, TypeArgumentList.Empty, StorageKind.Fixed, this.getParent(),
                this.fixedCount, 0, nullbit.clone(), boolbit.clone(), 0);
        } else {
            LayoutBit nullBit = nullable ? this.bitallocator.Allocate() : LayoutBit.Invalid;
            col = new LayoutColumn(path, type, TypeArgumentList.Empty, StorageKind.Fixed, this.getParent(),
                this.fixedCount, this.fixedSize, nullBit.clone(), LayoutBit.Invalid, length);

            this.fixedSize += type.getIsFixed() ? type.Size : length;
        }

        this.fixedCount++;
        this.fixedColumns.add(col);
    }

    public void AddObjectScope(String path, LayoutType type) {
        LayoutColumn col = new LayoutColumn(path, type, TypeArgumentList.Empty, StorageKind.Sparse, this.getParent(),
            this.sparseCount, -1, LayoutBit.Invalid, LayoutBit.Invalid, 0);

        this.sparseCount++;
        this.sparseColumns.add(col);
        this.scope.push(col);
    }

    public void AddSparseColumn(String path, LayoutType type) {
        LayoutColumn col = new LayoutColumn(path, type, TypeArgumentList.Empty, StorageKind.Sparse, this.getParent(),
            this.sparseCount, -1, LayoutBit.Invalid, LayoutBit.Invalid, 0);

        this.sparseCount++;
        this.sparseColumns.add(col);
    }

    public void AddTypedScope(String path, LayoutType type, TypeArgumentList typeArgs) {
        LayoutColumn col = new LayoutColumn(path, type, typeArgs.clone(), StorageKind.Sparse, this.getParent(),
            this.sparseCount, -1, LayoutBit.Invalid, LayoutBit.Invalid, 0);

        this.sparseCount++;
        this.sparseColumns.add(col);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void AddVariableColumn(string path, LayoutType type, int length = 0)
    public void AddVariableColumn(String path, LayoutType type, int length) {

        checkArgument(length >= 0);
        checkArgument(type.getAllowVariable());

        LayoutColumn col = new LayoutColumn(path, type, TypeArgumentList.Empty, StorageKind.Variable,
            this.getParent(), this.varCount, this.varCount, this.bitallocator.Allocate().clone(), LayoutBit.Invalid,
            length);

        this.varCount++;
        this.varColumns.add(col);
    }

    public void AddVariableColumn(String path, LayoutType type) {
        AddVariableColumn(path, type, 0);
    }

    public Layout Build() {
        // Compute offset deltas.  Offset bools by the present byte count, and fixed fields by the sum of the present
        // and bool count.
        int fixedDelta = this.bitallocator.getNumBytes();
        int varIndexDelta = this.fixedCount;

        // Update the fixedColumns with the delta before freezing them.
        ArrayList<LayoutColumn> updatedColumns =
            new ArrayList<LayoutColumn>(this.fixedColumns.size() + this.varColumns.size());

        for (LayoutColumn c : this.fixedColumns) {
            c.SetOffset(c.getOffset() + fixedDelta);
            updatedColumns.add(c);
        }

        for (LayoutColumn c : this.varColumns) {
            // Adjust variable column indexes such that they begin immediately following the last fixed column.
            c.SetIndex(c.getIndex() + varIndexDelta);
            updatedColumns.add(c);
        }

        updatedColumns.addAll(this.sparseColumns);

        Layout layout = new Layout(this.name, this.schemaId.clone(), this.bitallocator.getNumBytes(), this.fixedSize + fixedDelta, updatedColumns);
        this.Reset();
        return layout;
    }

    public void EndObjectScope() {
        checkArgument(this.scope.size() > 0);
        this.scope.pop();
    }

    private LayoutColumn getParent() {
        if (this.scope.empty()) {
            return null;
        }

        return this.scope.peek();
    }

    private void Reset() {
        this.bitallocator = new LayoutBit.Allocator();
        this.fixedSize = 0;
        this.fixedCount = 0;
        this.fixedColumns = new ArrayList<LayoutColumn>();
        this.varCount = 0;
        this.varColumns = new ArrayList<LayoutColumn>();
        this.sparseCount = 0;
        this.sparseColumns = new ArrayList<LayoutColumn>();
        this.scope = new Stack<LayoutColumn>();
    }
}