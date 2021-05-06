// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core
{
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;

    /// <summary>
    /// Used to check contract invariants in either Debug-only (Assert)
    /// or Debug and Release (Requires, Invariant, etc.). Contract failures
    /// will attempt to break into the debugger, will trace the error, and
    /// will throw a ContractViolationException.
    /// </summary>
    [ExcludeFromCodeCoverage]
    public static class Contract
    {
        [Conditional("DEBUG")]
        public static void Assert(bool condition)
        {
            if (!condition)
            {
                Contract.Fail("Assert", string.Empty);
            }
        }

        [Conditional("DEBUG")]
        public static void Assert(bool condition, string message)
        {
            if (!condition)
            {
                Contract.Fail("Assert", message);
            }
        }

        public static void Requires(bool condition)
        {
            if (!condition)
            {
                Contract.Fail("Requires", string.Empty);
            }
        }

        public static void Requires(bool condition, string message)
        {
            if (!condition)
            {
                Contract.Fail("Requires", message);
            }
        }

        public static void Invariant(bool condition)
        {
            if (!condition)
            {
                Contract.Fail("Invariant", string.Empty);
            }
        }

        public static void Invariant(bool condition, string message)
        {
            if (!condition)
            {
                Contract.Fail("Invariant", message);
            }
        }

        public static void Fail()
        {
            Contract.Fail("Fail", string.Empty);
        }

        public static void Fail(string message)
        {
            Contract.Fail("Fail", message);
        }

        private static void Fail(string api, string message)
        {
            StackTrace stack = new StackTrace(2, true);
            string error = $"{api} Failure: {message}\n\tStack: {stack}";
            Trace.TraceError(error);
            Trace.Flush();

            // Try breaking into the debugger if attached.
            Debugger.Break();

            // This exception should NEVER be caught.
            // TODO: make this uncatchable.
            throw new ContractViolationException(error);
        }
    }
}
