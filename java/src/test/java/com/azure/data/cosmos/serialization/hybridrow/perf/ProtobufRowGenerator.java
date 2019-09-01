// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.perf;

import Google.Protobuf.*;
import Google.Protobuf.Collections.*;
import com.azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Address;
import com.azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Available_Rooms_By_Hotel_Date;
import com.azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Guests;
import com.azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Hotels;
import com.azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.PostalCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.google.common.base.Strings.lenientFormat;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable DontUseNamespaceAliases // Namespace Aliases should be avoided

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning restore DontUseNamespaceAliases // Namespace Aliases should be avoided

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal ref struct ProtobufRowGenerator
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class ProtobufRowGenerator {
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private ReadOnlySpan<byte> active;
    private ReadOnlySpan<Byte> active;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private readonly byte[] buffer;
    private byte[] buffer;
    private String schemaName;

    public ProtobufRowGenerator() {
    }

    public ProtobufRowGenerator(String schemaName, int capacity) {
        this.schemaName = schemaName;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.buffer = new byte[capacity];
        this.buffer = new byte[capacity];
        this.active = this.buffer;
    }

    public int getLength() {
        return this.active.Length;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public void ReadBuffer(byte[] buffer)
    public void ReadBuffer(byte[] buffer) {
        switch (this.schemaName) {
            case "Hotels":
                ProtobufRowGenerator.ReadBufferHotel(buffer);
                break;

            case "Guests":
                ProtobufRowGenerator.ReadBufferGuest(buffer);
                break;

            case "Available_Rooms_By_Hotel_Date":
                ProtobufRowGenerator.ReadBufferRoom(buffer);
                break;

            default:
                throw new IllegalStateException(lenientFormat("Unknown schema will be ignored: %s", this.schemaName));
                break;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.active.ToArray();
    }

    public void WriteBuffer(HashMap<Utf8String, Object> tableValue) {
        switch (this.schemaName) {
            case "Hotels":
                this.WriteBufferHotel(tableValue);
                break;

            case "Guests":
                this.WriteBufferGuest(tableValue);
                break;

            case "Available_Rooms_By_Hotel_Date":
                this.WriteBufferRoom(tableValue);
                break;

            default:
                throw new IllegalStateException(lenientFormat("Unknown schema will be ignored: %s", this.schemaName));
                break;
        }
    }

    public ProtobufRowGenerator clone() {
        ProtobufRowGenerator varCopy = new ProtobufRowGenerator();

        varCopy.schemaName = this.schemaName;
        varCopy.buffer = this.buffer;
        varCopy.active = this.active;

        return varCopy;
    }

    private static Address MakeAddress(HashMap<Utf8String, Object> tableValue) {
        Address address = new Address();
        for ((Utf8String key,Object value) :tableValue)
        {
            switch (key.toString()) {
                case "street":
                    address.setStreet(value == null ? null : ((Utf8String)value).toString());
                    break;

                case "city":
                    address.setCity(value == null ? null : ((Utf8String)value).toString());
                    break;

                case "state":
                    address.setState(value == null ? null : ((Utf8String)value).toString());
                    break;

                case "postal_code":
                    address.setPostalCode(value == null ? null : ProtobufRowGenerator.MakePostalCode((HashMap<Utf8String, Object>)value));
                    break;

                default:
                    throw new IllegalStateException();
                    break;
            }
        }

        return address;
    }

    private static PostalCode MakePostalCode(HashMap<Utf8String, Object> tableValue) {
        PostalCode postalCode = new PostalCode();
        for ((Utf8String key,Object value) :tableValue)
        {
            switch (key.toString()) {
                case "zip":
                    postalCode.setZip((Integer)value);
                    break;

                case "plus4":
                    postalCode.setPlus4((Short)value);
                    break;

                default:
                    throw new IllegalStateException();
                    break;
            }
        }

        return postalCode;
    }

    private static void PopulateStringAddressMap(MapField<String,
        Address> field,
                                                 ArrayList<Object> list) {
        for (Object item : list) {
            ArrayList<Object> tuple = (ArrayList<Object>)item;
            String key = ((Utf8String)tuple.get(0)).toString();
            Address value =
                ProtobufRowGenerator.MakeAddress((HashMap<Utf8String, Object>)tuple.get(1));
            field.Add(key, value);
        }
    }

    private static void PopulateStringList(RepeatedField<String> field, ArrayList<Object> list) {
        for (Object item : list) {
            field.Add(((Utf8String)item).toString());
        }
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static void ReadBufferGuest(byte[] buffer)
    private static void ReadBufferGuest(byte[] buffer) {
        Guests item =
            new Guests();
        item.MergeFrom(buffer);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static void ReadBufferHotel(byte[] buffer)
    private static void ReadBufferHotel(byte[] buffer) {
        Hotels item =
            new Hotels();
        item.MergeFrom(buffer);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static void ReadBufferRoom(byte[] buffer)
    private static void ReadBufferRoom(byte[] buffer) {
        Available_Rooms_By_Hotel_Date item = new azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Available_Rooms_By_Hotel_Date();
        item.MergeFrom(buffer);
    }

    private void WriteBufferGuest(HashMap<Utf8String, Object> tableValue) {
        azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Guests room =
            new azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Guests();
        try (CodedOutputStream stm = new CodedOutputStream(this.buffer)) {
            for ((Utf8String key,Object value) :tableValue)
            {
                switch (key.toString()) {
                    case "guest_id":
                        room.setGuestId(value == null ? null : ((UUID)value).toString());
                        break;

                    case "first_name":
                        room.setFirstName(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "last_name":
                        room.setLastName(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "title":
                        room.setTitle(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "emails":
                        if (value != null) {
                            ProtobufRowGenerator.PopulateStringList(room.getEmails(), (ArrayList<Object>)value);
                        }

                        break;

                    case "phone_numbers":
                        if (value != null) {
                            ProtobufRowGenerator.PopulateStringList(room.getPhoneNumbers(), (ArrayList<Object>)value);
                        }

                        break;

                    case "addresses":
                        if (value != null) {
                            ProtobufRowGenerator.PopulateStringAddressMap(room.getAddresses(),
                                (ArrayList<Object>)value);
                        }

                        break;

                    case "confirm_number":
                        room.setConfirmNumber(value == null ? null : ((Utf8String)value).toString());
                        break;

                    default:
                        Assert.Fail("should never happen");
                        break;
                }
            }

            room.WriteTo(stm);
            stm.Flush();
            this.active = this.buffer.AsSpan(0, (int)stm.Position);
        }
    }

    private void WriteBufferHotel(HashMap<Utf8String, Object> tableValue) {
        azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Hotels room =
            new azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Hotels();
        try (CodedOutputStream stm = new CodedOutputStream(this.buffer)) {
            for ((Utf8String key,Object value) :tableValue)
            {
                switch (key.toString()) {
                    case "hotel_id":
                        room.setHotelId(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "name":
                        room.setName(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "phone":
                        room.setPhone(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "address":
                        room.setAddress(value == null ? null : ProtobufRowGenerator.MakeAddress((HashMap<Utf8String,
                            Object>)value));
                        break;

                    default:
                        Assert.Fail("should never happen");
                        break;
                }
            }

            room.WriteTo(stm);
            stm.Flush();
            this.active = this.buffer.AsSpan(0, (int)stm.Position);
        }
    }

    private void WriteBufferRoom(HashMap<Utf8String, Object> tableValue) {
        azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Available_Rooms_By_Hotel_Date room = new azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf.Available_Rooms_By_Hotel_Date();
        try (CodedOutputStream stm = new CodedOutputStream(this.buffer)) {
            for ((Utf8String key,Object value) :tableValue)
            {
                switch (key.toString()) {
                    case "hotel_id":
                        room.setHotelId(value == null ? null : ((Utf8String)value).toString());
                        break;

                    case "date":
                        room.setDate(value == null ? null : ((LocalDateTime)value).getTime());
                        break;

                    case "room_number":
                        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                        //ORIGINAL LINE: room.RoomNumber = (Nullable<byte>)value;
                        room.setRoomNumber((Byte)value);
                        break;

                    case "is_available":
                        room.setIsAvailable((Boolean)value);
                        break;

                    default:
                        Assert.Fail("should never happen");
                        break;
                }
            }

            room.WriteTo(stm);
            stm.Flush();
            this.active = this.buffer.AsSpan(0, (int)stm.Position);
        }
    }
}