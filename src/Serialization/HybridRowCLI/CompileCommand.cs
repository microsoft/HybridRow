// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Extensions.CommandLineUtils;

    public class CompileCommand
    {
        private bool verbose;
        private List<string> schemas;

        private CompileCommand()
        {
        }

        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "compile",
                command =>
                {
                    command.Description = "Compile and validate a hybrid row schema.";
                    command.ExtendedHelpText = "Compile a hybrid row schema using the object model compiler and run schema consistency validator.";
                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.  Default: false.", CommandOptionType.NoValue);
                    CommandArgument schemasOpt = command.Argument(
                        "schema",
                        "File(s) containing the schema namespace to compile.",
                        arg => { arg.MultipleValues = true; });

                    command.OnExecute(
                        () =>
                        {
                            CompileCommand config = new CompileCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                schemas = schemasOpt.Values
                            };

                            return config.OnExecuteAsync().Result;
                        });
                });
        }

        private async Task<int> OnExecuteAsync()
        {
            foreach (string schemaFile in this.schemas)
            {
                (Namespace ns, LayoutResolver resolver) = await SchemaUtil.CreateResolverAsync(schemaFile, this.verbose);

                foreach (Schema s in ns.Schemas)
                {
                    Console.WriteLine($"Compiling Schema: {s.Name}");
                    _ = resolver.Resolve(s.SchemaId);
                }

                if (this.verbose)
                {
                    Console.WriteLine();
                    Console.WriteLine($"Compiling {schemaFile} complete.\n");
                }
            }

            return 0;
        }
    }
}
