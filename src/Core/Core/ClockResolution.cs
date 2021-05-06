// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core
{
    using System;
    using System.Runtime.InteropServices;
    using System.Threading;

    public sealed class ClockResolution : IDisposable
    {
        private static readonly Timecaps TimeCapabilities;
        private readonly TimeSpan period;
        private int disposed;

        static ClockResolution()
        {
            int result = ClockResolution.TimeGetDevCaps(ref ClockResolution.TimeCapabilities, Marshal.SizeOf(typeof(Timecaps)));
            Contract.Assert(result == 0);
        }

        public ClockResolution(TimeSpan period)
        {
            int millis = Convert.ToInt32(period.TotalMilliseconds);
            millis = Math.Min(Math.Max(ClockResolution.TimeCapabilities.PeriodMin, millis), ClockResolution.TimeCapabilities.PeriodMax);

            int result = ClockResolution.TimeBeginPeriod(millis);
            Contract.Assert(result == 0);
            this.period = TimeSpan.FromMilliseconds(millis);
        }

        public static TimeSpan MinimumPeriod => TimeSpan.FromMilliseconds(ClockResolution.TimeCapabilities.PeriodMin);

        public static TimeSpan MaximumPeriod => TimeSpan.FromMilliseconds(ClockResolution.TimeCapabilities.PeriodMax);

        public TimeSpan Period => this.period;

        public void Dispose()
        {
            if (Interlocked.CompareExchange(ref this.disposed, 1, 0) == 0)
            {
                int millis = Convert.ToInt32(this.period.TotalMilliseconds);
                _ = ClockResolution.TimeEndPeriod(millis);
            }
        }

        [DllImport("winmm.dll", EntryPoint = "timeGetDevCaps")]
        private static extern int TimeGetDevCaps(ref Timecaps ptc, int cbtc);

        [DllImport("winmm.dll", EntryPoint = "timeBeginPeriod")]
        private static extern int TimeBeginPeriod(int uPeriod);

        [DllImport("winmm.dll", EntryPoint = "timeEndPeriod")]
        private static extern int TimeEndPeriod(int uPeriod);

        [StructLayout(LayoutKind.Sequential)]
        private struct Timecaps
        {
            public readonly int PeriodMin;

            public readonly int PeriodMax;
        }
    }
}
