//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.Float128;
import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.NullValue;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;
import azure.data.cosmos.serialization.hybridrow.UnixDateTime;

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
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.Row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.Row)
        this.Row = tempRef_Row.argValue;
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
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.Row);
        RowCursor root = RowCursor.Create(tempRef_Row);
        this.Row = tempRef_Row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        this.LayoutCodeSwitch(tempRef_root, path, type, typeArgs.clone(), value);
        root = tempRef_root.argValue;
    }

    public void LayoutCodeSwitch(tangible.RefObject<RowCursor> scope, String path, LayoutType type,
                                 TypeArgumentList typeArgs) {
        LayoutCodeSwitch(scope, path, type, typeArgs, null);
    }

    public void LayoutCodeSwitch(tangible.RefObject<RowCursor> scope, String path, LayoutType type) {
        LayoutCodeSwitch(scope, path, type, null, null);
    }

    public void LayoutCodeSwitch(tangible.RefObject<RowCursor> scope, String path) {
        LayoutCodeSwitch(scope, path, null, null, null);
    }

    public void LayoutCodeSwitch(tangible.RefObject<RowCursor> scope) {
        LayoutCodeSwitch(scope, null, null, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void LayoutCodeSwitch(ref RowCursor scope, string path = null, LayoutType type = null,
    // TypeArgumentList typeArgs = default, object value = null)
    public void LayoutCodeSwitch(tangible.RefObject<RowCursor> scope, String path, LayoutType type,
                                 TypeArgumentList typeArgs, Object value) {
        LayoutColumn col = null;
        if (type == null) {
            assert path != null;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_col =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert scope.argValue.getLayout().TryFind(path, tempOut_col);
            col = tempOut_col.argValue;
            assert col != null;
            type = col.getType();
            typeArgs = col.getTypeArgs().clone();
        }

        if ((path != null) && (col == null || col.getStorage() == StorageKind.Sparse)) {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.Row);
            scope.argValue.Find(tempRef_Row, path);
            this.Row = tempRef_Row.argValue;
        }

        switch (type.LayoutCode) {
            case Null:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutNull, NullValue>Dispatch(tempRef_this, scope, col, type, NullValue.Default);
                this = tempRef_this.argValue;
                break;
            case Boolean:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this2 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutBoolean, Boolean>Dispatch(tempRef_this2, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this2.argValue;

                break;
            case Int8:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this3 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt8, Byte>Dispatch(tempRef_this3, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this3.argValue;

                break;
            case Int16:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this4 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt16, Short>Dispatch(tempRef_this4, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this4.argValue;

                break;
            case Int32:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this5 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt32, Integer>Dispatch(tempRef_this5, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this5.argValue;
                break;
            case Int64:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this6 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutInt64, Long>Dispatch(tempRef_this6, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this6.argValue;
                break;
            case UInt8:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this7 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt8, byte>(tempRef_this7, scope, col, type,
                // (Nullable<byte>)value != null ? value : default);
                this.dispatcher.<LayoutUInt8, Byte>Dispatch(tempRef_this7, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this7.argValue;
                break;
            case UInt16:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this8 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt16, ushort>(tempRef_this8, scope, col, type,
                // (Nullable<ushort>)value != null ? value : default);
                this.dispatcher.<LayoutUInt16, Short>Dispatch(tempRef_this8, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this8.argValue;

                break;
            case UInt32:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this9 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt32, uint>(tempRef_this9, scope, col, type,
                // (Nullable<uint>)value != null ? value : default);
                this.dispatcher.<LayoutUInt32, Integer>Dispatch(tempRef_this9, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this9.argValue;
                break;
            case UInt64:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this10 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutUInt64, ulong>(tempRef_this10, scope, col, type,
                // (Nullable<ulong>)value != null ? value : default);
                this.dispatcher.<LayoutUInt64, Long>Dispatch(tempRef_this10, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this10.argValue;

                break;
            case VarInt:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this11 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutVarInt, Long>Dispatch(tempRef_this11, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this11.argValue;
                break;
            case VarUInt:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this12 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutVarUInt, ulong>(tempRef_this12, scope, col, type,
                // (Nullable<ulong>)value != null ? value : default);
                this.dispatcher.<LayoutVarUInt, Long>Dispatch(tempRef_this12, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this12.argValue;

                break;
            case Float32:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this13 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat32, Float>Dispatch(tempRef_this13, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this13.argValue;

                break;
            case Float64:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this14 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat64, Double>Dispatch(tempRef_this14, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this14.argValue;

                break;
            case Float128:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this15 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutFloat128, Float128>Dispatch(tempRef_this15, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this15.argValue;

                break;
            case Decimal:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this16 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutDecimal, BigDecimal>Dispatch(tempRef_this16, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this16.argValue;

                break;
            case DateTime:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this17 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutDateTime, LocalDateTime>Dispatch(tempRef_this17, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this17.argValue;

                break;
            case UnixDateTime:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this18 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutUnixDateTime, UnixDateTime>Dispatch(tempRef_this18, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this18.argValue;

                break;
            case Guid:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this19 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutGuid, UUID>Dispatch(tempRef_this19, scope, col, type, value != null ?
                    value :
            default)
                this = tempRef_this19.argValue;
                break;
            case MongoDbObjectId:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this20 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutMongoDbObjectId, MongoDbObjectId>Dispatch(tempRef_this20, scope, col, type,
                    value != null ? value :
            default)
                this = tempRef_this20.argValue;

                break;
            case Utf8:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this21 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.<LayoutUtf8, String>Dispatch(tempRef_this21, scope, col, type, (String)value);
                this = tempRef_this21.argValue;

                break;
            case Binary:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this22 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.dispatcher.Dispatch<LayoutBinary, byte[]>(ref this, ref scope, col, type,
                // (byte[])value);
                this.dispatcher.<LayoutBinary, byte[]>Dispatch(tempRef_this22, scope, col, type, (byte[])value);
                this = tempRef_this22.argValue;

                break;
            case ObjectScope:
            case ImmutableObjectScope:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this23 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchObject(tempRef_this23, scope);
                this = tempRef_this23.argValue;
                break;
            case TypedArrayScope:
            case ImmutableTypedArrayScope:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this24 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchArray(tempRef_this24, scope, type, typeArgs.clone(), value);
                this = tempRef_this24.argValue;
                break;
            case TypedSetScope:
            case ImmutableTypedSetScope:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this25 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchSet(tempRef_this25, scope, type, typeArgs.clone(), value);
                this = tempRef_this25.argValue;
                break;
            case TypedMapScope:
            case ImmutableTypedMapScope:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this26 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchMap(tempRef_this26, scope, type, typeArgs.clone(), value);
                this = tempRef_this26.argValue;
                break;
            case TupleScope:
            case ImmutableTupleScope:
            case TypedTupleScope:
            case ImmutableTypedTupleScope:
            case TaggedScope:
            case ImmutableTaggedScope:
            case Tagged2Scope:
            case ImmutableTagged2Scope:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this27 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchTuple(tempRef_this27, scope, type, typeArgs.clone(), value);
                this = tempRef_this27.argValue;
                break;
            case NullableScope:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this28 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchNullable(tempRef_this28, scope, type, typeArgs.clone(), value);
                this = tempRef_this28.argValue;
                break;
            case Schema:
            case ImmutableSchema:
                tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher> tempRef_this29 = new tangible.RefObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.RowOperationDispatcher>(this);
                this.dispatcher.DispatchUDT(tempRef_this29, scope, type, typeArgs.clone(), value);
                this = tempRef_this29.argValue;
                break;
            default:
                Contract.Assert(false, String.format("Unknown type will be ignored: %1$s", type.LayoutCode));
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