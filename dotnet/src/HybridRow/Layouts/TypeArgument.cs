// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Diagnostics;
    using Microsoft.Azure.Cosmos.Core;

    [DebuggerDisplay("{this.type == null ? null : ToString()}")]
    public readonly struct TypeArgument : IEquatable<TypeArgument>
    {
        private readonly LayoutType type;
        private readonly TypeArgumentList typeArgs;

        /// <summary>Initializes a new instance of the <see cref="TypeArgument" /> struct.</summary>
        /// <param name="type">The type of the constraint.</param>
        public TypeArgument(LayoutType type)
        {
            Contract.Requires(type != null);

            this.type = type;
            this.typeArgs = TypeArgumentList.Empty;
        }

        /// <summary>Initializes a new instance of the <see cref="TypeArgument" /> struct.</summary>
        /// <param name="type">The type of the constraint.</param>
        /// <param name="typeArgs">For generic types the type parameters.</param>
        public TypeArgument(LayoutType type, TypeArgumentList typeArgs)
        {
            Contract.Requires(type != null);

            this.type = type;
            this.typeArgs = typeArgs;
        }

        /// <summary>The physical layout type.</summary>
        public LayoutType Type => this.type;

        /// <summary>If the type argument is itself generic, then its type arguments.</summary>
        public TypeArgumentList TypeArgs => this.typeArgs;

        public static bool operator ==(TypeArgument left, TypeArgument right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(TypeArgument left, TypeArgument right)
        {
            return !left.Equals(right);
        }

        /// <summary>The physical layout type of the field cast to the specified type.</summary>
        [DebuggerHidden]
        public T TypeAs<T>()
            where T : ILayoutType
        {
            return this.type.TypeAs<T>();
        }

        public override string ToString()
        {
            if (this.type == null)
            {
                return string.Empty;
            }

            return this.type.Name + this.typeArgs.ToString();
        }

        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(null, obj))
            {
                return false;
            }

            if (obj is TypeArgument ota)
            {
                return this.Equals(ota);
            }

            return false;
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return (this.type.GetHashCode() * 397) ^ this.typeArgs.GetHashCode();
            }
        }

        public bool Equals(TypeArgument other)
        {
            return this.type.Equals(other.type) && this.typeArgs.Equals(other.typeArgs);
        }
    }
}
