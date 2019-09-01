// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.serialization.hybridrow.Result;

public final class ResultAssert {
    public static void Exists(Result actual) {
        assert Result.Exists == actual;
    }

    public static void Exists(Result actual, String message) {
        Assert.AreEqual(Result.Exists, actual, message);
    }

    public static void Exists(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.Exists, actual, message, parameters);
    }

    public static void InsufficientPermissions(Result actual) {
        assert Result.InsufficientPermissions == actual;
    }

    public static void InsufficientPermissions(Result actual, String message) {
        Assert.AreEqual(Result.InsufficientPermissions, actual, message);
    }

    public static void InsufficientPermissions(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.InsufficientPermissions, actual, message, parameters);
    }

    public static void IsSuccess(Result actual) {
        assert Result.Success == actual;
    }

    public static void IsSuccess(Result actual, String message) {
        Assert.AreEqual(Result.Success, actual, message);
    }

    public static void IsSuccess(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.Success, actual, message, parameters);
    }

    public static void NotFound(Result actual) {
        assert Result.NotFound == actual;
    }

    public static void NotFound(Result actual, String message) {
        Assert.AreEqual(Result.NotFound, actual, message);
    }

    public static void NotFound(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.NotFound, actual, message, parameters);
    }

    public static void TypeConstraint(Result actual) {
        assert Result.TypeConstraint == actual;
    }

    public static void TypeConstraint(Result actual, String message) {
        Assert.AreEqual(Result.TypeConstraint, actual, message);
    }

    public static void TypeConstraint(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.TypeConstraint, actual, message, parameters);
    }

    public static void TypeMismatch(Result actual) {
        assert Result.TypeMismatch == actual;
    }

    public static void TypeMismatch(Result actual, String message) {
        Assert.AreEqual(Result.TypeMismatch, actual, message);
    }

    public static void TypeMismatch(Result actual, String message, Object... parameters) {
        Assert.AreEqual(Result.TypeMismatch, actual, message, parameters);
    }
}