//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
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
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

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

        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.get();
        row = tempRef_row.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Todo t2 = this.ReadTodo(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.get();
        row = tempRef_row3.get();
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindAndDelete()
    public void FindAndDelete() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

        ArrayList<UUID> expected = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), UUID.fromString("{B7BC39C2" +
            "-1A2D-4EAF-8F33-ED976872D876}"), UUID.fromString("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}")));

        for (java.lang.Iterable<UUID> permutation : expected.Permute()) {
            Todo t1 = new Todo();
            t1.Projects = new ArrayList<UUID>(permutation);

            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(row);
            RefObject<RowBuffer> tempRef_row2 =
                new RefObject<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
            row = tempRef_row2.get();
            row = tempRef_row.get();

            // Attempt to update each item in turn and then update it with itself.
            RefObject<RowBuffer> tempRef_row3 =
                new RefObject<RowBuffer>(row);
            RowCursor root = RowCursor.Create(tempRef_row3);
            row = tempRef_row3.get();
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            RefObject<RowBuffer> tempRef_row4 =
                new RefObject<RowBuffer>(row);
            RowCursor setScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out setScope).Find(tempRef_row4, c.Path);
            row = tempRef_row4.get();
            RefObject<RowBuffer> tempRef_row5 =
                new RefObject<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
            row = tempRef_row5.get();
            for (UUID p : t1.Projects) {
                RefObject<RowBuffer> tempRef_row6 =
                    new RefObject<RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
                row = tempRef_row6.get();
                RefObject<RowBuffer> tempRef_row7 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row7,
                    ref tempCursor, p));
                row = tempRef_row7.get();
                RefObject<RowBuffer> tempRef_row8 =
                    new RefObject<RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref setScope, ref tempCursor,
                    out findScope));
                row = tempRef_row8.get();
                RefObject<RowBuffer> tempRef_row9 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().DeleteSparse(tempRef_row9,
                    ref findScope));
                row = tempRef_row9.get();
            }
        }

    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindInSet()
    public void FindInSet() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

        Todo t1 = new Todo();
        t1.Attendees = new ArrayList<String>(Arrays.asList("jason", "janice", "joshua"));
        t1.Prices = new ArrayList<ArrayList<Float>>(Arrays.asList(new ArrayList<Float>(Arrays.asList(1.2F, 3.0F)),
            new ArrayList<Float>(Arrays.asList(4.1F, 5.7F)), new ArrayList<Float>(Arrays.asList(7.3F, 8.12F, 9.14F))));

        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.get();
        row = tempRef_row.get();

        // Attempt to find each item in turn.
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.get();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.get();
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
        row = tempRef_row5.get();
        for (int i = 0; i < t1.Attendees.size(); i++) {
            RefObject<RowBuffer> tempRef_row6 =
                new RefObject<RowBuffer>(row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
            row = tempRef_row6.get();
            RefObject<RowBuffer> tempRef_row7 =
                new RefObject<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref tempCursor,
                t1.Attendees.get(i)));
            row = tempRef_row7.get();
            RefObject<RowBuffer> tempRef_row8 =
                new RefObject<RowBuffer>(row);
            RowCursor findScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref setScope, ref tempCursor,
                out findScope));
            row = tempRef_row8.get();
            Assert.AreEqual(i, findScope.Index, String.format("Failed to find t1.Attendees[%1$s]", i));
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        RefObject<RowBuffer> tempRef_row9 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row9, c.Path);
        row = tempRef_row9.get();
        RefObject<RowBuffer> tempRef_row10 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row10, ref setScope, out setScope));
        row = tempRef_row10.get();
        TypeArgument innerType = c.TypeArgs[0];
        TypeArgument itemType = innerType.getTypeArgs().get(0).clone();
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        for (int i = 0; i < t1.Prices.size(); i++) {
            RefObject<RowBuffer> tempRef_row11 =
                new RefObject<RowBuffer>(row);
            RowCursor tempCursor1;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor1).Find(tempRef_row11, Utf8String.Empty);
            row = tempRef_row11.get();
            RefObject<RowBuffer> tempRef_row12 =
                new RefObject<RowBuffer>(row);
            RowCursor innerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(innerLayout.WriteScope(tempRef_row12, ref tempCursor1,
                innerType.getTypeArgs().clone(), out innerScope));
            row = tempRef_row12.get();
            for (int j = 0; j < t1.Prices.get(i).size(); j++) {
                RefObject<RowBuffer> tempRef_row13 =
                    new RefObject<RowBuffer>(row);
                RowCursor tempCursor2;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor2).Find(tempRef_row13, "prices.0.0");
                row = tempRef_row13.get();
                RefObject<RowBuffer> tempRef_row14 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor2 =
                    new RefObject<RowCursor>(tempCursor2);
                ResultAssert.IsSuccess(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempRef_row14,
                    tempRef_tempCursor2, t1.Prices.get(i).get(j)));
                tempCursor2 = tempRef_tempCursor2.get();
                row = tempRef_row14.get();
                RefObject<RowBuffer> tempRef_row15 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_innerScope =
                    new RefObject<RowCursor>(innerScope);
                RefObject<RowCursor> tempRef_tempCursor22 =
                    new RefObject<RowCursor>(tempCursor2);
                ResultAssert.IsSuccess(innerLayout.MoveField(tempRef_row15, tempRef_innerScope, tempRef_tempCursor22));
                tempCursor2 = tempRef_tempCursor22.get();
                innerScope = tempRef_innerScope.get();
                row = tempRef_row15.get();
            }

            RefObject<RowBuffer> tempRef_row16 =
                new RefObject<RowBuffer>(row);
            RowCursor findScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row16, ref setScope, ref tempCursor1,
                out findScope));
            row = tempRef_row16.get();
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
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

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

        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.get();
        row = tempRef_row.get();

        // Attempt to insert duplicate items in existing sets.
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.get();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.get();
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
        row = tempRef_row5.get();
        RefObject<RowBuffer> tempRef_row6 =
            new RefObject<RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
        row = tempRef_row6.get();
        RefObject<RowBuffer> tempRef_row7 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref tempCursor,
            t1.Attendees.get(0)));
        row = tempRef_row7.get();
        RefObject<RowBuffer> tempRef_row8 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row8, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row8.get();
        RefObject<RowBuffer> tempRef_row9 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row9, Utf8String.Empty);
        row = tempRef_row9.get();
        RefObject<RowBuffer> tempRef_row10 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row10, ref tempCursor,
            t1.Attendees.get(0)));
        row = tempRef_row10.get();
        RefObject<RowBuffer> tempRef_row11 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row11, ref setScope, ref tempCursor, out _));
        row = tempRef_row11.get();
        RefObject<RowBuffer> tempRef_row12 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row12, Utf8String.Empty);
        row = tempRef_row12.get();
        RefObject<RowBuffer> tempRef_row13 =
            new RefObject<RowBuffer>(row);
        String _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.NotFound(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(tempRef_row13, ref tempCursor, out _));
        row = tempRef_row13.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("projects", out c);
        RefObject<RowBuffer> tempRef_row14 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row14, c.Path);
        row = tempRef_row14.get();
        RefObject<RowBuffer> tempRef_row15 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row15, ref setScope, out setScope));
        row = tempRef_row15.get();
        RefObject<RowBuffer> tempRef_row16 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row16, Utf8String.Empty);
        row = tempRef_row16.get();
        RefObject<RowBuffer> tempRef_row17 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row17, ref tempCursor,
            t1.Projects.get(0)));
        row = tempRef_row17.get();
        RefObject<RowBuffer> tempRef_row18 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row18, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row18.get();

        // Attempt to move a duplicate set into a set of sets.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        RefObject<RowBuffer> tempRef_row19 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row19, c.Path);
        row = tempRef_row19.get();
        RefObject<RowBuffer> tempRef_row20 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row20, ref setScope, out setScope));
        row = tempRef_row20.get();
        TypeArgument innerType = c.TypeArgs[0];
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        RefObject<RowBuffer> tempRef_row21 =
            new RefObject<RowBuffer>(row);
        RowCursor tempCursor1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor1).Find(tempRef_row21, Utf8String.Empty);
        row = tempRef_row21.get();
        RefObject<RowBuffer> tempRef_row22 =
            new RefObject<RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(innerLayout.WriteScope(tempRef_row22, ref tempCursor1, innerType.getTypeArgs().clone()
            , out innerScope));
        row = tempRef_row22.get();
        for (float innerItem : t1.Prices.get(0)) {
            LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
            RefObject<RowBuffer> tempRef_row23 =
                new RefObject<RowBuffer>(row);
            RowCursor tempCursor2;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor2).Find(tempRef_row23, "prices.0.0");
            row = tempRef_row23.get();
            RefObject<RowBuffer> tempRef_row24 =
                new RefObject<RowBuffer>(row);
            RefObject<RowCursor> tempRef_tempCursor2 =
                new RefObject<RowCursor>(tempCursor2);
            ResultAssert.IsSuccess(itemLayout.WriteSparse(tempRef_row24, tempRef_tempCursor2, innerItem));
            tempCursor2 = tempRef_tempCursor2.get();
            row = tempRef_row24.get();
            RefObject<RowBuffer> tempRef_row25 =
                new RefObject<RowBuffer>(row);
            RefObject<RowCursor> tempRef_innerScope =
                new RefObject<RowCursor>(innerScope);
            RefObject<RowCursor> tempRef_tempCursor22 =
                new RefObject<RowCursor>(tempCursor2);
            ResultAssert.IsSuccess(innerLayout.MoveField(tempRef_row25, tempRef_innerScope, tempRef_tempCursor22));
            tempCursor2 = tempRef_tempCursor22.get();
            innerScope = tempRef_innerScope.get();
            row = tempRef_row25.get();
        }

        RefObject<RowBuffer> tempRef_row26 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row26, ref setScope, ref tempCursor1,
            UpdateOptions.Insert));
        row = tempRef_row26.get();

        // Attempt to move a duplicate UDT into a set of UDT.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("shopping", out c);
        RefObject<RowBuffer> tempRef_row27 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row27, c.Path);
        row = tempRef_row27.get();
        RefObject<RowBuffer> tempRef_row28 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row28, ref setScope, out setScope));
        row = tempRef_row28.get();
        LayoutUDT udtLayout = c.TypeArgs[0].Type.<LayoutUDT>TypeAs();
        RefObject<RowBuffer> tempRef_row29 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row29, Utf8String.Empty);
        row = tempRef_row29.get();
        RefObject<RowBuffer> tempRef_row30 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor =
            new RefObject<RowCursor>(tempCursor);
        RowCursor udtScope;
        OutObject<RowCursor> tempOut_udtScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(udtLayout.WriteScope(tempRef_row30, tempRef_tempCursor, c.TypeArgs[0].TypeArgs,
            tempOut_udtScope));
        udtScope = tempOut_udtScope.get();
        tempCursor = tempRef_tempCursor.get();
        row = tempRef_row30.get();
        RefObject<RowBuffer> tempRef_row31 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_udtScope =
            new RefObject<RowCursor>(udtScope);
        TypedSetUnitTests.WriteShoppingItem(tempRef_row31, tempRef_udtScope, c.TypeArgs[0].TypeArgs,
            t1.Shopping.get(0));
        udtScope = tempRef_udtScope.get();
        row = tempRef_row31.get();
        RefObject<RowBuffer> tempRef_row32 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row32, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row32.get();

        // Attempt to move a duplicate tuple into a set of tuple.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("work", out c);
        RefObject<RowBuffer> tempRef_row33 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row33, c.Path);
        row = tempRef_row33.get();
        RefObject<RowBuffer> tempRef_row34 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row34, ref setScope, out setScope));
        row = tempRef_row34.get();
        innerType = c.TypeArgs[0];
        LayoutIndexedScope tupleLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
        RefObject<RowBuffer> tempRef_row35 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row35, Utf8String.Empty);
        row = tempRef_row35.get();
        RefObject<RowBuffer> tempRef_row36 =
            new RefObject<RowBuffer>(row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(tupleLayout.WriteScope(tempRef_row36, ref tempCursor, innerType.getTypeArgs().clone(),
            out tupleScope));
        row = tempRef_row36.get();
        RefObject<RowBuffer> tempRef_row37 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tupleScope =
            new RefObject<RowCursor>(tupleScope);
        ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(tempRef_row37, tempRef_tupleScope, t1.Work.get(0).Item1));
        tupleScope = tempRef_tupleScope.get();
        row = tempRef_row37.get();
        RefObject<RowBuffer> tempRef_row38 =
            new RefObject<RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row38);
        row = tempRef_row38.get();
        RefObject<RowBuffer> tempRef_row39 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tupleScope2 =
            new RefObject<RowCursor>(tupleScope);
        ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().WriteSparse(tempRef_row39, tempRef_tupleScope2, t1.Work.get(0).Item2));
        tupleScope = tempRef_tupleScope2.get();
        row = tempRef_row39.get();
        RefObject<RowBuffer> tempRef_row40 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row40, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row40.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUpdatesInNonUpdatableScope()
    public void PreventUpdatesInNonUpdatableScope() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

        // Write a set and then try to write directly into it.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row, out setScope).Find(tempRef_row2, c.Path);
        row = tempRef_row2.get();
        row = tempRef_row.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row3, ref setScope, c.TypeArgs,
            out setScope));
        row = tempRef_row3.get();
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row4,
            ref setScope, "foo"));
        row = tempRef_row4.get();
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row6 =
            new RefObject<RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row5, out tempCursor).Find(tempRef_row6, Utf8String.Empty);
        row = tempRef_row6.get();
        row = tempRef_row5.get();
        RefObject<RowBuffer> tempRef_row7 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref tempCursor, "foo"
        ));
        row = tempRef_row7.get();
        RefObject<RowBuffer> tempRef_row8 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row8, ref setScope, ref tempCursor));
        row = tempRef_row8.get();
        RefObject<RowBuffer> tempRef_row9 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row9,
            ref setScope, "foo"));
        row = tempRef_row9.get();
        RefObject<RowBuffer> tempRef_row10 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().DeleteSparse(tempRef_row10, ref setScope));
        row = tempRef_row10.get();

        // Write a set of sets, successfully insert an empty set into it, and then try to write directly to the inner
        // set.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        RefObject<RowBuffer> tempRef_row11 =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row12 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row11, out setScope).Find(tempRef_row12, c.Path);
        row = tempRef_row12.get();
        row = tempRef_row11.get();
        RefObject<RowBuffer> tempRef_row13 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row13, ref setScope, c.TypeArgs,
            out setScope));
        row = tempRef_row13.get();
        TypeArgument innerType = c.TypeArgs[0];
        TypeArgument itemType = innerType.getTypeArgs().get(0).clone();
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        RefObject<RowBuffer> tempRef_row14 =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row15 =
            new RefObject<RowBuffer>(row);
        RowCursor tempCursor1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row14, out tempCursor1).Find(tempRef_row15, "prices.0");
        row = tempRef_row15.get();
        row = tempRef_row14.get();
        RefObject<RowBuffer> tempRef_row16 =
            new RefObject<RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(innerLayout.WriteScope(tempRef_row16, ref tempCursor1, innerType.getTypeArgs().clone()
            , out innerScope));
        row = tempRef_row16.get();
        RefObject<RowBuffer> tempRef_row17 =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row18 =
            new RefObject<RowBuffer>(row);
        RowCursor tempCursor2;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row17, out tempCursor2).Find(tempRef_row18, "prices.0.0");
        row = tempRef_row18.get();
        row = tempRef_row17.get();
        RefObject<RowBuffer> tempRef_row19 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor2 =
            new RefObject<RowCursor>(tempCursor2);
        ResultAssert.IsSuccess(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempRef_row19,
            tempRef_tempCursor2, 1.0F));
        tempCursor2 = tempRef_tempCursor2.get();
        row = tempRef_row19.get();
        RefObject<RowBuffer> tempRef_row20 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_innerScope =
            new RefObject<RowCursor>(innerScope);
        RefObject<RowCursor> tempRef_tempCursor22 =
            new RefObject<RowCursor>(tempCursor2);
        ResultAssert.IsSuccess(innerLayout.MoveField(tempRef_row20, tempRef_innerScope, tempRef_tempCursor22));
        tempCursor2 = tempRef_tempCursor22.get();
        innerScope = tempRef_innerScope.get();
        row = tempRef_row20.get();
        RefObject<RowBuffer> tempRef_row21 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row21, ref setScope, ref tempCursor1));
        row = tempRef_row21.get();
        RefObject<RowBuffer> tempRef_row22 =
            new RefObject<RowBuffer>(row);
        assert setScope.MoveNext(tempRef_row22);
        row = tempRef_row22.get();
        RefObject<RowBuffer> tempRef_row23 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_setScope =
            new RefObject<RowCursor>(setScope);
        OutObject<RowCursor> tempOut_innerScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(innerLayout.ReadScope(tempRef_row23, tempRef_setScope, tempOut_innerScope));
        innerScope = tempOut_innerScope.get();
        setScope = tempRef_setScope.get();
        row = tempRef_row23.get();
        RefObject<RowBuffer> tempRef_row24 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_innerScope2 =
            new RefObject<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempRef_row24,
            tempRef_innerScope2, 1.0F));
        innerScope = tempRef_innerScope2.get();
        row = tempRef_row24.get();
        RefObject<RowBuffer> tempRef_row25 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_innerScope3 =
            new RefObject<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutFloat32>TypeAs().DeleteSparse(tempRef_row25,
            tempRef_innerScope3));
        innerScope = tempRef_innerScope3.get();
        row = tempRef_row25.get();
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

            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(row);
            ResultAssert.IsSuccess(RowWriter.WriteBuffer(tempRef_row, t1, TypedSetUnitTests.SerializeTodo));
            row = tempRef_row.get();

            // Update the existing Set by updating each item with itself.  This ensures that the RowWriter has
            // maintained the unique index correctly.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            RefObject<RowBuffer> tempRef_row2 =
                new RefObject<RowBuffer>(row);
            RowCursor root;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            RowCursor.Create(tempRef_row2, out root);
            row = tempRef_row2.get();
            RefObject<RowBuffer> tempRef_row3 =
                new RefObject<RowBuffer>(row);
            RowCursor projScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out projScope).Find(tempRef_row3, c.Path);
            row = tempRef_row3.get();
            RefObject<RowBuffer> tempRef_row4 =
                new RefObject<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row4, ref projScope, out projScope));
            row = tempRef_row4.get();
            for (UUID item : t1.Projects) {
                RefObject<RowBuffer> tempRef_row5 =
                    new RefObject<RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row5, Utf8String.Empty);
                row = tempRef_row5.get();
                RefObject<RowBuffer> tempRef_row6 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row6,
                    ref tempCursor, item));
                row = tempRef_row6.get();
                RefObject<RowBuffer> tempRef_row7 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row7, ref projScope,
                    ref tempCursor));
                row = tempRef_row7.get();
            }

            RefObject<RowBuffer> tempRef_row8 =
                new RefObject<RowBuffer>(row);
            RefObject<RowBuffer> tempRef_row9 =
                new RefObject<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            Todo t2 = this.ReadTodo(tempRef_row8, RowCursor.Create(tempRef_row9, out _));
            row = tempRef_row9.get();
            row = tempRef_row8.get();
            assert t1 == t2;
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void UpdateInSet()
    public void UpdateInSet() {
        RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

        ArrayList<UUID> expected = new ArrayList<UUID>(Arrays.asList(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), UUID.fromString("{B7BC39C2" +
            "-1A2D-4EAF-8F33-ED976872D876}"), UUID.fromString("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}")));

        for (java.lang.Iterable<UUID> permutation : expected.Permute()) {
            Todo t1 = new Todo();
            t1.Projects = new ArrayList<UUID>(permutation);

            RefObject<RowBuffer> tempRef_row =
                new RefObject<RowBuffer>(row);
            RefObject<RowBuffer> tempRef_row2 =
                new RefObject<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
            row = tempRef_row2.get();
            row = tempRef_row.get();

            // Attempt to find each item in turn and then delete it.
            RefObject<RowBuffer> tempRef_row3 =
                new RefObject<RowBuffer>(row);
            RowCursor root = RowCursor.Create(tempRef_row3);
            row = tempRef_row3.get();
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            RefObject<RowBuffer> tempRef_row4 =
                new RefObject<RowBuffer>(row);
            RowCursor setScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out setScope).Find(tempRef_row4, c.Path);
            row = tempRef_row4.get();
            RefObject<RowBuffer> tempRef_row5 =
                new RefObject<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
            row = tempRef_row5.get();
            for (UUID elm : t1.Projects) {
                // Verify it is already there.
                RefObject<RowBuffer> tempRef_row6 =
                    new RefObject<RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
                row = tempRef_row6.get();
                RefObject<RowBuffer> tempRef_row7 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row7,
                    ref tempCursor, elm));
                row = tempRef_row7.get();
                RefObject<RowBuffer> tempRef_row8 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
                // following line:
                //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref
                // tempCursor, value: out RowCursor _));
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref setScope, ref tempCursor,
                    value:
                out RowCursor _))
                row = tempRef_row8.get();

                // Insert it again with update.
                RefObject<RowBuffer> tempRef_row9 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row9, Utf8String.Empty);
                row = tempRef_row9.get();
                RefObject<RowBuffer> tempRef_row10 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row10,
                    ref tempCursor, elm));
                row = tempRef_row10.get();
                RefObject<RowBuffer> tempRef_row11 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row11, ref setScope,
                    ref tempCursor, UpdateOptions.Update));
                row = tempRef_row11.get();

                // Insert it again with upsert.
                RefObject<RowBuffer> tempRef_row12 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row12, Utf8String.Empty);
                row = tempRef_row12.get();
                RefObject<RowBuffer> tempRef_row13 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row13,
                    ref tempCursor, elm));
                row = tempRef_row13.get();
                RefObject<RowBuffer> tempRef_row14 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row14, ref setScope,
                    ref tempCursor));
                row = tempRef_row14.get();

                // Insert it again with insert (fail: exists).
                RefObject<RowBuffer> tempRef_row15 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row15, Utf8String.Empty);
                row = tempRef_row15.get();
                RefObject<RowBuffer> tempRef_row16 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row16,
                    ref tempCursor, elm));
                row = tempRef_row16.get();
                RefObject<RowBuffer> tempRef_row17 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row17, ref setScope,
                    ref tempCursor, UpdateOptions.Insert));
                row = tempRef_row17.get();

                // Insert it again with insert at (fail: disallowed).
                RefObject<RowBuffer> tempRef_row18 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row18, Utf8String.Empty);
                row = tempRef_row18.get();
                RefObject<RowBuffer> tempRef_row19 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row19,
                    ref tempCursor, elm));
                row = tempRef_row19.get();
                RefObject<RowBuffer> tempRef_row20 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.TypeConstraint(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row20, ref setScope,
                    ref tempCursor, UpdateOptions.InsertAt));
                row = tempRef_row20.get();
            }
        }
    }

    private static ShoppingItem ReadShoppingItem(RefObject<RowBuffer> row, RefObject<RowCursor> matchScope) {
        Layout matchLayout = matchScope.get().getLayout();
        ShoppingItem m = new ShoppingItem();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("label", out c);
        OutObject<String> tempOut_Label = new OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, matchScope, c, tempOut_Label));
        m.Label = tempOut_Label.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("count", out c);
        OutObject<Byte> tempOut_Count = new OutObject<Byte>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeAs<LayoutUInt8>().ReadFixed(ref row, ref matchScope, c, out m.Count));
        ResultAssert.IsSuccess(c.<LayoutUInt8>TypeAs().ReadFixed(row, matchScope, c, tempOut_Count));
        m.Count = tempOut_Count.get();
        return m;
    }

    private Todo ReadTodo(RefObject<RowBuffer> row, RefObject<RowCursor> root) {
        Todo value = new Todo();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        RowCursor tagsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out tagsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref tagsScope, out tagsScope) == Result.Success) {
            value.Attendees = new ArrayList<String>();
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
                value.Attendees.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("projects", out c);
        RowCursor projScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out projScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref projScope, out projScope) == Result.Success) {
            value.Projects = new ArrayList<UUID>();
            while (projScope.MoveNext(row)) {
                java.util.UUID item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().ReadSparse(row, ref projScope,
                    out item));
                value.Projects.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("checkboxes", out c);
        RowCursor checkboxScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out checkboxScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref checkboxScope, out checkboxScope) == Result.Success) {
            value.Checkboxes = new ArrayList<Boolean>();
            while (checkboxScope.MoveNext(row)) {
                boolean item;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutBoolean>TypeAs().ReadSparse(row, ref checkboxScope,
                    out item));
                value.Checkboxes.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        RowCursor pricesScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out pricesScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref pricesScope, out pricesScope) == Result.Success) {
            value.Prices = new ArrayList<ArrayList<Float>>();
            TypeArgument innerType = c.TypeArgs[0];
            LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
            while (pricesScope.MoveNext(row)) {
                ArrayList<Float> item = new ArrayList<Float>();
                RefObject<RowCursor> tempRef_pricesScope =
                    new RefObject<RowCursor>(pricesScope);
                RowCursor innerScope;
                OutObject<RowCursor> tempOut_innerScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_pricesScope, tempOut_innerScope));
                innerScope = tempOut_innerScope.get();
                pricesScope = tempRef_pricesScope.get();
                while (innerScope.MoveNext(row)) {
                    LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
                    RefObject<RowCursor> tempRef_innerScope =
                        new RefObject<RowCursor>(innerScope);
                    float innerItem;
                    OutObject<Float> tempOut_innerItem = new OutObject<Float>();
                    ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempRef_innerScope, tempOut_innerItem));
                    innerItem = tempOut_innerItem.get();
                    innerScope = tempRef_innerScope.get();
                    item.add(innerItem);
                }

                value.Prices.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nested", out c);
        RowCursor nestedScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out nestedScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref nestedScope, out nestedScope) == Result.Success) {
            value.Nested = new ArrayList<ArrayList<ArrayList<Integer>>>();
            TypeArgument in2Type = c.TypeArgs[0];
            LayoutUniqueScope in2Layout = in2Type.getType().<LayoutUniqueScope>TypeAs();
            while (nestedScope.MoveNext(row)) {
                ArrayList<ArrayList<Integer>> item = new ArrayList<ArrayList<Integer>>();
                RefObject<RowCursor> tempRef_nestedScope =
                    new RefObject<RowCursor>(nestedScope);
                RowCursor in2Scope;
                OutObject<RowCursor> tempOut_in2Scope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(in2Layout.ReadScope(row, tempRef_nestedScope, tempOut_in2Scope));
                in2Scope = tempOut_in2Scope.get();
                nestedScope = tempRef_nestedScope.get();
                while (in2Scope.MoveNext(row)) {
                    TypeArgument in3Type = in2Type.getTypeArgs().get(0).clone();
                    LayoutUniqueScope in3Layout = in3Type.getType().<LayoutUniqueScope>TypeAs();
                    ArrayList<Integer> item2 = new ArrayList<Integer>();
                    RefObject<RowCursor> tempRef_in2Scope =
                        new RefObject<RowCursor>(in2Scope);
                    RowCursor in3Scope;
                    OutObject<RowCursor> tempOut_in3Scope =
                        new OutObject<RowCursor>();
                    ResultAssert.IsSuccess(in3Layout.ReadScope(row, tempRef_in2Scope, tempOut_in3Scope));
                    in3Scope = tempOut_in3Scope.get();
                    in2Scope = tempRef_in2Scope.get();
                    while (in3Scope.MoveNext(row)) {
                        LayoutInt32 itemLayout = in3Type.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs();
                        RefObject<RowCursor> tempRef_in3Scope = new RefObject<RowCursor>(in3Scope);
                        int innerItem;
                        OutObject<Integer> tempOut_innerItem2 = new OutObject<Integer>();
                        ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempRef_in3Scope, tempOut_innerItem2));
                        innerItem = tempOut_innerItem2.get();
                        in3Scope = tempRef_in3Scope.get();
                        item2.add(innerItem);
                    }

                    item.add(item2);
                }

                value.Nested.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("shopping", out c);
        RowCursor shoppingScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out shoppingScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref shoppingScope, out shoppingScope) == Result.Success) {
            value.Shopping = new ArrayList<ShoppingItem>();
            while (shoppingScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                RefObject<RowCursor> tempRef_shoppingScope =
                    new RefObject<RowCursor>(shoppingScope);
                RowCursor matchScope;
                OutObject<RowCursor> tempOut_matchScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_shoppingScope, tempOut_matchScope));
                matchScope = tempOut_matchScope.get();
                shoppingScope = tempRef_shoppingScope.get();
                RefObject<RowCursor> tempRef_matchScope =
                    new RefObject<RowCursor>(matchScope);
                ShoppingItem item = TypedSetUnitTests.ReadShoppingItem(row, tempRef_matchScope);
                matchScope = tempRef_matchScope.get();
                value.Shopping.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("work", out c);
        RowCursor workScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out workScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref workScope, out workScope) == Result.Success) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: value.Work = new List<Tuple<bool, ulong>>();
            value.Work = new ArrayList<Tuple<Boolean, Long>>();
            while (workScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();

                RefObject<RowCursor> tempRef_workScope = new RefObject<RowCursor>(workScope);
                RowCursor tupleScope;
                OutObject<RowCursor> tempOut_tupleScope = new OutObject<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_workScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.get();
                workScope = tempRef_workScope.get();
                assert tupleScope.MoveNext(row);
                RefObject<RowCursor> tempRef_tupleScope = new RefObject<RowCursor>(tupleScope);
                boolean item1;
                OutObject<Boolean> tempOut_item1 = new OutObject<Boolean>();
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().ReadSparse(row, tempRef_tupleScope, tempOut_item1));
                item1 = tempOut_item1.get();
                tupleScope = tempRef_tupleScope.get();
                assert tupleScope.MoveNext(row);
                RefObject<RowCursor> tempRef_tupleScope2 = new RefObject<RowCursor>(tupleScope);
                long item2;
                OutObject<Long> tempOut_item2 = new OutObject<Long>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutVarUInt>().ReadSparse(ref row, ref tupleScope, out ulong item2));
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().ReadSparse(row, tempRef_tupleScope2, tempOut_item2));
                item2 = tempOut_item2.get();
                tupleScope = tempRef_tupleScope2.get();
                value.Work.add(Tuple.Create(item1, item2));
            }
        }

        return value;
    }

    private static Result SerializeTodo(RefObject<RowWriter> writer, TypeArgument typeArg, Todo value) {
        if (value.Projects != null) {
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
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

                return Result.Success;
            });
            return r;
        }

        return Result.Success;
    }

    private static void WriteShoppingItem(RefObject<RowBuffer> row, RefObject<RowCursor> matchScope, TypeArgumentList typeArgs, ShoppingItem m) {
        Layout matchLayout = row.get().getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("label", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, matchScope, c, m.Label));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("count", out c);
        ResultAssert.IsSuccess(c.<LayoutUInt8>TypeAs().WriteFixed(row, matchScope, c, m.Count));
    }

    private void WriteTodo(RefObject<RowBuffer> row, RefObject<RowCursor> root, Todo value) {
        LayoutColumn c;

        if (value.Attendees != null) {
            OutObject<LayoutColumn> tempOut_c =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("attendees", tempOut_c);
            c = tempOut_c.get();
            RowCursor attendScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out attendScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref attendScope,
                c.getTypeArgs().clone(), out attendScope));
            for (String item : value.Attendees) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().WriteSparse(row,
                    ref tempCursor, item));
                RefObject<RowCursor> tempRef_attendScope =
                    new RefObject<RowCursor>(attendScope);
                RefObject<RowCursor> tempRef_tempCursor =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_attendScope,
                    tempRef_tempCursor));
                tempCursor = tempRef_tempCursor.get();
                attendScope = tempRef_attendScope.get();
            }
        }

        if (value.Projects != null) {
            OutObject<LayoutColumn> tempOut_c2 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("projects", tempOut_c2);
            c = tempOut_c2.get();
            RowCursor projScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out projScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref projScope,
                c.getTypeArgs().clone(), out projScope));
            for (UUID item : value.Projects) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RefObject<RowCursor> tempRef_tempCursor2 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutGuid>TypeAs().WriteSparse(row,
                    tempRef_tempCursor2, item));
                tempCursor = tempRef_tempCursor2.get();
                RefObject<RowCursor> tempRef_projScope =
                    new RefObject<RowCursor>(projScope);
                RefObject<RowCursor> tempRef_tempCursor3 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_projScope,
                    tempRef_tempCursor3));
                tempCursor = tempRef_tempCursor3.get();
                projScope = tempRef_projScope.get();
            }
        }

        if (value.Checkboxes != null) {
            OutObject<LayoutColumn> tempOut_c3 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("checkboxes", tempOut_c3);
            c = tempOut_c3.get();
            RowCursor checkboxScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out checkboxScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref checkboxScope,
                c.getTypeArgs().clone(), out checkboxScope));
            for (boolean item : value.Checkboxes) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RefObject<RowCursor> tempRef_tempCursor4 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(row,
                    tempRef_tempCursor4, item));
                tempCursor = tempRef_tempCursor4.get();
                RefObject<RowCursor> tempRef_checkboxScope =
                    new RefObject<RowCursor>(checkboxScope);
                RefObject<RowCursor> tempRef_tempCursor5 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_checkboxScope,
                    tempRef_tempCursor5));
                tempCursor = tempRef_tempCursor5.get();
                checkboxScope = tempRef_checkboxScope.get();
            }
        }

        if (value.Prices != null) {
            OutObject<LayoutColumn> tempOut_c4 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("prices", tempOut_c4);
            c = tempOut_c4.get();
            RowCursor pricesScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out pricesScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref pricesScope,
                c.getTypeArgs().clone(), out pricesScope));
            for (ArrayList<Float> item : value.Prices) {
                assert item != null;
                TypeArgument innerType = c.getTypeArgs().get(0).clone();
                LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor1).Find(row, "prices.0");
                RowCursor innerScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref tempCursor1, innerType.getTypeArgs().clone(),
                    out innerScope));
                for (float innerItem : item) {
                    LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
                    RowCursor tempCursor2;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'OutObject' helper class unless the method is within the
                    // code being modified:
                    root.get().Clone(out tempCursor2).Find(row, "prices.0.0");
                    RefObject<RowCursor> tempRef_tempCursor2
                        = new RefObject<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempRef_tempCursor2, innerItem));
                    tempCursor2 = tempRef_tempCursor2.get();
                    RefObject<RowCursor> tempRef_innerScope =
                        new RefObject<RowCursor>(innerScope);
                    RefObject<RowCursor> tempRef_tempCursor22 = new RefObject<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(innerLayout.MoveField(row, tempRef_innerScope, tempRef_tempCursor22));
                    tempCursor2 = tempRef_tempCursor22.get();
                    innerScope = tempRef_innerScope.get();
                }

                RefObject<RowCursor> tempRef_pricesScope =
                    new RefObject<RowCursor>(pricesScope);
                RefObject<RowCursor> tempRef_tempCursor1 =
                    new RefObject<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_pricesScope,
                    tempRef_tempCursor1));
                tempCursor1 = tempRef_tempCursor1.get();
                pricesScope = tempRef_pricesScope.get();
            }
        }

        if (value.Nested != null) {
            OutObject<LayoutColumn> tempOut_c5 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("nested", tempOut_c5);
            c = tempOut_c5.get();
            RowCursor nestedScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out nestedScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref nestedScope,
                c.getTypeArgs().clone(), out nestedScope));
            for (ArrayList<ArrayList<Integer>> item : value.Nested) {
                assert item != null;
                TypeArgument in2Type = c.getTypeArgs().get(0).clone();
                LayoutUniqueScope in2Layout = in2Type.getType().<LayoutUniqueScope>TypeAs();
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor1).Find(row, "prices.0");
                RowCursor in2Scope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(in2Layout.WriteScope(row, ref tempCursor1, in2Type.getTypeArgs().clone(),
                    out in2Scope));
                for (ArrayList<Integer> item2 : item) {
                    assert item2 != null;
                    TypeArgument in3Type = in2Type.getTypeArgs().get(0).clone();
                    LayoutUniqueScope in3Layout = in3Type.getType().<LayoutUniqueScope>TypeAs();
                    RowCursor tempCursor2;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'OutObject' helper class unless the method is within the
                    // code being modified:
                    root.get().Clone(out tempCursor2).Find(row, "prices.0.0");
                    RowCursor in3Scope;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'OutObject' helper class unless the method is within the
                    // code being modified:
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword
                    // - these cannot be converted using the 'RefObject' helper class unless the method is within the
                    // code being modified:
                    ResultAssert.IsSuccess(in3Layout.WriteScope(row, ref tempCursor2, in3Type.getTypeArgs().clone(),
                        out in3Scope));
                    for (int innerItem : item2) {
                        LayoutInt32 itemLayout = in3Type.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs();
                        RowCursor tempCursor3;
                        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out'
                        // keyword - these cannot be converted using the 'OutObject' helper class unless the method
                        // is within the code being modified:
                        root.get().Clone(out tempCursor3).Find(row, "prices.0.0.0");
                        RefObject<RowCursor> tempRef_tempCursor3 = new RefObject<RowCursor>(tempCursor3);
                        ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempRef_tempCursor3, innerItem));
                        tempCursor3 = tempRef_tempCursor3.get();
                        RefObject<RowCursor> tempRef_in3Scope = new RefObject<RowCursor>(in3Scope);
                        RefObject<RowCursor> tempRef_tempCursor32 = new RefObject<RowCursor>(tempCursor3);
                        ResultAssert.IsSuccess(in3Layout.MoveField(row, tempRef_in3Scope, tempRef_tempCursor32));
                        tempCursor3 = tempRef_tempCursor32.get();
                        in3Scope = tempRef_in3Scope.get();
                    }

                    RefObject<RowCursor> tempRef_in2Scope =
                        new RefObject<RowCursor>(in2Scope);
                    RefObject<RowCursor> tempRef_tempCursor23 = new RefObject<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(in2Layout.MoveField(row, tempRef_in2Scope, tempRef_tempCursor23));
                    tempCursor2 = tempRef_tempCursor23.get();
                    in2Scope = tempRef_in2Scope.get();
                }

                RefObject<RowCursor> tempRef_nestedScope =
                    new RefObject<RowCursor>(nestedScope);
                RefObject<RowCursor> tempRef_tempCursor12 =
                    new RefObject<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_nestedScope,
                    tempRef_tempCursor12));
                tempCursor1 = tempRef_tempCursor12.get();
                nestedScope = tempRef_nestedScope.get();
            }
        }

        if (value.Shopping != null) {
            OutObject<LayoutColumn> tempOut_c6 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("shopping", tempOut_c6);
            c = tempOut_c6.get();
            RowCursor shoppingScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out shoppingScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref shoppingScope,
                c.getTypeArgs().clone(), out shoppingScope));
            for (ShoppingItem item : value.Shopping) {
                TypeArgument innerType = c.getTypeArgs().get(0).clone();
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RefObject<RowCursor> tempRef_tempCursor6 =
                    new RefObject<RowCursor>(tempCursor);
                RowCursor itemScope;
                OutObject<RowCursor> tempOut_itemScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempRef_tempCursor6,
                    innerType.getTypeArgs().clone(), tempOut_itemScope));
                itemScope = tempOut_itemScope.get();
                tempCursor = tempRef_tempCursor6.get();
                RefObject<RowCursor> tempRef_itemScope =
                    new RefObject<RowCursor>(itemScope);
                TypedSetUnitTests.WriteShoppingItem(row, tempRef_itemScope, innerType.getTypeArgs().clone(), item);
                itemScope = tempRef_itemScope.get();
                RefObject<RowCursor> tempRef_shoppingScope =
                    new RefObject<RowCursor>(shoppingScope);
                RefObject<RowCursor> tempRef_tempCursor7 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_shoppingScope,
                    tempRef_tempCursor7));
                tempCursor = tempRef_tempCursor7.get();
                shoppingScope = tempRef_shoppingScope.get();
            }
        }

        if (value.Work != null) {
            OutObject<LayoutColumn> tempOut_c7 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("work", tempOut_c7);
            c = tempOut_c7.get();
            RowCursor workScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out workScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref workScope,
                c.getTypeArgs().clone(), out workScope));
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: foreach (Tuple<bool, ulong> item in value.Work)
            for (Tuple<Boolean, Long> item : value.Work) {
                TypeArgument innerType = c.getTypeArgs().get(0).clone();
                LayoutIndexedScope innerLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref tempCursor, innerType.getTypeArgs().clone(),
                    out tupleScope));
                RefObject<RowCursor> tempRef_tupleScope =
                    new RefObject<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(row, tempRef_tupleScope, item.Item1));
                tupleScope = tempRef_tupleScope.get();
                assert tupleScope.MoveNext(row);
                RefObject<RowCursor> tempRef_tupleScope2 =
                    new RefObject<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().WriteSparse(row, tempRef_tupleScope2, item.Item2));
                tupleScope = tempRef_tupleScope2.get();
                RefObject<RowCursor> tempRef_workScope =
                    new RefObject<RowCursor>(workScope);
                RefObject<RowCursor> tempRef_tempCursor8 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_workScope,
                    tempRef_tempCursor8));
                tempCursor = tempRef_tempCursor8.get();
                workScope = tempRef_workScope.get();
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