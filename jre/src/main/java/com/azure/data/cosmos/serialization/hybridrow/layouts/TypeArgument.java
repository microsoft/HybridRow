//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import static com.google.common.base.Preconditions.checkArgument;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{this.type == null ? null : ToString()}")] public readonly struct TypeArgument :
// IEquatable<TypeArgument>
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [DebuggerDisplay("{this.type == null ? null : ToString()}")] public readonly struct TypeArgument :
// IEquatable<TypeArgument>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class TypeArgument implements IEquatable<TypeArgument> {
    private LayoutType type;
    private TypeArgumentList typeArgs = new TypeArgumentList();

    /**
     * Initializes a new instance of the <see cref="TypeArgument" /> struct.
     *
     * @param type The type of the constraint.
     */
    public TypeArgument() {
    }

    public TypeArgument(LayoutType type) {
        checkArgument(type != null);

        this.type = type;
        this.typeArgs = TypeArgumentList.Empty;
    }

    /**
     * Initializes a new instance of the <see cref="TypeArgument" /> struct.
     *
     * @param type     The type of the constraint.
     * @param typeArgs For generic types the type parameters.
     */
    public TypeArgument(LayoutType type, TypeArgumentList typeArgs) {
        checkArgument(type != null);

        this.type = type;
        this.typeArgs = typeArgs.clone();
    }

    /**
     * The physical layout type.
     */
    public LayoutType getType() {
        return this.type;
    }

    /**
     * If the type argument is itself generic, then its type arguments.
     */
    public TypeArgumentList getTypeArgs() {
        return this.typeArgs.clone();
    }

    /**
     * The physical layout type of the field cast to the specified type.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerHidden] public T TypeAs<T>() where T : ILayoutType
    public <T extends ILayoutType> T TypeAs() {
        return this.type.TypeAs();
    }

    public TypeArgument clone() {
        TypeArgument varCopy = new TypeArgument();

        varCopy.type = this.type;
        varCopy.typeArgs = this.typeArgs.clone();

        return varCopy;
    }

    public boolean equals(TypeArgument other) {
        return this.type.equals(other.type) && this.typeArgs.equals(other.typeArgs.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        boolean tempVar = obj instanceof TypeArgument;
        TypeArgument ota = tempVar ? (TypeArgument)obj : null;
        if (tempVar) {
            return this.equals(ota);
        }

        return false;
    }

    @Override
    public int hashCode() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            return (this.type.hashCode() * 397) ^ this.typeArgs.hashCode();
        }
    }

    public static boolean opEquals(TypeArgument left, TypeArgument right) {
        return left.equals(right.clone());
    }

    public static boolean opNotEquals(TypeArgument left, TypeArgument right) {
        return !left.equals(right.clone());
    }

    @Override
    public String toString() {
        if (this.type == null) {
            return "";
        }

        return this.type.getName() + this.typeArgs.toString();
    }
}