// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_SET_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_SET_SCOPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class LayoutTypedSet extends LayoutUniqueScope {

    public LayoutTypedSet(boolean immutable) {
        super(immutable ? IMMUTABLE_TYPED_SET_SCOPE : TYPED_SET_SCOPE, immutable, true, true);
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Nonnull
    @Override
    public TypeArgument fieldType(RowCursor scope) {
        return scope.scopeTypeArgs().get(0);
    }

    @Override
    public boolean hasImplicitTypeCode(RowCursor edit) {
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_set_t" : "set_t";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(RowBuffer buffer, int offset, Out<Integer> lenInBytes) {
        return new TypeArgumentList(readTypeArgument(buffer, offset, lenInBytes));
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        edit.cellType(edit.scopeTypeArgs().get(0).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(0).typeArgs());
    }

    @Override
    @Nonnull
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    @Nonnull
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {
        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        buffer.writeTypedSet(edit, this, typeArgs, options, value);
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(RowBuffer buffer, int offset, TypeArgumentList value) {
        checkArgument(value.count() == 1);
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lenInBytes = LayoutCode.BYTES;
        lenInBytes += value.get(0).type().writeTypeArgument(buffer, offset + lenInBytes,
            value.get(0).typeArgs());
        return lenInBytes;
    }
}