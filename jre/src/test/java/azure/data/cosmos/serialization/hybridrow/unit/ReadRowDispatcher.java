//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.Collection;
import java.util.List;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct ReadRowDispatcher : IDispatcher
public final class ReadRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue expected = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> root, LayoutColumn col, LayoutType t, TValue expected) {
        TValue value;
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                tangible.OutObject<TValue> tempOut_value = new tangible.OutObject<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().ReadFixed(tempRef_Row, root, col, tempOut_value));
                value = tempOut_value.argValue;
                dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
                break;
            case Variable:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                tangible.OutObject<TValue> tempOut_value2 = new tangible.OutObject<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().ReadVariable(tempRef_Row2, root, col, tempOut_value2));
                value = tempOut_value2.argValue;
                dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
                break;
            default:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                tangible.OutObject<TValue> tempOut_value3 = new tangible.OutObject<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().ReadSparse(tempRef_Row3, root, tempOut_value3));
                value = tempOut_value3.argValue;
                dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
                break;
        }

        if (TValue.class.IsArray) {
            CollectionAssert.AreEqual((Collection)expected, (Collection)value);
        } else {
            assert expected == value;
        }
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

        int i = 0;
        List items = (List)value;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        while (arrayScope.MoveNext(tempRef_Row2)) {
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), items.get(i++));
        }
        dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
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
        int i = 0;
        List items = (List)value;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        while (mapScope.MoveNext(tempRef_Row2)) {
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs.clone(),
                items.get(i++));
        }
        dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
    }

    public void DispatchNullable(tangible.RefObject<RowOperationDispatcher> dispatcher,
                                 tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        Contract.Requires(typeArgs.getCount() == 1);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor nullableScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().ReadScope(tempRef_Row, scope, tempOut_nullableScope));
        nullableScope = tempOut_nullableScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;

        if (value != null) {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
            ResultAssert.IsSuccess(LayoutNullable.HasValue(tempRef_Row2, tempRef_nullableScope));
            nullableScope = tempRef_nullableScope.argValue;
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            nullableScope.MoveNext(tempRef_Row3);
            dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref nullableScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), value);
        } else {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
            ResultAssert.NotFound(LayoutNullable.HasValue(tempRef_Row4, tempRef_nullableScope2));
            nullableScope = tempRef_nullableScope2.argValue;
            dispatcher.argValue.argValue.Row = tempRef_Row4.argValue;
        }
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
        int i = 0;
        List items = (List)value;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        while (setScope.MoveNext(tempRef_Row2)) {
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref setScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), items.get(i++));
        }
        dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
    }

    public void DispatchTuple(tangible.RefObject<RowOperationDispatcher> dispatcher,
                              tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        Contract.Requires(typeArgs.getCount() >= 2);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor tupleScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().ReadScope(tempRef_Row, scope, tempOut_tupleScope));
        tupleScope = tempOut_tupleScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;

        for (int i = 0; i < typeArgs.getCount(); i++) {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            tupleScope.MoveNext(tempRef_Row2);
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            PropertyInfo valueAccessor = value.getClass().GetProperty(String.format("Item%1$s", i + 1));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref tupleScope, null, typeArgs.get(i).getType(),
                typeArgs.get(i).getTypeArgs().clone(), valueAccessor.GetValue(value));
        }
    }

    public void DispatchUDT(tangible.RefObject<RowOperationDispatcher> dispatcher,
                            tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor udtScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_udtScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().ReadScope(tempRef_Row, scope, tempOut_udtScope));
        udtScope = tempOut_udtScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
        IDispatchable valueDispatcher = value instanceof IDispatchable ? (IDispatchable)value : null;
        assert valueDispatcher != null;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_udtScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(udtScope);
        valueDispatcher.Dispatch(dispatcher, tempRef_udtScope);
        udtScope = tempRef_udtScope.argValue;
    }
}