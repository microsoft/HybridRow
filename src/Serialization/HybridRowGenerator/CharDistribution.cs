// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;

    public class CharDistribution
    {
        private readonly char min;
        private readonly char max;
        private readonly DistributionType type;

        public CharDistribution(char min, char max, DistributionType type = DistributionType.Uniform)
        {
            this.min = min;
            this.max = max;
            this.type = type;
        }

        public char Min => this.min;

        public char Max => this.max;

        public DistributionType Type => this.type;

        public char Next(RandomGenerator rand)
        {
            Contract.Requires(this.type == DistributionType.Uniform);

            return (char)rand.NextUInt16(this.min, this.max);
        }
    }
}
