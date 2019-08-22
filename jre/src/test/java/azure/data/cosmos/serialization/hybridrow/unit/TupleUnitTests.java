//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

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
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "CoordInserts";
        Coord tempVar = new Coord();
        tempVar.Lat = 12L;
        tempVar.Lng = 40L;
        c1.Coord = Tuple.Create("units", tempVar);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        PerfCounter c2 = this.ReadCounter(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateCounter()
    public void CreateCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = this.counterExample;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        PerfCounter c2 = this.ReadCounter(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateMinMeanMaxCounter()
    public void CreateMinMeanMaxCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "RowInserts";
        c1.MinMaxValue = Tuple.Create("units", Tuple.Create(12L, 542L, 12046L));

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        PerfCounter c2 = this.ReadCounter(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        assert c1 == c2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void DownwardDelegateWriteScope()
    public void DownwardDelegateWriteScope() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        Layout layout = this.countersResolver.Resolve(tangible.ListHelper.find(this.counterSchema.getSchemas(),
            x -> x.Name.equals("CounterSet")).SchemaId);
        row.InitLayout(HybridRowVersion.V1, layout, this.countersResolver);

        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert layout.TryFind("history", out col);
        StringToken historyToken;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken> tempOut_historyToken =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.StringToken>();
        assert layout.getTokenizer().TryFindToken(col.Path, tempOut_historyToken);
        historyToken = tempOut_historyToken.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor history;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row, out history).Find(tempRef_row2, historyToken);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        int ctx = 1; // ignored
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_history =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(history);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        ResultAssert.IsSuccess(LayoutType.TypedArray.WriteScope(tempRef_row3, tempRef_history, col.TypeArgs, ctx,
            (ref RowBuffer row2, ref RowCursor arrCur, int ctx2) ->
        {
            for (int i = 0; i < 5; i++) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row2);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_arrCur =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(arrCur);
                ResultAssert.IsSuccess(LayoutType.UDT.WriteScope(tempRef_row2, tempRef_arrCur,
                    arrCur.ScopeTypeArgs[0].TypeArgs, i, (ref RowBuffer row3, ref RowCursor udtCur, int ctx3) ->
                {
                    LayoutColumn col3;
                    assert udtCur.Layout.TryFind("minmeanmax", out col3);
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row3);
                    tangible.RefObject<ref RowBuffer>tempRef_row32 = new tangible.RefObject<ref RowBuffer > (row3);
                    ResultAssert.IsSuccess(LayoutType.TypedTuple.WriteScope(tempRef_row3, udtCur.Find(tempRef_row32,
                        col3.Path), col3.TypeArgs, ctx3, (ref RowBuffer row4, ref RowCursor tupCur, int ctx4) ->
                    {
                        if (ctx4 > 0) {
                            tangible.RefObject<ref RowBuffer>tempRef_row4 = new tangible.RefObject<ref
                            RowBuffer > (row4);
                            tangible.RefObject<ref RowCursor>tempRef_tupCur = new tangible.RefObject<ref
                            RowCursor > (tupCur);
                            ResultAssert.IsSuccess(LayoutType.Utf8.WriteSparse(tempRef_row4, tempRef_tupCur, "abc"));
                            tupCur = tempRef_tupCur.argValue;
                            row4 = tempRef_row4.argValue;
                        }

                        if (ctx4 > 1) {
                            tangible.RefObject<ref RowBuffer>tempRef_row42 = new tangible.RefObject<ref
                            RowBuffer > (row4);
                            assert tupCur.MoveNext(tempRef_row42);
                            row4 = tempRef_row42.argValue;
                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row43 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row4);
                            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupCur2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur);
                            ResultAssert.IsSuccess(LayoutType.TypedTuple.WriteScope(tempRef_row43, tempRef_tupCur2,
                                tupCur.ScopeTypeArgs[1].TypeArgs, ctx4, (ref RowBuffer row5, ref RowCursor tupCur2,
                                                                         int ctx5) ->
                            {
                                if (ctx5 > 1) {
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row5);
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupCur2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur2);
                                    ResultAssert.IsSuccess(LayoutType.Int64.WriteSparse(tempRef_row5, tempRef_tupCur2
                                        , ctx5));
                                    tupCur2 = tempRef_tupCur2.argValue;
                                    row5 = tempRef_row5.argValue;
                                }

                                if (ctx5 > 2) {
                                    tangible.RefObject<ref RowBuffer>tempRef_row52 = new tangible.RefObject<ref
                                    RowBuffer > (row5);
                                    assert tupCur2.MoveNext(tempRef_row52);
                                    row5 = tempRef_row52.argValue;
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row53 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row5);
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupCur22 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur2);
                                    ResultAssert.IsSuccess(LayoutType.Int64.WriteSparse(tempRef_row53,
                                        tempRef_tupCur22, ctx5));
                                    tupCur2 = tempRef_tupCur22.argValue;
                                    row5 = tempRef_row53.argValue;
                                }

                                if (ctx5 > 3) {
                                    tangible.RefObject<ref RowBuffer>tempRef_row54 = new tangible.RefObject<ref
                                    RowBuffer > (row5);
                                    assert tupCur2.MoveNext(tempRef_row54);
                                    row5 = tempRef_row54.argValue;
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row55 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row5);
                                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupCur23 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupCur2);
                                    ResultAssert.IsSuccess(LayoutType.Int64.WriteSparse(tempRef_row55,
                                        tempRef_tupCur23, ctx5));
                                    tupCur2 = tempRef_tupCur23.argValue;
                                    row5 = tempRef_row55.argValue;
                                }

                                return Result.Success;
                            }));
                            tupCur = tempRef_tupCur2.argValue;
                            row4 = tempRef_row43.argValue;
                        }

                        return Result.Success;
                    }));
                    row3 = tempRef_row32.argValue;
                    row3 = tempRef_row3.argValue;

                    return Result.Success;
                }));
                arrCur = tempRef_arrCur.argValue;
                row2 = tempRef_row2.argValue;

                tangible.RefObject<ref RowBuffer>tempRef_row22 = new tangible.RefObject<ref RowBuffer > (row2);
                assert !arrCur.MoveNext(tempRef_row22);
                row2 = tempRef_row22.argValue;
            }

            return Result.Success;
        }));
        history = tempRef_history.argValue;
        row = tempRef_row3.argValue;
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
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = this.counterExample;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("value", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row3, out valueScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor valueScope2;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row6, out valueScope2).Find(tempRef_row7, c.Path);
        row = tempRef_row7.argValue;
        row = tempRef_row6.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().ReadScope(tempRef_row8, ref valueScope2,
            out valueScope2));
        row = tempRef_row8.argValue;
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert valueScope.AsReadOnly(out _).ScopeType == valueScope2.ScopeType;
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert valueScope.AsReadOnly(out _).start == valueScope2.start;
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert valueScope.AsReadOnly(out _).Immutable == valueScope2.Immutable;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row9, ref valueScope,
            "millis", UpdateOptions.InsertAt));
        row = tempRef_row9.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().DeleteSparse(tempRef_row10,
            ref valueScope));
        row = tempRef_row10.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert !valueScope.MoveTo(tempRef_row11, 2);
        row = tempRef_row11.argValue;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void VerifyTypeConstraintsCoordCounter()
    public void VerifyTypeConstraintsCoordCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "RowInserts";
        Coord tempVar = new Coord();
        tempVar.Lat = 12L;
        tempVar.Lng = 40L;
        c1.Coord = Tuple.Create("units", tempVar);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("coord", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row3, out valueScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.DateTime.WriteSparse(tempRef_row6, tempRef_valueScope,
            LocalDateTime.now()));
        valueScope = tempRef_valueScope.argValue;
        row = tempRef_row6.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref valueScope,
            "mins"));
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert valueScope.MoveNext(tempRef_row8);
        row = tempRef_row8.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Int8.WriteSparse(tempRef_row9, tempRef_valueScope2, (byte)42));
        valueScope = tempRef_valueScope2.argValue;
        row = tempRef_row9.argValue;

        TypeArgument coordType = c.TypeArgs[1];

        // Invalid because is a UDT but the wrong type.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        RowCursor _;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut__ =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        ResultAssert.TypeConstraint(coordType.getType().<LayoutUDT>TypeAs().WriteScope(tempRef_row10,
            tempRef_valueScope3, new TypeArgumentList(this.countersLayout.getSchemaId().clone()), tempOut__));
        _ = tempOut__.argValue;
        valueScope = tempRef_valueScope3.argValue;
        row = tempRef_row10.argValue;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void VerifyTypeConstraintsCounter()
    public void VerifyTypeConstraintsCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = this.counterExample;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("value", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row3, out valueScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Boolean.WriteSparse(tempRef_row6, tempRef_valueScope, true));
        valueScope = tempRef_valueScope.argValue;
        row = tempRef_row6.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref valueScope,
            "millis"));
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert valueScope.MoveNext(tempRef_row8);
        row = tempRef_row8.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Float32.WriteSparse(tempRef_row9, tempRef_valueScope2, 0.1F));
        valueScope = tempRef_valueScope2.argValue;
        row = tempRef_row9.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt64>TypeAs().WriteSparse(tempRef_row10, ref valueScope,
            100L));
        row = tempRef_row10.argValue;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void VerifyTypeConstraintsMinMeanMaxCounter()
    public void VerifyTypeConstraintsMinMeanMaxCounter() {
        RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.countersLayout, this.countersResolver);

        PerfCounter c1 = new PerfCounter();
        c1.Name = "RowInserts";
        c1.MinMaxValue = Tuple.Create("units", Tuple.Create(12L, 542L, 12046L));

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteCounter(tempRef_row, RowCursor.Create(tempRef_row2, out _), c1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;

        // ReSharper disable once StringLiteralTypo
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("minmeanmax", out c);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        RowCursor.Create(tempRef_row3, out valueScope).Find(tempRef_row4, c.Path);
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row5, ref valueScope, c.TypeArgs,
            out valueScope));
        row = tempRef_row5.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row6 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.DateTime.WriteSparse(tempRef_row6, tempRef_valueScope,
            LocalDateTime.now()));
        valueScope = tempRef_valueScope.argValue;
        row = tempRef_row6.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row7 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(tempRef_row7, ref valueScope,
            "secs"));
        row = tempRef_row7.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row8 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert valueScope.MoveNext(tempRef_row8);
        row = tempRef_row8.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row9 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
        ResultAssert.TypeConstraint(LayoutType.Decimal.WriteSparse(tempRef_row9, tempRef_valueScope2, 12));
        valueScope = tempRef_valueScope2.argValue;
        row = tempRef_row9.argValue;

        TypeArgument mmmType = c.TypeArgs[1];

        // Invalid because not a tuple type.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row10 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor mmmScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row10,
            ref valueScope, TypeArgumentList.Empty, out mmmScope));
        row = tempRef_row10.argValue;

        // Invalid because is a tuple type but with the wrong parameters.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row11 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row11,
            ref valueScope, new TypeArgumentList(new TypeArgument[]
            {
                new TypeArgument(LayoutType.Boolean),
                new TypeArgument(LayoutType.Int64)
            }), out mmmScope));
        row = tempRef_row11.argValue;

        // Invalid because is a tuple type but with the wrong arity.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row12 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.TypeConstraint(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row12,
            ref valueScope, new TypeArgumentList(new TypeArgument[] { new TypeArgument(LayoutType.Utf8) }),
            out mmmScope));
        row = tempRef_row12.argValue;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row13 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        ResultAssert.IsSuccess(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(tempRef_row13,
            ref valueScope, mmmType.getTypeArgs().clone(), out mmmScope));
        row = tempRef_row13.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row14 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ResultAssert.TypeConstraint(LayoutType.Binary.WriteSparse(ref row, ref valueScope, new
        // byte[] { 1, 2, 3 }));
        ResultAssert.TypeConstraint(LayoutType.Binary.WriteSparse(tempRef_row14, ref valueScope, new byte[] { 1, 2,
            3 }));
        row = tempRef_row14.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row15 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
        ResultAssert.IsSuccess(mmmType.getTypeArgs().get(0).getType().<LayoutInt64>TypeAs().WriteSparse(tempRef_row15
            , tempRef_mmmScope, 1L));
        mmmScope = tempRef_mmmScope.argValue;
        row = tempRef_row15.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row16 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert mmmScope.MoveNext(tempRef_row16);
        row = tempRef_row16.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row17 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
        ResultAssert.IsSuccess(mmmType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().WriteSparse(tempRef_row17
            , tempRef_mmmScope2, 2L));
        mmmScope = tempRef_mmmScope2.argValue;
        row = tempRef_row17.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row18 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        assert mmmScope.MoveNext(tempRef_row18);
        row = tempRef_row18.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row19 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
        ResultAssert.IsSuccess(mmmType.getTypeArgs().get(2).getType().<LayoutInt64>TypeAs().WriteSparse(tempRef_row19
            , tempRef_mmmScope3, 3L));
        mmmScope = tempRef_mmmScope3.argValue;
        row = tempRef_row19.argValue;
    }

    private static Coord ReadCoord(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> coordScope) {
        Layout coordLayout = coordScope.argValue.getLayout();
        Coord cd = new Coord();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lat", out c);
        tangible.OutObject<Long> tempOut_Lat = new tangible.OutObject<Long>();
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().ReadFixed(row, coordScope, c, tempOut_Lat));
        cd.Lat = tempOut_Lat.argValue;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lng", out c);
        tangible.OutObject<Long> tempOut_Lng = new tangible.OutObject<Long>();
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().ReadFixed(row, coordScope, c, tempOut_Lng));
        cd.Lng = tempOut_Lng.argValue;

        return cd;
    }

    private PerfCounter ReadCounter(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        PerfCounter pc = new PerfCounter();
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("name", out c);
        tangible.OutObject<String> tempOut_Name = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().ReadVariable(row, root, c, tempOut_Name));
        pc.Name = tempOut_Name.argValue;

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("value", out c);
        assert c.Type.Immutable;
        RowCursor valueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out valueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref valueScope, out valueScope) == Result.Success) {
            assert valueScope.Immutable;
            assert valueScope.MoveNext(row);
            String units;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref valueScope, out units));
            assert valueScope.MoveNext(row);
            long metric;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt64>TypeAs().ReadSparse(row, ref valueScope,
                out metric));
            pc.Value = Tuple.Create(units, metric);
        }

        // ReSharper disable once StringLiteralTypo
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("minmeanmax", out c);
        assert c.Type.Immutable;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out valueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref valueScope, out valueScope) == Result.Success) {
            assert valueScope.Immutable;
            assert valueScope.MoveNext(row);
            String units;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref valueScope, out units));

            assert valueScope.MoveNext(row);
            TypeArgument mmmType = c.TypeArgs[1];
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
            RowCursor mmmScope;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_mmmScope =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            ResultAssert.IsSuccess(mmmType.getType().<LayoutIndexedScope>TypeAs().ReadScope(row, tempRef_valueScope,
                tempOut_mmmScope));
            mmmScope = tempOut_mmmScope.argValue;
            valueScope = tempRef_valueScope.argValue;

            assert mmmScope.Immutable;
            assert mmmScope.MoveNext(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
            long min;
            tangible.OutObject<Long> tempOut_min = new tangible.OutObject<Long>();
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(0).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                tempRef_mmmScope, tempOut_min));
            min = tempOut_min.argValue;
            mmmScope = tempRef_mmmScope.argValue;
            assert mmmScope.MoveNext(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
            long mean;
            tangible.OutObject<Long> tempOut_mean = new tangible.OutObject<Long>();
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                tempRef_mmmScope2, tempOut_mean));
            mean = tempOut_mean.argValue;
            mmmScope = tempRef_mmmScope2.argValue;
            assert mmmScope.MoveNext(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
            long max;
            tangible.OutObject<Long> tempOut_max = new tangible.OutObject<Long>();
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(2).getType().<LayoutInt64>TypeAs().ReadSparse(row,
                tempRef_mmmScope3, tempOut_max));
            max = tempOut_max.argValue;
            mmmScope = tempRef_mmmScope3.argValue;

            pc.MinMaxValue = Tuple.Create(units, Tuple.Create(min, mean, max));
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("coord", out c);
        assert c.Type.Immutable;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out valueScope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutIndexedScope>TypeAs().ReadScope(row, ref valueScope, out valueScope) == Result.Success) {
            assert valueScope.Immutable;
            assert valueScope.MoveNext(row);
            String units;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().ReadSparse(row, ref valueScope, out units));

            assert valueScope.MoveNext(row);
            RowCursor coordScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutUDT>TypeAs().ReadScope(row, ref valueScope,
                out coordScope));
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_coordScope = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(coordScope);
            pc.Coord = Tuple.Create(units, TupleUnitTests.ReadCoord(row, tempRef_coordScope));
            coordScope = tempRef_coordScope.argValue;
        }

        return pc;
    }

    private static void WriteCoord(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> coordScope, TypeArgumentList typeArgs, Coord cd) {
        Layout coordLayout = row.argValue.getResolver().Resolve(typeArgs.getSchemaId().clone());
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lat", out c);
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().WriteFixed(row, coordScope, c, cd.Lat));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert coordLayout.TryFind("lng", out c);
        ResultAssert.IsSuccess(c.<LayoutInt64>TypeAs().WriteFixed(row, coordScope, c, cd.Lng));
    }

    private void WriteCounter(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root, PerfCounter pc) {
        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.countersLayout.TryFind("name", out c);
        ResultAssert.IsSuccess(c.<LayoutUtf8>TypeAs().WriteVariable(row, root, c, pc.Name));

        if (pc.Value != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.countersLayout.TryFind("value", out c);
            RowCursor valueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out valueScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope, c.TypeArgs,
                out valueScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref valueScope,
                pc.Value.Item1));
            assert valueScope.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.<LayoutInt64>TypeAs().WriteSparse(row, ref valueScope,
                pc.Value.Item2));
        }

        if (pc.MinMaxValue != null) {
            // ReSharper disable once StringLiteralTypo
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.countersLayout.TryFind("minmeanmax", out c);
            RowCursor valueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out valueScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope, c.TypeArgs,
                out valueScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref valueScope,
                pc.MinMaxValue.Item1));

            assert valueScope.MoveNext(row);
            TypeArgument mmmType = c.TypeArgs[1];
            RowCursor mmmScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(mmmType.getType().<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope,
                mmmType.getTypeArgs().clone(), out mmmScope));

            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(0).getType().<LayoutInt64>TypeAs().WriteSparse(row,
                tempRef_mmmScope, pc.MinMaxValue.Item2.Item1));
            mmmScope = tempRef_mmmScope.argValue;

            assert mmmScope.MoveNext(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(1).getType().<LayoutInt64>TypeAs().WriteSparse(row,
                tempRef_mmmScope2, pc.MinMaxValue.Item2.Item2));
            mmmScope = tempRef_mmmScope2.argValue;

            assert mmmScope.MoveNext(row);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_mmmScope3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(mmmScope);
            ResultAssert.IsSuccess(mmmType.getTypeArgs().get(2).getType().<LayoutInt64>TypeAs().WriteSparse(row,
                tempRef_mmmScope3, pc.MinMaxValue.Item2.Item3));
            mmmScope = tempRef_mmmScope3.argValue;
        }

        if (pc.Coord != null) {
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            assert this.countersLayout.TryFind("coord", out c);
            RowCursor valueScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out valueScope).Find(row, c.Path);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutIndexedScope>TypeAs().WriteScope(row, ref valueScope, c.TypeArgs,
                out valueScope));
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.<LayoutUtf8>TypeAs().WriteSparse(row, ref valueScope,
                pc.Coord.Item1));

            assert valueScope.MoveNext(row);
            TypeArgument mmmType = c.TypeArgs[1];
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_valueScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(valueScope);
            RowCursor coordScope;
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_coordScope =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            ResultAssert.IsSuccess(mmmType.getType().<LayoutUDT>TypeAs().WriteScope(row, tempRef_valueScope,
                mmmType.getTypeArgs().clone(), tempOut_coordScope));
            coordScope = tempOut_coordScope.argValue;
            valueScope = tempRef_valueScope.argValue;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_coordScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(coordScope);
            TupleUnitTests.WriteCoord(row, tempRef_coordScope, mmmType.getTypeArgs().clone(), pc.Coord.Item2);
            coordScope = tempRef_coordScope.argValue;
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