// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;

    public class IntDistribution
    {
        private readonly int min;
        private readonly int max;
        private readonly DistributionType type;

        public IntDistribution(int min, int max, DistributionType type = DistributionType.Uniform)
        {
            this.min = min;
            this.max = max;
            this.type = type;
        }

        public int Min => this.min;

        public int Max => this.max;

        public DistributionType Type => this.type;

        public int Next(RandomGenerator rand)
        {
            Contract.Requires(this.type == DistributionType.Uniform);

            return rand.NextInt32(this.min, this.max);
        }
    }
}
