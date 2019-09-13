// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

// ReSharper disable StringLiteralTypo
// ReSharper disable IdentifierTypo


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(TypedArrayUnitTests.SchemaFile, "TestData")] public sealed class
// TypedArrayUnitTests
public final class TypedArrayUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final String SchemaFile = "TestData\\TagSchema.json";
    private Namespace counterSchema;
    private Layout layout;
    private LayoutResolver resolver;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateTags()
    public void CreateTags() {
        RowBuffer row = new RowBuffer(TypedArrayUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        Tagged t1 = new Tagged();
        t1.Title = "Thriller";
        t1.Tags = new ArrayList<String>(Arrays.asList("classic", "Post-disco", "funk"));
        t1.Options = new ArrayList<Integer>(Arrays.asList(8, null, 9));
        t1.Ratings = new ArrayList<ArrayList<Double>>(Arrays.asList(new ArrayList<Double>(Arrays.asList(1.2, 3.0)),
            new ArrayList<Double>(Arrays.asList(4.1, 5.7)), new ArrayList<Double>(Arrays.asList(7.3, 8.12, 9.14))));
        SimilarMatch tempVar = new SimilarMatch();
        tempVar.Thumbprint = "TRABACN128F425B784";
        tempVar.Score = 0.87173699999999998;
        SimilarMatch tempVar2 = new SimilarMatch();
        tempVar2.Thumbprint = "TRJYGLF12903CB4952";
        tempVar2.Score = 0.75105200000000005;
        SimilarMatch tempVar3 = new SimilarMatch();
        tempVar3.Thumbprint = "TRWJMMB128F429D550";
        tempVar3.Score = 0.50866100000000003;
        t1.Similars = new ArrayList<SimilarMatch>(Arrays.asList(tempVar, tempVar2, tempVar3));
        t1.Priority = new ArrayList<Tuple<String, Long>>(Arrays.asList(Tuple.Create("80's", 100L), Tuple.Create(
            "classics", 100L), Tuple.Create("pop", 50L)));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteTagged(tempReference_row, RowCursor.create(tempReference_row2, out _), t1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Tagged t2 = this.ReadTagged(tempReference_row3, RowCursor.create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString(TypedArrayUnitTests.SchemaFile);
        this.counterSchema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.counterSchema);
        this.layout = this.resolver.Resolve(tangible.ListHelper.find(this.counterSchema.getSchemas(),
            x -> x.Name.equals("Tagged")).SchemaId);
    }

    private static SimilarMatch ReadSimilarMatch(Reference<RowBuffer> row,
                                                 Reference<RowCursor> matchScope) {
        Layout matchLayout = matchScope.get().getLayout();
        SimilarMatch m = new SimilarMatch();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("thumbprint", out c);
        Out<String> tempOut_Thumbprint = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadFixed(row, matchScope, c, tempOut_Thumbprint));
        m.Thumbprint = tempOut_Thumbprint.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("score", out c);
        Out<Double> tempOut_Score = new Out<Double>();
        ResultAssert.IsSuccess(c.<LayoutFloat64>TypeAs().ReadFixed(row, matchScope, c, tempOut_Score));
        m.Score = tempOut_Score.get();
        return m;
    }

    private Tagged ReadTagged(Reference<RowBuffer> row, Reference<RowCursor> root) {
        Tagged value = new Tagged();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("title", out c);
        Out<String> tempOut_Title = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Title));
        value.Title = tempOut_Title.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("tags", out c);
        RowCursor tagsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out tagsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref tagsScope, out tagsScope) == Result.SUCCESS) {
            value.Tags = new ArrayList<String>();
            while (tagsScope.MoveNext(row)) {
                String item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref tagsScope,
                    out item));
                value.Tags.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("options", out c);
        RowCursor optionsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out optionsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref optionsScope, out optionsScope) == Result.SUCCESS) {
            value.Options = new ArrayList<Integer>();
            while (optionsScope.MoveNext(row)) {
                TypeArgument itemType = c.TypeArgs[0];
                Reference<RowCursor> tempReference_optionsScope =
                    new Reference<RowCursor>(optionsScope);
                RowCursor nullableScope;
                Out<RowCursor> tempOut_nullableScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(itemType.getType().<LayoutNullable>TypeAs().ReadScope(row,
                    tempReference_optionsScope, tempOut_nullableScope));
                nullableScope = tempOut_nullableScope.get();
                optionsScope = tempReference_optionsScope.get();

                if (nullableScope.MoveNext(row)) {
                    Reference<RowCursor> tempReference_nullableScope = new Reference<RowCursor>(nullableScope);
                    ResultAssert.IsSuccess(LayoutNullable.HasValue(row, tempReference_nullableScope));
                    nullableScope = tempReference_nullableScope.get();

                    Reference<RowCursor> tempReference_nullableScope2 = new Reference<RowCursor>(nullableScope);
                    int itemValue;
                    Out<Integer> tempOut_itemValue = new Out<Integer>();
                    ResultAssert.IsSuccess(itemType.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs().ReadSparse(row, tempReference_nullableScope2, tempOut_itemValue));
                    itemValue = tempOut_itemValue.get();
                    nullableScope = tempReference_nullableScope2.get();

                    value.Options.add(itemValue);
                } else {
                    Reference<RowCursor> tempReference_nullableScope3 = new Reference<RowCursor>(nullableScope);
                    ResultAssert.NotFound(LayoutNullable.HasValue(row, tempReference_nullableScope3));
                    nullableScope = tempReference_nullableScope3.get();

                    value.Options.add(null);
                }
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("ratings", out c);
        RowCursor ratingsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out ratingsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref ratingsScope, out ratingsScope) == Result.SUCCESS) {
            value.Ratings = new ArrayList<ArrayList<Double>>();
            TypeArgument innerType = c.TypeArgs[0];
            LayoutTypedArray innerLayout = innerType.getType().<LayoutTypedArray>TypeAs();
            RowCursor innerScope = null;
            Reference<RowCursor> tempReference_innerScope =
                new Reference<RowCursor>(innerScope);
            while (ratingsScope.MoveNext(row, tempReference_innerScope)) {
                innerScope = tempReference_innerScope.get();
                ArrayList<Double> item = new ArrayList<Double>();
                Reference<RowCursor> tempReference_ratingsScope =
                    new Reference<RowCursor>(ratingsScope);
                Out<RowCursor> tempOut_innerScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempReference_ratingsScope, tempOut_innerScope));
                innerScope = tempOut_innerScope.get();
                ratingsScope = tempReference_ratingsScope.get();
                while (RowCursors.moveNext(innerScope.clone()
                    , row)) {
                    LayoutFloat64 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat64>TypeAs();
                    Reference<RowCursor> tempReference_innerScope2
                        = new Reference<RowCursor>(innerScope);
                    double innerItem;
                    Out<Double> tempOut_innerItem = new Out<Double>();
                    ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempReference_innerScope2, tempOut_innerItem));
                    innerItem = tempOut_innerItem.get();
                    innerScope = tempReference_innerScope2.get();
                    item.add(innerItem);
                }

                value.Ratings.add(item);
            }
            innerScope = tempReference_innerScope.get();
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("similars", out c);
        RowCursor similarsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out similarsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref similarsScope, out similarsScope) == Result.SUCCESS) {
            value.Similars = new ArrayList<SimilarMatch>();
            while (similarsScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                Reference<RowCursor> tempReference_similarsScope =
                    new Reference<RowCursor>(similarsScope);
                RowCursor matchScope;
                Out<RowCursor> tempOut_matchScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempReference_similarsScope, tempOut_matchScope));
                matchScope = tempOut_matchScope.get();
                similarsScope = tempReference_similarsScope.get();
                Reference<RowCursor> tempReference_matchScope =
                    new Reference<RowCursor>(matchScope);
                SimilarMatch item = TypedArrayUnitTests.ReadSimilarMatch(row, tempReference_matchScope);
                matchScope = tempReference_matchScope.get();
                value.Similars.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("priority", out c);
        RowCursor priorityScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out priorityScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref priorityScope, out priorityScope) == Result.SUCCESS) {
            value.Priority = new ArrayList<Tuple<String, Long>>();
            RowCursor tupleScope = null;
            Reference<RowCursor> tempReference_tupleScope =
                new Reference<RowCursor>(tupleScope);
            while (priorityScope.MoveNext(row, tempReference_tupleScope)) {
                tupleScope = tempReference_tupleScope.get();
                TypeArgument innerType = c.TypeArgs[0];
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();

                Reference<RowCursor> tempReference_priorityScope =
                    new Reference<RowCursor>(priorityScope);
                Out<RowCursor> tempOut_tupleScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempReference_priorityScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.get();
                priorityScope = tempReference_priorityScope.get();
                assert RowCursors.moveNext(tupleScope.clone()
                    , row);
                Reference<RowCursor> tempReference_tupleScope2 =
                    new Reference<RowCursor>(tupleScope);
                String item1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().ReadSparse(row,
                    tempReference_tupleScope2, out item1));
                tupleScope = tempReference_tupleScope2.get();

                assert RowCursors.moveNext(tupleScope.clone()
                    , row);
                Reference<RowCursor> tempReference_tupleScope3 =
                    new Reference<RowCursor>(tupleScope);
                long item2;
                Out<Long> tempOut_item2 = new Out<Long>();
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                    tempReference_tupleScope3, tempOut_item2));
                item2 = tempOut_item2.get();
                tupleScope = tempReference_tupleScope3.get();

                value.Priority.add(Tuple.Create(item1, item2));
            }
            tupleScope = tempReference_tupleScope.get();
        }

        return value;
    }

    private static void WriteSimilarMatch(Reference<RowBuffer> row, Reference<RowCursor> matchScope
        , TypeArgumentList typeArgs, SimilarMatch m) {
        Layout matchLayout = row.get().resolver().resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("thumbprint", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteFixed(row, matchScope, c, m.Thumbprint));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("score", out c);
        ResultAssert.IsSuccess(c.<LayoutFloat64>TypeAs().WriteFixed(row, matchScope, c, m.Score));
    }

    private void WriteTagged(Reference<RowBuffer> row, Reference<RowCursor> root, Tagged value) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("title", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, value.Title));

        if (value.Tags != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("tags", out c);
            RowCursor tagsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out tagsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref tagsScope, c.TypeArgs,
                out tagsScope));
            for (String item : value.Tags) {
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref tagsScope, item));
                assert !tagsScope.MoveNext(row);
            }
        }

        if (value.Options != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("options", out c);
            RowCursor optionsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out optionsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref optionsScope, c.TypeArgs,
                out optionsScope));
            for (Integer item : value.Options) {
                TypeArgument itemType = c.TypeArgs[0];
                RowCursor nullableScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(itemType.getType().<LayoutNullable>TypeAs().WriteScope(row, ref optionsScope,
                    itemType.getTypeArgs().clone(), item != null, out nullableScope));

                if (item != null) {
                    Reference<RowCursor> tempReference_nullableScope = new Reference<RowCursor>(nullableScope);
                    ResultAssert.IsSuccess(itemType.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs().WriteSparse(row, tempReference_nullableScope, item.intValue()));
                    nullableScope = tempReference_nullableScope.get();
                }

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !optionsScope.MoveNext(row, ref nullableScope);
            }
        }

        if (value.Ratings != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("ratings", out c);
            RowCursor ratingsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out ratingsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref ratingsScope, c.TypeArgs,
                out ratingsScope));
            for (ArrayList<Double> item : value.Ratings) {
                assert item != null;
                TypeArgument innerType = c.TypeArgs[0];
                LayoutTypedArray innerLayout = innerType.getType().<LayoutTypedArray>TypeAs();
                Reference<RowCursor> tempReference_ratingsScope =
                    new Reference<RowCursor>(ratingsScope);
                RowCursor innerScope;
                Out<RowCursor> tempOut_innerScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempReference_ratingsScope,
                    innerType.getTypeArgs().clone(), tempOut_innerScope));
                innerScope = tempOut_innerScope.get();
                ratingsScope = tempReference_ratingsScope.get();
                for (double innerItem : item) {
                    LayoutFloat64 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat64>TypeAs();
                    Reference<RowCursor> tempReference_innerScope =
                        new Reference<RowCursor>(innerScope);
                    ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempReference_innerScope, innerItem));
                    innerScope = tempReference_innerScope.get();
                    assert !innerScope.MoveNext(row);
                }

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !ratingsScope.MoveNext(row, ref innerScope);
            }
        }

        if (value.Similars != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("similars", out c);
            RowCursor similarsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out similarsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref similarsScope, c.TypeArgs,
                out similarsScope));
            for (SimilarMatch item : value.Similars) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                Reference<RowCursor> tempReference_similarsScope =
                    new Reference<RowCursor>(similarsScope);
                RowCursor matchScope;
                Out<RowCursor> tempOut_matchScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempReference_similarsScope,
                    innerType.getTypeArgs().clone(), tempOut_matchScope));
                matchScope = tempOut_matchScope.get();
                similarsScope = tempReference_similarsScope.get();
                Reference<RowCursor> tempReference_matchScope =
                    new Reference<RowCursor>(matchScope);
                TypedArrayUnitTests.WriteSimilarMatch(row, tempReference_matchScope, innerType.getTypeArgs().clone(), item);
                matchScope = tempReference_matchScope.get();

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !similarsScope.MoveNext(row, ref matchScope);
            }
        }

        if (value.Priority != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("priority", out c);
            RowCursor priorityScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out priorityScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref priorityScope, c.TypeArgs,
                out priorityScope));
            for (Tuple<String, Long> item : value.Priority) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref priorityScope, innerType.getTypeArgs().clone()
                    , out tupleScope));
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().WriteSparse(row,
                    ref tupleScope, item.Item1));
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope =
                    new Reference<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().WriteSparse(row
                    , tempReference_tupleScope, item.Item2));
                tupleScope = tempReference_tupleScope.get();

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !priorityScope.MoveNext(row, ref tupleScope);
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class SimilarMatch
    private final static class SimilarMatch {
        public double Score;
        public String Thumbprint;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof SimilarMatch;
            SimilarMatch match = tempVar ? (SimilarMatch)obj : null;
            return tempVar && this.equals(match);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                return (this.Thumbprint.hashCode() * 397) ^ (new Double(this.Score)).hashCode();
            }
        }

        private boolean equals(SimilarMatch other) {
            // ReSharper disable once CompareOfFloatsByEqualityOperator
            return this.Thumbprint.equals(other.Thumbprint) && this.Score == other.Score;
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test
    // types.")] private sealed class Tagged
    private final static class Tagged {
        public ArrayList<Integer> Options;
        public ArrayList<Tuple<String, Long>> Priority;
        public ArrayList<ArrayList<Double>> Ratings;
        public ArrayList<SimilarMatch> Similars;
        public ArrayList<String> Tags;
        public String Title;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Tagged;
            Tagged tagged = tempVar ? (Tagged)obj : null;
            return tempVar && this.equals(tagged);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                int hashCode = this.Title == null ? null : this.Title.hashCode() != null ? this.Title.hashCode() : 0;
                hashCode = (hashCode * 397) ^ (this.Tags == null ? null : this.Tags.hashCode() != null ?
                    this.Tags.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Options == null ? null : this.Options.hashCode() != null ?
                    this.Options.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Ratings == null ? null : this.Ratings.hashCode() != null ? this.Ratings.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Similars == null ? null : this.Similars.hashCode() != null ? this.Similars.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Priority == null ? null : this.Priority.hashCode() != null ? this.Priority.hashCode() : 0);
                return hashCode;
            }
        }

        private static <T> boolean NestedSequenceEquals(ArrayList<ArrayList<T>> left, ArrayList<ArrayList<T>> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (int i = 0; i < left.size(); i++) {
                //C# TO JAVA CONVERTER WARNING: Java AbstractList 'equals' is not always identical to LINQ 'SequenceEqual':
                //ORIGINAL LINE: if (!left[i].SequenceEqual(right[i]))
                if (!left.get(i).equals(right.get(i))) {
                    return false;
                }
            }

            return true;
        }

        private boolean equals(Tagged other) {
            //C# TO JAVA CONVERTER WARNING: Java AbstractList 'equals' is not always identical to LINQ 'SequenceEqual':
            //ORIGINAL LINE: return string.Equals(this.Title, other.Title) && (object.ReferenceEquals(this.Tags, other.Tags) || ((this.Tags != null) && (other.Tags != null) && this.Tags.SequenceEqual(other.Tags))) && (object.ReferenceEquals(this.Options, other.Options) || ((this.Options != null) && (other.Options != null) && this.Options.SequenceEqual(other.Options))) && (object.ReferenceEquals(this.Ratings, other.Ratings) || ((this.Ratings != null) && (other.Ratings != null) && Tagged.NestedSequenceEquals(this.Ratings, other.Ratings))) && (object.ReferenceEquals(this.Similars, other.Similars) || ((this.Similars != null) && (other.Similars != null) && this.Similars.SequenceEqual(other.Similars))) && (object.ReferenceEquals(this.Priority, other.Priority) || ((this.Priority != null) && (other.Priority != null) && this.Priority.SequenceEqual(other.Priority)));
            return this.Title.equals(other.Title) && (this.Tags == other.Tags || ((this.Tags != null) && (other.Tags != null) && this.Tags.equals(other.Tags))) && (this.Options == other.Options || ((this.Options != null) && (other.Options != null) && this.Options.equals(other.Options))) && (this.Ratings == other.Ratings || ((this.Ratings != null) && (other.Ratings != null) && Tagged.NestedSequenceEquals(this.Ratings, other.Ratings))) && (this.Similars == other.Similars || ((this.Similars != null) && (other.Similars != null) && this.Similars.equals(other.Similars))) && (this.Priority == other.Priority || ((this.Priority != null) && (other.Priority != null) && this.Priority.equals(other.Priority)));
        }
    }
}