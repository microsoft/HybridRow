//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> field, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, field, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor
    // field, LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> field, LayoutColumn col, LayoutType t, TValue value) {
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                Reference<RowBuffer> tempReference_Row =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                ResultAssert.IsSuccess(t.<TLayout>typeAs().writeFixed(tempReference_Row, field, col, value));
                dispatcher.get().argValue.Row = tempReference_Row.get();
                break;
            case Variable:
                Reference<RowBuffer> tempReference_Row2 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                ResultAssert.IsSuccess(t.<TLayout>typeAs().writeVariable(tempReference_Row2, field, col, value));
                dispatcher.get().argValue.Row = tempReference_Row2.get();
                break;
            default:
                Reference<RowBuffer> tempReference_Row3 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                ResultAssert.IsSuccess(t.<TLayout>typeAs().writeSparse(tempReference_Row3, field, value));
                dispatcher.get().argValue.Row = tempReference_Row3.get();
                break;
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
        ResultAssert.IsSuccess(t.<LayoutTypedArray>typeAs().WriteScope(tempReference_Row, scope, typeArgs.clone(),
            tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();

        List items = (List)value;
        for (Object item : items) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).type(),
                typeArgs.get(0).typeArgs().clone(), item);
            Reference<RowBuffer> tempReference_Row2 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            arrayScope.MoveNext(tempReference_Row2);
            dispatcher.get().argValue.Row = tempReference_Row2.get();
        }
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
        ResultAssert.IsSuccess(t.<LayoutTypedMap>typeAs().WriteScope(tempReference_Row, scope, typeArgs.clone(),
            tempOut_mapScope));
        mapScope = tempOut_mapScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        Reference<RowCursor> tempReference_mapScope =
            new Reference<RowCursor>(mapScope);
        TypeArgument fieldType = t.<LayoutUniqueScope>typeAs().FieldType(tempReference_mapScope).clone();
        mapScope = tempReference_mapScope.get();
        List pairs = (List)value;
        for (Object pair : pairs) {
            String elmPath = UUID.NewGuid().toString();
            Reference<RowBuffer> tempReference_Row2 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            RowCursor.CreateForAppend(tempReference_Row2, out tempCursor);
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tempCursor, elmPath, fieldType.getType(),
                fieldType.getTypeArgs().clone(), pair);

            // Move item into the map.
            Reference<RowBuffer> tempReference_Row3 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            Reference<RowCursor> tempReference_mapScope2 =
                new Reference<RowCursor>(mapScope);
            Reference<RowCursor> tempReference_tempCursor =
                new Reference<RowCursor>(tempCursor);
            ResultAssert.IsSuccess(t.<LayoutTypedMap>typeAs().MoveField(tempReference_Row3, tempReference_mapScope2,
                tempReference_tempCursor));
            tempCursor = tempReference_tempCursor.get();
            mapScope = tempReference_mapScope2.get();
            dispatcher.get().argValue.Row = tempReference_Row3.get();
        }
    }

    public void DispatchNullable(Reference<RowOperationDispatcher> dispatcher,
                                 Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        checkArgument(typeArgs.count() == 1);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor nullableScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(t.<LayoutNullable>typeAs().WriteScope(tempReference_Row, scope, typeArgs.clone(),
            value != null, out nullableScope));
        dispatcher.get().argValue.Row = tempReference_Row.get();

        if (value != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref nullableScope, null, typeArgs.get(0).type(),
                typeArgs.get(0).typeArgs().clone(), value);
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
        ResultAssert.IsSuccess(t.<LayoutTypedSet>typeAs().WriteScope(tempReference_Row, scope, typeArgs.clone(),
            tempOut_setScope));
        setScope = tempOut_setScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        List items = (List)value;
        for (Object item : items) {
            String elmPath = UUID.NewGuid().toString();
            Reference<RowBuffer> tempReference_Row2 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            RowCursor.CreateForAppend(tempReference_Row2, out tempCursor);
            dispatcher.get().argValue.Row = tempReference_Row2.get();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tempCursor, elmPath, typeArgs.get(0).type(),
                typeArgs.get(0).typeArgs().clone(), item);

            // Move item into the set.
            Reference<RowBuffer> tempReference_Row3 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            Reference<RowCursor> tempReference_setScope =
                new Reference<RowCursor>(setScope);
            Reference<RowCursor> tempReference_tempCursor =
                new Reference<RowCursor>(tempCursor);
            ResultAssert.IsSuccess(t.<LayoutTypedSet>typeAs().MoveField(tempReference_Row3, tempReference_setScope,
                tempReference_tempCursor));
            tempCursor = tempReference_tempCursor.get();
            setScope = tempReference_setScope.get();
            dispatcher.get().argValue.Row = tempReference_Row3.get();
        }
    }

    public void DispatchTuple(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.count() >= 2);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>typeAs().WriteScope(tempReference_Row, scope, typeArgs.clone(),
            out tupleScope));
        dispatcher.get().argValue.Row = tempReference_Row.get();

        for (int i = 0; i < typeArgs.count(); i++) {
            PropertyInfo valueAccessor = value.getClass().GetProperty(String.format("Item%1$s", i + 1));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            dispatcher.get().LayoutCodeSwitch(ref tupleScope, null, typeArgs.get(i).type(),
                typeArgs.get(i).typeArgs().clone(), valueAccessor.GetValue(value));
            Reference<RowBuffer> tempReference_Row2 =
                new Reference<RowBuffer>(dispatcher.get().Row);
            tupleScope.MoveNext(tempReference_Row2);
            dispatcher.get().argValue.Row = tempReference_Row2.get();
        }
    }

    public void DispatchUDT(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        Reference<RowBuffer> tempReference_Row = new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor udtScope;
        Out<RowCursor> tempOut_udtScope = new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutUDT>typeAs().WriteScope(tempReference_Row, scope, typeArgs.clone(), tempOut_udtScope));
        udtScope = tempOut_udtScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        IDispatchable valueDispatcher = value instanceof IDispatchable ? (IDispatchable)value : null;
        assert valueDispatcher != null;
        Reference<RowCursor> tempReference_udtScope = new Reference<RowCursor>(udtScope);
        valueDispatcher.Dispatch(dispatcher, tempReference_udtScope);
        udtScope = tempReference_udtScope.get();
    }
}