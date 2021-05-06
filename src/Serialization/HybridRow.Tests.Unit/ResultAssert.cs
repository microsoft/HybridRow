// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    internal static class ResultAssert
    {
        public static void IsSuccess(Result actual)
        {
            Assert.AreEqual(Result.Success, actual);
        }

        public static void IsSuccess(Result actual, string message)
        {
            Assert.AreEqual(Result.Success, actual, message);
        }

        public static void IsSuccess(Result actual, string message, params object[] parameters)
        {
            Assert.AreEqual(Result.Success, actual, message, parameters);
        }

        public static void NotFound(Result actual)
        {
            Assert.AreEqual(Result.NotFound, actual);
        }

        public static void NotFound(Result actual, string message)
        {
            Assert.AreEqual(Result.NotFound, actual, message);
        }

        public static void NotFound(Result actual, string message, params object[] parameters)
        {
            Assert.AreEqual(Result.NotFound, actual, message, parameters);
        }

        public static void Exists(Result actual)
        {
            Assert.AreEqual(Result.Exists, actual);
        }

        public static void Exists(Result actual, string message)
        {
            Assert.AreEqual(Result.Exists, actual, message);
        }

        public static void Exists(Result actual, string message, params object[] parameters)
        {
            Assert.AreEqual(Result.Exists, actual, message, parameters);
        }

        public static void TypeMismatch(Result actual)
        {
            Assert.AreEqual(Result.TypeMismatch, actual);
        }

        public static void TypeMismatch(Result actual, string message)
        {
            Assert.AreEqual(Result.TypeMismatch, actual, message);
        }

        public static void TypeMismatch(Result actual, string message, params object[] parameters)
        {
            Assert.AreEqual(Result.TypeMismatch, actual, message, parameters);
        }

        public static void InsufficientPermissions(Result actual)
        {
            Assert.AreEqual(Result.InsufficientPermissions, actual);
        }

        public static void InsufficientPermissions(Result actual, string message)
        {
            Assert.AreEqual(Result.InsufficientPermissions, actual, message);
        }

        public static void InsufficientPermissions(Result actual, string message, params object[] parameters)
        {
            Assert.AreEqual(Result.InsufficientPermissions, actual, message, parameters);
        }

        public static void TypeConstraint(Result actual)
        {
            Assert.AreEqual(Result.TypeConstraint, actual);
        }

        public static void TypeConstraint(Result actual, string message)
        {
            Assert.AreEqual(Result.TypeConstraint, actual, message);
        }

        public static void TypeConstraint(Result actual, string message, params object[] parameters)
        {
            Assert.AreEqual(Result.TypeConstraint, actual, message, parameters);
        }
    }
}
