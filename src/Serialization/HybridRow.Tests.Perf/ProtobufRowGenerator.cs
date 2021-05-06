// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using Google.Protobuf;
    using Google.Protobuf.Collections;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
#pragma warning disable DontUseNamespaceAliases // Namespace Aliases should be avoided
    using pb = Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf.CassandraHotel.Protobuf;

#pragma warning restore DontUseNamespaceAliases // Namespace Aliases should be avoided

    internal ref struct ProtobufRowGenerator
    {
        private readonly string schemaName;
        private readonly byte[] buffer;
        private ReadOnlySpan<byte> active;

        public ProtobufRowGenerator(string schemaName, int capacity)
        {
            this.schemaName = schemaName;
            this.buffer = new byte[capacity];
            this.active = this.buffer;
        }

        public void WriteBuffer(Dictionary<Utf8String, object> tableValue)
        {
            switch (this.schemaName)
            {
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
                    Contract.Fail($"Unknown schema will be ignored: {this.schemaName}");
                    break;
            }
        }

        public void ReadBuffer(byte[] buffer)
        {
            switch (this.schemaName)
            {
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
                    Contract.Fail($"Unknown schema will be ignored: {this.schemaName}");
                    break;
            }
        }

        public int Length => this.active.Length;

        public byte[] ToArray()
        {
            return this.active.ToArray();
        }

        private void WriteBufferGuest(Dictionary<Utf8String, object> tableValue)
        {
            pb.Guests room = new pb.Guests();
            using (CodedOutputStream stm = new CodedOutputStream(this.buffer))
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    switch (key.ToString())
                    {
                        case "guest_id":
                            room.GuestId = ((Guid?)value)?.ToString();
                            break;

                        case "first_name":
                            room.FirstName = ((Utf8String)value)?.ToString();
                            break;

                        case "last_name":
                            room.LastName = ((Utf8String)value)?.ToString();
                            break;

                        case "title":
                            room.Title = ((Utf8String)value)?.ToString();
                            break;

                        case "emails":
                            if (value != null)
                            {
                                ProtobufRowGenerator.PopulateStringList(room.Emails, (List<object>)value);
                            }

                            break;

                        case "phone_numbers":
                            if (value != null)
                            {
                                ProtobufRowGenerator.PopulateStringList(room.PhoneNumbers, (List<object>)value);
                            }

                            break;

                        case "addresses":
                            if (value != null)
                            {
                                ProtobufRowGenerator.PopulateStringAddressMap(room.Addresses, (List<object>)value);
                            }

                            break;

                        case "confirm_number":
                            room.ConfirmNumber = ((Utf8String)value)?.ToString();
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

        private void WriteBufferHotel(Dictionary<Utf8String, object> tableValue)
        {
            pb.Hotels room = new pb.Hotels();
            using (CodedOutputStream stm = new CodedOutputStream(this.buffer))
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    switch (key.ToString())
                    {
                        case "hotel_id":
                            room.HotelId = ((Utf8String)value)?.ToString();
                            break;

                        case "name":
                            room.Name = ((Utf8String)value)?.ToString();
                            break;

                        case "phone":
                            room.Phone = ((Utf8String)value)?.ToString();
                            break;

                        case "address":
                            room.Address = value == null ? null : ProtobufRowGenerator.MakeAddress((Dictionary<Utf8String, object>)value);
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

        private void WriteBufferRoom(Dictionary<Utf8String, object> tableValue)
        {
            pb.Available_Rooms_By_Hotel_Date room = new pb.Available_Rooms_By_Hotel_Date();
            using (CodedOutputStream stm = new CodedOutputStream(this.buffer))
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    switch (key.ToString())
                    {
                        case "hotel_id":
                            room.HotelId = ((Utf8String)value)?.ToString();
                            break;

                        case "date":
                            room.Date = ((DateTime?)value)?.Ticks;
                            break;

                        case "room_number":
                            room.RoomNumber = (byte?)value;
                            break;

                        case "is_available":
                            room.IsAvailable = (bool?)value;
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

        private static void ReadBufferGuest(byte[] buffer)
        {
            pb.Guests item = new pb.Guests();
            item.MergeFrom(buffer);
        }

        private static void ReadBufferHotel(byte[] buffer)
        {
            pb.Hotels item = new pb.Hotels();
            item.MergeFrom(buffer);
        }

        private static void ReadBufferRoom(byte[] buffer)
        {
            pb.Available_Rooms_By_Hotel_Date item = new pb.Available_Rooms_By_Hotel_Date();
            item.MergeFrom(buffer);
        }

        private static void PopulateStringList(RepeatedField<string> field, List<object> list)
        {
            foreach (object item in list)
            {
                field.Add(((Utf8String)item).ToString());
            }
        }

        private static void PopulateStringAddressMap(MapField<string, pb.Address> field, List<object> list)
        {
            foreach (object item in list)
            {
                List<object> tuple = (List<object>)item;
                string key = ((Utf8String)tuple[0]).ToString();
                pb.Address value = ProtobufRowGenerator.MakeAddress((Dictionary<Utf8String, object>)tuple[1]);
                field.Add(key, value);
            }
        }

        private static pb.PostalCode MakePostalCode(Dictionary<Utf8String, object> tableValue)
        {
            pb.PostalCode postalCode = new pb.PostalCode();
            foreach ((Utf8String key, object value) in tableValue)
            {
                switch (key.ToString())
                {
                    case "zip":
                        postalCode.Zip = (int?)value;
                        break;

                    case "plus4":
                        postalCode.Plus4 = (short?)value;
                        break;

                    default:
                        Assert.Fail("should never happen");
                        break;
                }
            }

            return postalCode;
        }

        private static pb.Address MakeAddress(Dictionary<Utf8String, object> tableValue)
        {
            pb.Address address = new pb.Address();
            foreach ((Utf8String key, object value) in tableValue)
            {
                switch (key.ToString())
                {
                    case "street":
                        address.Street = ((Utf8String)value)?.ToString();
                        break;

                    case "city":
                        address.City = ((Utf8String)value)?.ToString();
                        break;

                    case "state":
                        address.State = ((Utf8String)value)?.ToString();
                        break;

                    case "postal_code":
                        address.PostalCode = value == null ? null : ProtobufRowGenerator.MakePostalCode((Dictionary<Utf8String, object>)value);
                        break;

                    default:
                        Assert.Fail("should never happen");
                        break;
                }
            }

            return address;
        }
    }
}
