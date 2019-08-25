//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;

import static com.google.common.base.Preconditions.checkArgument;

public final class RowCursorExtensions {
    /** Makes a copy of the current cursor.

     The two cursors will have independent and unconnected lifetimes after cloning.  However,
     mutations to a {@link RowBuffer} can invalidate any active cursors over the same row.

     */
    // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
    //	public static ref RowCursor Clone(this in RowCursor src, out RowCursor dest)
    //		{
    //			dest = src;
    //			return ref dest;
    //		}

    /**
     * Returns an equivalent scope that is read-only.
     */
    // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
    //	public static ref RowCursor AsReadOnly(this in RowCursor src, out RowCursor dest)
    //		{
    //			dest = src;
    //			dest.immutable = true;
    //			return ref dest;
    //		}

    // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
    //	public static ref RowCursor Find(this ref RowCursor edit, ref RowBuffer row, UtfAnyString path)
    //		{
    //			Contract.Requires(!edit.scopeType.IsIndexedScope);
    //
    //			if (!(edit.cellType is LayoutEndScope))
    //			{
    //				while (row.SparseIteratorMoveNext(ref edit))
    //				{
    //					if (path.Equals(row.ReadSparsePath(ref edit)))
    //					{
    //						edit.exists = true;
    //						break;
    //					}
    //				}
    //			}
    //
    //			edit.writePath = path;
    //			edit.writePathToken = default;
    //			return ref edit;
    //		}

    // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
    //	public static ref RowCursor Find(this ref RowCursor edit, ref RowBuffer row, in StringToken pathToken)
    //		{
    //			Contract.Requires(!edit.scopeType.IsIndexedScope);
    //
    //			if (!(edit.cellType is LayoutEndScope))
    //			{
    //				while (row.SparseIteratorMoveNext(ref edit))
    //				{
    //					if (pathToken.Id == (ulong)edit.pathToken)
    //					{
    //						edit.exists = true;
    //						break;
    //					}
    //				}
    //			}
    //
    //			edit.writePath = pathToken.Path;
    //			edit.writePathToken = pathToken;
    //			return ref edit;
    //		}
    public static boolean MoveNext(Reference<RowCursor> edit, Reference<RowBuffer> row) {
        edit.get().writePath = null;
        edit.get().writePathToken = null;
        return row.get().SparseIteratorMoveNext(edit);
    }

    public static boolean MoveNext(Reference<RowCursor> edit, Reference<RowBuffer> row,
                                   Reference<RowCursor> childScope) {
        if (childScope.get().scopeType != null) {
            RowCursorExtensions.Skip(edit.get().clone(), row, childScope);
        }

        return RowCursorExtensions.MoveNext(edit.get().clone(), row);
    }

    public static boolean MoveTo(Reference<RowCursor> edit, Reference<RowBuffer> row, int index) {
        checkState(edit.get().index <= index);
        edit.get().writePath = null;
        edit.get().writePathToken = null;
        while (edit.get().index < index) {
            if (!row.get().SparseIteratorMoveNext(edit)) {
                return false;
            }
        }

        return true;
    }

    public static void Skip(Reference<RowCursor> edit, Reference<RowBuffer> row,
                            Reference<RowCursor> childScope) {
        checkArgument(childScope.get().start == edit.get().valueOffset);
        if (!(childScope.get().cellType instanceof LayoutEndScope)) {
            while (row.get().SparseIteratorMoveNext(childScope)) {
            }
        }

        if (childScope.get().scopeType.IsSizedScope) {
            edit.get().endOffset = childScope.get().metaOffset;
        } else {
            edit.get().endOffset = childScope.get().metaOffset + (LayoutCode.SIZE / Byte.SIZE); // Move past the end of scope marker.
        }

        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        childScope.setAndGet(null);
        //#endif
    }
}