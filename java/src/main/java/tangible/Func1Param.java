// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package tangible;

@FunctionalInterface
public interface Func1Param<T, TResult> {
    TResult invoke(T t);
}