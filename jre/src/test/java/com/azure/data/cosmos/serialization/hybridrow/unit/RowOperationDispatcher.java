//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBinary;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBoolean;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutDecimal;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutFloat128;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutFloat32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutFloat64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutGuid;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt16;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutMongoDbObjectId;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutNull;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt16;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUtf8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutVarInt;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutVarUInt;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1402 // FileMayOnlyContainASingleType
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1201 // OrderingRules
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Public Fields


//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal ref struct RowOperationDispatcher
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class RowOperationDispatcher {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    public LayoutResolver Resolver;
    public RowBuffer Row = new RowBuffer();
    private IDispatcher dispatcher;

    public RowOperationDispatcher() {
    }

    private RowOperationDispatcher(IDispatcher dispatcher, Layout layout, LayoutResolver resolver) {
        this.dispatcher = dispatcher;
        this.Row = new RowBuffer(RowOperationDispatcher.InitialRowSize);
        this.Resolver = resolver;
        this.Row.InitLayout(HybridRowVersion.V1, layout, this.Resolver);
    }

    private RowOperationDispatcher(IDispatcher dispatcher, LayoutResolver resolver, String expected) {
        this.dispatcher = dispatcher;
        this.Row = new RowBuffer(RowOperationDispatcher.InitialRowSize);
        this.Resolver = resolver;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: byte[] bytes = ByteConverter.ToBytes(expected);
        byte[] bytes = ByteConverter.ToBytes(expected);
        this.Row.ReadFrom(bytes, HybridRowVersion.V1, this.Resolver);
    }

    // TODO: C# TO JAVA CONVERTER: The C# 'struct' constraint has no equivalent in Java:
    //ORIGINAL LINE: public static RowOperationDispatcher Create<TDispatcher>(Layout layout, LayoutResolver resolver)
    // where TDispatcher : struct, IDispatcher
    public static <TDispatcher extends IDispatcher> RowOperationDispatcher Create(Layout layout,
                                                                                  LayoutResolver resolver) {
        return new RowOperationDispatcher(null, layout, resolver);
    }

    public RowReader GetReader() {
        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(this.Row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.Row)
        this.Row = tempRef_Row.get();
        return tempVar;
    }

    public void LayoutCodeSwitch(String path, LayoutType type, TypeArgumentList typeArgs) {
        LayoutCodeSwitch(path, type, typeArgs, null);
    }

    public void LayoutCodeSwitch(String path, LayoutType type) {
        LayoutCodeSwitch(path, type, null, null);
    }

    public void LayoutCodeSwitch(String path) {
        LayoutCodeSwitch(path, null, null, null);
    }

    public void LayoutCodeSwitch() {
        LayoutCodeSwitch(null, null, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void LayoutCodeSwitch(string path = null, LayoutType type = null, TypeArgumentList
    // typeArgs = default, object value = null)
    public void LayoutCodeSwitch(String path, LayoutType type, TypeArgumentList typeArgs, Object value) {
        RefObject<RowBuffer> tempRef_Row =
            new RefObject<RowBuffer>(this.Row);
        RowCursor root = RowCursor.Create(tempRef_Row);
        this.Row = tempRef_Row.get();
        RefObject<RowCursor> tempRef_root =
            new RefObject<RowCursor>(root);
        this.LayoutCodeSwitch(tempRef_root, path, type, typeArgs.clone(), value);
        root = tempRef_root.get();
    }

    public void LayoutCodeSwitch(RefObject<RowCursor> scope, String path, LayoutType type,
                                 TypeArgumentList typeArgs) {
        LayoutCodeSwitch(scope, path, type, typeArgs, null);
    }

    public void LayoutCodeSwitch(RefObject<RowCursor> scope, String path, LayoutType type) {
        LayoutCodeSwitch(scope, path, type, null, null);
    }

    public void LayoutCodeSwitch(RefObject<RowCursor> scope, String path) {
        LayoutCodeSwitch(scope, path, null, null, null);
    }

    public void LayoutCodeSwitch(RefObject<RowCursor> scope) {
        LayoutCodeSwitch(scope, null, null, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void LayoutCodeSwitch(ref RowCursor scope, string path = null, LayoutType type = null,
    // TypeArgumentList typeArgs = default, object value = null)
    public void LayoutCodeSwitch(RefObject<RowCursor> scope, String path, LayoutType type,
                                 TypeArgumentList typeArgs, Object value) {
        LayoutColumn col = null;
        if (type == null) {
            assert path != null;
            OutObject<LayoutColumn> tempOut_col =
                new OutObject<LayoutColumn>();
            assert scope.get().getLayout().TryFind(path, tempOut_col);
            col = tempOut_col.get();
            assert col != null;
            type = col.getType();
            typeArgs = col.getTypeArgs().clone();
        }

        if ((path != null) && (col == null || col.getStorage() == StorageKind.Sparse)) {
            RefObject<RowBuffer> tempRef_Row =
                new RefObject<RowBuffer>(this.Row);
            scope.get().Find(tempRef_Row, path);
            this.Row = tempRef_Row.get();
        }

        switch (type.LayoutCode) {
            case Null:
                RefObject<RowOperationDispatcher> tempRef_this = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutNull, NullValue>Dispatch(tempRef_this, scope, col, type, NullValue.Default);
                this = tempRef_this.get();
                break;
            case Boolean:
                RefObject<RowOperationDispatcher> tempRef_this2 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutBoolean, Boolean>Dispatch(tempRef_this2, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this2.get();

                break;
            case Int8:
                RefObject<RowOperationDispatcher> tempRef_this3 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt8, Byte>Dispatch(tempRef_this3, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this3.get();

                break;
            case Int16:
                RefObject<RowOperationDispatcher> tempRef_this4 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt16, Short>Dispatch(tempRef_this4, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this4.get();

                break;
            case Int32:
                RefObject<RowOperationDispatcher> tempRef_this5 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt32, Integer>Dispatch(tempRef_this5, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this5.get();
                break;
            case Int64:
                RefObject<RowOperationDispatcher> tempRef_this6 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt64, Long>Dispatch(tempRef_this6, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this6.get();
                break;
            case UInt8:
                RefObject<RowOperationDispatcher> tempRef_this7 = new RefObject<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt8, byte>(tempRef_this7, scope, col, type,
                // (Nullable<byte>)value != null ? value : default);
                this.dispatcher.<LayoutUInt8, Byte>Dispatch(tempRef_this7, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this7.get();
                break;
            case UInt16:
                RefObject<RowOperationDispatcher> tempRef_this8 = new RefObject<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt16, ushort>(tempRef_this8, scope, col, type,
                // (Nullable<ushort>)value != null ? value : default);
                this.dispatcher.<LayoutUInt16, Short>Dispatch(tempRef_this8, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this8.get();

                break;
            case UInt32:
                RefObject<RowOperationDispatcher> tempRef_this9 = new RefObject<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt32, uint>(tempRef_this9, scope, col, type,
                // (Nullable<uint>)value != null ? value : default);
                this.dispatcher.<LayoutUInt32, Integer>Dispatch(tempRef_this9, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this9.get();
                break;
            case UInt64:
                RefObject<RowOperationDispatcher> tempRef_this10 = new RefObject<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt64, ulong>(tempRef_this10, scope, col, type,
                // (Nullable<ulong>)value != null ? value : default);
                this.dispatcher.<LayoutUInt64, Long>Dispatch(tempRef_this10, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this10.get();

                break;
            case VarInt:
                RefObject<RowOperationDispatcher> tempRef_this11 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutVarInt, Long>Dispatch(tempRef_this11, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this11.get();
                break;
            case VarUInt:
                RefObject<RowOperationDispatcher> tempRef_this12 = new RefObject<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutVarUInt, ulong>(tempRef_this12, scope, col, type,
                // (Nullable<ulong>)value != null ? value : default);
                this.dispatcher.<LayoutVarUInt, Long>Dispatch(tempRef_this12, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this12.get();

                break;
            case Float32:
                RefObject<RowOperationDispatcher> tempRef_this13 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat32, Float>Dispatch(tempRef_this13, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this13.get();

                break;
            case Float64:
                RefObject<RowOperationDispatcher> tempRef_this14 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat64, Double>Dispatch(tempRef_this14, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this14.get();

                break;
            case Float128:
                RefObject<RowOperationDispatcher> tempRef_this15 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat128, Float128>Dispatch(tempRef_this15, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this15.get();

                break;
            case Decimal:
                RefObject<RowOperationDispatcher> tempRef_this16 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutDecimal, BigDecimal>Dispatch(tempRef_this16, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this16.get();

                break;
            case DateTime:
                RefObject<RowOperationDispatcher> tempRef_this17 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutDateTime, LocalDateTime>Dispatch(tempRef_this17, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this17.get();

                break;
            case UnixDateTime:
                RefObject<RowOperationDispatcher> tempRef_this18 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutUnixDateTime, UnixDateTime>Dispatch(tempRef_this18, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this18.get();

                break;
            case Guid:
                RefObject<RowOperationDispatcher> tempRef_this19 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutGuid, UUID>Dispatch(tempRef_this19, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this19.get();
                break;
            case MongoDbObjectId:
                RefObject<RowOperationDispatcher> tempRef_this20 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutMongoDbObjectId, MongoDbObjectId>Dispatch(tempRef_this20, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this20.get();

                break;
            case Utf8:
                RefObject<RowOperationDispatcher> tempRef_this21 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutUtf8, String>Dispatch(tempRef_this21, scope, col, type, (String)value);
                this = tempRef_this21.get();

                break;
            case Binary:
                RefObject<RowOperationDispatcher> tempRef_this22 = new RefObject<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutBinary, byte[]>(ref this, ref scope, col, type,
                // (byte[])value);
                this.dispatcher.<LayoutBinary, byte[]>Dispatch(tempRef_this22, scope, col, type, (byte[])value);
                this = tempRef_this22.get();

                break;
            case ObjectScope:
            case ImmutableObjectScope:
                RefObject<RowOperationDispatcher> tempRef_this23 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchObject(tempRef_this23, scope);
                this = tempRef_this23.get();
                break;
            case TypedArrayScope:
            case ImmutableTypedArrayScope:
                RefObject<RowOperationDispatcher> tempRef_this24 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchArray(tempRef_this24, scope, type, typeArgs.clone(), value);
                this = tempRef_this24.get();
                break;
            case TypedSetScope:
            case ImmutableTypedSetScope:
                RefObject<RowOperationDispatcher> tempRef_this25 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchSet(tempRef_this25, scope, type, typeArgs.clone(), value);
                this = tempRef_this25.get();
                break;
            case TypedMapScope:
            case ImmutableTypedMapScope:
                RefObject<RowOperationDispatcher> tempRef_this26 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchMap(tempRef_this26, scope, type, typeArgs.clone(), value);
                this = tempRef_this26.get();
                break;
            case TupleScope:
            case ImmutableTupleScope:
            case TypedTupleScope:
            case ImmutableTypedTupleScope:
            case TaggedScope:
            case ImmutableTaggedScope:
            case Tagged2Scope:
            case ImmutableTagged2Scope:
                RefObject<RowOperationDispatcher> tempRef_this27 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchTuple(tempRef_this27, scope, type, typeArgs.clone(), value);
                this = tempRef_this27.get();
                break;
            case NullableScope:
                RefObject<RowOperationDispatcher> tempRef_this28 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchNullable(tempRef_this28, scope, type, typeArgs.clone(), value);
                this = tempRef_this28.get();
                break;
            case Schema:
            case ImmutableSchema:
                RefObject<RowOperationDispatcher> tempRef_this29 = new RefObject<RowOperationDispatcher>(this);
                this.dispatcher.DispatchUDT(tempRef_this29, scope, type, typeArgs.clone(), value);
                this = tempRef_this29.get();
                break;
            default:
                if (logger.)
                throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", type.LayoutCode));
                break;
        }
    }

    // TODO: C# TO JAVA CONVERTER: The C# 'struct' constraint has no equivalent in Java:
    //ORIGINAL LINE: public static RowOperationDispatcher ReadFrom<TDispatcher>(LayoutResolver resolver, string
    // expected) where TDispatcher : struct, IDispatcher
    public static <TDispatcher extends IDispatcher> RowOperationDispatcher ReadFrom(LayoutResolver resolver,
                                                                                    String expected) {
        return new RowOperationDispatcher(null, resolver, expected);
    }

    public String RowToHex() {
        try (MemoryStream stm = new MemoryStream()) {
            this.Row.WriteTo(stm);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ReadOnlyMemory<byte> bytes = stm.GetBuffer().AsMemory(0, (int)stm.Position);
            ReadOnlyMemory<Byte> bytes = stm.GetBuffer().AsMemory(0, (int)stm.Position);
            return ByteConverter.ToHex(bytes.Span);
        }
    }

    public RowOperationDispatcher clone() {
        RowOperationDispatcher varCopy = new RowOperationDispatcher();

        varCopy.Resolver = this.Resolver;
        varCopy.Row = this.Row.clone();
        varCopy.dispatcher = this.dispatcher;

        return varCopy;
    }
}