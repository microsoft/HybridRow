// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using Microsoft.Extensions.CommandLineUtils;

    internal class HybridRowCLIProgram
    {
        private const string Version = "1.0.0";
        private const string LongVersion = nameof(HybridRowCLI) + " " + HybridRowCLIProgram.Version;

        public static int Main(string[] args)
        {
            try
            {
                CommandLineApplication command = new CommandLineApplication(throwOnUnexpectedArg: true)
                {
                    Name = nameof(HybridRowCLI),
                    Description = "HybridRow Command Line Interface.",
                };

                command.HelpOption("-? | -h | --help");
                command.VersionOption("-ver | --version", HybridRowCLIProgram.Version, HybridRowCLIProgram.LongVersion);
                GenCSharpCommand.AddCommand(command);
                GenCppCommand.AddCommand(command);
                CompileCommand.AddCommand(command);
                ConvertSchemaCommand.AddCommand(command);
                Csv2HybridRowCommand.AddCommand(command);
                Json2HybridRowCommand.AddCommand(command);
                PrintCommand.AddCommand(command);
                RowRewriterHybridRowCommand.AddCommand(command);

                return command.Execute(args);
            }
            catch (CommandParsingException ex)
            {
                Console.Error.WriteLine(ex.Message);
                return -1;
            }
        }
    }
}
