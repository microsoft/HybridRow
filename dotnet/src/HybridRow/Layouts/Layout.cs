// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.CompilerServices;
    using System.Text;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    /// <summary>A Layout describes the structure of a Hybrd Row.</summary>
    /// <remarks>
    /// A layout indicates the number, order, and type of all schematized columns to be stored
    /// within a hybrid row.  The order and type of columns defines the physical ordering of bytes used to
    /// encode the row and impacts the cost of updating the row.
    /// <para />
    /// A layout is created by compiling a <see cref="Schema" /> through <see cref="Schema.Compile" /> or
    /// by constructor through a <see cref="LayoutBuilder" />.
    /// <para />
    /// <see cref="Layout" /> is immutable.
    /// </remarks>
    public sealed class Layout
    {
        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly Layout Empty = SystemSchema.LayoutResolver.Resolve(SystemSchema.EmptySchemaId);

        private readonly LayoutColumn[] topColumns;
        private readonly Dictionary<Utf8String, LayoutColumn> pathMap;
        private readonly Dictionary<string, LayoutColumn> pathStringMap;

        internal Layout(string name, SchemaId schemaId, int numBitmaskBytes, int minRequiredSize, List<LayoutColumn> columns)
        {
            this.Name = name;
            this.SchemaId = schemaId;
            this.NumBitmaskBytes = numBitmaskBytes;
            this.Size = minRequiredSize;
            this.Tokenizer = new StringTokenizer();
            this.pathMap = new Dictionary<Utf8String, LayoutColumn>(columns.Count, SamplingUtf8StringComparer.Default);
            this.pathStringMap = new Dictionary<string, LayoutColumn>(columns.Count);
            this.NumFixed = 0;
            this.NumVariable = 0;

            List<LayoutColumn> top = new List<LayoutColumn>(columns.Count);
            foreach (LayoutColumn c in columns)
            {
                this.Tokenizer.Add(c.Path);
                this.pathMap.Add(c.FullPath, c);
                this.pathStringMap.Add(c.FullPath.ToString(), c);
                if (c.Storage == StorageKind.Fixed)
                {
                    this.NumFixed++;
                }
                else if (c.Storage == StorageKind.Variable)
                {
                    this.NumVariable++;
                }

                if (c.Parent == null)
                {
                    top.Add(c);
                }
            }

            this.topColumns = top.ToArray();
        }

        /// <summary>Name of the layout.</summary>
        /// <remarks>
        /// Usually this is the name of the <see cref="Schema" /> from which this
        /// <see cref="Layout" /> was generated.
        /// </remarks>
        public string Name { get; }

        /// <summary>Unique identifier of the schema from which this <see cref="Layout" /> was generated.</summary>
        public SchemaId SchemaId { get; }

        /// <summary>The set of top level columns defined in the layout (in left-to-right order).</summary>
        public ReadOnlySpan<LayoutColumn> Columns => this.topColumns.AsSpan();

        /// <summary>Minimum required size of a row of this layout.</summary>
        /// <remarks>
        /// This size excludes all sparse columns, and assumes all columns (including variable) are
        /// null.
        /// </remarks>
        public int Size { get; }

        /// <summary>The number of bitmask bytes allocated within the layout.</summary>
        /// <remarks>
        /// A presence bit is allocated for each fixed and variable-length field.  Sparse columns
        /// never have presence bits.  Fixed boolean allocate an additional bit from the bitmask to store their
        /// value.
        /// </remarks>
        public int NumBitmaskBytes { get; }

        /// <summary>The number of fixed columns.</summary>
        public int NumFixed { get; }

        /// <summary>The number of variable-length columns.</summary>
        public int NumVariable { get; }

        /// <summary>A tokenizer for path strings.</summary>
        [SuppressMessage(
            "Microsoft.Performance",
            "CA1822:MarkMembersAsStatic",
            Justification = "Bug in Analyzer. This is an auto-property not a method.")]
        public StringTokenizer Tokenizer
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get;
        }

        /// <summary>Finds a column specification for a column with a matching path.</summary>
        /// <param name="path">The path of the column to find.</param>
        /// <param name="column">If found, the column specification, otherwise null.</param>
        /// <returns>True if a column with the path is found, otherwise false.</returns>
        public bool TryFind(UtfAnyString path, out LayoutColumn column)
        {
            if (path.IsNull)
            {
                column = default;
                return false;
            }

            if (path.IsUtf8)
            {
                return this.pathMap.TryGetValue(path.ToUtf8String(), out column);
            }

            return this.pathStringMap.TryGetValue(path, out column);
        }

        /// <summary>Finds a column specification for a column with a matching path.</summary>
        /// <param name="path">The path of the column to find.</param>
        /// <param name="column">If found, the column specification, otherwise null.</param>
        /// <returns>True if a column with the path is found, otherwise false.</returns>
        public bool TryFind(string path, out LayoutColumn column)
        {
            return this.pathStringMap.TryGetValue(path, out column);
        }

        /// <summary>Returns a human readable diagnostic string representation of this <see cref="Layout" />.</summary>
        /// <remarks>This representation should only be used for debugging and diagnostic purposes.</remarks>
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat("Layout:\n");
            sb.AppendFormat("\tCount: {0}\n", this.topColumns.Length);
            sb.AppendFormat("\tFixedSize: {0}\n", this.Size);
            foreach (LayoutColumn c in this.topColumns)
            {
                if (c.Type.IsFixed)
                {
                    if (c.Type.IsBool)
                    {
                        sb.AppendFormat("\t{0}: {1} @ {2}:{3}:{4}\n", c.FullPath, c.Type.Name, c.Offset, c.NullBit, c.BoolBit);
                    }
                    else
                    {
                        sb.AppendFormat("\t{0}: {1} @ {2}\n", c.FullPath, c.Type.Name, c.Offset);
                    }
                }
                else
                {
                    sb.AppendFormat("\t{0}: {1}[{3}] @ {2}\n", c.FullPath, c.Type.Name, c.Offset, c.Size);
                }
            }

            return sb.ToString();
        }
    }
}
