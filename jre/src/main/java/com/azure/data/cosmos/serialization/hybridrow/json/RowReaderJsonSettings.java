//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.json;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1051 // Do not declare visible instance fields


//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public readonly struct RowReaderJsonSettings
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class RowReaderJsonSettings {
    /**
     * If non-null then child objects are indented by one copy of this string per level.
     */
    public String IndentChars;

    /**
     * The quote character to use.
     * May be <see cref="lang:\""/> or <see cref="'" />.
     */
    public char QuoteChar;


    public RowReaderJsonSettings(String indentChars) {
        this(indentChars, '"');
    }

    public RowReaderJsonSettings() {
        this("  ", '"');
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public RowReaderJsonSettings(string indentChars = "  ", char quoteChar = '"')
    public RowReaderJsonSettings(String indentChars, char quoteChar) {
        this.IndentChars = indentChars;
        this.QuoteChar = quoteChar;
    }

    public RowReaderJsonSettings clone() {
        RowReaderJsonSettings varCopy = new RowReaderJsonSettings();

        varCopy.IndentChars = this.IndentChars;
        varCopy.QuoteChar = this.QuoteChar;

        return varCopy;
    }
}