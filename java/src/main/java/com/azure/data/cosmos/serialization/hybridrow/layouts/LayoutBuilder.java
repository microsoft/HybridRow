// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import java.util.ArrayList;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutBuilder {
    private LayoutBit.Allocator bitAllocator;
    private ArrayList<LayoutColumn> fixedColumns;
    private int fixedCount;
    private int fixedSize;
    private String name;
    private SchemaId schemaId;
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
        this.schemaId = schemaId;
        this.reset();
    }

    public void addFixedColumn(String path, LayoutType type, boolean nullable, int length) {

        checkArgument(length >= 0);
        checkArgument(!type.isVarint());

        LayoutColumn column;
        if (type.isNull()) {
            checkArgument(nullable);
            LayoutBit nullBit = this.bitAllocator.Allocate();
            column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.FIXED, this.parent(),
                this.fixedCount, 0, nullBit, LayoutBit.INVALID, 0);
        } else if (type.isBoolean()) {
            LayoutBit nullBit = nullable ? this.bitAllocator.Allocate() : LayoutBit.INVALID;
            LayoutBit boolbit = this.bitAllocator.Allocate();
            column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.FIXED, this.parent(),
                this.fixedCount, 0, nullBit, boolbit, 0);
        } else {
            LayoutBit nullBit = nullable ? this.bitAllocator.Allocate() : LayoutBit.INVALID;
            column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.FIXED, this.parent(),
                this.fixedCount, this.fixedSize, nullBit, LayoutBit.INVALID, length);

            this.fixedSize += type.isFixed() ? type.size() : length;
        }

        this.fixedCount++;
        this.fixedColumns.add(column);
    }

    public void addObjectScope(String path, LayoutType type) {

        LayoutColumn column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.SPARSE, this.parent(),
            this.sparseCount, -1, LayoutBit.INVALID, LayoutBit.INVALID, 0);

        this.sparseCount++;
        this.sparseColumns.add(column);
        this.scope.push(column);
    }

    public void addSparseColumn(String path, LayoutType type) {

        LayoutColumn column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.SPARSE, this.parent(),
            this.sparseCount, -1, LayoutBit.INVALID, LayoutBit.INVALID, 0);

        this.sparseCount++;
        this.sparseColumns.add(column);
    }

    public void addTypedScope(String path, LayoutType type, TypeArgumentList typeArgs) {

        LayoutColumn col = new LayoutColumn(path, type, typeArgs, StorageKind.SPARSE, this.parent(), this.sparseCount,
            -1, LayoutBit.INVALID, LayoutBit.INVALID, 0);

        this.sparseCount++;
        this.sparseColumns.add(col);
    }

    public void addVariableColumn(String path, LayoutType type, int length) {

        checkArgument(length >= 0);
        checkArgument(type.allowVariable());

        LayoutColumn column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.VARIABLE, this.parent(),
            this.varCount, this.varCount, this.bitAllocator.Allocate(), LayoutBit.INVALID, length);

        this.varCount++;
        this.varColumns.add(column);
    }

    public Layout build() {
        // Compute offset deltas.  Offset bools by the present byte count, and fixed fields by the sum of the present
        // and bool count.
        int fixedDelta = this.bitAllocator.getNumBytes();
        int varIndexDelta = this.fixedCount;

        // Update the fixedColumns with the delta before freezing them.
        ArrayList<LayoutColumn> updatedColumns =
            new ArrayList<LayoutColumn>(this.fixedColumns.size() + this.varColumns.size());

        for (LayoutColumn column : this.fixedColumns) {
            column.offset(column.offset() + fixedDelta);
            updatedColumns.add(column);
        }

        for (LayoutColumn column : this.varColumns) {
            // Adjust variable column indexes such that they begin immediately following the last fixed column.
            column.index(column.index() + varIndexDelta);
            updatedColumns.add(column);
        }

        updatedColumns.addAll(this.sparseColumns);

        Layout layout = new Layout(this.name, this.schemaId, this.bitAllocator.getNumBytes(), this.fixedSize + fixedDelta, updatedColumns);
        this.reset();
        return layout;
    }

    public void EndObjectScope() {
        checkArgument(this.scope.size() > 0);
        this.scope.pop();
    }

    private LayoutColumn parent() {
        if (this.scope.empty()) {
            return null;
        }

        return this.scope.peek();
    }

    private void reset() {
        this.bitAllocator = new LayoutBit.Allocator();
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