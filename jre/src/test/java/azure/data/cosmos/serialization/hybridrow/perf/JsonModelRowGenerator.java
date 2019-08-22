//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.ISpanResizer;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;

import java.util.ArrayList;
import java.util.HashMap;

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
        return this.row.getLength();
    }

    public RowReader GetReader() {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.row)
        this.row = tempRef_row.argValue;
        return tempVar;
    }

    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public boolean ReadFrom(Stream stream, int length) {
        return this.row.ReadFrom(stream, length, HybridRowVersion.V1, this.row.getResolver());
    }

    public void Reset() {
        Layout layout = this.row.getResolver().Resolve(this.row.getHeader().getSchemaId().clone());
        this.row.InitLayout(HybridRowVersion.V1, layout, this.row.getResolver());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.row.ToArray();
    }

    public Result WriteBuffer(HashMap<Utf8String, Object> value) {
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(this.row);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        Result tempVar = RowWriter.WriteBuffer(tempRef_row, value, (ref RowWriter writer, TypeArgument typeArg,
                                                                    HashMap<Utf8String, Object> dict) ->
        {
            for ((Utf8String propPath,Object propValue) :dict)
            {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempRef_writer =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer);
                Result result = JsonModelRowGenerator.JsonModelSwitch(tempRef_writer, propPath, propValue);
                writer = tempRef_writer.argValue;
                return result;
            }

            return Result.Success;
        });
        this.row = tempRef_row.argValue;
        return tempVar;
    }

    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public void WriteTo(Stream stream) {
        this.row.WriteTo(stream);
    }

    public JsonModelRowGenerator clone() {
        JsonModelRowGenerator varCopy = new JsonModelRowGenerator();

        varCopy.row = this.row.clone();

        return varCopy;
    }

    private static Result JsonModelSwitch(tangible.RefObject<RowWriter> writer, Utf8String path, Object value) {
        switch (value) {
            case null:
                return writer.argValue.WriteNull(path);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case bool x:
            case
                boolean x:
                return writer.argValue.WriteBool(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case long x:
            case
                long x:
                return writer.argValue.WriteInt64(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case double x:
            case
                double x:
                return writer.argValue.WriteFloat64(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case string x:
            case String
                x:
                return writer.argValue.WriteString(path, x);
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case Utf8String x:
            case Utf8String
                x:
                return writer.argValue.WriteString(path, x.Span);
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case byte[] x:
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            case
                byte[] x:
                return writer.argValue.WriteBinary(path, x);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case ReadOnlyMemory<byte> x:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            case ReadOnlyMemory < Byte > x:
                return writer.argValue.WriteBinary(path, x.Span);
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case Dictionary<Utf8String, object> x:
            case HashMap < Utf8String, Object > x:
                // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these
                // are not converted by C# to Java Converter:
                return writer.argValue.WriteScope(path, new TypeArgument(LayoutType.Object), x,
                    (ref RowWriter writer2, TypeArgument typeArg, HashMap<Utf8String, Object> dict) ->
                {
                    for ((Utf8String propPath,Object propValue) :dict)
                    {
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempRef_writer2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer2);
                        Result result = JsonModelRowGenerator.JsonModelSwitch(tempRef_writer2, propPath, propValue);
                        writer2 = tempRef_writer2.argValue;
                        return result;
                    }

                    return Result.Success;
                });
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case List<object> x:
            case ArrayList < Object > x:
                // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
                return writer.argValue.WriteScope(path, new TypeArgument(LayoutType.Array), x, (ref RowWriter writer2, TypeArgument typeArg, ArrayList<Object> list) ->
                {
                    for (Object elm : list) {
                        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempRef_writer2 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer2);
                        Result result = JsonModelRowGenerator.JsonModelSwitch(tempRef_writer2, null, elm);
                        writer2 = tempRef_writer2.argValue;
                        if (result != Result.Success) {
                            return result;
                        }
                    }

                    return Result.Success;
                });
            default:
                Contract.Assert(false, String.format("Unknown type will be ignored: %1$s", value.getClass().getSimpleName()));
                return Result.Failure;
        }
    }
}