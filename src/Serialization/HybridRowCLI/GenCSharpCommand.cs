// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowCLI.CSharp;
    using Microsoft.Extensions.CommandLineUtils;

    public class GenCSharpCommand
    {
        private bool verbose;
        private bool includeDataContracts;
        private bool includeEmbedSchema;
        private List<string> schemas;
        private List<string> excludes;
        private string outputFile;

        private GenCSharpCommand()
        {
        }

        // ReSharper disable once StringLiteralTypo
        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "gencs",
                command =>
                {
                    command.Description = "Generate csharp code from a hybrid row schema.";
                    command.ExtendedHelpText = "Generate code for serialization of the types in a hybrid row schema " +
                                               "for use in read and writing instances of the schemas to rows.";
                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.  Default: false.", CommandOptionType.NoValue);
                    CommandOption noDataContractCommandOptionSchemaOpt = command.Option("-d|--no-data-contracts", "Skip data contract generation.  Default: false.", CommandOptionType.NoValue);
                    CommandOption noEmbedSchemaOpt = command.Option("-e|--no-embed", "Skip embedded schema.  Default: false.", CommandOptionType.NoValue);
                    CommandOption excludeOpt = command.Option(
                        "-x|--exclude",
                        "Schema that should be skipped during code generation.",
                        CommandOptionType.MultipleValue);

                    CommandOption outputOpt = command.Option(
                        "-o|--output",
                        "Output file to contain the conversion.",
                        CommandOptionType.SingleValue);

                    CommandArgument schemasOpt = command.Argument(
                        "schema",
                        "File(s) containing the schema namespace to compile.",
                        arg => { arg.MultipleValues = true; });

                    command.OnExecute(
                        () =>
                        {
                            if (!outputOpt.HasValue())
                            {
                                throw new CommandParsingException(command, "Output file is required.");
                            }

                            GenCSharpCommand config = new GenCSharpCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                includeDataContracts = !noDataContractCommandOptionSchemaOpt.HasValue(),
                                includeEmbedSchema = !noEmbedSchemaOpt.HasValue(),
                                schemas = schemasOpt.Values,
                                excludes = excludeOpt.Values,
                                outputFile = outputOpt.Value().Trim(),
                            };

                            return config.OnExecuteAsync().AsTask().Result;
                        });
                });
        }

        private async ValueTask<int> OnExecuteAsync()
        {
            // Ensure target directory exists.
            Directory.CreateDirectory(Path.GetDirectoryName(Path.GetFullPath(this.outputFile)));

            // Create output file.
            await using Stream stm = new FileStream(this.outputFile, FileMode.Create);
            await using CSharpNamespaceGenerator.Emit emit = new CSharpNamespaceGenerator.Emit(stm);
            await emit.FileHeader();
            await emit.Whitespace();
            await emit.Pragma("NamespaceMatchesFolderStructure", "Namespace Declarations must match folder structure.");
            await emit.Pragma("CA1707", "Identifiers should not contain underscores.");
            await emit.Pragma("CA1034", "Do not nest types.");
            await emit.Pragma("CA2104", "Do not declare readonly mutable reference types.");
            await emit.Pragma("SA1129", "Do not use default value type constructor.");
            await emit.Pragma("SA1309", "Field should not begin with an underscore.");
            await emit.Pragma("SA1310", "Field names should not contain underscore.");
            await emit.Pragma("SA1402", "File may only contain a single type.");
            await emit.Pragma("SA1414", "Tuple types in signatures should have element names.");
            await emit.Pragma("SA1514", "Element documentation header should be preceded by blank line.");
            await emit.Pragma("SA1516", "Elements should be separated by blank line.");
            await emit.Pragma("SA1649", "File name should match first type name.");

            foreach (string schemaFile in this.schemas)
            {
                (Namespace ns, LayoutResolver _) = await SchemaUtil.CreateResolverAsync(schemaFile, this.verbose);
                CSharpNamespaceGenerator gen = new CSharpNamespaceGenerator(ns);
                await gen.GenerateNamespace(
                    this.excludes, 
                    emit, 
                    includeDataContracts: this.includeDataContracts, 
                    includeEmbedSchema: this.includeEmbedSchema);

                if (this.verbose)
                {
                    Console.WriteLine();
                    Console.WriteLine($"Complete: {schemaFile}\n");
                }
            }

            if (this.verbose)
            {
                Console.WriteLine();
                Console.WriteLine($"Output: {this.outputFile}\n");
            }
            return 0;
        }
    }
}
