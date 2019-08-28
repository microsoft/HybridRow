//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.perf;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.ISpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.google.common.base.Strings.lenientFormat;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public ref struct JsonModelRowGenerator
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class JsonModelRowGenerator {
    private RowBuffer row = new RowBuffer();


    public JsonModelRowGenerator(int capacity, Layout layout, LayoutResolver resolver) {
        this(capacity, layout, resolver, null);
    }

    public JsonModelRowGenerator() {
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public JsonModelRowGenerator(int capacity, Layout layout, LayoutResolver resolver,
    // ISpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public JsonModelRowGenerator(int capacity, Layout layout, LayoutResolver resolver, ISpanResizer<Byte> resizer) {
        this.row = new RowBuffer(capacity, resizer);
        this.row.InitLayout(HybridRowVersion.V1, layout, resolver);
    }

    public int getLength() {
        return this.row.length();
    }

    public RowReader GetReader() {
        Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.row)
        this.row = tempReference_row.get();
        return tempVar;
    }

    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public boolean ReadFrom(InputStream stream, int length) {
        return this.row.ReadFrom(stream, length, HybridRowVersion.V1, this.row.resolver());
    }

    public void Reset() {
        Layout layout = this.row.resolver().Resolve(this.row.header().getSchemaId().clone());
        this.row.InitLayout(HybridRowVersion.V1, layout, this.row.resolver());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.row.ToArray();
    }

    public Result WriteBuffer(HashMap<Utf8String, Object> value) {
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(this.row);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        Result tempVar = RowWriter.WriteBuffer(tempReference_row, value, (ref RowWriter writer, TypeArgument typeArg,
                                                                          HashMap<Utf8String, Object> dict) ->
        {
            for ((Utf8String propPath,Object propValue) :dict)
            {
                Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempReference_writer =
                    new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer);
                Result result = JsonModelRowGenerator.JsonModelSwitch(tempReference_writer, propPath, propValue);
                writer = tempReference_writer.get();
                return result;
            }

            return Result.Success;
        });
        this.row = tempReference_row.get();
        return tempVar;
    }

    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public void WriteTo(OutputStream stream) {
        this.row.WriteTo(stream);
    }

    public JsonModelRowGenerator clone() {
        JsonModelRowGenerator varCopy = new JsonModelRowGenerator();
        varCopy.row = this.row.clone();
        return varCopy;
    }

    private static Result JsonModelSwitch(Reference<RowWriter> writer, Utf8String path, Object value) {
        switch (value) {
            case null:
                return writer.get().WriteNull(path);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case bool x:
            case
                boolean x:
                return writer.get().WriteBool(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case long x:
            case
                long x:
                return writer.get().WriteInt64(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case double x:
            case
                double x:
                return writer.get().WriteFloat64(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case string x:
            case String
                x:
                return writer.get().WriteString(path, x);
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case Utf8String x:
            case Utf8String
                x:
                return writer.get().WriteString(path, x.Span);
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case byte[] x:
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            case
                byte[] x:
                return writer.get().WriteBinary(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case ReadOnlyMemory<byte> x:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            case ReadOnlyMemory < Byte > x:
                return writer.get().WriteBinary(path, x.Span);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case Dictionary<Utf8String, object> x:
            case HashMap < Utf8String, Object > x:
                // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these
                // are not converted by C# to Java Converter:
                return writer.get().WriteScope(path, new TypeArgument(LayoutType.Object), x,
                    (ref RowWriter writer2, TypeArgument typeArg, HashMap<Utf8String, Object> dict) ->
                {
                    for ((Utf8String propPath,Object propValue) :dict)
                    {
                        Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempReference_writer2 = new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer2);
                        Result result = JsonModelRowGenerator.JsonModelSwitch(tempReference_writer2, propPath, propValue);
                        writer2 = tempReference_writer2.get();
                        return result;
                    }

                    return Result.Success;
                });
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case List<object> x:
            case ArrayList < Object > x:
                // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                return writer.get().WriteScope(path, new TypeArgument(LayoutType.Array), x, (ref RowWriter writer2, TypeArgument typeArg, ArrayList<Object> list) ->
                {
                    for (Object elm : list) {
                        Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempReference_writer2 = new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer2);
                        Result result = JsonModelRowGenerator.JsonModelSwitch(tempReference_writer2, null, elm);
                        writer2 = tempReference_writer2.get();
                        if (result != Result.Success) {
                            return result;
                        }
                    }

                    return Result.Success;
                });
            default:
                throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", value.getClass().getSimpleName()));
                return Result.Failure;
        }
    }
}