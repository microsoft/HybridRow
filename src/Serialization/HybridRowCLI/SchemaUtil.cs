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
        /// <returns>A Namespace and its resolver.</returns>
        public static async Task<(Namespace ns, LayoutResolver resolver)> CreateResolverAsync(string namespaceFile, bool verbose)
        {
            (Namespace ns, LayoutResolver resolver) t;
            if (string.IsNullOrWhiteSpace(namespaceFile))
            {
                t = (SystemSchema.GetNamespace(), SystemSchema.LayoutResolver);
            }
            else
            {
                if (verbose)
                {
                    Console.WriteLine($"Loading {namespaceFile}...");
                    Console.WriteLine();
                }

                string json = await File.ReadAllTextAsync(namespaceFile);
                t = SchemaUtil.LoadFromSdl(json, verbose, SystemSchema.LayoutResolver);
            }

            Contract.Requires(t.resolver != null);
            return t;
        }

        /// <summary>Create a HybridRow resolver for given piece of embedded Schema Definition Language (SDL).</summary>
        /// <param name="json">The SDL to parse.</param>
        /// <param name="verbose">True if verbose output should be written to stdout.</param>
        /// <param name="parent">An (optional) parent resolver for namespace chaining.</param>
        /// <returns>A resolver that resolves all types in the given SDL.</returns>
        public static (Namespace ns, LayoutResolver resolver) LoadFromSdl(string json, bool verbose, LayoutResolver parent = default)
        {
            Namespace ns = Namespace.Parse(json);
            if (verbose)
            {
                Console.WriteLine($"Namespace: {ns.Name}");
                foreach (Schema s in ns.Schemas)
                {
                    Console.WriteLine($"  {s.SchemaId} Schema: {s.Name}");
                }
            }

            LayoutResolver resolver = new LayoutResolverNamespace(ns, parent);
            if (verbose)
            {
                Console.WriteLine();
                Console.WriteLine($"Loaded {ns.Name}.\n");
            }

            return (ns, resolver);
        }
    }
}
