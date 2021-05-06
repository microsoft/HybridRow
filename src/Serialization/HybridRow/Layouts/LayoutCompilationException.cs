// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.Serialization;

    [Serializable]
    [ExcludeFromCodeCoverage]
    public sealed class LayoutCompilationException : Exception
    {
        public LayoutCompilationException()
        {
        }

        public LayoutCompilationException(string message)
            : base(message)
        {
        }

        public LayoutCompilationException(string message, Exception innerException)
            : base(message, innerException)
        {
        }

        private LayoutCompilationException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
