//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteMovie(tempReference_row, RowCursor.Create(tempReference_row2, out _), t1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Movie t2 = this.ReadMovie(tempReference_row3, RowCursor.Create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindAndDelete()
    public void FindAndDelete() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempReference_row);
        row = tempReference_row.get();

        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Mark", "Harrison", "Carrie"));

        for (java.lang.Iterable<String> permutation : expected.Permute()) {
            Movie t1 = new Movie();
            t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"
            ), Map.entry("Carrie", "Leia")));
            Reference<RowBuffer> tempReference_row2 =
                new Reference<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            this.WriteMovie(tempReference_row2, root.Clone(out _), t1);
            row = tempReference_row2.get();

            // Attempt to find each item in turn and then delete it.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("cast", out c);
            Reference<RowBuffer> tempReference_row3 =
                new Reference<RowBuffer>(row);
            RowCursor mapScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out mapScope).Find(tempReference_row3, c.Path);
            row = tempReference_row3.get();
            Reference<RowBuffer> tempReference_row4 =
                new Reference<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempReference_row4, ref mapScope, out mapScope));
            row = tempReference_row4.get();
            for (String key : permutation) {
                KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on" +
                    " key");
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
                Reference<RowCursor> tempReference_tempCursor =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row6, tempReference_tempCursor, c.TypeArgs,
                    pair));
                tempCursor = tempReference_tempCursor.get();
                row = tempReference_row6.get();
                Reference<RowBuffer> tempReference_row7 =
                    new Reference<RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempReference_row7, ref mapScope, ref tempCursor,
                    out findScope));
                row = tempReference_row7.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref mapScope);
                Reference<RowBuffer> tempReference_row8 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_findScope =
                    new Reference<RowCursor>(findScope);
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().DeleteScope(tempReference_row8,
                    tempReference_findScope));
                findScope = tempReference_findScope.get();
                row = tempReference_row8.get();
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void FindInMap()
    public void FindInMap() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempReference_row);
        row = tempReference_row.get();

        Movie t1 = new Movie();
        t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"),
            Map.entry("Carrie", "Leia")));
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempReference_row2);
        row = tempReference_row2.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc1 =
            new Reference<RowCursor>(rc1);
        this.WriteMovie(tempReference_row3, tempReference_rc1, t1);
        rc1 = tempReference_rc1.get();
        row = tempReference_row3.get();

        // Attempt to find each item in turn.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempReference_row5, ref mapScope, out mapScope));
        row = tempReference_row5.get();
        for (String key : t1.Cast.keySet()) {
            KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on key");
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
            Reference<RowCursor> tempReference_tempCursor =
                new Reference<RowCursor>(tempCursor);
            ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row7, tempReference_tempCursor, c.TypeArgs, pair));
            tempCursor = tempReference_tempCursor.get();
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
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempReference_row8, ref mapScope, ref tempCursor,
                out findScope));
            row = tempReference_row8.get();
            Reference<RowBuffer> tempReference_row9 =
                new Reference<RowBuffer>(row);
            Reference<RowCursor> tempReference_findScope =
                new Reference<RowCursor>(findScope);
            KeyValuePair<String, String> foundPair;
            Out<KeyValuePair<TKey, TValue>> tempOut_foundPair =
                new Out<KeyValuePair<TKey, TValue>>();
            ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempReference_row9, tempReference_findScope, c.TypeArgs,
                tempOut_foundPair));
            foundPair = tempOut_foundPair.get();
            findScope = tempReference_findScope.get();
            row = tempReference_row9.get();
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
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempReference_row);
        row = tempReference_row.get();

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

        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor rc1 = RowCursor.Create(tempReference_row2);
        row = tempReference_row2.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_rc1 =
            new Reference<RowCursor>(rc1);
        this.WriteMovie(tempReference_row3, tempReference_rc1, t1);
        rc1 = tempReference_rc1.get();
        row = tempReference_row3.get();

        // Attempt to insert duplicate items in existing sets.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempReference_row5, ref mapScope, out mapScope));
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
        Reference<RowCursor> tempReference_tempCursor =
            new Reference<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row7, tempReference_tempCursor, c.TypeArgs,
            KeyValuePair.Create("Mark", "Luke")));
        tempCursor = tempReference_tempCursor.get();
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row8, ref mapScope, ref tempCursor,
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
        Reference<RowCursor> tempReference_tempCursor2 =
            new Reference<RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        Out<KeyValuePair<TKey, TValue>> tempOut__ = new Out<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempReference_row10, tempReference_tempCursor2, c.TypeArgs,
            tempOut__));
        _ = tempOut__.get();
        tempCursor = tempReference_tempCursor2.get();
        row = tempReference_row10.get();
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row11, Utf8String.Empty);
        row = tempReference_row11.get();
        Reference<RowBuffer> tempReference_row12 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor3 =
            new Reference<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row12, tempReference_tempCursor3, c.TypeArgs,
            KeyValuePair.Create("Mark", "Joker")));
        tempCursor = tempReference_tempCursor3.get();
        row = tempReference_row12.get();
        Reference<RowBuffer> tempReference_row13 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row13, ref mapScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempReference_row13.get();
        Reference<RowBuffer> tempReference_row14 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row14, Utf8String.Empty);
        row = tempReference_row14.get();
        Reference<RowBuffer> tempReference_row15 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor4 =
            new Reference<RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        Out<KeyValuePair<TKey, TValue>> tempOut__2 =
            new Out<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempReference_row15, tempReference_tempCursor4, c.TypeArgs,
            tempOut__2));
        _ = tempOut__2.get();
        tempCursor = tempReference_tempCursor4.get();
        row = tempReference_row15.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("stats", out c);
        Reference<RowBuffer> tempReference_row16 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempReference_row16, c.Path);
        row = tempReference_row16.get();
        Reference<RowBuffer> tempReference_row17 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempReference_row17, ref mapScope, out mapScope));
        row = tempReference_row17.get();
        KeyValuePair<UUID, Double> pair = KeyValuePair.Create(UUID.fromString("{4674962B-CE11-4916-81C5-0421EE36F168" +
            "}"), 11000000.00);
        Reference<RowBuffer> tempReference_row18 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row18, Utf8String.Empty);
        row = tempReference_row18.get();
        Reference<RowBuffer> tempReference_row19 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor5 =
            new Reference<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row19, tempReference_tempCursor5, c.TypeArgs, pair));
        tempCursor = tempReference_tempCursor5.get();
        row = tempReference_row19.get();
        Reference<RowBuffer> tempReference_row20 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row20, ref mapScope, ref tempCursor,
            UpdateOptions.Insert));
        row = tempReference_row20.get();
        Reference<RowBuffer> tempReference_row21 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row21, Utf8String.Empty);
        row = tempReference_row21.get();
        Reference<RowBuffer> tempReference_row22 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor6 =
            new Reference<RowCursor>(tempCursor);
        Out<KeyValuePair<TKey, TValue>> tempOut_pair = new Out<KeyValuePair<TKey,
                            TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempReference_row22, tempReference_tempCursor6, c.TypeArgs,
            tempOut_pair));
        pair = tempOut_pair.get();
        tempCursor = tempReference_tempCursor6.get();
        row = tempReference_row22.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventUpdatesInNonUpdatableScope()
    public void PreventUpdatesInNonUpdatableScope() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempReference_row);
        row = tempReference_row.get();

        // Write a map and then try to write directly into it.
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor mapScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempReference_row2, c.Path);
        row = tempReference_row2.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempReference_row3, ref mapScope, c.TypeArgs,
            out mapScope));
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_mapScope =
            new Reference<RowCursor>(mapScope);
        ResultAssert.InsufficientPermissions(TypedMapUnitTests.WriteKeyValue(tempReference_row4, tempReference_mapScope,
            c.TypeArgs, KeyValuePair.Create("Mark", "Joker")));
        mapScope = tempReference_mapScope.get();
        row = tempReference_row4.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        RowCursor tempCursor;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row5, "cast.0");
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor =
            new Reference<RowCursor>(tempCursor);
        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row6, tempReference_tempCursor, c.TypeArgs,
            KeyValuePair.Create("Mark", "Joker")));
        tempCursor = tempReference_tempCursor.get();
        row = tempReference_row6.get();
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row7, ref mapScope, ref tempCursor));
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row8, "cast.0");
        row = tempReference_row8.get();
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tempCursor2 =
            new Reference<RowCursor>(tempCursor);
        KeyValuePair<String, String> _;
        Out<KeyValuePair<TKey, TValue>> tempOut__ = new Out<KeyValuePair<TKey, TValue>>();
        ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(tempReference_row9, tempReference_tempCursor2, c.TypeArgs, tempOut__));
        _ = tempOut__.get();
        tempCursor = tempReference_tempCursor2.get();
        row = tempReference_row9.get();

        // Write a map of maps, successfully insert an empty map into it, and then try to write directly to the inner
        // map.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("related", out c);
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out mapScope).Find(tempReference_row10, c.Path);
        row = tempReference_row10.get();
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(tempReference_row11, ref mapScope, c.TypeArgs,
            out mapScope));
        row = tempReference_row11.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        LayoutIndexedScope tupleLayout =
            c.<LayoutUniqueScope>TypeAs().FieldType(ref mapScope).<LayoutIndexedScope>TypeAs();
        Reference<RowBuffer> tempReference_row12 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.Clone(out tempCursor).Find(tempReference_row12, "related.0");
        row = tempReference_row12.get();
        Reference<RowBuffer> tempReference_row13 =
            new Reference<RowBuffer>(row);
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(tupleLayout.WriteScope(tempReference_row13, ref tempCursor, c.TypeArgs, out tupleScope));
        row = tempReference_row13.get();
        Reference<RowBuffer> tempReference_row14 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row14, ref tupleScope,
            "Mark"));
        row = tempReference_row14.get();
        Reference<RowBuffer> tempReference_row15 =
            new Reference<RowBuffer>(row);
        assert tupleScope.MoveNext(tempReference_row15);
        row = tempReference_row15.get();
        TypeArgument valueType = c.TypeArgs[1];
        LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
        Reference<RowBuffer> tempReference_row16 =
            new Reference<RowBuffer>(row);
        RowCursor innerScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(valueLayout.WriteScope(tempReference_row16, ref tupleScope, valueType.getTypeArgs().clone(),
            out innerScope));
        row = tempReference_row16.get();
        Reference<RowBuffer> tempReference_row17 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row17, ref mapScope, ref tempCursor));
        row = tempReference_row17.get();
        Reference<RowBuffer> tempReference_row18 =
            new Reference<RowBuffer>(row);
        assert mapScope.MoveNext(tempReference_row18);
        row = tempReference_row18.get();
        Reference<RowBuffer> tempReference_row19 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_mapScope2 =
            new Reference<RowCursor>(mapScope);
        Out<RowCursor> tempOut_tupleScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(tupleLayout.ReadScope(tempReference_row19, tempReference_mapScope2, tempOut_tupleScope));
        tupleScope = tempOut_tupleScope.get();
        mapScope = tempReference_mapScope2.get();
        row = tempReference_row19.get();
        Reference<RowBuffer> tempReference_row20 =
            new Reference<RowBuffer>(row);
        assert tupleScope.MoveNext(tempReference_row20);
        row = tempReference_row20.get();

        // Skip key.
        Reference<RowBuffer> tempReference_row21 =
            new Reference<RowBuffer>(row);
        assert tupleScope.MoveNext(tempReference_row21);
        row = tempReference_row21.get();
        Reference<RowBuffer> tempReference_row22 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_tupleScope =
            new Reference<RowCursor>(tupleScope);
        Out<RowCursor> tempOut_innerScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(valueLayout.ReadScope(tempReference_row22, tempReference_tupleScope, tempOut_innerScope));
        innerScope = tempOut_innerScope.get();
        tupleScope = tempReference_tupleScope.get();
        row = tempReference_row22.get();
        TypeArgument itemType = valueType.getTypeArgs().get(0).clone();
        Reference<RowBuffer> tempReference_row23 =
            new Reference<RowBuffer>(row);
        assert !innerScope.MoveNext(tempReference_row23);
        row = tempReference_row23.get();
        Reference<RowBuffer> tempReference_row24 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_innerScope =
            new Reference<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutInt64>TypeAs().WriteSparse(tempReference_row24,
            tempReference_innerScope, 1));
        innerScope = tempReference_innerScope.get();
        row = tempReference_row24.get();
        Reference<RowBuffer> tempReference_row25 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_innerScope2 =
            new Reference<RowCursor>(innerScope);
        ResultAssert.InsufficientPermissions(itemType.getType().<LayoutInt64>TypeAs().DeleteSparse(tempReference_row25,
            tempReference_innerScope2));
        innerScope = tempReference_innerScope2.get();
        row = tempReference_row25.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void UpdateInMap()
    public void UpdateInMap() {
        RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowCursor root = RowCursor.Create(tempReference_row);
        row = tempReference_row.get();

        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Mark", "Harrison", "Carrie"));

        for (java.lang.Iterable<String> permutation : expected.Permute()) {
            Movie t1 = new Movie();
            t1.Cast = new HashMap<String, String>(Map.ofEntries(Map.entry("Mark", "Luke"), Map.entry("Harrison", "Han"
            ), Map.entry("Carrie", "Leia")));
            Reference<RowBuffer> tempReference_row2 =
                new Reference<RowBuffer>(row);
            RowCursor _;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            this.WriteMovie(tempReference_row2, root.Clone(out _), t1);
            row = tempReference_row2.get();

            // Attempt to find each item in turn and then delete it.
            LayoutColumn c;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.layout.TryFind("cast", out c);
            Reference<RowBuffer> tempReference_row3 =
                new Reference<RowBuffer>(row);
            RowCursor mapScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.Clone(out mapScope).Find(tempReference_row3, c.Path);
            row = tempReference_row3.get();
            Reference<RowBuffer> tempReference_row4 =
                new Reference<RowBuffer>(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().ReadScope(tempReference_row4, ref mapScope, out mapScope));
            row = tempReference_row4.get();
            for (String key : permutation) {
                // Verify it is already there.
                KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, "map lookup matches only on" +
                    " key");
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
                Reference<RowCursor> tempReference_tempCursor =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row6, tempReference_tempCursor, c.TypeArgs,
                    pair));
                tempCursor = tempReference_tempCursor.get();
                row = tempReference_row6.get();
                Reference<RowBuffer> tempReference_row7 =
                    new Reference<RowBuffer>(row);
                RowCursor findScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempReference_row7, ref mapScope, ref tempCursor,
                    out findScope));
                row = tempReference_row7.get();

                // Insert it again with update.
                KeyValuePair<String, String> updatePair = new KeyValuePair<String, String>(key, "update value");
                Reference<RowBuffer> tempReference_row8 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row8, Utf8String.Empty);
                row = tempReference_row8.get();
                Reference<RowBuffer> tempReference_row9 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor2 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row9, tempReference_tempCursor2, c.TypeArgs,
                    updatePair));
                tempCursor = tempReference_tempCursor2.get();
                row = tempReference_row9.get();
                Reference<RowBuffer> tempReference_row10 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row10, ref mapScope,
                    ref tempCursor, UpdateOptions.Update));
                row = tempReference_row10.get();

                // Verify that the value was updated.
                Reference<RowBuffer> tempReference_row11 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row11, Utf8String.Empty);
                row = tempReference_row11.get();
                Reference<RowBuffer> tempReference_row12 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor3 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row12, tempReference_tempCursor3, c.TypeArgs
                    , pair));
                tempCursor = tempReference_tempCursor3.get();
                row = tempReference_row12.get();
                Reference<RowBuffer> tempReference_row13 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempReference_row13, ref mapScope, ref tempCursor
                    , out findScope));
                row = tempReference_row13.get();
                Reference<RowBuffer> tempReference_row14 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_findScope =
                    new Reference<RowCursor>(findScope);
                KeyValuePair<String, String> foundPair;
                Out<KeyValuePair<TKey, TValue>> tempOut_foundPair =
                    new Out<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempReference_row14, tempReference_findScope, c.TypeArgs,
                    tempOut_foundPair));
                foundPair = tempOut_foundPair.get();
                findScope = tempReference_findScope.get();
                row = tempReference_row14.get();
                assert key == foundPair.Key;
                assert updatePair.Value == foundPair.Value;

                // Insert it again with upsert.
                updatePair = new KeyValuePair<String, String>(key, "upsert value");
                Reference<RowBuffer> tempReference_row15 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row15, Utf8String.Empty);
                row = tempReference_row15.get();
                Reference<RowBuffer> tempReference_row16 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor4 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row16, tempReference_tempCursor4, c.TypeArgs
                    , updatePair));
                tempCursor = tempReference_tempCursor4.get();
                row = tempReference_row16.get();

                // ReSharper disable once RedundantArgumentDefaultValue
                Reference<RowBuffer> tempReference_row17 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row17, ref mapScope,
                    ref tempCursor, UpdateOptions.Upsert));
                row = tempReference_row17.get();

                // Verify that the value was upserted.
                Reference<RowBuffer> tempReference_row18 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row18, Utf8String.Empty);
                row = tempReference_row18.get();
                Reference<RowBuffer> tempReference_row19 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor5 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row19, tempReference_tempCursor5, c.TypeArgs
                    , pair));
                tempCursor = tempReference_tempCursor5.get();
                row = tempReference_row19.get();
                Reference<RowBuffer> tempReference_row20 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().Find(tempReference_row20, ref mapScope, ref tempCursor
                    , out findScope));
                row = tempReference_row20.get();
                Reference<RowBuffer> tempReference_row21 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_findScope2 =
                    new Reference<RowCursor>(findScope);
                Out<KeyValuePair<TKey, TValue>> tempOut_foundPair2 =
                    new Out<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(tempReference_row21, tempReference_findScope2, c.TypeArgs,
                    tempOut_foundPair2));
                foundPair = tempOut_foundPair2.get();
                findScope = tempReference_findScope2.get();
                row = tempReference_row21.get();
                assert key == foundPair.Key;
                assert updatePair.Value == foundPair.Value;

                // Insert it again with insert (fail: exists).
                updatePair = new KeyValuePair<String, String>(key, "insert value");
                Reference<RowBuffer> tempReference_row22 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row22, Utf8String.Empty);
                row = tempReference_row22.get();
                Reference<RowBuffer> tempReference_row23 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor6 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row23, tempReference_tempCursor6, c.TypeArgs
                    , updatePair));
                tempCursor = tempReference_tempCursor6.get();
                row = tempReference_row23.get();
                Reference<RowBuffer> tempReference_row24 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.Exists(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row24, ref mapScope,
                    ref tempCursor, UpdateOptions.Insert));
                row = tempReference_row24.get();

                // Insert it again with insert at (fail: disallowed).
                updatePair = new KeyValuePair<String, String>(key, "insertAt value");
                Reference<RowBuffer> tempReference_row25 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.Clone(out tempCursor).Find(tempReference_row25, Utf8String.Empty);
                row = tempReference_row25.get();
                Reference<RowBuffer> tempReference_row26 =
                    new Reference<RowBuffer>(row);
                Reference<RowCursor> tempReference_tempCursor7 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(tempReference_row26, tempReference_tempCursor7, c.TypeArgs
                    , updatePair));
                tempCursor = tempReference_tempCursor7.get();
                row = tempReference_row26.get();
                Reference<RowBuffer> tempReference_row27 =
                    new Reference<RowBuffer>(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.TypeConstraint(c.<LayoutUniqueScope>TypeAs().MoveField(tempReference_row27, ref mapScope,
                    ref tempCursor, UpdateOptions.InsertAt));
                row = tempReference_row27.get();
            }
        }
    }

    private static Earnings ReadEarnings(Reference<RowBuffer> row, Reference<RowCursor> udtScope) {
        Layout udt = udtScope.get().getLayout();
        Earnings m = new Earnings();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert udt.TryFind("domestic", out c);
        Out<BigDecimal> tempOut_Domestic = new Out<BigDecimal>();
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().ReadFixed(row, udtScope, c, tempOut_Domestic));
        m.Domestic = tempOut_Domestic.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert udt.TryFind("worldwide", out c);
        Out<BigDecimal> tempOut_Worldwide = new Out<BigDecimal>();
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().ReadFixed(row, udtScope, c, tempOut_Worldwide));
        m.Worldwide = tempOut_Worldwide.get();
        return m;
    }

    private static <TKey, TValue> Result ReadKeyValue(Reference<RowBuffer> row,
                                                      Reference<RowCursor> scope, TypeArgumentList typeArgs,
                                                      Out<KeyValuePair<TKey, TValue>> pair) {
        pair.setAndGet(null);
        LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
        RowCursor tupleScope;
        Out<RowCursor> tempOut_tupleScope =
            new Out<RowCursor>();
        Result r = tupleLayout.ReadScope(row, scope, tempOut_tupleScope);
        tupleScope = tempOut_tupleScope.get();
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        Reference<RowCursor> tempReference_tupleScope =
            new Reference<RowCursor>(tupleScope);
        TKey key;
        Out<TKey> tempOut_key = new Out<TKey>();
        r = typeArgs.get(0).getType().<LayoutType<TKey>>TypeAs().ReadSparse(row, tempReference_tupleScope, tempOut_key);
        key = tempOut_key.get();
        tupleScope = tempReference_tupleScope.get();
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        Reference<RowCursor> tempReference_tupleScope2 =
            new Reference<RowCursor>(tupleScope);
        TValue value;
        Out<TValue> tempOut_value = new Out<TValue>();
        r = typeArgs.get(1).getType().<LayoutType<TValue>>TypeAs().ReadSparse(row, tempReference_tupleScope2, tempOut_value);
        value = tempOut_value.get();
        tupleScope = tempReference_tupleScope2.get();
        if (r != Result.Success) {
            return r;
        }

        pair.setAndGet(new KeyValuePair<TKey, TValue>(key, value));
        return Result.Success;
    }

    private Movie ReadMovie(Reference<RowBuffer> row, Reference<RowCursor> root) {
        Movie value = new Movie();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("cast", out c);
        RowCursor castScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out castScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref castScope, out castScope) == Result.Success) {
            value.Cast = new HashMap<String, String>();
            while (castScope.MoveNext(row)) {
                Reference<RowCursor> tempReference_castScope =
                    new Reference<RowCursor>(castScope);
                KeyValuePair<String, String> item;
                Out<KeyValuePair<TKey, TValue>> tempOut_item =
                    new Out<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempReference_castScope, c.TypeArgs,
                    tempOut_item));
                item = tempOut_item.get();
                castScope = tempReference_castScope.get();
                value.Cast.put(item.Key, item.Value);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("stats", out c);
        RowCursor statsScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out statsScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref statsScope, out statsScope) == Result.Success) {
            value.Stats = new HashMap<UUID, Double>();
            while (statsScope.MoveNext(row)) {
                Reference<RowCursor> tempReference_statsScope =
                    new Reference<RowCursor>(statsScope);
                KeyValuePair<java.util.UUID, Double> item;
                Out<KeyValuePair<TKey, TValue>> tempOut_item2 =
                    new Out<KeyValuePair<TKey, TValue>>();
                ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempReference_statsScope, c.TypeArgs,
                    tempOut_item2));
                item = tempOut_item2.get();
                statsScope = tempReference_statsScope.get();
                value.Stats.put(item.Key, item.Value);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("related", out c);
        RowCursor relatedScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out relatedScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref relatedScope, out relatedScope) == Result.Success) {
            value.Related = new HashMap<String, HashMap<Long, String>>();
            TypeArgument keyType = c.TypeArgs[0];
            TypeArgument valueType = c.TypeArgs[1];
            LayoutUtf8 keyLayout = keyType.getType().<LayoutUtf8>TypeAs();
            LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
            while (relatedScope.MoveNext(row)) {
                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                Reference<RowCursor> tempReference_relatedScope =
                    new Reference<RowCursor>(relatedScope);
                RowCursor tupleScope;
                Out<RowCursor> tempOut_tupleScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(tupleLayout.ReadScope(row, tempReference_relatedScope, tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.get();
                relatedScope = tempReference_relatedScope.get();
                assert tupleScope.MoveNext(row);
                String itemKey;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(keyLayout.ReadSparse(row, ref tupleScope, out itemKey));
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope =
                    new Reference<RowCursor>(tupleScope);
                RowCursor itemValueScope;
                Out<RowCursor> tempOut_itemValueScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(valueLayout.ReadScope(row, tempReference_tupleScope, tempOut_itemValueScope));
                itemValueScope = tempOut_itemValueScope.get();
                tupleScope = tempReference_tupleScope.get();
                HashMap<Long, String> itemValue = new HashMap<Long, String>();
                while (itemValueScope.MoveNext(row)) {
                    Reference<RowCursor> tempReference_itemValueScope = new Reference<RowCursor>(itemValueScope);
                    KeyValuePair<Long, String> innerItem;
                    Out<KeyValuePair<TKey, TValue>> tempOut_innerItem =
                        new Out<KeyValuePair<TKey, TValue>>();
                    ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(row, tempReference_itemValueScope,
                        valueType.getTypeArgs().clone(), tempOut_innerItem));
                    innerItem = tempOut_innerItem.get();
                    itemValueScope = tempReference_itemValueScope.get();
                    itemValue.put(innerItem.Key, innerItem.Value);
                }

                value.Related.put(itemKey, itemValue);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("revenue", out c);
        RowCursor revenueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out revenueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref revenueScope, out revenueScope) == Result.Success) {
            value.Revenue = new HashMap<LocalDateTime, Earnings>();
            TypeArgument keyType = c.TypeArgs[0];
            TypeArgument valueType = c.TypeArgs[1];
            LayoutDateTime keyLayout = keyType.getType().<LayoutDateTime>TypeAs();
            LayoutUDT valueLayout = valueType.getType().<LayoutUDT>TypeAs();
            while (revenueScope.MoveNext(row)) {
                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                Reference<RowCursor> tempReference_revenueScope =
                    new Reference<RowCursor>(revenueScope);
                RowCursor tupleScope;
                Out<RowCursor> tempOut_tupleScope2 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(tupleLayout.ReadScope(row, tempReference_revenueScope, tempOut_tupleScope2));
                tupleScope = tempOut_tupleScope2.get();
                revenueScope = tempReference_revenueScope.get();
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope2 = new Reference<RowCursor>(tupleScope);
                java.time.LocalDateTime itemKey;
                Out<LocalDateTime> tempOut_itemKey = new Out<LocalDateTime>();
                ResultAssert.IsSuccess(keyLayout.ReadSparse(row, tempReference_tupleScope2, tempOut_itemKey));
                itemKey = tempOut_itemKey.get();
                tupleScope = tempReference_tupleScope2.get();
                assert tupleScope.MoveNext(row);
                Reference<RowCursor> tempReference_tupleScope3 = new Reference<RowCursor>(tupleScope);
                RowCursor itemValueScope;
                Out<RowCursor> tempOut_itemValueScope2 = new Out<RowCursor>();
                ResultAssert.IsSuccess(valueLayout.ReadScope(row, tempReference_tupleScope3, tempOut_itemValueScope2));
                itemValueScope = tempOut_itemValueScope2.get();
                tupleScope = tempReference_tupleScope3.get();
                Reference<RowCursor> tempReference_itemValueScope2 = new Reference<RowCursor>(itemValueScope);
                Earnings itemValue = TypedMapUnitTests.ReadEarnings(row, tempReference_itemValueScope2);
                itemValueScope = tempReference_itemValueScope2.get();

                value.Revenue.put(itemKey, itemValue);
            }
        }

        return value;
    }

    private static void WriteEarnings(Reference<RowBuffer> row, Reference<RowCursor> udtScope, TypeArgumentList typeArgs, Earnings m) {
        Layout udt = row.get().getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert udt.TryFind("domestic", out c);
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().WriteFixed(row, udtScope, c, m.Domestic));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert udt.TryFind("worldwide", out c);
        ResultAssert.IsSuccess(c.<LayoutDecimal>TypeAs().WriteFixed(row, udtScope, c, m.Worldwide));
    }

    private static <TKey, TValue> Result WriteKeyValue(Reference<RowBuffer> row,
                                                       Reference<RowCursor> scope, TypeArgumentList typeArgs, KeyValuePair<TKey, TValue> pair) {
        LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
        RowCursor tupleScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = tupleLayout.WriteScope(row, scope, typeArgs.clone(), out tupleScope);
        if (r != Result.Success) {
            return r;
        }

        Reference<RowCursor> tempReference_tupleScope =
            new Reference<RowCursor>(tupleScope);
        r = typeArgs.get(0).getType().<LayoutType<TKey>>TypeAs().WriteSparse(row, tempReference_tupleScope, pair.Key);
        tupleScope = tempReference_tupleScope.get();
        if (r != Result.Success) {
            return r;
        }

        tupleScope.MoveNext(row);
        Reference<RowCursor> tempReference_tupleScope2 =
            new Reference<RowCursor>(tupleScope);
        r = typeArgs.get(1).getType().<LayoutType<TValue>>TypeAs().WriteSparse(row, tempReference_tupleScope2, pair.Value);
        tupleScope = tempReference_tupleScope2.get();
        return r;
    }

    private void WriteMovie(Reference<RowBuffer> row, Reference<RowCursor> root, Movie value) {
        LayoutColumn c;

        if (value.Cast != null) {
            Out<LayoutColumn> tempOut_c =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("cast", tempOut_c);
            c = tempOut_c.get();
            RowCursor castScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out castScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref castScope,
                c.getTypeArgs().clone(), out castScope));
            for (KeyValuePair<String, String> item : value.Cast) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                Reference<RowCursor> tempReference_tempCursor =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempReference_tempCursor,
                    c.getTypeArgs().clone(), item));
                tempCursor = tempReference_tempCursor.get();
                Reference<RowCursor> tempReference_castScope =
                    new Reference<RowCursor>(castScope);
                Reference<RowCursor> tempReference_tempCursor2 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempReference_castScope,
                    tempReference_tempCursor2));
                tempCursor = tempReference_tempCursor2.get();
                castScope = tempReference_castScope.get();
            }
        }

        if (value.Stats != null) {
            Out<LayoutColumn> tempOut_c2 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("stats", tempOut_c2);
            c = tempOut_c2.get();
            RowCursor statsScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out statsScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref statsScope,
                c.getTypeArgs().clone(), out statsScope));
            for (KeyValuePair<UUID, Double> item : value.Stats) {
                RowCursor tempCursor;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
                Reference<RowCursor> tempReference_tempCursor3 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempReference_tempCursor3,
                    c.getTypeArgs().clone(), item));
                tempCursor = tempReference_tempCursor3.get();
                Reference<RowCursor> tempReference_statsScope =
                    new Reference<RowCursor>(statsScope);
                Reference<RowCursor> tempReference_tempCursor4 =
                    new Reference<RowCursor>(tempCursor);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempReference_statsScope,
                    tempReference_tempCursor4));
                tempCursor = tempReference_tempCursor4.get();
                statsScope = tempReference_statsScope.get();
            }
        }

        if (value.Related != null) {
            Out<LayoutColumn> tempOut_c3 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("related", tempOut_c3);
            c = tempOut_c3.get();
            RowCursor relatedScoped;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out relatedScoped).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref relatedScoped,
                c.getTypeArgs().clone(), out relatedScoped));
            for (KeyValuePair<String, HashMap<Long, String>> item : value.Related) {
                assert item.Value != null;

                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor1).Find(row, "related.0");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleLayout.WriteScope(row, ref tempCursor1, c.getTypeArgs().clone(),
                    out tupleScope));
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutUtf8>TypeAs().WriteSparse(row,
                    ref tupleScope, item.Key));
                assert tupleScope.MoveNext(row);
                TypeArgument valueType = c.getTypeArgs().get(1).clone();
                LayoutUniqueScope valueLayout = valueType.getType().<LayoutUniqueScope>TypeAs();
                RowCursor innerScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(valueLayout.WriteScope(row, ref tupleScope, valueType.getTypeArgs().clone(),
                    out innerScope));
                for (KeyValuePair<Long, String> innerItem : item.Value) {
                    RowCursor tempCursor2;
                    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'Out' helper class unless the method is within the
                    // code being modified:
                    root.get().Clone(out tempCursor2).Find(row, "related.0.0");
                    Reference<RowCursor> tempReference_tempCursor2
                        = new Reference<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(row, tempReference_tempCursor2,
                        valueType.getTypeArgs().clone(), innerItem));
                    tempCursor2 = tempReference_tempCursor2.get();
                    Reference<RowCursor> tempReference_innerScope =
                        new Reference<RowCursor>(innerScope);
                    Reference<RowCursor> tempReference_tempCursor22 = new Reference<RowCursor>(tempCursor2);
                    ResultAssert.IsSuccess(valueLayout.MoveField(row, tempReference_innerScope,
                        tempReference_tempCursor22));
                    tempCursor2 = tempReference_tempCursor22.get();
                    innerScope = tempReference_innerScope.get();
                }

                Reference<RowCursor> tempReference_relatedScoped =
                    new Reference<RowCursor>(relatedScoped);
                Reference<RowCursor> tempReference_tempCursor1 =
                    new Reference<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempReference_relatedScoped,
                    tempReference_tempCursor1));
                tempCursor1 = tempReference_tempCursor1.get();
                relatedScoped = tempReference_relatedScoped.get();
            }
        }

        if (value.Revenue != null) {
            Out<LayoutColumn> tempOut_c4 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("revenue", tempOut_c4);
            c = tempOut_c4.get();
            RowCursor revenueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out revenueScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref revenueScope,
                c.getTypeArgs().clone(), out revenueScope));
            for (KeyValuePair<LocalDateTime, Earnings> item : value.Revenue) {
                assert item.Value != null;

                LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                RowCursor tempCursor1;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                root.get().Clone(out tempCursor1).Find(row, "revenue.0");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleLayout.WriteScope(row, ref tempCursor1, c.getTypeArgs().clone(),
                    out tupleScope));
                Reference<RowCursor> tempReference_tupleScope =
                    new Reference<RowCursor>(tupleScope);
                ResultAssert.IsSuccess(c.getTypeArgs().get(0).getType().<LayoutDateTime>TypeAs().WriteSparse(row,
                    tempReference_tupleScope, item.Key));
                tupleScope = tempReference_tupleScope.get();
                assert tupleScope.MoveNext(row);
                TypeArgument valueType = c.getTypeArgs().get(1).clone();
                LayoutUDT valueLayout = valueType.getType().<LayoutUDT>TypeAs();
                Reference<RowCursor> tempReference_tupleScope2 =
                    new Reference<RowCursor>(tupleScope);
                RowCursor itemScope;
                Out<RowCursor> tempOut_itemScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(valueLayout.WriteScope(row, tempReference_tupleScope2,
                    valueType.getTypeArgs().clone(), tempOut_itemScope));
                itemScope = tempOut_itemScope.get();
                tupleScope = tempReference_tupleScope2.get();
                Reference<RowCursor> tempReference_itemScope =
                    new Reference<RowCursor>(itemScope);
                TypedMapUnitTests.WriteEarnings(row, tempReference_itemScope, valueType.getTypeArgs().clone(), item.Value);
                itemScope = tempReference_itemScope.get();

                Reference<RowCursor> tempReference_revenueScope =
                    new Reference<RowCursor>(revenueScope);
                Reference<RowCursor> tempReference_tempCursor12 =
                    new Reference<RowCursor>(tempCursor1);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempReference_revenueScope,
                    tempReference_tempCursor12));
                tempCursor1 = tempReference_tempCursor12.get();
                revenueScope = tempReference_revenueScope.get();
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