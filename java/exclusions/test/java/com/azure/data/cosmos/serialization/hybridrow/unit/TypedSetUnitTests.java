// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(TypedSetUnitTests.SchemaFile, "TestData")] public sealed class
// TypedSetUnitTests
public final class TypedSetUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final String SchemaFile = "TestData\\TodoSchema.json";
    private Namespace counterSchema;
    private Layout layout;
    private LayoutResolver resolver;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateTodos()
    public void CreateTodos() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        Todo t1 = new Todo();
        t1.Attendees = new ArrayList<String>(Arrays.asList("jason", "janice", "joshua"));
        t1.Projects = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5-0421EE36F168}"),
            UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), UUID.fromString("{B7BC39C2-1A2D-4EAF-8F33" +
                "-ED976872D876}"), UUID.fromString("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}")));
        t1.Checkboxes = new ArrayList<Boolean>(Arrays.asList(true, false));
        t1.Prices = new ArrayList<ArrayList<Float>>(Arrays.asList(new ArrayList<Float>(Arrays.asList(1.2F, 3.0F)),
            new ArrayList<Float>(Arrays.asList(4.1F, 5.7F)), new ArrayList<Float>(Arrays.asList(7.3F, 8.12F, 9.14F))));
        t1.Nested =
            new ArrayList<ArrayList<ArrayList<Integer>>>(Arrays.asList(new ArrayList<ArrayList<Integer>>(Arrays.asList(new ArrayList<Integer>(Arrays.asList(1, 2)))), new ArrayList<ArrayList<Integer>>(Arrays.asList(new ArrayList<Integer>(Arrays.asList(3, 4)))), new ArrayList<ArrayList<Integer>>(Arrays.asList(new ArrayList<Integer>(Arrays.asList(5, 6))))));
        ShoppingItem tempVar = new ShoppingItem();
        tempVar.Label = "milk";
        tempVar.Count = 1;
        ShoppingItem tempVar2 = new ShoppingItem();
        tempVar2.Label = "broccoli";
        tempVar2.Count = 2;
        ShoppingItem tempVar3 = new ShoppingItem();
        tempVar3.Label = "steak";
        tempVar3.Count = 6;
        t1.Shopping = new ArrayList<ShoppingItem>(Arrays.asList(tempVar, tempVar2, tempVar3));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: t1.Work = new List<Tuple<bool, ulong>> { Tuple.Create(false, 10000UL), Tuple.Create(true,
        // 49053UL), Tuple.Create(false, 53111UL)};
        t1.Work = new ArrayList<Tuple<Boolean, Long>>(Arrays.asList(Tuple.Create(false, 10000), Tuple.Create(true,
            49053), Tuple.Create(false, 53111)));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteTodo(tempReference_row, RowCursor.create(tempReference_row2, out _), t1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Todo t2 = this.ReadTodo(tempReference_row3, RowCursor.create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindAndDelete()
    public void FindAndDelete() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        ArrayList<UUID> expected = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), UUID.fromString("{B7BC39C2" +
            "-1A2D-4EAF-8F33-ED976872D876}"), UUID.fromString("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}")));

        for (java.lang.Iterable<UUID> permutation : expected.Permute()) {
            Todo t1 = new Todo();
            t1.Projects = new ArrayList<UUID>(permutation);

            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(row);
            Reference<RowBuffer> tempReference_row2 =
                new Reference<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            this.WriteTodo(tempReference_row, RowCursor.create(tempReference_row2, out _), t1);
            row = tempReference_row2.get();
            row = tempReference_row.get();

            // Attempt to update each item in turn and then update it with itself.
            Reference<RowBuffer> tempReference_row3 =
                new Reference<RowBuffer>(row);
            RowCursor root = RowCursor.create(tempReference_row3);
            row = tempReference_row3.get();
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            Reference<RowBuffer> tempReference_row4 =
                new Reference<RowBuffer>(row);
            RowCursor setScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out setScope).Find(tempReference_row4, c.Path);
            row = tempReference_row4.get();
            Reference<RowBuffer> tempReference_row5 =
                new Reference<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row5, ref setScope, out setScope));
            row = tempReference_row5.get();
            for (UUID p : t1.Projects) {
                Reference<RowBuffer> tempReference_row6 =
                    new Reference<RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row6, Utf8String.Empty);
                row = tempReference_row6.get();
                Reference<RowBuffer> tempReference_row7 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row7,
                    ref tempCursor, p));
                row = tempReference_row7.get();
                Reference<RowBuffer> tempReference_row8 =
                    new Reference<RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().Find(tempReference_row8, ref setScope, ref tempCursor,
                    out findScope));
                row = tempReference_row8.get();
                Reference<RowBuffer> tempReference_row9 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().DeleteSparse(tempReference_row9,
                    ref findScope));
                row = tempReference_row9.get();
            }
        }

    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindInSet()
    public void FindInSet() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        Todo t1 = new Todo();
        t1.Attendees = new ArrayList<String>(Arrays.asList("jason", "janice", "joshua"));
        t1.Prices = new ArrayList<ArrayList<Float>>(Arrays.asList(new ArrayList<Float>(Arrays.asList(1.2F, 3.0F)),
            new ArrayList<Float>(Arrays.asList(4.1F, 5.7F)), new ArrayList<Float>(Arrays.asList(7.3F, 8.12F, 9.14F))));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteTodo(tempReference_row, RowCursor.create(tempReference_row2, out _), t1);
        row = tempReference_row2.get();
        row = tempReference_row.get();

        // Attempt to find each item in turn.
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.create(tempReference_row3);
        row = tempReference_row3.get();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row5, ref setScope, out setScope));
        row = tempReference_row5.get();
        for (int i = 0; i < t1.Attendees.size(); i++) {
            Reference<RowBuffer> tempReference_row6 =
                new Reference<RowBuffer>(row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor).Find(tempReference_row6, Utf8String.Empty);
            row = tempReference_row6.get();
            Reference<RowBuffer> tempReference_row7 =
                new Reference<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row7, ref tempCursor,
                t1.Attendees.get(i)));
            row = tempReference_row7.get();
            Reference<RowBuffer> tempReference_row8 =
                new Reference<RowBuffer>(row);
            RowCursor findScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().Find(tempReference_row8, ref setScope, ref tempCursor,
                out findScope));
            row = tempReference_row8.get();
            Assert.AreEqual(i, findScope.Index, String.format("Failed to find t1.Attendees[%1$s]", i));
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row9, c.Path);
        row = tempReference_row9.get();
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row10, ref setScope, out setScope));
        row = tempReference_row10.get();
        TypeArgument innerType = c.TypeArgs[0];
        TypeArgument itemType = innerType.getTypeArgs().get(0).clone();
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        for (int i = 0; i < t1.Prices.size(); i++) {
            Reference<RowBuffer> tempReference_row11 =
                new Reference<RowBuffer>(row);
            RowCursor tempCursor1;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor1).Find(tempReference_row11, Utf8String.Empty);
            row = tempReference_row11.get();
            Reference<RowBuffer> tempReference_row12 =
                new Reference<RowBuffer>(row);
            RowCursor innerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(innerLayout.WriteScope(tempReference_row12, ref tempCursor1,
                innerType.getTypeArgs().clone(), out innerScope));
            row = tempReference_row12.get();
            for (int j = 0; j < t1.Prices.get(i).size(); j++) {
                Reference<RowBuffer> tempReference_row13 =
                    new Reference<RowBuffer>(row);
                RowCursor tempCursor2;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor2).Find(tempReference_row13, "prices.0.0");
                row = tempReference_row13.get();
                Reference<RowBuffer> tempReference_row14 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor2 =
                    new Reference<RowCursor>(tempCursor2);
                ResultAssert.IsSuccess(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempReference_row14,
                    tempReference_tempCursor2, t1.Prices.get(i).get(j)));
                tempCursor2 = tempReference_tempCursor2.get();
                row = tempReference_row14.get();
                Reference<RowBuffer> tempReference_row15 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_innerScope =
                    new Reference<RowCursor>(innerScope);
                Reference<RowCursor> tempReference_tempCursor22 =
                    new Reference<RowCursor>(tempCursor2);
                ResultAssert.IsSuccess(innerLayout.MoveField(tempReference_row15, tempReference_innerScope,
                    tempReference_tempCursor22));
                tempCursor2 = tempReference_tempCursor22.get();
                innerScope = tempReference_innerScope.get();
                row = tempReference_row15.get();
            }

            Reference<RowBuffer> tempReference_row16 =
                new Reference<RowBuffer>(row);
            RowCursor findScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().Find(tempReference_row16, ref setScope, ref tempCursor1,
                out findScope));
            row = tempReference_row16.get();
            Assert.AreEqual(i, findScope.Index, String.format("Failed to find t1.Prices[%1$s]", i));
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString(TypedSetUnitTests.SchemaFile);
        this.counterSchema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.counterSchema);
        this.layout = this.resolver.Resolve(tangible.ListHelper.find(this.counterSchema.getSchemas(),
            x -> x.Name.equals("Todo")).SchemaId);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUniquenessViolations()
    public void PreventUniquenessViolations() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        Todo t1 = new Todo();
        t1.Attendees = new ArrayList<String>(Arrays.asList("jason"));
        t1.Projects = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5-0421EE36F168}")));
        t1.Prices = new ArrayList<ArrayList<Float>>(Arrays.asList(new ArrayList<Float>(Arrays.asList(1.2F, 3.0F))));
        ShoppingItem tempVar = new ShoppingItem();
        tempVar.Label = "milk";
        tempVar.Count = 1;
        t1.Shopping = new ArrayList<ShoppingItem>(Arrays.asList(tempVar));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: t1.Work = new List<Tuple<bool, ulong>> { Tuple.Create(false, 10000UL)};
        t1.Work = new ArrayList<Tuple<Boolean, Long>>(Arrays.asList(Tuple.Create(false, 10000)));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteTodo(tempReference_row, RowCursor.create(tempReference_row2, out _), t1);
        row = tempReference_row2.get();
        row = tempReference_row.get();

        // Attempt to insert duplicate items in existing sets.
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.create(tempReference_row3);
        row = tempReference_row3.get();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row5, ref setScope, out setScope));
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row6, Utf8String.Empty);
        row = tempReference_row6.get();
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row7, ref tempCursor,
            t1.Attendees.get(0)));
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row8, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempReference_row8.get();
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row9, Utf8String.Empty);
        row = tempReference_row9.get();
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row10, ref tempCursor,
            t1.Attendees.get(0)));
        row = tempReference_row10.get();
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().Find(tempReference_row11, ref setScope, ref tempCursor, out _));
        row = tempReference_row11.get();
        Reference<RowBuffer> tempReference_row12 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row12, Utf8String.Empty);
        row = tempReference_row12.get();
        Reference<RowBuffer> tempReference_row13 =
            new Reference<RowBuffer>(row);
        String _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.NotFound(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(tempReference_row13, ref tempCursor, out _));
        row = tempReference_row13.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("projects", out c);
        Reference<RowBuffer> tempReference_row14 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row14, c.Path);
        row = tempReference_row14.get();
        Reference<RowBuffer> tempReference_row15 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row15, ref setScope, out setScope));
        row = tempReference_row15.get();
        Reference<RowBuffer> tempReference_row16 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row16, Utf8String.Empty);
        row = tempReference_row16.get();
        Reference<RowBuffer> tempReference_row17 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row17, ref tempCursor,
            t1.Projects.get(0)));
        row = tempReference_row17.get();
        Reference<RowBuffer> tempReference_row18 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row18, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempReference_row18.get();

        // Attempt to move a duplicate set into a set of sets.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        Reference<RowBuffer> tempReference_row19 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row19, c.Path);
        row = tempReference_row19.get();
        Reference<RowBuffer> tempReference_row20 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row20, ref setScope, out setScope));
        row = tempReference_row20.get();
        TypeArgument innerType = c.TypeArgs[0];
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        Reference<RowBuffer> tempReference_row21 =
            new Reference<RowBuffer>(row);
        RowCursor tempCursor1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor1).Find(tempReference_row21, Utf8String.Empty);
        row = tempReference_row21.get();
        Reference<RowBuffer> tempReference_row22 =
            new Reference<RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(innerLayout.WriteScope(tempReference_row22, ref tempCursor1, innerType.getTypeArgs().clone()
            , out innerScope));
        row = tempReference_row22.get();
        for (float innerItem : t1.Prices.get(0)) {
            LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
            Reference<RowBuffer> tempReference_row23 =
                new Reference<RowBuffer>(row);
            RowCursor tempCursor2;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor2).Find(tempReference_row23, "prices.0.0");
            row = tempReference_row23.get();
            Reference<RowBuffer> tempReference_row24 =
                new Reference<RowBuffer>(row);
            Reference<RowCursor> tempReference_tempCursor2 =
                new Reference<RowCursor>(tempCursor2);
            ResultAssert.IsSuccess(itemLayout.WriteSparse(tempReference_row24, tempReference_tempCursor2, innerItem));
            tempCursor2 = tempReference_tempCursor2.get();
            row = tempReference_row24.get();
            Reference<RowBuffer> tempReference_row25 =
                new Reference<RowBuffer>(row);
            Reference<RowCursor> tempReference_innerScope =
                new Reference<RowCursor>(innerScope);
            Reference<RowCursor> tempReference_tempCursor22 =
                new Reference<RowCursor>(tempCursor2);
            ResultAssert.IsSuccess(innerLayout.MoveField(tempReference_row25, tempReference_innerScope, tempReference_tempCursor22));
            tempCursor2 = tempReference_tempCursor22.get();
            innerScope = tempReference_innerScope.get();
            row = tempReference_row25.get();
        }

        Reference<RowBuffer> tempReference_row26 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row26, ref setScope, ref tempCursor1,
            UpdateOptions.Insert));
        row = tempReference_row26.get();

        // Attempt to move a duplicate UDT into a set of UDT.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("shopping", out c);
        Reference<RowBuffer> tempReference_row27 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row27, c.Path);
        row = tempReference_row27.get();
        Reference<RowBuffer> tempReference_row28 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row28, ref setScope, out setScope));
        row = tempReference_row28.get();
        LayoutUDT udtLayout = c.TypeArgs[0].Type.<LayoutUDT>TypeAs();
        Reference<RowBuffer> tempReference_row29 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row29, Utf8String.Empty);
        row = tempReference_row29.get();
        Reference<RowBuffer> tempReference_row30 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor =
            new Reference<RowCursor>(tempCursor);
        RowCursor udtScope;
        Out<RowCursor> tempOut_udtScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(udtLayout.WriteScope(tempReference_row30, tempReference_tempCursor, c.TypeArgs[0].TypeArgs,
            tempOut_udtScope));
        udtScope = tempOut_udtScope.get();
        tempCursor = tempReference_tempCursor.get();
        row = tempReference_row30.get();
        Reference<RowBuffer> tempReference_row31 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_udtScope =
            new Reference<RowCursor>(udtScope);
        TypedSetUnitTests.WriteShoppingItem(tempReference_row31, tempReference_udtScope, c.TypeArgs[0].TypeArgs,
            t1.Shopping.get(0));
        udtScope = tempReference_udtScope.get();
        row = tempReference_row31.get();
        Reference<RowBuffer> tempReference_row32 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row32, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempReference_row32.get();

        // Attempt to move a duplicate tuple into a set of tuple.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("work", out c);
        Reference<RowBuffer> tempReference_row33 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempReference_row33, c.Path);
        row = tempReference_row33.get();
        Reference<RowBuffer> tempReference_row34 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row34, ref setScope, out setScope));
        row = tempReference_row34.get();
        innerType = c.TypeArgs[0];
        LayoutIndexedScope tupleLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
        Reference<RowBuffer> tempReference_row35 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row35, Utf8String.Empty);
        row = tempReference_row35.get();
        Reference<RowBuffer> tempReference_row36 =
            new Reference<RowBuffer>(row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(tupleLayout.WriteScope(tempReference_row36, ref tempCursor, innerType.getTypeArgs().clone(),
            out tupleScope));
        row = tempReference_row36.get();
        Reference<RowBuffer> tempReference_row37 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tupleScope =
            new Reference<RowCursor>(tupleScope);
        ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(tempReference_row37, tempReference_tupleScope, t1.Work.get(0).Item1));
        tupleScope = tempReference_tupleScope.get();
        row = tempReference_row37.get();
        Reference<RowBuffer> tempReference_row38 =
            new Reference<RowBuffer>(row);
        assert tupleScope.MoveNext(tempReference_row38);
        row = tempReference_row38.get();
        Reference<RowBuffer> tempReference_row39 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tupleScope2 =
            new Reference<RowCursor>(tupleScope);
        ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().WriteSparse(tempReference_row39, tempReference_tupleScope2, t1.Work.get(0).Item2));
        tupleScope = tempReference_tupleScope2.get();
        row = tempReference_row39.get();
        Reference<RowBuffer> tempReference_row40 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row40, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempReference_row40.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUpdatesInNonUpdatableScope()
    public void PreventUpdatesInNonUpdatableScope() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        // Write a set and then try to write directly into it.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.create(tempReference_row, out setScope).Find(tempReference_row2, c.Path);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(tempReference_row3, ref setScope, c.TypeArgs,
            out setScope));
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row4,
            ref setScope, "foo"));
        row = tempReference_row4.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.create(tempReference_row5, out tempCursor).Find(tempReference_row6, Utf8String.Empty);
        row = tempReference_row6.get();
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row7, ref tempCursor, "foo"
        ));
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row8, ref setScope, ref tempCursor));
        row = tempReference_row8.get();
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row9,
            ref setScope, "foo"));
        row = tempReference_row9.get();
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().DeleteSparse(tempReference_row10, ref setScope));
        row = tempReference_row10.get();

        // Write a set of sets, successfully insert an empty set into it, and then try to write directly to the inner
        // set.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row12 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.create(tempReference_row11, out setScope).Find(tempReference_row12, c.Path);
        row = tempReference_row12.get();
        row = tempReference_row11.get();
        Reference<RowBuffer> tempReference_row13 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(tempReference_row13, ref setScope, c.TypeArgs,
            out setScope));
        row = tempReference_row13.get();
        TypeArgument innerType = c.TypeArgs[0];
        TypeArgument itemType = innerType.getTypeArgs().get(0).clone();
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        Reference<RowBuffer> tempReference_row14 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row15 =
            new Reference<RowBuffer>(row);
        RowCursor tempCursor1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.create(tempReference_row14, out tempCursor1).Find(tempReference_row15, "prices.0");
        row = tempReference_row15.get();
        row = tempReference_row14.get();
        Reference<RowBuffer> tempReference_row16 =
            new Reference<RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(innerLayout.WriteScope(tempReference_row16, ref tempCursor1, innerType.getTypeArgs().clone()
            , out innerScope));
        row = tempReference_row16.get();
        Reference<RowBuffer> tempReference_row17 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row18 =
            new Reference<RowBuffer>(row);
        RowCursor tempCursor2;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.create(tempReference_row17, out tempCursor2).Find(tempReference_row18, "prices.0.0");
        row = tempReference_row18.get();
        row = tempReference_row17.get();
        Reference<RowBuffer> tempReference_row19 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor2 =
            new Reference<RowCursor>(tempCursor2);
        ResultAssert.IsSuccess(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempReference_row19,
            tempReference_tempCursor2, 1.0F));
        tempCursor2 = tempReference_tempCursor2.get();
        row = tempReference_row19.get();
        Reference<RowBuffer> tempReference_row20 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_innerScope =
            new Reference<RowCursor>(innerScope);
        Reference<RowCursor> tempReference_tempCursor22 =
            new Reference<RowCursor>(tempCursor2);
        ResultAssert.IsSuccess(innerLayout.MoveField(tempReference_row20, tempReference_innerScope, tempReference_tempCursor22));
        tempCursor2 = tempReference_tempCursor22.get();
        innerScope = tempReference_innerScope.get();
        row = tempReference_row20.get();
        Reference<RowBuffer> tempReference_row21 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row21, ref setScope, ref tempCursor1));
        row = tempReference_row21.get();
        Reference<RowBuffer> tempReference_row22 =
            new Reference<RowBuffer>(row);
        assert setScope.MoveNext(tempReference_row22);
        row = tempReference_row22.get();
        Reference<RowBuffer> tempReference_row23 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_setScope =
            new Reference<RowCursor>(setScope);
        Out<RowCursor> tempOut_innerScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(innerLayout.ReadScope(tempReference_row23, tempReference_setScope, tempOut_innerScope));
        innerScope = tempOut_innerScope.get();
        setScope = tempReference_setScope.get();
        row = tempReference_row23.get();
        Reference<RowBuffer> tempReference_row24 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_innerScope2 =
            new Reference<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempReference_row24,
            tempReference_innerScope2, 1.0F));
        innerScope = tempReference_innerScope2.get();
        row = tempReference_row24.get();
        Reference<RowBuffer> tempReference_row25 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_innerScope3 =
            new Reference<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutFloat32>TypeAs().DeleteSparse(tempReference_row25,
            tempReference_innerScope3));
        innerScope = tempReference_innerScope3.get();
        row = tempReference_row25.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void RowWriterTest()
    public void RowWriterTest() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);

        ArrayList<UUID> expected = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), UUID.fromString("{B7BC39C2" +
            "-1A2D-4EAF-8F33-ED976872D876}"), UUID.fromString("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}")));

        for (java.lang.Iterable<UUID> permutation : expected.Permute()) {
            Todo t1 = new Todo();
            t1.Projects = new ArrayList<UUID>(permutation);

            row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);
            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(row);
            ResultAssert.IsSuccess(RowWriter.WriteBuffer(tempReference_row, t1, TypedSetUnitTests.SerializeTodo));
            row = tempReference_row.get();

            // Update the existing Set by updating each item with itself.  This ensures that the RowWriter has
            // maintained the unique index correctly.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            Reference<RowBuffer> tempReference_row2 =
                new Reference<RowBuffer>(row);
            RowCursor root;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            RowCursor.create(tempReference_row2, out root);
            row = tempReference_row2.get();
            Reference<RowBuffer> tempReference_row3 =
                new Reference<RowBuffer>(row);
            RowCursor projScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out projScope).Find(tempReference_row3, c.Path);
            row = tempReference_row3.get();
            Reference<RowBuffer> tempReference_row4 =
                new Reference<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row4, ref projScope, out projScope));
            row = tempReference_row4.get();
            for (UUID item : t1.Projects) {
                Reference<RowBuffer> tempReference_row5 =
                    new Reference<RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row5, Utf8String.Empty);
                row = tempReference_row5.get();
                Reference<RowBuffer> tempReference_row6 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row6,
                    ref tempCursor, item));
                row = tempReference_row6.get();
                Reference<RowBuffer> tempReference_row7 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row7, ref projScope,
                    ref tempCursor));
                row = tempReference_row7.get();
            }

            Reference<RowBuffer> tempReference_row8 =
                new Reference<RowBuffer>(row);
            Reference<RowBuffer> tempReference_row9 =
                new Reference<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            Todo t2 = this.ReadTodo(tempReference_row8, RowCursor.create(tempReference_row9, out _));
            row = tempReference_row9.get();
            row = tempReference_row8.get();
            assert t1 == t2;
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void UpdateInSet()
    public void UpdateInSet() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

        ArrayList<UUID> expected = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), UUID.fromString("{B7BC39C2" +
            "-1A2D-4EAF-8F33-ED976872D876}"), UUID.fromString("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}")));

        for (java.lang.Iterable<UUID> permutation : expected.Permute()) {
            Todo t1 = new Todo();
            t1.Projects = new ArrayList<UUID>(permutation);

            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(row);
            Reference<RowBuffer> tempReference_row2 =
                new Reference<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            this.WriteTodo(tempReference_row, RowCursor.create(tempReference_row2, out _), t1);
            row = tempReference_row2.get();
            row = tempReference_row.get();

            // Attempt to find each item in turn and then delete it.
            Reference<RowBuffer> tempReference_row3 =
                new Reference<RowBuffer>(row);
            RowCursor root = RowCursor.create(tempReference_row3);
            row = tempReference_row3.get();
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            Reference<RowBuffer> tempReference_row4 =
                new Reference<RowBuffer>(row);
            RowCursor setScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out setScope).Find(tempReference_row4, c.Path);
            row = tempReference_row4.get();
            Reference<RowBuffer> tempReference_row5 =
                new Reference<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().ReadScope(tempReference_row5, ref setScope, out setScope));
            row = tempReference_row5.get();
            for (UUID elm : t1.Projects) {
                // Verify it is already there.
                Reference<RowBuffer> tempReference_row6 =
                    new Reference<RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row6, Utf8String.Empty);
                row = tempReference_row6.get();
                Reference<RowBuffer> tempReference_row7 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row7,
                    ref tempCursor, elm));
                row = tempReference_row7.get();
                Reference<RowBuffer> tempReference_row8 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
                // following line:
                //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref
                // tempCursor, value: out RowCursor _));
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().Find(tempReference_row8, ref setScope, ref tempCursor,
                    value:
                out RowCursor _))
                row = tempReference_row8.get();

                // Insert it again with update.
                Reference<RowBuffer> tempReference_row9 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row9, Utf8String.Empty);
                row = tempReference_row9.get();
                Reference<RowBuffer> tempReference_row10 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row10,
                    ref tempCursor, elm));
                row = tempReference_row10.get();
                Reference<RowBuffer> tempReference_row11 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row11, ref setScope,
                    ref tempCursor, UpdateOptions.Update));
                row = tempReference_row11.get();

                // Insert it again with upsert.
                Reference<RowBuffer> tempReference_row12 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row12, Utf8String.Empty);
                row = tempReference_row12.get();
                Reference<RowBuffer> tempReference_row13 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row13,
                    ref tempCursor, elm));
                row = tempReference_row13.get();
                Reference<RowBuffer> tempReference_row14 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row14, ref setScope,
                    ref tempCursor));
                row = tempReference_row14.get();

                // Insert it again with insert (fail: exists).
                Reference<RowBuffer> tempReference_row15 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row15, Utf8String.Empty);
                row = tempReference_row15.get();
                Reference<RowBuffer> tempReference_row16 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row16,
                    ref tempCursor, elm));
                row = tempReference_row16.get();
                Reference<RowBuffer> tempReference_row17 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.Exists(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row17, ref setScope,
                    ref tempCursor, UpdateOptions.Insert));
                row = tempReference_row17.get();

                // Insert it again with insert at (fail: disallowed).
                Reference<RowBuffer> tempReference_row18 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row18, Utf8String.Empty);
                row = tempReference_row18.get();
                Reference<RowBuffer> tempReference_row19 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempReference_row19,
                    ref tempCursor, elm));
                row = tempReference_row19.get();
                Reference<RowBuffer> tempReference_row20 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.TypeConstraint(c.<LayoutUniqueScope>typeAs().MoveField(tempReference_row20, ref setScope,
                    ref tempCursor, UpdateOptions.InsertAt));
                row = tempReference_row20.get();
            }
        }
    }

    private static ShoppingItem ReadShoppingItem(Reference<RowBuffer> row, Reference<RowCursor> matchScope) {
        Layout matchLayout = matchScope.get().getLayout();
        ShoppingItem m = new ShoppingItem();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("label", out c);
        Out<String> tempOut_Label = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>typeAs().ReadVariable(row, matchScope, c, tempOut_Label));
        m.Label = tempOut_Label.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("count", out c);
        Out<Byte> tempOut_Count = new Out<Byte>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeAs<LayoutUInt8>().ReadFixed(ref row, ref matchScope, c, out m.Count));
        ResultAssert.IsSuccess(c.<LayoutUInt8>typeAs().ReadFixed(row, matchScope, c, tempOut_Count));
        m.Count = tempOut_Count.get();
        return m;
    }

    private Todo ReadTodo(Reference<RowBuffer> row, Reference<RowCursor> root) {
        Todo value = new Todo();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        RowCursor tagsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out tagsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref tagsScope, out tagsScope) == Result.SUCCESS) {
            value.Attendees = new ArrayList<String>();
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
                value.Attendees.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("projects", out c);
        RowCursor projScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out projScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref projScope, out projScope) == Result.SUCCESS) {
            value.Projects = new ArrayList<UUID>();
            while (projScope.MoveNext(row)) {
                java.util.UUID item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().ReadSparse(row, ref projScope,
                    out item));
                value.Projects.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("checkboxes", out c);
        RowCursor checkboxScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out checkboxScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref checkboxScope, out checkboxScope) == Result.SUCCESS) {
            value.Checkboxes = new ArrayList<Boolean>();
            while (checkboxScope.MoveNext(row)) {
                boolean item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutBoolean>TypeAs().ReadSparse(row, ref checkboxScope,
                    out item));
                value.Checkboxes.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        RowCursor pricesScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out pricesScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref pricesScope, out pricesScope) == Result.SUCCESS) {
            value.Prices = new ArrayList<ArrayList<Float>>();
            TypeArgument innerType = c.TypeArgs[0];
            LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
            while (pricesScope.MoveNext(row)) {
                ArrayList<Float> item = new ArrayList<Float>();
                Reference<RowCursor> tempReference_pricesScope =
                    new Reference<RowCursor>(pricesScope);
                RowCursor innerScope;
                Out<RowCursor> tempOut_innerScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempReference_pricesScope, tempOut_innerScope));
                innerScope = tempOut_innerScope.get();
                pricesScope = tempReference_pricesScope.get();
                while (innerScope.MoveNext(row)) {
                    LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
                    Reference<RowCursor> tempReference_innerScope =
                        new Reference<RowCursor>(innerScope);
                    float innerItem;
                    Out<Float> tempOut_innerItem = new Out<Float>();
                    ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempReference_innerScope, tempOut_innerItem));
                    innerItem = tempOut_innerItem.get();
                    innerScope = tempReference_innerScope.get();
                    item.add(innerItem);
                }

                value.Prices.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nested", out c);
        RowCursor nestedScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out nestedScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref nestedScope, out nestedScope) == Result.SUCCESS) {
            value.Nested = new ArrayList<ArrayList<ArrayList<Integer>>>();
            TypeArgument in2Type = c.TypeArgs[0];
            LayoutUniqueScope in2Layout = in2Type.getType().<LayoutUniqueScope>TypeAs();
            while (nestedScope.MoveNext(row)) {
                ArrayList<ArrayList<Integer>> item = new ArrayList<ArrayList<Integer>>();
                Reference<RowCursor> tempReference_nestedScope =
                    new Reference<RowCursor>(nestedScope);
                RowCursor in2Scope;
                Out<RowCursor> tempOut_in2Scope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(in2Layout.ReadScope(row, tempReference_nestedScope, tempOut_in2Scope));
                in2Scope = tempOut_in2Scope.get();
                nestedScope = tempReference_nestedScope.get();
                while (in2Scope.MoveNext(row)) {
                    TypeArgument in3Type = in2Type.getTypeArgs().get(0).clone();
                    LayoutUniqueScope in3Layout = in3Type.getType().<LayoutUniqueScope>TypeAs();
                    ArrayList<Integer> item2 = new ArrayList<Integer>();
                    Reference<RowCursor> tempReference_in2Scope =
                        new Reference<RowCursor>(in2Scope);
                    RowCursor in3Scope;
                    Out<RowCursor> tempOut_in3Scope =
                        new Out<RowCursor>();
                    ResultAssert.IsSuccess(in3Layout.ReadScope(row, tempReference_in2Scope, tempOut_in3Scope));
                    in3Scope = tempOut_in3Scope.get();
                    in2Scope = tempReference_in2Scope.get();
                    while (in3Scope.MoveNext(row)) {
                        LayoutInt32 itemLayout = in3Type.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs();
                        Reference<RowCursor> tempReference_in3Scope = new Reference<RowCursor>(in3Scope);
                        int innerItem;
                        Out<Integer> tempOut_innerItem2 = new Out<Integer>();
                        ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempReference_in3Scope, tempOut_innerItem2));
                        innerItem = tempOut_innerItem2.get();
                        in3Scope = tempReference_in3Scope.get();
                        item2.add(innerItem);
                    }

                    item.add(item2);
                }

                value.Nested.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("shopping", out c);
        RowCursor shoppingScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out shoppingScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref shoppingScope, out shoppingScope) == Result.SUCCESS) {
            value.Shopping = new ArrayList<ShoppingItem>();
            while (shoppingScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                Reference<RowCursor> tempReference_shoppingScope =
                    new Reference<RowCursor>(shoppingScope);
                RowCursor matchScope;
                Out<RowCursor> tempOut_matchScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempReference_shoppingScope, tempOut_matchScope));
                matchScope = tempOut_matchScope.get();
                shoppingScope = tempReference_shoppingScope.get();
                Reference<RowCursor> tempReference_matchScope =
                    new Reference<RowCursor>(matchScope);
                ShoppingItem item = TypedSetUnitTests.ReadShoppingItem(row, tempReference_matchScope);
                matchScope = tempReference_matchScope.get();
                value.Shopping.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("work", out c);
        RowCursor workScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out workScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>typeAs().ReadScope(row, ref workScope, out workScope) == Result.SUCCESS) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: value.Work = new List<Tuple<bool, ulong>>();
            value.Work = new ArrayList<Tuple<Boolean, Long>>();
            while (workScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();

                Reference<RowCursor> tempReference_workScope = new Reference<RowCursor>(workScope);
                RowCursor tupleScope;
                Out<RowCursor> tempOut_tupleScope = new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempReference_workScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.get();
                workScope = tempReference_workScope.get();
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope = new Reference<RowCursor>(tupleScope);
                boolean item1;
                Out<Boolean> tempOut_item1 = new Out<Boolean>();
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().ReadSparse(row, tempReference_tupleScope, tempOut_item1));
                item1 = tempOut_item1.get();
                tupleScope = tempReference_tupleScope.get();
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope2 = new Reference<RowCursor>(tupleScope);
                long item2;
                Out<Long> tempOut_item2 = new Out<Long>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutVarUInt>().ReadSparse(ref row, ref tupleScope, out ulong item2));
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().ReadSparse(row, tempReference_tupleScope2, tempOut_item2));
                item2 = tempOut_item2.get();
                tupleScope = tempReference_tupleScope2.get();
                value.Work.add(Tuple.Create(item1, item2));
            }
        }

        return value;
    }

    private static Result SerializeTodo(Reference<RowWriter> writer, TypeArgument typeArg, Todo value) {
        if (value.Projects != null) {
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert writer.get().getLayout().TryFind("projects", out c);
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are
            // not converted by C# to Java Converter:
            Result r = writer.get().WriteScope("projects", c.TypeArg, value.Projects, (ref RowWriter writer2,
                                                                                       TypeArgument typeArg2, ArrayList<UUID> value2) ->
            {
                for (UUID item : value2) {
                    ResultAssert.IsSuccess(writer2.WriteGuid(null, item));
                }

                return Result.SUCCESS;
            });
            return r;
        }

        return Result.SUCCESS;
    }

    private static void WriteShoppingItem(Reference<RowBuffer> row, Reference<RowCursor> matchScope, TypeArgumentList typeArgs, ShoppingItem m) {
        Layout matchLayout = row.get().resolver().resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("label", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>typeAs().WriteVariable(row, matchScope, c, m.Label));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("count", out c);
        ResultAssert.IsSuccess(c.<LayoutUInt8>typeAs().WriteFixed(row, matchScope, c, m.Count));
    }

    private void WriteTodo(Reference<RowBuffer> row, Reference<RowCursor> root, Todo value) {
        LayoutColumn c;

        if (value.Attendees != null) {
            Out<LayoutColumn> tempOut_c =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("attendees", tempOut_c);
            c = tempOut_c.get();
            RowCursor attendScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out attendScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref attendScope,
                c.typeArgs().clone(), out attendScope));
            for (String item : value.Attendees) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.typeArgs().get(0).type().<LayoutUtf8>typeAs().WriteSparse(row,
                    ref tempCursor, item));
                Reference<RowCursor> tempReference_attendScope =
                    new Reference<RowCursor>(attendScope);
                Reference<RowCursor> tempReference_tempCursor =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_attendScope,
                    tempReference_tempCursor));
                tempCursor = tempReference_tempCursor.get();
                attendScope = tempReference_attendScope.get();
            }
        }

        if (value.Projects != null) {
            Out<LayoutColumn> tempOut_c2 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("projects", tempOut_c2);
            c = tempOut_c2.get();
            RowCursor projScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out projScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref projScope,
                c.typeArgs().clone(), out projScope));
            for (UUID item : value.Projects) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                Reference<RowCursor> tempReference_tempCursor2 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.typeArgs().get(0).type().<LayoutGuid>typeAs().WriteSparse(row,
                    tempReference_tempCursor2, item));
                tempCursor = tempReference_tempCursor2.get();
                Reference<RowCursor> tempReference_projScope =
                    new Reference<RowCursor>(projScope);
                Reference<RowCursor> tempReference_tempCursor3 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_projScope,
                    tempReference_tempCursor3));
                tempCursor = tempReference_tempCursor3.get();
                projScope = tempReference_projScope.get();
            }
        }

        if (value.Checkboxes != null) {
            Out<LayoutColumn> tempOut_c3 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("checkboxes", tempOut_c3);
            c = tempOut_c3.get();
            RowCursor checkboxScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out checkboxScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref checkboxScope,
                c.typeArgs().clone(), out checkboxScope));
            for (boolean item : value.Checkboxes) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                Reference<RowCursor> tempReference_tempCursor4 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.typeArgs().get(0).type().<LayoutBoolean>typeAs().WriteSparse(row,
                    tempReference_tempCursor4, item));
                tempCursor = tempReference_tempCursor4.get();
                Reference<RowCursor> tempReference_checkboxScope =
                    new Reference<RowCursor>(checkboxScope);
                Reference<RowCursor> tempReference_tempCursor5 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_checkboxScope,
                    tempReference_tempCursor5));
                tempCursor = tempReference_tempCursor5.get();
                checkboxScope = tempReference_checkboxScope.get();
            }
        }

        if (value.Prices != null) {
            Out<LayoutColumn> tempOut_c4 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("prices", tempOut_c4);
            c = tempOut_c4.get();
            RowCursor pricesScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out pricesScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref pricesScope,
                c.typeArgs().clone(), out pricesScope));
            for (ArrayList<Float> item : value.Prices) {
                assert item != null;
                TypeArgument innerType = c.typeArgs().get(0).clone();
                LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor1).Find(row, "prices.0");
                RowCursor innerScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref tempCursor1, innerType.getTypeArgs().clone(),
                    out innerScope));
                for (float innerItem : item) {
                    LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
                    RowCursor tempCursor2;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'Out' helper class unless the method is within the
                    // code being modified:
                    root.get().Clone(out tempCursor2).Find(row, "prices.0.0");
                    Reference<RowCursor> tempReference_tempCursor2
                        = new Reference<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempReference_tempCursor2, innerItem));
                    tempCursor2 = tempReference_tempCursor2.get();
                    Reference<RowCursor> tempReference_innerScope =
                        new Reference<RowCursor>(innerScope);
                    Reference<RowCursor> tempReference_tempCursor22 = new Reference<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(innerLayout.MoveField(row, tempReference_innerScope, tempReference_tempCursor22));
                    tempCursor2 = tempReference_tempCursor22.get();
                    innerScope = tempReference_innerScope.get();
                }

                Reference<RowCursor> tempReference_pricesScope =
                    new Reference<RowCursor>(pricesScope);
                Reference<RowCursor> tempReference_tempCursor1 =
                    new Reference<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_pricesScope,
                    tempReference_tempCursor1));
                tempCursor1 = tempReference_tempCursor1.get();
                pricesScope = tempReference_pricesScope.get();
            }
        }

        if (value.Nested != null) {
            Out<LayoutColumn> tempOut_c5 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("nested", tempOut_c5);
            c = tempOut_c5.get();
            RowCursor nestedScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out nestedScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref nestedScope,
                c.typeArgs().clone(), out nestedScope));
            for (ArrayList<ArrayList<Integer>> item : value.Nested) {
                assert item != null;
                TypeArgument in2Type = c.typeArgs().get(0).clone();
                LayoutUniqueScope in2Layout = in2Type.getType().<LayoutUniqueScope>TypeAs();
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor1).Find(row, "prices.0");
                RowCursor in2Scope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(in2Layout.WriteScope(row, ref tempCursor1, in2Type.getTypeArgs().clone(),
                    out in2Scope));
                for (ArrayList<Integer> item2 : item) {
                    assert item2 != null;
                    TypeArgument in3Type = in2Type.getTypeArgs().get(0).clone();
                    LayoutUniqueScope in3Layout = in3Type.getType().<LayoutUniqueScope>TypeAs();
                    RowCursor tempCursor2;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'Out' helper class unless the method is within the
                    // code being modified:
                    root.get().Clone(out tempCursor2).Find(row, "prices.0.0");
                    RowCursor in3Scope;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'Out' helper class unless the method is within the
                    // code being modified:
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword
                    // - these cannot be converted using the 'Ref' helper class unless the method is within the
                    // code being modified:
                    ResultAssert.IsSuccess(in3Layout.WriteScope(row, ref tempCursor2, in3Type.getTypeArgs().clone(),
                        out in3Scope));
                    for (int innerItem : item2) {
                        LayoutInt32 itemLayout = in3Type.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs();
                        RowCursor tempCursor3;
                        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out'
                        // keyword - these cannot be converted using the 'Out' helper class unless the method
                        // is within the code being modified:
                        root.get().Clone(out tempCursor3).Find(row, "prices.0.0.0");
                        Reference<RowCursor> tempReference_tempCursor3 = new Reference<RowCursor>(tempCursor3);
                        ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempReference_tempCursor3, innerItem));
                        tempCursor3 = tempReference_tempCursor3.get();
                        Reference<RowCursor> tempReference_in3Scope = new Reference<RowCursor>(in3Scope);
                        Reference<RowCursor> tempReference_tempCursor32 = new Reference<RowCursor>(tempCursor3);
                        ResultAssert.IsSuccess(in3Layout.MoveField(row, tempReference_in3Scope, tempReference_tempCursor32));
                        tempCursor3 = tempReference_tempCursor32.get();
                        in3Scope = tempReference_in3Scope.get();
                    }

                    Reference<RowCursor> tempReference_in2Scope =
                        new Reference<RowCursor>(in2Scope);
                    Reference<RowCursor> tempReference_tempCursor23 = new Reference<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(in2Layout.MoveField(row, tempReference_in2Scope, tempReference_tempCursor23));
                    tempCursor2 = tempReference_tempCursor23.get();
                    in2Scope = tempReference_in2Scope.get();
                }

                Reference<RowCursor> tempReference_nestedScope =
                    new Reference<RowCursor>(nestedScope);
                Reference<RowCursor> tempReference_tempCursor12 =
                    new Reference<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_nestedScope,
                    tempReference_tempCursor12));
                tempCursor1 = tempReference_tempCursor12.get();
                nestedScope = tempReference_nestedScope.get();
            }
        }

        if (value.Shopping != null) {
            Out<LayoutColumn> tempOut_c6 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("shopping", tempOut_c6);
            c = tempOut_c6.get();
            RowCursor shoppingScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out shoppingScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref shoppingScope,
                c.typeArgs().clone(), out shoppingScope));
            for (ShoppingItem item : value.Shopping) {
                TypeArgument innerType = c.typeArgs().get(0).clone();
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                Reference<RowCursor> tempReference_tempCursor6 =
                    new Reference<RowCursor>(tempCursor);
                RowCursor itemScope;
                Out<RowCursor> tempOut_itemScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempReference_tempCursor6,
                    innerType.getTypeArgs().clone(), tempOut_itemScope));
                itemScope = tempOut_itemScope.get();
                tempCursor = tempReference_tempCursor6.get();
                Reference<RowCursor> tempReference_itemScope =
                    new Reference<RowCursor>(itemScope);
                TypedSetUnitTests.WriteShoppingItem(row, tempReference_itemScope, innerType.getTypeArgs().clone(), item);
                itemScope = tempReference_itemScope.get();
                Reference<RowCursor> tempReference_shoppingScope =
                    new Reference<RowCursor>(shoppingScope);
                Reference<RowCursor> tempReference_tempCursor7 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_shoppingScope,
                    tempReference_tempCursor7));
                tempCursor = tempReference_tempCursor7.get();
                shoppingScope = tempReference_shoppingScope.get();
            }
        }

        if (value.Work != null) {
            Out<LayoutColumn> tempOut_c7 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("work", tempOut_c7);
            c = tempOut_c7.get();
            RowCursor workScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out workScope).Find(row, c.path());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().WriteScope(row, ref workScope,
                c.typeArgs().clone(), out workScope));
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: foreach (Tuple<bool, ulong> item in value.Work)
            for (Tuple<Boolean, Long> item : value.Work) {
                TypeArgument innerType = c.typeArgs().get(0).clone();
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref tempCursor, innerType.getTypeArgs().clone(),
                    out tupleScope));
                Reference<RowCursor> tempReference_tupleScope =
                    new Reference<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(row, tempReference_tupleScope, item.Item1));
                tupleScope = tempReference_tupleScope.get();
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope2 =
                    new Reference<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().WriteSparse(row, tempReference_tupleScope2, item.Item2));
                tupleScope = tempReference_tupleScope2.get();
                Reference<RowCursor> tempReference_workScope =
                    new Reference<RowCursor>(workScope);
                Reference<RowCursor> tempReference_tempCursor8 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>typeAs().MoveField(row, tempReference_workScope,
                    tempReference_tempCursor8));
                tempCursor = tempReference_tempCursor8.get();
                workScope = tempReference_workScope.get();
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class ShoppingItem
    private final static class ShoppingItem {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public byte Count;
        public byte Count;
        public String Label;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof ShoppingItem;
            ShoppingItem shoppingItem = tempVar ? (ShoppingItem)obj : null;
            return tempVar && this.equals(shoppingItem);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: return (this.Label.GetHashCode() * 397) ^ this.Count.GetHashCode();
                return (this.Label.hashCode() * 397) ^ (new Byte(this.Count)).hashCode();
            }
        }

        private boolean equals(ShoppingItem other) {
            return this.Label.equals(other.Label) && this.Count == other.Count;
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class Todo
    private final static class Todo {
        public ArrayList<String> Attendees;
        public ArrayList<Boolean> Checkboxes;
        public ArrayList<ArrayList<ArrayList<Integer>>> Nested;
        public ArrayList<ArrayList<Float>> Prices;
        public ArrayList<UUID> Projects;
        public ArrayList<ShoppingItem> Shopping;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public List<Tuple<bool, ulong>> Work;
        public ArrayList<Tuple<Boolean, Long>> Work;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Todo;
            Todo todo = tempVar ? (Todo)obj : null;
            return tempVar && this.equals(todo);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                int hashCode = 0;
                hashCode = (hashCode * 397) ^ (this.Attendees == null ? null : this.Attendees.hashCode() != null ? this.Attendees.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Projects == null ? null : this.Projects.hashCode() != null ? this.Projects.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Checkboxes == null ? null : this.Checkboxes.hashCode() != null ? this.Checkboxes.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Prices == null ? null : this.Prices.hashCode() != null ? this.Prices.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Nested == null ? null : this.Nested.hashCode() != null ? this.Nested.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Shopping == null ? null : this.Shopping.hashCode() != null ? this.Shopping.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Work == null ? null : this.Work.hashCode() != null ? this.Work.hashCode() : 0);
                return hashCode;
            }
        }

        private static <T> boolean NestedNestedSetEquals(ArrayList<ArrayList<ArrayList<T>>> left, ArrayList<ArrayList<ArrayList<T>>> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (int i = 0; i < left.size(); i++) {
                if (!Todo.NestedSetEquals(left.get(i), right.get(i))) {
                    return false;
                }
            }

            return true;
        }

        private static <T> boolean NestedSetEquals(ArrayList<ArrayList<T>> left, ArrayList<ArrayList<T>> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (int i = 0; i < left.size(); i++) {
                if (!Todo.SetEquals(left.get(i), right.get(i))) {
                    return false;
                }
            }

            return true;
        }

        private static <T> boolean SetEquals(ArrayList<T> left, ArrayList<T> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (T item : left) {
                if (!right.contains(item)) {
                    return false;
                }
            }

            return true;
        }

        private boolean equals(Todo other) {
            return (this.Attendees == other.Attendees || ((this.Attendees != null) && (other.Attendees != null) && Todo.SetEquals(this.Attendees, other.Attendees))) && (this.Projects == other.Projects || ((this.Projects != null) && (other.Projects != null) && Todo.SetEquals(this.Projects, other.Projects))) && (this.Checkboxes == other.Checkboxes || ((this.Checkboxes != null) && (other.Checkboxes != null) && Todo.SetEquals(this.Checkboxes, other.Checkboxes))) && (this.Prices == other.Prices || ((this.Prices != null) && (other.Prices != null) && Todo.NestedSetEquals(this.Prices, other.Prices))) && (this.Nested == other.Nested || ((this.Nested != null) && (other.Nested != null) && Todo.NestedNestedSetEquals(this.Nested, other.Nested))) && (this.Shopping == other.Shopping || ((this.Shopping != null) && (other.Shopping != null) && Todo.SetEquals(this.Shopping, other.Shopping))) && (this.Work == other.Work || ((this.Work != null) && (other.Work != null) && Todo.SetEquals(this.Work, other.Work)));
        }
    }
}