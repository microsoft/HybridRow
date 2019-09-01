// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.nio.file.Files;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(TaggedUnitTests.SchemaFile, "TestData")] public sealed class TaggedUnitTests
public final class TaggedUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final String SchemaFile = "TestData\\TaggedApiSchema.json";
    private Layout layout;
    private LayoutResolver resolver;
    private Namespace schema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateTaggedApi()
    public void CreateTaggedApi() {
        RowBuffer row = new RowBuffer(TaggedUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        TaggedApi c1 = new TaggedApi();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: c1.Tag1 = ((byte)1, "hello");
        c1.Tag1 = ((byte)1, "hello")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: c1.Tag2 = ((byte)2, 28, 1974L);
        c1.Tag2 = ((byte)2, 28, 1974L)

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteTaggedApi(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        TaggedApi c2 = this.ReadTaggedApi(tempReference_row3, RowCursor.Create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString(TaggedUnitTests.SchemaFile);
        this.schema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.schema);
        this.layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "TaggedApi")).SchemaId);
    }

    private TaggedApi ReadTaggedApi(Reference<RowBuffer> row, Reference<RowCursor> root) {
        TaggedApi pc = new TaggedApi();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("tag1", out c);
        assert c.Type.Immutable;
        RowCursor tag1Scope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out tag1Scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref tag1Scope, out tag1Scope) == Result.Success) {
            assert tag1Scope.Immutable;
            assert tag1Scope.MoveNext(row);
            byte apiCode;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUInt8>().ReadSparse(ref row, ref
            // tag1Scope, out byte apiCode));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUInt8>TypeAs().ReadSparse(row, ref tag1Scope,
                out apiCode));
            assert tag1Scope.MoveNext(row);
            String str;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref tag1Scope, out str));
            pc.Tag1 = (apiCode, str)
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("tag2", out c);
        assert !c.Type.Immutable;
        RowCursor tag2Scope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out tag2Scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref tag2Scope, out tag2Scope) == Result.Success) {
            assert !tag2Scope.Immutable;
            assert tag2Scope.MoveNext(row);
            byte apiCode;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUInt8>().ReadSparse(ref row, ref
            // tag2Scope, out byte apiCode));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUInt8>TypeAs().ReadSparse(row, ref tag2Scope,
                out apiCode));
            assert tag2Scope.MoveNext(row);
            int val1;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt32>TypeAs().ReadSparse(row, ref tag2Scope, out val1));
            assert tag2Scope.MoveNext(row);
            long val2;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[2].Type.<LayoutInt64>TypeAs().ReadSparse(row, ref tag2Scope, out val2));
            pc.Tag2 = (apiCode, val1, val2)
        }

        return pc;
    }

    private void WriteTaggedApi(Reference<RowBuffer> row, Reference<RowCursor> root, TaggedApi pc) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("tag1", out c);
        RowCursor tag1Scope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out tag1Scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref tag1Scope, c.TypeArgs,
            out tag1Scope));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUInt8>TypeAs().WriteSparse(row, ref tag1Scope, pc.Tag1.Item1));
        assert tag1Scope.MoveNext(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref tag1Scope, pc.Tag1.Item2));

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("tag2", out c);
        RowCursor tag2Scope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out tag2Scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref tag2Scope, c.TypeArgs,
            out tag2Scope));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUInt8>TypeAs().WriteSparse(row, ref tag2Scope, pc.Tag2.Item1));
        assert tag2Scope.MoveNext(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt32>TypeAs().WriteSparse(row, ref tag2Scope, pc.Tag2.Item2));
        assert tag2Scope.MoveNext(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[2].Type.<LayoutInt64>TypeAs().WriteSparse(row, ref tag2Scope, pc.Tag2.Item3));
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test
    // types.")] private sealed class TaggedApi
    private final static class TaggedApi {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public (byte, string) Tag1;
        public (byte,String)Tag1
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public (byte, int, long) Tag2;
        public (byte,int,long)Tag2

        // ReSharper disable once MemberCanBePrivate.Local
        public boolean equals(TaggedApi other) {
            return this.Tag1.equals(other.Tag1) && this.Tag2.equals(other.Tag2);
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof TaggedApi;
            TaggedApi taggedApi = tempVar ? (TaggedApi)obj : null;
            return tempVar && this.equals(taggedApi);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                int hashCode = 0;
                hashCode = (hashCode * 397) ^ this.Tag1.hashCode();
                hashCode = (hashCode * 397) ^ this.Tag2.hashCode();
                return hashCode;
            }
        }
    }
}