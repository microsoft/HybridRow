//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow;

import azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode;
import azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;

import static com.google.common.base.Preconditions.checkArgument;

public final class RowCursorExtensions {
    /** Makes a copy of the current cursor.

     The two cursors will have independent and unconnected lifetimes after cloning.  However,
     mutations to a <see cref="RowBuffer" /> can invalidate any active cursors over the same row.

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
    public static boolean MoveNext(tangible.RefObject<RowCursor> edit, tangible.RefObject<RowBuffer> row) {
        edit.argValue.writePath = null;
        edit.argValue.writePathToken = null;
        return row.argValue.SparseIteratorMoveNext(edit);
    }

    public static boolean MoveNext(tangible.RefObject<RowCursor> edit, tangible.RefObject<RowBuffer> row,
                                   tangible.RefObject<RowCursor> childScope) {
        if (childScope.argValue.scopeType != null) {
            azure.data.cosmos.serialization.hybridrow.RowCursorExtensions.Skip(edit.argValue.clone(), row, childScope);
        }

        return azure.data.cosmos.serialization.hybridrow.RowCursorExtensions.MoveNext(edit.argValue.clone(), row);
    }

    public static boolean MoveTo(tangible.RefObject<RowCursor> edit, tangible.RefObject<RowBuffer> row, int index) {
        checkState(edit.argValue.index <= index);
        edit.argValue.writePath = null;
        edit.argValue.writePathToken = null;
        while (edit.argValue.index < index) {
            if (!row.argValue.SparseIteratorMoveNext(edit)) {
                return false;
            }
        }

        return true;
    }

    public static void Skip(tangible.RefObject<RowCursor> edit, tangible.RefObject<RowBuffer> row,
                            tangible.RefObject<RowCursor> childScope) {
        checkArgument(childScope.argValue.start == edit.argValue.valueOffset);
        if (!(childScope.argValue.cellType instanceof LayoutEndScope)) {
            while (row.argValue.SparseIteratorMoveNext(childScope)) {
            }
        }

        if (childScope.argValue.scopeType.IsSizedScope) {
            edit.argValue.endOffset = childScope.argValue.metaOffset;
        } else {
            edit.argValue.endOffset = childScope.argValue.metaOffset + (LayoutCode.SIZE / Byte.SIZE); // Move past the end of scope marker.
        }

        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        childScope.argValue = null;
        //#endif
    }
}