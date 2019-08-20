// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    // ReSharper disable once StringLiteralTypo
    [TestClass]
    [SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here are anonymous.")]
    [DeploymentItem(@"TestData\CustomerSchema.json", "TestData")]
    public sealed class CustomerExampleUnitTests
    {
        private readonly Hotel hotelExample = new Hotel()
        {
            Id = "The-Westin-St-John-Resort-Villas-1187",
            Name = "The Westin St. John Resort Villas",
            Phone = "+1 340-693-8000",
            Address = new Address
            {
                Street = "300B Chocolate Hole",
                City = "Great Cruz Bay",
                State = "VI",
                PostalCode = new PostalCode
                {
                    Zip = 00830,
                    Plus4 = 0001,
                },
            },
        };

        private Namespace customerSchema;
        private LayoutResolver customerResolver;
        private Layout hotelLayout;
        private Layout guestLayout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(@"TestData\CustomerSchema.json");
            this.customerSchema = Namespace.Parse(json);
            this.customerResolver = new LayoutResolverNamespace(this.customerSchema);
            this.hotelLayout = this.customerResolver.Resolve(this.customerSchema.Schemas.Find(x => x.Name == "Hotels").SchemaId);
            this.guestLayout = this.customerResolver.Resolve(this.customerSchema.Schemas.Find(x => x.Name == "Guests").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateHotel()
        {
            RowBuffer row = new RowBuffer(0);
            row.InitLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

            Hotel h1 = this.hotelExample;
            RowCursor root = RowCursor.Create(ref row);
            this.WriteHotel(ref row, ref root, h1);

            root = RowCursor.Create(ref row);
            Hotel h2 = this.ReadHotel(ref row, ref root);

            Assert.AreEqual(h1, h2);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void FrozenHotel()
        {
            RowBuffer row = new RowBuffer(0);
            row.InitLayout(HybridRowVersion.V1, this.hotelLayout, this.customerResolver);

            Hotel h1 = this.hotelExample;
            RowCursor root = RowCursor.Create(ref row);
            this.WriteHotel(ref row, ref root, h1);

            root = RowCursor.Create(ref row);
            ResultAssert.InsufficientPermissions(this.PartialUpdateHotelAddress(ref row, ref root, new Address { Street = "300B Brownie Way" }));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateGuest()
        {
            RowBuffer row = new RowBuffer(1024 * 1024);
            row.InitLayout(HybridRowVersion.V1, this.guestLayout, this.customerResolver);

            Guest g1 = new Guest()
            {
                Id = Guid.Parse("64d9d6d3-fd6b-4556-8c6e-d960a7ece7b9"),
                FirstName = "John",
                LastName = "Adams",
                Title = "President of the United States",
                PhoneNumbers = new List<string> { "(202) 456-1111" },
                ConfirmNumber = "(202) 456-1111",
                Emails = new SortedSet<string> { "president@whitehouse.gov" },
                Addresses = new Dictionary<string, Address>
                {
                    ["home"] = new Address
                    {
                        Street = "1600 Pennsylvania Avenue NW",
                        City = "Washington, D.C.",
                        State = "DC",
                        PostalCode = new PostalCode
                        {
                            Zip = 20500,
                            Plus4 = 0001,
                        },
                    },
                },
            };

            RowCursor rc1 = RowCursor.Create(ref row);
            this.WriteGuest(ref row, ref rc1, g1);
            RowCursor rc2 = RowCursor.Create(ref row);
            Guest g2 = this.ReadGuest(ref row, ref rc2);
            Assert.AreEqual(g1, g2);

            // Append an item to an existing list.
            RowCursor rc3 = RowCursor.Create(ref row);
            int index = this.AppendGuestEmail(ref row, ref rc3, "vice_president@whitehouse.gov");
            Assert.AreEqual(1, index);
            g1.Emails.Add("vice_president@whitehouse.gov");
            RowCursor rc4 = RowCursor.Create(ref row);
            g2 = this.ReadGuest(ref row, ref rc4);
            Assert.AreEqual(g1, g2);

            // Prepend an item to an existing list.
            RowCursor rc5 = RowCursor.Create(ref row);
            index = this.PrependGuestEmail(ref row, ref rc5, "ex_president@whitehouse.gov");
            Assert.AreEqual(0, index);
            g1.Emails = new SortedSet<string> { "ex_president@whitehouse.gov", "president@whitehouse.gov", "vice_president@whitehouse.gov" };
            RowCursor rc6 = RowCursor.Create(ref row);
            g2 = this.ReadGuest(ref row, ref rc6);
            Assert.AreEqual(g1, g2);

            // InsertAt an item to an existing list.
            RowCursor rc7 = RowCursor.Create(ref row);
            index = this.InsertAtGuestEmail(ref row, ref rc7, 1, "future_president@whitehouse.gov");
            Assert.AreEqual(1, index);
            g1.Emails = new SortedSet<string>
            {
                "ex_president@whitehouse.gov",
                "future_president@whitehouse.gov",
                "president@whitehouse.gov",
                "vice_president@whitehouse.gov",
            };

            RowCursor rc8 = RowCursor.Create(ref row);
            g2 = this.ReadGuest(ref row, ref rc8);
            Assert.AreEqual(g1, g2);
        }

        private static Address ReadAddress(ref RowBuffer row, ref RowCursor addressScope)
        {
            Address a = new Address();
            Layout addressLayout = addressScope.Layout;
            Assert.IsTrue(addressLayout.TryFind("street", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref addressScope, c, out a.Street));
            Assert.IsTrue(addressLayout.TryFind("city", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref addressScope, c, out a.City));
            Assert.IsTrue(addressLayout.TryFind("state", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadFixed(ref row, ref addressScope, c, out a.State));

            Assert.IsTrue(addressLayout.TryFind("postal_code", out c));
            addressScope.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUDT>().ReadScope(ref row, ref addressScope, out RowCursor postalCodeScope));
            a.PostalCode = CustomerExampleUnitTests.ReadPostalCode(ref row, ref postalCodeScope);
            addressScope.Skip(ref row, ref postalCodeScope);
            return a;
        }

        private static PostalCode ReadPostalCode(ref RowBuffer row, ref RowCursor postalCodeScope)
        {
            Layout postalCodeLayout = postalCodeScope.Layout;
            PostalCode pc = new PostalCode();
            Assert.IsTrue(postalCodeLayout.TryFind("zip", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutInt32>().ReadFixed(ref row, ref postalCodeScope, c, out pc.Zip));

            Assert.IsTrue(postalCodeLayout.TryFind("plus4", out c));
            postalCodeScope.Find(ref row, c.Path);
            if (c.TypeAs<LayoutInt16>().ReadSparse(ref row, ref postalCodeScope, out short plus4) == Result.Success)
            {
                pc.Plus4 = plus4;
            }

            return pc;
        }

        private void WriteHotel(ref RowBuffer row, ref RowCursor root, Hotel h)
        {
            Assert.IsTrue(this.hotelLayout.TryFind("hotel_id", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, h.Id));
            Assert.IsTrue(this.hotelLayout.TryFind("name", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, h.Name));
            Assert.IsTrue(this.hotelLayout.TryFind("phone", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, h.Phone));

            Assert.IsTrue(this.hotelLayout.TryFind("address", out c));
            root.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUDT>().WriteScope(ref row, ref root, c.TypeArgs, out RowCursor addressScope));
            this.WriteAddress(ref row, ref addressScope, c.TypeArgs, h.Address);
            root.Skip(ref row, ref addressScope);
        }

        private void WriteGuest(ref RowBuffer row, ref RowCursor root, Guest g)
        {
            Assert.IsTrue(this.guestLayout.TryFind("guest_id", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutGuid>().WriteFixed(ref row, ref root, c, g.Id));
            Assert.IsTrue(this.guestLayout.TryFind("first_name", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, g.FirstName));
            Assert.IsTrue(this.guestLayout.TryFind("last_name", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, g.LastName));
            Assert.IsTrue(this.guestLayout.TryFind("title", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, g.Title));
            Assert.IsTrue(this.guestLayout.TryFind("confirm_number", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, g.ConfirmNumber));

            if (g.Emails != null)
            {
                Assert.IsTrue(this.guestLayout.TryFind("emails", out c));
                root.Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref root, c.TypeArgs, out RowCursor emailScope));
                foreach (string email in g.Emails)
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref emailScope, email));
                    Assert.IsFalse(emailScope.MoveNext(ref row));
                }

                root.Skip(ref row, ref emailScope);
            }

            if (g.PhoneNumbers != null)
            {
                Assert.IsTrue(this.guestLayout.TryFind("phone_numbers", out c));
                root.Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref root, c.TypeArgs, out RowCursor phoneNumbersScope));
                foreach (string phone in g.PhoneNumbers)
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref phoneNumbersScope, phone));
                    Assert.IsFalse(phoneNumbersScope.MoveNext(ref row));
                }

                root.Skip(ref row, ref phoneNumbersScope);
            }

            if (g.Addresses != null)
            {
                Assert.IsTrue(this.guestLayout.TryFind("addresses", out c));
                root.Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref root, c.TypeArgs, out RowCursor addressesScope));
                TypeArgument tupleType = c.TypeAs<LayoutUniqueScope>().FieldType(ref addressesScope);
                TypeArgument t0 = tupleType.TypeArgs[0];
                TypeArgument t1 = tupleType.TypeArgs[1];
                foreach (KeyValuePair<string, Address> pair in g.Addresses)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(
                        tupleType.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref tempCursor, c.TypeArgs, out RowCursor tupleScope));

                    ResultAssert.IsSuccess(t0.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tupleScope, pair.Key));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(
                        t1.TypeAs<LayoutUDT>().WriteScope(ref row, ref tupleScope, t1.TypeArgs, out RowCursor addressScope));
                    this.WriteAddress(ref row, ref addressScope, t1.TypeArgs, pair.Value);
                    Assert.IsFalse(tupleScope.MoveNext(ref row, ref addressScope));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref addressesScope, ref tempCursor));
                }

                root.Skip(ref row, ref addressesScope);
            }
        }

        private int AppendGuestEmail(ref RowBuffer row, ref RowCursor root, string email)
        {
            Assert.IsTrue(this.guestLayout.TryFind("emails", out LayoutColumn c));
            root.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref root, out RowCursor emailScope));
            Assert.IsFalse(emailScope.MoveTo(ref row, int.MaxValue));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref emailScope, email));
            return emailScope.Index;
        }

        private int PrependGuestEmail(ref RowBuffer row, ref RowCursor root, string email)
        {
            Assert.IsTrue(this.guestLayout.TryFind("emails", out LayoutColumn c));
            root.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref root, out RowCursor emailScope));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref emailScope, email, UpdateOptions.InsertAt));
            return emailScope.Index;
        }

        private int InsertAtGuestEmail(ref RowBuffer row, ref RowCursor root, int i, string email)
        {
            Assert.IsTrue(this.guestLayout.TryFind("emails", out LayoutColumn c));
            root.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref root, out RowCursor emailScope));
            Assert.IsTrue(emailScope.MoveTo(ref row, i));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref emailScope, email, UpdateOptions.InsertAt));
            return emailScope.Index;
        }

        private void WriteAddress(ref RowBuffer row, ref RowCursor addressScope, TypeArgumentList typeArgs, Address a)
        {
            Layout addressLayout = this.customerResolver.Resolve(typeArgs.SchemaId);
            Assert.IsTrue(addressLayout.TryFind("street", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref addressScope, c, a.Street));
            Assert.IsTrue(addressLayout.TryFind("city", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref addressScope, c, a.City));
            Assert.IsTrue(addressLayout.TryFind("state", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteFixed(ref row, ref addressScope, c, a.State));

            Assert.IsTrue(addressLayout.TryFind("postal_code", out c));
            addressScope.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUDT>().WriteScope(ref row, ref addressScope, c.TypeArgs, out RowCursor postalCodeScope));
            this.WritePostalCode(ref row, ref postalCodeScope, c.TypeArgs, a.PostalCode);
            addressScope.Skip(ref row, ref postalCodeScope);
        }

        private Result PartialUpdateHotelAddress(ref RowBuffer row, ref RowCursor root, Address a)
        {
            Assert.IsTrue(this.hotelLayout.TryFind("address", out LayoutColumn c));
            root.Find(ref row, c.Path);
            Result r = c.TypeAs<LayoutUDT>().ReadScope(ref row, ref root, out RowCursor addressScope);
            if (r != Result.Success)
            {
                return r;
            }

            Layout addressLayout = addressScope.Layout;
            if (a.Street != null)
            {
                Assert.IsTrue(addressLayout.TryFind("street", out c));
                r = c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref addressScope, c, a.Street);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (a.City != null)
            {
                Assert.IsTrue(addressLayout.TryFind("city", out c));
                r = c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref addressScope, c, a.City);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (a.State != null)
            {
                Assert.IsTrue(addressLayout.TryFind("state", out c));
                r = c.TypeAs<LayoutUtf8>().WriteFixed(ref row, ref addressScope, c, a.State);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (a.PostalCode != null)
            {
                Assert.IsTrue(addressLayout.TryFind("postal_code", out c));
                addressScope.Find(ref row, c.Path);
                r = c.TypeAs<LayoutUDT>().WriteScope(ref row, ref addressScope, c.TypeArgs, out RowCursor postalCodeScope);
                if (r != Result.Success)
                {
                    return r;
                }

                this.WritePostalCode(ref row, ref postalCodeScope, c.TypeArgs, a.PostalCode);
            }

            return Result.Success;
        }

        private void WritePostalCode(ref RowBuffer row, ref RowCursor postalCodeScope, TypeArgumentList typeArgs, PostalCode pc)
        {
            Layout postalCodeLayout = this.customerResolver.Resolve(typeArgs.SchemaId);
            Assert.IsNotNull(postalCodeLayout);
            Assert.IsTrue(postalCodeLayout.TryFind("zip", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutInt32>().WriteFixed(ref row, ref postalCodeScope, c, pc.Zip));
            if (pc.Plus4.HasValue)
            {
                Assert.IsTrue(postalCodeLayout.TryFind("plus4", out c));
                postalCodeScope.Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutInt16>().WriteSparse(ref row, ref postalCodeScope, pc.Plus4.Value));
            }
        }

        private Hotel ReadHotel(ref RowBuffer row, ref RowCursor root)
        {
            Hotel h = new Hotel();
            Assert.IsTrue(this.hotelLayout.TryFind("hotel_id", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out h.Id));
            Assert.IsTrue(this.hotelLayout.TryFind("name", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out h.Name));
            Assert.IsTrue(this.hotelLayout.TryFind("phone", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out h.Phone));

            Assert.IsTrue(this.hotelLayout.TryFind("address", out c));
            Assert.IsTrue(c.Type.Immutable);
            root.Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUDT>().ReadScope(ref row, ref root, out RowCursor addressScope));
            Assert.IsTrue(addressScope.Immutable);
            h.Address = CustomerExampleUnitTests.ReadAddress(ref row, ref addressScope);
            root.Skip(ref row, ref addressScope);
            return h;
        }

        private Guest ReadGuest(ref RowBuffer row, ref RowCursor root)
        {
            Guest g = new Guest();
            Assert.IsTrue(this.guestLayout.TryFind("guest_id", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutGuid>().ReadFixed(ref row, ref root, c, out g.Id));
            Assert.IsTrue(this.guestLayout.TryFind("first_name", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out g.FirstName));
            Assert.IsTrue(this.guestLayout.TryFind("last_name", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out g.LastName));
            Assert.IsTrue(this.guestLayout.TryFind("title", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out g.Title));
            Assert.IsTrue(this.guestLayout.TryFind("confirm_number", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out g.ConfirmNumber));

            Assert.IsTrue(this.guestLayout.TryFind("emails", out c));
            root.Clone(out RowCursor emailScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref emailScope, out emailScope) == Result.Success)
            {
                g.Emails = new SortedSet<string>();
                while (emailScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref emailScope, out string item));
                    g.Emails.Add(item);
                }
            }

            Assert.IsTrue(this.guestLayout.TryFind("phone_numbers", out c));
            root.Clone(out RowCursor phoneNumbersScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref phoneNumbersScope, out phoneNumbersScope) == Result.Success)
            {
                g.PhoneNumbers = new List<string>();
                while (phoneNumbersScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref phoneNumbersScope, out string item));
                    g.PhoneNumbers.Add(item);
                }
            }

            Assert.IsTrue(this.guestLayout.TryFind("addresses", out c));
            root.Clone(out RowCursor addressesScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedMap>().ReadScope(ref row, ref addressesScope, out addressesScope) == Result.Success)
            {
                TypeArgument tupleType = LayoutType.TypedMap.FieldType(ref addressesScope);
                TypeArgument t0 = tupleType.TypeArgs[0];
                TypeArgument t1 = tupleType.TypeArgs[1];
                g.Addresses = new Dictionary<string, Address>();
                RowCursor pairScope = default;
                while (addressesScope.MoveNext(ref row, ref pairScope))
                {
                    ResultAssert.IsSuccess(tupleType.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref addressesScope, out pairScope));
                    Assert.IsTrue(pairScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(t0.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref pairScope, out string key));
                    Assert.IsTrue(pairScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(t1.TypeAs<LayoutUDT>().ReadScope(ref row, ref pairScope, out RowCursor addressScope));
                    Address value = CustomerExampleUnitTests.ReadAddress(ref row, ref addressScope);
                    g.Addresses.Add(key, value);
                    Assert.IsFalse(pairScope.MoveNext(ref row, ref addressScope));
                }
            }

            return g;
        }
    }
}
