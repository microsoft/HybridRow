//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package tangible;

@FunctionalInterface
public interface Func1Param<T, TResult> {
    TResult invoke(T t);
}