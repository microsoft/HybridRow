// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import static com.google.common.base.Preconditions.checkArgument;

public final class MemorySpanResizer<T> implements ISpanResizer<T> {
    private Memory<T> memory;


    public MemorySpanResizer() {
        this(0);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public MemorySpanResizer(int initialCapacity = 0)
    public MemorySpanResizer(int initialCapacity) {
        checkArgument(initialCapacity >= 0);

        //C# TO JAVA CONVERTER WARNING: Java does not allow direct instantiation of arrays of generic type parameters:
        //ORIGINAL LINE: this.memory = initialCapacity == 0 ? default : new Memory<T>(new T[initialCapacity]);
        this.memory = initialCapacity == 0 ? null : new Memory<T>((T[])new Object[initialCapacity]);
    }

    public Memory<T> getMemory() {
        return this.memory;
    }

    /**
     * <inheritdoc />
     */

    public Span<T> Resize(int minimumLength) {
        return Resize(minimumLength, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Span<T> Resize(int minimumLength, Span<T> buffer = default)
    public Span<T> Resize(int minimumLength, Span<T> buffer) {
        if (this.memory.Length < minimumLength) {
            //C# TO JAVA CONVERTER WARNING: Java does not allow direct instantiation of arrays of generic type
            // parameters:
            //ORIGINAL LINE: this.memory = new Memory<T>(new T[Math.Max(minimumLength, buffer.Length)]);
            this.memory = new Memory<T>((T[])new Object[Math.max(minimumLength, buffer.Length)]);
        }

        Span<T> next = this.memory.Span;
        if (!buffer.IsEmpty && next.Slice(0, buffer.Length) != buffer) {
            buffer.CopyTo(next);
        }

        return next;
    }
}