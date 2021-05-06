// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    internal static class AssertThrowsException
    {
        /// <summary>
        /// Tests whether the code specified by delegate <paramref name="action" /> throws exact given
        /// exception of type <typeparamref name="T" /> (and not of derived type) and throws <code>
        /// AssertFailedException
        /// </code> if code does not throws exception or throws exception of type other than
        /// <typeparamref name="T" />.
        /// </summary>
        /// <param name="action">Delegate to code to be tested and which is expected to throw exception.</param>
        /// <typeparam name="T">Type of exception expected to be thrown.</typeparam>
        /// <exception cref="T:Microsoft.VisualStudio.TestTools.UnitTesting.AssertFailedException">
        /// Thrown if
        /// <paramref name="action" /> does not throws exception of type <typeparamref name="T" />.
        /// </exception>
        /// <returns>The exception that was thrown.</returns>
        public static T ThrowsException<T>(Action action)
            where T : Exception
        {
            return AssertThrowsException.ThrowsException<T>(action, string.Empty, null);
        }

        /// <summary>
        /// Tests whether the code specified by delegate <paramref name="action" /> throws exact given
        /// exception of type <typeparamref name="T" /> (and not of derived type) and throws <code>
        /// AssertFailedException
        /// </code> if code does not throws exception or throws exception of type other than
        /// <typeparamref name="T" />.
        /// </summary>
        /// <param name="action">Delegate to code to be tested and which is expected to throw exception.</param>
        /// <param name="message">
        /// The message to include in the exception when <paramref name="action" /> does
        /// not throws exception of type <typeparamref name="T" />.
        /// </param>
        /// <typeparam name="T">Type of exception expected to be thrown.</typeparam>
        /// <exception cref="T:Microsoft.VisualStudio.TestTools.UnitTesting.AssertFailedException">
        /// Thrown if
        /// <paramref name="action" /> does not throws exception of type <typeparamref name="T" />.
        /// </exception>
        /// <returns>The exception that was thrown.</returns>
        public static T ThrowsException<T>(Action action, string message)
            where T : Exception
        {
            return AssertThrowsException.ThrowsException<T>(action, message, null);
        }

        /// <summary>
        /// Tests whether the code specified by delegate <paramref name="action" /> throws exact given
        /// exception of type <typeparamref name="T" /> (and not of derived type) and throws <code>
        /// AssertFailedException
        /// </code> if code does not throws exception or throws exception of type other than
        /// <typeparamref name="T" />.
        /// </summary>
        /// <param name="action">Delegate to code to be tested and which is expected to throw exception.</param>
        /// <param name="message">
        /// The message to include in the exception when <paramref name="action" /> does
        /// not throws exception of type <typeparamref name="T" />.
        /// </param>
        /// <param name="parameters">An array of parameters to use when formatting <paramref name="message" />.</param>
        /// <typeparam name="T">Type of exception expected to be thrown.</typeparam>
        /// <exception cref="T:Microsoft.VisualStudio.TestTools.UnitTesting.AssertFailedException">
        /// Thrown if
        /// <paramref name="action" /> does not throws exception of type <typeparamref name="T" />.
        /// </exception>
        /// <returns>The exception that was thrown.</returns>
        public static T ThrowsException<T>(Action action, string message, params object[] parameters)
            where T : Exception
        {
            if (action == null)
            {
                throw new ArgumentNullException(nameof(action));
            }

            if (message == null)
            {
                throw new ArgumentNullException(nameof(message));
            }

            try
            {
                action();
            }
            catch (Exception ex)
            {
                if (typeof(T) != ex.GetType())
                {
                    Assert.Fail(
                        string.Format(
                            "Threw exception {2}, but exception {1} was expected. {0}\nException Message: {3}\nStack Trace: {4}",
                            (object)AssertThrowsException.ReplaceNulls(message),
                            (object)typeof(T).Name,
                            (object)ex.GetType().Name,
                            (object)ex.Message,
                            (object)ex.StackTrace),
                        parameters);
                }

                return (T)ex;
            }

            Assert.Fail(
                string.Format(
                    "No exception thrown. {1} exception was expected. {0}",
                    AssertThrowsException.ReplaceNulls(message),
                    typeof(T).Name),
                parameters);

            return default;
        }

        /// <summary>
        /// Safely converts an object to a string, handling null values and null characters. Null
        /// values are converted to "(null)". Null characters are converted to "\\0".
        /// </summary>
        /// <param name="input">The object to convert to a string.</param>
        /// <returns>The converted string.</returns>
        internal static string ReplaceNulls(object input)
        {
            string input1 = input?.ToString();
            if (input1 == null)
            {
                return "(null)";
            }

            return Assert.ReplaceNullChars(input1);
        }
    }
}
