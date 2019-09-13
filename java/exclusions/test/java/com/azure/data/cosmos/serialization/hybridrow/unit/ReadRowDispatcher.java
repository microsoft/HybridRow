// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue expected = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> root, LayoutColumn col, LayoutType t, TValue expected) {
        TValue value;
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                Reference<RowBuffer> tempReference_Row =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                Out<TValue> tempOut_value = new Out<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>typeAs().readFixed(tempReference_Row, root, col, tempOut_value));
                value = tempOut_value.get();
                dispatcher.get().argValue.Row = tempReference_Row.get();
                break;
            case Variable:
                Reference<RowBuffer> tempReference_Row2 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                Out<TValue> tempOut_value2 = new Out<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>typeAs().readVariable(tempReference_Row2, root, col, tempOut_value2));
                value = tempOut_value2.get();
                dispatcher.get().argValue.Row = tempReference_Row2.get();
                break;
            default:
                Reference<RowBuffer> tempReference_Row3 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                Out<TValue> tempOut_value3 = new Out<TValue>();
                ResultAssert.IsSuccess(t.<TLayout>typeAs().readSparse(tempReference_Row3, root, tempOut_value3));
                value = tempOut_value3.get();
                dispatcher.get().argValue.Row = tempReference_Row3.get();
                break;
        }

        if (TValue.class.IsArray) {
            CollectionAssert.AreEqual((Collection)expected, (Collection)value);
        } else {
            assert expected == value;
        }
    }

    public void DispatchArray(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.count() == 1);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor arrayScope;
        Out<RowCursor> tempOut_arrayScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedArray>typeAs().ReadScope(tempReference_Row, scope, tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();

        int i = 0;
        List items = (List)value;
        Reference<RowBuffer> tempReference_Row2 =
            new Reference<RowBuffer>(dispatcher.get().Row);
        while (arrayScope.MoveNext(tempReference_Row2)) {
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).type(),
                typeArgs.get(0).typeArgs().clone(), items.get(i++));
        }
        dispatcher.get().argValue.Row = tempReference_Row2.get();
    }

    public void DispatchMap(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        checkArgument(typeArgs.count() == 2);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor mapScope;
        Out<RowCursor> tempOut_mapScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedMap>typeAs().ReadScope(tempReference_Row, scope, tempOut_mapScope));
        mapScope = tempOut_mapScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        int i = 0;
        List items = (List)value;
        Reference<RowBuffer> tempReference_Row2 =
            new Reference<RowBuffer>(dispatcher.get().Row);
        while (mapScope.MoveNext(tempReference_Row2)) {
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs.clone(),
                items.get(i++));
        }
        dispatcher.get().argValue.Row = tempReference_Row2.get();
    }

    public void DispatchNullable(Reference<RowOperationDispatcher> dispatcher,
                                 Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        checkArgument(typeArgs.count() == 1);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor nullableScope;
        Out<RowCursor> tempOut_nullableScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutNullable>typeAs().ReadScope(tempReference_Row, scope, tempOut_nullableScope));
        nullableScope = tempOut_nullableScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();

        if (value != null) {
            Reference<RowBuffer> tempReference_Row2 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            Reference<RowCursor> tempReference_nullableScope =
                new Reference<RowCursor>(nullableScope);
            ResultAssert.IsSuccess(LayoutNullable.HasValue(tempReference_Row2, tempReference_nullableScope));
            nullableScope = tempReference_nullableScope.get();
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            Reference<RowBuffer> tempReference_Row3 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            nullableScope.MoveNext(tempReference_Row3);
            dispatcher.get().argValue.Row = tempReference_Row3.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref nullableScope, null, typeArgs.get(0).type(),
                typeArgs.get(0).typeArgs().clone(), value);
        } else {
            Reference<RowBuffer> tempReference_Row4 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            Reference<RowCursor> tempReference_nullableScope2 =
                new Reference<RowCursor>(nullableScope);
            ResultAssert.NotFound(LayoutNullable.HasValue(tempReference_Row4, tempReference_nullableScope2));
            nullableScope = tempReference_nullableScope2.get();
            dispatcher.get().argValue.Row = tempReference_Row4.get();
        }
    }

    public void DispatchObject(Reference<RowOperationDispatcher> dispatcher,
                               Reference<RowCursor> scope) {
    }

    public void DispatchSet(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        checkArgument(typeArgs.count() == 1);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor setScope;
        Out<RowCursor> tempOut_setScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedSet>typeAs().ReadScope(tempReference_Row, scope, tempOut_setScope));
        setScope = tempOut_setScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        int i = 0;
        List items = (List)value;
        Reference<RowBuffer> tempReference_Row2 =
            new Reference<RowBuffer>(dispatcher.get().Row);
        while (setScope.MoveNext(tempReference_Row2)) {
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref setScope, null, typeArgs.get(0).type(),
                typeArgs.get(0).typeArgs().clone(), items.get(i++));
        }
        dispatcher.get().argValue.Row = tempReference_Row2.get();
    }

    public void DispatchTuple(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.count() >= 2);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor tupleScope;
        Out<RowCursor> tempOut_tupleScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>typeAs().ReadScope(tempReference_Row, scope, tempOut_tupleScope));
        tupleScope = tempOut_tupleScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();

        for (int i = 0; i < typeArgs.count(); i++) {
            Reference<RowBuffer> tempReference_Row2 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            tupleScope.MoveNext(tempReference_Row2);
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            PropertyInfo valueAccessor = value.getClass().GetProperty(String.format("Item%1$s", i + 1));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tupleScope, null, typeArgs.get(i).type(),
                typeArgs.get(i).typeArgs().clone(), valueAccessor.GetValue(value));
        }
    }

    public void DispatchUDT(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        Reference<RowBuffer> tempReference_Row = new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor udtScope;
        Out<RowCursor> tempOut_udtScope = new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutUDT>typeAs().ReadScope(tempReference_Row, scope, tempOut_udtScope));
        udtScope = tempOut_udtScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        IDispatchable valueDispatcher = value instanceof IDispatchable ? (IDispatchable)value : null;
        assert valueDispatcher != null;
        Reference<RowCursor> tempReference_udtScope = new Reference<RowCursor>(udtScope);
        valueDispatcher.Dispatch(dispatcher, tempReference_udtScope);
        udtScope = tempReference_udtScope.get();
    }
}