// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Extensions.CommandLineUtils;

    public class CompileCommand
    {
        private CompileCommand()
        {
        }

        public bool Verbose { get; set; } = false;

        public List<string> Schemas { get; set; } = null;

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
                            CompileCommand config = new CompileCommand();
                            config.Verbose = verboseOpt.HasValue();
                            config.Schemas = schemasOpt.Values;

                            return config.OnExecute();
                        });
                });
        }

        public int OnExecute()
        {
            foreach (string schemaFile in this.Schemas)
            {
                if (this.Verbose)
                {
                    Console.WriteLine($"Compiling {schemaFile}...");
                    Console.WriteLine();
                }

                string json = File.ReadAllText(schemaFile);
                Namespace n = Namespace.Parse(json);
                if (this.Verbose)
                {
                    Console.WriteLine($"Namespace: {n.Name}");
                    foreach (Schema s in n.Schemas)
                    {
                        Console.WriteLine($"  {s.SchemaId} Schema: {s.Name}");
                    }
                }

                if (this.Verbose)
                {
                    Console.WriteLine();
                    Console.WriteLine($"Compiling {schemaFile} complete.\n");
                }
            }

            return 0;
        }
    }
}
