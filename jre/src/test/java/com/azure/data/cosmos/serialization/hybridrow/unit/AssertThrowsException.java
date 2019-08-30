// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

public final class AssertThrowsException {
    /**
     * Safely converts an object to a string, handling null values and null characters. Null
     * values are converted to "(null)". Null characters are converted to "\\0".
     *
     * @param input The object to convert to a string.
     * @return The converted string.
     */
    public static String ReplaceNulls(Object input) {
        String input1 = input == null ? null : input.toString();
        if (input1 == null) {
            return "(null)";
        }

        return Assert.ReplaceNullChars(input1);
    }

    /**
     * Tests whether the code specified by delegate <paramref name="action" /> throws exact given
     * exception of type <typeparamref name="T" /> (and not of derived type) and throws <code>
     * AssertFailedException
     * </code> if code does not throws exception or throws exception of type other than
     * <typeparamref name="T" />.
     *
     * @param action  Delegate to code to be tested and which is expected to throw exception.
     * @param message The message to include in the exception when <paramref name="action" /> does
     *                not throws exception of type <typeparamref name="T" />.
     *
     *                <typeparam name="T">Type of exception expected to be thrown.</typeparam>
     * @return The exception that was thrown.
     * @throws T:Microsoft.VisualStudio.TestTools.UnitTesting.AssertFailedException Thrown if
     *                                                                              <paramref name="action" /> does
     *                                                                              not throws exception of type
     *                                                                              <typeparamref name="T" />.
     */
    public static <T extends RuntimeException> T ThrowsException(tangible.Action0Param action, String message) {
        return AssertThrowsException.ThrowsException(action, message, null);
    }

    /**
     * Tests whether the code specified by delegate <paramref name="action" /> throws exact given
     * exception of type <typeparamref name="T" /> (and not of derived type) and throws <code>
     * AssertFailedException
     * </code> if code does not throws exception or throws exception of type other than
     * <typeparamref name="T" />.
     *
     * @param action     Delegate to code to be tested and which is expected to throw exception.
     * @param message    The message to include in the exception when <paramref name="action" /> does
     *                   not throws exception of type <typeparamref name="T" />.
     * @param parameters An array of parameters to use when formatting <paramref name="message" />.
     *                   <typeparam name="T">Type of exception expected to be thrown.</typeparam>
     * @return The exception that was thrown.
     * @throws T:Microsoft.VisualStudio.TestTools.UnitTesting.AssertFailedException Thrown if
     *                                                                              <paramref name="action" /> does
     *                                                                              not throws exception of type
     *                                                                              <typeparamref name="T" />.
     */
    public static <T extends RuntimeException> T ThrowsException(tangible.Action0Param action, String message,
                                                                 Object... parameters) {
        if (action == null) {
            throw new NullPointerException("action");
        }

        if (message == null) {
            throw new NullPointerException("message");
        }

        try {
            action.invoke();
        } catch (RuntimeException ex) {
            if (T.class != ex.getClass()) {
                Assert.Fail(String.format("Threw exception %3$s, but exception %2$s was expected. %1$s\nException " +
                    "Message: %4$s\nStack Trace: %5$s", AssertThrowsException.ReplaceNulls(message),
                    (Object)T.class.Name, ex.getClass().getSimpleName(), ex.getMessage(),
                    (Object)ex.StackTrace), parameters);
            }

            return (T)ex;
        }

        Assert.Fail(String.format("No exception thrown. %2$s exception was expected. %1$s",
            AssertThrowsException.ReplaceNulls(message), T.class.Name), parameters);

        return null;
    }

    /**
     * Tests whether the code specified by delegate <paramref name="action" /> throws exact given
     * exception of type <typeparamref name="T" /> (and not of derived type) and throws <code>
     * AssertFailedException
     * </code> if code does not throws exception or throws exception of type other than
     * <typeparamref name="T" />.
     *
     * @param action Delegate to code to be tested and which is expected to throw exception.
     *               <typeparam name="T">Type of exception expected to be thrown.</typeparam>
     * @return The exception that was thrown.
     * @throws T:Microsoft.VisualStudio.TestTools.UnitTesting.AssertFailedException Thrown if
     *                                                                              <paramref name="action" /> does
     *                                                                              not throws exception of type
     *                                                                              <typeparamref name="T" />.
     */
    public static <T extends RuntimeException> T ThrowsException(tangible.Action0Param action) {
        return AssertThrowsException.ThrowsException(action, "", null);
    }
}