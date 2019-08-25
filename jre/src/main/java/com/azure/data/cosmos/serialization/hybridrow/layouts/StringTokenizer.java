//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public final class StringTokenizer {

    private final HashMap<String, StringToken> stringTokens;
    private final ArrayList<Utf8String> strings;
    private final HashMap<Utf8String, StringToken> tokens;

    private int count;

    /**
     * Initializes a new instance of the {@link StringTokenizer} class.
     */
    public StringTokenizer() {

        this.tokens = new HashMap<Utf8String, StringToken>(
            Map.ofEntries(Map.entry(Utf8String.EMPTY, new StringToken(0, Utf8String.EMPTY))));

        this.stringTokens = new HashMap<String, StringToken>(
            Map.ofEntries(Map.entry("", new StringToken(0, Utf8String.EMPTY))));

        this.strings = new ArrayList<Utf8String>(Collections.singletonList(Utf8String.EMPTY));
        this.count = 1;
    }

    /**
     * Looks up a token's corresponding string.
     *
     * @param token The token to look up.
     * @param path  If successful, the token's assigned string.
     * @return True if successful, false otherwise.
     */
    public boolean tryFindString(long token, Out<Utf8String> path) {

        if (token >= (long)this.strings.size()) {
            path.setAndGet(null);
            return false;
        }

        path.setAndGet(this.strings.get((int) token));
        return true;
    }

    /**
     * Looks up a string's corresponding token.
     *
     * @param path  The string to look up.
     * @param token If successful, the string's assigned token.
     * @return True if successful, false otherwise.
     */
    public boolean tryFindToken(UtfAnyString path, Out<StringToken> token) {

        if (path.isNull()) {
            token.setAndGet(null);
            return false;
        }

        if (path.isUtf8()) {
            return (this.tokens.containsKey(path.toUtf8()) && (token.setAndGet(this.tokens.get(path.toUtf8()))) == token.get());
        }

        return (this.stringTokens.containsKey(path.toUtf16()) && (token.setAndGet(this.stringTokens.get(path.toUtf16()))) == token.get());
    }

    /**
     * Assign a token to the string.
     * If the string already has a token, that token is returned instead.
     *
     * @param path The string to assign a new token.
     * @return The token assigned to the string.
     */
    public StringToken add(Utf8String path) {

        checkArgument(path != null);
        StringToken token;

        if (this.tokens.containsKey(path) && (token = this.tokens.get(path)) == token) {
            return token;
        }

        token = this.allocateToken(path).clone();
        return token;
    }

    /**
     * The number of unique tokens described by the encoding.
     */
    public int count() {
        return this.count;
    }

    /**
     * Allocates a new token and assigns the string to it.
     *
     * @param path The string that needs a new token.
     * @return The new allocated token.
     */
    private StringToken allocateToken(Utf8String path) {

        final long id = this.count++;
        final StringToken token = new StringToken(id, path);

        this.tokens.put(path, token.clone());
        this.stringTokens.put(path.toString(), token.clone());
        this.strings.add(path);

        checkState((long)this.strings.size() - 1 == id);
        return token.clone();
    }
}