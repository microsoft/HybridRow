// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1137 // Elements should have the same indentation

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public class HybridRowValueGenerator
    {
        private readonly RandomGenerator rand;
        private readonly HybridRowGeneratorConfig config;

        public HybridRowValueGenerator(RandomGenerator rand, HybridRowGeneratorConfig config)
        {
            this.rand = rand;
            this.config = config;
        }

        public static bool DynamicTypeArgumentEquals(LayoutResolver resolver, object left, object right, TypeArgument typeArg)
        {
            if (left is null)
            {
                return right is null;
            }

            if (right is null)
            {
                return false;
            }

            switch (typeArg.Type.LayoutCode)
            {
                case LayoutCode.Null:
                case LayoutCode.Boolean:
                case LayoutCode.Int8:
                case LayoutCode.Int16:
                case LayoutCode.Int32:
                case LayoutCode.Int64:
                case LayoutCode.UInt8:
                case LayoutCode.UInt16:
                case LayoutCode.UInt32:
                case LayoutCode.UInt64:
                case LayoutCode.VarInt:
                case LayoutCode.VarUInt:
                case LayoutCode.Float32:
                case LayoutCode.Float64:
                case LayoutCode.Float128:
                case LayoutCode.Decimal:
                case LayoutCode.DateTime:
                case LayoutCode.UnixDateTime:
                case LayoutCode.Guid:
                case LayoutCode.MongoDbObjectId:
                case LayoutCode.Utf8:
                    return object.Equals(left, right);
                case LayoutCode.Binary:
                    return ((byte[])left).SequenceEqual((byte[])right);

                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                {
                    Dictionary<Utf8String, object> leftDict = (Dictionary<Utf8String, object>)left;
                    Dictionary<Utf8String, object> rightDict = (Dictionary<Utf8String, object>)right;
                    if (leftDict.Count != rightDict.Count)
                    {
                        return false;
                    }

                    // TODO: add properties to an object scope.
                    return true;
                }

                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                {
                    List<object> leftList = (List<object>)left;
                    List<object> rightList = (List<object>)right;
                    if (leftList.Count != rightList.Count)
                    {
                        return false;
                    }

                    for (int i = 0; i < leftList.Count; i++)
                    {
                        if (!HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, leftList[i], rightList[i], typeArg.TypeArgs[0]))
                        {
                            return false;
                        }
                    }

                    return true;
                }

                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                {
                    List<object> leftList = (List<object>)left;
                    List<object> rightList = (List<object>)right;
                    if (leftList.Count != rightList.Count)
                    {
                        return false;
                    }

                    List<object> working = new List<object>(leftList);
                    foreach (object rightItem in rightList)
                    {
                        int i = HybridRowValueGenerator.SetContains(resolver, working, rightItem, typeArg);
                        if (i == -1)
                        {
                            return false;
                        }

                        working.RemoveAt(i);
                    }

                    return true;
                }

                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                {
                    List<List<object>> leftList = (List<List<object>>)left;
                    List<List<object>> rightList = (List<List<object>>)right;
                    if (leftList.Count != rightList.Count)
                    {
                        return false;
                    }

                    List<object> working = new List<object>(leftList);
                    foreach (List<object> rightItem in rightList)
                    {
                        int i = HybridRowValueGenerator.MapContains(resolver, working, rightItem[0], typeArg);
                        if (i == -1)
                        {
                            return false;
                        }

                        List<object> leftItem = (List<object>)working[i];
                        if (!HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, leftItem[1], rightItem[1], typeArg.TypeArgs[1]))
                        {
                            return false;
                        }

                        working.RemoveAt(i);
                    }

                    return true;
                }

                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                {
                    List<object> leftList = (List<object>)left;
                    List<object> rightList = (List<object>)right;
                    if (leftList.Count != rightList.Count)
                    {
                        return false;
                    }

                    Contract.Assert(leftList.Count == typeArg.TypeArgs.Count);
                    for (int i = 0; i < leftList.Count; i++)
                    {
                        if (!HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, leftList[i], rightList[i], typeArg.TypeArgs[i]))
                        {
                            return false;
                        }
                    }

                    return true;
                }

                case LayoutCode.NullableScope:
                case LayoutCode.ImmutableNullableScope:
                {
                    Contract.Assert(typeArg.TypeArgs.Count == 1);
                    return HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, left, right, typeArg.TypeArgs[0]);
                }

                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                {
                    SchemaId schemaId = typeArg.TypeArgs.SchemaId;
                    Contract.Assert(schemaId != SchemaId.Invalid);
                    Layout layout = resolver.Resolve(schemaId);
                    Contract.Assert(layout != null);

                    Dictionary<Utf8String, object> leftDict = (Dictionary<Utf8String, object>)left;
                    Dictionary<Utf8String, object> rightDict = (Dictionary<Utf8String, object>)right;
                    if (leftDict.Count != rightDict.Count)
                    {
                        return false;
                    }

                    foreach (KeyValuePair<Utf8String, object> pair in leftDict)
                    {
                        if (!rightDict.TryGetValue(pair.Key, out object rightValue))
                        {
                            return false;
                        }

                        Contract.Requires(layout.TryFind(pair.Key, out LayoutColumn c));
                        return HybridRowValueGenerator.DynamicTypeArgumentEquals(
                            resolver,
                            pair.Value,
                            rightValue,
                            new TypeArgument(c.Type, c.TypeArgs));
                    }

                    return true;
                }

                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {typeArg}");
                    return false;
            }
        }

        public static T GenerateExclusive<T>(Func<T> op, IEnumerable<T> exclusions, int maxCandidates)
        {
            HashSet<T> exclusionSet = new HashSet<T>(exclusions);
            T candidate;

            int retryCount = 0;
            do
            {
                if (retryCount >= maxCandidates)
                {
                    throw new Exception(
                        $"Max Candidates Reached: {maxCandidates} : " +
                        string.Join(",", from e in exclusions select e.ToString()));
                }

                candidate = op();
                retryCount++;
            }
            while (exclusionSet.Contains(candidate));

            return candidate;
        }

        public string GenerateIdentifier()
        {
            int length = this.config.IdentifierLength.Next(this.rand);
            CharDistribution distribution = this.config.IdentifierCharacters;
            return this.GenerateString(length, distribution);
        }

        public unsafe string GenerateString(int length, CharDistribution distribution)
        {
            if (length == 0)
            {
                return string.Empty;
            }

            char* result = stackalloc char[length];
            int trim;
            do
            {
                for (int i = 0; i < length; i++)
                {
                    result[i] = distribution.Next(this.rand);
                }

                // Drop characters until we are under the encoded length.
                trim = length;
                while ((trim > 0) && Encoding.UTF8.GetByteCount(result, trim) > length)
                {
                    trim--;
                    result[trim] = '\0';
                }
            }
            while (trim == 0);

            // Pad with zero's if the resulting encoding is too short.
            while (Encoding.UTF8.GetByteCount(result, trim) < length)
            {
                trim++;
            }

            Contract.Assert(Encoding.UTF8.GetByteCount(result, trim) == length);
            return new string(result, 0, trim);
        }

        public byte[] GenerateBinary(int length)
        {
            byte[] result = new byte[length];
            for (int i = 0; i < length; i++)
            {
                result[i] = this.rand.NextUInt8();
            }

            return result;
        }

        public SchemaId GenerateSchemaId()
        {
            return new SchemaId(this.config.SchemaIds.Next(this.rand));
        }

        public StorageKind GenerateStorageKind()
        {
            return (StorageKind)this.config.FieldStorage.Next(this.rand);
        }

        public TypeKind GenerateTypeKind()
        {
            return (TypeKind)this.config.FieldType.Next(this.rand);
        }

        public string GenerateComment()
        {
            int length = this.config.CommentLength.Next(this.rand);
            CharDistribution distribution = this.config.UnicodeCharacters;
            return this.GenerateString(length, distribution);
        }

        public bool GenerateBool()
        {
            return this.rand.NextInt32(0, 1) != 0;
        }

        public object GenerateLayoutType(
            LayoutResolver resolver,
            TypeArgument typeArg,
            bool nullable = false,
            int length = 0,
            StorageKind storage = StorageKind.Sparse)
        {
            switch (typeArg.Type.LayoutCode)
            {
                case LayoutCode.Null:
                    return nullable && this.GenerateBool() ? (object)null : NullValue.Default;
                case LayoutCode.Boolean:
                    return nullable && this.GenerateBool() ? (object)null : this.GenerateBool();
                case LayoutCode.Int8:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextInt8();
                case LayoutCode.Int16:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextInt16();
                case LayoutCode.Int32:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextInt32();
                case LayoutCode.Int64:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextInt64();
                case LayoutCode.UInt8:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextUInt8();
                case LayoutCode.UInt16:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextUInt16();
                case LayoutCode.UInt32:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextUInt32();
                case LayoutCode.UInt64:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextUInt64();
                case LayoutCode.VarInt:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextInt64();
                case LayoutCode.VarUInt:
                    return nullable && this.GenerateBool() ? (object)null : this.rand.NextUInt64();
                case LayoutCode.Float32:
                    return nullable && this.GenerateBool() ? (object)null : (float)this.rand.NextInt32();
                case LayoutCode.Float64:
                    return nullable && this.GenerateBool() ? (object)null : (double)this.rand.NextInt64();
                case LayoutCode.Float128:
                    return nullable && this.GenerateBool() ? (object)null : new Float128(this.rand.NextInt64(), this.rand.NextInt64());
                case LayoutCode.Decimal:
                    return nullable && this.GenerateBool()
                        ? (object)null
                        : new decimal(
                            this.rand.NextInt32(),
                            this.rand.NextInt32(),
                            this.rand.NextInt32(),
                            this.GenerateBool(),
                            this.rand.NextUInt8(0, 28));
                case LayoutCode.DateTime:
                {
                    Contract.Assert(DateTime.MinValue.Ticks == 0);
                    long ticks = unchecked((long)(this.rand.NextUInt64() % (ulong)(DateTime.MaxValue.Ticks + 1)));
                    return nullable && this.GenerateBool() ? (object)null : new DateTime(ticks);
                }

                case LayoutCode.UnixDateTime:
                {
                    return nullable && this.GenerateBool() ? (object)null : new UnixDateTime(this.rand.NextInt64());
                }

                case LayoutCode.Guid:
                    return nullable && this.GenerateBool()
                        ? (object)null
                        : new Guid(
                            this.rand.NextInt32(),
                            this.rand.NextInt16(),
                            this.rand.NextInt16(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8(),
                            this.rand.NextUInt8());

                case LayoutCode.MongoDbObjectId:
                {
                    return nullable && this.GenerateBool() ? (object)null : new MongoDbObjectId(this.rand.NextUInt32(), this.rand.NextUInt64());
                }

                case LayoutCode.Utf8:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    switch (storage)
                    {
                        case StorageKind.Variable:
                            length = this.rand.NextInt32(0, length == 0 ? this.config.StringValueLength.Next(this.rand) : length);
                            break;
                        case StorageKind.Sparse:
                            length = this.config.StringValueLength.Next(this.rand);
                            break;
                    }

                    CharDistribution distribution = this.config.UnicodeCharacters;
                    return Utf8String.TranscodeUtf16(this.GenerateString(length, distribution));
                }

                case LayoutCode.Binary:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    switch (storage)
                    {
                        case StorageKind.Variable:
                            length = this.rand.NextInt32(0, length == 0 ? this.config.StringValueLength.Next(this.rand) : length);
                            break;
                        case StorageKind.Sparse:
                            length = this.config.StringValueLength.Next(this.rand);
                            break;
                    }

                    return this.GenerateBinary(length);
                }

                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    Dictionary<Utf8String, object> dict = new Dictionary<Utf8String, object>(SamplingUtf8StringComparer.Default);

                    // TODO: add properties to an object scope.
                    return dict;
                }

                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    Contract.Assert(typeArg.TypeArgs.Count == 1);
                    length = this.config.CollectionValueLength.Next(this.rand);
                    List<object> arrayValue = new List<object>(length);
                    for (int i = 0; i < length; i++)
                    {
                        arrayValue.Add(this.GenerateLayoutType(resolver, typeArg.TypeArgs[0]));
                    }

                    return arrayValue;
                }

                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    Contract.Assert(typeArg.TypeArgs.Count == 1);
                    length = this.config.CollectionValueLength.Next(this.rand);
                    List<object> setValue = new List<object>(length);
                    for (int i = 0; i < length; i++)
                    {
                        object value = this.GenerateLayoutType(resolver, typeArg.TypeArgs[0]);
                        if (HybridRowValueGenerator.SetContains(resolver, setValue, value, typeArg) == -1)
                        {
                            setValue.Add(value);
                        }
                    }

                    return setValue;
                }

                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    Contract.Assert(typeArg.TypeArgs.Count == 2);
                    length = this.config.CollectionValueLength.Next(this.rand);
                    List<object> mapValue = new List<object>(length);
                    for (int i = 0; i < length; i++)
                    {
                        object key = this.GenerateLayoutType(resolver, typeArg.TypeArgs[0]);
                        if (HybridRowValueGenerator.MapContains(resolver, mapValue, key, typeArg) == -1)
                        {
                            object value = this.GenerateLayoutType(resolver, typeArg.TypeArgs[1]);
                            mapValue.Add(new List<object> { key, value });
                        }
                    }

                    return mapValue;
                }

                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    List<object> tupleValue = new List<object>(typeArg.TypeArgs.Count);
                    foreach (TypeArgument t in typeArg.TypeArgs)
                    {
                        tupleValue.Add(this.GenerateLayoutType(resolver, t));
                    }

                    return tupleValue;
                }

                case LayoutCode.NullableScope:
                case LayoutCode.ImmutableNullableScope:
                {
                    if (this.GenerateBool())
                    {
                        return null;
                    }

                    return this.GenerateLayoutType(resolver, typeArg.TypeArgs[0]);
                }

                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                {
                    if (nullable && this.GenerateBool())
                    {
                        return null;
                    }

                    SchemaId schemaId = typeArg.TypeArgs.SchemaId;
                    Contract.Assert(schemaId != SchemaId.Invalid);
                    Layout layout = resolver.Resolve(schemaId);
                    Contract.Assert(layout != null);

                    Dictionary<Utf8String, object> dict = new Dictionary<Utf8String, object>(SamplingUtf8StringComparer.Default);
                    foreach (LayoutColumn c in layout.Columns)
                    {
                        dict[c.Path] = this.GenerateLayoutType(
                            resolver,
                            new TypeArgument(c.Type, c.TypeArgs),
                            length: c.Size,
                            storage: c.Storage);
                    }

                    return dict;
                }

                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {typeArg}");
                    return null;
            }
        }

        private static int SetContains(LayoutResolver resolver, List<object> set, object right, TypeArgument typeArg)
        {
            for (int i = 0; i < set.Count; i++)
            {
                object left = set[i];
                if (HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, left, right, typeArg.TypeArgs[0]))
                {
                    return i;
                }
            }

            return -1;
        }

        private static int MapContains(LayoutResolver resolver, List<object> map, object right, TypeArgument typeArg)
        {
            for (int i = 0; i < map.Count; i++)
            {
                List<object> pair = (List<object>)map[i];
                Contract.Assert(pair.Count == 2);
                if (HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, pair[0], right, typeArg.TypeArgs[0]))
                {
                    return i;
                }
            }

            return -1;
        }
    }
}
