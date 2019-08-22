//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

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
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

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

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTagged(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Tagged t2 = this.ReadTagged(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
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

    private static SimilarMatch ReadSimilarMatch(tangible.RefObject<RowBuffer> row,
                                                 tangible.RefObject<RowCursor> matchScope) {
        Layout matchLayout = matchScope.argValue.getLayout();
        SimilarMatch m = new SimilarMatch();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("thumbprint", out c);
        tangible.OutObject<String> tempOut_Thumbprint = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadFixed(row, matchScope, c, tempOut_Thumbprint));
        m.Thumbprint = tempOut_Thumbprint.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("score", out c);
        tangible.OutObject<Double> tempOut_Score = new tangible.OutObject<Double>();
        ResultAssert.IsSuccess(c.<LayoutFloat64>TypeAs().ReadFixed(row, matchScope, c, tempOut_Score));
        m.Score = tempOut_Score.argValue;
        return m;
    }

    private Tagged ReadTagged(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        Tagged value = new Tagged();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("title", out c);
        tangible.OutObject<String> tempOut_Title = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Title));
        value.Title = tempOut_Title.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("tags", out c);
        RowCursor tagsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out tagsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref tagsScope, out tagsScope) == Result.Success) {
            value.Tags = new ArrayList<String>();
            while (tagsScope.MoveNext(row)) {
                String item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref tagsScope,
                    out item));
                value.Tags.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("options", out c);
        RowCursor optionsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out optionsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref optionsScope, out optionsScope) == Result.Success) {
            value.Options = new ArrayList<Integer>();
            while (optionsScope.MoveNext(row)) {
                TypeArgument itemType = c.TypeArgs[0];
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_optionsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(optionsScope);
                RowCursor nullableScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(itemType.getType().<LayoutNullable>TypeAs().ReadScope(row,
                    tempRef_optionsScope, tempOut_nullableScope));
                nullableScope = tempOut_nullableScope.argValue;
                optionsScope = tempRef_optionsScope.argValue;

                if (nullableScope.MoveNext(row)) {
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                    ResultAssert.IsSuccess(LayoutNullable.HasValue(row, tempRef_nullableScope));
                    nullableScope = tempRef_nullableScope.argValue;

                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                    int itemValue;
                    tangible.OutObject<Integer> tempOut_itemValue = new tangible.OutObject<Integer>();
                    ResultAssert.IsSuccess(itemType.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs().ReadSparse(row, tempRef_nullableScope2, tempOut_itemValue));
                    itemValue = tempOut_itemValue.argValue;
                    nullableScope = tempRef_nullableScope2.argValue;

                    value.Options.add(itemValue);
                } else {
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope3 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                    ResultAssert.NotFound(LayoutNullable.HasValue(row, tempRef_nullableScope3));
                    nullableScope = tempRef_nullableScope3.argValue;

                    value.Options.add(null);
                }
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("ratings", out c);
        RowCursor ratingsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out ratingsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref ratingsScope, out ratingsScope) == Result.Success) {
            value.Ratings = new ArrayList<ArrayList<Double>>();
            TypeArgument innerType = c.TypeArgs[0];
            LayoutTypedArray innerLayout = innerType.getType().<LayoutTypedArray>TypeAs();
            RowCursor innerScope = null;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
            while (ratingsScope.MoveNext(row, tempRef_innerScope)) {
                innerScope = tempRef_innerScope.argValue;
                ArrayList<Double> item = new ArrayList<Double>();
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_ratingsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(ratingsScope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_ratingsScope, tempOut_innerScope));
                innerScope = tempOut_innerScope.argValue;
                ratingsScope = tempRef_ratingsScope.argValue;
                while (Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(innerScope.clone()
                    , row)) {
                    LayoutFloat64 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat64>TypeAs();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope2
                        = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
                    double innerItem;
                    tangible.OutObject<Double> tempOut_innerItem = new tangible.OutObject<Double>();
                    ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempRef_innerScope2, tempOut_innerItem));
                    innerItem = tempOut_innerItem.argValue;
                    innerScope = tempRef_innerScope2.argValue;
                    item.add(innerItem);
                }

                value.Ratings.add(item);
            }
            innerScope = tempRef_innerScope.argValue;
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("similars", out c);
        RowCursor similarsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out similarsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref similarsScope, out similarsScope) == Result.Success) {
            value.Similars = new ArrayList<SimilarMatch>();
            while (similarsScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_similarsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(similarsScope);
                RowCursor matchScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_matchScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_similarsScope, tempOut_matchScope));
                matchScope = tempOut_matchScope.argValue;
                similarsScope = tempRef_similarsScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_matchScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(matchScope);
                SimilarMatch item = TypedArrayUnitTests.ReadSimilarMatch(row, tempRef_matchScope);
                matchScope = tempRef_matchScope.argValue;
                value.Similars.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("priority", out c);
        RowCursor priorityScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out priorityScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref priorityScope, out priorityScope) == Result.Success) {
            value.Priority = new ArrayList<Tuple<String, Long>>();
            RowCursor tupleScope = null;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
            while (priorityScope.MoveNext(row, tempRef_tupleScope)) {
                tupleScope = tempRef_tupleScope.argValue;
                TypeArgument innerType = c.TypeArgs[0];
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_priorityScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(priorityScope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_priorityScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.argValue;
                priorityScope = tempRef_priorityScope.argValue;
                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone()
                    , row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                String item1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().ReadSparse(row,
                    tempRef_tupleScope2, out item1));
                tupleScope = tempRef_tupleScope2.argValue;

                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone()
                    , row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                long item2;
                tangible.OutObject<Long> tempOut_item2 = new tangible.OutObject<Long>();
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                    tempRef_tupleScope3, tempOut_item2));
                item2 = tempOut_item2.argValue;
                tupleScope = tempRef_tupleScope3.argValue;

                value.Priority.add(Tuple.Create(item1, item2));
            }
            tupleScope = tempRef_tupleScope.argValue;
        }

        return value;
    }

    private static void WriteSimilarMatch(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> matchScope
        , TypeArgumentList typeArgs, SimilarMatch m) {
        Layout matchLayout = row.argValue.getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("thumbprint", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteFixed(row, matchScope, c, m.Thumbprint));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("score", out c);
        ResultAssert.IsSuccess(c.<LayoutFloat64>TypeAs().WriteFixed(row, matchScope, c, m.Score));
    }

    private void WriteTagged(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, Tagged value) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("title", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, value.Title));

        if (value.Tags != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("tags", out c);
            RowCursor tagsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out tagsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref tagsScope, c.TypeArgs,
                out tagsScope));
            for (String item : value.Tags) {
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref tagsScope, item));
                assert !tagsScope.MoveNext(row);
            }
        }

        if (value.Options != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("options", out c);
            RowCursor optionsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out optionsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref optionsScope, c.TypeArgs,
                out optionsScope));
            for (Integer item : value.Options) {
                TypeArgument itemType = c.TypeArgs[0];
                RowCursor nullableScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(itemType.getType().<LayoutNullable>TypeAs().WriteScope(row, ref optionsScope,
                    itemType.getTypeArgs().clone(), item != null, out nullableScope));

                if (item != null) {
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                    ResultAssert.IsSuccess(itemType.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs().WriteSparse(row, tempRef_nullableScope, item.intValue()));
                    nullableScope = tempRef_nullableScope.argValue;
                }

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !optionsScope.MoveNext(row, ref nullableScope);
            }
        }

        if (value.Ratings != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("ratings", out c);
            RowCursor ratingsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out ratingsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref ratingsScope, c.TypeArgs,
                out ratingsScope));
            for (ArrayList<Double> item : value.Ratings) {
                assert item != null;
                TypeArgument innerType = c.TypeArgs[0];
                LayoutTypedArray innerLayout = innerType.getType().<LayoutTypedArray>TypeAs();
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_ratingsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(ratingsScope);
                RowCursor innerScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempRef_ratingsScope,
                    innerType.getTypeArgs().clone(), tempOut_innerScope));
                innerScope = tempOut_innerScope.argValue;
                ratingsScope = tempRef_ratingsScope.argValue;
                for (double innerItem : item) {
                    LayoutFloat64 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat64>TypeAs();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
                    ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempRef_innerScope, innerItem));
                    innerScope = tempRef_innerScope.argValue;
                    assert !innerScope.MoveNext(row);
                }

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !ratingsScope.MoveNext(row, ref innerScope);
            }
        }

        if (value.Similars != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("similars", out c);
            RowCursor similarsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out similarsScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref similarsScope, c.TypeArgs,
                out similarsScope));
            for (SimilarMatch item : value.Similars) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_similarsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(similarsScope);
                RowCursor matchScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_matchScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempRef_similarsScope,
                    innerType.getTypeArgs().clone(), tempOut_matchScope));
                matchScope = tempOut_matchScope.argValue;
                similarsScope = tempRef_similarsScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_matchScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(matchScope);
                TypedArrayUnitTests.WriteSimilarMatch(row, tempRef_matchScope, innerType.getTypeArgs().clone(), item);
                matchScope = tempRef_matchScope.argValue;

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !similarsScope.MoveNext(row, ref matchScope);
            }
        }

        if (value.Priority != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("priority", out c);
            RowCursor priorityScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out priorityScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, ref priorityScope, c.TypeArgs,
                out priorityScope));
            for (Tuple<String, Long> item : value.Priority) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref priorityScope, innerType.getTypeArgs().clone()
                    , out tupleScope));
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().WriteSparse(row,
                    ref tupleScope, item.Item1));
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().WriteSparse(row
                    , tempRef_tupleScope, item.Item2));
                tupleScope = tempRef_tupleScope.argValue;

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
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