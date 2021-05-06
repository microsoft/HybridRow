// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.Serialization;

    [Serializable]
    [ExcludeFromCodeCoverage]
    public sealed class SchemaException : Exception
    {
        public SchemaException()
        {
        }

        public SchemaException(string message)
            : base(message)
        {
        }

        public SchemaException(string message, Exception innerException)
            : base(message, innerException)
        {
        }

        private SchemaException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
