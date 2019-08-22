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

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Todo t2 = this.ReadTodo(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
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

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
            row = tempRef_row2.argValue;
            row = tempRef_row.argValue;

            // Attempt to update each item in turn and then update it with itself.
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor root = RowCursor.Create(tempRef_row3);
            row = tempRef_row3.argValue;
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor setScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out setScope).Find(tempRef_row4, c.Path);
            row = tempRef_row4.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
            row = tempRef_row5.argValue;
            for (UUID p : t1.Projects) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
                row = tempRef_row6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row7,
                    ref tempCursor, p));
                row = tempRef_row7.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref setScope, ref tempCursor,
                    out findScope));
                row = tempRef_row8.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().DeleteSparse(tempRef_row9,
                    ref findScope));
                row = tempRef_row9.argValue;
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

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;

        // Attempt to find each item in turn.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.argValue;
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
        row = tempRef_row5.argValue;
        for (int i = 0; i < t1.Attendees.size(); i++) {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
            row = tempRef_row6.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref tempCursor,
                t1.Attendees.get(i)));
            row = tempRef_row7.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor findScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref setScope, ref tempCursor,
                out findScope));
            row = tempRef_row8.argValue;
            Assert.AreEqual(i, findScope.Index, String.format("Failed to find t1.Attendees[%1$s]", i));
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row9, c.Path);
        row = tempRef_row9.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row10, ref setScope, out setScope));
        row = tempRef_row10.argValue;
        TypeArgument innerType = c.TypeArgs[0];
        TypeArgument itemType = innerType.getTypeArgs().get(0).clone();
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        for (int i = 0; i < t1.Prices.size(); i++) {
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor tempCursor1;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor1).Find(tempRef_row11, Utf8String.Empty);
            row = tempRef_row11.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor innerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(innerLayout.WriteScope(tempRef_row12, ref tempCursor1,
                innerType.getTypeArgs().clone(), out innerScope));
            row = tempRef_row12.argValue;
            for (int j = 0; j < t1.Prices.get(i).size(); j++) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor tempCursor2;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor2).Find(tempRef_row13, "prices.0.0");
                row = tempRef_row13.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                ResultAssert.IsSuccess(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempRef_row14,
                    tempRef_tempCursor2, t1.Prices.get(i).get(j)));
                tempCursor2 = tempRef_tempCursor2.argValue;
                row = tempRef_row14.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor22 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                ResultAssert.IsSuccess(innerLayout.MoveField(tempRef_row15, tempRef_innerScope, tempRef_tempCursor22));
                tempCursor2 = tempRef_tempCursor22.argValue;
                innerScope = tempRef_innerScope.argValue;
                row = tempRef_row15.argValue;
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor findScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row16, ref setScope, ref tempCursor1,
                out findScope));
            row = tempRef_row16.argValue;
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

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;

        // Attempt to insert duplicate items in existing sets.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row3);
        row = tempRef_row3.argValue;
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
        row = tempRef_row6.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref tempCursor,
            t1.Attendees.get(0)));
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row8, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row8.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row9, Utf8String.Empty);
        row = tempRef_row9.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row10, ref tempCursor,
            t1.Attendees.get(0)));
        row = tempRef_row10.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row11, ref setScope, ref tempCursor, out _));
        row = tempRef_row11.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row12, Utf8String.Empty);
        row = tempRef_row12.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        String _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.NotFound(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(tempRef_row13, ref tempCursor, out _));
        row = tempRef_row13.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("projects", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row14, c.Path);
        row = tempRef_row14.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row15, ref setScope, out setScope));
        row = tempRef_row15.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row16, Utf8String.Empty);
        row = tempRef_row16.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row17, ref tempCursor,
            t1.Projects.get(0)));
        row = tempRef_row17.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row18, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row18.argValue;

        // Attempt to move a duplicate set into a set of sets.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row19, c.Path);
        row = tempRef_row19.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row20 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row20, ref setScope, out setScope));
        row = tempRef_row20.argValue;
        TypeArgument innerType = c.TypeArgs[0];
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row21 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tempCursor1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor1).Find(tempRef_row21, Utf8String.Empty);
        row = tempRef_row21.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row22 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(innerLayout.WriteScope(tempRef_row22, ref tempCursor1, innerType.getTypeArgs().clone()
            , out innerScope));
        row = tempRef_row22.argValue;
        for (float innerItem : t1.Prices.get(0)) {
            LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row23 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor tempCursor2;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out tempCursor2).Find(tempRef_row23, "prices.0.0");
            row = tempRef_row23.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row24 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
            ResultAssert.IsSuccess(itemLayout.WriteSparse(tempRef_row24, tempRef_tempCursor2, innerItem));
            tempCursor2 = tempRef_tempCursor2.argValue;
            row = tempRef_row24.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row25 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor22 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
            ResultAssert.IsSuccess(innerLayout.MoveField(tempRef_row25, tempRef_innerScope, tempRef_tempCursor22));
            tempCursor2 = tempRef_tempCursor22.argValue;
            innerScope = tempRef_innerScope.argValue;
            row = tempRef_row25.argValue;
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row26 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row26, ref setScope, ref tempCursor1,
            UpdateOptions.Insert));
        row = tempRef_row26.argValue;

        // Attempt to move a duplicate UDT into a set of UDT.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("shopping", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row27 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row27, c.Path);
        row = tempRef_row27.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row28 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row28, ref setScope, out setScope));
        row = tempRef_row28.argValue;
        LayoutUDT udtLayout = c.TypeArgs[0].Type.<LayoutUDT>TypeAs();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row29 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row29, Utf8String.Empty);
        row = tempRef_row29.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row30 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        RowCursor udtScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_udtScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(udtLayout.WriteScope(tempRef_row30, tempRef_tempCursor, c.TypeArgs[0].TypeArgs,
            tempOut_udtScope));
        udtScope = tempOut_udtScope.argValue;
        tempCursor = tempRef_tempCursor.argValue;
        row = tempRef_row30.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row31 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_udtScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(udtScope);
        TypedSetUnitTests.WriteShoppingItem(tempRef_row31, tempRef_udtScope, c.TypeArgs[0].TypeArgs,
            t1.Shopping.get(0));
        udtScope = tempRef_udtScope.argValue;
        row = tempRef_row31.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row32 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row32, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row32.argValue;

        // Attempt to move a duplicate tuple into a set of tuple.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("work", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row33 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out setScope).Find(tempRef_row33, c.Path);
        row = tempRef_row33.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row34 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row34, ref setScope, out setScope));
        row = tempRef_row34.argValue;
        innerType = c.TypeArgs[0];
        LayoutIndexedScope tupleLayout = innerType.getType().<LayoutIndexedScope>TypeAs();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row35 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row35, Utf8String.Empty);
        row = tempRef_row35.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row36 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(tupleLayout.WriteScope(tempRef_row36, ref tempCursor, innerType.getTypeArgs().clone(),
            out tupleScope));
        row = tempRef_row36.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row37 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(tempRef_row37, tempRef_tupleScope, t1.Work.get(0).Item1));
        tupleScope = tempRef_tupleScope.argValue;
        row = tempRef_row37.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row38 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row38);
        row = tempRef_row38.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row39 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().WriteSparse(tempRef_row39, tempRef_tupleScope2, t1.Work.get(0).Item2));
        tupleScope = tempRef_tupleScope2.argValue;
        row = tempRef_row39.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row40 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row40, ref setScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row40.argValue;
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
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor setScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row, out setScope).Find(tempRef_row2, c.Path);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row3, ref setScope, c.TypeArgs,
            out setScope));
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row4,
            ref setScope, "foo"));
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row5, out tempCursor).Find(tempRef_row6, Utf8String.Empty);
        row = tempRef_row6.argValue;
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref tempCursor, "foo"
        ));
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row8, ref setScope, ref tempCursor));
        row = tempRef_row8.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row9,
            ref setScope, "foo"));
        row = tempRef_row9.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().DeleteSparse(tempRef_row10, ref setScope));
        row = tempRef_row10.argValue;

        // Write a set of sets, successfully insert an empty set into it, and then try to write directly to the inner
        // set.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("prices", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row11, out setScope).Find(tempRef_row12, c.Path);
        row = tempRef_row12.argValue;
        row = tempRef_row11.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row13, ref setScope, c.TypeArgs,
            out setScope));
        row = tempRef_row13.argValue;
        TypeArgument innerType = c.TypeArgs[0];
        TypeArgument itemType = innerType.getTypeArgs().get(0).clone();
        LayoutUniqueScope innerLayout = innerType.getType().<LayoutUniqueScope>TypeAs();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tempCursor1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row14, out tempCursor1).Find(tempRef_row15, "prices.0");
        row = tempRef_row15.argValue;
        row = tempRef_row14.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(innerLayout.WriteScope(tempRef_row16, ref tempCursor1, innerType.getTypeArgs().clone()
            , out innerScope));
        row = tempRef_row16.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tempCursor2;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row17, out tempCursor2).Find(tempRef_row18, "prices.0.0");
        row = tempRef_row18.argValue;
        row = tempRef_row17.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
        ResultAssert.IsSuccess(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempRef_row19,
            tempRef_tempCursor2, 1.0F));
        tempCursor2 = tempRef_tempCursor2.argValue;
        row = tempRef_row19.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row20 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor22 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
        ResultAssert.IsSuccess(innerLayout.MoveField(tempRef_row20, tempRef_innerScope, tempRef_tempCursor22));
        tempCursor2 = tempRef_tempCursor22.argValue;
        innerScope = tempRef_innerScope.argValue;
        row = tempRef_row20.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row21 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row21, ref setScope, ref tempCursor1));
        row = tempRef_row21.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row22 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert setScope.MoveNext(tempRef_row22);
        row = tempRef_row22.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row23 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_setScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(setScope);
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(innerLayout.ReadScope(tempRef_row23, tempRef_setScope, tempOut_innerScope));
        innerScope = tempOut_innerScope.argValue;
        setScope = tempRef_setScope.argValue;
        row = tempRef_row23.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row24 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutFloat32>TypeAs().WriteSparse(tempRef_row24,
            tempRef_innerScope2, 1.0F));
        innerScope = tempRef_innerScope2.argValue;
        row = tempRef_row24.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row25 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutFloat32>TypeAs().DeleteSparse(tempRef_row25,
            tempRef_innerScope3));
        innerScope = tempRef_innerScope3.argValue;
        row = tempRef_row25.argValue;
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
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            ResultAssert.IsSuccess(RowWriter.WriteBuffer(tempRef_row, t1, TypedSetUnitTests.SerializeTodo));
            row = tempRef_row.argValue;

            // Update the existing Set by updating each item with itself.  This ensures that the RowWriter has
            // maintained the unique index correctly.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor root;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            RowCursor.Create(tempRef_row2, out root);
            row = tempRef_row2.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor projScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out projScope).Find(tempRef_row3, c.Path);
            row = tempRef_row3.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row4, ref projScope, out projScope));
            row = tempRef_row4.argValue;
            for (UUID item : t1.Projects) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row5, Utf8String.Empty);
                row = tempRef_row5.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row6,
                    ref tempCursor, item));
                row = tempRef_row6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row7, ref projScope,
                    ref tempCursor));
                row = tempRef_row7.argValue;
            }

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            Todo t2 = this.ReadTodo(tempRef_row8, RowCursor.Create(tempRef_row9, out _));
            row = tempRef_row9.argValue;
            row = tempRef_row8.argValue;
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

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteTodo(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
            row = tempRef_row2.argValue;
            row = tempRef_row.argValue;

            // Attempt to find each item in turn and then delete it.
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor root = RowCursor.Create(tempRef_row3);
            row = tempRef_row3.argValue;
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("projects", out c);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor setScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out setScope).Find(tempRef_row4, c.Path);
            row = tempRef_row4.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref setScope, out setScope));
            row = tempRef_row5.argValue;
            for (UUID elm : t1.Projects) {
                // Verify it is already there.
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row6, Utf8String.Empty);
                row = tempRef_row6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row7,
                    ref tempCursor, elm));
                row = tempRef_row7.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
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
                row = tempRef_row8.argValue;

                // Insert it again with update.
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row9, Utf8String.Empty);
                row = tempRef_row9.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row10,
                    ref tempCursor, elm));
                row = tempRef_row10.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row11, ref setScope,
                    ref tempCursor, UpdateOptions.Update));
                row = tempRef_row11.argValue;

                // Insert it again with upsert.
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row12, Utf8String.Empty);
                row = tempRef_row12.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row13,
                    ref tempCursor, elm));
                row = tempRef_row13.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row14, ref setScope,
                    ref tempCursor));
                row = tempRef_row14.argValue;

                // Insert it again with insert (fail: exists).
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row15, Utf8String.Empty);
                row = tempRef_row15.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row16,
                    ref tempCursor, elm));
                row = tempRef_row16.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row17, ref setScope,
                    ref tempCursor, UpdateOptions.Insert));
                row = tempRef_row17.argValue;

                // Insert it again with insert at (fail: disallowed).
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row18, Utf8String.Empty);
                row = tempRef_row18.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutGuid>TypeAs().WriteSparse(tempRef_row19,
                    ref tempCursor, elm));
                row = tempRef_row19.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row20 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.TypeConstraint(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row20, ref setScope,
                    ref tempCursor, UpdateOptions.InsertAt));
                row = tempRef_row20.argValue;
            }
        }
    }

    private static ShoppingItem ReadShoppingItem(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> matchScope) {
        Layout matchLayout = matchScope.argValue.getLayout();
        ShoppingItem m = new ShoppingItem();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("label", out c);
        tangible.OutObject<String> tempOut_Label = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, matchScope, c, tempOut_Label));
        m.Label = tempOut_Label.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("count", out c);
        tangible.OutObject<Byte> tempOut_Count = new tangible.OutObject<Byte>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ResultAssert.IsSuccess(c.TypeAs<LayoutUInt8>().ReadFixed(ref row, ref matchScope, c, out m.Count));
        ResultAssert.IsSuccess(c.<LayoutUInt8>TypeAs().ReadFixed(row, matchScope, c, tempOut_Count));
        m.Count = tempOut_Count.argValue;
        return m;
    }

    private Todo ReadTodo(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        Todo value = new Todo();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("attendees", out c);
        RowCursor tagsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out tagsScope).Find(row, c.Path);
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
        root.argValue.Clone(out projScope).Find(row, c.Path);
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
        root.argValue.Clone(out checkboxScope).Find(row, c.Path);
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
        root.argValue.Clone(out pricesScope).Find(row, c.Path);
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
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_pricesScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(pricesScope);
                RowCursor innerScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_pricesScope, tempOut_innerScope));
                innerScope = tempOut_innerScope.argValue;
                pricesScope = tempRef_pricesScope.argValue;
                while (innerScope.MoveNext(row)) {
                    LayoutFloat32 itemLayout = innerType.getTypeArgs().get(0).getType().<LayoutFloat32>TypeAs();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
                    float innerItem;
                    tangible.OutObject<Float> tempOut_innerItem = new tangible.OutObject<Float>();
                    ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempRef_innerScope, tempOut_innerItem));
                    innerItem = tempOut_innerItem.argValue;
                    innerScope = tempRef_innerScope.argValue;
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
        root.argValue.Clone(out nestedScope).Find(row, c.Path);
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
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nestedScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nestedScope);
                RowCursor in2Scope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_in2Scope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(in2Layout.ReadScope(row, tempRef_nestedScope, tempOut_in2Scope));
                in2Scope = tempOut_in2Scope.argValue;
                nestedScope = tempRef_nestedScope.argValue;
                while (in2Scope.MoveNext(row)) {
                    TypeArgument in3Type = in2Type.getTypeArgs().get(0).clone();
                    LayoutUniqueScope in3Layout = in3Type.getType().<LayoutUniqueScope>TypeAs();
                    ArrayList<Integer> item2 = new ArrayList<Integer>();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_in2Scope =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(in2Scope);
                    RowCursor in3Scope;
                    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_in3Scope =
                        new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                    ResultAssert.IsSuccess(in3Layout.ReadScope(row, tempRef_in2Scope, tempOut_in3Scope));
                    in3Scope = tempOut_in3Scope.argValue;
                    in2Scope = tempRef_in2Scope.argValue;
                    while (in3Scope.MoveNext(row)) {
                        LayoutInt32 itemLayout = in3Type.getTypeArgs().get(0).getType().<LayoutInt32>TypeAs();
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_in3Scope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(in3Scope);
                        int innerItem;
                        tangible.OutObject<Integer> tempOut_innerItem2 = new tangible.OutObject<Integer>();
                        ResultAssert.IsSuccess(itemLayout.ReadSparse(row, tempRef_in3Scope, tempOut_innerItem2));
                        innerItem = tempOut_innerItem2.argValue;
                        in3Scope = tempRef_in3Scope.argValue;
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
        root.argValue.Clone(out shoppingScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref shoppingScope, out shoppingScope) == Result.Success) {
            value.Shopping = new ArrayList<ShoppingItem>();
            while (shoppingScope.MoveNext(row)) {
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUDT innerLayout = innerType.getType().<LayoutUDT>TypeAs();
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_shoppingScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(shoppingScope);
                RowCursor matchScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_matchScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_shoppingScope, tempOut_matchScope));
                matchScope = tempOut_matchScope.argValue;
                shoppingScope = tempRef_shoppingScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_matchScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(matchScope);
                ShoppingItem item = TypedSetUnitTests.ReadShoppingItem(row, tempRef_matchScope);
                matchScope = tempRef_matchScope.argValue;
                value.Shopping.add(item);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("work", out c);
        RowCursor workScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out workScope).Find(row, c.Path);
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

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_workScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(workScope);
                RowCursor tupleScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.ReadScope(row, tempRef_workScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.argValue;
                workScope = tempRef_workScope.argValue;
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                boolean item1;
                tangible.OutObject<Boolean> tempOut_item1 = new tangible.OutObject<Boolean>();
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().ReadSparse(row, tempRef_tupleScope, tempOut_item1));
                item1 = tempOut_item1.argValue;
                tupleScope = tempRef_tupleScope.argValue;
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                long item2;
                tangible.OutObject<Long> tempOut_item2 = new tangible.OutObject<Long>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutVarUInt>().ReadSparse(ref row, ref tupleScope, out ulong item2));
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().ReadSparse(row, tempRef_tupleScope2, tempOut_item2));
                item2 = tempOut_item2.argValue;
                tupleScope = tempRef_tupleScope2.argValue;
                value.Work.add(Tuple.Create(item1, item2));
            }
        }

        return value;
    }

    private static Result SerializeTodo(tangible.RefObject<RowWriter> writer, TypeArgument typeArg, Todo value) {
        if (value.Projects != null) {
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert writer.argValue.getLayout().TryFind("projects", out c);
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are
            // not converted by C# to Java Converter:
            Result r = writer.argValue.WriteScope("projects", c.TypeArg, value.Projects, (ref RowWriter writer2,
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

    private static void WriteShoppingItem(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> matchScope, TypeArgumentList typeArgs, ShoppingItem m) {
        Layout matchLayout = row.argValue.getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("label", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, matchScope, c, m.Label));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert matchLayout.TryFind("count", out c);
        ResultAssert.IsSuccess(c.<LayoutUInt8>TypeAs().WriteFixed(row, matchScope, c, m.Count));
    }

    private void WriteTodo(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, Todo value) {
        LayoutColumn c;

        if (value.Attendees != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("attendees", tempOut_c);
            c = tempOut_c.argValue;
            RowCursor attendScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out attendScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().WriteSparse(row,
                    ref tempCursor, item));
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_attendScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(attendScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_attendScope,
                    tempRef_tempCursor));
                tempCursor = tempRef_tempCursor.argValue;
                attendScope = tempRef_attendScope.argValue;
            }
        }

        if (value.Projects != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c2 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("projects", tempOut_c2);
            c = tempOut_c2.argValue;
            RowCursor projScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out projScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutGuid>TypeAs().WriteSparse(row,
                    tempRef_tempCursor2, item));
                tempCursor = tempRef_tempCursor2.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_projScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(projScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_projScope,
                    tempRef_tempCursor3));
                tempCursor = tempRef_tempCursor3.argValue;
                projScope = tempRef_projScope.argValue;
            }
        }

        if (value.Checkboxes != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c3 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("checkboxes", tempOut_c3);
            c = tempOut_c3.argValue;
            RowCursor checkboxScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out checkboxScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(row,
                    tempRef_tempCursor4, item));
                tempCursor = tempRef_tempCursor4.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_checkboxScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(checkboxScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor5 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_checkboxScope,
                    tempRef_tempCursor5));
                tempCursor = tempRef_tempCursor5.argValue;
                checkboxScope = tempRef_checkboxScope.argValue;
            }
        }

        if (value.Prices != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c4 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("prices", tempOut_c4);
            c = tempOut_c4.argValue;
            RowCursor pricesScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out pricesScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor1).Find(row, "prices.0");
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
                    root.argValue.Clone(out tempCursor2).Find(row, "prices.0.0");
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2
                        = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempRef_tempCursor2, innerItem));
                    tempCursor2 = tempRef_tempCursor2.argValue;
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor22 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(innerLayout.MoveField(row, tempRef_innerScope, tempRef_tempCursor22));
                    tempCursor2 = tempRef_tempCursor22.argValue;
                    innerScope = tempRef_innerScope.argValue;
                }

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_pricesScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(pricesScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor1 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_pricesScope,
                    tempRef_tempCursor1));
                tempCursor1 = tempRef_tempCursor1.argValue;
                pricesScope = tempRef_pricesScope.argValue;
            }
        }

        if (value.Nested != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c5 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("nested", tempOut_c5);
            c = tempOut_c5.argValue;
            RowCursor nestedScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out nestedScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor1).Find(row, "prices.0");
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
                    root.argValue.Clone(out tempCursor2).Find(row, "prices.0.0");
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
                        root.argValue.Clone(out tempCursor3).Find(row, "prices.0.0.0");
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor3 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor3);
                        ResultAssert.IsSuccess(itemLayout.WriteSparse(row, tempRef_tempCursor3, innerItem));
                        tempCursor3 = tempRef_tempCursor3.argValue;
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_in3Scope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(in3Scope);
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor32 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor3);
                        ResultAssert.IsSuccess(in3Layout.MoveField(row, tempRef_in3Scope, tempRef_tempCursor32));
                        tempCursor3 = tempRef_tempCursor32.argValue;
                        in3Scope = tempRef_in3Scope.argValue;
                    }

                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_in2Scope =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(in2Scope);
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor23 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(in2Layout.MoveField(row, tempRef_in2Scope, tempRef_tempCursor23));
                    tempCursor2 = tempRef_tempCursor23.argValue;
                    in2Scope = tempRef_in2Scope.argValue;
                }

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nestedScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nestedScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor12 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_nestedScope,
                    tempRef_tempCursor12));
                tempCursor1 = tempRef_tempCursor12.argValue;
                nestedScope = tempRef_nestedScope.argValue;
            }
        }

        if (value.Shopping != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c6 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("shopping", tempOut_c6);
            c = tempOut_c6.argValue;
            RowCursor shoppingScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out shoppingScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                RowCursor itemScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_itemScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, tempRef_tempCursor6,
                    innerType.getTypeArgs().clone(), tempOut_itemScope));
                itemScope = tempOut_itemScope.argValue;
                tempCursor = tempRef_tempCursor6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_itemScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(itemScope);
                TypedSetUnitTests.WriteShoppingItem(row, tempRef_itemScope, innerType.getTypeArgs().clone(), item);
                itemScope = tempRef_itemScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_shoppingScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(shoppingScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_shoppingScope,
                    tempRef_tempCursor7));
                tempCursor = tempRef_tempCursor7.argValue;
                shoppingScope = tempRef_shoppingScope.argValue;
            }
        }

        if (value.Work != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c7 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("work", tempOut_c7);
            c = tempOut_c7.argValue;
            RowCursor workScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out workScope).Find(row, c.getPath());
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
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(innerLayout.WriteScope(row, ref tempCursor, innerType.getTypeArgs().clone(),
                    out tupleScope));
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(0).getType().<LayoutBoolean>TypeAs().WriteSparse(row, tempRef_tupleScope, item.Item1));
                tupleScope = tempRef_tupleScope.argValue;
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                ResultAssert.IsSuccess(innerType.getTypeArgs().get(1).getType().<LayoutVarUInt>TypeAs().WriteSparse(row, tempRef_tupleScope2, item.Item2));
                tupleScope = tempRef_tupleScope2.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_workScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(workScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor8 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_workScope,
                    tempRef_tempCursor8));
                tempCursor = tempRef_tempCursor8.argValue;
                workScope = tempRef_workScope.argValue;
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