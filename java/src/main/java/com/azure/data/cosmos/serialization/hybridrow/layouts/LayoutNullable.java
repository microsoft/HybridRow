// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class LayoutNullable extends LayoutIndexedScope {
    public LayoutNullable(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_NULLABLE_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.NULLABLE_SCOPE, immutable, true, true, false, true);
    }

    public String name() {
        return this.Immutable ? "im_nullable" : "nullable";
    }

    public int countTypeArgument(@Nonnull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 1);
        return (LayoutCode.SIZE / Byte.SIZE) + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() == 1);
        checkArgument(edit.index() == 1);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    public static Result hasValue(@Nonnull final RowBuffer b, @Nonnull final RowCursor scope) {
        checkNotNull(b);
        checkNotNull(scope);
        checkArgument(scope.scopeType() instanceof LayoutNullable);
        checkArgument(scope.index() == 1 || scope.index() == 2);
        checkArgument(scope.scopeTypeArgs().count() == 1);
        boolean hasValue = b.readInt8(scope.start()) != 0;
        return hasValue ? Result.SUCCESS : Result.NOT_FOUND;
    }

    @Override
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer row, int offset, @Nonnull final Out<Integer> lenInBytes) {
        return new TypeArgumentList(LayoutType.readTypeArgument(row, offset, lenInBytes));
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        checkState(edit.index() == 1);
        edit.get().cellType(edit.get().scopeTypeArgs().get(0).type());
        edit.get().cellTypeArgs(edit.get().scopeTypeArgs().get(0).typeArgs());
    }

    public Result writeScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, boolean hasValue, Out<RowCursor> value) {
        return writeScope(b, edit, typeArgs, hasValue, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList typeArgs, bool
    // hasValue, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    public Result writeScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, boolean hasValue, Out<RowCursor> value,
                             UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteNullable(edit, this, typeArgs.clone(), options, hasValue, value.clone());
        return Result.SUCCESS;
    }

    @Override
    public Result writeScope(RowBuffer b, RowCursor edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return writeScope(b, edit, typeArgs, UpdateOptions.Upsert, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeScope(RowBuffer b, RowCursor edit,
                             TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {
        return this.WriteScope(b, edit, typeArgs.clone(), true, value, options);
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.count() == 1);
        row.get().writeSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(0).type().writeTypeArgument(row, offset + lenInBytes,
            value.get(0).typeArgs().clone());
        return lenInBytes;
    }
}