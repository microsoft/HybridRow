// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public ref struct VisitRowGenerator
    {
        private readonly LayoutResolver resolver;
        private RowBuffer row;

        public VisitRowGenerator(Span<byte> span, LayoutResolver resolver)
        {
            this.resolver = resolver;
            this.row = new RowBuffer(span, HybridRowVersion.V1, this.resolver);
        }

        public int Length => this.row.Length;

        public Result DispatchLayout(Layout layout)
        {
            RowCursor scope = RowCursor.Create(ref this.row);
            return this.DispatchLayout(ref scope, layout);
        }

        private Result LayoutCodeSwitch(
            ref RowCursor scope,
            LayoutColumn col = default,
            TypeArgument typeArg = default)
        {
            if (col != null)
            {
                typeArg = col.TypeArg;
            }

            switch (typeArg.Type.LayoutCode)
            {
                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                    return this.DispatchObject(ref scope, typeArg);

                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                    return this.DispatchArray(ref scope, typeArg);

                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                    return this.DispatchSet(ref scope, typeArg);

                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                    return this.DispatchMap(ref scope, typeArg);

                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                    return this.DispatchTuple(ref scope, typeArg);

                case LayoutCode.NullableScope:
                case LayoutCode.ImmutableNullableScope:
                    return this.DispatchNullable(ref scope, typeArg);

                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                    return this.DispatchUDT(ref scope, typeArg);

                default:
                    return Result.Success;
            }
        }

        private Result DispatchObject(ref RowCursor scope, TypeArgument t)
        {
            Result r = t.TypeAs<LayoutObject>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            // TODO: support properties in an object scope.
            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchArray(ref RowCursor scope, TypeArgument t)
        {
            Contract.Assert(t.TypeArgs.Count == 1);

            Result r = t.TypeAs<LayoutTypedArray>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            while (childScope.MoveNext(ref this.row))
            {
                r = this.LayoutCodeSwitch(ref childScope, null, t.TypeArgs[0]);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchTuple(ref RowCursor scope, TypeArgument t)
        {
            Contract.Assert(t.TypeArgs.Count >= 2);
            Result r = t.TypeAs<LayoutIndexedScope>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            while (childScope.MoveNext(ref this.row))
            {
                r = this.LayoutCodeSwitch(ref childScope, null, t.TypeArgs[childScope.Index]);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchNullable(ref RowCursor scope, TypeArgument t)
        {
            Contract.Assert(t.TypeArgs.Count == 1);

            Result r = t.TypeAs<LayoutNullable>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            if (childScope.MoveNext(ref this.row))
            {
                r = this.LayoutCodeSwitch(ref childScope, null, t.TypeArgs[0]);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchSet(ref RowCursor scope, TypeArgument t)
        {
            Contract.Assert(t.TypeArgs.Count == 1);

            Result r = t.TypeAs<LayoutTypedSet>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            while (childScope.MoveNext(ref this.row))
            {
                r = this.LayoutCodeSwitch(ref childScope, null, t.TypeArgs[0]);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchMap(ref RowCursor scope, TypeArgument t)
        {
            Contract.Assert(t.TypeArgs.Count == 2);

            Result r = t.TypeAs<LayoutUniqueScope>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            TypeArgument fieldType = t.TypeAs<LayoutUniqueScope>().FieldType(ref childScope);
            while (childScope.MoveNext(ref this.row))
            {
                r = this.LayoutCodeSwitch(ref childScope, null, fieldType);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchUDT(ref RowCursor scope, TypeArgument t)
        {
            Result r = t.TypeAs<LayoutUDT>().ReadScope(ref this.row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            Layout layout = this.resolver.Resolve(t.TypeArgs.SchemaId);
            r = this.DispatchLayout(ref childScope, layout);
            if (r != Result.Success)
            {
                return r;
            }

            scope.Skip(ref this.row, ref childScope);
            return Result.Success;
        }

        private Result DispatchLayout(ref RowCursor scope, Layout layout)
        {
            // Process schematized segment.
            foreach (LayoutColumn c in layout.Columns)
            {
                if (c.Storage == StorageKind.Sparse)
                {
                    break;
                }

                Result r = this.LayoutCodeSwitch(ref scope, c);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            // Process sparse segment.
            while (scope.MoveNext(ref this.row))
            {
                Result r = this.LayoutCodeSwitch(ref scope, null, scope.TypeArg);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }
    }
}
