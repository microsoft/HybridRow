//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf.CassandraHotel.Protobuf;

import azure.data.cosmos.serialization.hybridrow.Tests.Perf.*;

// <auto-generated>
//     Generated by the protocol buffer compiler.  DO NOT EDIT!
//     source: TestData/CassandraHotelSchema.proto
// </auto-generated>

//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using pb = Google.Protobuf;
//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using pbc = Google.Protobuf.Collections;
//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using pbr = Google.Protobuf.Reflection;

public final class Available_Rooms_By_Hotel_Date implements Google.Protobuf.IMessage<Available_Rooms_By_Hotel_Date> {
    /**
     * Field number for the "date" field.
     */
    public static final int DateFieldNumber = 2;
    /**
     * Field number for the "hotel_id" field.
     */
    public static final int HotelIdFieldNumber = 1;
    /**
     * Field number for the "is_available" field.
     */
    public static final int IsAvailableFieldNumber = 4;
    /**
     * Field number for the "room_number" field.
     */
    public static final int RoomNumberFieldNumber = 3;
    private static final Google.Protobuf.MessageParser<Available_Rooms_By_Hotel_Date> _parser =
		new Google.Protobuf.MessageParser<Available_Rooms_By_Hotel_Date>(() -> new Available_Rooms_By_Hotel_Date());
    private static final Google.Protobuf.FieldCodec<Long> _single_date_codec =
		Google.Protobuf.FieldCodec.<Long>ForStructWrapper(18);

    // TODO: C# TO JAVA CONVERTER: Java does not support 'partial' methods:
    //	partial void OnConstruction();
    private static final Google.Protobuf.FieldCodec<String> _single_hotelId_codec =
		Google.Protobuf.FieldCodec.<String>ForClassWrapper(10);
    private static final Google.Protobuf.FieldCodec<Boolean> _single_isAvailable_codec =
		Google.Protobuf.FieldCodec.<Boolean>ForStructWrapper(34);
    private static final Google.Protobuf.FieldCodec<Integer> _single_roomNumber_codec =
		Google.Protobuf.FieldCodec.<Integer>ForStructWrapper(26);
    private Google.Protobuf.UnknownFieldSet _unknownFields;
    private Long date_;
    private String hotelId_;
    private Boolean isAvailable_;
    private Integer roomNumber_;
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Available_Rooms_By_Hotel_Date()
    public Available_Rooms_By_Hotel_Date() {
        OnConstruction();
    }
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Available_Rooms_By_Hotel_Date
	// (Available_Rooms_By_Hotel_Date other)
    public Available_Rooms_By_Hotel_Date(Available_Rooms_By_Hotel_Date other) {
        this();
        setHotelId(other.getHotelId());
        setDate(other.getDate());
        setRoomNumber(other.getRoomNumber());
        setIsAvailable(other.getIsAvailable());
        _unknownFields = Google.Protobuf.UnknownFieldSet.Clone(other._unknownFields);
    }

    /**
     * datetime
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Nullable<long> Date
    public Long getDate() {
        return date_;
    }

    public void setDate(Long value) {
        date_ = value;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] pbr::MessageDescriptor pb::IMessage.Descriptor
    public Google.Protobuf.Reflection getDescriptor() {
        return getDescriptor();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public static Google.Protobuf.Reflection
	// .MessageDescriptor Descriptor
    public static Google.Protobuf.Reflection.MessageDescriptor getDescriptor() {
        return Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.CassandraHotel.Protobuf.CassandraHotelSchemaReflection.getDescriptor().MessageTypes[3];
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public string HotelId
    public String getHotelId() {
        return hotelId_;
    }

    public void setHotelId(String value) {
        hotelId_ = value;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Nullable<bool> IsAvailable
    public Boolean getIsAvailable() {
        return isAvailable_;
    }

    public void setIsAvailable(Boolean value) {
        isAvailable_ = value;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public static Google.Protobuf
	// .MessageParser<Available_Rooms_By_Hotel_Date> Parser
    public static Google.Protobuf.MessageParser<Available_Rooms_By_Hotel_Date> getParser() {
        return _parser;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Nullable<int> RoomNumber
    public Integer getRoomNumber() {
        return roomNumber_;
    }

    public void setRoomNumber(Integer value) {
        roomNumber_ = value;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public int CalculateSize()
    public int CalculateSize() {
        int size = 0;
        if (hotelId_ != null) {
            size += _single_hotelId_codec.CalculateSizeWithTag(getHotelId());
        }
        if (date_ != null) {
            size += _single_date_codec.CalculateSizeWithTag(getDate());
        }
        if (roomNumber_ != null) {
            size += _single_roomNumber_codec.CalculateSizeWithTag(getRoomNumber());
        }
        if (isAvailable_ != null) {
            size += _single_isAvailable_codec.CalculateSizeWithTag(getIsAvailable());
        }
        if (_unknownFields != null) {
            size += _unknownFields.CalculateSize();
        }
        return size;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Available_Rooms_By_Hotel_Date Clone()
    public Available_Rooms_By_Hotel_Date Clone() {
        return new Available_Rooms_By_Hotel_Date(this);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public void MergeFrom
	// (Available_Rooms_By_Hotel_Date other)
    public void MergeFrom(Available_Rooms_By_Hotel_Date other) {
        if (other == null) {
            return;
        }
        if (other.hotelId_ != null) {
            if (hotelId_ == null || !other.getHotelId().equals("")) {
                setHotelId(other.getHotelId());
            }
        }
        if (other.date_ != null) {
            if (date_ == null || other.getDate() != 0L) {
                setDate(other.getDate());
            }
        }
        if (other.roomNumber_ != null) {
            if (roomNumber_ == null || other.getRoomNumber() != 0) {
                setRoomNumber(other.getRoomNumber());
            }
        }
        if (other.isAvailable_ != null) {
            if (isAvailable_ == null || other.getIsAvailable() != false) {
                setIsAvailable(other.getIsAvailable());
            }
        }
        _unknownFields = Google.Protobuf.UnknownFieldSet.MergeFrom(_unknownFields, other._unknownFields);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public void MergeFrom(Google.Protobuf
	// .CodedInputStream input)
    public void MergeFrom(Google.Protobuf.CodedInputStream input) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: uint tag;
        int tag;
        while ((tag = input.ReadTag()) != 0) {
            switch (tag) {
                default:
                    _unknownFields = Google.Protobuf.UnknownFieldSet.MergeFieldFrom(_unknownFields, input);
                    break;
                case 10: {
                    String value = _single_hotelId_codec.Read(input);
                    if (hotelId_ == null || !value.equals("")) {
                        setHotelId(value);
                    }
                    break;
                }
                case 18: {
                    Long value = _single_date_codec.Read(input);
                    if (date_ == null || value != 0L) {
                        setDate(value);
                    }
                    break;
                }
                case 26: {
                    Integer value = _single_roomNumber_codec.Read(input);
                    if (roomNumber_ == null || value != 0) {
                        setRoomNumber(value);
                    }
                    break;
                }
                case 34: {
                    Boolean value = _single_isAvailable_codec.Read(input);
                    if (isAvailable_ == null || value != false) {
                        setIsAvailable(value);
                    }
                    break;
                }
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public void WriteTo(Google.Protobuf
	// .CodedOutputStream output)
    public void WriteTo(Google.Protobuf.CodedOutputStream output) {
        if (hotelId_ != null) {
            _single_hotelId_codec.WriteTagAndValue(output, getHotelId());
        }
        if (date_ != null) {
            _single_date_codec.WriteTagAndValue(output, getDate());
        }
        if (roomNumber_ != null) {
            _single_roomNumber_codec.WriteTagAndValue(output, getRoomNumber());
        }
        if (isAvailable_ != null) {
            _single_isAvailable_codec.WriteTagAndValue(output, getIsAvailable());
        }
        if (_unknownFields != null) {
            _unknownFields.WriteTo(output);
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public override bool Equals(object other)
    @Override
    public boolean equals(Object other) {
        return Equals(other instanceof Available_Rooms_By_Hotel_Date ? (Available_Rooms_By_Hotel_Date)other : null);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public bool Equals
	// (Available_Rooms_By_Hotel_Date other)
    public boolean equals(Available_Rooms_By_Hotel_Date other) {
        if (ReferenceEquals(other, null)) {
            return false;
        }
        if (ReferenceEquals(other, this)) {
            return true;
        }
        if (!getHotelId().equals(other.getHotelId())) {
            return false;
        }
        if (getDate() != other.getDate()) {
            return false;
        }
        if (getRoomNumber() != other.getRoomNumber()) {
            return false;
        }
        if (getIsAvailable() != other.getIsAvailable()) {
            return false;
        }
        return Equals(_unknownFields, other._unknownFields);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public override int GetHashCode()
    @Override
    public int hashCode() {
        int hash = 1;
        if (hotelId_ != null) {
            hash ^= getHotelId().hashCode();
        }
        if (date_ != null) {
            hash ^= getDate().hashCode();
        }
        if (roomNumber_ != null) {
            hash ^= getRoomNumber().hashCode();
        }
        if (isAvailable_ != null) {
            hash ^= getIsAvailable().hashCode();
        }
        if (_unknownFields != null) {
            hash ^= _unknownFields.hashCode();
        }
        return hash;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public override string ToString()
    @Override
    public String toString() {
        return Google.Protobuf.JsonFormatter.ToDiagnosticString(this);
    }

}