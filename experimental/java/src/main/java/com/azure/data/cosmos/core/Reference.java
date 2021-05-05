// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.core;

import java.util.Objects;

/**
 * A container object which may or may not contain a non-null value.
 *
 * This is a value-based class and as such use of identity-sensitive operations--including reference equality
 * ({@code ==}), identity hash code, or synchronization--on instances of {@link Reference} may have unpredictable
 * results and should be avoided.
 *
 * @param <T> type of the referent.
 */
public final class Reference<T> {

    private volatile T value;

    public Reference(T value) {
        this.setAndGet(value);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }
    public T setAndGet(T value) {
        return this.value = value;
    }

    /**
     * {@code true} if there is a value present, otherwise {@code false}.
     *
     * This is equivalent to evaluating the expression {@code ref.get() == null}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return this.value != null;
    }

    /**
     * Indicates whether some other object is equal to this {@link Reference} value. 
     * <p>
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@link Reference} and;
     * <li>both instances have no value present or;
     * <li>the present values are equal to each other as determined by {@code T.equals(Object)}}.
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

        if (other.getClass() != Reference.class) {
            return false;
        }

        return Objects.equals(this.value, ((Reference)other).value);
    }


    @Override
    public String toString() {
        return this.value == null ? "null" : this.value.toString();
    }
}