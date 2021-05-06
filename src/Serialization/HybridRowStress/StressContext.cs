// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowStress
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Newtonsoft.Json;

    public class StressContext
    {
        private static readonly JsonSerializerSettings JsonSettings = new JsonSerializerSettings
        {
            NullValueHandling = NullValueHandling.Ignore,
            Formatting = Formatting.Indented,
        };

        public StressContext(long iteration, HybridRowStressConfig config)
        {
            Contract.Requires(config != null);

            this.Iteration = iteration;
            this.Config = config;
        }

        public long Iteration { get; }

        public long Errors { get; private set; }

        public HybridRowStressConfig Config { get; }

        public Namespace Namespace { get; set; }

        public Schema TableSchema { get; set; }

        public Dictionary<Utf8String, object> TableValue { get; set; }

        public bool IsSuccess(Result result)
        {
            if (result != Result.Success)
            {
                this.ReportFailure(Result.Success, result, string.Empty);
                return false;
            }

            return true;
        }

        public bool AreEqual<T>(T expected, T actual, string msg)
        {
            if (!actual.Equals(expected))
            {
                this.ReportFailure(expected, actual, msg);
                return false;
            }

            return true;
        }

        public bool IsNotNull(object actual, string msg)
        {
            if (actual == null)
            {
                this.ReportFailure("not null", "null", msg);
                return false;
            }

            return true;
        }

        public void Fail(Exception ex, string msg)
        {
            this.Errors++;
            this.ReportContext();
            Console.WriteLine("Failure: {0}\n{1}", msg, ex);
        }

        public void Trace(ref RowReader reader)
        {
            if (this.Config.Verbose)
            {
                Result result = DiagnosticConverter.ReaderToString(ref reader, out string str);
                if (result != Result.Success)
                {
                    this.Errors++;
                    this.ReportContext();
                    Console.WriteLine("Trace RowReader Failure: {0}", Enum.GetName(typeof(Result), result));
                    return;
                }

                Console.WriteLine(str);
            }
        }

        public void Trace<T>(T arg)
        {
            if (this.Config.Verbose)
            {
                Console.WriteLine(arg);
            }
        }

        public void Trace(string msg)
        {
            if (this.Config.Verbose)
            {
                Console.WriteLine(msg);
            }
        }

        public void Trace(string format, params object[] args)
        {
            if (this.Config.Verbose)
            {
                Console.WriteLine(format, args);
            }
        }

        private void ReportFailure<T>(T expected, T actual, string msg)
        {
            this.Errors++;
            this.ReportContext();
            Console.WriteLine("Failure: expected: '{0}', actual: '{1}' : {2}", expected, actual, msg);
        }

        private void ReportContext()
        {
            Console.WriteLine("Iteration: {0}", this.Iteration);
            Console.WriteLine("Config: {0}", JsonConvert.SerializeObject(this.Config, StressContext.JsonSettings));
            Console.WriteLine("Namespace: {0}", JsonConvert.SerializeObject(this.Namespace, StressContext.JsonSettings));
            Console.WriteLine("TableSchema: {0}", this.TableSchema.Name);
            Console.WriteLine("TableValue: {0}", JsonConvert.SerializeObject(this.TableValue, StressContext.JsonSettings));
        }
    }
}
