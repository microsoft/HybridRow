// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private
#pragma warning disable SA1201 // Elements should appear in the correct order
#pragma warning disable SA1204 // Elements should appear in the correct order
#pragma warning disable CA1034 // Nested types should not be visible
#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(SerializerUnitTest.SchemaFile, "TestData")]
    public sealed class SerializerUnitTest
    {
        private const string SchemaFile = @"TestData\BatchApiSchema.json";
        private const int InitialRowSize = 2 * 1024 * 1024;

        private Namespace schema;
        private LayoutResolver resolver;
        private Layout layout;

        [TestInitialize]
        public void InitTestSuite()
        {
            string json = File.ReadAllText(SerializerUnitTest.SchemaFile);
            this.schema = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.schema);
            this.layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "BatchRequest").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateBatchRequest()
        {
            BatchRequest request = new BatchRequest()
            {
                Operations = new List<BatchOperation>()
                {
                    new BatchOperation()
                    {
                        OperationType = 3,
                        Headers = new BatchRequestHeaders()
                        {
                            SampleRequestHeader = 12345L,
                        },
                        ResourceType = 1,
                        ResourcePath = "/some/url/path",
                        ResourceBody = new byte[] { 1, 2, 3 },
                    },
                    new BatchOperation()
                    {
                        OperationType = 2,
                        Headers = new BatchRequestHeaders()
                        {
                            SampleRequestHeader = 98746L,
                        },
                        ResourceType = 2,
                        ResourcePath = "/some/other/url/path",
                        ResourceBody = new byte[] { 3, 2, 1 },
                    },
                },
            };

            // Write the request by serializing it to a row.
            RowBuffer row = new RowBuffer(SerializerUnitTest.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            Result r = RowWriter.WriteBuffer(ref row, request, BatchRequestSerializer.Write);
            Assert.AreEqual(Result.Success, r);
            Console.WriteLine("Length of serialized row: {0}", row.Length);

            // Read the row back again.
            RowReader reader = new RowReader(ref row);
            r = BatchRequestSerializer.Read(ref reader, out BatchRequest _);
            Assert.AreEqual(Result.Success, r);

            // Dump the materialized request to the console.
            reader = new RowReader(ref row);
            r = DiagnosticConverter.ReaderToString(ref reader, out string dumpStr);
            Assert.AreEqual(Result.Success, r);
            Console.WriteLine(dumpStr);
        }

        public sealed class BatchRequestHeaders
        {
            public long SampleRequestHeader;
        }

        public sealed class BatchOperation
        {
            public int OperationType;
            public BatchRequestHeaders Headers;
            public int ResourceType;
            public string ResourcePath;
            public byte[] ResourceBody;
        }

        public sealed class BatchRequest
        {
            public List<BatchOperation> Operations;
        }

        public sealed class BatchResponseHeaders
        {
            public string SampleResponseHeader;
        }

        public sealed class BatchOperationResponse
        {
            public int StatusCode;
            public BatchResponseHeaders Headers;
            public byte[] ResourceBody;
        }

        public sealed class BatchResponse
        {
            public List<BatchOperationResponse> Operations;
        }

        public static class BatchRequestHeadersSerializer
        {
            public static readonly TypeArgument TypeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(new SchemaId(1)));

            public static Result Write(ref RowWriter writer, TypeArgument typeArg, BatchRequestHeaders header)
            {
                Result r = writer.WriteInt64("sampleRequestHeader", header.SampleRequestHeader);
                if (r != Result.Success)
                {
                    return r;
                }

                return Result.Success;
            }

            public static Result Read(ref RowReader reader, out BatchRequestHeaders header)
            {
                BatchRequestHeaders retval = new BatchRequestHeaders();
                header = default;
                while (reader.Read())
                {
                    switch (reader.Path)
                    {
                        case "sampleRequestHeader":
                            Result r = reader.ReadInt64(out retval.SampleRequestHeader);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            break;
                    }
                }

                header = retval;
                return Result.Success;
            }
        }

        public static class BatchOperationSerializer
        {
            public static readonly TypeArgument TypeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(new SchemaId(2)));

            public static Result Write(ref RowWriter writer, TypeArgument typeArg, BatchOperation operation)
            {
                Result r = writer.WriteInt32("operationType", operation.OperationType);
                if (r != Result.Success)
                {
                    return r;
                }

                r = writer.WriteScope("headers", BatchRequestHeadersSerializer.TypeArg, operation.Headers, BatchRequestHeadersSerializer.Write);
                if (r != Result.Success)
                {
                    return r;
                }

                r = writer.WriteInt32("resourceType", operation.ResourceType);
                if (r != Result.Success)
                {
                    return r;
                }

                r = writer.WriteString("resourcePath", operation.ResourcePath);
                if (r != Result.Success)
                {
                    return r;
                }

                r = writer.WriteBinary("resourceBody", operation.ResourceBody);
                if (r != Result.Success)
                {
                    return r;
                }

                return Result.Success;
            }

            public static Result Read(ref RowReader reader, out BatchOperation operation)
            {
                BatchOperation retval = new BatchOperation();
                operation = default;
                while (reader.Read())
                {
                    Result r;
                    switch (reader.Path)
                    {
                        case "operationType":
                            r = reader.ReadInt32(out retval.OperationType);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            break;
                        case "headers":
                            r = reader.ReadScope(
                                retval,
                                (ref RowReader child, BatchOperation parent) =>
                                    BatchRequestHeadersSerializer.Read(ref child, out parent.Headers));
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            break;
                        case "resourceType":
                            r = reader.ReadInt32(out retval.ResourceType);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            break;
                        case "resourcePath":
                            r = reader.ReadString(out retval.ResourcePath);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            break;
                        case "resourceBody":
                            r = reader.ReadBinary(out retval.ResourceBody);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            break;
                    }
                }

                operation = retval;
                return Result.Success;
            }
        }

        public static class BatchRequestSerializer
        {
            public static readonly TypeArgument OperationsTypeArg = new TypeArgument(
                LayoutType.TypedArray,
                new TypeArgumentList(new[] { BatchOperationSerializer.TypeArg }));

            public static Result Write(ref RowWriter writer, TypeArgument typeArg, BatchRequest request)
            {
                return writer.WriteScope(
                    "operations",
                    BatchRequestSerializer.OperationsTypeArg,
                    request.Operations,
                    (ref RowWriter writer2, TypeArgument typeArg2, List<BatchOperation> operations) =>
                    {
                        foreach (BatchOperation operation in operations)
                        {
                            Result r = writer2.WriteScope(null, BatchOperationSerializer.TypeArg, operation, BatchOperationSerializer.Write);
                            if (r != Result.Success)
                            {
                                return r;
                            }
                        }

                        return Result.Success;
                    });
            }

            public static Result Read(ref RowReader reader, out BatchRequest request)
            {
                Assert.IsTrue(reader.Read());
                Contract.Assert(reader.Path == "operations");

                Result r = reader.ReadList(BatchOperationSerializer.Read, out List<BatchOperation> operations);
                if (r != Result.Success)
                {
                    request = default;
                    return r;
                }

                request = new BatchRequest()
                {
                    Operations = operations,
                };

                return Result.Success;
            }
        }
    }
}
