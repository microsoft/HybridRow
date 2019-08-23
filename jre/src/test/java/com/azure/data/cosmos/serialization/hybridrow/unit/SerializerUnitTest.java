//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
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
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        Result r = RowWriter.WriteBuffer(tempRef_row, request, BatchRequestSerializer.Write);
        row = tempRef_row.get();
        assert Result.Success == r;
        System.out.printf("Length of serialized row: %1$s" + "\r\n", row.getLength());

        // Read the row back again.
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowReader reader = new RowReader(tempRef_row2);
        row = tempRef_row2.get();
        RefObject<RowReader> tempRef_reader =
            new RefObject<RowReader>(reader);
        BatchRequest _;
        OutObject<BatchRequest> tempOut__ = new OutObject<BatchRequest>();
        r = BatchRequestSerializer.Read(tempRef_reader, tempOut__);
        _ = tempOut__.get();
        reader = tempRef_reader.get();
        assert Result.Success == r;

        // Dump the materialized request to the console.
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        reader = new RowReader(tempRef_row3);
        row = tempRef_row3.get();
        RefObject<RowReader> tempRef_reader2 =
            new RefObject<RowReader>(reader);
        String dumpStr;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        r = DiagnosticConverter.ReaderToString(tempRef_reader2, out dumpStr);
        reader = tempRef_reader2.get();
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

        public static Result Read(RefObject<RowReader> reader, OutObject<BatchOperation> operation) {
            BatchOperation retval = new BatchOperation();
            operation.set(null);
            while (reader.get().Read()) {
                Result r;
                switch (reader.get().getPath()) {
                    case "operationType":
                        OutObject<Integer> tempOut_OperationType = new OutObject<Integer>();
                        r = reader.get().ReadInt32(tempOut_OperationType);
                        retval.OperationType = tempOut_OperationType.get();
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "headers":
                        RefObject<RowReader> tempRef_child = new RefObject<RowReader>(child);
                        OutObject<BatchRequestHeaders> tempOut_Headers = new OutObject<BatchRequestHeaders>();
                        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword
                        // - these are not converted by C# to Java Converter:
                        r = reader.get().ReadScope(retval,
                            (ref RowReader child, BatchOperation parent) -> BatchRequestHeadersSerializer.Read(tempRef_child, tempOut_Headers));
                        parent.Headers = tempOut_Headers.get();
                        child = tempRef_child.get();
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "resourceType":
                        OutObject<Integer> tempOut_ResourceType = new OutObject<Integer>();
                        r = reader.get().ReadInt32(tempOut_ResourceType);
                        retval.ResourceType = tempOut_ResourceType.get();
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "resourcePath":
                        OutObject<String> tempOut_ResourcePath = new OutObject<String>();
                        r = reader.get().ReadString(tempOut_ResourcePath);
                        retval.ResourcePath = tempOut_ResourcePath.get();
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                    case "resourceBody":
                        OutObject<Byte> tempOut_ResourceBody = new OutObject<Byte>();
                        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                        //ORIGINAL LINE: r = reader.ReadBinary(out retval.ResourceBody);
                        r = reader.get().ReadBinary(tempOut_ResourceBody);
                        retval.ResourceBody = tempOut_ResourceBody.get();
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                }
            }

            operation.set(retval);
            return Result.Success;
        }

        public static Result Write(RefObject<RowWriter> writer, TypeArgument typeArg,
                                   BatchOperation operation) {
            Result r = writer.get().WriteInt32("operationType", operation.OperationType);
            if (r != Result.Success) {
                return r;
            }

            r = writer.get().WriteScope("headers", BatchRequestHeadersSerializer.TypeArg, operation.Headers,
                BatchRequestHeadersSerializer.Write);
            if (r != Result.Success) {
                return r;
            }

            r = writer.get().WriteInt32("resourceType", operation.ResourceType);
            if (r != Result.Success) {
                return r;
            }

            r = writer.get().WriteString("resourcePath", operation.ResourcePath);
            if (r != Result.Success) {
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

        public static Result Read(RefObject<RowReader> reader,
                                  OutObject<BatchRequestHeaders> header) {
            BatchRequestHeaders retval = new BatchRequestHeaders();
            header.set(null);
            while (reader.get().Read()) {
                switch (reader.get().getPath()) {
                    case "sampleRequestHeader":
                        OutObject<Long> tempOut_SampleRequestHeader = new OutObject<Long>();
                        Result r = reader.get().ReadInt64(tempOut_SampleRequestHeader);
                        retval.SampleRequestHeader = tempOut_SampleRequestHeader.get();
                        if (r != Result.Success) {
                            return r;
                        }

                        break;
                }
            }

            header.set(retval);
            return Result.Success;
        }

        public static Result Write(RefObject<RowWriter> writer, TypeArgument typeArg,
                                   BatchRequestHeaders header) {
            Result r = writer.get().WriteInt64("sampleRequestHeader", header.SampleRequestHeader);
            return r;
        }
    }

    public static class BatchRequestSerializer {
        public static final TypeArgument OperationsTypeArg = new TypeArgument(LayoutType.TypedArray, new TypeArgumentList(new TypeArgument[] { BatchOperationSerializer.TypeArg }));

        public static Result Read(RefObject<RowReader> reader, OutObject<BatchRequest> request) {
            assert reader.get().Read();
            checkState(reader.get().getPath().equals("operations"));

            java.util.ArrayList<BatchOperation> operations;
            OutObject<ArrayList<TItem>> tempOut_operations = new OutObject<ArrayList<TItem>>();
            Result r = RowReaderExtensions.ReadList(reader.get().clone(), BatchOperationSerializer.Read, tempOut_operations);
            operations = tempOut_operations.get();
            if (r != Result.Success) {
                request.set(null);
                return r;
            }

            request.set(new BatchRequest());
            request.get().Operations = operations;

            return Result.Success;
        }

        public static Result Write(RefObject<RowWriter> writer, TypeArgument typeArg, BatchRequest request) {
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
            return writer.get().WriteScope("operations", BatchRequestSerializer.OperationsTypeArg, request.Operations, (ref RowWriter writer2, TypeArgument typeArg2, ArrayList<BatchOperation> operations) ->
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