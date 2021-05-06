// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.TypedArray;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.TypedTuple;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    // ReSharper disable once StringLiteralTypo
    [TestClass]
    public sealed class TupleUnitTests
    {
        private const int InitialRowSize = 2 * 1024 * 1024;

        private readonly PerfCounter counterExample = new PerfCounter()
        {
            Name = "RowInserts",
            Value = ("units", 12046L),
        };

        private Layout countersLayout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            this.countersLayout = TypedTupleHrSchema.LayoutResolver.Resolve((SchemaId)PerfCounterHybridRowSerializer.SchemaId);

        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = this.counterExample;
            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);
            PerfCounter c2 = this.ReadCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.IsTrue(PerfCounterComparer.Default.Equals(c1, c2));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void VerifyTypeConstraintsCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = this.counterExample;
            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);

            Assert.IsTrue(this.countersLayout.TryFind("value", out LayoutColumn c));
            RowCursor.Create(ref row, out RowCursor valueScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
            ResultAssert.TypeConstraint(LayoutType.Boolean.WriteSparse(ref row, ref valueScope, true));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, "millis"));
            Assert.IsTrue(valueScope.MoveNext(ref row));
            ResultAssert.TypeConstraint(LayoutType.Float32.WriteSparse(ref row, ref valueScope, 0.1F));
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref valueScope, 100L));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void PreventInsertsAndDeletesInFixedArityCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = this.counterExample;
            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);

            Assert.IsTrue(this.countersLayout.TryFind("value", out LayoutColumn c));
            RowCursor.Create(ref row, out RowCursor valueScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
            RowCursor.Create(ref row, out RowCursor valueScope2).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref valueScope2, out valueScope2));
            Assert.AreEqual(valueScope.AsReadOnly(out RowCursor _).ScopeType, valueScope2.ScopeType);
            Assert.AreEqual(valueScope.AsReadOnly(out RowCursor _).start, valueScope2.start);
            Assert.AreEqual(valueScope.AsReadOnly(out RowCursor _).Immutable, valueScope2.Immutable);

            ResultAssert.TypeConstraint(
                c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, "millis", UpdateOptions.InsertAt));
            ResultAssert.TypeConstraint(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().DeleteSparse(ref row, ref valueScope));
            Assert.IsFalse(valueScope.MoveTo(ref row, 2));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateMinMeanMaxCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = new PerfCounter()
            {
                Name = "RowInserts",
                MinMaxValue = ("units", (12L, 542L, 12046L)),
            };

            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);
            PerfCounter c2 = this.ReadCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.IsTrue(PerfCounterComparer.Default.Equals(c1, c2));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateCoordCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = new PerfCounter()
            {
                Name = "CoordInserts",
                Coord = ("units", new Coord { Lat = 12L, Lng = 40L }),
            };

            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);
            PerfCounter c2 = this.ReadCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.IsTrue(PerfCounterComparer.Default.Equals(c1, c2));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void VerifyTypeConstraintsMinMeanMaxCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = new PerfCounter()
            {
                Name = "RowInserts",
                MinMaxValue = ("units", (12L, 542L, 12046L)),
            };

            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);

            // ReSharper disable once StringLiteralTypo
            Assert.IsTrue(this.countersLayout.TryFind("minmeanmax", out LayoutColumn c));
            RowCursor.Create(ref row, out RowCursor valueScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
            ResultAssert.TypeConstraint(LayoutType.DateTime.WriteSparse(ref row, ref valueScope, DateTime.Now));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, "secs"));
            Assert.IsTrue(valueScope.MoveNext(ref row));
            ResultAssert.TypeConstraint(LayoutType.Decimal.WriteSparse(ref row, ref valueScope, 12M));

            TypeArgument mmmType = c.TypeArgs[1];

            // Invalid because not a tuple type.
            ResultAssert.TypeConstraint(
                mmmType.Type.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, TypeArgumentList.Empty, out RowCursor mmmScope));

            // Invalid because is a tuple type but with the wrong parameters.
            ResultAssert.TypeConstraint(
                mmmType.Type.TypeAs<LayoutIndexedScope>()
                    .WriteScope(
                        ref row,
                        ref valueScope,
                        new TypeArgumentList(
                            new[]
                            {
                                new TypeArgument(LayoutType.Boolean),
                                new TypeArgument(LayoutType.Int64),
                            }),
                        out mmmScope));

            // Invalid because is a tuple type but with the wrong arity.
            ResultAssert.TypeConstraint(
                mmmType.Type.TypeAs<LayoutIndexedScope>()
                    .WriteScope(
                        ref row,
                        ref valueScope,
                        new TypeArgumentList(
                            new[]
                            {
                                new TypeArgument(LayoutType.Utf8),
                            }),
                        out mmmScope));

            ResultAssert.IsSuccess(
                mmmType.Type.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, mmmType.TypeArgs, out mmmScope));
            ResultAssert.TypeConstraint(LayoutType.Binary.WriteSparse(ref row, ref valueScope, new byte[] { 1, 2, 3 }));
            ResultAssert.IsSuccess(mmmType.TypeArgs[0].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref mmmScope, 1L));
            Assert.IsTrue(mmmScope.MoveNext(ref row));
            ResultAssert.IsSuccess(mmmType.TypeArgs[1].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref mmmScope, 2L));
            Assert.IsTrue(mmmScope.MoveNext(ref row));
            ResultAssert.IsSuccess(mmmType.TypeArgs[2].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref mmmScope, 3L));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void VerifyTypeConstraintsCoordCounter()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.countersLayout, TypedTupleHrSchema.LayoutResolver);

            PerfCounter c1 = new PerfCounter()
            {
                Name = "RowInserts",
                Coord = ("units", new Coord { Lat = 12L, Lng = 40L }),
            };

            this.WriteCounter(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);

            Assert.IsTrue(this.countersLayout.TryFind("coord", out LayoutColumn c));
            RowCursor.Create(ref row, out RowCursor valueScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
            ResultAssert.TypeConstraint(LayoutType.DateTime.WriteSparse(ref row, ref valueScope, DateTime.Now));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, "mins"));
            Assert.IsTrue(valueScope.MoveNext(ref row));
            ResultAssert.TypeConstraint(LayoutType.Int8.WriteSparse(ref row, ref valueScope, 42));

            TypeArgument coordType = c.TypeArgs[1];

            // Invalid because is a UDT but the wrong type.
            ResultAssert.TypeConstraint(
                coordType.Type.TypeAs<LayoutUDT>()
                    .WriteScope(
                        ref row,
                        ref valueScope,
                        new TypeArgumentList(this.countersLayout.SchemaId),
                        out RowCursor _));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void DownwardDelegateWriteScope()
        {
            RowBuffer row = new RowBuffer(TupleUnitTests.InitialRowSize);
            Layout layout = TypedTupleHrSchema.LayoutResolver.Resolve((SchemaId)CounterSetHybridRowSerializer.SchemaId);
            row.InitLayout(HybridRowVersion.V1, layout, TypedTupleHrSchema.LayoutResolver);

            Assert.IsTrue(layout.TryFind("history", out LayoutColumn col));
            Assert.IsTrue(layout.Tokenizer.TryFindToken(col.Path, out StringToken historyToken));
            RowCursor.Create(ref row, out RowCursor history).Find(ref row, historyToken);
            int ctx = 1; // ignored
            ResultAssert.IsSuccess(
                LayoutType.TypedArray.WriteScope(
                    ref row,
                    ref history,
                    col.TypeArgs,
                    ctx,
                    (ref RowBuffer row2, ref RowCursor arrCur, int ctx2) =>
                    {
                        for (int i = 0; i < 5; i++)
                        {
                            ResultAssert.IsSuccess(
                                LayoutType.UDT.WriteScope(
                                    ref row2,
                                    ref arrCur,
                                    arrCur.ScopeTypeArgs[0].TypeArgs,
                                    i,
                                    (ref RowBuffer row3, ref RowCursor udtCur, int ctx3) =>
                                    {
                                        Assert.IsTrue(udtCur.Layout.TryFind("minmeanmax", out LayoutColumn col3));
                                        ResultAssert.IsSuccess(
                                            LayoutType.TypedTuple.WriteScope(
                                                ref row3,
                                                ref udtCur.Find(ref row3, col3.Path),
                                                col3.TypeArgs,
                                                ctx3,
                                                (ref RowBuffer row4, ref RowCursor tupCur, int ctx4) =>
                                                {
                                                    if (ctx4 > 0)
                                                    {
                                                        ResultAssert.IsSuccess(LayoutType.Utf8.WriteSparse(ref row4, ref tupCur, "abc"));
                                                    }

                                                    if (ctx4 > 1)
                                                    {
                                                        Assert.IsTrue(tupCur.MoveNext(ref row4));
                                                        ResultAssert.IsSuccess(
                                                            LayoutType.TypedTuple.WriteScope(
                                                                ref row4,
                                                                ref tupCur,
                                                                tupCur.ScopeTypeArgs[1].TypeArgs,
                                                                ctx4,
                                                                (ref RowBuffer row5, ref RowCursor tupCur2, int ctx5) =>
                                                                {
                                                                    if (ctx5 > 1)
                                                                    {
                                                                        ResultAssert.IsSuccess(
                                                                            LayoutType.Int64.WriteSparse(ref row5, ref tupCur2, ctx5));
                                                                    }

                                                                    if (ctx5 > 2)
                                                                    {
                                                                        Assert.IsTrue(tupCur2.MoveNext(ref row5));
                                                                        ResultAssert.IsSuccess(
                                                                            LayoutType.Int64.WriteSparse(ref row5, ref tupCur2, ctx5));
                                                                    }

                                                                    if (ctx5 > 3)
                                                                    {
                                                                        Assert.IsTrue(tupCur2.MoveNext(ref row5));
                                                                        ResultAssert.IsSuccess(
                                                                            LayoutType.Int64.WriteSparse(ref row5, ref tupCur2, ctx5));
                                                                    }

                                                                    return Result.Success;
                                                                }));
                                                    }

                                                    return Result.Success;
                                                }));

                                        return Result.Success;
                                    }));

                            Assert.IsFalse(arrCur.MoveNext(ref row2));
                        }

                        return Result.Success;
                    }));
        }

        private static void WriteCoord(ref RowBuffer row, ref RowCursor coordScope, TypeArgumentList typeArgs, Coord cd)
        {
            Layout coordLayout = row.Resolver.Resolve(typeArgs.SchemaId);
            Assert.IsTrue(coordLayout.TryFind("lat", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutInt64>().WriteFixed(ref row, ref coordScope, c, cd.Lat));
            Assert.IsTrue(coordLayout.TryFind("lng", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutInt64>().WriteFixed(ref row, ref coordScope, c, cd.Lng));
        }

        private static Coord ReadCoord(ref RowBuffer row, ref RowCursor coordScope)
        {
            Layout coordLayout = coordScope.Layout;
            Coord cd = new Coord();
            Assert.IsTrue(coordLayout.TryFind("lat", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutInt64>().ReadFixed(ref row, ref coordScope, c, out long lat));
            cd.Lat = lat;
            Assert.IsTrue(coordLayout.TryFind("lng", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutInt64>().ReadFixed(ref row, ref coordScope, c, out long lng));
            cd.Lng = lng;

            return cd;
        }

        private void WriteCounter(ref RowBuffer row, ref RowCursor root, PerfCounter pc)
        {
            Assert.IsTrue(this.countersLayout.TryFind("name", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, pc.Name));

            if (pc.Value != default)
            {
                Assert.IsTrue(this.countersLayout.TryFind("value", out c));
                root.Clone(out RowCursor valueScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, pc.Value.Item1));
                Assert.IsTrue(valueScope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref valueScope, pc.Value.Item2));
            }

            if (pc.MinMaxValue != default)
            {
                // ReSharper disable once StringLiteralTypo
                Assert.IsTrue(this.countersLayout.TryFind("minmeanmax", out c));
                root.Clone(out RowCursor valueScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, pc.MinMaxValue.Item1));

                Assert.IsTrue(valueScope.MoveNext(ref row));
                TypeArgument mmmType = c.TypeArgs[1];
                ResultAssert.IsSuccess(
                    mmmType.Type.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, mmmType.TypeArgs, out RowCursor mmmScope));

                ResultAssert.IsSuccess(
                    mmmType.TypeArgs[0].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref mmmScope, pc.MinMaxValue.Item2.Item1));

                Assert.IsTrue(mmmScope.MoveNext(ref row));
                ResultAssert.IsSuccess(
                    mmmType.TypeArgs[1].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref mmmScope, pc.MinMaxValue.Item2.Item2));

                Assert.IsTrue(mmmScope.MoveNext(ref row));
                ResultAssert.IsSuccess(
                    mmmType.TypeArgs[2].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref mmmScope, pc.MinMaxValue.Item2.Item3));
            }

            if (pc.Coord != default)
            {
                Assert.IsTrue(this.countersLayout.TryFind("coord", out c));
                root.Clone(out RowCursor valueScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref valueScope, c.TypeArgs, out valueScope));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref valueScope, pc.Coord.Item1));

                Assert.IsTrue(valueScope.MoveNext(ref row));
                TypeArgument mmmType = c.TypeArgs[1];
                ResultAssert.IsSuccess(
                    mmmType.Type.TypeAs<LayoutUDT>().WriteScope(ref row, ref valueScope, mmmType.TypeArgs, out RowCursor coordScope));
                TupleUnitTests.WriteCoord(ref row, ref coordScope, mmmType.TypeArgs, pc.Coord.Item2);
            }
        }

        private PerfCounter ReadCounter(ref RowBuffer row, ref RowCursor root)
        {
            PerfCounter pc = new PerfCounter();
            Assert.IsTrue(this.countersLayout.TryFind("name", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out string name));
            pc.Name = name;

            Assert.IsTrue(this.countersLayout.TryFind("value", out c));
            Assert.IsTrue(c.Type.Immutable);
            root.Clone(out RowCursor valueScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref valueScope, out valueScope) == Result.Success)
            {
                Assert.IsTrue(valueScope.Immutable);
                Assert.IsTrue(valueScope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref valueScope, out string units));
                Assert.IsTrue(valueScope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutInt64>().ReadSparse(ref row, ref valueScope, out long metric));
                pc.Value = (units, metric);
            }

            // ReSharper disable once StringLiteralTypo
            Assert.IsTrue(this.countersLayout.TryFind("minmeanmax", out c));
            Assert.IsTrue(c.Type.Immutable);
            root.Clone(out valueScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref valueScope, out valueScope) == Result.Success)
            {
                Assert.IsTrue(valueScope.Immutable);
                Assert.IsTrue(valueScope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref valueScope, out string units));

                Assert.IsTrue(valueScope.MoveNext(ref row));
                TypeArgument mmmType = c.TypeArgs[1];
                ResultAssert.IsSuccess(
                    mmmType.Type.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref valueScope, out RowCursor mmmScope));

                Assert.IsTrue(mmmScope.Immutable);
                Assert.IsTrue(mmmScope.MoveNext(ref row));
                ResultAssert.IsSuccess(mmmType.TypeArgs[0].Type.TypeAs<LayoutInt64>().ReadSparse(ref row, ref mmmScope, out long min));
                Assert.IsTrue(mmmScope.MoveNext(ref row));
                ResultAssert.IsSuccess(mmmType.TypeArgs[1].Type.TypeAs<LayoutInt64>().ReadSparse(ref row, ref mmmScope, out long mean));
                Assert.IsTrue(mmmScope.MoveNext(ref row));
                ResultAssert.IsSuccess(mmmType.TypeArgs[2].Type.TypeAs<LayoutInt64>().ReadSparse(ref row, ref mmmScope, out long max));

                pc.MinMaxValue = (units, (min, mean, max));
            }

            Assert.IsTrue(this.countersLayout.TryFind("coord", out c));
            Assert.IsTrue(c.Type.Immutable);
            root.Clone(out valueScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref valueScope, out valueScope) == Result.Success)
            {
                Assert.IsTrue(valueScope.Immutable);
                Assert.IsTrue(valueScope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref valueScope, out string units));

                Assert.IsTrue(valueScope.MoveNext(ref row));
                ResultAssert.IsSuccess(
                    c.TypeArgs[1].Type.TypeAs<LayoutUDT>().ReadScope(ref row, ref valueScope, out RowCursor coordScope));
                pc.Coord = (units, TupleUnitTests.ReadCoord(ref row, ref coordScope));
            }

            return pc;
        }

        private sealed class PerfCounterComparer : EqualityComparer<PerfCounter>
        {
            public static new readonly PerfCounterComparer Default = new PerfCounterComparer();

            public override bool Equals(PerfCounter x, PerfCounter y)
            {
                if (object.ReferenceEquals(x, y))
                {
                    return true;
                }
                if (object.ReferenceEquals(x, null))
                {
                    return false;
                }
                if (object.ReferenceEquals(y, null))
                {
                    return false;
                }
                if (x.GetType() != y.GetType())
                {
                    return false;
                }
                return string.Equals(x.Name, y.Name) &&
                       object.Equals(x.Value, y.Value) &&
                       object.Equals(x.MinMaxValue, y.MinMaxValue) &&
                       object.Equals(x.Coord.Item1, y.Coord.Item1) &&
                       CoordComparer.Default.Equals(x.Coord.Item2, y.Coord.Item2);
            }

            public override int GetHashCode(PerfCounter obj)
            {
                return HashCode.Combine(
                    obj.Name,
                    obj.Value,
                    obj.MinMaxValue,
                    obj.Coord.Item1,
                    CoordComparer.Default.GetHashCode(obj.Coord.Item2));
            }
        }

        private sealed class CoordComparer : EqualityComparer<Coord>
        {
            public static new readonly CoordComparer Default = new CoordComparer();

            public override bool Equals(Coord x, Coord y)
            {
                if (object.ReferenceEquals(x, y))
                {
                    return true;
                }
                if (object.ReferenceEquals(x, null))
                {
                    return false;
                }
                if (object.ReferenceEquals(y, null))
                {
                    return false;
                }
                if (x.GetType() != y.GetType())
                {
                    return false;
                }
                return x.Lat == y.Lat && x.Lng == y.Lng;
            }

            public override int GetHashCode(Coord obj)
            {
                return HashCode.Combine(obj.Lat, obj.Lng);
            }
        }
    }
}
