//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.SchemaId;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1201 // Elements should appear in the correct order
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1204 // Elements should appear in the correct order
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1034 // Nested types should not be visible
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1051 // Do not declare visible instance fields


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(SerializerUnitTest.SchemaFile, "TestData")] public sealed class
// SerializerUnitTest
public final class SerializerUnitTest {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final String SchemaFile = "TestData\\BatchApiSchema.json";
    private Layout layout;
    private LayoutResolver resolver;
    private Namespace schema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateBatchRequest()
    public void CreateBatchRequest() {
        BatchRequest request = new BatchRequest();
        BatchOperation tempVar = new BatchOperation();
        tempVar.OperationType = 3;
        tempVar.Headers = new BatchRequestHeaders();
        tempVar.Headers.SampleRequestHeader = 12345L;
        tempVar.ResourceType = 1;
        tempVar.ResourcePath = "/some/url/path";
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: tempVar.ResourceBody = new byte[] { 1, 2, 3 };
        tempVar.ResourceBody = new byte[] { 1, 2, 3 };
        BatchOperation tempVar2 = new BatchOperation();
        tempVar2.OperationType = 2;
        tempVar2.Headers = new BatchRequestHeaders();
        tempVar2.Headers.SampleRequestHeader = 98746L;
        tempVar2.ResourceType = 2;
        tempVar2.ResourcePath = "/some/other/url/path";
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: tempVar2.ResourceBody = new byte[] { 3, 2, 1 };
        tempVar2.ResourceBody = new byte[] { 3, 2, 1 };
        request.Operations = new ArrayList<BatchOperation>(Arrays.asList(tempVar, tempVar2));

        // Write the request by serializing it to a row.
        RowBuffer row = new RowBuffer(SerializerUnitTest.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        Result r = RowWriter.WriteBuffer(tempRef_row, request, BatchRequestSerializer.Write);
        row = tempRef_row.argValue;
        assert Result.Success == r;
        System.out.printf("Length of serialized row: %1$s" + "\r\n", row.getLength());

        // Read the row back again.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowReader reader = new RowReader(tempRef_row2);
        row = tempRef_row2.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
        BatchRequest _;
        tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.SerializerUnitTest.BatchRequest> tempOut__ = new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.SerializerUnitTest.BatchRequest>();
        r = BatchRequestSerializer.Read(tempRef_reader, tempOut__);
        _ = tempOut__.argValue;
        reader = tempRef_reader.argValue;
        assert Result.Success == r;

        // Dump the materialized request to the console.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        reader = new RowReader(tempRef_row3);
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
        String dumpStr;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        r = DiagnosticConverter.ReaderToString(tempRef_reader2, out dumpStr);
        reader = tempRef_reader2.argValue;
        assert Result.Success == r;
        System.out.println(dumpStr);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void InitTestSuite()
    public void InitTestSuite() {
        String json = Files.readString(SerializerUnitTest.SchemaFile);
        this.schema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.schema);
        this.layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "BatchRequest")).SchemaId);
    }

    public final static class BatchOperation {
        public BatchRequestHeaders Headers;
        public int OperationType;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public byte[] ResourceBody;
        public byte[] ResourceBody;
        public String ResourcePath;
        public int ResourceType;
    }

    public final static class BatchOperationResponse {
        public BatchResponseHeaders Headers;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public byte[] ResourceBody;
        public byte[] ResourceBody;
        public int StatusCode;
    }

    public static class BatchOperationSerializer {
        public static final TypeArgument TypeArg = new TypeArgument(LayoutType.UDT,
            new TypeArgumentList(new SchemaId(2)));

        public static Result Read(tangible.RefObject<RowReader> reader, tangible.OutObject<BatchOperation> operation) {
            BatchOperation retval = new BatchOperation();
            operation.argValue = null;
            while (reader.argValue.Read()) {
                Result r;
                switch (reader.argValue.getPath()) {
                    case "operationType":
                        tangible.OutObject<Integer> tempOut_OperationType = new tangible.OutObject<Integer>();
                        r = reader.argValue.ReadInt32(tempOut_OperationType);
                        retval.OperationType = tempOut_OperationType.argValue;
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "headers":
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_child = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(child);
                        tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.SerializerUnitTest.BatchRequestHeaders> tempOut_Headers = new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.SerializerUnitTest.BatchRequestHeaders>();
                        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword
                        // - these are not converted by C# to Java Converter:
                        r = reader.argValue.ReadScope(retval,
                            (ref RowReader child, BatchOperation parent) -> BatchRequestHeadersSerializer.Read(tempRef_child, tempOut_Headers));
                        parent.Headers = tempOut_Headers.argValue;
                        child = tempRef_child.argValue;
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "resourceType":
                        tangible.OutObject<Integer> tempOut_ResourceType = new tangible.OutObject<Integer>();
                        r = reader.argValue.ReadInt32(tempOut_ResourceType);
                        retval.ResourceType = tempOut_ResourceType.argValue;
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "resourcePath":
                        tangible.OutObject<String> tempOut_ResourcePath = new tangible.OutObject<String>();
                        r = reader.argValue.ReadString(tempOut_ResourcePath);
                        retval.ResourcePath = tempOut_ResourcePath.argValue;
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "resourceBody":
                        tangible.OutObject<Byte> tempOut_ResourceBody = new tangible.OutObject<Byte>();
                        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                        //ORIGINAL LINE: r = reader.ReadBinary(out retval.ResourceBody);
                        r = reader.argValue.ReadBinary(tempOut_ResourceBody);
                        retval.ResourceBody = tempOut_ResourceBody.argValue;
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                }
            }

            operation.argValue = retval;
            return Result.Success;
        }

        public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg,
                                   BatchOperation operation) {
            Result r = writer.argValue.WriteInt32("operationType", operation.OperationType);
            if (r != Result.Success) {
                return r;
            }

            r = writer.argValue.WriteScope("headers", BatchRequestHeadersSerializer.TypeArg, operation.Headers,
                BatchRequestHeadersSerializer.Write);
            if (r != Result.Success) {
                return r;
            }

            r = writer.argValue.WriteInt32("resourceType", operation.ResourceType);
            if (r != Result.Success) {
                return r;
            }

            r = writer.argValue.WriteString("resourcePath", operation.ResourcePath);
            if (r != Result.Success) {
                return r;
            }

            r = writer.argValue.WriteBinary("resourceBody", operation.ResourceBody);
            return r;
        }
    }

    public final static class BatchRequest {
        public ArrayList<BatchOperation> Operations;
    }

    public final static class BatchRequestHeaders {
        public long SampleRequestHeader;
    }

    public static class BatchRequestHeadersSerializer {
        public static final TypeArgument TypeArg = new TypeArgument(LayoutType.UDT,
            new TypeArgumentList(new SchemaId(1)));

        public static Result Read(tangible.RefObject<RowReader> reader,
                                  tangible.OutObject<BatchRequestHeaders> header) {
            BatchRequestHeaders retval = new BatchRequestHeaders();
            header.argValue = null;
            while (reader.argValue.Read()) {
                switch (reader.argValue.getPath()) {
                    case "sampleRequestHeader":
                        tangible.OutObject<Long> tempOut_SampleRequestHeader = new tangible.OutObject<Long>();
                        Result r = reader.argValue.ReadInt64(tempOut_SampleRequestHeader);
                        retval.SampleRequestHeader = tempOut_SampleRequestHeader.argValue;
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                }
            }

            header.argValue = retval;
            return Result.Success;
        }

        public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg,
                                   BatchRequestHeaders header) {
            Result r = writer.argValue.WriteInt64("sampleRequestHeader", header.SampleRequestHeader);
            return r;
        }
    }

    public static class BatchRequestSerializer {
        public static final TypeArgument OperationsTypeArg = new TypeArgument(LayoutType.TypedArray, new TypeArgumentList(new azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument[] { BatchOperationSerializer.TypeArg }));

        public static Result Read(tangible.RefObject<RowReader> reader, tangible.OutObject<BatchRequest> request) {
            assert reader.argValue.Read();
            Contract.Assert(reader.argValue.getPath().equals("operations"));

            java.util.ArrayList<BatchOperation> operations;
            tangible.OutObject<ArrayList<TItem>> tempOut_operations = new tangible.OutObject<ArrayList<TItem>>();
            Result r = azure.data.cosmos.serialization.hybridrow.io.RowReaderExtensions.ReadList(reader.argValue.clone(), BatchOperationSerializer.Read, tempOut_operations);
            operations = tempOut_operations.argValue;
            if (r != Result.Success) {
                request.argValue = null;
                return r;
            }

            request.argValue = new BatchRequest();
            request.argValue.Operations = operations;

            return Result.Success;
        }

        public static Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg, BatchRequest request) {
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
            return writer.argValue.WriteScope("operations", BatchRequestSerializer.OperationsTypeArg, request.Operations, (ref RowWriter writer2, TypeArgument typeArg2, ArrayList<BatchOperation> operations) ->
            {
                for (BatchOperation operation : operations) {
                    Result r = writer2.WriteScope(null, BatchOperationSerializer.TypeArg, operation, BatchOperationSerializer.Write);
                    if (r != Result.Success) {
                        return r;
                    }
                }

                return Result.Success;
            });
        }
    }

    public final static class BatchResponse {
        public ArrayList<BatchOperationResponse> Operations;
    }

    public final static class BatchResponseHeaders {
        public String SampleResponseHeader;
    }
}