//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursorExtensions;

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
        row.InitLayout(HybridRowVersion.V1, this.guestLayout, this.customerResolver);

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

        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc1 =
            new RefObject<RowCursor>(rc1);
        this.WriteGuest(tempRef_row2, tempRef_rc1, g1);
        rc1 = tempRef_rc1.get();
        row = tempRef_row2.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RowCursor rc2 = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.get();
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc2 =
            new RefObject<RowCursor>(rc2);
        Guest g2 = this.ReadGuest(tempRef_row4, tempRef_rc2);
        rc2 = tempRef_rc2.get();
        row = tempRef_row4.get();
        assert g1 == g2;

        // Append an item to an existing list.
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        RowCursor rc3 = RowCursor.Create(tempRef_row5);
        row = tempRef_row5.get();
        RefObject<RowBuffer> tempRef_row6 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc3 =
            new RefObject<RowCursor>(rc3);
        int index = this.AppendGuestEmail(tempRef_row6, tempRef_rc3, "vice_president@whitehouse.gov");
        rc3 = tempRef_rc3.get();
        row = tempRef_row6.get();
        assert 1 == index;
        g1.Emails.add("vice_president@whitehouse.gov");
        RefObject<RowBuffer> tempRef_row7 =
            new RefObject<RowBuffer>(row);
        RowCursor rc4 = RowCursor.Create(tempRef_row7);
        row = tempRef_row7.get();
        RefObject<RowBuffer> tempRef_row8 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc4 =
            new RefObject<RowCursor>(rc4);
        g2 = this.ReadGuest(tempRef_row8, tempRef_rc4);
        rc4 = tempRef_rc4.get();
        row = tempRef_row8.get();
        assert g1 == g2;

        // Prepend an item to an existing list.
        RefObject<RowBuffer> tempRef_row9 =
            new RefObject<RowBuffer>(row);
        RowCursor rc5 = RowCursor.Create(tempRef_row9);
        row = tempRef_row9.get();
        RefObject<RowBuffer> tempRef_row10 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc5 =
            new RefObject<RowCursor>(rc5);
        index = this.PrependGuestEmail(tempRef_row10, tempRef_rc5, "ex_president@whitehouse.gov");
        rc5 = tempRef_rc5.get();
        row = tempRef_row10.get();
        assert 0 == index;
        g1.Emails = new TreeSet<String> {
            "ex_president@whitehouse.gov", "president@whitehouse.gov", "vice_president@whitehouse.gov"
        }
        RefObject<RowBuffer> tempRef_row11 =
            new RefObject<RowBuffer>(row);
        RowCursor rc6 = RowCursor.Create(tempRef_row11);
        row = tempRef_row11.get();
        RefObject<RowBuffer> tempRef_row12 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc6 =
            new RefObject<RowCursor>(rc6);
        g2 = this.ReadGuest(tempRef_row12, tempRef_rc6);
        rc6 = tempRef_rc6.get();
        row = tempRef_row12.get();
        assert g1 == g2;

        // InsertAt an item to an existing list.
        RefObject<RowBuffer> tempRef_row13 =
            new RefObject<RowBuffer>(row);
        RowCursor rc7 = RowCursor.Create(tempRef_row13);
        row = tempRef_row13.get();
        RefObject<RowBuffer> tempRef_row14 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc7 =
            new RefObject<RowCursor>(rc7);
        index = this.InsertAtGuestEmail(tempRef_row14, tempRef_rc7, 1, "future_president@whitehouse.gov");
        rc7 = tempRef_rc7.get();
        row = tempRef_row14.get();
        assert 1 == index;
        g1.Emails = new TreeSet<String> {
            "ex_president@whitehouse.gov", "future_president@whitehouse.gov", "president@whitehouse.gov",
                "vice_president@whitehouse.gov"
        }

        RefObject<RowBuffer> tempRef_row15 =
            new RefObject<RowBuffer>(row);
        RowCursor rc8 = RowCursor.Create(tempRef_row15);
        row = tempRef_row15.get();
        RefObject<RowBuffer> tempRef_row16 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc8 =
            new RefObject<RowCursor>(rc8);
        g2 = this.ReadGuest(tempRef_row16, tempRef_rc8);
        rc8 = tempRef_rc8.get();
        row = tempRef_row16.get();
        assert g1 == g2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateHotel()
    public void CreateHotel() {
        RowBuffer row = new RowBuffer(0);
        row.InitLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

        Hotel h1 = this.hotelExample;
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_root =
            new RefObject<RowCursor>(root);
        this.WriteHotel(tempRef_row2, tempRef_root, h1);
        root = tempRef_root.get();
        row = tempRef_row2.get();

        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.get();
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_root2 =
            new RefObject<RowCursor>(root);
        Hotel h2 = this.ReadHotel(tempRef_row4, tempRef_root2);
        root = tempRef_root2.get();
        row = tempRef_row4.get();

        assert h1 == h2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FrozenHotel()
    public void FrozenHotel() {
        RowBuffer row = new RowBuffer(0);
        row.InitLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

        Hotel h1 = this.hotelExample;
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_root =
            new RefObject<RowCursor>(root);
        this.WriteHotel(tempRef_row2, tempRef_root, h1);
        root = tempRef_root.get();
        row = tempRef_row2.get();

        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.get();
        Address tempVar = new Address();
        tempVar.setStreet("300B Brownie Way");
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_root2 =
            new RefObject<RowCursor>(root);
        ResultAssert.InsufficientPermissions(this.PartialUpdateHotelAddress(tempRef_row4, tempRef_root2, tempVar));
        root = tempRef_root2.get();
        row = tempRef_row4.get();
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

    private int AppendGuestEmail(RefObject<RowBuffer> row, RefObject<RowCursor> root, String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.get().Find(row, c.Path);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().ReadScope(row, root, out emailScope));
        assert !emailScope.MoveTo(row, Integer.MAX_VALUE);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email));
        return emailScope.Index;
    }

    private int InsertAtGuestEmail(RefObject<RowBuffer> row, RefObject<RowCursor> root, int i,
                                   String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.get().Find(row, c.Path);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().ReadScope(row, root, out emailScope));
        assert emailScope.MoveTo(row, i);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email,
            UpdateOptions.InsertAt));
        return emailScope.Index;
    }

    private Result PartialUpdateHotelAddress(RefObject<RowBuffer> row, RefObject<RowCursor> root,
                                             Address a) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        root.get().Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = c.<LayoutUDT>TypeAs().ReadScope(row, root, out addressScope);
        if (r != Result.Success) {
            return r;
        }

        Layout addressLayout = addressScope.Layout;
        if (a.Street != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("street", out c);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUtf8>TypeAs().WriteVariable(row, ref addressScope, c, a.Street);
            if (r != Result.Success) {
                return r;
            }
        }

        if (a.City != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("city", out c);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUtf8>TypeAs().WriteVariable(row, ref addressScope, c, a.City);
            if (r != Result.Success) {
                return r;
            }
        }

        if (a.State != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("state", out c);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUtf8>TypeAs().WriteFixed(row, ref addressScope, c, a.State);
            if (r != Result.Success) {
                return r;
            }
        }

        if (a.PostalCode != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert addressLayout.TryFind("postal_code", out c);
            addressScope.Find(row, c.Path);
            RowCursor postalCodeScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            r = c.<LayoutUDT>TypeAs().WriteScope(row, ref addressScope, c.TypeArgs, out postalCodeScope);
            if (r != Result.Success) {
                return r;
            }

            RefObject<RowCursor> tempRef_postalCodeScope =
                new RefObject<RowCursor>(postalCodeScope);
            this.WritePostalCode(row, tempRef_postalCodeScope, c.TypeArgs, a.PostalCode);
            postalCodeScope = tempRef_postalCodeScope.get();
        }

        return Result.Success;
    }

    private int PrependGuestEmail(RefObject<RowBuffer> row, RefObject<RowCursor> root, String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.get().Find(row, c.Path);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().ReadScope(row, root, out emailScope));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email,
            UpdateOptions.InsertAt));
        return emailScope.Index;
    }

    private static Address ReadAddress(RefObject<RowBuffer> row, RefObject<RowCursor> addressScope) {
        Address a = new Address();
        Layout addressLayout = addressScope.get().getLayout();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("street", out c);
        OutObject<String> tempOut_Street = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, addressScope, c, tempOut_Street));
        a.Street = tempOut_Street.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("city", out c);
        OutObject<String> tempOut_City = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, addressScope, c, tempOut_City));
        a.City = tempOut_City.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("state", out c);
        OutObject<String> tempOut_State = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadFixed(row, addressScope, c, tempOut_State));
        a.State = tempOut_State.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("postal_code", out c);
        addressScope.get().Find(row, c.Path);
        RowCursor postalCodeScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().ReadScope(row, addressScope, out postalCodeScope));
        RefObject<RowCursor> tempRef_postalCodeScope =
            new RefObject<RowCursor>(postalCodeScope);
        a.PostalCode = CustomerExampleUnitTests.ReadPostalCode(row, tempRef_postalCodeScope);
        postalCodeScope = tempRef_postalCodeScope.get();
        RefObject<RowCursor> tempRef_postalCodeScope2 =
            new RefObject<RowCursor>(postalCodeScope);
        RowCursorExtensions.Skip(addressScope.get().clone(), row,
            tempRef_postalCodeScope2);
        postalCodeScope = tempRef_postalCodeScope2.get();
        return a;
    }

    private Guest ReadGuest(RefObject<RowBuffer> row, RefObject<RowCursor> root) {
        Guest g = new Guest();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("guest_id", out c);
        OutObject<UUID> tempOut_Id = new OutObject<UUID>();
        ResultAssert.IsSuccess(c.<LayoutGuid>TypeAs().ReadFixed(row, root, c, tempOut_Id));
        g.Id = tempOut_Id.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("first_name", out c);
        OutObject<String> tempOut_FirstName = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_FirstName));
        g.FirstName = tempOut_FirstName.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("last_name", out c);
        OutObject<String> tempOut_LastName = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_LastName));
        g.LastName = tempOut_LastName.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("title", out c);
        OutObject<String> tempOut_Title = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Title));
        g.Title = tempOut_Title.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("confirm_number", out c);
        OutObject<String> tempOut_ConfirmNumber = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_ConfirmNumber));
        g.ConfirmNumber = tempOut_ConfirmNumber.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out emailScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref emailScope, out emailScope) == Result.Success) {
            g.Emails = new TreeSet<String>();
            while (emailScope.MoveNext(row)) {
                String item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref emailScope,
                    out item));
                g.Emails.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("phone_numbers", out c);
        RowCursor phoneNumbersScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out phoneNumbersScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref phoneNumbersScope, out phoneNumbersScope) == Result.Success) {
            g.PhoneNumbers = new ArrayList<String>();
            while (phoneNumbersScope.MoveNext(row)) {
                String item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref phoneNumbersScope,
                    out item));
                g.PhoneNumbers.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("addresses", out c);
        RowCursor addressesScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out addressesScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedMap>TypeAs().ReadScope(row, ref addressesScope, out addressesScope) == Result.Success) {
            RefObject<RowCursor> tempRef_addressesScope = new RefObject<RowCursor>(addressesScope);
            TypeArgument tupleType = LayoutType.TypedMap.FieldType(tempRef_addressesScope).clone();
            addressesScope = tempRef_addressesScope.get();
            TypeArgument t0 = tupleType.getTypeArgs().get(0).clone();
            TypeArgument t1 = tupleType.getTypeArgs().get(1).clone();
            g.Addresses = new HashMap<String, Address>();
            RowCursor pairScope = null;
            RefObject<RowCursor> tempRef_pairScope = new RefObject<RowCursor>(pairScope);
            while (addressesScope.MoveNext(row, tempRef_pairScope)) {
                pairScope = tempRef_pairScope.get();
                RefObject<RowCursor> tempRef_addressesScope2 = new RefObject<RowCursor>(addressesScope);
                OutObject<RowCursor> tempOut_pairScope = new OutObject<RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row, tempRef_addressesScope2, tempOut_pairScope));
                pairScope = tempOut_pairScope.get();
                addressesScope = tempRef_addressesScope2.get();
                assert RowCursorExtensions.MoveNext(pairScope.clone(), row);
                RefObject<RowCursor> tempRef_pairScope2 = new RefObject<RowCursor>(pairScope);
                String key;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
                ResultAssert.IsSuccess(t0.<LayoutUtf8>TypeAs().ReadSparse(row, tempRef_pairScope2, out key));
                pairScope = tempRef_pairScope2.get();
                assert RowCursorExtensions.MoveNext(pairScope.clone(), row);
                RefObject<RowCursor> tempRef_pairScope3 = new RefObject<RowCursor>(pairScope);
                RowCursor addressScope;
                OutObject<RowCursor> tempOut_addressScope = new OutObject<RowCursor>();
                ResultAssert.IsSuccess(t1.<LayoutUDT>TypeAs().ReadScope(row, tempRef_pairScope3, tempOut_addressScope));
                addressScope = tempOut_addressScope.get();
                pairScope = tempRef_pairScope3.get();
                RefObject<RowCursor> tempRef_addressScope = new RefObject<RowCursor>(addressScope);
                Address value = CustomerExampleUnitTests.ReadAddress(row, tempRef_addressScope);
                addressScope = tempRef_addressScope.get();
                g.Addresses.put(key, value);
                RefObject<RowCursor> tempRef_addressScope2 = new RefObject<RowCursor>(addressScope);
                assert !RowCursorExtensions.MoveNext(pairScope.clone(), row, tempRef_addressScope2);
                addressScope = tempRef_addressScope2.get();
            }
            pairScope = tempRef_pairScope.get();
        }

        return g;
    }

    private Hotel ReadHotel(RefObject<RowBuffer> row, RefObject<RowCursor> root) {
        Hotel h = new Hotel();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("hotel_id", out c);
        OutObject<String> tempOut_Id = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Id));
        h.Id = tempOut_Id.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("name", out c);
        OutObject<String> tempOut_Name = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Name));
        h.Name = tempOut_Name.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("phone", out c);
        OutObject<String> tempOut_Phone = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Phone));
        h.Phone = tempOut_Phone.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        assert c.Type.Immutable;
        root.get().Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().ReadScope(row, root, out addressScope));
        assert addressScope.Immutable;
        RefObject<RowCursor> tempRef_addressScope =
            new RefObject<RowCursor>(addressScope);
        h.Address = CustomerExampleUnitTests.ReadAddress(row, tempRef_addressScope);
        addressScope = tempRef_addressScope.get();
        RefObject<RowCursor> tempRef_addressScope2 =
            new RefObject<RowCursor>(addressScope);
        RowCursorExtensions.Skip(root.get().clone(), row,
            tempRef_addressScope2);
        addressScope = tempRef_addressScope2.get();
        return h;
    }

    private static PostalCode ReadPostalCode(RefObject<RowBuffer> row,
                                             RefObject<RowCursor> postalCodeScope) {
        Layout postalCodeLayout = postalCodeScope.get().getLayout();
        PostalCode pc = new PostalCode();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("zip", out c);
        OutObject<Integer> tempOut_Zip = new OutObject<Integer>();
        ResultAssert.IsSuccess(c.<LayoutInt32>TypeAs().ReadFixed(row, postalCodeScope, c, tempOut_Zip));
        pc.Zip = tempOut_Zip.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("plus4", out c);
        postalCodeScope.get().Find(row, c.Path);
        short plus4;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (c.<LayoutInt16>TypeAs().ReadSparse(row, postalCodeScope, out plus4) == Result.Success) {
            pc.Plus4 = plus4;
        }

        return pc;
    }

    private void WriteAddress(RefObject<RowBuffer> row, RefObject<RowCursor> addressScope,
                              TypeArgumentList typeArgs, Address a) {
        Layout addressLayout = this.customerResolver.Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("street", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, addressScope, c, a.Street));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("city", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, addressScope, c, a.City));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("state", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteFixed(row, addressScope, c, a.State));

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("postal_code", out c);
        addressScope.get().Find(row, c.Path);
        RowCursor postalCodeScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().WriteScope(row, addressScope, c.TypeArgs, out postalCodeScope));
        RefObject<RowCursor> tempRef_postalCodeScope =
            new RefObject<RowCursor>(postalCodeScope);
        this.WritePostalCode(row, tempRef_postalCodeScope, c.TypeArgs, a.PostalCode);
        postalCodeScope = tempRef_postalCodeScope.get();
        RefObject<RowCursor> tempRef_postalCodeScope2 =
            new RefObject<RowCursor>(postalCodeScope);
        RowCursorExtensions.Skip(addressScope.get().clone(), row,
            tempRef_postalCodeScope2);
        postalCodeScope = tempRef_postalCodeScope2.get();
    }

    private void WriteGuest(RefObject<RowBuffer> row, RefObject<RowCursor> root, Guest g) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("guest_id", out c);
        ResultAssert.IsSuccess(c.<LayoutGuid>TypeAs().WriteFixed(row, root, c, g.Id));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("first_name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.FirstName));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("last_name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.LastName));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("title", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.Title));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("confirm_number", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, g.ConfirmNumber));

        if (g.Emails != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("emails", out c);
            root.get().Find(row, c.Path);
            RowCursor emailScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, root, c.TypeArgs, out emailScope));
            for (String email : g.Emails) {
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref emailScope, email));
                assert !emailScope.MoveNext(row);
            }

            RefObject<RowCursor> tempRef_emailScope =
                new RefObject<RowCursor>(emailScope);
            RowCursorExtensions.Skip(root.get().clone(), row,
                tempRef_emailScope);
            emailScope = tempRef_emailScope.get();
        }

        if (g.PhoneNumbers != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("phone_numbers", out c);
            root.get().Find(row, c.Path);
            RowCursor phoneNumbersScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, root, c.TypeArgs,
                out phoneNumbersScope));
            for (String phone : g.PhoneNumbers) {
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref phoneNumbersScope
                    , phone));
                assert !phoneNumbersScope.MoveNext(row);
            }

            RefObject<RowCursor> tempRef_phoneNumbersScope =
                new RefObject<RowCursor>(phoneNumbersScope);
            RowCursorExtensions.Skip(root.get().clone(), row,
                tempRef_phoneNumbersScope);
            phoneNumbersScope = tempRef_phoneNumbersScope.get();
        }

        if (g.Addresses != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("addresses", out c);
            root.get().Find(row, c.Path);
            RowCursor addressesScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, root, c.TypeArgs, out addressesScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref addressesScope);
            TypeArgument t0 = tupleType.getTypeArgs().get(0).clone();
            TypeArgument t1 = tupleType.getTypeArgs().get(1).clone();
            for (Map.Entry<String, Address> pair : g.Addresses.entrySet()) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().WriteScope(row, ref tempCursor,
                    c.TypeArgs, out tupleScope));

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(t0.<LayoutUtf8>TypeAs().WriteSparse(row, ref tupleScope, pair.getKey()));
                assert tupleScope.MoveNext(row);
                RefObject<RowCursor> tempRef_tupleScope =
                    new RefObject<RowCursor>(tupleScope);
                RowCursor addressScope;
                OutObject<RowCursor> tempOut_addressScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(t1.<LayoutUDT>TypeAs().WriteScope(row, tempRef_tupleScope,
                    t1.getTypeArgs().clone(), tempOut_addressScope));
                addressScope = tempOut_addressScope.get();
                tupleScope = tempRef_tupleScope.get();
                RefObject<RowCursor> tempRef_addressScope =
                    new RefObject<RowCursor>(addressScope);
                this.WriteAddress(row, tempRef_addressScope, t1.getTypeArgs().clone(), pair.getValue());
                addressScope = tempRef_addressScope.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !tupleScope.MoveNext(row, ref addressScope);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, ref addressesScope,
                    ref tempCursor));
            }

            RefObject<RowCursor> tempRef_addressesScope =
                new RefObject<RowCursor>(addressesScope);
            RowCursorExtensions.Skip(root.get().clone(), row,
                tempRef_addressesScope);
            addressesScope = tempRef_addressesScope.get();
        }
    }

    private void WriteHotel(RefObject<RowBuffer> row, RefObject<RowCursor> root, Hotel h) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("hotel_id", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, h.Id));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, h.Name));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("phone", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, h.Phone));

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        root.get().Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().WriteScope(row, root, c.TypeArgs, out addressScope));
        RefObject<RowCursor> tempRef_addressScope =
            new RefObject<RowCursor>(addressScope);
        this.WriteAddress(row, tempRef_addressScope, c.TypeArgs, h.Address);
        addressScope = tempRef_addressScope.get();
        RefObject<RowCursor> tempRef_addressScope2 =
            new RefObject<RowCursor>(addressScope);
        RowCursorExtensions.Skip(root.get().clone(), row,
            tempRef_addressScope2);
        addressScope = tempRef_addressScope2.get();
    }

    private void WritePostalCode(RefObject<RowBuffer> row, RefObject<RowCursor> postalCodeScope,
                                 TypeArgumentList typeArgs, PostalCode pc) {
        Layout postalCodeLayout = this.customerResolver.Resolve(typeArgs.getSchemaId().clone());
        assert postalCodeLayout != null;
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("zip", out c);
        ResultAssert.IsSuccess(c.<LayoutInt32>TypeAs().WriteFixed(row, postalCodeScope, c, pc.Zip));
        if (pc.Plus4.HasValue) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert postalCodeLayout.TryFind("plus4", out c);
            postalCodeScope.get().Find(row, c.Path);
            ResultAssert.IsSuccess(c.<LayoutInt16>TypeAs().WriteSparse(row, postalCodeScope, pc.Plus4.Value));
        }
    }
}