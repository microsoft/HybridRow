// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1028 // Enum Storage should be Int32
#pragma warning disable CA1707 // Identifiers should not contain underscores

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.InteropServices;

    public static class ConsoleNative
    {
        [Flags]
        [SuppressMessage("Design", "CA1069:Enums values should not be duplicated", Justification = "Interop")]
        public enum ConsoleModes : uint
        {
            ENABLE_PROCESSED_INPUT = 0x0001,
            ENABLE_LINE_INPUT = 0x0002,
            ENABLE_ECHO_INPUT = 0x0004,
            ENABLE_WINDOW_INPUT = 0x0008,
            ENABLE_MOUSE_INPUT = 0x0010,
            ENABLE_INSERT_MODE = 0x0020,
            ENABLE_QUICK_EDIT_MODE = 0x0040,
            ENABLE_EXTENDED_FLAGS = 0x0080,
            ENABLE_AUTO_POSITION = 0x0100,

            ENABLE_PROCESSED_OUTPUT = 0x0001,
            ENABLE_WRAP_AT_EOL_OUTPUT = 0x0002,
            ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004,
            DISABLE_NEWLINE_AUTO_RETURN = 0x0008,
            ENABLE_LVB_GRID_WORLDWIDE = 0x0010,
        }

        private const int StdOutputHandle = -11;

        public static ConsoleModes Mode
        {
            get
            {
                ConsoleNative.GetConsoleMode(ConsoleNative.GetStdHandle(ConsoleNative.StdOutputHandle), out ConsoleModes mode);
                return mode;
            }

            set => ConsoleNative.SetConsoleMode(ConsoleNative.GetStdHandle(ConsoleNative.StdOutputHandle), value);
        }

        public static void SetBufferSize(int width, int height)
        {
            Coord size = new Coord
            {
                X = (short)width,
                Y = (short)height,
            };

            ConsoleNative.SetConsoleScreenBufferSize(ConsoleNative.GetStdHandle(ConsoleNative.StdOutputHandle), size);
        }

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern IntPtr GetStdHandle(int nStdHandle);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool SetConsoleMode(IntPtr hConsoleHandle, ConsoleModes dwMode);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool GetConsoleMode(IntPtr hConsoleHandle, out ConsoleModes lpMode);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool SetConsoleScreenBufferSize(IntPtr hConsoleHandle, Coord dwSize);

        [StructLayout(LayoutKind.Sequential)]
        private struct Coord
        {
            public short X;
            public short Y;
        }
    }
}
