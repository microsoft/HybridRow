// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringToken;

import java.nio.file.Files;
import java.time.LocalDateTime;

// ReSharper disable once StringLiteralTypo
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here
// are anonymous.")][DeploymentItem("TestData\\PerfCounterSchema.json", "TestData")] public sealed class TupleUnitTests
public final class TupleUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;

    private final PerfCounter counterExample = new PerfCounter() {
        Name ="RowInserts",Value =Tuple.Create("units",12046L)
    };

    private Namespace counterSchema;
    private Layout countersLayout;
    private LayoutResolver countersResolver;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateCoordCounter()
    public void CreateCoordCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "CoordInserts";
        Coord tempVar = new Coord();
        tempVar.Lat = 12L;
        tempVar.Lng = 40L;
        c1.Coord = Tuple.Create("units", tempVar);

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        PerfCounter c2 = this.ReadCounter(tempReference_row3, RowCursor.Create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateCounter()
    public void CreateCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = this.counterExample;
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        PerfCounter c2 = this.ReadCounter(tempReference_row3, RowCursor.Create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateMinMeanMaxCounter()
    public void CreateMinMeanMaxCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "RowInserts";
        c1.MinMaxValue = Tuple.Create("units", Tuple.Create(12L, 542L, 12046L));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        PerfCounter c2 = this.ReadCounter(tempReference_row3, RowCursor.Create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void DownwardDelegateWriteScope()
    public void DownwardDelegateWriteScope() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        Layout layout = this.countersResolver.Resolve(tangible.ListHelper.find(this.counterSchema.getSchemas(),
            x -> x.Name.equals("CounterSet")).SchemaId);
        row.initLayout(HybridRowVersion.V1, layout, this.countersResolver);

        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert layout.TryFind("history", out col);
        StringToken historyToken;
        Out<StringToken> tempOut_historyToken =
            new Out<StringToken>();
        assert layout.getTokenizer().TryFindToken(col.Path, tempOut_historyToken);
        historyToken = tempOut_historyToken.get();
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor history;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.Create(tempReference_row, out history).Find(tempReference_row2, historyToken);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        int ctx = 1; // ignored
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_history =
            new Reference<RowCursor>(history);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        ResultAssert.IsSuccess(LayoutType.TypedArray.WriteScope(tempReference_row3, tempReference_history, col.TypeArgs, ctx,
            (ref RowBuffer row2, ref RowCursor arrCur, int ctx2) ->
        {
            for (int i = 0; i < 5; i++) {
                Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer> tempReference_row2 =
                    new Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer>(row2);
                Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor> tempReference_arrCur =
                    new Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor>(arrCur);
                ResultAssert.IsSuccess(LayoutType.UDT.WriteScope(tempReference_row2, tempReference_arrCur,
                    arrCur.ScopeTypeArgs[0].TypeArgs, i, (ref RowBuffer row3, ref RowCursor udtCur, int ctx3) ->
                {
                    LayoutColumn col3;
                    assert udtCur.Layout.TryFind("minmeanmax", out col3);
                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer> tempReference_row3 =
                        new Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer>(row3);
                    Reference<ref RowBuffer>tempRef_row32 = new Reference<ref RowBuffer > (row3);
                    ResultAssert.IsSuccess(LayoutType.TypedTuple.WriteScope(tempReference_row3, udtCur.Find(tempRef_row32,
                        col3.Path), col3.TypeArgs, ctx3, (ref RowBuffer row4, ref RowCursor tupCur, int ctx4) ->
                    {
                        if (ctx4 > 0) {
                            Reference<ref RowBuffer>tempRef_row4 = new Reference<ref
                            RowBuffer > (row4);
                            Reference<ref RowCursor>tempRef_tupCur = new Reference<ref
                            RowCursor > (tupCur);
                            ResultAssert.IsSuccess(LayoutType.Utf8.WriteSparse(tempRef_row4, tempRef_tupCur, "abc"));
                            tupCur = tempRef_tupCur.argValue;
                            row4 = tempRef_row4.argValue;
                        }

                        if (ctx4 > 1) {
                            Reference<ref RowBuffer>tempRef_row42 = new Reference<ref
                            RowBuffer > (row4);
                            assert tupCur.MoveNext(tempRef_row42);
                            row4 = tempRef_row42.argValue;
                            Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer> tempReference_row43 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer>(row4);
                            Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor> tempReference_tupCur2 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur);
                            ResultAssert.IsSuccess(LayoutType.TypedTuple.WriteScope(tempReference_row43,
                                tempReference_tupCur2,
                                tupCur.ScopeTypeArgs[1].TypeArgs, ctx4, (ref RowBuffer row5, ref RowCursor tupCur2,
                                                                         int ctx5) ->
                            {
                                if (ctx5 > 1) {
                                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer> tempReference_row5 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer>(row5);
                                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor> tempReference_tupCur2 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur2);
                                    ResultAssert.IsSuccess(LayoutType.Int64.WriteSparse(tempReference_row5, tempReference_tupCur2
                                        , ctx5));
                                    tupCur2 = tempReference_tupCur2.get();
                                    row5 = tempReference_row5.get();
                                }

                                if (ctx5 > 2) {
                                    Reference<ref RowBuffer>tempRef_row52 = new Reference<ref
                                    RowBuffer > (row5);
                                    assert tupCur2.MoveNext(tempRef_row52);
                                    row5 = tempRef_row52.argValue;
                                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer> tempReference_row53 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer>(row5);
                                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor> tempReference_tupCur22 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur2);
                                    ResultAssert.IsSuccess(LayoutType.Int64.WriteSparse(tempReference_row53,
                                        tempReference_tupCur22, ctx5));
                                    tupCur2 = tempReference_tupCur22.get();
                                    row5 = tempReference_row53.get();
                                }

                                if (ctx5 > 3) {
                                    Reference<ref RowBuffer>tempRef_row54 = new Reference<ref
                                    RowBuffer > (row5);
                                    assert tupCur2.MoveNext(tempRef_row54);
                                    row5 = tempRef_row54.argValue;
                                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer> tempReference_row55 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowBuffer>(row5);
                                    Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor> tempReference_tupCur23 = new Reference<com.azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur2);
                                    ResultAssert.IsSuccess(LayoutType.Int64.WriteSparse(tempReference_row55,
                                        tempReference_tupCur23, ctx5));
                                    tupCur2 = tempReference_tupCur23.get();
                                    row5 = tempReference_row55.get();
                                }

                                return Result.SUCCESS;
                            }));
                            tupCur = tempReference_tupCur2.get();
                            row4 = tempReference_row43.get();
                        }

                        return Result.SUCCESS;
                    }));
                    row3 = tempRef_row32.argValue;
                    row3 = tempReference_row3.get();

                    return Result.SUCCESS;
                }));
                arrCur = tempReference_arrCur.get();
                row2 = tempReference_row2.get();

                Reference<ref RowBuffer>tempRef_row22 = new Reference<ref RowBuffer > (row2);
                assert !arrCur.MoveNext(tempRef_row22);
                row2 = tempRef_row22.argValue;
            }

            return Result.SUCCESS;
        }));
        history = tempReference_history.get();
        row = tempReference_row3.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString("TestData\\PerfCounterSchema.json");
        this.counterSchema = Namespace.Parse(json);
        this.countersResolver = new LayoutResolverNamespace(this.counterSchema);
        this.countersLayout = this.countersResolver.Resolve(tangible.ListHelper.find(this.counterSchema.getSchemas(),
            x -> x.Name.equals("Counters")).SchemaId);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PreventInsertsAndDeletesInFixedArityCounter()
    public void PreventInsertsAndDeletesInFixedArityCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = this.counterExample;
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("value", out c);
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.Create(tempReference_row3, out valueScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        RowCursor valueScope2;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.Create(tempReference_row6, out valueScope2).Find(tempReference_row7, c.Path);
        row = tempReference_row7.get();
        row = tempReference_row6.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().ReadScope(tempReference_row8, ref valueScope2,
            out valueScope2));
        row = tempReference_row8.get();
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert valueScope.AsReadOnly(out _).ScopeType == valueScope2.ScopeType;
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert valueScope.AsReadOnly(out _).start == valueScope2.start();
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert valueScope.AsReadOnly(out _).Immutable == valueScope2.Immutable;

        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row9, ref valueScope,
            "millis", UpdateOptions.InsertAt));
        row = tempReference_row9.get();
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().DeleteSparse(tempReference_row10,
            ref valueScope));
        row = tempReference_row10.get();
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        assert !valueScope.MoveTo(tempReference_row11, 2);
        row = tempReference_row11.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void VerifyTypeConstraintsCoordCounter()
    public void VerifyTypeConstraintsCoordCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "RowInserts";
        Coord tempVar = new Coord();
        tempVar.Lat = 12L;
        tempVar.Lng = 40L;
        c1.Coord = Tuple.Create("units", tempVar);

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("coord", out c);
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.Create(tempReference_row3, out valueScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope =
            new Reference<RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.DateTime.WriteSparse(tempReference_row6, tempReference_valueScope,
            LocalDateTime.now()));
        valueScope = tempReference_valueScope.get();
        row = tempReference_row6.get();
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row7, ref valueScope,
            "mins"));
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        assert valueScope.MoveNext(tempReference_row8);
        row = tempReference_row8.get();
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope2 =
            new Reference<RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Int8.WriteSparse(tempReference_row9, tempReference_valueScope2, (byte)42));
        valueScope = tempReference_valueScope2.get();
        row = tempReference_row9.get();

        TypeArgument coordType = c.TypeArgs[1];

        // Invalid because is a UDT but the wrong type.
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope3 =
            new Reference<RowCursor>(valueScope);
        RowCursor _;
        Out<RowCursor> tempOut__ =
            new Out<RowCursor>();
        ResultAssert.TypeConstraint(coordType.getType().<LayoutUDT>TypeAs().WriteScope(tempReference_row10,
            tempReference_valueScope3, new TypeArgumentList(this.countersLayout.getSchemaId().clone()), tempOut__));
        _ = tempOut__.get();
        valueScope = tempReference_valueScope3.get();
        row = tempReference_row10.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void VerifyTypeConstraintsCounter()
    public void VerifyTypeConstraintsCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = this.counterExample;
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("value", out c);
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.Create(tempReference_row3, out valueScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope =
            new Reference<RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Boolean.WriteSparse(tempReference_row6, tempReference_valueScope, true));
        valueScope = tempReference_valueScope.get();
        row = tempReference_row6.get();
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row7, ref valueScope,
            "millis"));
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        assert valueScope.MoveNext(tempReference_row8);
        row = tempReference_row8.get();
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope2 =
            new Reference<RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Float32.WriteSparse(tempReference_row9, tempReference_valueScope2,
            0.1F));
        valueScope = tempReference_valueScope2.get();
        row = tempReference_row9.get();
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt64>TypeAs().WriteSparse(tempReference_row10, ref valueScope,
            100L));
        row = tempReference_row10.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void VerifyTypeConstraintsMinMeanMaxCounter()
    public void VerifyTypeConstraintsMinMeanMaxCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "RowInserts";
        c1.MinMaxValue = Tuple.Create("units", Tuple.Create(12L, 542L, 12046L));

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteCounter(tempReference_row, RowCursor.Create(tempReference_row2, out _), c1);
        row = tempReference_row2.get();
        row = tempReference_row.get();

        // ReSharper disable once StringLiteralTypo
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("minmeanmax", out c);
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        RowCursor.Create(tempReference_row3, out valueScope).Find(tempReference_row4, c.Path);
        row = tempReference_row4.get();
        row = tempReference_row3.get();
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempReference_row5.get();
        Reference<RowBuffer> tempReference_row6 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope =
            new Reference<RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.DateTime.WriteSparse(tempReference_row6, tempReference_valueScope,
            LocalDateTime.now()));
        valueScope = tempReference_valueScope.get();
        row = tempReference_row6.get();
        Reference<RowBuffer> tempReference_row7 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempReference_row7, ref valueScope,
            "secs"));
        row = tempReference_row7.get();
        Reference<RowBuffer> tempReference_row8 =
            new Reference<RowBuffer>(row);
        assert valueScope.MoveNext(tempReference_row8);
        row = tempReference_row8.get();
        Reference<RowBuffer> tempReference_row9 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_valueScope2 =
            new Reference<RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Decimal.WriteSparse(tempReference_row9, tempReference_valueScope2, 12));
        valueScope = tempReference_valueScope2.get();
        row = tempReference_row9.get();

        TypeArgument mmmType = c.TypeArgs[1];

        // Invalid because not a tuple type.
        Reference<RowBuffer> tempReference_row10 =
            new Reference<RowBuffer>(row);
        RowCursor mmmScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row10,
            ref valueScope, TypeArgumentList.Empty, out mmmScope));
        row = tempReference_row10.get();

        // Invalid because is a tuple type but with the wrong parameters.
        Reference<RowBuffer> tempReference_row11 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row11,
            ref valueScope, new TypeArgumentList(new TypeArgument[]
            {
                new TypeArgument(LayoutType.Boolean),
                new TypeArgument(LayoutType.Int64)
            }), out mmmScope));
        row = tempReference_row11.get();

        // Invalid because is a tuple type but with the wrong arity.
        Reference<RowBuffer> tempReference_row12 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row12,
            ref valueScope, new TypeArgumentList(new TypeArgument[] { new TypeArgument(LayoutType.Utf8) }),
            out mmmScope));
        row = tempReference_row12.get();

        Reference<RowBuffer> tempReference_row13 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempReference_row13,
            ref valueScope, mmmType.getTypeArgs().clone(), out mmmScope));
        row = tempReference_row13.get();
        Reference<RowBuffer> tempReference_row14 =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ResultAssert.TypeConstraint(LayoutType.Binary.WriteSparse(ref row, ref valueScope, new
        // byte[] { 1, 2, 3 }));
        ResultAssert.TypeConstraint(LayoutType.Binary.WriteSparse(tempReference_row14, ref valueScope, new byte[] { 1
            , 2,
            3 }));
        row = tempReference_row14.get();
        Reference<RowBuffer> tempReference_row15 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_mmmScope =
            new Reference<RowCursor>(mmmScope);
        ResultAssert.IsSuccess(mmmType.getTypeArgs().get(0).getType().<LayoutInt64>TypeAs().WriteSparse(tempReference_row15
            , tempReference_mmmScope, 1L));
        mmmScope = tempReference_mmmScope.get();
        row = tempReference_row15.get();
        Reference<RowBuffer> tempReference_row16 =
            new Reference<RowBuffer>(row);
        assert mmmScope.MoveNext(tempReference_row16);
        row = tempReference_row16.get();
        Reference<RowBuffer> tempReference_row17 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_mmmScope2 =
            new Reference<RowCursor>(mmmScope);
        ResultAssert.IsSuccess(mmmType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().WriteSparse(tempReference_row17
            , tempReference_mmmScope2, 2L));
        mmmScope = tempReference_mmmScope2.get();
        row = tempReference_row17.get();
        Reference<RowBuffer> tempReference_row18 =
            new Reference<RowBuffer>(row);
        assert mmmScope.MoveNext(tempReference_row18);
        row = tempReference_row18.get();
        Reference<RowBuffer> tempReference_row19 =
            new Reference<RowBuffer>(row);
        Reference<RowCursor> tempReference_mmmScope3 =
            new Reference<RowCursor>(mmmScope);
        ResultAssert.IsSuccess(mmmType.getTypeArgs().get(2).getType().<LayoutInt64>TypeAs().WriteSparse(tempReference_row19
            , tempReference_mmmScope3, 3L));
        mmmScope = tempReference_mmmScope3.get();
        row = tempReference_row19.get();
    }

    private static Coord ReadCoord(Reference<RowBuffer> row, Reference<RowCursor> coordScope) {
        Layout coordLayout = coordScope.get().getLayout();
        Coord cd = new Coord();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lat", out c);
        Out<Long> tempOut_Lat = new Out<Long>();
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().ReadFixed(row, coordScope, c, tempOut_Lat));
        cd.Lat = tempOut_Lat.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lng", out c);
        Out<Long> tempOut_Lng = new Out<Long>();
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().ReadFixed(row, coordScope, c, tempOut_Lng));
        cd.Lng = tempOut_Lng.get();

        return cd;
    }

    private PerfCounter ReadCounter(Reference<RowBuffer> row, Reference<RowCursor> root) {
        PerfCounter pc = new PerfCounter();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("name", out c);
        Out<String> tempOut_Name = new Out<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Name));
        pc.Name = tempOut_Name.get();

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("value", out c);
        assert c.Type.Immutable;
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out valueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref valueScope, out valueScope) == Result.SUCCESS) {
            assert valueScope.Immutable;
            assert valueScope.MoveNext(row);
            String units;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref valueScope, out units));
            assert valueScope.MoveNext(row);
            long metric;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt64>TypeAs().ReadSparse(row, ref valueScope,
                out metric));
            pc.Value = Tuple.Create(units, metric);
        }

        // ReSharper disable once StringLiteralTypo
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("minmeanmax", out c);
        assert c.Type.Immutable;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out valueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref valueScope, out valueScope) == Result.SUCCESS) {
            assert valueScope.Immutable;
            assert valueScope.MoveNext(row);
            String units;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref valueScope, out units));

            assert valueScope.MoveNext(row);
            TypeArgument mmmType = c.TypeArgs[1];
            Reference<RowCursor> tempReference_valueScope =
                new Reference<RowCursor>(valueScope);
            RowCursor mmmScope;
            Out<RowCursor> tempOut_mmmScope =
                new Out<RowCursor>();
            ResultAssert.IsSuccess(mmmType.getType().<LayoutIndexedScope>TypeAs().ReadScope(row, tempReference_valueScope,
                tempOut_mmmScope));
            mmmScope = tempOut_mmmScope.get();
            valueScope = tempReference_valueScope.get();

            assert mmmScope.Immutable;
            assert mmmScope.MoveNext(row);
            Reference<RowCursor> tempReference_mmmScope =
                new Reference<RowCursor>(mmmScope);
            long min;
            Out<Long> tempOut_min = new Out<Long>();
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(0).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                tempReference_mmmScope, tempOut_min));
            min = tempOut_min.get();
            mmmScope = tempReference_mmmScope.get();
            assert mmmScope.MoveNext(row);
            Reference<RowCursor> tempReference_mmmScope2 =
                new Reference<RowCursor>(mmmScope);
            long mean;
            Out<Long> tempOut_mean = new Out<Long>();
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                tempReference_mmmScope2, tempOut_mean));
            mean = tempOut_mean.get();
            mmmScope = tempReference_mmmScope2.get();
            assert mmmScope.MoveNext(row);
            Reference<RowCursor> tempReference_mmmScope3 =
                new Reference<RowCursor>(mmmScope);
            long max;
            Out<Long> tempOut_max = new Out<Long>();
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(2).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                tempReference_mmmScope3, tempOut_max));
            max = tempOut_max.get();
            mmmScope = tempReference_mmmScope3.get();

            pc.MinMaxValue = Tuple.Create(units, Tuple.Create(min, mean, max));
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("coord", out c);
        assert c.Type.Immutable;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out valueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref valueScope, out valueScope) == Result.SUCCESS) {
            assert valueScope.Immutable;
            assert valueScope.MoveNext(row);
            String units;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref valueScope, out units));

            assert valueScope.MoveNext(row);
            RowCursor coordScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutUDT>TypeAs().ReadScope(row, ref valueScope,
                out coordScope));
            Reference<RowCursor> tempReference_coordScope = new Reference<RowCursor>(coordScope);
            pc.Coord = Tuple.Create(units, TupleUnitTests.ReadCoord(row, tempReference_coordScope));
            coordScope = tempReference_coordScope.get();
        }

        return pc;
    }

    private static void WriteCoord(Reference<RowBuffer> row, Reference<RowCursor> coordScope, TypeArgumentList typeArgs, Coord cd) {
        Layout coordLayout = row.get().resolver().resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lat", out c);
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().WriteFixed(row, coordScope, c, cd.Lat));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lng", out c);
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().WriteFixed(row, coordScope, c, cd.Lng));
    }

    private void WriteCounter(Reference<RowBuffer> row, Reference<RowCursor> root, PerfCounter pc) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, pc.Name));

        if (pc.Value != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.countersLayout.TryFind("value", out c);
            RowCursor valueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out valueScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope, c.TypeArgs,
                out valueScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref valueScope,
                pc.Value.Item1));
            assert valueScope.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt64>TypeAs().WriteSparse(row, ref valueScope,
                pc.Value.Item2));
        }

        if (pc.MinMaxValue != null) {
            // ReSharper disable once StringLiteralTypo
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.countersLayout.TryFind("minmeanmax", out c);
            RowCursor valueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out valueScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope, c.TypeArgs,
                out valueScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref valueScope,
                pc.MinMaxValue.Item1));

            assert valueScope.MoveNext(row);
            TypeArgument mmmType = c.TypeArgs[1];
            RowCursor mmmScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope,
                mmmType.getTypeArgs().clone(), out mmmScope));

            Reference<RowCursor> tempReference_mmmScope =
                new Reference<RowCursor>(mmmScope);
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(0).getType().<LayoutInt64>TypeAs().WriteSparse(row,
                tempReference_mmmScope, pc.MinMaxValue.Item2.Item1));
            mmmScope = tempReference_mmmScope.get();

            assert mmmScope.MoveNext(row);
            Reference<RowCursor> tempReference_mmmScope2 =
                new Reference<RowCursor>(mmmScope);
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().WriteSparse(row,
                tempReference_mmmScope2, pc.MinMaxValue.Item2.Item2));
            mmmScope = tempReference_mmmScope2.get();

            assert mmmScope.MoveNext(row);
            Reference<RowCursor> tempReference_mmmScope3 =
                new Reference<RowCursor>(mmmScope);
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(2).getType().<LayoutInt64>TypeAs().WriteSparse(row,
                tempReference_mmmScope3, pc.MinMaxValue.Item2.Item3));
            mmmScope = tempReference_mmmScope3.get();
        }

        if (pc.Coord != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            assert this.countersLayout.TryFind("coord", out c);
            RowCursor valueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out valueScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope, c.TypeArgs,
                out valueScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref valueScope,
                pc.Coord.Item1));

            assert valueScope.MoveNext(row);
            TypeArgument mmmType = c.TypeArgs[1];
            Reference<RowCursor> tempReference_valueScope =
                new Reference<RowCursor>(valueScope);
            RowCursor coordScope;
            Out<RowCursor> tempOut_coordScope =
                new Out<RowCursor>();
            ResultAssert.IsSuccess(mmmType.getType().<LayoutUDT>TypeAs().WriteScope(row, tempReference_valueScope,
                mmmType.getTypeArgs().clone(), tempOut_coordScope));
            coordScope = tempOut_coordScope.get();
            valueScope = tempReference_valueScope.get();
            Reference<RowCursor> tempReference_coordScope =
                new Reference<RowCursor>(coordScope);
            TupleUnitTests.WriteCoord(row, tempReference_coordScope, mmmType.getTypeArgs().clone(), pc.Coord.Item2);
            coordScope = tempReference_coordScope.get();
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class Coord
    private final static class Coord {
        public long Lat;
        public long Lng;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Coord;
            Coord coord = tempVar ? (Coord)obj : null;
            return tempVar && this.equals(coord);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                return ((new Long(this.Lat)).hashCode() * 397) ^ (new Long(this.Lng)).hashCode();
            }
        }

        private boolean equals(Coord other) {
            return this.Lat == other.Lat && this.Lng == other.Lng;
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class PerfCounter
    private final static class PerfCounter {
        // ReSharper disable once MemberHidesStaticFromOuterClass
        public Tuple<String, Coord> Coord;
        public Tuple<String, Tuple<Long, Long, Long>> MinMaxValue;
        public String Name;
        public Tuple<String, Long> Value;

        // ReSharper disable once MemberCanBePrivate.Local
        public boolean equals(PerfCounter other) {
            return this.Name.equals(other.Name) && this.Value.equals(other.Value) && this.MinMaxValue.equals(other.MinMaxValue) && this.Coord.equals(other.Coord);
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof PerfCounter;
            PerfCounter counter = tempVar ? (PerfCounter)obj : null;
            return tempVar && this.equals(counter);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                int hashCode = this.Name == null ? null : this.Name.hashCode() != null ? this.Name.hashCode() : 0;
                hashCode = (hashCode * 397) ^ (this.Value == null ? null : this.Value.hashCode() != null ? this.Value.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.MinMaxValue == null ? null : this.MinMaxValue.hashCode() != null ? this.MinMaxValue.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.Coord == null ? null : this.Coord.hashCode() != null ? this.Coord.hashCode() : 0);
                return hashCode;
            }
        }
    }
}