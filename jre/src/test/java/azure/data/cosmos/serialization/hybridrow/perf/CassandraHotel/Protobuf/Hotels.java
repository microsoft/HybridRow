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

public final class Hotels implements Google.Protobuf.IMessage<Hotels> {
    /**
     * Field number for the "address" field.
     */
    public static final int AddressFieldNumber = 4;
    /**
     * Field number for the "hotel_id" field.
     */
    public static final int HotelIdFieldNumber = 1;
    /**
     * Field number for the "name" field.
     */
    public static final int NameFieldNumber = 2;
    /**
     * Field number for the "phone" field.
     */
    public static final int PhoneFieldNumber = 3;
    private static final Google.Protobuf.MessageParser<Hotels> _parser =
        new Google.Protobuf.MessageParser<Hotels>(() -> new Hotels());
    private static final Google.Protobuf.FieldCodec<String> _single_hotelId_codec =
        Google.Protobuf.FieldCodec.<String>ForClassWrapper(10);

    // TODO: C# TO JAVA CONVERTER: Java does not support 'partial' methods:
    //	partial void OnConstruction();
    private static final Google.Protobuf.FieldCodec<String> _single_name_codec =
        Google.Protobuf.FieldCodec.<String>ForClassWrapper(18);
    private static final Google.Protobuf.FieldCodec<String> _single_phone_codec =
        Google.Protobuf.FieldCodec.<String>ForClassWrapper(26);
    private Google.Protobuf.UnknownFieldSet _unknownFields;
    private Address address_;
    private String hotelId_;
    private String name_;
    private String phone_;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Hotels()
    public Hotels() {
        OnConstruction();
    }
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Hotels(Hotels other)
    public Hotels(Hotels other) {
        this();
        setHotelId(other.getHotelId());
        setName(other.getName());
        setPhone(other.getPhone());
        setAddress(other.address_ != null ? other.getAddress().Clone() : null);
        _unknownFields = Google.Protobuf.UnknownFieldSet.Clone(other._unknownFields);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Microsoft.Azure.Cosmos.Serialization
    // .HybridRow.Tests.Perf.CassandraHotel.Protobuf.Address Address
    public Address getAddress() {
        return address_;
    }

    public void setAddress(Address value) {
        address_ = value;
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
        return Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.CassandraHotel.Protobuf.CassandraHotelSchemaReflection.getDescriptor().MessageTypes[2];
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
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public string Name
    public String getName() {
        return name_;
    }

    public void setName(String value) {
        name_ = value;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public static Google.Protobuf
    // .MessageParser<Hotels> Parser
    public static Google.Protobuf.MessageParser<Hotels> getParser() {
        return _parser;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public string Phone
    public String getPhone() {
        return phone_;
    }

    public void setPhone(String value) {
        phone_ = value;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public int CalculateSize()
    public int CalculateSize() {
        int size = 0;
        if (hotelId_ != null) {
            size += _single_hotelId_codec.CalculateSizeWithTag(getHotelId());
        }
        if (name_ != null) {
            size += _single_name_codec.CalculateSizeWithTag(getName());
        }
        if (phone_ != null) {
            size += _single_phone_codec.CalculateSizeWithTag(getPhone());
        }
        if (address_ != null) {
            size += 1 + Google.Protobuf.CodedOutputStream.ComputeMessageSize(getAddress());
        }
        if (_unknownFields != null) {
            size += _unknownFields.CalculateSize();
        }
        return size;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public Hotels Clone()
    public Hotels Clone() {
        return new Hotels(this);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public void MergeFrom(Hotels other)
    public void MergeFrom(Hotels other) {
        if (other == null) {
            return;
        }
        if (other.hotelId_ != null) {
            if (hotelId_ == null || !other.getHotelId().equals("")) {
                setHotelId(other.getHotelId());
            }
        }
        if (other.name_ != null) {
            if (name_ == null || !other.getName().equals("")) {
                setName(other.getName());
            }
        }
        if (other.phone_ != null) {
            if (phone_ == null || !other.getPhone().equals("")) {
                setPhone(other.getPhone());
            }
        }
        if (other.address_ != null) {
            if (address_ == null) {
                address_ = new Address();
            }
            getAddress().MergeFrom(other.getAddress());
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
                    String value = _single_name_codec.Read(input);
                    if (name_ == null || !value.equals("")) {
                        setName(value);
                    }
                    break;
                }
                case 26: {
                    String value = _single_phone_codec.Read(input);
                    if (phone_ == null || !value.equals("")) {
                        setPhone(value);
                    }
                    break;
                }
                case 34: {
                    if (address_ == null) {
                        address_ = new Address();
                    }
                    input.ReadMessage(address_);
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
        if (name_ != null) {
            _single_name_codec.WriteTagAndValue(output, getName());
        }
        if (phone_ != null) {
            _single_phone_codec.WriteTagAndValue(output, getPhone());
        }
        if (address_ != null) {
            output.WriteRawTag(34);
            output.WriteMessage(getAddress());
        }
        if (_unknownFields != null) {
            _unknownFields.WriteTo(output);
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public override bool Equals(object other)
    @Override
    public boolean equals(Object other) {
        return Equals(other instanceof Hotels ? (Hotels)other : null);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [System.Diagnostics.DebuggerNonUserCodeAttribute] public bool Equals(Hotels other)
    public boolean equals(Hotels other) {
        if (ReferenceEquals(other, null)) {
            return false;
        }
        if (ReferenceEquals(other, this)) {
            return true;
        }
        if (!getHotelId().equals(other.getHotelId())) {
            return false;
        }
        if (!getName().equals(other.getName())) {
            return false;
        }
        if (!getPhone().equals(other.getPhone())) {
            return false;
        }
        if (!getAddress().equals(other.getAddress())) {
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
        if (name_ != null) {
            hash ^= getName().hashCode();
        }
        if (phone_ != null) {
            hash ^= getPhone().hashCode();
        }
        if (address_ != null) {
            hash ^= getAddress().hashCode();
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