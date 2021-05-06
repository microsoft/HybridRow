// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Globalization;
    using System.IO;
    using System.Linq;
    using System.Runtime.InteropServices;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Json;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.Extensions.CommandLineUtils;

    public class PrintCommand
    {
        private const int InitialCapacity = 2 * 1024 * 1024;
        private bool showSchema;

        private bool verbose;

        private bool interactive;

        private bool outputJson;

        private char outputJsonQuote;

        private string outputJsonIndent;

        private string namespaceFile;

        private List<string> rows;

        private PrintCommand()
        {
        }

        public static void AddCommand(CommandLineApplication app)
        {
            app.Command(
                "print",
                command =>
                {
                    command.Description = "Print hybrid row value(s).";
                    command.ExtendedHelpText =
                        "Convert binary serialized hybrid row value(s) to a printable string form using the given schema.\n\n" +
                        "The print command accepts files in three formats:\n" +
                        "\t* A HybridRow RecordIO file containing 0 or more records (with or without embedded schema).\n" +
                        "\t* A HybridRow binary file containing exactly 1 record. The length of the file indicates the record size.\n" +
                        "\t* A HybridRow text file containing exactly 1 record written as a HEX text string. The length of the file\n" +
                        "\t  indicates the length of the encoding.  All non-HEX characters (e.g. extra spaces, newlines, or dashes)\n" +
                        "\t  are ignored when decoding the file from HEX string to binary.";

                    command.HelpOption("-? | -h | --help");

                    CommandOption verboseOpt = command.Option("-v|--verbose", "Display verbose output.", CommandOptionType.NoValue);
                    CommandOption showSchemaOpt = command.Option(
                        "--show-schema",
                        "Include embedded the schema in the output.",
                        CommandOptionType.NoValue);

                    CommandOption interactiveOpt = command.Option("-i|--interactive", "Use interactive interface.", CommandOptionType.NoValue);
                    CommandOption jsonOpt = command.Option("-j|--json", "Output in JSON.", CommandOptionType.NoValue);
                    CommandOption jsonSingleQuoteOpt = command.Option("--json-single-quote", "Use single quotes in JSON.", CommandOptionType.NoValue);
                    CommandOption jsonNoIndentOpt = command.Option("--json-no-indent", "Don't indent in JSON.", CommandOptionType.NoValue);

                    CommandOption namespaceOpt = command.Option(
                        "-n|--namespace",
                        "File containing the schema namespace.",
                        CommandOptionType.SingleValue);

                    CommandArgument rowsOpt = command.Argument(
                        "rows",
                        "File(s) containing rows to print.",
                        arg => { arg.MultipleValues = true; });

                    command.OnExecute(
                        () =>
                        {
                            PrintCommand config = new PrintCommand
                            {
                                verbose = verboseOpt.HasValue(),
                                showSchema = showSchemaOpt.HasValue(),
                                outputJson = jsonOpt.HasValue(),
                                outputJsonQuote = jsonSingleQuoteOpt.HasValue() ? '\'' : '"',
                                outputJsonIndent = jsonNoIndentOpt.HasValue() ? null : "  ",
                                interactive = interactiveOpt.HasValue(),
                                namespaceFile = namespaceOpt.Value(),
                                rows = rowsOpt.Values
                            };

                            return config.OnExecuteAsync().Result;
                        });
                });
        }

        /// <summary>
        /// Read a <see cref="HybridRowHeader" /> from the current stream position without moving the
        /// position.
        /// </summary>
        /// <param name="stm">The stream to read from.</param>
        /// <returns>The header read at the current position.</returns>
        /// <exception cref="Exception">
        /// If a header cannot be read from the current stream position for any
        /// reason.
        /// </exception>
        /// <remarks>
        /// On success the stream's position is not changed. On error the stream position is
        /// undefined.
        /// </remarks>
        private static async Task<HybridRowHeader> PeekHybridRowHeaderAsync(Stream stm)
        {
            try
            {
                Memory<byte> bytes = await PrintCommand.ReadFixedAsync(stm, HybridRowHeader.Size);
                return MemoryMarshal.Read<HybridRowHeader>(bytes.Span);
            }
            finally
            {
                stm.Seek(-HybridRowHeader.Size, SeekOrigin.Current);
            }
        }

        /// <summary>Reads a fixed length segment from a stream.</summary>
        /// <param name="stm">The stream to read from.</param>
        /// <param name="length">The length to read in bytes.</param>
        /// <returns>A sequence of bytes read from the stream exactly <paramref name="length" /> long.</returns>
        /// <exception cref="Exception">
        /// if <paramref name="length" /> bytes cannot be read from the current
        /// stream position.
        /// </exception>
        private static async Task<Memory<byte>> ReadFixedAsync(Stream stm, int length)
        {
            Memory<byte> bytes = new byte[length].AsMemory();
            Memory<byte> active = bytes;
            int bytesRead;
            do
            {
                bytesRead = await stm.ReadAsync(active);
                active = active.Slice(bytesRead);
            }
            while (bytesRead != 0);

            if (active.Length != 0)
            {
                throw new Exception("Failed to parse row header");
            }

            return bytes;
        }

        private static void Refresh(ConsoleNative.ConsoleModes origMode, Layout layout, int length, long index, string str)
        {
            string[] lines = str.Split('\n');
            int height = lines.Length + 2;
            int width = (from line in lines select line.Length).Max();
            height = Math.Max(height, Console.WindowHeight);
            width = Math.Max(width, Console.WindowWidth);
            ConsoleNative.Mode = origMode & ~ConsoleNative.ConsoleModes.ENABLE_WRAP_AT_EOL_OUTPUT;
            Console.CursorVisible = false;
            ConsoleNative.SetBufferSize(width, height);

            Console.Clear();
            if (layout != null)
            {
                Console.ForegroundColor = ConsoleColor.White;
                Console.WriteLine($"[{index}] Schema: {layout.SchemaId} {layout.Name}, Length: {length}");
            }

            Console.ForegroundColor = ConsoleColor.Gray;

            Console.WriteLine(str);
            Console.SetWindowPosition(0, 0);
        }

        private static bool ShowInteractive(Layout layout, int length, long index, string str)
        {
            int origBufferHeight = Console.BufferHeight;
            int origBufferWidth = Console.BufferWidth;
            ConsoleNative.ConsoleModes origMode = ConsoleNative.Mode;
            try
            {
                PrintCommand.Refresh(origMode, layout, length, index, str);

                while (true)
                {
                    ConsoleKeyInfo cki = Console.ReadKey(true);
                    if ((cki.Modifiers & ConsoleModifiers.Alt) != 0 ||
                        (cki.Modifiers & ConsoleModifiers.Shift) != 0 ||
                        (cki.Modifiers & ConsoleModifiers.Control) != 0)
                    {
                        continue;
                    }

                    int top;
                    int left;
                    switch (cki.Key)
                    {
                        case ConsoleKey.Q:
                        case ConsoleKey.Escape:
                            return false;

                        case ConsoleKey.Spacebar:
                        case ConsoleKey.N:
                        case ConsoleKey.Enter:
                            return true;

                        case ConsoleKey.R:
                            PrintCommand.Refresh(origMode, layout, length, index, str);
                            break;

                        case ConsoleKey.Home:
                            Console.SetWindowPosition(0, 0);
                            break;

                        case ConsoleKey.End:
                            Console.SetWindowPosition(Console.WindowLeft, Console.BufferHeight - Console.WindowHeight);
                            break;

                        case ConsoleKey.PageDown:
                            top = Console.WindowTop + Console.WindowHeight;
                            top = Math.Min(top, Console.BufferHeight - Console.WindowHeight);
                            Console.SetWindowPosition(Console.WindowLeft, top);
                            break;

                        case ConsoleKey.PageUp:
                            top = Console.WindowTop - Console.WindowHeight;
                            top = Math.Max(top, 0);
                            Console.SetWindowPosition(Console.WindowLeft, top);
                            break;

                        case ConsoleKey.DownArrow:
                            top = Console.WindowTop + 1;
                            top = Math.Min(top, Console.BufferHeight - Console.WindowHeight);
                            Console.SetWindowPosition(Console.WindowLeft, top);
                            break;

                        case ConsoleKey.UpArrow:
                            top = Console.WindowTop - 1;
                            top = Math.Max(top, 0);
                            Console.SetWindowPosition(Console.WindowLeft, top);
                            break;

                        case ConsoleKey.RightArrow:
                            left = Console.WindowLeft + 1;
                            left = Math.Min(left, Console.BufferWidth - Console.WindowWidth);
                            Console.SetWindowPosition(left, Console.WindowTop);
                            break;

                        case ConsoleKey.LeftArrow:
                            left = Console.WindowLeft - 1;
                            left = Math.Max(left, 0);
                            Console.SetWindowPosition(left, Console.WindowTop);
                            break;
                    }
                }
            }
            finally
            {
                ConsoleNative.Mode = origMode;
                Console.ResetColor();
                origBufferWidth = Math.Max(Console.WindowWidth, origBufferWidth);
                origBufferHeight = Math.Max(Console.WindowHeight, origBufferHeight);
                Console.SetBufferSize(origBufferWidth, origBufferHeight);
                Console.Clear();
                Console.CursorVisible = true;
            }
        }

        /// <summary>Convert a text stream containing a sequence of HEX characters into a byte sequence.</summary>
        /// <param name="stm">The stream to read the text from.</param>
        /// <returns>The entire contents of the stream interpreted as HEX characters.</returns>
        /// <remarks>Any character that is not a HEX character is ignored.</remarks>
        private static async Task<Memory<byte>> HexTextStreamToBinaryAsync(Stream stm)
        {
            using TextReader reader = new StreamReader(stm);
            string rowAsText = await reader.ReadToEndAsync();
            string trimmed = string.Concat(
                from c in rowAsText
                where (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')
                select c);

            return ByteConverter.ToBytes(trimmed).AsMemory();
        }

        [SuppressMessage("Reliability", "CA2000:Dispose objects before losing scope", Justification = "Bug in CA rule.")]
        private async Task<int> OnExecuteAsync()
        {
            (_, LayoutResolver globalResolver) = await SchemaUtil.CreateResolverAsync(this.namespaceFile, this.verbose);
            MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(PrintCommand.InitialCapacity);
            foreach (string rowFile in this.rows)
            {
                await using Stream stm = new FileStream(rowFile, FileMode.Open);

                // Detect if it is a text or binary file via the encoding of the magic number at the
                // beginning of the HybridRow header.
                int magicNumber = stm.ReadByte();
                stm.Seek(-1, SeekOrigin.Current);
                if (magicNumber == -1)
                {
                    continue; // empty file
                }

                Result r = Result.Failure;
                if (magicNumber == (int)HybridRowVersion.V1)
                {
                    HybridRowHeader header = await PrintCommand.PeekHybridRowHeaderAsync(stm);
                    if (header.SchemaId == (SchemaId)SegmentHybridRowSerializer.SchemaId)
                    {
                        r = await this.PrintRecordIOAsync(stm, globalResolver, resizer);
                    }
                    else
                    {
                        Memory<byte> rowAsBinary = await PrintCommand.ReadFixedAsync(stm, (int)stm.Length);
                        r = this.PrintOneRow(rowAsBinary, 0, globalResolver);
                    }
                }
                else if (char.ToUpper((char)magicNumber, CultureInfo.InvariantCulture) == HybridRowVersion.V1.ToString("X")[0])
                {
                    // Convert hex text file to binary.
                    Memory<byte> rowAsBinary = await PrintCommand.HexTextStreamToBinaryAsync(stm);
                    r = this.PrintOneRow(rowAsBinary, 0, globalResolver);
                }

                if (r == Result.Canceled)
                {
                    return 0;
                }

                if (r != Result.Success)
                {
                    await Console.Error.WriteLineAsync($"Error reading row at {rowFile}");
                    return -1;
                }
            }

            return 0;
        }

        /// <summary>Print all records from a HybridRow RecordIO file.</summary>
        /// <param name="stm">Stream containing the HybridRow RecordIO content.</param>
        /// <param name="globalResolver">The global resolver to use when segments don't contain embedded SDL.</param>
        /// <param name="resizer">The resizer for allocating row buffers.</param>
        /// <returns>Success if the print is successful, an error code otherwise.</returns>
        private async Task<Result> PrintRecordIOAsync(Stream stm, LayoutResolver globalResolver, MemorySpanResizer<byte> resizer)
        {
            LayoutResolver segmentResolver = globalResolver;

            // Read a RecordIO stream.
            long index = 0;
            Result r = await stm.ReadRecordIOAsync(
                (ReadOnlyMemory<byte> record) => this.PrintOneRow(record, index++, segmentResolver),
                segment =>
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

                    Result r2 = ReadSegment(segment, globalResolver, out Segment s);
                    if (r2 != Result.Success)
                    {
                        return r2;
                    }

                    segmentResolver = string.IsNullOrWhiteSpace(s.SDL)
                        ? globalResolver
                        : SchemaUtil.LoadFromSdl(s.SDL, this.verbose, globalResolver).resolver;

                    if (this.showSchema)
                    {
                        string str;
                        switch (0)
                        {
                            case 0 when s.Schema != null:
                                str = "HrSchema Schema:\n" + Namespace.ToJson(s.Schema);
                                break;
                            case 0 when !string.IsNullOrWhiteSpace(s.SDL):
                                str = "JSON Schema:\n" + s.SDL;
                                break;
                            default:
                                str = "<empty>";
                                break;
                        }
                        if (!this.interactive)
                        {
                            Console.WriteLine(str);
                        }
                        else
                        {
                            if (!PrintCommand.ShowInteractive(null, 0, -1, str))
                            {
                                return Result.Canceled;
                            }
                        }
                    }

                    return Result.Success;
                },
                resizer);

            return r;
        }

        /// <summary>Print a single HybridRow record.</summary>
        /// <param name="buffer">The raw bytes of the row.</param>
        /// <param name="index">
        /// A 0-based index of the row relative to its outer container (for display
        /// purposes only).
        /// </param>
        /// <param name="resolver">The resolver for nested contents within the row.</param>
        /// <returns>Success if the print is successful, an error code otherwise.</returns>
        private Result PrintOneRow(ReadOnlyMemory<byte> buffer, long index, LayoutResolver resolver)
        {
            RowReader reader = new RowReader(buffer, HybridRowVersion.V1, resolver);
            Result r;
            string str;
            if (this.outputJson)
            {
                r = reader.ToJson(new RowReaderJsonSettings(this.outputJsonIndent, this.outputJsonQuote), out str);
            }
            else
            {
                r = DiagnosticConverter.ReaderToString(ref reader, out str);
            }

            if (r != Result.Success)
            {
                return r;
            }

            Layout layout = resolver.Resolve(reader.Header.SchemaId);
            if (!this.interactive)
            {
                Console.WriteLine($"Schema: {layout.SchemaId} {layout.Name}, Length: {reader.Length}");
                Console.WriteLine(str);
            }
            else
            {
                if (!PrintCommand.ShowInteractive(layout, reader.Length, index, str))
                {
                    return Result.Canceled;
                }
            }

            return Result.Success;
        }
    }
}
