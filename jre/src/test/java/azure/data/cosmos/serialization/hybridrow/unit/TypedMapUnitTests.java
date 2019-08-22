//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(TypedMapUnitTests.SchemaFile, "TestData")] public sealed class
// TypedMapUnitTests
public final class TypedMapUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final String SchemaFile = "TestData\\MovieSchema.json";
    private Namespace counterSchema;
    private Layout layout;
    private LayoutResolver resolver;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateMovies()
    public void CreateMovies() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

        // ReSharper disable StringLiteralTypo
        Movie t1 = new Movie();
        t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"),
            Map.entry("Carrie", "Leia")));
        t1.Stats = new HashMap<UUID, Double>(Map.ofEntries(Map.entry(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), 11000000.00), Map.entry(UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"),
            1554475.00)));
        t1.Related = new HashMap<String, HashMap<Long, String>>(Map.ofEntries(Map.entry("Mark", new HashMap<Long,
            String>(Map.ofEntries(Map.entry(103359, "Joker"), Map.entry(131646, "Merlin")))), Map.entry("Harrison",
            new HashMap<Long, String>(Map.ofEntries(Map.entry(0082971, "Indy"), Map.entry(83658, "Deckard"))))));
        Earnings tempVar = new Earnings();
        tempVar.Domestic = new BigDecimal(307263857);
        tempVar.Worldwide = new BigDecimal(100000);
        Earnings tempVar2 = new Earnings();
        tempVar2.Domestic = new BigDecimal(15476285);
        tempVar2.Worldwide = new BigDecimal(200000);
        Earnings tempVar3 = new Earnings();
        tempVar3.Domestic = new BigDecimal(138257865);
        tempVar3.Worldwide = new BigDecimal(300000);
        t1.Revenue = new HashMap<LocalDateTime, Earnings>(Map.ofEntries(Map.entry(LocalDateTime.parse("05/25/1977"),
            tempVar), Map.entry(LocalDateTime.parse("08/13/1982"), tempVar2),
            Map.entry(LocalDateTime.parse("01/31/1997"), tempVar3)));

        // ReSharper restore StringLiteralTypo
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteMovie(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Movie t2 = this.ReadMovie(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindAndDelete()
    public void FindAndDelete() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;

        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Mark", "Harrison", "Carrie"));

        for (java.lang.Iterable<String> permutation : expected.Permute()) {
            Movie t1 = new Movie();
            t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"
            ), Map.entry("Carrie", "Leia")));
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteMovie(tempRef_row2, root.Clone(out _), t1);
            row = tempRef_row2.argValue;

            // Attempt to find each item in turn and then delete it.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("cast", out c);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor mapScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out mapScope).Find(tempRef_row3, c.Path);
            row = tempRef_row3.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row4, ref mapScope, out mapScope));
            row = tempRef_row4.argValue;
            for (String key : permutation) {
                KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on" +
                    " key");
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
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row6, tempRef_tempCursor, c.TypeArgs,
                    pair));
                tempCursor = tempRef_tempCursor.argValue;
                row = tempRef_row6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row7, ref mapScope, ref tempCursor,
                    out findScope));
                row = tempRef_row7.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref mapScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_findScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(findScope);
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().DeleteScope(tempRef_row8,
                    tempRef_findScope));
                findScope = tempRef_findScope.argValue;
                row = tempRef_row8.argValue;
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindInMap()
    public void FindInMap() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;

        Movie t1 = new Movie();
        t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"),
            Map.entry("Carrie", "Leia")));
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempRef_row2);
        row = tempRef_row2.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc1 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc1);
        this.WriteMovie(tempRef_row3, tempRef_rc1, t1);
        rc1 = tempRef_rc1.argValue;
        row = tempRef_row3.argValue;

        // Attempt to find each item in turn.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref mapScope, out mapScope));
        row = tempRef_row5.argValue;
        for (String key : t1.Cast.keySet()) {
            KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on key");
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
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
            ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row7, tempRef_tempCursor, c.TypeArgs, pair));
            tempCursor = tempRef_tempCursor.argValue;
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
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref mapScope, ref tempCursor,
                out findScope));
            row = tempRef_row8.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_findScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(findScope);
            KeyValuePair<String, String> foundPair;
            tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_foundPair =
                new tangible.OutObject<KeyValuePair<TKey, TValue>>();
            ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempRef_row9, tempRef_findScope, c.TypeArgs,
                tempOut_foundPair));
            foundPair = tempOut_foundPair.argValue;
            findScope = tempRef_findScope.argValue;
            row = tempRef_row9.argValue;
            Assert.AreEqual(key, foundPair.Key, String.format("Failed to find t1.Cast[%1$s]", key));
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString(TypedMapUnitTests.SchemaFile);
        this.counterSchema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.counterSchema);
        this.layout = this.resolver.Resolve(tangible.ListHelper.find(this.counterSchema.getSchemas(),
            x -> x.Name.equals("Movie")).SchemaId);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUniquenessViolations()
    public void PreventUniquenessViolations() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;

        Movie t1 = new Movie();
        t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke")));
        t1.Stats = new HashMap<UUID, Double>(Map.ofEntries(Map.entry(UUID.fromString("{4674962B-CE11-4916-81C5" +
            "-0421EE36F168}"), 11000000.00)));
        t1.Related = new HashMap<String, HashMap<Long, String>>(Map.ofEntries(Map.entry("Mark", new HashMap<Long,
            String>(Map.ofEntries(Map.entry(103359, "Joker"))))));
        Earnings tempVar = new Earnings();
        tempVar.Domestic = new BigDecimal(307263857);
        tempVar.Worldwide = new BigDecimal(100000);
        t1.Revenue = new HashMap<LocalDateTime, Earnings>(Map.ofEntries(Map.entry(LocalDateTime.parse("05/25/1977"),
            tempVar)));

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempRef_row2);
        row = tempRef_row2.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_rc1 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(rc1);
        this.WriteMovie(tempRef_row3, tempRef_rc1, t1);
        rc1 = tempRef_rc1.argValue;
        row = tempRef_row3.argValue;

        // Attempt to insert duplicate items in existing sets.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref mapScope, out mapScope));
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
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row7, tempRef_tempCursor, c.TypeArgs,
            KeyValuePair.Create("Mark", "Luke")));
        tempCursor = tempRef_tempCursor.argValue;
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row8, ref mapScope, ref tempCursor,
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
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut__ = new tangible.OutObject<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row10, tempRef_tempCursor2, c.TypeArgs,
            tempOut__));
        _ = tempOut__.argValue;
        tempCursor = tempRef_tempCursor2.argValue;
        row = tempRef_row10.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row11, Utf8String.Empty);
        row = tempRef_row11.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row12, tempRef_tempCursor3, c.TypeArgs,
            KeyValuePair.Create("Mark", "Joker")));
        tempCursor = tempRef_tempCursor3.argValue;
        row = tempRef_row12.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row13, ref mapScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row13.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row14, Utf8String.Empty);
        row = tempRef_row14.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut__2 =
            new tangible.OutObject<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row15, tempRef_tempCursor4, c.TypeArgs,
            tempOut__2));
        _ = tempOut__2.argValue;
        tempCursor = tempRef_tempCursor4.argValue;
        row = tempRef_row15.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("stats", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row16, c.Path);
        row = tempRef_row16.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row17, ref mapScope, out mapScope));
        row = tempRef_row17.argValue;
        KeyValuePair<UUID, Double> pair = KeyValuePair.Create(UUID.fromString("{4674962B-CE11-4916-81C5-0421EE36F168" +
            "}"), 11000000.00);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row18, Utf8String.Empty);
        row = tempRef_row18.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row19, tempRef_tempCursor5, c.TypeArgs, pair));
        tempCursor = tempRef_tempCursor5.argValue;
        row = tempRef_row19.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row20 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row20, ref mapScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row20.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row21 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row21, Utf8String.Empty);
        row = tempRef_row21.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row22 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_pair = new tangible.OutObject<KeyValuePair<TKey,
            TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row22, tempRef_tempCursor6, c.TypeArgs,
            tempOut_pair));
        pair = tempOut_pair.argValue;
        tempCursor = tempRef_tempCursor6.argValue;
        row = tempRef_row22.argValue;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUpdatesInNonUpdatableScope()
    public void PreventUpdatesInNonUpdatableScope() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;

        // Write a map and then try to write directly into it.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row2, c.Path);
        row = tempRef_row2.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row3, ref mapScope, c.TypeArgs,
            out mapScope));
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mapScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mapScope);
        ResultAssert.InsufficientPermissions(TypedMapUnitTests.WriteKeyValue(tempRef_row4, tempRef_mapScope,
            c.TypeArgs, KeyValuePair.Create("Mark", "Joker")));
        mapScope = tempRef_mapScope.argValue;
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row5, "cast.0");
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row6, tempRef_tempCursor, c.TypeArgs,
            KeyValuePair.Create("Mark", "Joker")));
        tempCursor = tempRef_tempCursor.argValue;
        row = tempRef_row6.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row7, ref mapScope, ref tempCursor));
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row8, "cast.0");
        row = tempRef_row8.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut__ = new tangible.OutObject<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row9, tempRef_tempCursor2, c.TypeArgs, tempOut__));
        _ = tempOut__.argValue;
        tempCursor = tempRef_tempCursor2.argValue;
        row = tempRef_row9.argValue;

        // Write a map of maps, successfully insert an empty map into it, and then try to write directly to the inner
        // map.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("related", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row10, c.Path);
        row = tempRef_row10.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row11, ref mapScope, c.TypeArgs,
            out mapScope));
        row = tempRef_row11.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        LayoutIndexedScope tupleLayout =
            c.<LayoutUniqueScope>TypeAs().FieldType(ref mapScope).<LayoutIndexedScope>TypeAs();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row12, "related.0");
        row = tempRef_row12.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(tupleLayout.WriteScope(tempRef_row13, ref tempCursor, c.TypeArgs, out tupleScope));
        row = tempRef_row13.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row14, ref tupleScope,
            "Mark"));
        row = tempRef_row14.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row15);
        row = tempRef_row15.argValue;
        TypeArgument valueType = c.TypeArgs[1];
        LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(valueLayout.WriteScope(tempRef_row16, ref tupleScope, valueType.getTypeArgs().clone(),
            out innerScope));
        row = tempRef_row16.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row17, ref mapScope, ref tempCursor));
        row = tempRef_row17.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert mapScope.MoveNext(tempRef_row18);
        row = tempRef_row18.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mapScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mapScope);
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(tupleLayout.ReadScope(tempRef_row19, tempRef_mapScope2, tempOut_tupleScope));
        tupleScope = tempOut_tupleScope.argValue;
        mapScope = tempRef_mapScope2.argValue;
        row = tempRef_row19.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row20 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row20);
        row = tempRef_row20.argValue;

        // Skip key.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row21 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row21);
        row = tempRef_row21.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row22 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.IsSuccess(valueLayout.ReadScope(tempRef_row22, tempRef_tupleScope, tempOut_innerScope));
        innerScope = tempOut_innerScope.argValue;
        tupleScope = tempRef_tupleScope.argValue;
        row = tempRef_row22.argValue;
        TypeArgument itemType = valueType.getTypeArgs().get(0).clone();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row23 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert !innerScope.MoveNext(tempRef_row23);
        row = tempRef_row23.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row24 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutInt64>TypeAs().WriteSparse(tempRef_row24,
            tempRef_innerScope, 1));
        innerScope = tempRef_innerScope.argValue;
        row = tempRef_row24.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row25 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutInt64>TypeAs().DeleteSparse(tempRef_row25,
            tempRef_innerScope2));
        innerScope = tempRef_innerScope2.argValue;
        row = tempRef_row25.argValue;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void UpdateInMap()
    public void UpdateInMap() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.argValue;

        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Mark", "Harrison", "Carrie"));

        for (java.lang.Iterable<String> permutation : expected.Permute()) {
            Movie t1 = new Movie();
            t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"
            ), Map.entry("Carrie", "Leia")));
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteMovie(tempRef_row2, root.Clone(out _), t1);
            row = tempRef_row2.argValue;

            // Attempt to find each item in turn and then delete it.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("cast", out c);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            RowCursor mapScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out mapScope).Find(tempRef_row3, c.Path);
            row = tempRef_row3.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row4, ref mapScope, out mapScope));
            row = tempRef_row4.argValue;
            for (String key : permutation) {
                // Verify it is already there.
                KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on" +
                    " key");
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
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row6, tempRef_tempCursor, c.TypeArgs,
                    pair));
                tempCursor = tempRef_tempCursor.argValue;
                row = tempRef_row6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row7, ref mapScope, ref tempCursor,
                    out findScope));
                row = tempRef_row7.argValue;

                // Insert it again with update.
                KeyValuePair<String, String> updatePair = new KeyValuePair<String, String>(key, "update value");
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row8, Utf8String.Empty);
                row = tempRef_row8.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row9, tempRef_tempCursor2, c.TypeArgs,
                    updatePair));
                tempCursor = tempRef_tempCursor2.argValue;
                row = tempRef_row9.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row10, ref mapScope,
                    ref tempCursor, UpdateOptions.Update));
                row = tempRef_row10.argValue;

                // Verify that the value was updated.
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row11, Utf8String.Empty);
                row = tempRef_row11.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row12, tempRef_tempCursor3, c.TypeArgs
                    , pair));
                tempCursor = tempRef_tempCursor3.argValue;
                row = tempRef_row12.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row13, ref mapScope, ref tempCursor
                    , out findScope));
                row = tempRef_row13.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_findScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(findScope);
                KeyValuePair<String, String> foundPair;
                tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_foundPair =
                    new tangible.OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempRef_row14, tempRef_findScope, c.TypeArgs,
                    tempOut_foundPair));
                foundPair = tempOut_foundPair.argValue;
                findScope = tempRef_findScope.argValue;
                row = tempRef_row14.argValue;
                assert key == foundPair.Key;
                assert updatePair.Value == foundPair.Value;

                // Insert it again with upsert.
                updatePair = new KeyValuePair<String, String>(key, "upsert value");
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row15, Utf8String.Empty);
                row = tempRef_row15.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row16, tempRef_tempCursor4, c.TypeArgs
                    , updatePair));
                tempCursor = tempRef_tempCursor4.argValue;
                row = tempRef_row16.argValue;

                // ReSharper disable once RedundantArgumentDefaultValue
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row17, ref mapScope,
                    ref tempCursor, UpdateOptions.Upsert));
                row = tempRef_row17.argValue;

                // Verify that the value was upserted.
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row18, Utf8String.Empty);
                row = tempRef_row18.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor5 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row19, tempRef_tempCursor5, c.TypeArgs
                    , pair));
                tempCursor = tempRef_tempCursor5.argValue;
                row = tempRef_row19.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row20 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row20, ref mapScope, ref tempCursor
                    , out findScope));
                row = tempRef_row20.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row21 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_findScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(findScope);
                tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_foundPair2 =
                    new tangible.OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempRef_row21, tempRef_findScope2, c.TypeArgs,
                    tempOut_foundPair2));
                foundPair = tempOut_foundPair2.argValue;
                findScope = tempRef_findScope2.argValue;
                row = tempRef_row21.argValue;
                assert key == foundPair.Key;
                assert updatePair.Value == foundPair.Value;

                // Insert it again with insert (fail: exists).
                updatePair = new KeyValuePair<String, String>(key, "insert value");
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row22 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row22, Utf8String.Empty);
                row = tempRef_row22.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row23 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row23, tempRef_tempCursor6, c.TypeArgs
                    , updatePair));
                tempCursor = tempRef_tempCursor6.argValue;
                row = tempRef_row23.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row24 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row24, ref mapScope,
                    ref tempCursor, UpdateOptions.Insert));
                row = tempRef_row24.argValue;

                // Insert it again with insert at (fail: disallowed).
                updatePair = new KeyValuePair<String, String>(key, "insertAt value");
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row25 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row25, Utf8String.Empty);
                row = tempRef_row25.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row26 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row26, tempRef_tempCursor7, c.TypeArgs
                    , updatePair));
                tempCursor = tempRef_tempCursor7.argValue;
                row = tempRef_row26.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row27 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.TypeConstraint(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row27, ref mapScope,
                    ref tempCursor, UpdateOptions.InsertAt));
                row = tempRef_row27.argValue;
            }
        }
    }

    private static Earnings ReadEarnings(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> udtScope) {
        Layout udt = udtScope.argValue.getLayout();
        Earnings m = new Earnings();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("domestic", out c);
        tangible.OutObject<BigDecimal> tempOut_Domestic = new tangible.OutObject<BigDecimal>();
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().ReadFixed(row, udtScope, c, tempOut_Domestic));
        m.Domestic = tempOut_Domestic.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("worldwide", out c);
        tangible.OutObject<BigDecimal> tempOut_Worldwide = new tangible.OutObject<BigDecimal>();
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().ReadFixed(row, udtScope, c, tempOut_Worldwide));
        m.Worldwide = tempOut_Worldwide.argValue;
        return m;
    }

    private static <TKey, TValue> Result ReadKeyValue(tangible.RefObject<RowBuffer> row,
                                                      tangible.RefObject<RowCursor> scope, TypeArgumentList typeArgs,
                                                      tangible.OutObject<KeyValuePair<TKey, TValue>> pair) {
        pair.argValue = null;
        LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
        RowCursor tupleScope;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        Result r = tupleLayout.ReadScope(row, scope, tempOut_tupleScope);
        tupleScope = tempOut_tupleScope.argValue;
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        TKey key;
        tangible.OutObject<TKey> tempOut_key = new tangible.OutObject<TKey>();
        r = typeArgs.get(0).getType().<LayoutType<TKey>>TypeAs().ReadSparse(row, tempRef_tupleScope, tempOut_key);
        key = tempOut_key.argValue;
        tupleScope = tempRef_tupleScope.argValue;
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        TValue value;
        tangible.OutObject<TValue> tempOut_value = new tangible.OutObject<TValue>();
        r = typeArgs.get(1).getType().<LayoutType<TValue>>TypeAs().ReadSparse(row, tempRef_tupleScope2, tempOut_value);
        value = tempOut_value.argValue;
        tupleScope = tempRef_tupleScope2.argValue;
        if (r != Result.Success) {
            return r;
        }

        pair.argValue = new KeyValuePair<TKey, TValue>(key, value);
        return Result.Success;
    }

    private Movie ReadMovie(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        Movie value = new Movie();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        RowCursor castScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out castScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref castScope, out castScope) == Result.Success) {
            value.Cast = new HashMap<String, String>();
            while (castScope.MoveNext(row)) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_castScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(castScope);
                KeyValuePair<String, String> item;
                tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_item =
                    new tangible.OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempRef_castScope, c.TypeArgs,
                    tempOut_item));
                item = tempOut_item.argValue;
                castScope = tempRef_castScope.argValue;
                value.Cast.put(item.Key, item.Value);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("stats", out c);
        RowCursor statsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out statsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref statsScope, out statsScope) == Result.Success) {
            value.Stats = new HashMap<UUID, Double>();
            while (statsScope.MoveNext(row)) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_statsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(statsScope);
                KeyValuePair<java.util.UUID, Double> item;
                tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_item2 =
                    new tangible.OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempRef_statsScope, c.TypeArgs,
                    tempOut_item2));
                item = tempOut_item2.argValue;
                statsScope = tempRef_statsScope.argValue;
                value.Stats.put(item.Key, item.Value);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("related", out c);
        RowCursor relatedScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out relatedScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref relatedScope, out relatedScope) == Result.Success) {
            value.Related = new HashMap<String, HashMap<Long, String>>();
            TypeArgument keyType = c.TypeArgs[0];
            TypeArgument valueType = c.TypeArgs[1];
            LayoutUtf8 keyLayout = keyType.getType().<LayoutUtf8>TypeAs();
            LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
            while (relatedScope.MoveNext(row)) {
                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_relatedScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(relatedScope);
                RowCursor tupleScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(tupleLayout.ReadScope(row, tempRef_relatedScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.argValue;
                relatedScope = tempRef_relatedScope.argValue;
                assert tupleScope.MoveNext(row);
                String itemKey;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(keyLayout.ReadSparse(row, ref tupleScope, out itemKey));
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor itemValueScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_itemValueScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(valueLayout.ReadScope(row, tempRef_tupleScope, tempOut_itemValueScope));
                itemValueScope = tempOut_itemValueScope.argValue;
                tupleScope = tempRef_tupleScope.argValue;
                HashMap<Long, String> itemValue = new HashMap<Long, String>();
                while (itemValueScope.MoveNext(row)) {
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_itemValueScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(itemValueScope);
                    KeyValuePair<Long, String> innerItem;
                    tangible.OutObject<KeyValuePair<TKey, TValue>> tempOut_innerItem =
                        new tangible.OutObject<KeyValuePair<TKey, TValue>>();
                    ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempRef_itemValueScope,
                        valueType.getTypeArgs().clone(), tempOut_innerItem));
                    innerItem = tempOut_innerItem.argValue;
                    itemValueScope = tempRef_itemValueScope.argValue;
                    itemValue.put(innerItem.Key, innerItem.Value);
                }

                value.Related.put(itemKey, itemValue);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("revenue", out c);
        RowCursor revenueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out revenueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref revenueScope, out revenueScope) == Result.Success) {
            value.Revenue = new HashMap<LocalDateTime, Earnings>();
            TypeArgument keyType = c.TypeArgs[0];
            TypeArgument valueType = c.TypeArgs[1];
            LayoutDateTime keyLayout = keyType.getType().<LayoutDateTime>TypeAs();
            LayoutUDT valueLayout = valueType.getType().<LayoutUDT>TypeAs();
            while (revenueScope.MoveNext(row)) {
                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_revenueScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(revenueScope);
                RowCursor tupleScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope2 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(tupleLayout.ReadScope(row, tempRef_revenueScope, tempOut_tupleScope2));
                tupleScope = tempOut_tupleScope2.argValue;
                revenueScope = tempRef_revenueScope.argValue;
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                java.time.LocalDateTime itemKey;
                tangible.OutObject<LocalDateTime> tempOut_itemKey = new tangible.OutObject<LocalDateTime>();
                ResultAssert.IsSuccess(keyLayout.ReadSparse(row, tempRef_tupleScope2, tempOut_itemKey));
                itemKey = tempOut_itemKey.argValue;
                tupleScope = tempRef_tupleScope2.argValue;
                assert tupleScope.MoveNext(row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope3 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor itemValueScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_itemValueScope2 = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(valueLayout.ReadScope(row, tempRef_tupleScope3, tempOut_itemValueScope2));
                itemValueScope = tempOut_itemValueScope2.argValue;
                tupleScope = tempRef_tupleScope3.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_itemValueScope2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(itemValueScope);
                Earnings itemValue = TypedMapUnitTests.ReadEarnings(row, tempRef_itemValueScope2);
                itemValueScope = tempRef_itemValueScope2.argValue;

                value.Revenue.put(itemKey, itemValue);
            }
        }

        return value;
    }

    private static void WriteEarnings(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> udtScope, TypeArgumentList typeArgs, Earnings m) {
        Layout udt = row.argValue.getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("domestic", out c);
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().WriteFixed(row, udtScope, c, m.Domestic));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("worldwide", out c);
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().WriteFixed(row, udtScope, c, m.Worldwide));
    }

    private static <TKey, TValue> Result WriteKeyValue(tangible.RefObject<RowBuffer> row,
                                                       tangible.RefObject<RowCursor> scope, TypeArgumentList typeArgs, KeyValuePair<TKey, TValue> pair) {
        LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = tupleLayout.WriteScope(row, scope, typeArgs.clone(), out tupleScope);
        if (r != Result.Success) {
            return r;
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        r = typeArgs.get(0).getType().<LayoutType<TKey>>TypeAs().WriteSparse(row, tempRef_tupleScope, pair.Key);
        tupleScope = tempRef_tupleScope.argValue;
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
        r = typeArgs.get(1).getType().<LayoutType<TValue>>TypeAs().WriteSparse(row, tempRef_tupleScope2, pair.Value);
        tupleScope = tempRef_tupleScope2.argValue;
        return r;
    }

    private void WriteMovie(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, Movie value) {
        LayoutColumn c;

        if (value.Cast != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("cast", tempOut_c);
            c = tempOut_c.argValue;
            RowCursor castScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out castScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref castScope,
                c.getTypeArgs().clone(), out castScope));
            for (KeyValuePair<String, String> item : value.Cast) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempRef_tempCursor,
                    c.getTypeArgs().clone(), item));
                tempCursor = tempRef_tempCursor.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_castScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(castScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_castScope,
                    tempRef_tempCursor2));
                tempCursor = tempRef_tempCursor2.argValue;
                castScope = tempRef_castScope.argValue;
            }
        }

        if (value.Stats != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c2 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("stats", tempOut_c2);
            c = tempOut_c2.argValue;
            RowCursor statsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out statsScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref statsScope,
                c.getTypeArgs().clone(), out statsScope));
            for (KeyValuePair<UUID, Double> item : value.Stats) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.argValue.Clone(out tempCursor).Find(row, Utf8String.Empty);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempRef_tempCursor3,
                    c.getTypeArgs().clone(), item));
                tempCursor = tempRef_tempCursor3.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_statsScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(statsScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_statsScope,
                    tempRef_tempCursor4));
                tempCursor = tempRef_tempCursor4.argValue;
                statsScope = tempRef_statsScope.argValue;
            }
        }

        if (value.Related != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c3 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("related", tempOut_c3);
            c = tempOut_c3.argValue;
            RowCursor relatedScoped;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out relatedScoped).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref relatedScoped,
                c.getTypeArgs().clone(), out relatedScoped));
            for (KeyValuePair<String, HashMap<Long, String>> item : value.Related) {
                assert item.Value != null;

                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.argValue.Clone(out tempCursor1).Find(row, "related.0");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleLayout.WriteScope(row, ref tempCursor1, c.getTypeArgs().clone(),
                    out tupleScope));
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().WriteSparse(row,
                    ref tupleScope, item.Key));
                assert tupleScope.MoveNext(row);
                TypeArgument valueType = c.getTypeArgs().get(1).clone();
                LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
                RowCursor innerScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(valueLayout.WriteScope(row, ref tupleScope, valueType.getTypeArgs().clone(),
                    out innerScope));
                for (KeyValuePair<Long, String> innerItem : item.Value) {
                    RowCursor tempCursor2;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'OutObject' helper class unless the method is within the
                    // code being modified:
                    root.argValue.Clone(out tempCursor2).Find(row, "related.0.0");
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor2
                        = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempRef_tempCursor2,
                        valueType.getTypeArgs().clone(), innerItem));
                    tempCursor2 = tempRef_tempCursor2.argValue;
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_innerScope =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(innerScope);
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor22 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(valueLayout.MoveField(row, tempRef_innerScope, tempRef_tempCursor22));
                    tempCursor2 = tempRef_tempCursor22.argValue;
                    innerScope = tempRef_innerScope.argValue;
                }

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_relatedScoped =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(relatedScoped);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor1 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_relatedScoped,
                    tempRef_tempCursor1));
                tempCursor1 = tempRef_tempCursor1.argValue;
                relatedScoped = tempRef_relatedScoped.argValue;
            }
        }

        if (value.Revenue != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c4 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("revenue", tempOut_c4);
            c = tempOut_c4.argValue;
            RowCursor revenueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out revenueScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref revenueScope,
                c.getTypeArgs().clone(), out revenueScope));
            for (KeyValuePair<LocalDateTime, Earnings> item : value.Revenue) {
                assert item.Value != null;

                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.argValue.Clone(out tempCursor1).Find(row, "revenue.0");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleLayout.WriteScope(row, ref tempCursor1, c.getTypeArgs().clone(),
                    out tupleScope));
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutDateTime>TypeAs().WriteSparse(row,
                    tempRef_tupleScope, item.Key));
                tupleScope = tempRef_tupleScope.argValue;
                assert tupleScope.MoveNext(row);
                TypeArgument valueType = c.getTypeArgs().get(1).clone();
                LayoutUDT valueLayout = valueType.getType().<LayoutUDT>TypeAs();
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor itemScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_itemScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(valueLayout.WriteScope(row, tempRef_tupleScope2,
                    valueType.getTypeArgs().clone(), tempOut_itemScope));
                itemScope = tempOut_itemScope.argValue;
                tupleScope = tempRef_tupleScope2.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_itemScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(itemScope);
                TypedMapUnitTests.WriteEarnings(row, tempRef_itemScope, valueType.getTypeArgs().clone(), item.Value);
                itemScope = tempRef_itemScope.argValue;

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_revenueScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(revenueScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tempCursor12 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_revenueScope,
                    tempRef_tempCursor12));
                tempCursor1 = tempRef_tempCursor12.argValue;
                revenueScope = tempRef_revenueScope.argValue;
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class Earnings
    private final static class Earnings {
        public BigDecimal Domestic = new BigDecimal(0);
        public BigDecimal Worldwide = new BigDecimal(0);

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Earnings;
            Earnings earnings = tempVar ? (Earnings)obj : null;
            return tempVar && this.equals(earnings);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                return (this.Domestic.hashCode() * 397) ^ this.Worldwide.hashCode();
            }
        }

        private boolean equals(Earnings other) {
            return this.Domestic.compareTo(other.Domestic) == 0 && this.Worldwide.compareTo(other.Worldwide) == 0;
        }
    }

    private static class KeyValuePair {
        public static <TKey, TValue> KeyValuePair<TKey, TValue> Create(TKey key, TValue value) {
            return new KeyValuePair<TKey, TValue>(key, value);
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class Movie
    private final static class Movie {
        public HashMap<String, String> Cast;
        public HashMap<String, HashMap<Long, String>> Related;
        public HashMap<LocalDateTime, Earnings> Revenue;
        public HashMap<UUID, Double> Stats;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Movie;
            Movie movie = tempVar ? (Movie)obj : null;
            return tempVar && this.equals(movie);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                int hashCode = 0;
                hashCode = (hashCode * 397) ^ (this.Cast == null ? null : this.Cast.hashCode() != null ? this.Cast.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Stats == null ? null : this.Stats.hashCode() != null ? this.Stats.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Related == null ? null : this.Related.hashCode() != null ? this.Related.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Revenue == null ? null : this.Revenue.hashCode() != null ? this.Revenue.hashCode() : 0);
                return hashCode;
            }
        }

        private static <TKey, TValue> boolean MapEquals(HashMap<TKey, TValue> left, HashMap<TKey, TValue> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (KeyValuePair<TKey, TValue> item : left) {
                TValue value;
                if (!(right.containsKey(item.Key) && (value = right.get(item.Key)) == value)) {
                    return false;
                }

                if (!item.Value.equals(value)) {
                    return false;
                }
            }

            return true;
        }

        private static <TKey1, TKey2, TValue> boolean NestedMapEquals(HashMap<TKey1, HashMap<TKey2, TValue>> left, HashMap<TKey1, HashMap<TKey2, TValue>> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (KeyValuePair<TKey1, HashMap<TKey2, TValue>> item : left) {
                java.util.HashMap<TKey2, TValue> value;
                if (!(right.containsKey(item.Key) && (value = right.get(item.Key)) == value)) {
                    return false;
                }

                if (!Movie.MapEquals(item.Value, value)) {
                    return false;
                }
            }

            return true;
        }

        private boolean equals(Movie other) {
            return (this.Cast == other.Cast || ((this.Cast != null) && (other.Cast != null) && Movie.MapEquals(this.Cast, other.Cast))) && (this.Stats == other.Stats || ((this.Stats != null) && (other.Stats != null) && Movie.MapEquals(this.Stats, other.Stats))) && (this.Related == other.Related || ((this.Related != null) && (other.Related != null) && Movie.NestedMapEquals(this.Related, other.Related))) && (this.Revenue == other.Revenue || ((this.Revenue != null) && (other.Revenue != null) && Movie.MapEquals(this.Revenue, other.Revenue)));
        }
    }
}