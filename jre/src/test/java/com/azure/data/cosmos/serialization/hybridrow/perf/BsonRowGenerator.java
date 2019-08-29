//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.perf;

import MongoDB.Bson.*;
import MongoDB.Bson.IO.*;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import org.bson.BsonBinaryWriter;
import org.bson.BsonWriter;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.lenientFormat;

public final class BsonRowGenerator implements Closeable {
    private Layout layout;
    private LayoutResolver resolver;
    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.MemoryStream is
    // input or output:
    private MemoryStream stream;
    private BsonWriter writer;

    public BsonRowGenerator(int capacity, Layout layout, LayoutResolver resolver) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.MemoryStream
        // is input or output:
        this.stream = new MemoryStream(capacity);
        this.writer = new BsonBinaryWriter(this.stream);
        this.layout = layout;
        this.resolver = resolver;
    }

    public int getLength() {
        return (int)this.stream.Position;
    }

    public void Reset() {
        this.stream.SetLength(0);
        this.stream.Position = 0;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.stream.ToArray();
    }

    public void WriteBuffer(HashMap<Utf8String, Object> dict) {
        this.writer.writeStartDocument();
        for (LayoutColumn c : this.layout.columns()) {
            this.LayoutCodeSwitch(c.getPath(), c.getTypeArg().clone(), dict.get(c.getPath()));
        }

        this.writer.writeEndDocument();
    }

    public void close() throws IOException {
        this.writer.Dispose();
        this.stream.Dispose();
    }

    private void DispatchArray(TypeArgument typeArg, Object value) {

        checkArgument(typeArg.typeArgs().count() == 1);
        this.writer.writeStartArray();

        for (Object item : (ArrayList<Object>)value) {
            this.LayoutCodeSwitch(null, typeArg.typeArgs().get(0).clone(), item);
        }

        this.writer.writeEndArray();
    }

    private void DispatchMap(TypeArgument typeArg, Object value) {
        checkArgument(typeArg.typeArgs().count() == 2);

        this.writer.writeStartArray();
        for (Object item : (ArrayList<Object>)value) {
            this.DispatchTuple(typeArg.clone(), item);
        }

        this.writer.writeEndArray();
    }

    private void DispatchNullable(TypeArgument typeArg, Object value) {
        checkArgument(typeArg.typeArgs().count() == 1);

        if (value != null) {
            this.LayoutCodeSwitch(null, typeArg.typeArgs().get(0).clone(), value);
        } else {
            this.writer.writeNull();
        }
    }

    private void DispatchObject(TypeArgument typeArg, Object value) {
        this.writer.writeStartDocument();
        // TODO: support properties in an object scope.
        this.writer.writeEndDocument();
    }

    private void DispatchSet(TypeArgument typeArg, Object value) {
        checkArgument(typeArg.typeArgs().count() == 1);

        this.writer.WriteStartArray();
        for (Object item : (ArrayList<Object>)value) {
            this.LayoutCodeSwitch(null, typeArg.typeArgs().get(0).clone(), item);
        }

        this.writer.WriteEndArray();
    }

    private void DispatchTuple(TypeArgument typeArg, Object value) {
        checkArgument(typeArg.typeArgs().count() >= 2);
        ArrayList<Object> items = (ArrayList<Object>)value;
        checkArgument(items.size() == typeArg.typeArgs().count());

        this.writer.WriteStartArray();
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            this.LayoutCodeSwitch(null, typeArg.typeArgs().get(i).clone(), item);
        }

        this.writer.WriteEndArray();
    }

    private void DispatchUDT(TypeArgument typeArg, Object value) {
        this.writer.WriteStartDocument();

        HashMap<Utf8String, Object> dict = (HashMap<Utf8String, Object>)value;
        Layout udt = this.resolver.resolve(typeArg.typeArgs().schemaId().clone());
        for (LayoutColumn c : udt.columns()) {
            this.LayoutCodeSwitch(c.getPath(), c.getTypeArg().clone(), dict.get(c.getPath()));
        }

        this.writer.WriteEndDocument();
    }

    private void LayoutCodeSwitch(UtfAnyString path, TypeArgument typeArg, Object value) {
        if (!path.IsNull) {
            this.writer.WriteName(path);
        }

        switch (typeArg.type().LayoutCode) {
            case Null:
                this.writer.WriteNull();
                return;

            case Boolean:
                this.writer.WriteBoolean(value == null ? false : (Boolean)value);
                return;

            case Int8:
                this.writer.WriteInt32(value == null ? 0 : (byte)value);
                return;

            case Int16:
                this.writer.WriteInt32(value == null ? 0 : (Short)value);
                return;

            case Int32:
                this.writer.WriteInt32(value == null ? 0 : (Integer)value);
                return;

            case Int64:
                this.writer.WriteInt64(value == null ? 0 : (Long)value);
                return;

            case UInt8:
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.writer.WriteInt32(value == null ? default(byte) : (byte)value);
                this.writer.WriteInt32(value == null ? 0 : (Byte)value);
                return;

            case UInt16:
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.writer.WriteInt32(value == null ? default(ushort) : (ushort)value);
                this.writer.WriteInt32(value == null ? 0 : (short)value);
                return;

            case UInt32:
                // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
                //ORIGINAL LINE: this.writer.WriteInt32(value == null ? default(int) : unchecked((int)(uint)value));
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                this.writer.WriteInt32(value == null ? 0 : (int)(int)value);
                return;

            case UInt64:
                // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
                //ORIGINAL LINE: this.writer.WriteInt64(value == null ? default(long) : unchecked((long)(ulong)value));
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                this.writer.WriteInt64(value == null ? 0 : (long)(long)value);
                return;

            case VarInt:
                this.writer.WriteInt64(value == null ? 0 : (Long)value);
                return;

            case VarUInt:
                // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
                //ORIGINAL LINE: this.writer.WriteInt64(value == null ? default(long) : unchecked((long)(ulong)value));
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                this.writer.WriteInt64(value == null ? 0 : (long)(long)value);
                return;

            case Float32:
                this.writer.WriteDouble(value == null ? 0 : (Float)value);
                return;

            case Float64:
                this.writer.WriteDouble(value == null ? 0 : (Double)value);
                return;

            case Float128:
                Decimal128 d128 = null;
                if (value != null) {
                    Float128 f128 = (Float128)value;
                    // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
                    //ORIGINAL LINE: d128 = unchecked(Decimal128.FromIEEEBits((ulong)f128.High, (ulong)f128.Low));
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    d128 = Decimal128.FromIEEEBits((long)f128.High, (long)f128.Low);
                }

                this.writer.WriteDecimal128(d128);
                return;

            case Decimal:
                this.writer.WriteDecimal128(value == null ? null : new Decimal128((BigDecimal)value));
                return;

            case DateTime:
                this.writer.WriteDateTime(value == null ? 0 : ((LocalDateTime)value).getTime());
                return;

            case UnixDateTime:
                this.writer.WriteDateTime(value == null ? 0 : ((UnixDateTime)value).getMilliseconds());
                return;

            case Guid:
                this.writer.WriteString(value == null ? "" : ((UUID)value).toString());
                return;

            case MongoDbObjectId:
                this.writer.WriteObjectId(value == null ? null : new ObjectId(((MongoDbObjectId)value).ToByteArray()));
                return;

            case Utf8:
                this.writer.WriteString(value == null ? "" : ((Utf8String)value).toString());
                return;

            case Binary:
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: this.writer.WriteBytes(value == null ? default(byte[]) : (byte[])value);
                this.writer.WriteBytes(value == null ? null : (byte[])value);
                return;

            case ObjectScope:
            case ImmutableObjectScope:
                this.DispatchObject(typeArg.clone(), value);
                return;

            case TypedArrayScope:
            case ImmutableTypedArrayScope:
                this.DispatchArray(typeArg.clone(), value);
                return;

            case TypedSetScope:
            case ImmutableTypedSetScope:
                this.DispatchSet(typeArg.clone(), value);
                return;

            case TypedMapScope:
            case ImmutableTypedMapScope:
                this.DispatchMap(typeArg.clone(), value);
                return;

            case TupleScope:
            case ImmutableTupleScope:
            case TypedTupleScope:
            case ImmutableTypedTupleScope:
            case TaggedScope:
            case ImmutableTaggedScope:
            case Tagged2Scope:
            case ImmutableTagged2Scope:
                this.DispatchTuple(typeArg.clone(), value);
                return;

            case NullableScope:
            case ImmutableNullableScope:
                this.DispatchNullable(typeArg.clone(), value);
                return;

            case Schema:
            case ImmutableSchema:
                this.DispatchUDT(typeArg.clone(), value);
                return;

            default:
                throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", typeArg.clone()));
                return;
        }
    }
}