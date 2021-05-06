// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Extensions.CommandLineUtils;

    public class ConvertSchemaCommand
    {
        private const int InitialCapacity = 2 * 1024 * 1024;
        private bool verbose;
        private string outputFile;
        private string namespaceFile;
        private OutputFormat format;

        private ConvertSchemaCommand()
        {
        }

        public static void AddCommand(CommandLineApplication app)
        {
            // ReSharper disable once StringLiteralTypo
            app.Command(
                "convertschema",
                command =>
                {
                    command.Description = "Convert a hybrid row schema namespace between formats.";
                    command.ExtendedHelpText = "Converts a hybrid row schema between formats.  " +
                                               "Supports both JSON SDL and HR Schema as either input or output.";
                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.  Default: false.", CommandOptionType.NoValue);

                    CommandArgument inputOpt = command.Argument("input", "Input file to be converted.");
                    CommandArgument outputOpt = command.Argument("output", "Output file to contain the conversion.");

                    // ReSharper disable twice StringLiteralTypo
                    CommandArgument formatOpt = command.Argument("format", "Output format: hrschema json   Default: hrschema");

                    command.OnExecute(
                        () =>
                        {
                            ConvertSchemaCommand config = new ConvertSchemaCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                namespaceFile = inputOpt.Value.Trim(),
                                outputFile = outputOpt.Value.Trim(),
                                format = OutputFormat.HrSchema,
                            };

                            if (!string.IsNullOrWhiteSpace(formatOpt.Value))
                            {
                                if (!Enum.TryParse(formatOpt.Value.Trim(), true, out config.format))
                                {
                                    throw new CommandParsingException(command, "Invalid output format");
                                }
                            }

                            return config.OnExecute();
                        });
                });
        }

        private enum OutputFormat
        {
            HrSchema,
            Json,
        }

        public int OnExecute()
        {
            if (this.verbose)
            {
                Console.WriteLine($"Converting {this.namespaceFile}...");
                Console.WriteLine();
            }

            int magicNumber;
            using (Stream stm = new FileStream(this.namespaceFile, FileMode.Open))
            {
                // Detect if it is a text or binary file via the encoding of the magic number at the
                // beginning of the HybridRow header.
                magicNumber = stm.ReadByte();
                stm.Seek(-1, SeekOrigin.Current);
                if (magicNumber == -1)
                {
                    Console.Error.WriteLine($"Invalid file: {this.namespaceFile}");
                    return -1;
                }
            }

            Namespace n;
            if (magicNumber == (int)HybridRowVersion.V1)
            {
                byte[] buffer = File.ReadAllBytes(this.namespaceFile);
                RowBuffer row = new RowBuffer(buffer.AsSpan(), HybridRowVersion.V1, SystemSchema.LayoutResolver);
                Result r = Namespace.Read(ref row, out n);
                if (r != Result.Success)
                {
                    Console.Error.WriteLine($"Error {r} while reading file: {this.namespaceFile}");
                    return -1;
                }
            }
            else
            {
                string json = File.ReadAllText(this.namespaceFile);
                n = Namespace.Parse(json);
            }

            if (this.verbose)
            {
                Console.WriteLine($"Namespace: {n.Name}");
                foreach (Schema s in n.Schemas)
                {
                    Console.WriteLine($"  {s.SchemaId} Schema: {s.Name}");
                }
            }

            if (this.verbose)
            {
                Console.WriteLine();
                Console.WriteLine("Writing output:");
                Console.WriteLine($"  Format: {this.format}");
                Console.WriteLine($"  Output: {this.outputFile}");
            }

            switch (this.format)
            {
                case OutputFormat.HrSchema:
                {
                    RowBuffer row = new RowBuffer(ConvertSchemaCommand.InitialCapacity);
                    Layout layout = SystemSchema.LayoutResolver.Resolve((SchemaId)NamespaceHybridRowSerializer.SchemaId);
                    row.InitLayout(HybridRowVersion.V1, layout, SystemSchema.LayoutResolver);
                    Result r = n.Write(ref row);
                    if (r != Result.Success)
                    {
                        Console.Error.WriteLine($"Error {r} while writing file: {this.outputFile}");
                        return -1;
                    }

                    using Stream stm = new FileStream(this.outputFile, FileMode.Create);
                    row.WriteTo(stm);
                    break;
                }
                case OutputFormat.Json:
                {
                    string json = Namespace.ToJson(n);
                    File.WriteAllText(this.outputFile, json);
                    break;
                }
                default:
                    Contract.Fail("Unknown output format.");
                    break;
            }

            if (this.verbose)
            {
                Console.WriteLine();
                Console.WriteLine("Conversion complete.\n");
            }

            return 0;
        }
    }
}
