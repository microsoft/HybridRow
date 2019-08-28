//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TypeArgument {

    public static final TypeArgument NONE = new TypeArgument();
    private final LayoutType type;
    private final TypeArgumentList typeArgs;

    private TypeArgument() {
        this.type = null;
        this.typeArgs = null;
    }

    /**
     * Initializes a new instance of the {@link TypeArgument} struct.
     *
     * @param type The type of the constraint.
     */
    public TypeArgument(LayoutType type) {
        checkNotNull(type);
        this.type = type;
        this.typeArgs = TypeArgumentList.EMPTY;
    }

    /**
     * Initializes a new instance of the {@link TypeArgument} struct.
     *
     * @param type     The type of the constraint.
     * @param typeArgs For generic types the type parameters.
     */
    public TypeArgument(LayoutType type, TypeArgumentList typeArgs) {
        checkNotNull(type);
        this.type = type;
        this.typeArgs = typeArgs;
    }

    @Override
    public boolean equals(Object other) {

        if (null == other) {
            return false;
        }

        return other instanceof TypeArgument && this.equals((TypeArgument) other);
    }

    @Override
    public int hashCode() {
        return (this.type.hashCode() * 397) ^ this.typeArgs.hashCode();
    }

    public boolean equals(TypeArgument other) {
        return this.type.equals(other.type) && this.typeArgs.equals(other.typeArgs);
    }

    @Override
    public String toString() {
        if (this.type == null) {
            return "";
        }
        return this.type.name() + this.typeArgs.toString();
    }

    /**
     * The physical layout type.
     */
    public LayoutType type() {
        return this.type;
    }

    /**
     * If the type argument is itself generic, then its type arguments.
     */
    public TypeArgumentList typeArgs() {
        return this.typeArgs;
    }
}