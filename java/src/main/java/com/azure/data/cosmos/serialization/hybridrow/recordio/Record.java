// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

public final class Record {

    public static Record empty() {
        return new Record(0, 0);
    }

    private int crc32;
    private int length;

    public Record(int length, int crc32) {
        this.length = length;
        this.crc32 = crc32;
    }

    public int crc32() {
        return this.crc32;
    }

    public Record crc32(int value) {
        this.crc32 = value;
        return this;
    }

    public int length() {
        return this.length;
    }

    public Record length(int value) {
        this.length = value;
        return this;
    }
}