//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;
import java.util.UUID;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct WriteRowDispatcher : IDispatcher
public final class WriteRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> field, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, field, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor
    // field, LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> field, LayoutColumn col, LayoutType t, TValue value) {
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().WriteFixed(tempRef_Row, field, col, value));
                dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
                break;
            case Variable:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().WriteVariable(tempRef_Row2, field, col, value));
                dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
                break;
            default:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                ResultAssert.IsSuccess(t.<TLayout>TypeAs().WriteSparse(tempRef_Row3, field, value));
                dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
                break;
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
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;

        List items = (List)value;
        for (Object item : items) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), item);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            arrayScope.MoveNext(tempRef_Row2);
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
        }
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
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            tempOut_mapScope));
        mapScope = tempOut_mapScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mapScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mapScope);
        TypeArgument fieldType = t.<LayoutUniqueScope>TypeAs().FieldType(tempRef_mapScope).clone();
        mapScope = tempRef_mapScope.argValue;
        List pairs = (List)value;
        for (Object pair : pairs) {
            String elmPath = UUID.NewGuid().toString();
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            RowCursor.CreateForAppend(tempRef_Row2, out tempCursor);
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref tempCursor, elmPath, fieldType.getType(),
                fieldType.getTypeArgs().clone(), pair);

            // Move item into the map.
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mapScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mapScope);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
            ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().MoveField(tempRef_Row3, tempRef_mapScope2,
                tempRef_tempCursor));
            tempCursor = tempRef_tempCursor.argValue;
            mapScope = tempRef_mapScope2.argValue;
            dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
        }
    }

    public void DispatchNullable(tangible.RefObject<RowOperationDispatcher> dispatcher,
                                 tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        Contract.Requires(typeArgs.getCount() == 1);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor nullableScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            value != null, out nullableScope));
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;

        if (value != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref nullableScope, null, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), value);
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
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            tempOut_setScope));
        setScope = tempOut_setScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
        List items = (List)value;
        for (Object item : items) {
            String elmPath = UUID.NewGuid().toString();
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            RowCursor.CreateForAppend(tempRef_Row2, out tempCursor);
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref tempCursor, elmPath, typeArgs.get(0).getType(),
                typeArgs.get(0).getTypeArgs().clone(), item);

            // Move item into the set.
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_setScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(setScope);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
            ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().MoveField(tempRef_Row3, tempRef_setScope,
                tempRef_tempCursor));
            tempCursor = tempRef_tempCursor.argValue;
            setScope = tempRef_setScope.argValue;
            dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
        }
    }

    public void DispatchTuple(tangible.RefObject<RowOperationDispatcher> dispatcher,
                              tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        Contract.Requires(typeArgs.getCount() >= 2);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(),
            out tupleScope));
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;

        for (int i = 0; i < typeArgs.getCount(); i++) {
            PropertyInfo valueAccessor = value.getClass().GetProperty(String.format("Item%1$s", i + 1));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            dispatcher.argValue.LayoutCodeSwitch(ref tupleScope, null, typeArgs.get(i).getType(),
                typeArgs.get(i).getTypeArgs().clone(), valueAccessor.GetValue(value));
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
            tupleScope.MoveNext(tempRef_Row2);
            dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
        }
    }

    public void DispatchUDT(tangible.RefObject<RowOperationDispatcher> dispatcher,
                            tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
        RowCursor udtScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_udtScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().WriteScope(tempRef_Row, scope, typeArgs.clone(), tempOut_udtScope));
        udtScope = tempOut_udtScope.argValue;
        dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
        IDispatchable valueDispatcher = value instanceof IDispatchable ? (IDispatchable)value : null;
        assert valueDispatcher != null;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_udtScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(udtScope);
        valueDispatcher.Dispatch(dispatcher, tempRef_udtScope);
        udtScope = tempRef_udtScope.argValue;
    }
}