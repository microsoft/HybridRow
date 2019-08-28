//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public final class TypeArgumentList
{
    public static final TypeArgumentList EMPTY = new TypeArgumentList();

    private final TypeArgument[] args;
    private final SchemaId schemaId;

    private TypeArgumentList() {
        this.args = new TypeArgument[] {};
        this.schemaId = SchemaId.NONE;
    }

    public TypeArgumentList(TypeArgument[] args) {
        checkArgument(args != null);
        this.args = args;
        this.schemaId = SchemaId.INVALID;
    }

    /**
     * Initializes a new instance of the {@link TypeArgumentList} struct.
     *
     * @param schemaId For UDT fields, the schema id of the nested layout.
     */
    public TypeArgumentList(SchemaId schemaId, TypeArgument...args) {
        this.args = args.length == 0 ? EMPTY.args : args;
        this.schemaId = schemaId;
    }

    public int count() {
        return this.args.length;
    }

    /**
     * For UDT fields, the schema id of the nested layout.
     */
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * Gets an enumerator for this span.
     */
    public Enumerator GetEnumerator() {
        return new Enumerator(this.args);
    }

    public boolean equals(TypeArgumentList other) {
        if (null == other) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return this.schemaId().equals(other.schemaId()) && Arrays.equals(this.args, other.args);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TypeArgumentList && this.equals((TypeArgumentList) other);
    }

    public TypeArgument get(int i) {
        return this.args[i].clone();
    }

    @Override
    public int hashCode() {

        int hash = 19;
        hash = (hash * 397) ^ this.schemaId().hashCode();

        for (TypeArgument a : this.args) {
            hash = (hash * 397) ^ a.hashCode();
        }

        return hash;
    }

    public static boolean opEquals(TypeArgumentList left, TypeArgumentList right) {
        return left.equals(right);
    }

    public static boolean opNotEquals(TypeArgumentList left, TypeArgumentList right) {
        return !left.equals(right);
    }

    @Override
    public String toString() {

        if (this.schemaId.equals(SchemaId.INVALID)) {
            return String.format("<%1$s>", this.schemaId().toString());
        }

        if (this.args == null || this.args == null ? null : this.args.length == 0) {
            return "";
        }

        return String.format("<%1$s>", tangible.StringHelper.join(", ", this.args));
    }

    /**
     * Enumerates the elements of a {@link TypeArgumentList}.
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
         * Initializes a new instance of the {@link Enumerator} struct.
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