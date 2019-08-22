//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow;

public class DefaultSpanResizer<T> implements ISpanResizer<T> {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly DefaultSpanResizer<T> Default = new
    // DefaultSpanResizer<T>();
    public static final DefaultSpanResizer<T> Default = new DefaultSpanResizer<T>();

    private DefaultSpanResizer() {
    }

    /**
     * <inheritdoc />
     */

    public final Span<T> Resize(int minimumLength) {
        return Resize(minimumLength, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Span<T> Resize(int minimumLength, Span<T> buffer = default)
    public final Span<T> Resize(int minimumLength, Span<T> buffer) {
        //C# TO JAVA CONVERTER WARNING: Java does not allow direct instantiation of arrays of generic type parameters:
        //ORIGINAL LINE: Span<T> next = new Memory<T>(new T[Math.Max(minimumLength, buffer.Length)]).Span;
        Span<T> next = (new Memory<T>((T[])new Object[Math.max(minimumLength, buffer.Length)])).Span;
        if (!buffer.IsEmpty && next.Slice(0, buffer.Length) != buffer) {
            buffer.CopyTo(next);
        }

        return next;
    }
}