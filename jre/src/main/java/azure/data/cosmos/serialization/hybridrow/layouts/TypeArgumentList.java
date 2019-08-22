//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.SchemaId;

import static com.google.common.base.Preconditions.checkArgument;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1034 // Nested types should not be visible


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{this.args == null ? null : ToString()}")] public readonly struct TypeArgumentList
// : IEquatable<TypeArgumentList>
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [DebuggerDisplay("{this.args == null ? null : ToString()}")] public readonly struct TypeArgumentList
// : IEquatable<TypeArgumentList>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class TypeArgumentList implements IEquatable<TypeArgumentList> {
    public static final TypeArgumentList Empty = new TypeArgumentList(Array.<TypeArgument>Empty());

    private TypeArgument[] args;

    /**
     * For UDT fields, the schema id of the nested layout.
     */
    private SchemaId schemaId = new SchemaId();

    public TypeArgumentList() {
    }

    public TypeArgumentList(TypeArgument[] args) {
        checkArgument(args != null);

        this.args = args;
        this.schemaId = getSchemaId().Invalid;
    }

    /**
     * Initializes a new instance of the <see cref="TypeArgumentList" /> struct.
     *
     * @param schemaId For UDT fields, the schema id of the nested layout.
     */
    public TypeArgumentList(SchemaId schemaId) {
        this.args = Array.<TypeArgument>Empty();
        this.schemaId = schemaId.clone();
    }

    public int getCount() {
        return this.args.length;
    }

    /**
     * For UDT fields, the schema id of the nested layout.
     */
    public SchemaId getSchemaId() {
        return this.schemaId.clone();
    }

    /**
     * Gets an enumerator for this span.
     */
    public Enumerator GetEnumerator() {
        return new Enumerator(this.args);
    }

    public TypeArgumentList clone() {
        TypeArgumentList varCopy = new TypeArgumentList();

        varCopy.args = this.args.clone();
        varCopy.schemaId = this.schemaId.clone();

        return varCopy;
    }

    public boolean equals(TypeArgumentList other) {
        //C# TO JAVA CONVERTER WARNING: Java Arrays.equals is not always identical to LINQ 'SequenceEqual':
        //ORIGINAL LINE: return (this.schemaId == other.schemaId) && this.args.SequenceEqual(other.args);
        return (azure.data.cosmos.serialization.hybridrow.SchemaId.opEquals(this.schemaId.clone(),
            other.schemaId.clone())) && Arrays.equals(this.args, other.args);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        boolean tempVar = obj instanceof TypeArgumentList;
        TypeArgumentList ota = tempVar ? (TypeArgumentList)obj : null;
        if (tempVar) {
            return this.equals(ota);
        }

        return false;
    }

    public TypeArgument get(int i) {
        return this.args[i].clone();
    }

    @Override
    public int hashCode() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            int hash = 19;
            hash = (hash * 397) ^ this.schemaId.hashCode();
            for (TypeArgument a : this.args) {
                hash = (hash * 397) ^ a.hashCode();
            }

            return hash;
        }
    }

    public static boolean opEquals(TypeArgumentList left, TypeArgumentList right) {
        return left.equals(right.clone());
    }

    public static boolean opNotEquals(TypeArgumentList left, TypeArgumentList right) {
        return !left.equals(right.clone());
    }

    @Override
    public String toString() {
        if (azure.data.cosmos.serialization.hybridrow.SchemaId.opNotEquals(this.schemaId.clone(),
            getSchemaId().Invalid)) {
            return String.format("<%1$s>", this.schemaId.toString());
        }

        if (this.args == null || this.args == null ? null : this.args.length == 0) {
            return "";
        }

        return String.format("<%1$s>", tangible.StringHelper.join(", ", this.args));
    }

    /**
     * Enumerates the elements of a <see cref="TypeArgumentList" />.
     */
    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Enumerator
    public final static class Enumerator {
        /**
         * The next index to yield.
         */
        private int index;
        /**
         * The list being enumerated.
         */
        private TypeArgument[] list;

        /**
         * Initializes a new instance of the <see cref="Enumerator" /> struct.
         *
         * @param list The list to enumerate.
         */
        // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
        //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal Enumerator(TypeArgument[] list)
        public Enumerator() {
        }

        public Enumerator(TypeArgument[] list) {
            this.list = list;
            this.index = -1;
        }

        /**
         * Advances the enumerator to the next element of the span.
         */
        // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
        //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public bool MoveNext()
        public boolean MoveNext() {
            int i = this.index + 1;
            if (i < this.list.length) {
                this.index = i;
                return true;
            }

            return false;
        }

        /**
         * Gets the element at the current position of the enumerator.
         */
        // TODO: C# TO JAVA CONVERTER: 'ref return' methods are not converted by C# to Java Converter:
        //		public ref readonly TypeArgument Current
        //			{
        //				[MethodImpl(MethodImplOptions.AggressiveInlining)] get => ref this.list[this.index];
        //			}
        public Enumerator clone() {
            Enumerator varCopy = new Enumerator();

            varCopy.list = this.list.clone();
            varCopy.index = this.index;

            return varCopy;
        }
    }
}