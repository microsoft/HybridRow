// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkState;

public final class RecordSerializer {

    @Nonnull
    public static Result read(RowReader reader, Out<Record> record) {

        Out<Integer> value = new Out<>();
        record.set(Record.empty());

        while (reader.read()) {

            String path = reader.path().toUtf16();
            checkState(path != null);
            Result result;

            // TODO: use Path tokens here

            switch (path) {

                case "length":

                    result = reader.readInt32(value);
                    record.get().length(value.get());

                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;

                case "crc32":

                    result = reader.readInt32(value);
                    record.get().crc32(value.get());

                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;
            }
        }

        return Result.SUCCESS;
    }

    @Nonnull
    public static Result write(RowWriter writer, TypeArgument typeArg, Record record) {
        Result result = writer.writeInt32(new UtfAnyString("length"), record.length());
        if (result != Result.SUCCESS) {
            return result;
        }
        return writer.writeUInt32(new UtfAnyString("crc32"), record.crc32());
    }
}