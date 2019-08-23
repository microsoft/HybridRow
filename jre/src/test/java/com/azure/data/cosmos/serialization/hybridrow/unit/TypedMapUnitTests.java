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
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteMovie(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.get();
        row = tempRef_row.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Movie t2 = this.ReadMovie(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.get();
        row = tempRef_row3.get();
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindAndDelete()
    public void FindAndDelete() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();

        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Mark", "Harrison", "Carrie"));

        for (java.lang.Iterable<String> permutation : expected.Permute()) {
            Movie t1 = new Movie();
            t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"
            ), Map.entry("Carrie", "Leia")));
            RefObject<RowBuffer> tempRef_row2 =
                new RefObject<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteMovie(tempRef_row2, root.Clone(out _), t1);
            row = tempRef_row2.get();

            // Attempt to find each item in turn and then delete it.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("cast", out c);
            RefObject<RowBuffer> tempRef_row3 =
                new RefObject<RowBuffer>(row);
            RowCursor mapScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out mapScope).Find(tempRef_row3, c.Path);
            row = tempRef_row3.get();
            RefObject<RowBuffer> tempRef_row4 =
                new RefObject<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row4, ref mapScope, out mapScope));
            row = tempRef_row4.get();
            for (String key : permutation) {
                KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on" +
                    " key");
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
                RefObject<RowCursor> tempRef_tempCursor =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row6, tempRef_tempCursor, c.TypeArgs,
                    pair));
                tempCursor = tempRef_tempCursor.get();
                row = tempRef_row6.get();
                RefObject<RowBuffer> tempRef_row7 =
                    new RefObject<RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row7, ref mapScope, ref tempCursor,
                    out findScope));
                row = tempRef_row7.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref mapScope);
                RefObject<RowBuffer> tempRef_row8 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_findScope =
                    new RefObject<RowCursor>(findScope);
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().DeleteScope(tempRef_row8,
                    tempRef_findScope));
                findScope = tempRef_findScope.get();
                row = tempRef_row8.get();
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindInMap()
    public void FindInMap() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();

        Movie t1 = new Movie();
        t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"),
            Map.entry("Carrie", "Leia")));
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempRef_row2);
        row = tempRef_row2.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc1 =
            new RefObject<RowCursor>(rc1);
        this.WriteMovie(tempRef_row3, tempRef_rc1, t1);
        rc1 = tempRef_rc1.get();
        row = tempRef_row3.get();

        // Attempt to find each item in turn.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.get();
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref mapScope, out mapScope));
        row = tempRef_row5.get();
        for (String key : t1.Cast.keySet()) {
            KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on key");
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
            RefObject<RowCursor> tempRef_tempCursor =
                new RefObject<RowCursor>(tempCursor);
            ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row7, tempRef_tempCursor, c.TypeArgs, pair));
            tempCursor = tempRef_tempCursor.get();
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
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row8, ref mapScope, ref tempCursor,
                out findScope));
            row = tempRef_row8.get();
            RefObject<RowBuffer> tempRef_row9 =
                new RefObject<RowBuffer>(row);
            RefObject<RowCursor> tempRef_findScope =
                new RefObject<RowCursor>(findScope);
            KeyValuePair<String, String> foundPair;
            OutObject<KeyValuePair<TKey, TValue>> tempOut_foundPair =
                new OutObject<KeyValuePair<TKey, TValue>>();
            ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempRef_row9, tempRef_findScope, c.TypeArgs,
                tempOut_foundPair));
            foundPair = tempOut_foundPair.get();
            findScope = tempRef_findScope.get();
            row = tempRef_row9.get();
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
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();

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

        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempRef_row2);
        row = tempRef_row2.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_rc1 =
            new RefObject<RowCursor>(rc1);
        this.WriteMovie(tempRef_row3, tempRef_rc1, t1);
        rc1 = tempRef_rc1.get();
        row = tempRef_row3.get();

        // Attempt to insert duplicate items in existing sets.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.get();
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row5, ref mapScope, out mapScope));
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
        RefObject<RowCursor> tempRef_tempCursor =
            new RefObject<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row7, tempRef_tempCursor, c.TypeArgs,
            KeyValuePair.Create("Mark", "Luke")));
        tempCursor = tempRef_tempCursor.get();
        row = tempRef_row7.get();
        RefObject<RowBuffer> tempRef_row8 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row8, ref mapScope, ref tempCursor,
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
        RefObject<RowCursor> tempRef_tempCursor2 =
            new RefObject<RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        OutObject<KeyValuePair<TKey, TValue>> tempOut__ = new OutObject<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row10, tempRef_tempCursor2, c.TypeArgs,
            tempOut__));
        _ = tempOut__.get();
        tempCursor = tempRef_tempCursor2.get();
        row = tempRef_row10.get();
        RefObject<RowBuffer> tempRef_row11 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row11, Utf8String.Empty);
        row = tempRef_row11.get();
        RefObject<RowBuffer> tempRef_row12 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor3 =
            new RefObject<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row12, tempRef_tempCursor3, c.TypeArgs,
            KeyValuePair.Create("Mark", "Joker")));
        tempCursor = tempRef_tempCursor3.get();
        row = tempRef_row12.get();
        RefObject<RowBuffer> tempRef_row13 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row13, ref mapScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row13.get();
        RefObject<RowBuffer> tempRef_row14 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row14, Utf8String.Empty);
        row = tempRef_row14.get();
        RefObject<RowBuffer> tempRef_row15 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor4 =
            new RefObject<RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        OutObject<KeyValuePair<TKey, TValue>> tempOut__2 =
            new OutObject<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row15, tempRef_tempCursor4, c.TypeArgs,
            tempOut__2));
        _ = tempOut__2.get();
        tempCursor = tempRef_tempCursor4.get();
        row = tempRef_row15.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("stats", out c);
        RefObject<RowBuffer> tempRef_row16 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row16, c.Path);
        row = tempRef_row16.get();
        RefObject<RowBuffer> tempRef_row17 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row17, ref mapScope, out mapScope));
        row = tempRef_row17.get();
        KeyValuePair<UUID, Double> pair = KeyValuePair.Create(UUID.fromString("{4674962B-CE11-4916-81C5-0421EE36F168" +
            "}"), 11000000.00);
        RefObject<RowBuffer> tempRef_row18 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row18, Utf8String.Empty);
        row = tempRef_row18.get();
        RefObject<RowBuffer> tempRef_row19 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor5 =
            new RefObject<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row19, tempRef_tempCursor5, c.TypeArgs, pair));
        tempCursor = tempRef_tempCursor5.get();
        row = tempRef_row19.get();
        RefObject<RowBuffer> tempRef_row20 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row20, ref mapScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempRef_row20.get();
        RefObject<RowBuffer> tempRef_row21 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row21, Utf8String.Empty);
        row = tempRef_row21.get();
        RefObject<RowBuffer> tempRef_row22 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor6 =
            new RefObject<RowCursor>(tempCursor);
        OutObject<KeyValuePair<TKey, TValue>> tempOut_pair = new OutObject<KeyValuePair<TKey,
                    TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row22, tempRef_tempCursor6, c.TypeArgs,
            tempOut_pair));
        pair = tempOut_pair.get();
        tempCursor = tempRef_tempCursor6.get();
        row = tempRef_row22.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUpdatesInNonUpdatableScope()
    public void PreventUpdatesInNonUpdatableScope() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();

        // Write a map and then try to write directly into it.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row2, c.Path);
        row = tempRef_row2.get();
        RefObject<RowBuffer> tempRef_row3 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row3, ref mapScope, c.TypeArgs,
            out mapScope));
        row = tempRef_row3.get();
        RefObject<RowBuffer> tempRef_row4 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_mapScope =
            new RefObject<RowCursor>(mapScope);
        ResultAssert.InsufficientPermissions(TypedMapUnitTests.WriteKeyValue(tempRef_row4, tempRef_mapScope,
            c.TypeArgs, KeyValuePair.Create("Mark", "Joker")));
        mapScope = tempRef_mapScope.get();
        row = tempRef_row4.get();
        RefObject<RowBuffer> tempRef_row5 =
            new RefObject<RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row5, "cast.0");
        row = tempRef_row5.get();
        RefObject<RowBuffer> tempRef_row6 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor =
            new RefObject<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row6, tempRef_tempCursor, c.TypeArgs,
            KeyValuePair.Create("Mark", "Joker")));
        tempCursor = tempRef_tempCursor.get();
        row = tempRef_row6.get();
        RefObject<RowBuffer> tempRef_row7 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row7, ref mapScope, ref tempCursor));
        row = tempRef_row7.get();
        RefObject<RowBuffer> tempRef_row8 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row8, "cast.0");
        row = tempRef_row8.get();
        RefObject<RowBuffer> tempRef_row9 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tempCursor2 =
            new RefObject<RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        OutObject<KeyValuePair<TKey, TValue>> tempOut__ = new OutObject<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempRef_row9, tempRef_tempCursor2, c.TypeArgs, tempOut__));
        _ = tempOut__.get();
        tempCursor = tempRef_tempCursor2.get();
        row = tempRef_row9.get();

        // Write a map of maps, successfully insert an empty map into it, and then try to write directly to the inner
        // map.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("related", out c);
        RefObject<RowBuffer> tempRef_row10 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempRef_row10, c.Path);
        row = tempRef_row10.get();
        RefObject<RowBuffer> tempRef_row11 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempRef_row11, ref mapScope, c.TypeArgs,
            out mapScope));
        row = tempRef_row11.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        LayoutIndexedScope tupleLayout =
            c.<LayoutUniqueScope>TypeAs().FieldType(ref mapScope).<LayoutIndexedScope>TypeAs();
        RefObject<RowBuffer> tempRef_row12 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempRef_row12, "related.0");
        row = tempRef_row12.get();
        RefObject<RowBuffer> tempRef_row13 =
            new RefObject<RowBuffer>(row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(tupleLayout.WriteScope(tempRef_row13, ref tempCursor, c.TypeArgs, out tupleScope));
        row = tempRef_row13.get();
        RefObject<RowBuffer> tempRef_row14 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row14, ref tupleScope,
            "Mark"));
        row = tempRef_row14.get();
        RefObject<RowBuffer> tempRef_row15 =
            new RefObject<RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row15);
        row = tempRef_row15.get();
        TypeArgument valueType = c.TypeArgs[1];
        LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
        RefObject<RowBuffer> tempRef_row16 =
            new RefObject<RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(valueLayout.WriteScope(tempRef_row16, ref tupleScope, valueType.getTypeArgs().clone(),
            out innerScope));
        row = tempRef_row16.get();
        RefObject<RowBuffer> tempRef_row17 =
            new RefObject<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row17, ref mapScope, ref tempCursor));
        row = tempRef_row17.get();
        RefObject<RowBuffer> tempRef_row18 =
            new RefObject<RowBuffer>(row);
        assert mapScope.MoveNext(tempRef_row18);
        row = tempRef_row18.get();
        RefObject<RowBuffer> tempRef_row19 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_mapScope2 =
            new RefObject<RowCursor>(mapScope);
        OutObject<RowCursor> tempOut_tupleScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(tupleLayout.ReadScope(tempRef_row19, tempRef_mapScope2, tempOut_tupleScope));
        tupleScope = tempOut_tupleScope.get();
        mapScope = tempRef_mapScope2.get();
        row = tempRef_row19.get();
        RefObject<RowBuffer> tempRef_row20 =
            new RefObject<RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row20);
        row = tempRef_row20.get();

        // Skip key.
        RefObject<RowBuffer> tempRef_row21 =
            new RefObject<RowBuffer>(row);
        assert tupleScope.MoveNext(tempRef_row21);
        row = tempRef_row21.get();
        RefObject<RowBuffer> tempRef_row22 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_tupleScope =
            new RefObject<RowCursor>(tupleScope);
        OutObject<RowCursor> tempOut_innerScope =
            new OutObject<RowCursor>();
        ResultAssert.IsSuccess(valueLayout.ReadScope(tempRef_row22, tempRef_tupleScope, tempOut_innerScope));
        innerScope = tempOut_innerScope.get();
        tupleScope = tempRef_tupleScope.get();
        row = tempRef_row22.get();
        TypeArgument itemType = valueType.getTypeArgs().get(0).clone();
        RefObject<RowBuffer> tempRef_row23 =
            new RefObject<RowBuffer>(row);
        assert !innerScope.MoveNext(tempRef_row23);
        row = tempRef_row23.get();
        RefObject<RowBuffer> tempRef_row24 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_innerScope =
            new RefObject<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutInt64>TypeAs().WriteSparse(tempRef_row24,
            tempRef_innerScope, 1));
        innerScope = tempRef_innerScope.get();
        row = tempRef_row24.get();
        RefObject<RowBuffer> tempRef_row25 =
            new RefObject<RowBuffer>(row);
        RefObject<RowCursor> tempRef_innerScope2 =
            new RefObject<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutInt64>TypeAs().DeleteSparse(tempRef_row25,
            tempRef_innerScope2));
        innerScope = tempRef_innerScope2.get();
        row = tempRef_row25.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void UpdateInMap()
    public void UpdateInMap() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempRef_row);
        row = tempRef_row.get();

        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Mark", "Harrison", "Carrie"));

        for (java.lang.Iterable<String> permutation : expected.Permute()) {
            Movie t1 = new Movie();
            t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"
            ), Map.entry("Carrie", "Leia")));
            RefObject<RowBuffer> tempRef_row2 =
                new RefObject<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            this.WriteMovie(tempRef_row2, root.Clone(out _), t1);
            row = tempRef_row2.get();

            // Attempt to find each item in turn and then delete it.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("cast", out c);
            RefObject<RowBuffer> tempRef_row3 =
                new RefObject<RowBuffer>(row);
            RowCursor mapScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.Clone(out mapScope).Find(tempRef_row3, c.Path);
            row = tempRef_row3.get();
            RefObject<RowBuffer> tempRef_row4 =
                new RefObject<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempRef_row4, ref mapScope, out mapScope));
            row = tempRef_row4.get();
            for (String key : permutation) {
                // Verify it is already there.
                KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on" +
                    " key");
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
                RefObject<RowCursor> tempRef_tempCursor =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row6, tempRef_tempCursor, c.TypeArgs,
                    pair));
                tempCursor = tempRef_tempCursor.get();
                row = tempRef_row6.get();
                RefObject<RowBuffer> tempRef_row7 =
                    new RefObject<RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row7, ref mapScope, ref tempCursor,
                    out findScope));
                row = tempRef_row7.get();

                // Insert it again with update.
                KeyValuePair<String, String> updatePair = new KeyValuePair<String, String>(key, "update value");
                RefObject<RowBuffer> tempRef_row8 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row8, Utf8String.Empty);
                row = tempRef_row8.get();
                RefObject<RowBuffer> tempRef_row9 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor2 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row9, tempRef_tempCursor2, c.TypeArgs,
                    updatePair));
                tempCursor = tempRef_tempCursor2.get();
                row = tempRef_row9.get();
                RefObject<RowBuffer> tempRef_row10 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row10, ref mapScope,
                    ref tempCursor, UpdateOptions.Update));
                row = tempRef_row10.get();

                // Verify that the value was updated.
                RefObject<RowBuffer> tempRef_row11 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row11, Utf8String.Empty);
                row = tempRef_row11.get();
                RefObject<RowBuffer> tempRef_row12 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor3 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row12, tempRef_tempCursor3, c.TypeArgs
                    , pair));
                tempCursor = tempRef_tempCursor3.get();
                row = tempRef_row12.get();
                RefObject<RowBuffer> tempRef_row13 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row13, ref mapScope, ref tempCursor
                    , out findScope));
                row = tempRef_row13.get();
                RefObject<RowBuffer> tempRef_row14 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_findScope =
                    new RefObject<RowCursor>(findScope);
                KeyValuePair<String, String> foundPair;
                OutObject<KeyValuePair<TKey, TValue>> tempOut_foundPair =
                    new OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempRef_row14, tempRef_findScope, c.TypeArgs,
                    tempOut_foundPair));
                foundPair = tempOut_foundPair.get();
                findScope = tempRef_findScope.get();
                row = tempRef_row14.get();
                assert key == foundPair.Key;
                assert updatePair.Value == foundPair.Value;

                // Insert it again with upsert.
                updatePair = new KeyValuePair<String, String>(key, "upsert value");
                RefObject<RowBuffer> tempRef_row15 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row15, Utf8String.Empty);
                row = tempRef_row15.get();
                RefObject<RowBuffer> tempRef_row16 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor4 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row16, tempRef_tempCursor4, c.TypeArgs
                    , updatePair));
                tempCursor = tempRef_tempCursor4.get();
                row = tempRef_row16.get();

                // ReSharper disable once RedundantArgumentDefaultValue
                RefObject<RowBuffer> tempRef_row17 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row17, ref mapScope,
                    ref tempCursor, UpdateOptions.Upsert));
                row = tempRef_row17.get();

                // Verify that the value was upserted.
                RefObject<RowBuffer> tempRef_row18 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row18, Utf8String.Empty);
                row = tempRef_row18.get();
                RefObject<RowBuffer> tempRef_row19 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor5 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row19, tempRef_tempCursor5, c.TypeArgs
                    , pair));
                tempCursor = tempRef_tempCursor5.get();
                row = tempRef_row19.get();
                RefObject<RowBuffer> tempRef_row20 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempRef_row20, ref mapScope, ref tempCursor
                    , out findScope));
                row = tempRef_row20.get();
                RefObject<RowBuffer> tempRef_row21 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_findScope2 =
                    new RefObject<RowCursor>(findScope);
                OutObject<KeyValuePair<TKey, TValue>> tempOut_foundPair2 =
                    new OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempRef_row21, tempRef_findScope2, c.TypeArgs,
                    tempOut_foundPair2));
                foundPair = tempOut_foundPair2.get();
                findScope = tempRef_findScope2.get();
                row = tempRef_row21.get();
                assert key == foundPair.Key;
                assert updatePair.Value == foundPair.Value;

                // Insert it again with insert (fail: exists).
                updatePair = new KeyValuePair<String, String>(key, "insert value");
                RefObject<RowBuffer> tempRef_row22 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row22, Utf8String.Empty);
                row = tempRef_row22.get();
                RefObject<RowBuffer> tempRef_row23 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor6 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row23, tempRef_tempCursor6, c.TypeArgs
                    , updatePair));
                tempCursor = tempRef_tempCursor6.get();
                row = tempRef_row23.get();
                RefObject<RowBuffer> tempRef_row24 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row24, ref mapScope,
                    ref tempCursor, UpdateOptions.Insert));
                row = tempRef_row24.get();

                // Insert it again with insert at (fail: disallowed).
                updatePair = new KeyValuePair<String, String>(key, "insertAt value");
                RefObject<RowBuffer> tempRef_row25 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempRef_row25, Utf8String.Empty);
                row = tempRef_row25.get();
                RefObject<RowBuffer> tempRef_row26 =
                    new RefObject<RowBuffer>(row);
                RefObject<RowCursor> tempRef_tempCursor7 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempRef_row26, tempRef_tempCursor7, c.TypeArgs
                    , updatePair));
                tempCursor = tempRef_tempCursor7.get();
                row = tempRef_row26.get();
                RefObject<RowBuffer> tempRef_row27 =
                    new RefObject<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.TypeConstraint(c.<LayoutUniqueScope>TypeAs().MoveField(tempRef_row27, ref mapScope,
                    ref tempCursor, UpdateOptions.InsertAt));
                row = tempRef_row27.get();
            }
        }
    }

    private static Earnings ReadEarnings(RefObject<RowBuffer> row, RefObject<RowCursor> udtScope) {
        Layout udt = udtScope.get().getLayout();
        Earnings m = new Earnings();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("domestic", out c);
        OutObject<BigDecimal> tempOut_Domestic = new OutObject<BigDecimal>();
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().ReadFixed(row, udtScope, c, tempOut_Domestic));
        m.Domestic = tempOut_Domestic.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("worldwide", out c);
        OutObject<BigDecimal> tempOut_Worldwide = new OutObject<BigDecimal>();
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().ReadFixed(row, udtScope, c, tempOut_Worldwide));
        m.Worldwide = tempOut_Worldwide.get();
        return m;
    }

    private static <TKey, TValue> Result ReadKeyValue(RefObject<RowBuffer> row,
                                                      RefObject<RowCursor> scope, TypeArgumentList typeArgs,
                                                      OutObject<KeyValuePair<TKey, TValue>> pair) {
        pair.set(null);
        LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
        RowCursor tupleScope;
        OutObject<RowCursor> tempOut_tupleScope =
            new OutObject<RowCursor>();
        Result r = tupleLayout.ReadScope(row, scope, tempOut_tupleScope);
        tupleScope = tempOut_tupleScope.get();
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        RefObject<RowCursor> tempRef_tupleScope =
            new RefObject<RowCursor>(tupleScope);
        TKey key;
        OutObject<TKey> tempOut_key = new OutObject<TKey>();
        r = typeArgs.get(0).getType().<LayoutType<TKey>>TypeAs().ReadSparse(row, tempRef_tupleScope, tempOut_key);
        key = tempOut_key.get();
        tupleScope = tempRef_tupleScope.get();
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        RefObject<RowCursor> tempRef_tupleScope2 =
            new RefObject<RowCursor>(tupleScope);
        TValue value;
        OutObject<TValue> tempOut_value = new OutObject<TValue>();
        r = typeArgs.get(1).getType().<LayoutType<TValue>>TypeAs().ReadSparse(row, tempRef_tupleScope2, tempOut_value);
        value = tempOut_value.get();
        tupleScope = tempRef_tupleScope2.get();
        if (r != Result.Success) {
            return r;
        }

        pair.set(new KeyValuePair<TKey, TValue>(key, value));
        return Result.Success;
    }

    private Movie ReadMovie(RefObject<RowBuffer> row, RefObject<RowCursor> root) {
        Movie value = new Movie();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        RowCursor castScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out castScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref castScope, out castScope) == Result.Success) {
            value.Cast = new HashMap<String, String>();
            while (castScope.MoveNext(row)) {
                RefObject<RowCursor> tempRef_castScope =
                    new RefObject<RowCursor>(castScope);
                KeyValuePair<String, String> item;
                OutObject<KeyValuePair<TKey, TValue>> tempOut_item =
                    new OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempRef_castScope, c.TypeArgs,
                    tempOut_item));
                item = tempOut_item.get();
                castScope = tempRef_castScope.get();
                value.Cast.put(item.Key, item.Value);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("stats", out c);
        RowCursor statsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out statsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref statsScope, out statsScope) == Result.Success) {
            value.Stats = new HashMap<UUID, Double>();
            while (statsScope.MoveNext(row)) {
                RefObject<RowCursor> tempRef_statsScope =
                    new RefObject<RowCursor>(statsScope);
                KeyValuePair<java.util.UUID, Double> item;
                OutObject<KeyValuePair<TKey, TValue>> tempOut_item2 =
                    new OutObject<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempRef_statsScope, c.TypeArgs,
                    tempOut_item2));
                item = tempOut_item2.get();
                statsScope = tempRef_statsScope.get();
                value.Stats.put(item.Key, item.Value);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("related", out c);
        RowCursor relatedScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.get().Clone(out relatedScope).Find(row, c.Path);
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
                RefObject<RowCursor> tempRef_relatedScope =
                    new RefObject<RowCursor>(relatedScope);
                RowCursor tupleScope;
                OutObject<RowCursor> tempOut_tupleScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(tupleLayout.ReadScope(row, tempRef_relatedScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.get();
                relatedScope = tempRef_relatedScope.get();
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
                RefObject<RowCursor> tempRef_tupleScope =
                    new RefObject<RowCursor>(tupleScope);
                RowCursor itemValueScope;
                OutObject<RowCursor> tempOut_itemValueScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(valueLayout.ReadScope(row, tempRef_tupleScope, tempOut_itemValueScope));
                itemValueScope = tempOut_itemValueScope.get();
                tupleScope = tempRef_tupleScope.get();
                HashMap<Long, String> itemValue = new HashMap<Long, String>();
                while (itemValueScope.MoveNext(row)) {
                    RefObject<RowCursor> tempRef_itemValueScope = new RefObject<RowCursor>(itemValueScope);
                    KeyValuePair<Long, String> innerItem;
                    OutObject<KeyValuePair<TKey, TValue>> tempOut_innerItem =
                        new OutObject<KeyValuePair<TKey, TValue>>();
                    ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempRef_itemValueScope,
                        valueType.getTypeArgs().clone(), tempOut_innerItem));
                    innerItem = tempOut_innerItem.get();
                    itemValueScope = tempRef_itemValueScope.get();
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
        root.get().Clone(out revenueScope).Find(row, c.Path);
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
                RefObject<RowCursor> tempRef_revenueScope =
                    new RefObject<RowCursor>(revenueScope);
                RowCursor tupleScope;
                OutObject<RowCursor> tempOut_tupleScope2 =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(tupleLayout.ReadScope(row, tempRef_revenueScope, tempOut_tupleScope2));
                tupleScope = tempOut_tupleScope2.get();
                revenueScope = tempRef_revenueScope.get();
                assert tupleScope.MoveNext(row);
                RefObject<RowCursor> tempRef_tupleScope2 = new RefObject<RowCursor>(tupleScope);
                java.time.LocalDateTime itemKey;
                OutObject<LocalDateTime> tempOut_itemKey = new OutObject<LocalDateTime>();
                ResultAssert.IsSuccess(keyLayout.ReadSparse(row, tempRef_tupleScope2, tempOut_itemKey));
                itemKey = tempOut_itemKey.get();
                tupleScope = tempRef_tupleScope2.get();
                assert tupleScope.MoveNext(row);
                RefObject<RowCursor> tempRef_tupleScope3 = new RefObject<RowCursor>(tupleScope);
                RowCursor itemValueScope;
                OutObject<RowCursor> tempOut_itemValueScope2 = new OutObject<RowCursor>();
                ResultAssert.IsSuccess(valueLayout.ReadScope(row, tempRef_tupleScope3, tempOut_itemValueScope2));
                itemValueScope = tempOut_itemValueScope2.get();
                tupleScope = tempRef_tupleScope3.get();
                RefObject<RowCursor> tempRef_itemValueScope2 = new RefObject<RowCursor>(itemValueScope);
                Earnings itemValue = TypedMapUnitTests.ReadEarnings(row, tempRef_itemValueScope2);
                itemValueScope = tempRef_itemValueScope2.get();

                value.Revenue.put(itemKey, itemValue);
            }
        }

        return value;
    }

    private static void WriteEarnings(RefObject<RowBuffer> row, RefObject<RowCursor> udtScope, TypeArgumentList typeArgs, Earnings m) {
        Layout udt = row.get().getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("domestic", out c);
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().WriteFixed(row, udtScope, c, m.Domestic));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert udt.TryFind("worldwide", out c);
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().WriteFixed(row, udtScope, c, m.Worldwide));
    }

    private static <TKey, TValue> Result WriteKeyValue(RefObject<RowBuffer> row,
                                                       RefObject<RowCursor> scope, TypeArgumentList typeArgs, KeyValuePair<TKey, TValue> pair) {
        LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = tupleLayout.WriteScope(row, scope, typeArgs.clone(), out tupleScope);
        if (r != Result.Success) {
            return r;
        }

        RefObject<RowCursor> tempRef_tupleScope =
            new RefObject<RowCursor>(tupleScope);
        r = typeArgs.get(0).getType().<LayoutType<TKey>>TypeAs().WriteSparse(row, tempRef_tupleScope, pair.Key);
        tupleScope = tempRef_tupleScope.get();
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        RefObject<RowCursor> tempRef_tupleScope2 =
            new RefObject<RowCursor>(tupleScope);
        r = typeArgs.get(1).getType().<LayoutType<TValue>>TypeAs().WriteSparse(row, tempRef_tupleScope2, pair.Value);
        tupleScope = tempRef_tupleScope2.get();
        return r;
    }

    private void WriteMovie(RefObject<RowBuffer> row, RefObject<RowCursor> root, Movie value) {
        LayoutColumn c;

        if (value.Cast != null) {
            OutObject<LayoutColumn> tempOut_c =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("cast", tempOut_c);
            c = tempOut_c.get();
            RowCursor castScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out castScope).Find(row, c.getPath());
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
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RefObject<RowCursor> tempRef_tempCursor =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempRef_tempCursor,
                    c.getTypeArgs().clone(), item));
                tempCursor = tempRef_tempCursor.get();
                RefObject<RowCursor> tempRef_castScope =
                    new RefObject<RowCursor>(castScope);
                RefObject<RowCursor> tempRef_tempCursor2 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_castScope,
                    tempRef_tempCursor2));
                tempCursor = tempRef_tempCursor2.get();
                castScope = tempRef_castScope.get();
            }
        }

        if (value.Stats != null) {
            OutObject<LayoutColumn> tempOut_c2 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("stats", tempOut_c2);
            c = tempOut_c2.get();
            RowCursor statsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out statsScope).Find(row, c.getPath());
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
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                RefObject<RowCursor> tempRef_tempCursor3 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempRef_tempCursor3,
                    c.getTypeArgs().clone(), item));
                tempCursor = tempRef_tempCursor3.get();
                RefObject<RowCursor> tempRef_statsScope =
                    new RefObject<RowCursor>(statsScope);
                RefObject<RowCursor> tempRef_tempCursor4 =
                    new RefObject<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_statsScope,
                    tempRef_tempCursor4));
                tempCursor = tempRef_tempCursor4.get();
                statsScope = tempRef_statsScope.get();
            }
        }

        if (value.Related != null) {
            OutObject<LayoutColumn> tempOut_c3 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("related", tempOut_c3);
            c = tempOut_c3.get();
            RowCursor relatedScoped;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out relatedScoped).Find(row, c.getPath());
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
                root.get().Clone(out tempCursor1).Find(row, "related.0");
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
                    root.get().Clone(out tempCursor2).Find(row, "related.0.0");
                    RefObject<RowCursor> tempRef_tempCursor2
                        = new RefObject<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempRef_tempCursor2,
                        valueType.getTypeArgs().clone(), innerItem));
                    tempCursor2 = tempRef_tempCursor2.get();
                    RefObject<RowCursor> tempRef_innerScope =
                        new RefObject<RowCursor>(innerScope);
                    RefObject<RowCursor> tempRef_tempCursor22 = new RefObject<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(valueLayout.MoveField(row, tempRef_innerScope, tempRef_tempCursor22));
                    tempCursor2 = tempRef_tempCursor22.get();
                    innerScope = tempRef_innerScope.get();
                }

                RefObject<RowCursor> tempRef_relatedScoped =
                    new RefObject<RowCursor>(relatedScoped);
                RefObject<RowCursor> tempRef_tempCursor1 =
                    new RefObject<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_relatedScoped,
                    tempRef_tempCursor1));
                tempCursor1 = tempRef_tempCursor1.get();
                relatedScoped = tempRef_relatedScoped.get();
            }
        }

        if (value.Revenue != null) {
            OutObject<LayoutColumn> tempOut_c4 =
                new OutObject<LayoutColumn>();
            assert this.layout.TryFind("revenue", tempOut_c4);
            c = tempOut_c4.get();
            RowCursor revenueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out revenueScope).Find(row, c.getPath());
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
                root.get().Clone(out tempCursor1).Find(row, "revenue.0");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleLayout.WriteScope(row, ref tempCursor1, c.getTypeArgs().clone(),
                    out tupleScope));
                RefObject<RowCursor> tempRef_tupleScope =
                    new RefObject<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutDateTime>TypeAs().WriteSparse(row,
                    tempRef_tupleScope, item.Key));
                tupleScope = tempRef_tupleScope.get();
                assert tupleScope.MoveNext(row);
                TypeArgument valueType = c.getTypeArgs().get(1).clone();
                LayoutUDT valueLayout = valueType.getType().<LayoutUDT>TypeAs();
                RefObject<RowCursor> tempRef_tupleScope2 =
                    new RefObject<RowCursor>(tupleScope);
                RowCursor itemScope;
                OutObject<RowCursor> tempOut_itemScope =
                    new OutObject<RowCursor>();
                ResultAssert.IsSuccess(valueLayout.WriteScope(row, tempRef_tupleScope2,
                    valueType.getTypeArgs().clone(), tempOut_itemScope));
                itemScope = tempOut_itemScope.get();
                tupleScope = tempRef_tupleScope2.get();
                RefObject<RowCursor> tempRef_itemScope =
                    new RefObject<RowCursor>(itemScope);
                TypedMapUnitTests.WriteEarnings(row, tempRef_itemScope, valueType.getTypeArgs().clone(), item.Value);
                itemScope = tempRef_itemScope.get();

                RefObject<RowCursor> tempRef_revenueScope =
                    new RefObject<RowCursor>(revenueScope);
                RefObject<RowCursor> tempRef_tempCursor12 =
                    new RefObject<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_revenueScope,
                    tempRef_tempCursor12));
                tempCursor1 = tempRef_tempCursor12.get();
                revenueScope = tempRef_revenueScope.get();
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