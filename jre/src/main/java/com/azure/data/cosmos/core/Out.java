//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.core;

import java.util.Objects;

/**
 * A container object which may or may not contain a non-null value
 *
 * This is a value-based class and as such use of identity-sensitive operations--including reference equality
 * ({@code ==}), identity hash code, or synchronization--on instances of {@Out} may have unpredictable results and
 * should be avoided.
 *
 * @param <T>
 */
public final class Out<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    /**
     * {@code true} if there is a value present, otherwise {@code false}
     * <p>
     * This is equivalent to evaluating the expression {@code out.get() == null}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Indicates whether some other object is equal to this {@link Out} value. The other object is considered equal if:
     * <ul>
     * <li>it is also an {@link Out} and;
     * <li>both instances have no value present or;
     * <li>the present values are equal to each other as determined by {@link T#equals(Object)}}.
     * </ul>
     *
     * @param other an object to be tested for equality
     * @return {code true} if the other object is equal to this object; otherwise {@code false}
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof Out)) {
            return false;
        }

        return Objects.equals(value, ((Out)other).value);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}
