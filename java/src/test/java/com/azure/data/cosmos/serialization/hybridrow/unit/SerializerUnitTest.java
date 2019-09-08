// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReaderExtensions;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;

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
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Result r = RowWriter.WriteBuffer(tempReference_row, request, BatchRequestSerializer.Write);
        row = tempReference_row.get();
        assert Result.SUCCESS == r;
        System.out.printf("Length of serialized row: %1$s" + "\r\n", row.length());

        // Read the row back again.
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowReader reader = new RowReader(tempReference_row2);
        row = tempReference_row2.get();
        Reference<RowReader> tempReference_reader =
            new Reference<RowReader>(reader);
        BatchRequest _;
        Out<BatchRequest> tempOut__ = new Out<BatchRequest>();
        r = BatchRequestSerializer.Read(tempReference_reader, tempOut__);
        _ = tempOut__.get();
        reader = tempReference_reader.get();
        assert Result.SUCCESS == r;

        // Dump the materialized request to the console.
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        reader = new RowReader(tempReference_row3);
        row = tempReference_row3.get();
        Reference<RowReader> tempReference_reader2 =
            new Reference<RowReader>(reader);
        String dumpStr;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        r = DiagnosticConverter.ReaderToString(tempReference_reader2, out dumpStr);
        reader = tempReference_reader2.get();
        assert Result.SUCCESS == r;
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

        public static Result Read(Reference<RowReader> reader, Out<BatchOperation> operation) {
            BatchOperation retval = new BatchOperation();
            operation.setAndGet(null);
            while (reader.get().read()) {
                Result r;
                switch (reader.get().path()) {
                    case "operationType":
                        Out<Integer> tempOut_OperationType = new Out<Integer>();
                        r = reader.get().readInt32(tempOut_OperationType);
                        retval.OperationType = tempOut_OperationType.get();
                        if (r != Result.SUCCESS) {
                            return r;
                        }

                        break;
                    case "headers":
                        Reference<RowReader> tempReference_child = new Reference<RowReader>(child);
                        Out<BatchRequestHeaders> tempOut_Headers = new Out<BatchRequestHeaders>();
                        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword
                        // - these are not converted by C# to Java Converter:
                        r = reader.get().readScope(retval,
                            (RowReader RowReader child, BatchOperation parent) -> BatchRequestHeadersSerializer.Read(tempReference_child, tempOut_Headers));
                        parent.Headers = tempOut_Headers.get();
                        child = tempReference_child.get();
                        if (r != Result.SUCCESS) {
                            return r;
                        }

                        break;
                    case "resourceType":
                        Out<Integer> tempOut_ResourceType = new Out<Integer>();
                        r = reader.get().readInt32(tempOut_ResourceType);
                        retval.ResourceType = tempOut_ResourceType.get();
                        if (r != Result.SUCCESS) {
                            return r;
                        }

                        break;
                    case "resourcePath":
                        Out<String> tempOut_ResourcePath = new Out<String>();
                        r = reader.get().readString(tempOut_ResourcePath);
                        retval.ResourcePath = tempOut_ResourcePath.get();
                        if (r != Result.SUCCESS) {
                            return r;
                        }

                        break;
                    case "resourceBody":
                        Out<Byte> tempOut_ResourceBody = new Out<Byte>();
                        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                        //ORIGINAL LINE: r = reader.ReadBinary(out retval.ResourceBody);
                        r = reader.get().ReadBinary(tempOut_ResourceBody);
                        retval.ResourceBody = tempOut_ResourceBody.get();
                        if (r != Result.SUCCESS) {
                            return r;
                        }

                        break;
                }
            }

            operation.setAndGet(retval);
            return Result.SUCCESS;
        }

        public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg,
                                   BatchOperation operation) {
            Result r = writer.get().WriteInt32("operationType", operation.OperationType);
            if (r != Result.SUCCESS) {
                return r;
            }

            r = writer.get().WriteScope("headers", BatchRequestHeadersSerializer.TypeArg, operation.Headers,
                BatchRequestHeadersSerializer.Write);
            if (r != Result.SUCCESS) {
                return r;
            }

            r = writer.get().WriteInt32("resourceType", operation.ResourceType);
            if (r != Result.SUCCESS) {
                return r;
            }

            r = writer.get().WriteString("resourcePath", operation.ResourcePath);
            if (r != Result.SUCCESS) {
                return r;
            }

            r = writer.get().WriteBinary("resourceBody", operation.ResourceBody);
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

        public static Result Read(Reference<RowReader> reader,
                                  Out<BatchRequestHeaders> header) {
            BatchRequestHeaders retval = new BatchRequestHeaders();
            header.setAndGet(null);
            while (reader.get().read()) {
                switch (reader.get().path()) {
                    case "sampleRequestHeader":
                        Out<Long> tempOut_SampleRequestHeader = new Out<Long>();
                        Result r = reader.get().readInt64(tempOut_SampleRequestHeader);
                        retval.SampleRequestHeader = tempOut_SampleRequestHeader.get();
                        if (r != Result.SUCCESS) {
                            return r;
                        }

                        break;
                }
            }

            header.setAndGet(retval);
            return Result.SUCCESS;
        }

        public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg,
                                   BatchRequestHeaders header) {
            Result r = writer.get().WriteInt64("sampleRequestHeader", header.SampleRequestHeader);
            return r;
        }
    }

    public static class BatchRequestSerializer {
        public static final TypeArgument OperationsTypeArg = new TypeArgument(LayoutType.TypedArray, new TypeArgumentList(new TypeArgument[] { BatchOperationSerializer.TypeArg }));

        public static Result Read(Reference<RowReader> reader, Out<BatchRequest> request) {
            assert reader.get().read();
            checkState(reader.get().path().equals("operations"));

            java.util.ArrayList<BatchOperation> operations;
            Out<ArrayList<TItem>> tempOut_operations = new Out<ArrayList<TItem>>();
            Result r = RowReaderExtensions.ReadList(reader.get().clone(), BatchOperationSerializer.Read, tempOut_operations);
            operations = tempOut_operations.get();
            if (r != Result.SUCCESS) {
                request.setAndGet(null);
                return r;
            }

            request.setAndGet(new BatchRequest());
            request.get().Operations = operations;

            return Result.SUCCESS;
        }

        public static Result Write(Reference<RowWriter> writer, TypeArgument typeArg, BatchRequest request) {
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
            return writer.get().WriteScope("operations", BatchRequestSerializer.OperationsTypeArg, request.Operations, (ref RowWriter writer2, TypeArgument typeArg2, ArrayList<BatchOperation> operations) ->
            {
                for (BatchOperation operation : operations) {
                    Result r = writer2.WriteScope(null, BatchOperationSerializer.TypeArg, operation, BatchOperationSerializer.Write);
                    if (r != Result.SUCCESS) {
                        return r;
                    }
                }

                return Result.SUCCESS;
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