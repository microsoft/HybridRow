//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct DeleteRowDispatcher : IDispatcher
public final class DeleteRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> root, LayoutColumn col, LayoutType t, TValue value) {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<TLayout>TypeAs().DeleteSparse(tempRef_Row, root));
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
    }

    public void DispatchArray(tangible.RefObject<RowOperationDispatcher> dispatcher,
                              tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        Contract.Requires(typeArgs.getCount() == 1);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor arrayScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_arrayScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().ReadScope(tempRef_Row, scope, tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;

        if (!arrayScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                assert arrayScope.MoveNext(tempRef_Row2);
                dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                dispatcher.argValue.LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                    typeArgs.get(0).getTypeArgs().clone(), item);
            }
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().DeleteScope(tempRef_Row3, scope));
        dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
    }

    public void DispatchMap(tangible.RefObject<RowOperationDispatcher> dispatcher,
                            tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        Contract.Requires(typeArgs.getCount() == 2);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor mapScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_mapScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().ReadScope(tempRef_Row, scope, tempOut_mapScope));
        mapScope = tempOut_mapScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
        if (!mapScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                assert mapScope.MoveNext(tempRef_Row2);
                dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                dispatcher.argValue.LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs.clone(), item);
            }
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().DeleteScope(tempRef_Row3, scope));
        dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
    }

    public void DispatchNullable(tangible.RefObject<RowOperationDispatcher> dispatcher,
                                 tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        Contract.Requires(typeArgs.getCount() == 1);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().DeleteScope(tempRef_Row, scope));
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
    }

    public void DispatchObject(tangible.RefObject<RowOperationDispatcher> dispatcher,
                               tangible.RefObject<RowCursor> scope) {
    }

    public void DispatchSet(tangible.RefObject<RowOperationDispatcher> dispatcher,
                            tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        Contract.Requires(typeArgs.getCount() == 1);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor setScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_setScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().ReadScope(tempRef_Row, scope, tempOut_setScope));
        setScope = tempOut_setScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
        if (!setScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                assert setScope.MoveNext(tempRef_Row2);
                dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                dispatcher.argValue.LayoutCodeSwitch(ref setScope, null, typeArgs.get(0).getType(),
                    typeArgs.get(0).getTypeArgs().clone(), item);
            }
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().DeleteScope(tempRef_Row3, scope));
        dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
    }

    public void DispatchTuple(tangible.RefObject<RowOperationDispatcher> dispatcher,
                              tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        Contract.Requires(typeArgs.getCount() >= 2);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().DeleteScope(tempRef_Row, scope));
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
    }

    public void DispatchUDT(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().DeleteScope(tempRef_Row, scope));
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
    }
}