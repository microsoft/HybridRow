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

import java.util.List;
import java.util.UUID;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct WriteRowDispatcher : IDispatcher
public final class WriteRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> field, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, field, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor
    // field, LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> field, LayoutColumn col, LayoutType t, TValue value) {
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                RefObject<RowBuffer> tempRef_Row =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().WriteFixed(tempRef_Row, field, col, value));
                dispatcher.get().argValue.Row = tempRef_Row.get();
                break;
            case Variable:
                RefObject<RowBuffer> tempRef_Row2 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().WriteVariable(tempRef_Row2, field, col, value));
                dispatcher.get().argValue.Row = tempRef_Row2.get();
                break;
            default:
                RefObject<RowBuffer> tempRef_Row3 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().WriteSparse(tempRef_Row3, field, value));
                dispatcher.get().argValue.Row = tempRef_Row3.get();
                break;
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
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();

        List items = (List)value;
        for (Object item : items) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), item);
            RefObject<RowBuffer> tempRef_Row2 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            arrayScope.MoveNext(tempRef_Row2);
            dispatcher.get().argValue.Row = tempRef_Row2.get();
        }
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
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            tempOut_mapScope));
        mapScope = tempOut_mapScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();
        RefObject<RowCursor> tempRef_mapScope =
            new RefObject<RowCursor>(mapScope);
        TypeArgument fieldType = t.<LayoutUniqueScope>TypeAs().FieldType(tempRef_mapScope).clone();
        mapScope = tempRef_mapScope.get();
        List pairs = (List)value;
        for (Object pair : pairs) {
            String elmPath = UUID.NewGuid().toString();
            RefObject<RowBuffer> tempRef_Row2 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            RowCursor.CreateForAppend(tempRef_Row2, out tempCursor);
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tempCursor, elmPath, fieldType.getType(),
                fieldType.getTypeArgs().clone(), pair);

            // Move item into the map.
            RefObject<RowBuffer> tempRef_Row3 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            RefObject<RowCursor> tempRef_mapScope2 =
                new RefObject<RowCursor>(mapScope);
            RefObject<RowCursor> tempRef_tempCursor =
                new RefObject<RowCursor>(tempCursor);
            ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().MoveField(tempRef_Row3, tempRef_mapScope2,
                tempRef_tempCursor));
            tempCursor = tempRef_tempCursor.get();
            mapScope = tempRef_mapScope2.get();
            dispatcher.get().argValue.Row = tempRef_Row3.get();
        }
    }

    public void DispatchNullable(RefObject<RowOperationDispatcher> dispatcher,
                                 RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        checkArgument(typeArgs.getCount() == 1);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor nullableScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            value != null, out nullableScope));
        dispatcher.get().argValue.Row = tempRef_Row.get();

        if (value != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref nullableScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), value);
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
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            tempOut_setScope));
        setScope = tempOut_setScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();
        List items = (List)value;
        for (Object item : items) {
            String elmPath = UUID.NewGuid().toString();
            RefObject<RowBuffer> tempRef_Row2 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            RowCursor.CreateForAppend(tempRef_Row2, out tempCursor);
            dispatcher.get().argValue.Row = tempRef_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tempCursor, elmPath, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), item);

            // Move item into the set.
            RefObject<RowBuffer> tempRef_Row3 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            RefObject<RowCursor> tempRef_setScope =
                new RefObject<RowCursor>(setScope);
            RefObject<RowCursor> tempRef_tempCursor =
                new RefObject<RowCursor>(tempCursor);
            ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().MoveField(tempRef_Row3, tempRef_setScope,
                tempRef_tempCursor));
            tempCursor = tempRef_tempCursor.get();
            setScope = tempRef_setScope.get();
            dispatcher.get().argValue.Row = tempRef_Row3.get();
        }
    }

    public void DispatchTuple(RefObject<RowOperationDispatcher> dispatcher,
                              RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.getCount() >= 2);

        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            out tupleScope));
        dispatcher.get().argValue.Row = tempRef_Row.get();

        for (int i = 0; i < typeArgs.getCount(); i++) {
            PropertyInfo valueAccessor = value.getClass().GetProperty(String.format("Item%1$s", i + 1));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tupleScope, null, typeArgs.get(i).getType(),
                typeArgs.get(i).getTypeArgs().clone(), valueAccessor.GetValue(value));
            RefObject<RowBuffer> tempRef_Row2 =
                new RefObject<RowBuffer>(dispatcher.get().Row);
            tupleScope.MoveNext(tempRef_Row2);
            dispatcher.get().argValue.Row = tempRef_Row2.get();
        }
    }

    public void DispatchUDT(RefObject<RowOperationDispatcher> dispatcher,
                            RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        RefObject<RowBuffer> tempRef_Row = new RefObject<RowBuffer>(dispatcher.get().Row);
        RowCursor udtScope;
        OutObject<RowCursor> tempOut_udtScope = new OutObject<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(), tempOut_udtScope));
        udtScope = tempOut_udtScope.get();
        dispatcher.get().argValue.Row = tempRef_Row.get();
        IDispatchable valueDispatcher = value instanceof IDispatchable ? (IDispatchable)value : null;
        assert valueDispatcher != null;
        RefObject<RowCursor> tempRef_udtScope = new RefObject<RowCursor>(udtScope);
        valueDispatcher.Dispatch(dispatcher, tempRef_udtScope);
        udtScope = tempRef_udtScope.get();
    }
}