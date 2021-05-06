// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Runtime.InteropServices;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.CustomerSchema;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class RecordIOUnitTests
    {
        private const int InitialRowSize = 0;

        [TestMethod]
        [Owner("jthunter")]
        public void LoadSchema()
        {
            LayoutResolver systemResolver = SchemasHrSchema.LayoutResolver;
            Layout segmentLayout = systemResolver.Resolve((SchemaId)SegmentHybridRowSerializer.SchemaId);
            Assert.AreEqual(segmentLayout.Name, "Segment");
            Assert.AreEqual(segmentLayout.SchemaId, (SchemaId)SegmentHybridRowSerializer.SchemaId);

            Layout recordLayout = systemResolver.Resolve((SchemaId)RecordHybridRowSerializer.SchemaId);
            Assert.AreEqual(recordLayout.Name, "Record");
            Assert.AreEqual(recordLayout.SchemaId, (SchemaId)RecordHybridRowSerializer.SchemaId);
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
            Namespace sampleNs = new Namespace { Name = "some sample namespace" };

            using (Stream stm = new MemoryStream())
            {
                // Create a reusable, resizable buffer.
                MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>();

                // Write a RecordIO stream.
                Result r = await stm.WriteRecordIOAsync(
                    new Segment(sampleComment, sampleNs),
                    (long index, out ReadOnlyMemory<byte> body) =>
                    {
                        body = default;
                        if (index >= addresses.Length)
                        {
                            return Result.Success;
                        }

                        return RecordIOUnitTests.WriteAddress(resizer, addresses[index], out body);
                    });

                ResultAssert.IsSuccess(r);

                // Read a RecordIO stream.
                List<Address> addressesRead = new List<Address>();
                stm.Position = 0;
                resizer = new MemorySpanResizer<byte>(1);
                r = await stm.ReadRecordIOAsync(
                    (ReadOnlyMemory<byte> record) =>
                    {
                        Assert.IsFalse(record.IsEmpty);

                        r = RecordIOUnitTests.ReadAddress(record, out Address obj);
                        ResultAssert.IsSuccess(r);
                        addressesRead.Add(obj);
                        return Result.Success;
                    },
                    segment =>
                    {
                        Assert.IsFalse(segment.IsEmpty);

                        r = RecordIOUnitTests.ReadSegment(segment, out Segment obj);
                        ResultAssert.IsSuccess(r);
                        Assert.AreEqual(obj.Comment, sampleComment);
                        Assert.AreEqual(obj.Schema.Name, sampleNs.Name);
                        return Result.Success;
                    },
                    resizer);

                ResultAssert.IsSuccess(r);

                // Check that the values all round-tripped.
                Assert.AreEqual(addresses.Length, addressesRead.Count);
                for (int i = 0; i < addresses.Length; i++)
                {
                    Assert.IsTrue(default(AddressHybridRowSerializer).Comparer.Equals(addresses[i], addressesRead[i]));
                }
            }
        }

        private static Result ReadSegment(ReadOnlyMemory<byte> buffer, out Segment obj)
        {
            RowReader reader = new RowReader(buffer, HybridRowVersion.V1, SchemasHrSchema.LayoutResolver);

            // Use the reader to dump to the screen.
            Result r = DiagnosticConverter.ReaderToString(ref reader, out string str);
            if (r != Result.Success)
            {
                obj = default;
                return r;
            }

            Console.WriteLine(str);

            // Reset the reader and materialize the object.
            // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
            // The cost-cast implied by MemoryMarshal.AsMemory is only safe here because:
            // 1. Only READ operations are performed on the row.
            // 2. The row is not allowed to escape this code.
            RowBuffer row = new RowBuffer(
                MemoryMarshal.AsMemory(buffer).Span,
                HybridRowVersion.V1,
                SchemasHrSchema.LayoutResolver);
            RowCursor root = RowCursor.Create(ref row);
            return default(SegmentHybridRowSerializer).Read(ref row, ref root, true, out obj);
        }

        private static Result WriteAddress(MemorySpanResizer<byte> resizer, Address obj, out ReadOnlyMemory<byte> buffer)
        {
            RowBuffer row = new RowBuffer(RecordIOUnitTests.InitialRowSize, resizer);
            Layout addressLayout = CustomerSchemaHrSchema.LayoutResolver.Resolve((SchemaId)AddressHybridRowSerializer.SchemaId);
            row.InitLayout(HybridRowVersion.V1, addressLayout, CustomerSchemaHrSchema.LayoutResolver);
            Result r = default(AddressHybridRowSerializer).Write(ref row, ref RowCursor.Create(ref row, out RowCursor _), true, default, obj);
            if (r != Result.Success)
            {
                buffer = default;
                return r;
            }

            buffer = resizer.Memory.Slice(0, row.Length);
            return Result.Success;
        }

        private static Result ReadAddress(ReadOnlyMemory<byte> buffer, out Address obj)
        {
            RowReader reader = new RowReader(buffer, HybridRowVersion.V1, CustomerSchemaHrSchema.LayoutResolver);

            // Use the reader to dump to the screen.
            Result r = DiagnosticConverter.ReaderToString(ref reader, out string str);
            if (r != Result.Success)
            {
                obj = default;
                return r;
            }

            Console.WriteLine(str);

            // Reset the reader and materialize the object.
            // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
            // The cost-cast implied by MemoryMarshal.AsMemory is only safe here because:
            // 1. Only READ operations are performed on the row.
            // 2. The row is not allowed to escape this code.
            RowBuffer row = new RowBuffer(
                MemoryMarshal.AsMemory(buffer).Span,
                HybridRowVersion.V1,
                CustomerSchemaHrSchema.LayoutResolver);
            return default(AddressHybridRowSerializer).Read(ref row, ref RowCursor.Create(ref row, out RowCursor _), true, out obj);
        }
    }
}
