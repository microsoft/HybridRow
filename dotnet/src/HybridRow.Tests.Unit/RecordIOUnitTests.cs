// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(RecordIOUnitTests.SchemaFile, "TestData")]
    public class RecordIOUnitTests
    {
        private const string SchemaFile = @"TestData\CustomerSchema.json";
        private const int InitialRowSize = 0;

        private Namespace ns;
        private LayoutResolver resolver;
        private Layout addressLayout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(RecordIOUnitTests.SchemaFile);
            this.ns = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.ns);
            this.addressLayout = this.resolver.Resolve(this.ns.Schemas.Find(x => x.Name == "Address").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void LoadSchema()
        {
            LayoutResolver systemResolver = SystemSchema.LayoutResolver;
            Layout segmentLayout = systemResolver.Resolve(SystemSchema.SegmentSchemaId);
            Assert.AreEqual(segmentLayout.Name, "Segment");
            Assert.AreEqual(segmentLayout.SchemaId, SystemSchema.SegmentSchemaId);

            Layout recordLayout = systemResolver.Resolve(SystemSchema.RecordSchemaId);
            Assert.AreEqual(recordLayout.Name, "Record");
            Assert.AreEqual(recordLayout.SchemaId, SystemSchema.RecordSchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public async Task RoundTripAsync()
        {
            Address[] addresses =
            {
                new Address
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
                new Address
                {
                    Street = "1 Microsoft Way",
                    City = "Redmond",
                    State = "WA",
                    PostalCode = new PostalCode
                    {
                        Zip = 98052,
                    },
                },
            };

            string sampleComment = "hello there";
            string sampleSDL = "some SDL";

            using (Stream stm = new MemoryStream())
            {
                // Create a reusable, resizable buffer.
                MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(RecordIOUnitTests.InitialRowSize);

                // Write a RecordIO stream.
                Result r = await stm.WriteRecordIOAsync(
                    new Segment(sampleComment, sampleSDL),
                    (long index, out ReadOnlyMemory<byte> body) =>
                    {
                        body = default;
                        if (index >= addresses.Length)
                        {
                            return Result.Success;
                        }

                        return this.WriteAddress(resizer, addresses[index], out body);
                    });

                // Read a RecordIO stream.
                List<Address> addressesRead = new List<Address>();
                stm.Position = 0;
                resizer = new MemorySpanResizer<byte>(1);
                r = await stm.ReadRecordIOAsync(
                    record =>
                    {
                        Assert.IsFalse(record.IsEmpty);

                        r = this.ReadAddress(record, out Address obj);
                        ResultAssert.IsSuccess(r);
                        addressesRead.Add(obj);
                        return Result.Success;
                    },
                    segment =>
                    {
                        Assert.IsFalse(segment.IsEmpty);

                        r = this.ReadSegment(segment, out Segment obj);
                        ResultAssert.IsSuccess(r);
                        Assert.AreEqual(obj.Comment, sampleComment);
                        Assert.AreEqual(obj.SDL, sampleSDL);
                        return Result.Success;
                    },
                    resizer);

                ResultAssert.IsSuccess(r);

                // Check that the values all round-tripped.
                Assert.AreEqual(addresses.Length, addressesRead.Count);
                for (int i = 0; i < addresses.Length; i++)
                {
                    Assert.AreEqual(addresses[i], addressesRead[i]);
                }
            }
        }

        private Result WriteAddress(MemorySpanResizer<byte> resizer, Address obj, out ReadOnlyMemory<byte> buffer)
        {
            RowBuffer row = new RowBuffer(RecordIOUnitTests.InitialRowSize, resizer);
            row.InitLayout(HybridRowVersion.V1, this.addressLayout, this.resolver);
            Result r = RowWriter.WriteBuffer(ref row, obj, AddressSerializer.Write);
            if (r != Result.Success)
            {
                buffer = default;
                return r;
            }

            buffer = resizer.Memory.Slice(0, row.Length);
            return Result.Success;
        }

        private Result ReadAddress(Memory<byte> buffer, out Address obj)
        {
            RowBuffer row = new RowBuffer(buffer.Span, HybridRowVersion.V1, this.resolver);
            RowReader reader = new RowReader(ref row);

            // Use the reader to dump to the screen.
            Result r = DiagnosticConverter.ReaderToString(ref reader, out string str);
            if (r != Result.Success)
            {
                obj = default;
                return r;
            }

            Console.WriteLine(str);

            // Reset the reader and materialize the object.
            reader = new RowReader(ref row);
            return AddressSerializer.Read(ref reader, out obj);
        }

        private Result ReadSegment(Memory<byte> buffer, out Segment obj)
        {
            RowBuffer row = new RowBuffer(buffer.Span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
            RowReader reader = new RowReader(ref row);

            // Use the reader to dump to the screen.
            Result r = DiagnosticConverter.ReaderToString(ref reader, out string str);
            if (r != Result.Success)
            {
                obj = default;
                return r;
            }

            Console.WriteLine(str);

            // Reset the reader and materialize the object.
            reader = new RowReader(ref row);
            return SegmentSerializer.Read(ref reader, out obj);
        }
    }
}
