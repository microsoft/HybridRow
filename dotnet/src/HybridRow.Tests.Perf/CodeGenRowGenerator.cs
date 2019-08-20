// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    internal ref struct CodeGenRowGenerator
    {
        private RowBuffer row;
        private readonly HybridRowSerializer dispatcher;

        public CodeGenRowGenerator(int capacity, Layout layout, LayoutResolver resolver, ISpanResizer<byte> resizer = default)
        {
            this.row = new RowBuffer(capacity, resizer);
            this.row.InitLayout(HybridRowVersion.V1, layout, resolver);

            switch (layout.Name)
            {
                case "Hotels":
                    this.dispatcher = new HotelsHybridRowSerializer(layout, resolver);
                    break;

                case "Guests":
                    this.dispatcher = new GuestsHybridRowSerializer(layout, resolver);
                    break;

                case "Available_Rooms_By_Hotel_Date":
                    this.dispatcher = new RoomsHybridRowSerializer(layout, resolver);
                    break;

                default:
                    Contract.Fail($"Unknown schema will be ignored: {layout.Name}");
                    this.dispatcher = null;
                    break;
            }
        }

        public int Length => this.row.Length;

        public byte[] ToArray()
        {
            return this.row.ToArray();
        }

        public void Reset()
        {
            Layout layout = this.row.Resolver.Resolve(this.row.Header.SchemaId);
            this.row.InitLayout(HybridRowVersion.V1, layout, this.row.Resolver);
        }

        public Result WriteBuffer(Dictionary<Utf8String, object> tableValue)
        {
            RowCursor root = RowCursor.Create(ref this.row);
            return this.dispatcher.WriteBuffer(ref this.row, ref root, tableValue);
        }

        public Result ReadBuffer(byte[] buffer)
        {
            this.row = new RowBuffer(buffer.AsSpan(), HybridRowVersion.V1, this.row.Resolver);
            RowCursor root = RowCursor.Create(ref this.row);
            return this.dispatcher.ReadBuffer(ref this.row, ref root);
        }

        private abstract class HybridRowSerializer
        {
            public abstract Result WriteBuffer(ref RowBuffer row, ref RowCursor root, Dictionary<Utf8String, object> tableValue);

            public abstract Result ReadBuffer(ref RowBuffer row, ref RowCursor root);
        }

        private sealed class GuestsHybridRowSerializer : HybridRowSerializer
        {
            private static readonly Utf8String GuestIdName = Utf8String.TranscodeUtf16("guest_id");
            private static readonly Utf8String FirstNameName = Utf8String.TranscodeUtf16("first_name");
            private static readonly Utf8String LastNameName = Utf8String.TranscodeUtf16("last_name");
            private static readonly Utf8String TitleName = Utf8String.TranscodeUtf16("title");
            private static readonly Utf8String EmailsName = Utf8String.TranscodeUtf16("emails");
            private static readonly Utf8String PhoneNumbersName = Utf8String.TranscodeUtf16("phone_numbers");
            private static readonly Utf8String AddressesName = Utf8String.TranscodeUtf16("addresses");
            private static readonly Utf8String ConfirmNumberName = Utf8String.TranscodeUtf16("confirm_number");
            private readonly LayoutColumn guestId;
            private readonly LayoutColumn firstName;
            private readonly LayoutColumn lastName;
            private readonly LayoutColumn title;
            private readonly LayoutColumn emails;
            private readonly LayoutColumn phoneNumbers;
            private readonly LayoutColumn addresses;
            private readonly LayoutColumn confirmNumber;
            private readonly StringToken emailsToken;
            private readonly StringToken phoneNumbersToken;
            private readonly StringToken addressesToken;
            private readonly TypeArgumentList addressesFieldType;
            private readonly AddressHybridRowSerializer addressSerializer;
            private readonly LayoutScope.WriterFunc<Dictionary<Utf8String, object>> addressSerializerWriter;

            public GuestsHybridRowSerializer(Layout layout, LayoutResolver resolver)
            {
                layout.TryFind(GuestsHybridRowSerializer.GuestIdName, out this.guestId);
                layout.TryFind(GuestsHybridRowSerializer.FirstNameName, out this.firstName);
                layout.TryFind(GuestsHybridRowSerializer.LastNameName, out this.lastName);
                layout.TryFind(GuestsHybridRowSerializer.TitleName, out this.title);
                layout.TryFind(GuestsHybridRowSerializer.EmailsName, out this.emails);
                layout.TryFind(GuestsHybridRowSerializer.PhoneNumbersName, out this.phoneNumbers);
                layout.TryFind(GuestsHybridRowSerializer.AddressesName, out this.addresses);
                layout.TryFind(GuestsHybridRowSerializer.ConfirmNumberName, out this.confirmNumber);
                layout.Tokenizer.TryFindToken(this.emails.Path, out this.emailsToken);
                layout.Tokenizer.TryFindToken(this.phoneNumbers.Path, out this.phoneNumbersToken);
                layout.Tokenizer.TryFindToken(this.addresses.Path, out this.addressesToken);

                this.addressesFieldType = new TypeArgumentList(
                    new[]
                    {
                        new TypeArgument(LayoutType.TypedTuple, this.addresses.TypeArgs)
                    });

                this.addressSerializer = new AddressHybridRowSerializer(resolver.Resolve(this.addresses.TypeArgs[1].TypeArgs.SchemaId), resolver);
                this.addressSerializerWriter = this.addressSerializer.WriteBuffer;
            }

            public override Result WriteBuffer(ref RowBuffer row, ref RowCursor root, Dictionary<Utf8String, object> tableValue)
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    Result r;
                    switch (0)
                    {
                        case 0 when key.Equals(GuestsHybridRowSerializer.GuestIdName):
                            if (value != null)
                            {
                                r = LayoutType.Guid.WriteFixed(ref row, ref root, this.guestId, (Guid)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.FirstNameName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.firstName, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.LastNameName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.lastName, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.TitleName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.title, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.EmailsName):
                            if (value != null)
                            {
                                root.Find(ref row, this.emailsToken);
                                r = LayoutType.TypedArray.WriteScope(
                                    ref row,
                                    ref root,
                                    this.emails.TypeArgs,
                                    (List<object>)value,
                                    (ref RowBuffer row2, ref RowCursor childScope, List<object> context) =>
                                    {
                                        foreach (object item in context)
                                        {
                                            Result r2 = LayoutType.Utf8.WriteSparse(ref row2, ref childScope, (Utf8String)item);
                                            if (r2 != Result.Success)
                                            {
                                                return r2;
                                            }

                                            childScope.MoveNext(ref row2);
                                        }

                                        return Result.Success;
                                    });
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.PhoneNumbersName):
                            if (value != null)
                            {
                                root.Find(ref row, this.phoneNumbersToken);
                                r = LayoutType.TypedArray.WriteScope(ref row, ref root, this.phoneNumbers.TypeArgs, out RowCursor childScope);
                                if (r != Result.Success)
                                {
                                    return r;
                                }

                                foreach (object item in (List<object>)value)
                                {
                                    r = LayoutType.Utf8.WriteSparse(ref row, ref childScope, (Utf8String)item);
                                    if (r != Result.Success)
                                    {
                                        return r;
                                    }

                                    childScope.MoveNext(ref row);
                                }

                                root.Skip(ref row, ref childScope);
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.AddressesName):
                            if (value != null)
                            {
                                root.Find(ref row, this.addressesToken);
                                r = LayoutType.TypedMap.WriteScope(
                                    ref row,
                                    ref root,
                                    this.addresses.TypeArgs,
                                    (this, (List<object>)value),
                                    (ref RowBuffer row2, ref RowCursor childScope, (GuestsHybridRowSerializer _this, List<object> value) ctx) =>
                                    {
                                        foreach (object item in ctx.value)
                                        {
                                            Result r2 = LayoutType.TypedTuple.WriteScope(
                                                ref row2,
                                                ref childScope,
                                                ctx._this.addresses.TypeArgs,
                                                (ctx._this, (List<object>)item),
                                                (ref RowBuffer row3, ref RowCursor tupleScope, (GuestsHybridRowSerializer _this, List<object> value) ctx2) =>
                                                {
                                                    Result r3 = LayoutType.Utf8.WriteSparse(ref row3, ref tupleScope, (Utf8String)ctx2.value[0]);
                                                    if (r3 != Result.Success)
                                                    {
                                                        return r3;
                                                    }

                                                    tupleScope.MoveNext(ref row3);
                                                    return LayoutType.ImmutableUDT.WriteScope(
                                                        ref row3,
                                                        ref tupleScope,
                                                        ctx2._this.addresses.TypeArgs[1].TypeArgs,
                                                        (Dictionary<Utf8String, object>)ctx2.value[1],
                                                        ctx2._this.addressSerializerWriter);
                                                });
                                            if (r2 != Result.Success)
                                            {
                                                return r2;
                                            }

                                            childScope.MoveNext(ref row2);
                                        }

                                        return Result.Success;
                                    });
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(GuestsHybridRowSerializer.ConfirmNumberName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.confirmNumber, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        default:
                            Contract.Fail($"Unknown property name: {key}");
                            break;
                    }
                }

                return Result.Success;
            }

            public override Result ReadBuffer(ref RowBuffer row, ref RowCursor root)
            {
                Result r = LayoutType.Guid.ReadFixed(ref row, ref root, this.guestId, out Guid _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.firstName, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.lastName, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.title, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                root.Find(ref row, this.emailsToken);
                r = LayoutType.TypedArray.ReadScope(ref row, ref root, out RowCursor childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                while (childScope.MoveNext(ref row))
                {
                    r = LayoutType.Utf8.ReadSparse(ref row, ref childScope, out Utf8Span _);
                    if (r != Result.Success)
                    {
                        return r;
                    }
                }

                root.Skip(ref row, ref childScope);
                root.Find(ref row, this.phoneNumbersToken);
                r = LayoutType.TypedArray.ReadScope(ref row, ref root, out childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                while (childScope.MoveNext(ref row))
                {
                    r = LayoutType.Utf8.ReadSparse(ref row, ref childScope, out Utf8Span _);
                    if (r != Result.Success)
                    {
                        return r;
                    }
                }

                root.Skip(ref row, ref childScope);
                root.Find(ref row, this.addressesToken);
                r = LayoutType.TypedMap.ReadScope(ref row, ref root, out childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                while (childScope.MoveNext(ref row))
                {
                    r = LayoutType.TypedTuple.ReadScope(ref row, ref childScope, out RowCursor tupleScope);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    if (!tupleScope.MoveNext(ref row))
                    {
                        return Result.InvalidRow;
                    }

                    r = LayoutType.Utf8.ReadSparse(ref row, ref tupleScope, out Utf8Span _);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    if (!tupleScope.MoveNext(ref row))
                    {
                        return Result.InvalidRow;
                    }

                    r = LayoutType.ImmutableUDT.ReadScope(ref row, ref tupleScope, out RowCursor valueScope);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    r = this.addressSerializer.ReadBuffer(ref row, ref valueScope);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    tupleScope.Skip(ref row, ref valueScope);
                    childScope.Skip(ref row, ref tupleScope);
                }

                root.Skip(ref row, ref childScope);

                return LayoutType.Utf8.ReadVariable(ref row, ref root, this.confirmNumber, out Utf8Span _);
            }
        }

        private sealed class HotelsHybridRowSerializer : HybridRowSerializer
        {
            private static readonly Utf8String HotelIdName = Utf8String.TranscodeUtf16("hotel_id");
            private static readonly Utf8String NameName = Utf8String.TranscodeUtf16("name");
            private static readonly Utf8String PhoneName = Utf8String.TranscodeUtf16("phone");
            private static readonly Utf8String AddressName = Utf8String.TranscodeUtf16("address");
            private readonly LayoutColumn hotelId;
            private readonly LayoutColumn name;
            private readonly LayoutColumn phone;
            private readonly LayoutColumn address;
            private readonly StringToken addressToken;
            private readonly AddressHybridRowSerializer addressSerializer;

            public HotelsHybridRowSerializer(Layout layout, LayoutResolver resolver)
            {
                layout.TryFind(HotelsHybridRowSerializer.HotelIdName, out this.hotelId);
                layout.TryFind(HotelsHybridRowSerializer.NameName, out this.name);
                layout.TryFind(HotelsHybridRowSerializer.PhoneName, out this.phone);
                layout.TryFind(HotelsHybridRowSerializer.AddressName, out this.address);
                layout.Tokenizer.TryFindToken(this.address.Path, out this.addressToken);
                this.addressSerializer = new AddressHybridRowSerializer(resolver.Resolve(this.address.TypeArgs.SchemaId), resolver);
            }

            public override Result WriteBuffer(ref RowBuffer row, ref RowCursor root, Dictionary<Utf8String, object> tableValue)
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    Result r;
                    switch (0)
                    {
                        case 0 when key.Equals(HotelsHybridRowSerializer.HotelIdName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteFixed(ref row, ref root, this.hotelId, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(HotelsHybridRowSerializer.NameName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.name, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(HotelsHybridRowSerializer.PhoneName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.phone, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(HotelsHybridRowSerializer.AddressName):
                            if (value != null)
                            {
                                root.Find(ref row, this.addressToken);
                                r = LayoutType.UDT.WriteScope(ref row, ref root, this.address.TypeArgs, out RowCursor childScope);
                                if (r != Result.Success)
                                {
                                    return r;
                                }

                                r = this.addressSerializer.WriteBuffer(ref row, ref childScope, (Dictionary<Utf8String, object>)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }

                                root.Skip(ref row, ref childScope);
                            }

                            break;

                        default:
                            Contract.Fail($"Unknown property name: {key}");
                            break;
                    }
                }

                return Result.Success;
            }

            public override Result ReadBuffer(ref RowBuffer row, ref RowCursor root)
            {
                Result r = LayoutType.Utf8.ReadFixed(ref row, ref root, this.hotelId, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.name, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.phone, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                root.Find(ref row, this.addressToken);
                r = LayoutType.UDT.ReadScope(ref row, ref root, out RowCursor childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                r = this.addressSerializer.ReadBuffer(ref row, ref childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                root.Skip(ref row, ref childScope);
                return Result.Success;
            }
        }

        private sealed class RoomsHybridRowSerializer : HybridRowSerializer
        {
            private static readonly Utf8String HotelIdName = Utf8String.TranscodeUtf16("hotel_id");
            private static readonly Utf8String DateName = Utf8String.TranscodeUtf16("date");
            private static readonly Utf8String RoomNumberName = Utf8String.TranscodeUtf16("room_number");
            private static readonly Utf8String IsAvailableName = Utf8String.TranscodeUtf16("is_available");
            private readonly LayoutColumn hotelId;
            private readonly LayoutColumn date;
            private readonly LayoutColumn roomNumber;
            private readonly LayoutColumn isAvailable;

            // ReSharper disable once UnusedParameter.Local
            public RoomsHybridRowSerializer(Layout layout, LayoutResolver resolver)
            {
                layout.TryFind(RoomsHybridRowSerializer.HotelIdName, out this.hotelId);
                layout.TryFind(RoomsHybridRowSerializer.DateName, out this.date);
                layout.TryFind(RoomsHybridRowSerializer.RoomNumberName, out this.roomNumber);
                layout.TryFind(RoomsHybridRowSerializer.IsAvailableName, out this.isAvailable);
            }

            public override Result WriteBuffer(ref RowBuffer row, ref RowCursor root, Dictionary<Utf8String, object> tableValue)
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    Result r;
                    switch (0)
                    {
                        case 0 when key.Equals(RoomsHybridRowSerializer.HotelIdName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteFixed(ref row, ref root, this.hotelId, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(RoomsHybridRowSerializer.DateName):
                            if (value != null)
                            {
                                r = LayoutType.DateTime.WriteFixed(ref row, ref root, this.date, (DateTime)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(RoomsHybridRowSerializer.RoomNumberName):
                            if (value != null)
                            {
                                r = LayoutType.UInt8.WriteFixed(ref row, ref root, this.roomNumber, (byte)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(RoomsHybridRowSerializer.IsAvailableName):
                            if (value != null)
                            {
                                r = LayoutType.Boolean.WriteFixed(ref row, ref root, this.isAvailable, (bool)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        default:
                            Contract.Fail($"Unknown property name: {key}");
                            break;
                    }
                }

                return Result.Success;
            }

            public override Result ReadBuffer(ref RowBuffer row, ref RowCursor root)
            {
                Result r = LayoutType.Utf8.ReadFixed(ref row, ref root, this.hotelId, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.DateTime.ReadFixed(ref row, ref root, this.date, out DateTime _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.UInt8.ReadFixed(ref row, ref root, this.roomNumber, out byte _);
                if (r != Result.Success)
                {
                    return r;
                }

                return LayoutType.Boolean.ReadFixed(ref row, ref root, this.isAvailable, out bool _);
            }
        }

        private sealed class PostalCodeHybridRowSerializer : HybridRowSerializer
        {
            private static readonly Utf8String ZipName = Utf8String.TranscodeUtf16("zip");
            private static readonly Utf8String Plus4Name = Utf8String.TranscodeUtf16("plus4");
            private readonly LayoutColumn zip;
            private readonly LayoutColumn plus4;
            private readonly StringToken plus4Token;

            // ReSharper disable once UnusedParameter.Local
            public PostalCodeHybridRowSerializer(Layout layout, LayoutResolver resolver)
            {
                layout.TryFind(PostalCodeHybridRowSerializer.ZipName, out this.zip);
                layout.TryFind(PostalCodeHybridRowSerializer.Plus4Name, out this.plus4);
                layout.Tokenizer.TryFindToken(this.plus4.Path, out this.plus4Token);
            }

            public override Result WriteBuffer(ref RowBuffer row, ref RowCursor root, Dictionary<Utf8String, object> tableValue)
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    Result r;
                    switch (0)
                    {
                        case 0 when key.Equals(PostalCodeHybridRowSerializer.ZipName):
                            if (value != null)
                            {
                                r = LayoutType.Int32.WriteFixed(ref row, ref root, this.zip, (int)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(PostalCodeHybridRowSerializer.Plus4Name):
                            if (value != null)
                            {
                                root.Find(ref row, this.plus4Token);
                                r = LayoutType.Int16.WriteSparse(ref row, ref root, (short)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        default:
                            Contract.Fail($"Unknown property name: {key}");
                            break;
                    }
                }

                return Result.Success;
            }

            public override Result ReadBuffer(ref RowBuffer row, ref RowCursor root)
            {
                Result r = LayoutType.Int32.ReadFixed(ref row, ref root, this.zip, out int _);
                if (r != Result.Success)
                {
                    return r;
                }

                root.Find(ref row, this.plus4Token);
                return LayoutType.Int16.ReadSparse(ref row, ref root, out short _);
            }
        }

        private sealed class AddressHybridRowSerializer : HybridRowSerializer
        {
            private static readonly Utf8String StreetName = Utf8String.TranscodeUtf16("street");
            private static readonly Utf8String CityName = Utf8String.TranscodeUtf16("city");
            private static readonly Utf8String StateName = Utf8String.TranscodeUtf16("state");
            private static readonly Utf8String PostalCodeName = Utf8String.TranscodeUtf16("postal_code");
            private readonly LayoutColumn street;
            private readonly LayoutColumn city;
            private readonly LayoutColumn state;
            private readonly LayoutColumn postalCode;
            private readonly StringToken postalCodeToken;
            private readonly PostalCodeHybridRowSerializer postalCodeSerializer;

            public AddressHybridRowSerializer(Layout layout, LayoutResolver resolver)
            {
                layout.TryFind(AddressHybridRowSerializer.StreetName, out this.street);
                layout.TryFind(AddressHybridRowSerializer.CityName, out this.city);
                layout.TryFind(AddressHybridRowSerializer.StateName, out this.state);
                layout.TryFind(AddressHybridRowSerializer.PostalCodeName, out this.postalCode);
                layout.Tokenizer.TryFindToken(this.postalCode.Path, out this.postalCodeToken);
                this.postalCodeSerializer = new PostalCodeHybridRowSerializer(resolver.Resolve(this.postalCode.TypeArgs.SchemaId), resolver);
            }

            public override Result WriteBuffer(ref RowBuffer row, ref RowCursor root, Dictionary<Utf8String, object> tableValue)
            {
                foreach ((Utf8String key, object value) in tableValue)
                {
                    Result r;
                    switch (0)
                    {
                        case 0 when key.Equals(AddressHybridRowSerializer.StreetName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.street, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(AddressHybridRowSerializer.CityName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteVariable(ref row, ref root, this.city, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(AddressHybridRowSerializer.StateName):
                            if (value != null)
                            {
                                r = LayoutType.Utf8.WriteFixed(ref row, ref root, this.state, (Utf8String)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }
                            }

                            break;

                        case 0 when key.Equals(AddressHybridRowSerializer.PostalCodeName):
                            if (value != null)
                            {
                                root.Find(ref row, this.postalCodeToken);
                                r = LayoutType.UDT.WriteScope(ref row, ref root, this.postalCode.TypeArgs, out RowCursor childScope);

                                if (r != Result.Success)
                                {
                                    return r;
                                }

                                r = this.postalCodeSerializer.WriteBuffer(ref row, ref childScope, (Dictionary<Utf8String, object>)value);
                                if (r != Result.Success)
                                {
                                    return r;
                                }

                                root.Skip(ref row, ref childScope);
                            }

                            break;

                        default:
                            Contract.Fail($"Unknown property name: {key}");
                            break;
                    }
                }

                return Result.Success;
            }

            public override Result ReadBuffer(ref RowBuffer row, ref RowCursor root)
            {
                Result r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.street, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadVariable(ref row, ref root, this.city, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                r = LayoutType.Utf8.ReadFixed(ref row, ref root, this.state, out Utf8Span _);
                if (r != Result.Success)
                {
                    return r;
                }

                root.Find(ref row, this.postalCodeToken);
                r = LayoutType.UDT.ReadScope(ref row, ref root, out RowCursor childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                r = this.postalCodeSerializer.ReadBuffer(ref row, ref childScope);
                if (r != Result.Success)
                {
                    return r;
                }

                root.Skip(ref row, ref childScope);
                return Result.Success;
            }
        }
    }
}
