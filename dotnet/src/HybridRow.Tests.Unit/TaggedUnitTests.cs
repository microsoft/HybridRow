// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(TaggedUnitTests.SchemaFile, "TestData")]
    public sealed class TaggedUnitTests
    {
        private const string SchemaFile = @"TestData\TaggedApiSchema.json";
        private const int InitialRowSize = 2 * 1024 * 1024;

        private Namespace schema;
        private LayoutResolver resolver;
        private Layout layout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(TaggedUnitTests.SchemaFile);
            this.schema = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.schema);
            this.layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "TaggedApi").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateTaggedApi()
        {
            RowBuffer row = new RowBuffer(TaggedUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            TaggedApi c1 = new TaggedApi()
            {
                Tag1 = ((byte)1, "hello"),
                Tag2 = ((byte)2, 28, 1974L),
            };

            this.WriteTaggedApi(ref row, ref RowCursor.Create(ref row, out RowCursor _), c1);
            TaggedApi c2 = this.ReadTaggedApi(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.AreEqual(c1, c2);
        }

        private void WriteTaggedApi(ref RowBuffer row, ref RowCursor root, TaggedApi pc)
        {
            Assert.IsTrue(this.layout.TryFind("tag1", out LayoutColumn c));
            root.Clone(out RowCursor tag1Scope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref tag1Scope, c.TypeArgs, out tag1Scope));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUInt8>().WriteSparse(ref row, ref tag1Scope, pc.Tag1.Item1));
            Assert.IsTrue(tag1Scope.MoveNext(ref row));
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tag1Scope, pc.Tag1.Item2));

            Assert.IsTrue(this.layout.TryFind("tag2", out c));
            root.Clone(out RowCursor tag2Scope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref tag2Scope, c.TypeArgs, out tag2Scope));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUInt8>().WriteSparse(ref row, ref tag2Scope, pc.Tag2.Item1));
            Assert.IsTrue(tag2Scope.MoveNext(ref row));
            ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutInt32>().WriteSparse(ref row, ref tag2Scope, pc.Tag2.Item2));
            Assert.IsTrue(tag2Scope.MoveNext(ref row));
            ResultAssert.IsSuccess(c.TypeArgs[2].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref tag2Scope, pc.Tag2.Item3));
        }

        private TaggedApi ReadTaggedApi(ref RowBuffer row, ref RowCursor root)
        {
            TaggedApi pc = new TaggedApi();

            Assert.IsTrue(this.layout.TryFind("tag1", out LayoutColumn c));
            Assert.IsTrue(c.Type.Immutable);
            root.Clone(out RowCursor tag1Scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref tag1Scope, out tag1Scope) == Result.Success)
            {
                Assert.IsTrue(tag1Scope.Immutable);
                Assert.IsTrue(tag1Scope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUInt8>().ReadSparse(ref row, ref tag1Scope, out byte apiCode));
                Assert.IsTrue(tag1Scope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref tag1Scope, out string str));
                pc.Tag1 = (apiCode, str);
            }

            Assert.IsTrue(this.layout.TryFind("tag2", out c));
            Assert.IsFalse(c.Type.Immutable);
            root.Clone(out RowCursor tag2Scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref tag2Scope, out tag2Scope) == Result.Success)
            {
                Assert.IsFalse(tag2Scope.Immutable);
                Assert.IsTrue(tag2Scope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUInt8>().ReadSparse(ref row, ref tag2Scope, out byte apiCode));
                Assert.IsTrue(tag2Scope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[1].Type.TypeAs<LayoutInt32>().ReadSparse(ref row, ref tag2Scope, out int val1));
                Assert.IsTrue(tag2Scope.MoveNext(ref row));
                ResultAssert.IsSuccess(c.TypeArgs[2].Type.TypeAs<LayoutInt64>().ReadSparse(ref row, ref tag2Scope, out long val2));
                pc.Tag2 = (apiCode, val1, val2);
            }

            return pc;
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class TaggedApi
        {
            public (byte, string) Tag1;
            public (byte, int, long) Tag2;

            // ReSharper disable once MemberCanBePrivate.Local
            public bool Equals(TaggedApi other)
            {
                return object.Equals(this.Tag1, other.Tag1) &&
                       object.Equals(this.Tag2, other.Tag2);
            }

            public override bool Equals(object obj)
            {
                if (object.ReferenceEquals(null, obj))
                {
                    return false;
                }

                if (object.ReferenceEquals(this, obj))
                {
                    return true;
                }

                return obj is TaggedApi taggedApi && this.Equals(taggedApi);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    int hashCode = 0;
                    hashCode = (hashCode * 397) ^ this.Tag1.GetHashCode();
                    hashCode = (hashCode * 397) ^ this.Tag2.GetHashCode();
                    return hashCode;
                }
            }
        }
    }
}
