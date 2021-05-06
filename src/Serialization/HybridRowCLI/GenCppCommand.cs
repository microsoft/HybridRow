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
    using Microsoft.Azure.Cosmos.Serialization.HybridRowCLI.Cpp;
    using Microsoft.Extensions.CommandLineUtils;

    public class GenCppCommand
    {
        private bool verbose;
        private bool includeDataContracts;
        private bool includeEmbedSchema;
        private List<string> schemas;
        private List<string> excludes;
        private string outputHeaderFile;
        private string outputSourceFile;
        private string dataHeaderFile;

        private GenCppCommand()
        {
        }

        // ReSharper disable once StringLiteralTypo
        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "gencpp",
                command =>
                {
                    command.Description = "Generate cpp code from a hybrid row schema.";
                    command.ExtendedHelpText = "Generate code for serialization of the types in a hybrid row schema " +
                                               "for use in read and writing instances of the schemas to rows.";
                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.  Default: false.", CommandOptionType.NoValue);
                    CommandOption noEmbedSchemaOpt = command.Option(
                        "-e|--no-embed",
                        "Skip embedded schema.  Default: false.",
                        CommandOptionType.NoValue);
                    CommandOption noDataContractCommandOptionSchemaOpt = command.Option(
                        "-d|--no-data-contracts",
                        "Skip data contract generation.  Default: false.",
                        CommandOptionType.NoValue);
                    CommandOption excludeOpt = command.Option(
                        "-x|--exclude",
                        "Schema that should be skipped during code generation.",
                        CommandOptionType.MultipleValue);

                    CommandOption outputHeaderOpt = command.Option(
                        "-oh|--output-header",
                        "Output header file to contain the conversion.",
                        CommandOptionType.SingleValue);

                    CommandOption outputSourceOpt = command.Option(
                        "-os|--output-source",
                        "Output source file to contain the conversion.",
                        CommandOptionType.SingleValue);

                    CommandOption inputDataFile = command.Option(
                        "-ih|--input-header",
                        "Input header file containing the data contract classes.",
                        CommandOptionType.SingleValue);

                    CommandArgument schemasOpt = command.Argument(
                        "schema",
                        "File(s) containing the schema namespace to compile.",
                        arg => { arg.MultipleValues = true; });

                    command.OnExecute(
                        () =>
                        {
                            if (!outputHeaderOpt.HasValue())
                            {
                                throw new CommandParsingException(command, "Output header file is required.");
                            }

                            if (!outputSourceOpt.HasValue())
                            {
                                throw new CommandParsingException(command, "Output source file is required.");
                            }

                            GenCppCommand config = new GenCppCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                includeDataContracts = !noDataContractCommandOptionSchemaOpt.HasValue(),
                                includeEmbedSchema = !noEmbedSchemaOpt.HasValue(),
                                schemas = schemasOpt.Values,
                                excludes = excludeOpt.Values,
                                outputHeaderFile = outputHeaderOpt.Value().Trim(),
                                outputSourceFile = outputSourceOpt.Value().Trim(),
                                dataHeaderFile = inputDataFile.HasValue() ? inputDataFile.Value().Trim() : null
                            };

                            return config.OnExecuteAsync().AsTask().Result;
                        });
                });
        }

        private async ValueTask<int> OnExecuteAsync()
        {
            // Ensure target directory exists.
            Directory.CreateDirectory(Path.GetDirectoryName(Path.GetFullPath(this.outputHeaderFile)));
            Directory.CreateDirectory(Path.GetDirectoryName(Path.GetFullPath(this.outputSourceFile)));

            // Create output files.
            await using Stream headerStm = new FileStream(this.outputHeaderFile, FileMode.Create);
            await using Stream sourceStm = new FileStream(this.outputSourceFile, FileMode.Create);
            await using CppNamespaceGenerator.Emit emitHeader = new CppNamespaceGenerator.Emit(headerStm);
            await using CppNamespaceGenerator.Emit emitSource = new CppNamespaceGenerator.Emit(sourceStm);
            await emitHeader.FileHeader();
            await emitHeader.Pragma("once");
            await emitHeader.Whitespace();
            await emitHeader.GeneratedComment();
            await emitHeader.Whitespace();

            await emitSource.FileHeader();
            await emitSource.Include("pch.h");
            if (this.dataHeaderFile != null)
            {
                await emitSource.Include(Path.GetFileName(this.dataHeaderFile));
            }

            await emitSource.Include(Path.GetFileName(this.outputHeaderFile));
            await emitSource.Whitespace();
            await emitSource.GeneratedComment();
            await emitSource.Whitespace();

            foreach (string schemaFile in this.schemas)
            {
                (Namespace ns, LayoutResolver _) = await SchemaUtil.CreateResolverAsync(schemaFile, this.verbose);
                CppNamespaceGenerator gen = new CppNamespaceGenerator(ns);
                await gen.GenerateNamespace(
                    this.excludes,
                    emitHeader,
                    emitSource,
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
                Console.WriteLine($"Output: {this.outputHeaderFile}\n");
                Console.WriteLine($"Output: {this.outputSourceFile}\n");
            }
            return 0;
        }
    }
}
