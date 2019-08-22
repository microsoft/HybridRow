//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf;

import org.bson.BsonWriter;
import org.bson.BsonBinaryWriter
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.google.common.base.Strings.lenientFormat;

public final class BsonJsonModelRowGenerator implements Closeable {
    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.MemoryStream is
    // input or output:
    private MemoryStream stream;
    private BsonWriter writer;

    public BsonJsonModelRowGenerator(int capacity) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.MemoryStream
        // is input or output:
        this.stream = new MemoryStream(capacity);
        this.writer = new BsonBinaryWriter(this.stream);
    }

    public int getLength() {
        return (int)this.stream.Position;
    }

    public void Reset() {
        this.stream.SetLength(0);
        this.stream.Position = 0;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.stream.ToArray();
    }

    public void WriteBuffer(HashMap<Utf8String, Object> dict) {
        this.writer.writeStartDocument();
        for ((Utf8String propPath,Object propValue) : dict)
        {
            this.JsonModelSwitch(propPath, propValue);
        }

        this.writer.writeEndDocument();
    }

    public void close() throws IOException {
        this.writer.Dispose();
        this.stream.Dispose();
    }

    private void JsonModelSwitch(Utf8String path, Object value) {
        if (path != null) {
            this.writer.writeName(path.toString());
        }

        switch (value) {
            case null:
                this.writer.writeNull();
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case bool x:
            case
                boolean x:
                this.writer.writeBoolean(x);
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case long x:
            case
                long x:
                this.writer.writeInt64(x);
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case double x:
            case
                double x:
                this.writer.writeDouble(x);
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case string x:
            case String
                x:
                this.writer.writeString(x);
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case Utf8String x:
            case Utf8String
                x:
                this.writer.writeString(x.toString());
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case byte[] x:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            case
                byte[] x:
                this.writer.writeBytes(x);
                return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case Dictionary<Utf8String, object> x:
            case HashMap < Utf8String, Object > x:
                this.writer.writeStartDocument();
                for ((Utf8String propPath,Object propValue) :x)
            {
                this.JsonModelSwitch(propPath, propValue);
            }

            this.writer.writeEndDocument();
            return;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case List<object> x:
            case ArrayList < Object > x:
                this.writer.writeStartArray();
                for (Object item : x) {
                    this.JsonModelSwitch(null, item);
                }

                this.writer.writeEndArray();

                return;
            default:
                throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s", value.getClass().getSimpleName()));
        }
    }
}