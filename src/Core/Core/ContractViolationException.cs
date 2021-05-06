// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.Serialization;

    [Serializable]
    [ExcludeFromCodeCoverage]
    public class ContractViolationException : Exception
    {
        public ContractViolationException()
        {
        }

        public ContractViolationException(string message)
            : base(message)
        {
        }

        public ContractViolationException(string message, Exception innerException)
            : base(message, innerException)
        {
        }

        protected ContractViolationException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
