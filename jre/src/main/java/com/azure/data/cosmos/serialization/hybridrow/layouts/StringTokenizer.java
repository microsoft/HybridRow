//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class StringTokenizer {
    /**
     * The number of unique tokens described by the encoding.
     */
    private int Count;
    private HashMap<String, StringToken> stringTokens;
    private ArrayList<Utf8String> strings;
    private HashMap<Utf8String, StringToken> tokens;

    /**
     * Initializes a new instance of the {@link StringTokenizer} class.
     */
    public StringTokenizer() {
        this.tokens = new HashMap<Utf8String, StringToken>(Map.ofEntries(Map.entry(Utf8String.Empty,
            new StringToken(0, Utf8String.Empty))));
        this.stringTokens = new HashMap<String, StringToken>(Map.ofEntries(Map.entry("", new StringToken(0,
            Utf8String.Empty))));
        this.strings = new ArrayList<Utf8String>(Arrays.asList(Utf8String.Empty));
        this.setCount(1);
    }

    public int getCount() {
        return Count;
    }

    private void setCount(int value) {
        Count = value;
    }

    /**
     * Assign a token to the string.
     * If the string already has a token, that token is returned instead.
     *
     * @param path The string to assign a new token.
     * @return The token assigned to the string.
     */
    public StringToken Add(Utf8String path) {
        checkArgument(path != null);

        StringToken token;
        if (this.tokens.containsKey(path) && (token = this.tokens.get(path)) == token) {
            return token;
        }

        token = this.AllocateToken(path).clone();
        return token;
    }

    /**
     * Looks up a token's corresponding string.
     *
     * @param token The token to look up.
     * @param path  If successful, the token's assigned string.
     * @return True if successful, false otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public bool TryFindString(ulong token, out Utf8String path)
    public boolean TryFindString(long token, Out<Utf8String> path) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: if (token >= (ulong)this.strings.Count)
        if (token >= (long)this.strings.size()) {
            path.set(null);
            return false;
        }

        path.set(this.strings.get((int)token));
        return true;
    }

    /**
     * Looks up a string's corresponding token.
     *
     * @param path  The string to look up.
     * @param token If successful, the string's assigned token.
     * @return True if successful, false otherwise.
     */
    public boolean TryFindToken(UtfAnyString path, Out<StringToken> token) {
        if (path.IsNull) {
            token.set(null);
            return false;
        }

        if (path.IsUtf8) {
            return (this.tokens.containsKey(path.ToUtf8String()) && (token.set(this.tokens.get(path.ToUtf8String()))) == token.get());
        }

        return (this.stringTokens.containsKey(path.toString()) && (token.set(this.stringTokens.get(path.toString()))) == token.get());
    }

    /**
     * Allocates a new token and assigns the string to it.
     *
     * @param path The string that needs a new token.
     * @return The new allocated token.
     */
    private StringToken AllocateToken(Utf8String path) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong id = (ulong)this.Count++;
        long id = this.getCount();
        this.setCount(this.getCount() + 1);
        StringToken token = new StringToken(id, path);
        this.tokens.put(path, token.clone());
        this.stringTokens.put(path.toString(), token.clone());
        this.strings.add(path);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Contract.Assert((ulong)this.strings.Count - 1 == id);
        checkState((long)this.strings.size() - 1 == id);
        return token.clone();
    }
}