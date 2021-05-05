// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

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

        this.tokens = new HashMap<>();
        this.tokens.put(Utf8String.EMPTY, StringToken.NONE);

        this.stringTokens = new HashMap<>();
        this.stringTokens.put("", StringToken.NONE);

        this.strings = new ArrayList<>();
        this.strings.add(Utf8String.EMPTY);

        this.count = 1;
    }

    /**
     * Looks up a token's corresponding string.
     *
     * @param token The token to look up.
     * @return True if successful, false otherwise.
     */
    public Optional<Utf8String> tryFindString(long token) {
        return token >= (long)this.strings.size() ? Optional.empty() : Optional.of(this.strings.get((int) token));
    }

    /**
     * Looks up a string's corresponding token.
     *
     * @param path  The string to look up.
     * @return {@code true} if successful, {@code false} otherwise.
     */
    public Optional<StringToken> tryFindToken(UtfAnyString path) {

        if (path.isNull()) {
            return Optional.empty();
        }

        if (path.isUtf8()) {
            return Optional.ofNullable(this.tokens.get(path.toUtf8()));
        }

        return Optional.ofNullable(this.stringTokens.get(path.toUtf16()));
    }

    /**
     * Assign a token to a string
     * <p>
     * If the string already has a token, that token is returned instead.
     *
     * @param path The string to assign a new token.
     * @return The token assigned to the string.
     */
    public StringToken add(Utf8String path) {
        checkArgument(path != null);
        final StringToken token = this.tokens.get(path);
        return token == null ? this.allocateToken(path) : token;
    }

    /**
     * The number of unique tokens described by the encoding.
     *
     * @return the number of unique tokens described by the encoding.
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

        final StringToken token = new StringToken(this.count++, path);

        this.stringTokens.put(path.toUtf16(), token);
        this.tokens.put(path, token);
        this.strings.add(path);

        checkState((long)this.strings.size() - 1 == token.id());
        return token;
    }
}