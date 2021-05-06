// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.IO;
    using System.Runtime.InteropServices;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Extensions.CommandLineUtils;

    public class RowRewriterHybridRowCommand
    {
        private const int InitialCapacity = 2 * 1024 * 1024;
        private bool verbose;
        private string inputFile;
        private string outputFile;
        private string namespaceFile;

        private RowRewriterHybridRowCommand()
        {
        }

        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "rewrite",
                command =>
                {
                    command.Description = "Rewrite a HybridRow RecordIO document with a new schema.";
                    command.ExtendedHelpText = "Rewrite a HybridRow RecordIO document with a new schema." +
                                               command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.", CommandOptionType.NoValue);

                    CommandOption namespaceOpt = command.Option(
                        "-n|--namespace",
                        "File containing the schema namespace.",
                        CommandOptionType.SingleValue);

                    CommandArgument jsonOpt = command.Argument("input", "Input file containing rows to convert.");
                    CommandArgument outputOpt = command.Argument("output", "Output file to contain the conversion.");

                    command.OnExecute(
                        () =>
                        {
                            RowRewriterHybridRowCommand config = new RowRewriterHybridRowCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                namespaceFile = namespaceOpt.Value(),
                                inputFile = jsonOpt.Value,
                                outputFile = outputOpt.Value
                            };

                            return config.OnExecuteAsync().Result;
                        });
                });
        }

        private static (Result Result, Memory<byte> Block) FormatRow(ReadOnlyMemory<byte> body, MemorySpanResizer<byte> resizer)
        {
            Result r = RecordIOFormatter.FormatRecord(body, out RowBuffer row, resizer);
            if (r != Result.Success)
            {
                return (r, default);
            }

            return (Result.Success, resizer.Memory.Slice(0, row.Length));
        }

        private static (Result Result, Memory<byte> Metadata) FormatSegment(Segment s, MemorySpanResizer<byte> resizer)
        {
            Result r = RecordIOFormatter.FormatSegment(s, out RowBuffer row, resizer);
            if (r != Result.Success)
            {
                return (r, default);
            }

            return (r, resizer.Memory.Slice(0, row.Length));
        }

        private async Task<int> OnExecuteAsync()
        {
            (Namespace ns, LayoutResolver globalResolver) = await SchemaUtil.CreateResolverAsync(this.namespaceFile, this.verbose);
            MemorySpanResizer<byte> inResizer = new MemorySpanResizer<byte>(RowRewriterHybridRowCommand.InitialCapacity);
            MemorySpanResizer<byte> outResizer = new MemorySpanResizer<byte>(RowRewriterHybridRowCommand.InitialCapacity);
            await using Stream inStm = new FileStream(this.inputFile, FileMode.Open);
            await using Stream outputStm = new FileStream(this.outputFile, FileMode.Create);
            long totalWritten = 0;
            Result r1 = await inStm.ReadRecordIOAsync(
                async (record) =>
                {
                    (Result r, Memory<byte> metadata) = RowRewriterHybridRowCommand.FormatRow(record, outResizer);
                    if (r != Result.Success)
                    {
                        return r;
                    }
                    totalWritten++;

                    // Metadata and Body memory blocks should not overlap since they are both in
                    // play at the same time. If they do this usually means that the same resizer
                    // was incorrectly used for both. Check the resizer parameter passed to
                    // WriteRecordIOAsync for metadata.
                    Contract.Assert(!metadata.Span.Overlaps(record.Span));

                    await outputStm.WriteAsync(metadata);
                    await outputStm.WriteAsync(record);
                    return Result.Success;
                },
                async (segment) =>
                {
                    static Result ReadSegment(ReadOnlyMemory<byte> b, LayoutResolver r, out Segment s)
                    {
                        // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
                        // The cost-cast implied by MemoryMarshal.AsMemory is only safe here because:
                        // 1. Only READ operations are performed on the row.
                        // 2. The row is not allowed to escape this code.
                        RowBuffer row = new RowBuffer(MemoryMarshal.AsMemory(b).Span, HybridRowVersion.V1, r);
                        RowCursor root = RowCursor.Create(ref row);
                        return default(SegmentHybridRowSerializer).Read(ref row, ref root, true, out s);
                    }

                    Result r = ReadSegment(segment, globalResolver, out Segment s);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    // Apply the new schema.
                    if (!string.IsNullOrWhiteSpace(this.namespaceFile))
                    {
                        s.Schema = ns;
                    }
                    else if (!string.IsNullOrWhiteSpace(s.SDL))
                    {
                        s.Schema = Namespace.Parse(s.SDL);
                    }
                    s.SDL = null;

                    (Result r3, Memory<byte> metadata) = RowRewriterHybridRowCommand.FormatSegment(s, outResizer);
                    if (r3 != Result.Success)
                    {
                        return r3;
                    }

                    await outputStm.WriteAsync(metadata);
                    return Result.Success;
                },
                inResizer);

            if (r1 != Result.Success)
            {
                Console.WriteLine($"Error while writing record: {totalWritten} to HybridRow(s): {this.outputFile}");
                return -1;
            }

            Console.WriteLine($"Wrote ({totalWritten}) HybridRow(s): {this.outputFile}");
            return 0;
        }
    }
}
