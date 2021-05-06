// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.json;

public final class RowReaderJsonSettings {

    private String indentChars;
    private char quoteChar;

    public RowReaderJsonSettings(String indentChars) {
        this(indentChars, '"');
    }

    public RowReaderJsonSettings() {
        this("  ", '"');
    }

    public RowReaderJsonSettings(String indentChars, char quoteChar) {
        this.indentChars = indentChars;
        this.quoteChar = quoteChar;
    }

    /**
     * If non-null then child objects are indented by one copy of this string per level.
     *
     * @return indentation characters.
     */
    public String indentChars() {
        return this.indentChars;
    }

    /**
     * The current quote character.
     * <p>
     * May be double or single quote.
     *
     * @return quote character.
     */
    public char quoteChar() {
        return this.quoteChar;
    }
}