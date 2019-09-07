// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

// ReSharper disable once StringLiteralTypo
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here
// are anonymous.")][DeploymentItem("TestData\\CustomerSchema.json", "TestData")] public sealed class
// CustomerExampleUnitTests
public final class CustomerExampleUnitTests {
    private final Hotel hotelExample = new Hotel() {
        Id ="The-Westin-St-John-Resort-Villas-1187",Name ="The Westin St. John Resort Villas",Phone ="+1 340-693-8000"
            ,Address =new Address

        {
            Street = "300B Chocolate Hole", City = "Great Cruz Bay", State = "VI", PostalCode = new PostalCode {
            Zip = 00830, Plus4 = 0001
        }
        }
    };
    private LayoutResolver customerResolver;
    private Namespace customerSchema;
    private Layout guestLayout;
    private Layout hotelLayout;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateGuest()
    public void CreateGuest() {
        RowBuffer row = new RowBuffer(1024 * 1024);
        row.initLayout(HybridRowVersion.V1, this.guestLayout, this.customerResolver);

        Guest g1 = new Guest();
        g1.Id = UUID.fromString("64d9d6d3-fd6b-4556-8c6e-d960a7ece7b9");
        g1.FirstName = "John";
        g1.LastName = "Adams";
        g1.Title = "President of the United States";
        g1.PhoneNumbers = new ArrayList<String>(Arrays.asList("(202) 456-1111"));
        g1.ConfirmNumber = "(202) 456-1111";
        g1.Emails = new TreeSet<String> {
            "president@whitehouse.gov"
        }
        Address tempVar = new Address();
        tempVar.setStreet("1600 Pennsylvania Avenue NW");
        tempVar.setCity("Washington, D.C.");
        tempVar.setState("DC");
        PostalCode tempVar2 = new PostalCode();
        tempVar2.setZip(20500);
        tempVar2.setPlus4(0001);
        tempVar.setPostalCode(tempVar2);
        g1.Addresses = new HashMap<String, Address>(Map.ofEntries(Map.entry("home", tempVar)));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor rc1 = RowCursor.create(tempReference_row);
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc1 =
            new Reference<RowCursor>(rc1);
        this.WriteGuest(tempReference_row2, tempReference_rc1, g1);
        rc1 = tempReference_rc1.get();
        row = tempReference_row2.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        RowCursor rc2 = RowCursor.create(tempReference_row3);
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc2 =
            new Reference<RowCursor>(rc2);
        Guest g2 = this.ReadGuest(tempReference_row4, tempReference_rc2);
        rc2 = tempReference_rc2.get();
        row = tempReference_row4.get();
        assert g1 == g2;

        // Append an item to an existing list.
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        RowCursor rc3 = RowCursor.create(tempReference_row5);
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc3 =
            new Reference<RowCursor>(rc3);
        int index = this.AppendGuestEmail(tempReference_row6, tempReference_rc3, "vice_president@whitehouse.gov");
        rc3 = tempReference_rc3.get();
        row = tempReference_row6.get();
        assert 1 == index;
        g1.Emails.add("vice_president@whitehouse.gov");
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        RowCursor rc4 = RowCursor.create(tempReference_row7);
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc4 =
            new Reference<RowCursor>(rc4);
        g2 = this.ReadGuest(tempReference_row8, tempReference_rc4);
        rc4 = tempReference_rc4.get();
        row = tempReference_row8.get();
        assert g1 == g2;

        // Prepend an item to an existing list.
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        RowCursor rc5 = RowCursor.create(tempReference_row9);
        row = tempReference_row9.get();
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc5 =
            new Reference<RowCursor>(rc5);
        index = this.PrependGuestEmail(tempReference_row10, tempReference_rc5, "ex_president@whitehouse.gov");
        rc5 = tempReference_rc5.get();
        row = tempReference_row10.get();
        assert 0 == index;
        g1.Emails = new TreeSet<String> {
            "ex_president@whitehouse.gov", "president@whitehouse.gov", "vice_president@whitehouse.gov"
        }
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        RowCursor rc6 = RowCursor.create(tempReference_row11);
        row = tempReference_row11.get();
        Reference<RowBuffer> tempReference_row12 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc6 =
            new Reference<RowCursor>(rc6);
        g2 = this.ReadGuest(tempReference_row12, tempReference_rc6);
        rc6 = tempReference_rc6.get();
        row = tempReference_row12.get();
        assert g1 == g2;

        // InsertAt an item to an existing list.
        Reference<RowBuffer> tempReference_row13 =
            new Reference<RowBuffer>(row);
        RowCursor rc7 = RowCursor.create(tempReference_row13);
        row = tempReference_row13.get();
        Reference<RowBuffer> tempReference_row14 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc7 =
            new Reference<RowCursor>(rc7);
        index = this.InsertAtGuestEmail(tempReference_row14, tempReference_rc7, 1, "future_president@whitehouse.gov");
        rc7 = tempReference_rc7.get();
        row = tempReference_row14.get();
        assert 1 == index;
        g1.Emails = new TreeSet<String> {
            "ex_president@whitehouse.gov", "future_president@whitehouse.gov", "president@whitehouse.gov",
                "vice_president@whitehouse.gov"
        }

        Reference<RowBuffer> tempReference_row15 =
            new Reference<RowBuffer>(row);
        RowCursor rc8 = RowCursor.create(tempReference_row15);
        row = tempReference_row15.get();
        Reference<RowBuffer> tempReference_row16 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc8 =
            new Reference<RowCursor>(rc8);
        g2 = this.ReadGuest(tempReference_row16, tempReference_rc8);
        rc8 = tempReference_rc8.get();
        row = tempReference_row16.get();
        assert g1 == g2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateHotel()
    public void CreateHotel() {
        RowBuffer row = new RowBuffer(0);
        row.initLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

        Hotel h1 = this.hotelExample;
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.create(tempReference_row);
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_root =
            new Reference<RowCursor>(root);
        this.WriteHotel(tempReference_row2, tempReference_root, h1);
        root = tempReference_root.get();
        row = tempReference_row2.get();

        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        root = RowCursor.create(tempReference_row3);
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_root2 =
            new Reference<RowCursor>(root);
        Hotel h2 = this.ReadHotel(tempReference_row4, tempReference_root2);
        root = tempReference_root2.get();
        row = tempReference_row4.get();

        assert h1 == h2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FrozenHotel()
    public void FrozenHotel() {
        RowBuffer row = new RowBuffer(0);
        row.initLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

        Hotel h1 = this.hotelExample;
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.create(tempReference_row);
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_root =
            new Reference<RowCursor>(root);
        this.WriteHotel(tempReference_row2, tempReference_root, h1);
        root = tempReference_root.get();
        row = tempReference_row2.get();

        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        root = RowCursor.create(tempReference_row3);
        row = tempReference_row3.get();
        Address tempVar = new Address();
        tempVar.setStreet("300B Brownie Way");
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_root2 =
            new Reference<RowCursor>(root);
        ResultAssert.InsufficientPermissions(this.PartialUpdateHotelAddress(tempReference_row4, tempReference_root2, tempVar));
        root = tempReference_root2.get();
        row = tempReference_row4.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString("TestData\\CustomerSchema.json");
        this.customerSchema = Namespace.Parse(json);
        this.customerResolver = new LayoutResolverNamespace(this.customerSchema);
        this.hotelLayout = this.customerResolver.Resolve(tangible.ListHelper.find(this.customerSchema.getSchemas(),
            x -> x.Name.equals("Hotels")).SchemaId);
        this.guestLayout = this.customerResolver.Resolve(tangible.ListHelper.find(this.customerSchema.getSchemas(),
            x -> x.Name.equals("Guests")).SchemaId);
    }

    private int AppendGuestEmail(Reference<RowBuffer> row, Reference<RowCursor> root, String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.get().Find(row, c.Path);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().ReadScope(row, root, out emailScope));
        assert !emailScope.MoveTo(row, Integer.MAX_VALUE);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email));
        return emailScope.Index;
    }

    private int InsertAtGuestEmail(Reference<RowBuffer> row, Reference<RowCursor> root, int i,
                                   String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.get().Find(row, c.Path);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().ReadScope(row, root, out emailScope));
        assert emailScope.MoveTo(row, i);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email,
            UpdateOptions.InsertAt));
        return emailScope.Index;
    }

    private Result PartialUpdateHotelAddress(Reference<RowBuffer> row, Reference<RowCursor> root,
                                             Address a) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        root.get().Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = c.<LayoutUDT>TypeAs().ReadScope(row, root, out addressScope);
        if (r != Result.SUCCESS) {
            return r;
        }

        Layout addressLayout = addressScope.Layout;
        if (a.Street != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("street", out c);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUtf8>TypeAs().WriteVariable(row, ref addressScope, c, a.Street);
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (a.City != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("city", out c);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUtf8>TypeAs().WriteVariable(row, ref addressScope, c, a.City);
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (a.State != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("state", out c);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUtf8>TypeAs().WriteFixed(row, ref addressScope, c, a.State);
            if (r != Result.SUCCESS) {
                return r;
            }
        }

        if (a.PostalCode != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("postal_code", out c);
            addressScope.Find(row, c.Path);
            RowCursor postalCodeScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUDT>TypeAs().WriteScope(row, ref addressScope, c.TypeArgs, out postalCodeScope);
            if (r != Result.SUCCESS) {
                return r;
            }

            Reference<RowCursor> tempReference_postalCodeScope =
                new Reference<RowCursor>(postalCodeScope);
            this.WritePostalCode(row, tempReference_postalCodeScope, c.TypeArgs, a.PostalCode);
            postalCodeScope = tempReference_postalCodeScope.get();
        }

        return Result.SUCCESS;
    }

    private int PrependGuestEmail(Reference<RowBuffer> row, Reference<RowCursor> root, String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.get().Find(row, c.Path);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().ReadScope(row, root, out emailScope));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email,
            UpdateOptions.InsertAt));
        return emailScope.Index;
    }

    private static Address ReadAddress(Reference<RowBuffer> row, Reference<RowCursor> addressScope) {
        Address a = new Address();
        Layout addressLayout = addressScope.get().getLayout();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("street", out c);
        Out<String> tempOut_Street = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, addressScope, c, tempOut_Street));
        a.Street = tempOut_Street.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("city", out c);
        Out<String> tempOut_City = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, addressScope, c, tempOut_City));
        a.City = tempOut_City.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("state", out c);
        Out<String> tempOut_State = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadFixed(row, addressScope, c, tempOut_State));
        a.State = tempOut_State.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("postal_code", out c);
        addressScope.get().Find(row, c.Path);
        RowCursor postalCodeScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().ReadScope(row, addressScope, out postalCodeScope));
        Reference<RowCursor> tempReference_postalCodeScope =
            new Reference<RowCursor>(postalCodeScope);
        a.PostalCode = CustomerExampleUnitTests.ReadPostalCode(row, tempReference_postalCodeScope);
        postalCodeScope = tempReference_postalCodeScope.get();
        Reference<RowCursor> tempReference_postalCodeScope2 =
            new Reference<RowCursor>(postalCodeScope);
        RowCursors.skip(addressScope.get().clone(), row,
            tempReference_postalCodeScope2);
        postalCodeScope = tempReference_postalCodeScope2.get();
        return a;
    }

    private Guest ReadGuest(Reference<RowBuffer> row, Reference<RowCursor> root) {
        Guest g = new Guest();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("guest_id", out c);
        Out<UUID> tempOut_Id = new Out<UUID>();
        ResultAssert.IsSuccess(c.<LayoutGuid>TypeAs().ReadFixed(row, root, c, tempOut_Id));
        g.Id = tempOut_Id.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("first_name", out c);
        Out<String> tempOut_FirstName = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_FirstName));
        g.FirstName = tempOut_FirstName.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("last_name", out c);
        Out<String> tempOut_LastName = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_LastName));
        g.LastName = tempOut_LastName.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("title", out c);
        Out<String> tempOut_Title = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Title));
        g.Title = tempOut_Title.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("confirm_number", out c);
        Out<String> tempOut_ConfirmNumber = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_ConfirmNumber));
        g.ConfirmNumber = tempOut_ConfirmNumber.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out emailScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref emailScope, out emailScope) == Result.SUCCESS) {
            g.Emails = new TreeSet<String>();
            while (emailScope.MoveNext(row)) {
                String item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref emailScope,
                    out item));
                g.Emails.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("phone_numbers", out c);
        RowCursor phoneNumbersScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out phoneNumbersScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref phoneNumbersScope, out phoneNumbersScope) == Result.SUCCESS) {
            g.PhoneNumbers = new ArrayList<String>();
            while (phoneNumbersScope.MoveNext(row)) {
                String item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref phoneNumbersScope,
                    out item));
                g.PhoneNumbers.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("addresses", out c);
        RowCursor addressesScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out addressesScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedMap>TypeAs().ReadScope(row, ref addressesScope, out addressesScope) == Result.SUCCESS) {
            Reference<RowCursor> tempReference_addressesScope = new Reference<RowCursor>(addressesScope);
            TypeArgument tupleType = LayoutType.TypedMap.FieldType(tempReference_addressesScope).clone();
            addressesScope = tempReference_addressesScope.get();
            TypeArgument t0 = tupleType.getTypeArgs().get(0).clone();
            TypeArgument t1 = tupleType.getTypeArgs().get(1).clone();
            g.Addresses = new HashMap<String, Address>();
            RowCursor pairScope = null;
            Reference<RowCursor> tempReference_pairScope = new Reference<RowCursor>(pairScope);
            while (addressesScope.MoveNext(row, tempReference_pairScope)) {
                pairScope = tempReference_pairScope.get();
                Reference<RowCursor> tempReference_addressesScope2 = new Reference<RowCursor>(addressesScope);
                Out<RowCursor> tempOut_pairScope = new Out<RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row,
                    tempReference_addressesScope2, tempOut_pairScope));
                pairScope = tempOut_pairScope.get();
                addressesScope = tempReference_addressesScope2.get();
                assert RowCursors.moveNext(pairScope.clone(), row);
                Reference<RowCursor> tempReference_pairScope2 = new Reference<RowCursor>(pairScope);
                String key;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
                ResultAssert.IsSuccess(t0.<LayoutUtf8>TypeAs().ReadSparse(row, tempReference_pairScope2, out key));
                pairScope = tempReference_pairScope2.get();
                assert RowCursors.moveNext(pairScope.clone(), row);
                Reference<RowCursor> tempReference_pairScope3 = new Reference<RowCursor>(pairScope);
                RowCursor addressScope;
                Out<RowCursor> tempOut_addressScope = new Out<RowCursor>();
                ResultAssert.IsSuccess(t1.<LayoutUDT>TypeAs().ReadScope(row, tempReference_pairScope3, tempOut_addressScope));
                addressScope = tempOut_addressScope.get();
                pairScope = tempReference_pairScope3.get();
                Reference<RowCursor> tempReference_addressScope = new Reference<RowCursor>(addressScope);
                Address value = CustomerExampleUnitTests.ReadAddress(row, tempReference_addressScope);
                addressScope = tempReference_addressScope.get();
                g.Addresses.put(key, value);
                Reference<RowCursor> tempReference_addressScope2 = new Reference<RowCursor>(addressScope);
                assert !RowCursors.moveNext(pairScope.clone(), row, tempReference_addressScope2);
                addressScope = tempReference_addressScope2.get();
            }
            pairScope = tempReference_pairScope.get();
        }

        return g;
    }

    private Hotel ReadHotel(Reference<RowBuffer> row, Reference<RowCursor> root) {
        Hotel h = new Hotel();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("hotel_id", out c);
        Out<String> tempOut_Id = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Id));
        h.Id = tempOut_Id.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("name", out c);
        Out<String> tempOut_Name = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Name));
        h.Name = tempOut_Name.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("phone", out c);
        Out<String> tempOut_Phone = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Phone));
        h.Phone = tempOut_Phone.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        assert c.Type.Immutable;
        root.get().Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().ReadScope(row, root, out addressScope));
        assert addressScope.Immutable;
        Reference<RowCursor> tempReference_addressScope =
            new Reference<RowCursor>(addressScope);
        h.Address = CustomerExampleUnitTests.ReadAddress(row, tempReference_addressScope);
        addressScope = tempReference_addressScope.get();
        Reference<RowCursor> tempReference_addressScope2 =
            new Reference<RowCursor>(addressScope);
        RowCursors.skip(root.get().clone(), row,
            tempReference_addressScope2);
        addressScope = tempReference_addressScope2.get();
        return h;
    }

    private static PostalCode ReadPostalCode(Reference<RowBuffer> row,
                                             Reference<RowCursor> postalCodeScope) {
        Layout postalCodeLayout = postalCodeScope.get().getLayout();
        PostalCode pc = new PostalCode();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("zip", out c);
        Out<Integer> tempOut_Zip = new Out<Integer>();
        ResultAssert.IsSuccess(c.<LayoutInt32>TypeAs().ReadFixed(row, postalCodeScope, c, tempOut_Zip));
        pc.Zip = tempOut_Zip.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("plus4", out c);
        postalCodeScope.get().Find(row, c.Path);
        short plus4;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        if (c.<LayoutInt16>TypeAs().ReadSparse(row, postalCodeScope, out plus4) == Result.SUCCESS) {
            pc.Plus4 = plus4;
        }

        return pc;
    }

    private void WriteAddress(Reference<RowBuffer> row, Reference<RowCursor> addressScope,
                              TypeArgumentList typeArgs, Address a) {
        Layout addressLayout = this.customerResolver.Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("street", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, addressScope, c, a.Street));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("city", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, addressScope, c, a.City));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("state", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteFixed(row, addressScope, c, a.State));

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("postal_code", out c);
        addressScope.get().Find(row, c.Path);
        RowCursor postalCodeScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().WriteScope(row, addressScope, c.TypeArgs, out postalCodeScope));
        Reference<RowCursor> tempReference_postalCodeScope =
            new Reference<RowCursor>(postalCodeScope);
        this.WritePostalCode(row, tempReference_postalCodeScope, c.TypeArgs, a.PostalCode);
        postalCodeScope = tempReference_postalCodeScope.get();
        Reference<RowCursor> tempReference_postalCodeScope2 =
            new Reference<RowCursor>(postalCodeScope);
        RowCursors.skip(addressScope.get().clone(), row,
            tempReference_postalCodeScope2);
        postalCodeScope = tempReference_postalCodeScope2.get();
    }

    private void WriteGuest(Reference<RowBuffer> row, Reference<RowCursor> root, Guest g) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("guest_id", out c);
        ResultAssert.IsSuccess(c.<LayoutGuid>TypeAs().WriteFixed(row, root, c, g.Id));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("first_name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.FirstName));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("last_name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.LastName));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("title", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.Title));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("confirm_number", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.ConfirmNumber));

        if (g.Emails != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("emails", out c);
            root.get().Find(row, c.Path);
            RowCursor emailScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, root, c.TypeArgs, out emailScope));
            for (String email : g.Emails) {
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email));
                assert !emailScope.MoveNext(row);
            }

            Reference<RowCursor> tempReference_emailScope =
                new Reference<RowCursor>(emailScope);
            RowCursors.skip(root.get().clone(), row,
                tempReference_emailScope);
            emailScope = tempReference_emailScope.get();
        }

        if (g.PhoneNumbers != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("phone_numbers", out c);
            root.get().Find(row, c.Path);
            RowCursor phoneNumbersScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, root, c.TypeArgs,
                out phoneNumbersScope));
            for (String phone : g.PhoneNumbers) {
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref phoneNumbersScope
                    , phone));
                assert !phoneNumbersScope.MoveNext(row);
            }

            Reference<RowCursor> tempReference_phoneNumbersScope =
                new Reference<RowCursor>(phoneNumbersScope);
            RowCursors.skip(root.get().clone(), row,
                tempReference_phoneNumbersScope);
            phoneNumbersScope = tempReference_phoneNumbersScope.get();
        }

        if (g.Addresses != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("addresses", out c);
            root.get().Find(row, c.Path);
            RowCursor addressesScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, root, c.TypeArgs, out addressesScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref addressesScope);
            TypeArgument t0 = tupleType.getTypeArgs().get(0).clone();
            TypeArgument t1 = tupleType.getTypeArgs().get(1).clone();
            for (Map.Entry<String, Address> pair : g.Addresses.entrySet()) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().WriteScope(row, ref tempCursor,
                    c.TypeArgs, out tupleScope));

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(t0.<LayoutUtf8>TypeAs().WriteSparse(row, ref tupleScope, pair.getKey()));
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope =
                    new Reference<RowCursor>(tupleScope);
                RowCursor addressScope;
                Out<RowCursor> tempOut_addressScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(t1.<LayoutUDT>TypeAs().WriteScope(row, tempReference_tupleScope,
                    t1.getTypeArgs().clone(), tempOut_addressScope));
                addressScope = tempOut_addressScope.get();
                tupleScope = tempReference_tupleScope.get();
                Reference<RowCursor> tempReference_addressScope =
                    new Reference<RowCursor>(addressScope);
                this.WriteAddress(row, tempReference_addressScope, t1.getTypeArgs().clone(), pair.getValue());
                addressScope = tempReference_addressScope.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !tupleScope.MoveNext(row, ref addressScope);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, ref addressesScope,
                    ref tempCursor));
            }

            Reference<RowCursor> tempReference_addressesScope =
                new Reference<RowCursor>(addressesScope);
            RowCursors.skip(root.get().clone(), row,
                tempReference_addressesScope);
            addressesScope = tempReference_addressesScope.get();
        }
    }

    private void WriteHotel(Reference<RowBuffer> row, Reference<RowCursor> root, Hotel h) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("hotel_id", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, h.Id));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, h.Name));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("phone", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, h.Phone));

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        root.get().Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().WriteScope(row, root, c.TypeArgs, out addressScope));
        Reference<RowCursor> tempReference_addressScope =
            new Reference<RowCursor>(addressScope);
        this.WriteAddress(row, tempReference_addressScope, c.TypeArgs, h.Address);
        addressScope = tempReference_addressScope.get();
        Reference<RowCursor> tempReference_addressScope2 =
            new Reference<RowCursor>(addressScope);
        RowCursors.skip(root.get().clone(), row,
            tempReference_addressScope2);
        addressScope = tempReference_addressScope2.get();
    }

    private void WritePostalCode(Reference<RowBuffer> row, Reference<RowCursor> postalCodeScope,
                                 TypeArgumentList typeArgs, PostalCode pc) {
        Layout postalCodeLayout = this.customerResolver.Resolve(typeArgs.getSchemaId().clone());
        assert postalCodeLayout != null;
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("zip", out c);
        ResultAssert.IsSuccess(c.<LayoutInt32>TypeAs().WriteFixed(row, postalCodeScope, c, pc.Zip));
        if (pc.Plus4.HasValue) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert postalCodeLayout.TryFind("plus4", out c);
            postalCodeScope.get().Find(row, c.Path);
            ResultAssert.IsSuccess(c.<LayoutInt16>TypeAs().WriteSparse(row, postalCodeScope, pc.Plus4.Value));
        }
    }
}