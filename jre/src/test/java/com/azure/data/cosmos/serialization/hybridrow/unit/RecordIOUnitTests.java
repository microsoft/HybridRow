//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.recordio.RecordIOStream;
import com.azure.data.cosmos.serialization.hybridrow.recordio.Segment;
import com.azure.data.cosmos.serialization.hybridrow.unit.customerschema.Address;

import java.nio.file.Files;
import java.util.ArrayList;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(RecordIOUnitTests.SchemaFile, "TestData")] public class RecordIOUnitTests
public class RecordIOUnitTests {
    private static final int InitialRowSize = 0;
    private static final String SchemaFile = "TestData\\CustomerSchema.json";
    private Layout addressLayout;
    private Namespace ns;
    private LayoutResolver resolver;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void LoadSchema()
    public final void LoadSchema() {
        LayoutResolver systemResolver = SystemSchema.LayoutResolver;
        Layout segmentLayout = systemResolver.Resolve(SystemSchema.SegmentSchemaId);
        assert segmentLayout.getName().equals("Segment");
        assert segmentLayout.getSchemaId().clone() == SystemSchema.SegmentSchemaId;

        Layout recordLayout = systemResolver.Resolve(SystemSchema.RecordSchemaId);
        assert recordLayout.getName().equals("Record");
        assert recordLayout.getSchemaId().clone() == SystemSchema.RecordSchemaId;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public final void ParseNamespaceExample() {
        String json = Files.readString(RecordIOUnitTests.SchemaFile);
        this.ns = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.ns);
        this.addressLayout = this.resolver.Resolve(tangible.ListHelper.find(this.ns.getSchemas(), x -> x.Name.equals(
            "Address")).SchemaId);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public async Task RoundTripAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public async Task RoundTripAsync()
    public final Task RoundTripAsync() {
        Address tempVar = new Address();
        tempVar.setStreet("300B Chocolate Hole");
        tempVar.setCity("Great Cruz Bay");
        tempVar.setState("VI");
        PostalCode tempVar2 = new PostalCode();
        tempVar2.setZip(00830);
        tempVar2.setPlus4(0001);
        tempVar.setPostalCode(tempVar2);
        Address tempVar3 = new Address();
        tempVar3.setStreet("1 Microsoft Way");
        tempVar3.setCity("Redmond");
        tempVar3.setState("WA");
        PostalCode tempVar4 = new PostalCode();
        tempVar4.setZip(98052);
        tempVar3.setPostalCode(tempVar4);
        Address[] addresses = { tempVar, tempVar3 };

        String sampleComment = "hello there";
        String sampleSDL = "some SDL";

        try (Stream stm = new MemoryStream()) {
            // Create a reusable, resizable buffer.
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(RecordIOUnitTests
            // .InitialRowSize);
            MemorySpanResizer<Byte> resizer = new MemorySpanResizer<Byte>(RecordIOUnitTests.InitialRowSize);

            // Write a RecordIO stream.
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'out' keyword - these are
            // not converted by C# to Java Converter:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: Result r = await stm.WriteRecordIOAsync(new Segment(sampleComment, sampleSDL), (long
            // index, out ReadOnlyMemory<byte> body) =>
            Result r = await
            RecordIOStream.WriteRecordIOAsync(stm,
                new Segment(sampleComment, sampleSDL), (long index, out ReadOnlyMemory<Byte>body) ->
            {
                body = null;
                if (index >= addresses.length) {
                    return Result.Success;
                }

                OutObject<ReadOnlyMemory<Byte>> tempOut_body = new OutObject<ReadOnlyMemory<Byte>>();
                Task tempVar5 = this.WriteAddress(resizer, addresses[index], tempOut_body);
                body = tempOut_body.get();
                return tempVar5;
            });

            // Read a RecordIO stream.
            ArrayList<Address> addressesRead = new ArrayList<Address>();
            stm.Position = 0;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: resizer = new MemorySpanResizer<byte>(1);
            resizer = new MemorySpanResizer<Byte>(1);
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            r = await
            RecordIOStream.ReadRecordIOAsync(stm, record ->
            {
                assert !record.IsEmpty;

                Address obj;
                OutObject<Address> tempOut_obj = new OutObject<Address>();
                r = this.ReadAddress(record, tempOut_obj);
                obj = tempOut_obj.get();
                ResultAssert.IsSuccess(r);
                addressesRead.add(obj);
                return Result.Success;
            }, segment ->
            {
                assert !segment.IsEmpty;

                Segment obj;
                OutObject<Segment> tempOut_obj =
                    new OutObject<Segment>();
                r = this.ReadSegment(segment, tempOut_obj);
                obj = tempOut_obj.get();
                ResultAssert.IsSuccess(r);
                assert obj.Comment == sampleComment;
                assert obj.SDL == sampleSDL;
                return Result.Success;
            }, resizer);

            ResultAssert.IsSuccess(r);

            // Check that the values all round-tripped.
            assert addresses.length == addressesRead.size();
            for (int i = 0; i < addresses.length; i++) {
                assert addresses[i] == addressesRead.get(i);
            }
        }
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private Result ReadAddress(Memory<byte> buffer, out Address obj)
    private Result ReadAddress(Memory<Byte> buffer, OutObject<Address> obj) {
        RowBuffer row = new RowBuffer(buffer.Span, HybridRowVersion.V1, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowReader reader = new RowReader(tempRef_row);
        row = tempRef_row.get();

        // Use the reader to dump to the screen.
        RefObject<RowReader> tempRef_reader =
            new RefObject<RowReader>(reader);
        String str;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = DiagnosticConverter.ReaderToString(tempRef_reader, out str);
        reader = tempRef_reader.get();
        if (r != Result.Success) {
            obj.set(null);
            return r;
        }

        System.out.println(str);

        // Reset the reader and materialize the object.
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        reader = new RowReader(tempRef_row2);
        row = tempRef_row2.get();
        RefObject<RowReader> tempRef_reader2 =
            new RefObject<RowReader>(reader);
        Result tempVar = AddressSerializer.Read(tempRef_reader2, obj);
        reader = tempRef_reader2.get();
        return tempVar;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private Result ReadSegment(Memory<byte> buffer, out Segment obj)
    private Result ReadSegment(Memory<Byte> buffer, OutObject<Segment> obj) {
        RowBuffer row = new RowBuffer(buffer.Span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowReader reader = new RowReader(tempRef_row);
        row = tempRef_row.get();

        // Use the reader to dump to the screen.
        RefObject<RowReader> tempRef_reader =
            new RefObject<RowReader>(reader);
        String str;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = DiagnosticConverter.ReaderToString(tempRef_reader, out str);
        reader = tempRef_reader.get();
        if (r != Result.Success) {
            obj.set(null);
            return r;
        }

        System.out.println(str);

        // Reset the reader and materialize the object.
        RefObject<RowBuffer> tempRef_row2 = new RefObject<RowBuffer>(row);
        reader = new RowReader(tempRef_row2);
        row = tempRef_row2.get();
        RefObject<RowReader> tempRef_reader2 = new RefObject<RowReader>(reader);
        Result tempVar = SegmentSerializer.Read(tempRef_reader2, obj.clone());
        reader = tempRef_reader2.get();
        return tempVar;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private Result WriteAddress(MemorySpanResizer<byte> resizer, Address obj, out
    // ReadOnlyMemory<byte> buffer)
    private Result WriteAddress(MemorySpanResizer<Byte> resizer, Address obj,
                                OutObject<ReadOnlyMemory<Byte>> buffer) {
        RowBuffer row = new RowBuffer(RecordIOUnitTests.InitialRowSize, resizer);
        row.InitLayout(HybridRowVersion.V1, this.addressLayout, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        Result r = RowWriter.WriteBuffer(tempRef_row, obj, AddressSerializer.Write);
        row = tempRef_row.get();
        if (r != Result.Success) {
            buffer.set(null);
            return r;
        }

        buffer.set(resizer.getMemory().Slice(0, row.getLength()));
        return Result.Success;
    }
}