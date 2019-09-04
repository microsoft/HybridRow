// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.serialization.hybridrow.Result;

public final class ResultAssert {
    public static void Exists(Result actual) {
        assert Result.EXISTS == actual;
    }

    public static void Exists(Result actual, String message) {
        Assert.AreEqual(Result.EXISTS, actual, message);
    }

    public static void Exists(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.EXISTS, actual, message, parameters);
    }

    public static void InsufficientPermissions(Result actual) {
        assert Result.INSUFFICIENT_PERMISSIONS == actual;
    }

    public static void InsufficientPermissions(Result actual, String message) {
        Assert.AreEqual(Result.INSUFFICIENT_PERMISSIONS, actual, message);
    }

    public static void InsufficientPermissions(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.INSUFFICIENT_PERMISSIONS, actual, message, parameters);
    }

    public static void IsSuccess(Result actual) {
        assert Result.SUCCESS == actual;
    }

    public static void IsSuccess(Result actual, String message) {
        Assert.AreEqual(Result.SUCCESS, actual, message);
    }

    public static void IsSuccess(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.SUCCESS, actual, message, parameters);
    }

    public static void NotFound(Result actual) {
        assert Result.NOT_FOUND == actual;
    }

    public static void NotFound(Result actual, String message) {
        Assert.AreEqual(Result.NOT_FOUND, actual, message);
    }

    public static void NotFound(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.NOT_FOUND, actual, message, parameters);
    }

    public static void TypeConstraint(Result actual) {
        assert Result.TYPE_CONSTRAINT == actual;
    }

    public static void TypeConstraint(Result actual, String message) {
        Assert.AreEqual(Result.TYPE_CONSTRAINT, actual, message);
    }

    public static void TypeConstraint(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.TYPE_CONSTRAINT, actual, message, parameters);
    }

    public static void TypeMismatch(Result actual) {
        assert Result.TYPE_MISMATCH == actual;
    }

    public static void TypeMismatch(Result actual, String message) {
        Assert.AreEqual(Result.TYPE_MISMATCH, actual, message);
    }

    public static void TypeMismatch(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.TYPE_MISMATCH, actual, message, parameters);
    }
}