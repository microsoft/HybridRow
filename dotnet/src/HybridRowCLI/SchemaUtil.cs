// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.IO;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public static class SchemaUtil
    {
        /// <summary>Create a resolver.</summary>
        /// <param name="namespaceFile">
        /// Optional namespace file containing a namespace to be included in the
        /// resolver.
        /// </param>
        /// <param name="verbose">True if verbose output should be written to stdout.</param>
        /// <returns>A resolver.</returns>
        public static async Task<LayoutResolver> CreateResolverAsync(string namespaceFile, bool verbose)
        {
            LayoutResolver globalResolver;
            if (string.IsNullOrWhiteSpace(namespaceFile))
            {
                globalResolver = SystemSchema.LayoutResolver;
            }
            else
            {
                if (verbose)
                {
                    Console.WriteLine($"Loading {namespaceFile}...");
                    Console.WriteLine();
                }

                string json = await File.ReadAllTextAsync(namespaceFile);
                globalResolver = SchemaUtil.LoadFromSdl(json, verbose, SystemSchema.LayoutResolver);
            }

            Contract.Requires(globalResolver != null);
            return globalResolver;
        }

        /// <summary>Create a HybridRow resolver for given piece of embedded Schema Definition Language (SDL).</summary>
        /// <param name="json">The SDL to parse.</param>
        /// <param name="verbose">True if verbose output should be written to stdout.</param>
        /// <returns>A resolver that resolves all types in the given SDL.</returns>
        public static LayoutResolver LoadFromSdl(string json, bool verbose, LayoutResolver parent = default)
        {
            Namespace n = Namespace.Parse(json);
            if (verbose)
            {
                Console.WriteLine($"Namespace: {n.Name}");
                foreach (Schema s in n.Schemas)
                {
                    Console.WriteLine($"  {s.SchemaId} Schema: {s.Name}");
                }
            }

            LayoutResolver resolver = new LayoutResolverNamespace(n, parent);
            if (verbose)
            {
                Console.WriteLine();
                Console.WriteLine($"Loaded {n.Name}.\n");
            }

            return resolver;
        }
    }
}
