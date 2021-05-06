// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1034 // Nested types should not be visible

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;

    [DebuggerDisplay("{this.args == null ? null : ToString()}")]
    public readonly struct TypeArgumentList : IEquatable<TypeArgumentList>
    {
        public static readonly TypeArgumentList Empty = new TypeArgumentList(Array.Empty<TypeArgument>());

        private readonly TypeArgument[] args;

        /// <summary>For UDT fields, the schema id of the nested layout.</summary>
        private readonly SchemaId schemaId;

        public TypeArgumentList(TypeArgument[] args)
        {
            Contract.Requires(args != null);

            this.args = args;
            this.schemaId = SchemaId.Invalid;
        }

        /// <summary>Initializes a new instance of the <see cref="TypeArgumentList" /> struct.</summary>
        /// <param name="schemaId">For UDT fields, the schema id of the nested layout.</param>
        public TypeArgumentList(SchemaId schemaId)
        {
            this.args = Array.Empty<TypeArgument>();
            this.schemaId = schemaId;
        }

        public int Count => this.args.Length;

        /// <summary>For UDT fields, the schema id of the nested layout.</summary>
        public SchemaId SchemaId => this.schemaId;

        public TypeArgument this[int i] => this.args[i];

        [SuppressMessage("Usage", "CA2225:Operator overloads have named alternates", Justification = "Constructor")]
        public static implicit operator TypeArgumentList(SchemaId schemaId)
        {
            return new TypeArgumentList(schemaId);
        }

        public static bool operator ==(TypeArgumentList left, TypeArgumentList right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(TypeArgumentList left, TypeArgumentList right)
        {
            return !left.Equals(right);
        }

        /// <summary>Gets an enumerator for this span.</summary>
        public Enumerator GetEnumerator()
        {
            return new Enumerator(this.args);
        }

        public override string ToString()
        {
            if (this.schemaId != SchemaId.Invalid)
            {
                return $"<{this.schemaId}>";
            }

            if (this.args == null || this.args?.Length == 0)
            {
                return string.Empty;
            }

            return $"<{string.Join(", ", this.args)}>";
        }

        public override bool Equals(object obj)
        {
            if (obj is TypeArgumentList ota)
            {
                return this.Equals(ota);
            }

            return false;
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = 19;
                hash = (hash * 397) ^ this.schemaId.GetHashCode();
                foreach (TypeArgument a in this.args)
                {
                    hash = (hash * 397) ^ a.GetHashCode();
                }

                return hash;
            }
        }

        public bool Equals(TypeArgumentList other)
        {
            return (this.schemaId == other.schemaId) && this.args.SequenceEqual(other.args);
        }

        /// <summary>Enumerates the elements of a <see cref="TypeArgumentList" />.</summary>
        public struct Enumerator
        {
            /// <summary>The list being enumerated.</summary>
            private readonly TypeArgument[] list;

            /// <summary>The next index to yield.</summary>
            private int index;

            /// <summary>Initializes a new instance of the <see cref="Enumerator" /> struct.</summary>
            /// <param name="list">The list to enumerate.</param>
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            internal Enumerator(TypeArgument[] list)
            {
                this.list = list;
                this.index = -1;
            }

            /// <summary>Advances the enumerator to the next element of the span.</summary>
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            public bool MoveNext()
            {
                int i = this.index + 1;
                if (i < this.list.Length)
                {
                    this.index = i;
                    return true;
                }

                return false;
            }

            /// <summary>Gets the element at the current position of the enumerator.</summary>
            public ref readonly TypeArgument Current
            {
                [MethodImpl(MethodImplOptions.AggressiveInlining)]
                get => ref this.list[this.index];
            }
        }
    }
}
