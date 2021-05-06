// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowStress
{
    using System;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.Extensions.CommandLineUtils;

    public class HybridRowStressConfig
    {
        private HybridRowStressConfig()
        {
        }

        public HybridRowGeneratorConfig GeneratorConfig { get; set; }

        public bool Verbose { get; set; } = false;

        public bool BreakOnError { get; set; } = false;

        public long Iterations { get; set; } = -1;

        public long InnerLoopCount { get; set; } = 1;

        public bool GCBeforeInnerLoop { get; set; } = false;

        public int Seed { get; set; } = 42;

        public long ReportFrequency { get; set; } = 1000;

        public string NamespaceFile { get; set; } = null;

        public string TableSchemaName { get; set; } = null;

        public string ValueFile { get; set; } = null;

        public static HybridRowStressConfig FromArgs(string[] args)
        {
            CommandLineApplication command = new CommandLineApplication(throwOnUnexpectedArg: true)
            {
                Name = nameof(HybridRowStressProgram),
                Description = "HybridRow Stress.",
            };

            HybridRowStressConfig config = new HybridRowStressConfig
            {
                GeneratorConfig = new HybridRowGeneratorConfig(),
            };

            command.HelpOption("-? | -h | --help");
            CommandOption verboseOpt = command.Option("-verbose", "Display verbose output.  Default: false.", CommandOptionType.NoValue);
            CommandOption breakOnErrOpt = command.Option("-breakonerror", "Stop after the first error.  Default: false.", CommandOptionType.NoValue);
            CommandOption namespaceOpt = command.Option(
                "-namespace",
                "Force stress to used the fixed schema Namespace.  Default: null.",
                CommandOptionType.SingleValue);

            CommandOption tableNameOpt = command.Option(
                "-tablename",
                "The table schema (when using -namespace).  Default: null.",
                CommandOptionType.SingleValue);

            CommandOption valueOpt = command.Option(
                "-value",
                "Force stress to use the value encoded in the file as JSON.  Default: null.",
                CommandOptionType.SingleValue);

            CommandOption seedOpt = command.Option(
                "-seed",
                "<seed> Random number generator seed.  Default: 42.",
                CommandOptionType.SingleValue);

            CommandOption randomOpt = command.Option("-random", "Choose a random seed for the number generator.  Default: false.", CommandOptionType.NoValue);

            CommandOption iterationsOpt = command.Option(
                "-iterations",
                "<iterations> Number of test iterations.  Default: unlimited.",
                CommandOptionType.SingleValue);

            CommandOption innerloopOpt = command.Option(
                "-innerloop",
                "<count> Number of times to write a row in an inner.  Default: 1.",
                CommandOptionType.SingleValue);

            CommandOption gcOpt = command.Option("-gc", "Perform a forced GC before executing inner loop encoding.", CommandOptionType.NoValue);

            CommandOption reportFrequencyOpt = command.Option(
                "-reportfrequency",
                "<iterations> Report progress every N iterations.  Default: 1000.",
                CommandOptionType.SingleValue);

            command.OnExecute(
                () =>
                {
                    config.Verbose = verboseOpt.HasValue();
                    config.BreakOnError = breakOnErrOpt.HasValue();
                    if (namespaceOpt.HasValue())
                    {
                        config.NamespaceFile = namespaceOpt.Value();
                    }

                    if (tableNameOpt.HasValue())
                    {
                        config.TableSchemaName = tableNameOpt.Value();
                    }

                    if (valueOpt.HasValue())
                    {
                        config.ValueFile = valueOpt.Value();
                    }

                    if (seedOpt.HasValue())
                    {
                        config.Seed = int.Parse(seedOpt.Value());
                    }

                    if (randomOpt.HasValue())
                    {
                        config.Seed = unchecked((int)DateTime.UtcNow.Ticks);
                    }

                    if (iterationsOpt.HasValue())
                    {
                        config.Iterations = int.Parse(iterationsOpt.Value());
                    }

                    if (innerloopOpt.HasValue())
                    {
                        config.InnerLoopCount = int.Parse(innerloopOpt.Value());
                    }

                    config.GCBeforeInnerLoop = gcOpt.HasValue();

                    if (reportFrequencyOpt.HasValue())
                    {
                        config.ReportFrequency = long.Parse(reportFrequencyOpt.Value());
                        if (config.ReportFrequency <= 0)
                        {
                            throw new CommandParsingException(command, "Report Frequency cannot be negative.");
                        }
                    }

                    return 0;
                });

            command.Execute(args);
            if (command.IsShowingInformation)
            {
                throw new CommandParsingException(command, "Help");
            }

            return config;
        }
    }
}
