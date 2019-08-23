//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedArray;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;

import java.util.Collection;
import java.util.List;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct ReadRowDispatcher : IDispatcher
public final class ReadRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue expected = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> root, LayoutColumn col, LayoutType t, TValue expected) {
        TValue value;
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                RefObject<RowBuffer> tempRef_Row =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                OutObject<TValue> tempOut_value = new OutObject<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().ReadFixed(tempRef_Row, root, col, tempOut_value));
                value = tempOut_value.get();
                dispatcher.get().argValue.Row = tempRef_Row.get();
                break;
            case Variable:
                RefObject<RowBuffer> tempRef_Row2 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                OutObject<TValue> tempOut_value2 = new OutObject<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().ReadVariable(tempRef_Row2, root, col, tempOut_value2));
                value = tempOut_value2.get();
                dispatcher.get().argValue.Row = tempRef_Row2.get();
                break;
            default:
                RefObject<RowBuffer> tempRef_Row3 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                OutObject<TValue> tempOut_value3 = new OutObject<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().ReadSparse(tempRef_Row3, root, tempOut_value3));
                value = tempOut_value3.get();
                dispatcher.get().argValue.Row = tempRef_Row3.get();
                break;
        }

        if (TValue.class.IsArray) {
            CollectionAssert.AreEqual((Collection)expected, (Collection)value);
        } else {
            assert expected == value;
        }
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

        int i = 0;
        List items = (List)value;
        RefObject<RowBuffer> tempRef_Row2 =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        while (arrayScope.MoveNext(tempRef_Row2)) {
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), items.get(i++));
        }
        dispatcher.get().argValue.Row = tempRef_Row2.get();
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
        int i = 0;
        List items = (List)value;
        RefObject<RowBuffer> tempRef_Row2 =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        while (mapScope.MoveNext(tempRef_Row2)) {
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs.clone(),
                items.get(i++));
        }
        dispatcher.get().argValue.Row = tempRef_Row2.get();
    }

    public void DispatchNullable(RefObject<RowOperationDispatcher> dispatcher,
                                 RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        checkArgument(typeArgs.getCount() == 1);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor nullableScope;
        OutObject<RowCursor> tempOut_nullableScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().ReadScope(tempRef_Row, scope, tempOut_nullableScope));
        nullableScope = tempOut_nullableScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();

        if (value != null) {
            RefObject<RowBuffer> tempRef_Row2 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            RefObject<RowCursor> tempRef_nullableScope =
                new RefObject<RowCursor>(nullableScope);
            ResultAssert.IsSuccess(LayoutNullable.HasValue(tempRef_Row2, tempRef_nullableScope));
            nullableScope = tempRef_nullableScope.get();
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            RefObject<RowBuffer> tempRef_Row3 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            nullableScope.MoveNext(tempRef_Row3);
            dispatcher.get().argValue.Row = tempRef_Row3.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref nullableScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), value);
        } else {
            RefObject<RowBuffer> tempRef_Row4 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            RefObject<RowCursor> tempRef_nullableScope2 =
                new RefObject<RowCursor>(nullableScope);
            ResultAssert.NotFound(LayoutNullable.HasValue(tempRef_Row4, tempRef_nullableScope2));
            nullableScope = tempRef_nullableScope2.get();
            dispatcher.get().argValue.Row = tempRef_Row4.get();
        }
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
        int i = 0;
        List items = (List)value;
        RefObject<RowBuffer> tempRef_Row2 =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        while (setScope.MoveNext(tempRef_Row2)) {
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref setScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), items.get(i++));
        }
        dispatcher.get().argValue.Row = tempRef_Row2.get();
    }

    public void DispatchTuple(RefObject<RowOperationDispatcher> dispatcher,
                              RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.getCount() >= 2);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor tupleScope;
        OutObject<RowCursor> tempOut_tupleScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().ReadScope(tempRef_Row, scope, tempOut_tupleScope));
        tupleScope = tempOut_tupleScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();

        for (int i = 0; i < typeArgs.getCount(); i++) {
            RefObject<RowBuffer> tempRef_Row2 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            tupleScope.MoveNext(tempRef_Row2);
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            PropertyInfo valueAccessor = value.getClass().GetProperty(String.format("Item%1$s", i + 1));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tupleScope, null, typeArgs.get(i).getType(),
                typeArgs.get(i).getTypeArgs().clone(), valueAccessor.GetValue(value));
        }
    }

    public void DispatchUDT(RefObject<RowOperationDispatcher> dispatcher,
                            RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        RefObject<RowBuffer> tempRef_Row = new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor udtScope;
        OutObject<RowCursor> tempOut_udtScope = new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().ReadScope(tempRef_Row, scope, tempOut_udtScope));
        udtScope = tempOut_udtScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();
        IDispatchable valueDispatcher = value instanceof IDispatchable ? (IDispatchable)value : null;
        assert valueDispatcher != null;
        RefObject<RowCursor> tempRef_udtScope = new RefObject<RowCursor>(udtScope);
        valueDispatcher.Dispatch(dispatcher, tempRef_udtScope);
        udtScope = tempRef_udtScope.get();
    }
}