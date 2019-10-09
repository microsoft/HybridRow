// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

public final class ArrayAssert {
    public static <T> void AreEqual(T[] expected, T[] actual) {
        if (expected == null) {
            assert actual == null;
            return;
        }

        assert actual != null;
        assert expected.length == actual.length;
        for (int i = 0; i < expected.length; i++) {
            assert expected[i] == actual[i];
        }
    }

    public static <T> void AreEqual(T[] expected, T[] actual, String message) {
        if (expected == null) {
            Assert.IsNull(actual, message);
            return;
        }

        Assert.IsNotNull(actual, message);
        Assert.AreEqual(expected.length, actual.length, message);
        for (int i = 0; i < expected.length; i++) {
            Assert.AreEqual(expected[i], actual[i], message);
        }
    }

    public static <T> void AreEqual(T[] expected, T[] actual, String message, Object... parameters) {
        if (expected == null) {
            Assert.IsNull(actual, message, parameters);
            return;
        }

        Assert.IsNotNull(actual, message, parameters);
        Assert.AreEqual(expected.length, actual.length, message, parameters);
        for (int i = 0; i < expected.length; i++) {
            Assert.AreEqual(expected[i], actual[i], message, parameters);
        }
    }
}