//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutIndexedScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedSet;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;

import java.util.List;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct DeleteRowDispatcher : IDispatcher
public final class DeleteRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> root, LayoutColumn col, LayoutType t, TValue value) {
        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<TLayout>TypeAs().DeleteSparse(tempRef_Row, root));
        dispatcher.get().argValue.Row = tempRef_Row.get();
    }

    public void DispatchArray(RefObject<RowOperationDispatcher> dispatcher,
                              RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.getCount() == 1);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor arrayScope;
        OutObject<RowCursor> tempOut_arrayScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().ReadScope(tempRef_Row, scope, tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();

        if (!arrayScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                RefObject<RowBuffer> tempRef_Row2 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                assert arrayScope.MoveNext(tempRef_Row2);
                dispatcher.get().argValue.Row = tempRef_Row2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                dispatcher.get().LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                    typeArgs.get(0).getTypeArgs().clone(), item);
            }
        }

        RefObject<RowBuffer> tempRef_Row3 =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().DeleteScope(tempRef_Row3, scope));
        dispatcher.get().argValue.Row = tempRef_Row3.get();
    }

    public void DispatchMap(RefObject<RowOperationDispatcher> dispatcher,
                            RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        checkArgument(typeArgs.getCount() == 2);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor mapScope;
        OutObject<RowCursor> tempOut_mapScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().ReadScope(tempRef_Row, scope, tempOut_mapScope));
        mapScope = tempOut_mapScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();
        if (!mapScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                RefObject<RowBuffer> tempRef_Row2 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                assert mapScope.MoveNext(tempRef_Row2);
                dispatcher.get().argValue.Row = tempRef_Row2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                dispatcher.get().LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs.clone(), item);
            }
        }

        RefObject<RowBuffer> tempRef_Row3 =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().DeleteScope(tempRef_Row3, scope));
        dispatcher.get().argValue.Row = tempRef_Row3.get();
    }

    public void DispatchNullable(RefObject<RowOperationDispatcher> dispatcher,
                                 RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        checkArgument(typeArgs.getCount() == 1);
        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().DeleteScope(tempRef_Row, scope));
        dispatcher.get().argValue.Row = tempRef_Row.get();
    }

    public void DispatchObject(RefObject<RowOperationDispatcher> dispatcher,
                               RefObject<RowCursor> scope) {
    }

    public void DispatchSet(RefObject<RowOperationDispatcher> dispatcher,
                            RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        checkArgument(typeArgs.getCount() == 1);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor setScope;
        OutObject<RowCursor> tempOut_setScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().ReadScope(tempRef_Row, scope, tempOut_setScope));
        setScope = tempOut_setScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();
        if (!setScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                RefObject<RowBuffer> tempRef_Row2 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                assert setScope.MoveNext(tempRef_Row2);
                dispatcher.get().argValue.Row = tempRef_Row2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                dispatcher.get().LayoutCodeSwitch(ref setScope, null, typeArgs.get(0).getType(),
                    typeArgs.get(0).getTypeArgs().clone(), item);
            }
        }

        RefObject<RowBuffer> tempRef_Row3 =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().DeleteScope(tempRef_Row3, scope));
        dispatcher.get().argValue.Row = tempRef_Row3.get();
    }

    public void DispatchTuple(RefObject<RowOperationDispatcher> dispatcher,
                              RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.getCount() >= 2);
        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().DeleteScope(tempRef_Row, scope));
        dispatcher.get().argValue.Row = tempRef_Row.get();
    }

    public void DispatchUDT(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
        RefObject<RowBuffer> tempRef_Row = new RefObject<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().DeleteScope(tempRef_Row, scope));
        dispatcher.get().argValue.Row = tempRef_Row.get();
    }
}