//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.ISpanResizer;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;
import azure.data.cosmos.serialization.hybridrow.perf.CodeGenRowGenerator.GuestsHybridRowSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal ref struct CodeGenRowGenerator
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class CodeGenRowGenerator {
    private HybridRowSerializer dispatcher;
    private RowBuffer row = new RowBuffer();


    public CodeGenRowGenerator(int capacity, Layout layout, LayoutResolver resolver) {
        this(capacity, layout, resolver, null);
    }

    public CodeGenRowGenerator() {
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public CodeGenRowGenerator(int capacity, Layout layout, LayoutResolver resolver,
    // ISpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public CodeGenRowGenerator(int capacity, Layout layout, LayoutResolver resolver, ISpanResizer<Byte> resizer) {
        this.row = new RowBuffer(capacity, resizer);
        this.row.InitLayout(HybridRowVersion.V1, layout, resolver);

        switch (layout.getName()) {
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
                Contract.Fail(String.format("Unknown schema will be ignored: %1$s", layout.getName()));
                this.dispatcher = null;
                break;
        }
    }

    public int getLength() {
        return this.row.getLength();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadBuffer(byte[] buffer)
    public Result ReadBuffer(byte[] buffer) {
        this.row = new RowBuffer(buffer.AsSpan(), HybridRowVersion.V1, this.row.getResolver());
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.row);
        RowCursor root = RowCursor.Create(tempRef_row);
        this.row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        Result tempVar = this.dispatcher.ReadBuffer(tempRef_row2, tempRef_root);
        root = tempRef_root.argValue;
        this.row = tempRef_row2.argValue;
        return tempVar;
    }

    public void Reset() {
        Layout layout = this.row.getResolver().Resolve(this.row.getHeader().getSchemaId().clone());
        this.row.InitLayout(HybridRowVersion.V1, layout, this.row.getResolver());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.row.ToArray();
    }

    public Result WriteBuffer(HashMap<Utf8String, Object> tableValue) {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.row);
        RowCursor root = RowCursor.Create(tempRef_row);
        this.row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        Result tempVar = this.dispatcher.WriteBuffer(tempRef_row2, tempRef_root, tableValue);
        root = tempRef_root.argValue;
        this.row = tempRef_row2.argValue;
        return tempVar;
    }

    public CodeGenRowGenerator clone() {
        CodeGenRowGenerator varCopy = new CodeGenRowGenerator();

        varCopy.row = this.row.clone();
        varCopy.dispatcher = this.dispatcher;

        return varCopy;
    }

    private final static class AddressHybridRowSerializer extends HybridRowSerializer {
        private static final Utf8String CityName = Utf8String.TranscodeUtf16("city");
        private static final Utf8String PostalCodeName = Utf8String.TranscodeUtf16("postal_code");
        private static final Utf8String StateName = Utf8String.TranscodeUtf16("state");
        private static final Utf8String StreetName = Utf8String.TranscodeUtf16("street");
        private LayoutColumn city;
        private LayoutColumn postalCode;
        private PostalCodeHybridRowSerializer postalCodeSerializer;
        private StringToken postalCodeToken = new StringToken();
        private LayoutColumn state;
        private LayoutColumn street;

        public AddressHybridRowSerializer(Layout layout, LayoutResolver resolver) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_street = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(AddressHybridRowSerializer.StreetName, tempOut_street);
            this.street = tempOut_street.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_city = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(AddressHybridRowSerializer.CityName, tempOut_city);
            this.city = tempOut_city.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_state = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(AddressHybridRowSerializer.StateName, tempOut_state);
            this.state = tempOut_state.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_postalCode = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(AddressHybridRowSerializer.PostalCodeName, tempOut_postalCode);
            this.postalCode = tempOut_postalCode.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_postalCodeToken = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
            layout.getTokenizer().TryFindToken(this.postalCode.getPath(), tempOut_postalCodeToken);
            this.postalCodeToken = tempOut_postalCodeToken.argValue;
            this.postalCodeSerializer = new PostalCodeHybridRowSerializer(resolver.Resolve(this.postalCode.getTypeArgs().getSchemaId().clone()), resolver);
        }

        @Override
        public Result ReadBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
            Result r = LayoutType.Utf8.ReadVariable(row, root, this.street, out _);
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
            r = LayoutType.Utf8.ReadVariable(row, root, this.city, out _);
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
            r = LayoutType.Utf8.ReadFixed(row, root, this.state, out _);
            if (r != Result.Success) {
                return r;
            }

            root.argValue.Find(row, this.postalCodeToken.clone());
            RowCursor childScope;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            r = LayoutType.UDT.ReadScope(row, root, tempOut_childScope);
            childScope = tempOut_childScope.argValue;
            if (r != Result.Success) {
                return r;
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            r = this.postalCodeSerializer.ReadBuffer(row, tempRef_childScope);
            childScope = tempRef_childScope.argValue;
            if (r != Result.Success) {
                return r;
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row, tempRef_childScope2);
            childScope = tempRef_childScope2.argValue;
            return Result.Success;
        }

        @Override
        public Result WriteBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, HashMap<Utf8String, Object> tableValue) {
            for ((Utf8String key,Object value) :tableValue)
            {
                Result r;
                switch (0) {
                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case' statements:
                    //ORIGINAL LINE: case 0 when key.Equals(AddressHybridRowSerializer.StreetName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.street, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case' statements:
                    //ORIGINAL LINE: case 0 when key.Equals(AddressHybridRowSerializer.CityName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.city, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case' statements:
                    //ORIGINAL LINE: case 0 when key.Equals(AddressHybridRowSerializer.StateName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteFixed(row, root, this.state, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case' statements:
                    //ORIGINAL LINE: case 0 when key.Equals(AddressHybridRowSerializer.PostalCodeName):
                    case 0
                        if (value != null) {
                            root.argValue.Find(row, this.postalCodeToken.clone());
                            RowCursor childScope;
                            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                            r = LayoutType.UDT.WriteScope(row, root, this.postalCode.getTypeArgs().clone(), tempOut_childScope);
                            childScope = tempOut_childScope.argValue;

                            if (r != Result.Success) {
                                return r;
                            }

                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                            r = this.postalCodeSerializer.WriteBuffer(row, tempRef_childScope, (HashMap<Utf8String, Object>)value);
                            childScope = tempRef_childScope.argValue;
                            if (r != Result.Success) {
                                return r;
                            }

                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row, tempRef_childScope2);
                            childScope = tempRef_childScope2.argValue;
                        }

                        break;

                    default:
                        Contract.Fail(String.format("Unknown property name: %1$s", key));
                        break;
                }
            }

            return Result.Success;
        }
    }

    private final static class GuestsHybridRowSerializer extends HybridRowSerializer {
        private static final Utf8String AddressesName = Utf8String.TranscodeUtf16("addresses");
        private static final Utf8String ConfirmNumberName = Utf8String.TranscodeUtf16("confirm_number");
        private static final Utf8String EmailsName = Utf8String.TranscodeUtf16("emails");
        private static final Utf8String FirstNameName = Utf8String.TranscodeUtf16("first_name");
        private static final Utf8String GuestIdName = Utf8String.TranscodeUtf16("guest_id");
        private static final Utf8String LastNameName = Utf8String.TranscodeUtf16("last_name");
        private static final Utf8String PhoneNumbersName = Utf8String.TranscodeUtf16("phone_numbers");
        private static final Utf8String TitleName = Utf8String.TranscodeUtf16("title");
        private AddressHybridRowSerializer addressSerializer;
        private LayoutScope.WriterFunc<HashMap<Utf8String, Object>> addressSerializerWriter;
        private LayoutColumn addresses;
        private TypeArgumentList addressesFieldType = new TypeArgumentList();
        private StringToken addressesToken = new StringToken();
        private LayoutColumn confirmNumber;
        private LayoutColumn emails;
        private StringToken emailsToken = new StringToken();
        private LayoutColumn firstName;
        private LayoutColumn guestId;
        private LayoutColumn lastName;
        private LayoutColumn phoneNumbers;
        private StringToken phoneNumbersToken = new StringToken();
        private LayoutColumn title;

        public GuestsHybridRowSerializer(Layout layout, LayoutResolver resolver) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_guestId =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.GuestIdName, tempOut_guestId);
            this.guestId = tempOut_guestId.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_firstName = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.FirstNameName, tempOut_firstName);
            this.firstName = tempOut_firstName.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_lastName
                = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.LastNameName, tempOut_lastName);
            this.lastName = tempOut_lastName.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_title =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.TitleName, tempOut_title);
            this.title = tempOut_title.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_emails =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.EmailsName, tempOut_emails);
            this.emails = tempOut_emails.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_phoneNumbers = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.PhoneNumbersName, tempOut_phoneNumbers);
            this.phoneNumbers = tempOut_phoneNumbers.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_addresses = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.AddressesName, tempOut_addresses);
            this.addresses = tempOut_addresses.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_confirmNumber = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(GuestsHybridRowSerializer.ConfirmNumberName, tempOut_confirmNumber);
            this.confirmNumber = tempOut_confirmNumber.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_emailsToken = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
            layout.getTokenizer().TryFindToken(this.emails.getPath(), tempOut_emailsToken);
            this.emailsToken = tempOut_emailsToken.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_phoneNumbersToken = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
            layout.getTokenizer().TryFindToken(this.phoneNumbers.getPath(), tempOut_phoneNumbersToken);
            this.phoneNumbersToken = tempOut_phoneNumbersToken.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_addressesToken = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
            layout.getTokenizer().TryFindToken(this.addresses.getPath(), tempOut_addressesToken);
            this.addressesToken = tempOut_addressesToken.argValue;

            this.addressesFieldType =
                new TypeArgumentList(new TypeArgument[] { new TypeArgument(LayoutType.TypedTuple,
                    this.addresses.getTypeArgs().clone()) });

            this.addressSerializer =
                new AddressHybridRowSerializer(resolver.Resolve(this.addresses.getTypeArgs().get(1).getTypeArgs().getSchemaId().clone()), resolver);
            this.addressSerializerWriter = (tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                            HashMap<Utf8String, Object> context) -> addressSerializer.WriteBuffer(b,
                scope, context);
        }

        @Override
        public Result ReadBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
            java.util.UUID _;
            tangible.OutObject<UUID> tempOut__ = new tangible.OutObject<UUID>();
            Result r = LayoutType.Guid.ReadFixed(row, root, this.guestId, tempOut__);
            _ = tempOut__.argValue;
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            r = LayoutType.Utf8.ReadVariable(row, root, this.firstName, out _);
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            r = LayoutType.Utf8.ReadVariable(row, root, this.lastName, out _);
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            r = LayoutType.Utf8.ReadVariable(row, root, this.title, out _);
            if (r != Result.Success) {
                return r;
            }

            root.argValue.Find(row, this.emailsToken.clone());
            RowCursor childScope;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            r = LayoutType.TypedArray.ReadScope(row, root, tempOut_childScope);
            childScope = tempOut_childScope.argValue;
            if (r != Result.Success) {
                return r;
            }

            while (childScope.MoveNext(row)) {
                Utf8Span _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                r = LayoutType.Utf8.ReadSparse(row, ref childScope, out _);
                if (r != Result.Success) {
                    return r;
                }
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_childScope);
            childScope = tempRef_childScope.argValue;
            root.argValue.Find(row, this.phoneNumbersToken.clone());
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope2 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            r = LayoutType.TypedArray.ReadScope(row, root, tempOut_childScope2);
            childScope = tempOut_childScope2.argValue;
            if (r != Result.Success) {
                return r;
            }

            while (childScope.MoveNext(row)) {
                Utf8Span _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                r = LayoutType.Utf8.ReadSparse(row, ref childScope, out _);
                if (r != Result.Success) {
                    return r;
                }
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_childScope2);
            childScope = tempRef_childScope2.argValue;
            root.argValue.Find(row, this.addressesToken.clone());
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope3 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            r = LayoutType.TypedMap.ReadScope(row, root, tempOut_childScope3);
            childScope = tempOut_childScope3.argValue;
            if (r != Result.Success) {
                return r;
            }

            while (childScope.MoveNext(row)) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                RowCursor tupleScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                r = LayoutType.TypedTuple.ReadScope(row, tempRef_childScope3, tempOut_tupleScope);
                tupleScope = tempOut_tupleScope.argValue;
                childScope = tempRef_childScope3.argValue;
                if (r != Result.Success) {
                    return r;
                }

                if (!tupleScope.MoveNext(row)) {
                    return Result.InvalidRow;
                }

                Utf8Span _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                r = LayoutType.Utf8.ReadSparse(row, ref tupleScope, out _);
                if (r != Result.Success) {
                    return r;
                }

                if (!tupleScope.MoveNext(row)) {
                    return Result.InvalidRow;
                }

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor valueScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_valueScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                r = LayoutType.ImmutableUDT.ReadScope(row, tempRef_tupleScope, tempOut_valueScope);
                valueScope = tempOut_valueScope.argValue;
                tupleScope = tempRef_tupleScope.argValue;
                if (r != Result.Success) {
                    return r;
                }

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
                r = this.addressSerializer.ReadBuffer(row, tempRef_valueScope);
                valueScope = tempRef_valueScope.argValue;
                if (r != Result.Success) {
                    return r;
                }

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                tupleScope.Skip(row, ref valueScope);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                childScope.Skip(row, ref tupleScope);
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_childScope4);
            childScope = tempRef_childScope4.argValue;

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            return LayoutType.Utf8.ReadVariable(row, root, this.confirmNumber, out _);
        }

        @Override
        public Result WriteBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                  HashMap<Utf8String, Object> tableValue) {
            for ((Utf8String key,Object value) :tableValue)
            {
                Result r;
                switch (0) {
                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.GuestIdName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Guid.WriteFixed(row, root, this.guestId, (UUID)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.FirstNameName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.firstName, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.LastNameName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.lastName, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.TitleName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.title, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.EmailsName):
                    case 0
                        if (value != null) {
                            root.argValue.Find(row, this.emailsToken.clone());
                            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref'
                            // keyword - these are not converted by C# to Java Converter:
                            r = LayoutType.TypedArray.WriteScope(row, root, this.emails.getTypeArgs().clone(),
                                (ArrayList<Object>)value, (ref RowBuffer row2, ref RowCursor childScope,
                                                           ArrayList<Object> context) ->
                            {
                                for (Object item : context) {
                                    tangible.RefObject<ref RowBuffer>tempRef_row2 = new tangible.RefObject<ref
                                    RowBuffer > (row2);
                                    tangible.RefObject<ref RowCursor>tempRef_childScope = new tangible.RefObject<ref
                                    RowCursor > (childScope);
                                    Result r2 = LayoutType.Utf8.WriteSparse(tempRef_row2, tempRef_childScope,
                                        (Utf8String)item);
                                    childScope = tempRef_childScope.argValue;
                                    row2 = tempRef_row2.argValue;
                                    if (r2 != Result.Success) {
                                        return r2;
                                    }

                                    tangible.RefObject<ref RowBuffer>tempRef_row22 = new tangible.RefObject<ref
                                    RowBuffer > (row2);
                                    childScope.MoveNext(tempRef_row22);
                                    row2 = tempRef_row22.argValue;
                                }

                                return Result.Success;
                            });
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.PhoneNumbersName):
                    case 0
                        if (value != null) {
                            root.argValue.Find(row, this.phoneNumbersToken.clone());
                            RowCursor childScope;
                            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                            r = LayoutType.TypedArray.WriteScope(row, root, this.phoneNumbers.getTypeArgs().clone(),
                                tempOut_childScope);
                            childScope = tempOut_childScope.argValue;
                            if (r != Result.Success) {
                                return r;
                            }

                            for (Object item : (ArrayList<Object>)value) {
                                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved
                                // 'ref' keyword - these cannot be converted using the 'RefObject' helper class
                                // unless the method is within the code being modified:
                                r = LayoutType.Utf8.WriteSparse(row, ref childScope, (Utf8String)item);
                                if (r != Result.Success) {
                                    return r;
                                }

                                childScope.MoveNext(row);
                            }

                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row, tempRef_childScope);
                            childScope = tempRef_childScope.argValue;
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.AddressesName):
                    case 0
                        if (value != null) {
                            root.argValue.Find(row, this.addressesToken.clone());
                            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref'
                            // keyword - these are not converted by C# to Java Converter:
                            r = LayoutType.TypedMap.WriteScope(row, root, this.addresses.getTypeArgs().clone(), (this
                                , (ArrayList<Object>)value), (ref
                            RowBuffer row2, ref RowCursor childScope, (GuestsHybridRowSerializer
                            _this, ArrayList < Object > value)ctx) ->
                            {
                                for (Object item : ctx.value) {
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row2);
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                                    Result r2 = LayoutType.TypedTuple.WriteScope(tempRef_row2, tempRef_childScope,
                                        ctx._this.addresses.TypeArgs, (ctx._this, (ArrayList<Object>)item),
                                    (ref RowBuffer row3, ref RowCursor tupleScope, (GuestsHybridRowSerializer
                                    _this, ArrayList < Object > value)ctx2) ->
                                    {
                                        tangible.RefObject<ref RowBuffer>tempRef_row3 = new tangible.RefObject<ref
                                        RowBuffer > (row3);
                                        tangible.RefObject<ref RowCursor>tempRef_tupleScope = new tangible.RefObject<ref
                                        RowCursor > (tupleScope);
                                        Result r3 = LayoutType.Utf8.WriteSparse(tempRef_row3, tempRef_tupleScope,
                                            (Utf8String)ctx2.value[0]);
                                        tupleScope = tempRef_tupleScope.argValue;
                                        row3 = tempRef_row3.argValue;
                                        if (r3 != Result.Success) {
                                            return r3;
                                        }

                                        tangible.RefObject<ref RowBuffer>tempRef_row32 = new tangible.RefObject<ref
                                        RowBuffer > (row3);
                                        tupleScope.MoveNext(tempRef_row32);
                                        row3 = tempRef_row32.argValue;
                                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row33 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row3);
                                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                                        Result tempVar = LayoutType.ImmutableUDT.WriteScope(tempRef_row33,
                                            tempRef_tupleScope2, ctx2._this.addresses.TypeArgs[1].TypeArgs,
                                            (HashMap<Utf8String, Object>)ctx2.value[1],
                                            ctx2._this.addressSerializerWriter);
                                        tupleScope = tempRef_tupleScope2.argValue;
                                        row3 = tempRef_row33.argValue;
                                        return tempVar;
                                    })
                                    childScope = tempRef_childScope.argValue;
                                    row2 = tempRef_row2.argValue;
                                    if (r2 != Result.Success) {
                                        return r2;
                                    }

                                    tangible.RefObject<ref RowBuffer>tempRef_row22 = new tangible.RefObject<ref
                                    RowBuffer > (row2);
                                    childScope.MoveNext(tempRef_row22);
                                    row2 = tempRef_row22.argValue;
                                }

                                return Result.Success;
                            })
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(GuestsHybridRowSerializer.ConfirmNumberName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.confirmNumber, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    default:
                        Contract.Fail(String.format("Unknown property name: %1$s", key));
                        break;
                }
            }

            return Result.Success;
        }
    }

    private final static class HotelsHybridRowSerializer extends HybridRowSerializer {
        private static final Utf8String AddressName = Utf8String.TranscodeUtf16("address");
        private static final Utf8String HotelIdName = Utf8String.TranscodeUtf16("hotel_id");
        private static final Utf8String NameName = Utf8String.TranscodeUtf16("name");
        private static final Utf8String PhoneName = Utf8String.TranscodeUtf16("phone");
        private LayoutColumn address;
        private AddressHybridRowSerializer addressSerializer;
        private StringToken addressToken = new StringToken();
        private LayoutColumn hotelId;
        private LayoutColumn name;
        private LayoutColumn phone;

        public HotelsHybridRowSerializer(Layout layout, LayoutResolver resolver) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_hotelId =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(HotelsHybridRowSerializer.HotelIdName, tempOut_hotelId);
            this.hotelId = tempOut_hotelId.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_name =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(HotelsHybridRowSerializer.NameName, tempOut_name);
            this.name = tempOut_name.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_phone =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(HotelsHybridRowSerializer.PhoneName, tempOut_phone);
            this.phone = tempOut_phone.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_address =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(HotelsHybridRowSerializer.AddressName, tempOut_address);
            this.address = tempOut_address.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_addressToken = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
            layout.getTokenizer().TryFindToken(this.address.getPath(), tempOut_addressToken);
            this.addressToken = tempOut_addressToken.argValue;
            this.addressSerializer =
                new AddressHybridRowSerializer(resolver.Resolve(this.address.getTypeArgs().getSchemaId().clone()),
                    resolver);
        }

        @Override
        public Result ReadBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            Result r = LayoutType.Utf8.ReadFixed(row, root, this.hotelId, out _);
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            r = LayoutType.Utf8.ReadVariable(row, root, this.name, out _);
            if (r != Result.Success) {
                return r;
            }

            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            r = LayoutType.Utf8.ReadVariable(row, root, this.phone, out _);
            if (r != Result.Success) {
                return r;
            }

            root.argValue.Find(row, this.addressToken.clone());
            RowCursor childScope;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            r = LayoutType.UDT.ReadScope(row, root, tempOut_childScope);
            childScope = tempOut_childScope.argValue;
            if (r != Result.Success) {
                return r;
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            r = this.addressSerializer.ReadBuffer(row, tempRef_childScope);
            childScope = tempRef_childScope.argValue;
            if (r != Result.Success) {
                return r;
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_childScope2);
            childScope = tempRef_childScope2.argValue;
            return Result.Success;
        }

        @Override
        public Result WriteBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                  HashMap<Utf8String, Object> tableValue) {
            for ((Utf8String key,Object value) :tableValue)
            {
                Result r;
                switch (0) {
                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(HotelsHybridRowSerializer.HotelIdName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteFixed(row, root, this.hotelId, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(HotelsHybridRowSerializer.NameName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.name, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(HotelsHybridRowSerializer.PhoneName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteVariable(row, root, this.phone, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(HotelsHybridRowSerializer.AddressName):
                    case 0
                        if (value != null) {
                            root.argValue.Find(row, this.addressToken.clone());
                            RowCursor childScope;
                            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_childScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                            r = LayoutType.UDT.WriteScope(row, root, this.address.getTypeArgs().clone(),
                                tempOut_childScope);
                            childScope = tempOut_childScope.argValue;
                            if (r != Result.Success) {
                                return r;
                            }

                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                            r = this.addressSerializer.WriteBuffer(row, tempRef_childScope, (HashMap<Utf8String,
                                Object>)value);
                            childScope = tempRef_childScope.argValue;
                            if (r != Result.Success) {
                                return r;
                            }

                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
                            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row, tempRef_childScope2);
                            childScope = tempRef_childScope2.argValue;
                        }

                        break;

                    default:
                        Contract.Fail(String.format("Unknown property name: %1$s", key));
                        break;
                }
            }

            return Result.Success;
        }
    }

    private abstract static class HybridRowSerializer {
        public abstract Result ReadBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root);

        public abstract Result WriteBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                           HashMap<Utf8String, Object> tableValue);
    }

    private final static class PostalCodeHybridRowSerializer extends HybridRowSerializer {
        private static final Utf8String Plus4Name = Utf8String.TranscodeUtf16("plus4");
        private static final Utf8String ZipName = Utf8String.TranscodeUtf16("zip");
        private LayoutColumn plus4;
        private StringToken plus4Token = new StringToken();
        private LayoutColumn zip;

        // ReSharper disable once UnusedParameter.Local
        public PostalCodeHybridRowSerializer(Layout layout, LayoutResolver resolver) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_zip =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(PostalCodeHybridRowSerializer.ZipName, tempOut_zip);
            this.zip = tempOut_zip.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_plus4 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(PostalCodeHybridRowSerializer.Plus4Name, tempOut_plus4);
            this.plus4 = tempOut_plus4.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_plus4Token = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
            layout.getTokenizer().TryFindToken(this.plus4.getPath(), tempOut_plus4Token);
            this.plus4Token = tempOut_plus4Token.argValue;
        }

        @Override
        public Result ReadBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
            int _;
            tangible.OutObject<Integer> tempOut__ = new tangible.OutObject<Integer>();
            Result r = LayoutType.Int32.ReadFixed(row, root, this.zip, tempOut__);
            _ = tempOut__.argValue;
            if (r != Result.Success) {
                return r;
            }

            root.argValue.Find(row, this.plus4Token.clone());
            short _;
            tangible.OutObject<Short> tempOut__2 = new tangible.OutObject<Short>();
            Result tempVar = LayoutType.Int16.ReadSparse(row, root, tempOut__2);
            _ = tempOut__2.argValue;
            return tempVar;
        }

        @Override
        public Result WriteBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                  HashMap<Utf8String, Object> tableValue) {
            for ((Utf8String key,Object value) :tableValue)
            {
                Result r;
                switch (0) {
                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(PostalCodeHybridRowSerializer.ZipName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Int32.WriteFixed(row, root, this.zip, (int)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(PostalCodeHybridRowSerializer.Plus4Name):
                    case 0
                        if (value != null) {
                            root.argValue.Find(row, this.plus4Token.clone());
                            r = LayoutType.Int16.WriteSparse(row, root, (short)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    default:
                        Contract.Fail(String.format("Unknown property name: %1$s", key));
                        break;
                }
            }

            return Result.Success;
        }
    }

    private final static class RoomsHybridRowSerializer extends HybridRowSerializer {
        private static final Utf8String DateName = Utf8String.TranscodeUtf16("date");
        private static final Utf8String HotelIdName = Utf8String.TranscodeUtf16("hotel_id");
        private static final Utf8String IsAvailableName = Utf8String.TranscodeUtf16("is_available");
        private static final Utf8String RoomNumberName = Utf8String.TranscodeUtf16("room_number");
        private LayoutColumn date;
        private LayoutColumn hotelId;
        private LayoutColumn isAvailable;
        private LayoutColumn roomNumber;

        // ReSharper disable once UnusedParameter.Local
        public RoomsHybridRowSerializer(Layout layout, LayoutResolver resolver) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_hotelId =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(RoomsHybridRowSerializer.HotelIdName, tempOut_hotelId);
            this.hotelId = tempOut_hotelId.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_date =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(RoomsHybridRowSerializer.DateName, tempOut_date);
            this.date = tempOut_date.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_roomNumber = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(RoomsHybridRowSerializer.RoomNumberName, tempOut_roomNumber);
            this.roomNumber = tempOut_roomNumber.argValue;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_isAvailable = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            layout.TryFind(RoomsHybridRowSerializer.IsAvailableName, tempOut_isAvailable);
            this.isAvailable = tempOut_isAvailable.argValue;
        }

        @Override
        public Result ReadBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
            Utf8Span _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            Result r = LayoutType.Utf8.ReadFixed(row, root, this.hotelId, out _);
            if (r != Result.Success) {
                return r;
            }

            java.time.LocalDateTime _;
            tangible.OutObject<LocalDateTime> tempOut__ = new tangible.OutObject<LocalDateTime>();
            r = LayoutType.DateTime.ReadFixed(row, root, this.date, tempOut__);
            _ = tempOut__.argValue;
            if (r != Result.Success) {
                return r;
            }

            byte _;
            tangible.OutObject<Byte> tempOut__2 = new tangible.OutObject<Byte>();
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: r = LayoutType.UInt8.ReadFixed(ref row, ref root, this.roomNumber, out byte _);
            r = LayoutType.UInt8.ReadFixed(row, root, this.roomNumber, tempOut__2);
            _ = tempOut__2.argValue;
            if (r != Result.Success) {
                return r;
            }

            boolean _;
            tangible.OutObject<Boolean> tempOut__3 = new tangible.OutObject<Boolean>();
            Result tempVar = LayoutType.Boolean.ReadFixed(row, root, this.isAvailable, tempOut__3);
            _ = tempOut__3.argValue;
            return tempVar;
        }

        @Override
        public Result WriteBuffer(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                  HashMap<Utf8String, Object> tableValue) {
            for ((Utf8String key,Object value) :tableValue)
            {
                Result r;
                switch (0) {
                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(RoomsHybridRowSerializer.HotelIdName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Utf8.WriteFixed(row, root, this.hotelId, (Utf8String)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(RoomsHybridRowSerializer.DateName):
                    case 0
                        if (value != null) {
                            r = LayoutType.DateTime.WriteFixed(row, root, this.date, (LocalDateTime)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(RoomsHybridRowSerializer.RoomNumberName):
                    case 0
                        if (value != null) {
                            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                            //ORIGINAL LINE: r = LayoutType.UInt8.WriteFixed(ref row, ref root, this.roomNumber,
                            // (byte)value);
                            r = LayoutType.UInt8.WriteFixed(row, root, this.roomNumber, (byte)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# 'when' clause in 'case'
                    // statements:
                    //ORIGINAL LINE: case 0 when key.Equals(RoomsHybridRowSerializer.IsAvailableName):
                    case 0
                        if (value != null) {
                            r = LayoutType.Boolean.WriteFixed(row, root, this.isAvailable, (boolean)value);
                            if (r != Result.Success) {
                                return r;
                            }
                        }

                        break;

                    default:
                        Contract.Fail(String.format("Unknown property name: %1$s", key));
                        break;
                }
            }

            return Result.Success;
        }
    }
}