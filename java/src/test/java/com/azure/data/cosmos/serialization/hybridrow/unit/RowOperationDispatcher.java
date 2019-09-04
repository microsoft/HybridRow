// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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
        this.Row.initLayout(HybridRowVersion.V1, layout, this.Resolver);
    }

    private RowOperationDispatcher(IDispatcher dispatcher, LayoutResolver resolver, String expected) {
        this.dispatcher = dispatcher;
        this.Row = new RowBuffer(RowOperationDispatcher.InitialRowSize);
        this.Resolver = resolver;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: byte[] bytes = ByteConverter.ToBytes(expected);
        byte[] bytes = ByteConverter.ToBytes(expected);
        this.Row.readFrom(bytes, HybridRowVersion.V1, this.Resolver);
    }

    // TODO: C# TO JAVA CONVERTER: The C# 'struct' constraint has no equivalent in Java:
    //ORIGINAL LINE: public static RowOperationDispatcher Create<TDispatcher>(Layout layout, LayoutResolver resolver)
    // where TDispatcher : struct, IDispatcher
    public static <TDispatcher extends IDispatcher> RowOperationDispatcher Create(Layout layout,
                                                                                  LayoutResolver resolver) {
        return new RowOperationDispatcher(null, layout, resolver);
    }

    public RowReader GetReader() {
        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(this.Row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.Row)
        this.Row = tempReference_Row.get();
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
        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(this.Row);
        RowCursor root = RowCursor.Create(tempReference_Row);
        this.Row = tempReference_Row.get();
        Reference<RowCursor> tempReference_root =
            new Reference<RowCursor>(root);
        this.LayoutCodeSwitch(tempReference_root, path, type, typeArgs.clone(), value);
        root = tempReference_root.get();
    }

    public void LayoutCodeSwitch(Reference<RowCursor> scope, String path, LayoutType type,
                                 TypeArgumentList typeArgs) {
        LayoutCodeSwitch(scope, path, type, typeArgs, null);
    }

    public void LayoutCodeSwitch(Reference<RowCursor> scope, String path, LayoutType type) {
        LayoutCodeSwitch(scope, path, type, null, null);
    }

    public void LayoutCodeSwitch(Reference<RowCursor> scope, String path) {
        LayoutCodeSwitch(scope, path, null, null, null);
    }

    public void LayoutCodeSwitch(Reference<RowCursor> scope) {
        LayoutCodeSwitch(scope, null, null, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void LayoutCodeSwitch(ref RowCursor scope, string path = null, LayoutType type = null,
    // TypeArgumentList typeArgs = default, object value = null)
    public void LayoutCodeSwitch(Reference<RowCursor> scope, String path, LayoutType type,
                                 TypeArgumentList typeArgs, Object value) {
        LayoutColumn col = null;
        if (type == null) {
            assert path != null;
            Out<LayoutColumn> tempOut_col =
                new Out<LayoutColumn>();
            assert scope.get().getLayout().TryFind(path, tempOut_col);
            col = tempOut_col.get();
            assert col != null;
            type = col.type();
            typeArgs = col.typeArgs().clone();
        }

        if ((path != null) && (col == null || col.storage() == StorageKind.Sparse)) {
            Reference<RowBuffer> tempReference_Row =
                new Reference<RowBuffer>(this.Row);
            scope.get().Find(tempReference_Row, path);
            this.Row = tempReference_Row.get();
        }

        switch (type.LayoutCode) {
            case Null:
                Reference<RowOperationDispatcher> tempReference_this = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutNull, NullValue>Dispatch(tempReference_this, scope, col, type, NullValue.Default);
                this = tempReference_this.get();
                break;
            case Boolean:
                Reference<RowOperationDispatcher> tempReference_this2 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutBoolean, Boolean>Dispatch(tempReference_this2, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this2.get();

                break;
            case Int8:
                Reference<RowOperationDispatcher> tempReference_this3 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt8, Byte>Dispatch(tempReference_this3, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this3.get();

                break;
            case Int16:
                Reference<RowOperationDispatcher> tempReference_this4 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt16, Short>Dispatch(tempReference_this4, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this4.get();

                break;
            case Int32:
                Reference<RowOperationDispatcher> tempReference_this5 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt32, Integer>Dispatch(tempReference_this5, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this5.get();
                break;
            case Int64:
                Reference<RowOperationDispatcher> tempReference_this6 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt64, Long>Dispatch(tempReference_this6, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this6.get();
                break;
            case UInt8:
                Reference<RowOperationDispatcher> tempReference_this7 = new Reference<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt8, byte>(tempRef_this7, scope, col, type,
                // (Nullable<byte>)value != null ? value : default);
                this.dispatcher.<LayoutUInt8, Byte>Dispatch(tempReference_this7, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this7.get();
                break;
            case UInt16:
                Reference<RowOperationDispatcher> tempReference_this8 = new Reference<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt16, ushort>(tempRef_this8, scope, col, type,
                // (Nullable<ushort>)value != null ? value : default);
                this.dispatcher.<LayoutUInt16, Short>Dispatch(tempReference_this8, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this8.get();

                break;
            case UInt32:
                Reference<RowOperationDispatcher> tempReference_this9 = new Reference<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt32, uint>(tempRef_this9, scope, col, type,
                // (Nullable<uint>)value != null ? value : default);
                this.dispatcher.<LayoutUInt32, Integer>Dispatch(tempReference_this9, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this9.get();
                break;
            case UInt64:
                Reference<RowOperationDispatcher> tempReference_this10 = new Reference<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt64, ulong>(tempRef_this10, scope, col, type,
                // (Nullable<ulong>)value != null ? value : default);
                this.dispatcher.<LayoutUInt64, Long>Dispatch(tempReference_this10, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this10.get();

                break;
            case VarInt:
                Reference<RowOperationDispatcher> tempReference_this11 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutVarInt, Long>Dispatch(tempReference_this11, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this11.get();
                break;
            case VarUInt:
                Reference<RowOperationDispatcher> tempReference_this12 = new Reference<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutVarUInt, ulong>(tempRef_this12, scope, col, type,
                // (Nullable<ulong>)value != null ? value : default);
                this.dispatcher.<LayoutVarUInt, Long>Dispatch(tempReference_this12, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this12.get();

                break;
            case Float32:
                Reference<RowOperationDispatcher> tempReference_this13 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat32, Float>Dispatch(tempReference_this13, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this13.get();

                break;
            case Float64:
                Reference<RowOperationDispatcher> tempReference_this14 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat64, Double>Dispatch(tempReference_this14, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this14.get();

                break;
            case Float128:
                Reference<RowOperationDispatcher> tempReference_this15 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat128, Float128>Dispatch(tempReference_this15, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this15.get();

                break;
            case Decimal:
                Reference<RowOperationDispatcher> tempReference_this16 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutDecimal, BigDecimal>Dispatch(tempReference_this16, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this16.get();

                break;
            case DateTime:
                Reference<RowOperationDispatcher> tempReference_this17 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutDateTime, LocalDateTime>Dispatch(tempReference_this17, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this17.get();

                break;
            case UnixDateTime:
                Reference<RowOperationDispatcher> tempReference_this18 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutUnixDateTime, UnixDateTime>Dispatch(tempReference_this18, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this18.get();

                break;
            case Guid:
                Reference<RowOperationDispatcher> tempReference_this19 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutGuid, UUID>Dispatch(tempReference_this19, scope, col, type, value != null ?
                    value :
            default)
                this = tempReference_this19.get();
                break;
            case MongoDbObjectId:
                Reference<RowOperationDispatcher> tempReference_this20 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutMongoDbObjectId, MongoDbObjectId>Dispatch(tempReference_this20, scope, col, type,
                    value != null ? value :
            default)
                this = tempReference_this20.get();

                break;
            case Utf8:
                Reference<RowOperationDispatcher> tempReference_this21 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.<LayoutUtf8, String>Dispatch(tempReference_this21, scope, col, type, (String)value);
                this = tempReference_this21.get();

                break;
            case Binary:
                Reference<RowOperationDispatcher> tempReference_this22 = new Reference<RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutBinary, byte[]>(ref this, ref scope, col, type,
                // (byte[])value);
                this.dispatcher.<LayoutBinary, byte[]>Dispatch(tempReference_this22, scope, col, type, (byte[])value);
                this = tempReference_this22.get();

                break;
            case ObjectScope:
            case ImmutableObjectScope:
                Reference<RowOperationDispatcher> tempReference_this23 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchObject(tempReference_this23, scope);
                this = tempReference_this23.get();
                break;
            case TypedArrayScope:
            case ImmutableTypedArrayScope:
                Reference<RowOperationDispatcher> tempReference_this24 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchArray(tempReference_this24, scope, type, typeArgs.clone(), value);
                this = tempReference_this24.get();
                break;
            case TypedSetScope:
            case ImmutableTypedSetScope:
                Reference<RowOperationDispatcher> tempReference_this25 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchSet(tempReference_this25, scope, type, typeArgs.clone(), value);
                this = tempReference_this25.get();
                break;
            case TypedMapScope:
            case ImmutableTypedMapScope:
                Reference<RowOperationDispatcher> tempReference_this26 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchMap(tempReference_this26, scope, type, typeArgs.clone(), value);
                this = tempReference_this26.get();
                break;
            case TupleScope:
            case ImmutableTupleScope:
            case TypedTupleScope:
            case ImmutableTypedTupleScope:
            case TaggedScope:
            case ImmutableTaggedScope:
            case Tagged2Scope:
            case ImmutableTagged2Scope:
                Reference<RowOperationDispatcher> tempReference_this27 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchTuple(tempReference_this27, scope, type, typeArgs.clone(), value);
                this = tempReference_this27.get();
                break;
            case NullableScope:
                Reference<RowOperationDispatcher> tempReference_this28 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchNullable(tempReference_this28, scope, type, typeArgs.clone(), value);
                this = tempReference_this28.get();
                break;
            case Schema:
            case ImmutableSchema:
                Reference<RowOperationDispatcher> tempReference_this29 = new Reference<RowOperationDispatcher>(this);
                this.dispatcher.DispatchUDT(tempReference_this29, scope, type, typeArgs.clone(), value);
                this = tempReference_this29.get();
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
            this.Row.writeTo(stm);
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