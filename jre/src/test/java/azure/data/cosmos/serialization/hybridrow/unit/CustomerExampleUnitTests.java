//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

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

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc1 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc1);
        this.WriteGuest(tempRef_row2, tempRef_rc1, g1);
        rc1 = tempRef_rc1.argValue;
        row = tempRef_row2.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc2 = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc2);
        Guest g2 = this.ReadGuest(tempRef_row4, tempRef_rc2);
        rc2 = tempRef_rc2.argValue;
        row = tempRef_row4.argValue;
        assert g1 == g2;

        // Append an item to an existing list.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc3 = RowCursor.Create(tempRef_row5);
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc3);
        int index = this.AppendGuestEmail(tempRef_row6, tempRef_rc3, "vice_president@whitehouse.gov");
        rc3 = tempRef_rc3.argValue;
        row = tempRef_row6.argValue;
        assert 1 == index;
        g1.Emails.add("vice_president@whitehouse.gov");
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc4 = RowCursor.Create(tempRef_row7);
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc4);
        g2 = this.ReadGuest(tempRef_row8, tempRef_rc4);
        rc4 = tempRef_rc4.argValue;
        row = tempRef_row8.argValue;
        assert g1 == g2;

        // Prepend an item to an existing list.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc5 = RowCursor.Create(tempRef_row9);
        row = tempRef_row9.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc5);
        index = this.PrependGuestEmail(tempRef_row10, tempRef_rc5, "ex_president@whitehouse.gov");
        rc5 = tempRef_rc5.argValue;
        row = tempRef_row10.argValue;
        assert 0 == index;
        g1.Emails = new TreeSet<String> {
            "ex_president@whitehouse.gov", "president@whitehouse.gov", "vice_president@whitehouse.gov"
        }
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc6 = RowCursor.Create(tempRef_row11);
        row = tempRef_row11.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc6);
        g2 = this.ReadGuest(tempRef_row12, tempRef_rc6);
        rc6 = tempRef_rc6.argValue;
        row = tempRef_row12.argValue;
        assert g1 == g2;

        // InsertAt an item to an existing list.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc7 = RowCursor.Create(tempRef_row13);
        row = tempRef_row13.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc7);
        index = this.InsertAtGuestEmail(tempRef_row14, tempRef_rc7, 1, "future_president@whitehouse.gov");
        rc7 = tempRef_rc7.argValue;
        row = tempRef_row14.argValue;
        assert 1 == index;
        g1.Emails = new TreeSet<String> {
            "ex_president@whitehouse.gov", "future_president@whitehouse.gov", "president@whitehouse.gov",
                "vice_president@whitehouse.gov"
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc8 = RowCursor.Create(tempRef_row15);
        row = tempRef_row15.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc8);
        g2 = this.ReadGuest(tempRef_row16, tempRef_rc8);
        rc8 = tempRef_rc8.argValue;
        row = tempRef_row16.argValue;
        assert g1 == g2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateHotel()
    public void CreateHotel() {
        RowBuffer row = new RowBuffer(0);
        row.InitLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

        Hotel h1 = this.hotelExample;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        this.WriteHotel(tempRef_row2, tempRef_root, h1);
        root = tempRef_root.argValue;
        row = tempRef_row2.argValue;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        Hotel h2 = this.ReadHotel(tempRef_row4, tempRef_root2);
        root = tempRef_root2.argValue;
        row = tempRef_row4.argValue;

        assert h1 == h2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FrozenHotel()
    public void FrozenHotel() {
        RowBuffer row = new RowBuffer(0);
        row.InitLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

        Hotel h1 = this.hotelExample;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        this.WriteHotel(tempRef_row2, tempRef_root, h1);
        root = tempRef_root.argValue;
        row = tempRef_row2.argValue;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.argValue;
        Address tempVar = new Address();
        tempVar.setStreet("300B Brownie Way");
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_root2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
        ResultAssert.InsufficientPermissions(this.PartialUpdateHotelAddress(tempRef_row4, tempRef_root2, tempVar));
        root = tempRef_root2.argValue;
        row = tempRef_row4.argValue;
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

    private int AppendGuestEmail(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.argValue.Find(row, c.Path);
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

    private int InsertAtGuestEmail(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, int i,
                                   String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.argValue.Find(row, c.Path);
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

    private Result PartialUpdateHotelAddress(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                             Address a) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        root.argValue.Find(row, c.Path);
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

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_postalCodeScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(postalCodeScope);
            this.WritePostalCode(row, tempRef_postalCodeScope, c.TypeArgs, a.PostalCode);
            postalCodeScope = tempRef_postalCodeScope.argValue;
        }

        return Result.Success;
    }

    private int PrependGuestEmail(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, String email) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        root.argValue.Find(row, c.Path);
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

    private static Address ReadAddress(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> addressScope) {
        Address a = new Address();
        Layout addressLayout = addressScope.argValue.getLayout();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("street", out c);
        tangible.OutObject<String> tempOut_Street = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, addressScope, c, tempOut_Street));
        a.Street = tempOut_Street.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("city", out c);
        tangible.OutObject<String> tempOut_City = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, addressScope, c, tempOut_City));
        a.City = tempOut_City.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("state", out c);
        tangible.OutObject<String> tempOut_State = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadFixed(row, addressScope, c, tempOut_State));
        a.State = tempOut_State.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert addressLayout.TryFind("postal_code", out c);
        addressScope.argValue.Find(row, c.Path);
        RowCursor postalCodeScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().ReadScope(row, addressScope, out postalCodeScope));
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_postalCodeScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(postalCodeScope);
        a.PostalCode = CustomerExampleUnitTests.ReadPostalCode(row, tempRef_postalCodeScope);
        postalCodeScope = tempRef_postalCodeScope.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_postalCodeScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(postalCodeScope);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(addressScope.argValue.clone(), row,
            tempRef_postalCodeScope2);
        postalCodeScope = tempRef_postalCodeScope2.argValue;
        return a;
    }

    private Guest ReadGuest(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        Guest g = new Guest();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("guest_id", out c);
        tangible.OutObject<UUID> tempOut_Id = new tangible.OutObject<UUID>();
        ResultAssert.IsSuccess(c.<LayoutGuid>TypeAs().ReadFixed(row, root, c, tempOut_Id));
        g.Id = tempOut_Id.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("first_name", out c);
        tangible.OutObject<String> tempOut_FirstName = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_FirstName));
        g.FirstName = tempOut_FirstName.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("last_name", out c);
        tangible.OutObject<String> tempOut_LastName = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_LastName));
        g.LastName = tempOut_LastName.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("title", out c);
        tangible.OutObject<String> tempOut_Title = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Title));
        g.Title = tempOut_Title.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("confirm_number", out c);
        tangible.OutObject<String> tempOut_ConfirmNumber = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_ConfirmNumber));
        g.ConfirmNumber = tempOut_ConfirmNumber.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.guestLayout.TryFind("emails", out c);
        RowCursor emailScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out emailScope).Find(row, c.Path);
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
        root.argValue.Clone(out phoneNumbersScope).Find(row, c.Path);
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
        root.argValue.Clone(out addressesScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedMap>TypeAs().ReadScope(row, ref addressesScope, out addressesScope) == Result.Success) {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressesScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressesScope);
            TypeArgument tupleType = LayoutType.TypedMap.FieldType(tempRef_addressesScope).clone();
            addressesScope = tempRef_addressesScope.argValue;
            TypeArgument t0 = tupleType.getTypeArgs().get(0).clone();
            TypeArgument t1 = tupleType.getTypeArgs().get(1).clone();
            g.Addresses = new HashMap<String, Address>();
            RowCursor pairScope = null;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_pairScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(pairScope);
            while (addressesScope.MoveNext(row, tempRef_pairScope)) {
                pairScope = tempRef_pairScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressesScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressesScope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_pairScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row, tempRef_addressesScope2, tempOut_pairScope));
                pairScope = tempOut_pairScope.argValue;
                addressesScope = tempRef_addressesScope2.argValue;
                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(pairScope.clone(), row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_pairScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(pairScope);
                String key;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
                ResultAssert.IsSuccess(t0.<LayoutUtf8>TypeAs().ReadSparse(row, tempRef_pairScope2, out key));
                pairScope = tempRef_pairScope2.argValue;
                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(pairScope.clone(), row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_pairScope3 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(pairScope);
                RowCursor addressScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_addressScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(t1.<LayoutUDT>TypeAs().ReadScope(row, tempRef_pairScope3, tempOut_addressScope));
                addressScope = tempOut_addressScope.argValue;
                pairScope = tempRef_pairScope3.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
                Address value = CustomerExampleUnitTests.ReadAddress(row, tempRef_addressScope);
                addressScope = tempRef_addressScope.argValue;
                g.Addresses.put(key, value);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
                assert !Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(pairScope.clone(), row, tempRef_addressScope2);
                addressScope = tempRef_addressScope2.argValue;
            }
            pairScope = tempRef_pairScope.argValue;
        }

        return g;
    }

    private Hotel ReadHotel(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        Hotel h = new Hotel();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("hotel_id", out c);
        tangible.OutObject<String> tempOut_Id = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Id));
        h.Id = tempOut_Id.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("name", out c);
        tangible.OutObject<String> tempOut_Name = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Name));
        h.Name = tempOut_Name.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("phone", out c);
        tangible.OutObject<String> tempOut_Phone = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Phone));
        h.Phone = tempOut_Phone.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.hotelLayout.TryFind("address", out c);
        assert c.Type.Immutable;
        root.argValue.Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().ReadScope(row, root, out addressScope));
        assert addressScope.Immutable;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
        h.Address = CustomerExampleUnitTests.ReadAddress(row, tempRef_addressScope);
        addressScope = tempRef_addressScope.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
            tempRef_addressScope2);
        addressScope = tempRef_addressScope2.argValue;
        return h;
    }

    private static PostalCode ReadPostalCode(tangible.RefObject<RowBuffer> row,
                                             tangible.RefObject<RowCursor> postalCodeScope) {
        Layout postalCodeLayout = postalCodeScope.argValue.getLayout();
        PostalCode pc = new PostalCode();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("zip", out c);
        tangible.OutObject<Integer> tempOut_Zip = new tangible.OutObject<Integer>();
        ResultAssert.IsSuccess(c.<LayoutInt32>TypeAs().ReadFixed(row, postalCodeScope, c, tempOut_Zip));
        pc.Zip = tempOut_Zip.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert postalCodeLayout.TryFind("plus4", out c);
        postalCodeScope.argValue.Find(row, c.Path);
        short plus4;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (c.<LayoutInt16>TypeAs().ReadSparse(row, postalCodeScope, out plus4) == Result.Success) {
            pc.Plus4 = plus4;
        }

        return pc;
    }

    private void WriteAddress(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> addressScope,
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
        addressScope.argValue.Find(row, c.Path);
        RowCursor postalCodeScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().WriteScope(row, addressScope, c.TypeArgs, out postalCodeScope));
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_postalCodeScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(postalCodeScope);
        this.WritePostalCode(row, tempRef_postalCodeScope, c.TypeArgs, a.PostalCode);
        postalCodeScope = tempRef_postalCodeScope.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_postalCodeScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(postalCodeScope);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(addressScope.argValue.clone(), row,
            tempRef_postalCodeScope2);
        postalCodeScope = tempRef_postalCodeScope2.argValue;
    }

    private void WriteGuest(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, Guest g) {
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
            root.argValue.Find(row, c.Path);
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

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_emailScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(emailScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_emailScope);
            emailScope = tempRef_emailScope.argValue;
        }

        if (g.PhoneNumbers != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("phone_numbers", out c);
            root.argValue.Find(row, c.Path);
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

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_phoneNumbersScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(phoneNumbersScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_phoneNumbersScope);
            phoneNumbersScope = tempRef_phoneNumbersScope.argValue;
        }

        if (g.Addresses != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.guestLayout.TryFind("addresses", out c);
            root.argValue.Find(row, c.Path);
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
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
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
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor addressScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_addressScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(t1.<LayoutUDT>TypeAs().WriteScope(row, tempRef_tupleScope,
                    t1.getTypeArgs().clone(), tempOut_addressScope));
                addressScope = tempOut_addressScope.argValue;
                tupleScope = tempRef_tupleScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
                this.WriteAddress(row, tempRef_addressScope, t1.getTypeArgs().clone(), pair.getValue());
                addressScope = tempRef_addressScope.argValue;
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

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressesScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressesScope);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
                tempRef_addressesScope);
            addressesScope = tempRef_addressesScope.argValue;
        }
    }

    private void WriteHotel(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, Hotel h) {
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
        root.argValue.Find(row, c.Path);
        RowCursor addressScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUDT>TypeAs().WriteScope(row, root, c.TypeArgs, out addressScope));
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
        this.WriteAddress(row, tempRef_addressScope, c.TypeArgs, h.Address);
        addressScope = tempRef_addressScope.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_addressScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(addressScope);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(root.argValue.clone(), row,
            tempRef_addressScope2);
        addressScope = tempRef_addressScope2.argValue;
    }

    private void WritePostalCode(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> postalCodeScope,
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
            postalCodeScope.argValue.Find(row, c.Path);
            ResultAssert.IsSuccess(c.<LayoutInt16>TypeAs().WriteSparse(row, postalCodeScope, pc.Plus4.Value));
        }
    }
}