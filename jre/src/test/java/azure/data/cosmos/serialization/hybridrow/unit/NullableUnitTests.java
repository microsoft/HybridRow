//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// ReSharper disable StringLiteralTypo


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(NullableUnitTests.SchemaFile, "TestData")] public sealed class
// NullableUnitTests
public final class NullableUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final String SchemaFile = "TestData\\NullableSchema.json";
    private Layout layout;
    private LayoutResolver resolver;
    private Namespace schema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CreateNullables()
    public void CreateNullables() {
        RowBuffer row = new RowBuffer(NullableUnitTests.InitialRowSize);
        row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

        Nullables t1 = new Nullables();
        t1.NullBool = new ArrayList<Boolean>(Arrays.asList(true, false, null));
        t1.NullArray = new ArrayList<Float>(Arrays.asList(1.2F, null, 3.0F));
        t1.NullSet = new ArrayList<String>(Arrays.asList(null, "abc", "def"));
        t1.NullTuple = new ArrayList<(Integer, Long) > (Arrays.asList((1, 2), (null, 3),(4, null),(null, null)))
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: t1.NullMap = new Dictionary<System.Guid, Nullable<byte>> { { System.Guid.Parse
        // ("{00000000-0000-0000-0000-000000000000}"), 1 }, { System.Guid.Parse
        // ("{4674962B-CE11-4916-81C5-0421EE36F168}"), 20 }, { System.Guid.Parse
        // ("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), null }};
        t1.NullMap = new HashMap<UUID, Byte>(Map.ofEntries(Map.entry(UUID.fromString("{00000000-0000-0000-0000" +
            "-000000000000}"), 1), Map.entry(UUID.fromString("{4674962B-CE11-4916-81C5-0421EE36F168}"), 20),
            Map.entry(UUID.fromString("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), null)));

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        this.WriteNullables(tempRef_row, RowCursor.Create(tempRef_row2, out _), t1);
        row = tempRef_row2.argValue;
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Nullables t2 = this.ReadNullables(tempRef_row3, RowCursor.Create(tempRef_row4, out _));
        row = tempRef_row4.argValue;
        row = tempRef_row3.argValue;
        assert t1 == t2;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString(NullableUnitTests.SchemaFile);
        this.schema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.schema);
        this.layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Nullables")).SchemaId);
    }

    // TODO: C# TO JAVA CONVERTER: The C# 'struct' constraint has no equivalent in Java:
    //ORIGINAL LINE: private static Result ReadNullable<TValue>(ref RowBuffer row, ref RowCursor scope, TypeArgument
    // itemType, out Nullable<TValue> item, out RowCursor nullableScope) where TValue : struct
    private static <TValue> Result ReadNullable(tangible.RefObject<RowBuffer> row,
                                                tangible.RefObject<RowCursor> scope, TypeArgument itemType,
                                                tangible.OutObject<TValue> item,
                                                tangible.OutObject<RowCursor> nullableScope) {
        TValue value;
        tangible.OutObject<TValue> tempOut_value = new tangible.OutObject<TValue>();
        Result r = NullableUnitTests.ReadNullableImpl(row, scope, itemType.clone(), tempOut_value,
            nullableScope.clone());
        value = tempOut_value.argValue;
        if ((r != Result.Success) && (r != Result.NotFound)) {
            item.argValue = null;
            return r;
        }

        item.argValue = (r == Result.NotFound) ? null : value;
        return Result.Success;
    }

    private static <TValue> Result ReadNullable(tangible.RefObject<RowBuffer> row,
                                                tangible.RefObject<RowCursor> scope, TypeArgument itemType,
                                                tangible.OutObject<TValue> item,
                                                tangible.OutObject<RowCursor> nullableScope) {
        Result r = NullableUnitTests.ReadNullableImpl(row, scope, itemType.clone(), item, nullableScope.clone());
        return (r == Result.NotFound) ? Result.Success : r;
    }

    private static <TValue> Result ReadNullableImpl(tangible.RefObject<RowBuffer> row,
                                                    tangible.RefObject<RowCursor> scope, TypeArgument itemType,
                                                    tangible.OutObject<TValue> item,
                                                    tangible.OutObject<RowCursor> nullableScope) {
        Result r = itemType.getType().<LayoutNullable>TypeAs().ReadScope(row, scope, nullableScope.clone());
        if (r != Result.Success) {
            item.argValue = null;
            return r;
        }

        if (Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(nullableScope.argValue.clone(), row)) {
            ResultAssert.IsSuccess(LayoutNullable.HasValue(row, nullableScope.clone()));
            return itemType.getTypeArgs().get(0).getType().<LayoutType<TValue>>TypeAs().ReadSparse(row,
                nullableScope.clone(), item);
        }

        ResultAssert.NotFound(LayoutNullable.HasValue(row, nullableScope.clone()));
        item.argValue = null;
        return Result.NotFound;
    }

    private Nullables ReadNullables(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root) {
        Nullables value = new Nullables();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullbool", out c);
        RowCursor scope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullBool = new ArrayList<Boolean>();
            RowCursor nullableScope = null;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
            while (scope.MoveNext(row, tempRef_nullableScope)) {
                nullableScope = tempRef_nullableScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_scope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
                Nullable<Boolean> item;
                tangible.OutObject<TValue> tempOut_item = new tangible.OutObject<TValue>();
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_scope, c.TypeArgs[0], tempOut_item
                    , tempOut_nullableScope));
                nullableScope = tempOut_nullableScope.argValue;
                item = tempOut_item.argValue;
                scope = tempRef_scope.argValue;
                value.NullBool.add(item);
            }
            nullableScope = tempRef_nullableScope.argValue;
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullarray", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullArray = new ArrayList<Float>();
            RowCursor nullableScope = null;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope2 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
            while (scope.MoveNext(row, tempRef_nullableScope2)) {
                nullableScope = tempRef_nullableScope2.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_scope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
                Nullable<Float> item;
                tangible.OutObject<TValue> tempOut_item2 = new tangible.OutObject<TValue>();
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope2 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_scope2, c.TypeArgs[0],
                    tempOut_item2, tempOut_nullableScope2));
                nullableScope = tempOut_nullableScope2.argValue;
                item = tempOut_item2.argValue;
                scope = tempRef_scope2.argValue;
                value.NullArray.add(item);
            }
            nullableScope = tempRef_nullableScope2.argValue;
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullset", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedSet>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullSet = new ArrayList<String>();
            RowCursor nullableScope = null;
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
            while (scope.MoveNext(row, tempRef_nullableScope3)) {
                nullableScope = tempRef_nullableScope3.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_scope3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
                String item;
                tangible.OutObject<TValue> tempOut_item3 = new tangible.OutObject<TValue>();
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope3 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_scope3, c.TypeArgs[0],
                    tempOut_item3, tempOut_nullableScope3));
                nullableScope = tempOut_nullableScope3.argValue;
                item = tempOut_item3.argValue;
                scope = tempRef_scope3.argValue;
                value.NullSet.add(item);
            }
            nullableScope = tempRef_nullableScope3.argValue;
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nulltuple", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullTuple = new ArrayList<(Integer, Long) > ();
            RowCursor tupleScope = null;
            TypeArgument tupleType = c.TypeArgs[0];
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
            while (scope.MoveNext(row, tempRef_tupleScope)) {
                tupleScope = tempRef_tupleScope.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_scope4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row, tempRef_scope4,
                    tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.argValue;
                scope = tempRef_scope4.argValue;

                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone()
                    , row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                Nullable<Integer> item1;
                tangible.OutObject<TValue> tempOut_item1 = new tangible.OutObject<TValue>();
                RowCursor nullableScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope4 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_tupleScope2,
                    tupleType.getTypeArgs().get(0).clone(), tempOut_item1, tempOut_nullableScope4));
                nullableScope = tempOut_nullableScope4.argValue;
                item1 = tempOut_item1.argValue;
                tupleScope = tempRef_tupleScope2.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone()
                    , row, tempRef_nullableScope4);
                nullableScope = tempRef_nullableScope4.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                Nullable<Long> item2;
                tangible.OutObject<TValue> tempOut_item2 = new tangible.OutObject<TValue>();
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope5 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_tupleScope3,
                    tupleType.getTypeArgs().get(1).clone(), tempOut_item2, tempOut_nullableScope5));
                nullableScope = tempOut_nullableScope5.argValue;
                item2 = tempOut_item2.argValue;
                tupleScope = tempRef_tupleScope3.argValue;

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope5 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                assert !Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone(), row, tempRef_nullableScope5);
                nullableScope = tempRef_nullableScope5.argValue;
                value.NullTuple.add((item1, item2))
            }
            tupleScope = tempRef_tupleScope.argValue;
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullmap", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        root.argValue.Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'RefObject' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: value.NullMap = new Dictionary<Guid, Nullable<byte>>();
            value.NullMap = new HashMap<UUID, Byte>();
            RowCursor tupleScope = null;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref scope);
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope4 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
            while (scope.MoveNext(row, tempRef_tupleScope4)) {
                tupleScope = tempRef_tupleScope4.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_scope5 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_tupleScope2 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row, tempRef_scope5,
                    tempOut_tupleScope2));
                tupleScope = tempOut_tupleScope2.argValue;
                scope = tempRef_scope5.argValue;

                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone()
                    , row);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope5 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                Nullable<java.util.UUID> itemKey;
                tangible.OutObject<TValue> tempOut_itemKey = new tangible.OutObject<TValue>();
                RowCursor nullableScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope6 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_tupleScope5,
                    tupleType.getTypeArgs().get(0).clone(), tempOut_itemKey, tempOut_nullableScope6));
                nullableScope = tempOut_nullableScope6.argValue;
                itemKey = tempOut_itemKey.argValue;
                tupleScope = tempRef_tupleScope5.argValue;

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                assert Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone()
                    , row, tempRef_nullableScope6);
                nullableScope = tempRef_nullableScope6.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                Nullable<Byte> itemValue;
                tangible.OutObject<TValue> tempOut_itemValue = new tangible.OutObject<TValue>();
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope7 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempRef_tupleScope6,
                    tupleType.getTypeArgs().get(1).clone(), tempOut_itemValue, tempOut_nullableScope7));
                nullableScope = tempOut_nullableScope7.argValue;
                itemValue = tempOut_itemValue.argValue;
                tupleScope = tempRef_tupleScope6.argValue;

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_nullableScope7 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(nullableScope);
                assert !Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.MoveNext(tupleScope.clone(), row, tempRef_nullableScope7);
                nullableScope = tempRef_nullableScope7.argValue;
                value.NullMap.put(itemKey != null ? itemKey : UUID.Empty, itemValue);
            }
            tupleScope = tempRef_tupleScope4.argValue;
        }

        return value;
    }

    // TODO: C# TO JAVA CONVERTER: The C# 'struct' constraint has no equivalent in Java:
    //ORIGINAL LINE: private static Result WriteNullable<TValue>(ref RowBuffer row, ref RowCursor scope, TypeArgument
    // itemType, Nullable<TValue> item, out RowCursor nullableScope) where TValue : struct
    private static <TValue> Result WriteNullable(tangible.RefObject<RowBuffer> row,
                                                 tangible.RefObject<RowCursor> scope, TypeArgument itemType,
                                                 TValue item, tangible.OutObject<RowCursor> nullableScope) {
        return NullableUnitTests.WriteNullableImpl(row, scope, itemType.clone(), item != null, item != null ? item :
        default,nullableScope.clone())
    }

    private static <TValue> Result WriteNullable(tangible.RefObject<RowBuffer> row,
                                                 tangible.RefObject<RowCursor> scope, TypeArgument itemType,
                                                 TValue item, tangible.OutObject<RowCursor> nullableScope) {
        return NullableUnitTests.WriteNullableImpl(row, scope, itemType.clone(), item != null, item,
            nullableScope.clone());
    }

    private static <TValue> Result WriteNullableImpl(tangible.RefObject<RowBuffer> row,
                                                     tangible.RefObject<RowCursor> scope, TypeArgument itemType,
                                                     boolean hasValue, TValue item,
                                                     tangible.OutObject<RowCursor> nullableScope) {
        Result r = itemType.<LayoutNullable>TypeAs().WriteScope(row, scope, itemType.getTypeArgs().clone(), hasValue,
            nullableScope);

        if (r != Result.Success) {
            return r;
        }

        if (hasValue) {
            r = itemType.getTypeArgs().get(0).getType().<LayoutType<TValue>>TypeAs().WriteSparse(row,
                nullableScope.clone(), item);
            return r;
        }

        return Result.Success;
    }

    private void WriteNullables(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> root,
                                Nullables value) {
        LayoutColumn c;

        if (value.NullBool != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("nullbool", tempOut_c);
            c = tempOut_c.argValue;
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out outerScope).Find(row, c.getPath());
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_outerScope =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, tempRef_outerScope,
                c.getTypeArgs().clone(), tempOut_outerScope));
            outerScope = tempOut_outerScope.argValue;
            outerScope = tempRef_outerScope.argValue;
            for (Boolean item : value.NullBool) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
                RowCursor innerScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_outerScope2,
                    c.getTypeArgs().get(0).clone(), item, tempOut_innerScope));
                innerScope = tempOut_innerScope.argValue;
                outerScope = tempRef_outerScope2.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !outerScope.MoveNext(row, ref innerScope);
            }
        }

        if (value.NullArray != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c2 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("nullarray", tempOut_c2);
            c = tempOut_c2.argValue;
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out outerScope).Find(row, c.getPath());
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope3 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_outerScope2 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, tempRef_outerScope3,
                c.getTypeArgs().clone(), tempOut_outerScope2));
            outerScope = tempOut_outerScope2.argValue;
            outerScope = tempRef_outerScope3.argValue;
            for (Float item : value.NullArray) {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
                RowCursor innerScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_innerScope2 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_outerScope4,
                    c.getTypeArgs().get(0).clone(), item, tempOut_innerScope2));
                innerScope = tempOut_innerScope2.argValue;
                outerScope = tempRef_outerScope4.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !outerScope.MoveNext(row, ref innerScope);
            }
        }

        if (value.NullSet != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c3 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("nullset", tempOut_c3);
            c = tempOut_c3.argValue;
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out outerScope).Find(row, c.getPath());
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope5 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_outerScope3 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedSet>TypeAs().WriteScope(row, tempRef_outerScope5,
                c.getTypeArgs().clone(), tempOut_outerScope3));
            outerScope = tempOut_outerScope3.argValue;
            outerScope = tempRef_outerScope5.argValue;
            for (String item : value.NullSet) {
                RowCursor temp;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                RowCursor.CreateForAppend(row, out temp).Find(row, "");
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_temp =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(temp);
                RowCursor _;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut__ =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_temp,
                    c.getTypeArgs().get(0).clone(), item, tempOut__));
                _ = tempOut__.argValue;
                temp = tempRef_temp.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope6 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_temp2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(temp);
                ResultAssert.IsSuccess(c.<LayoutTypedSet>TypeAs().MoveField(row, tempRef_outerScope6, tempRef_temp2));
                temp = tempRef_temp2.argValue;
                outerScope = tempRef_outerScope6.argValue;
            }
        }

        if (value.NullTuple != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c4 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("nulltuple", tempOut_c4);
            c = tempOut_c4.argValue;
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out outerScope).Find(row, c.getPath());
            tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope7 =
                new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_outerScope4 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, tempRef_outerScope7,
                c.getTypeArgs().clone(), tempOut_outerScope4));
            outerScope = tempOut_outerScope4.argValue;
            outerScope = tempRef_outerScope7.argValue;
            for ((Integer item1,Long item2) :value.NullTuple)
            {
                TypeArgument tupleType = c.getTypeArgs().get(0).clone();
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().WriteScope(row, ref outerScope,
                    tupleType.getTypeArgs().clone(), out tupleScope));

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor nullableScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_tupleScope,
                    tupleType.getTypeArgs().get(0).clone(), item1, tempOut_nullableScope));
                nullableScope = tempOut_nullableScope.argValue;
                tupleScope = tempRef_tupleScope.argValue;

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert tupleScope.MoveNext(row, ref nullableScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope2 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_tupleScope2,
                    tupleType.getTypeArgs().get(1).clone(), item2, tempOut_nullableScope2));
                nullableScope = tempOut_nullableScope2.argValue;
                tupleScope = tempRef_tupleScope2.argValue;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !tupleScope.MoveNext(row, ref nullableScope);

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !outerScope.MoveNext(row, ref tupleScope);
            }
        }

        if (value.NullMap != null) {
            tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn> tempOut_c5 =
                new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn>();
            assert this.layout.TryFind("nullmap", tempOut_c5);
            c = tempOut_c5.argValue;
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            root.argValue.Clone(out outerScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'OutObject' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'RefObject' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref outerScope,
                c.getTypeArgs().clone(), out outerScope));
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: foreach ((Guid key, Nullable<byte> itemValue) in value.NullMap)
            for ((UUID key,Byte itemValue) :value.NullMap)
            {
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope8 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
                TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(tempRef_outerScope8).clone();
                outerScope = tempRef_outerScope8.argValue;
                RowCursor temp;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                RowCursor.CreateForAppend(row, out temp).Find(row, "");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().WriteScope(row, ref temp,
                    tupleType.getTypeArgs().clone(), out tupleScope));

                UUID itemKey = key.equals(UUID.Empty) ? null : key;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                RowCursor nullableScope;
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope3 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_tupleScope3,
                    tupleType.getTypeArgs().get(0).clone(), itemKey, tempOut_nullableScope3));
                nullableScope = tempOut_nullableScope3.argValue;
                tupleScope = tempRef_tupleScope3.argValue;

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert tupleScope.MoveNext(row, ref nullableScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_tupleScope4 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(tupleScope);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_nullableScope4 =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempRef_tupleScope4,
                    tupleType.getTypeArgs().get(1).clone(), itemValue, tempOut_nullableScope4));
                nullableScope = tempOut_nullableScope4.argValue;
                tupleScope = tempRef_tupleScope4.argValue;

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'RefObject' helper class unless the method is within the code
                // being modified:
                assert !tupleScope.MoveNext(row, ref nullableScope);

                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_outerScope9 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(outerScope);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_temp3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(temp);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempRef_outerScope9,
                    tempRef_temp3));
                temp = tempRef_temp3.argValue;
                outerScope = tempRef_outerScope9.argValue;
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] private sealed class Nullables
    private final static class Nullables {
        public ArrayList<Float> NullArray;
        public ArrayList<Boolean> NullBool;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: public Dictionary<Guid, Nullable<byte>> NullMap;
        public HashMap<UUID, Byte> NullMap;
		public ArrayList<(Integer,Long)>NullTuple
        public ArrayList<String> NullSet;

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Nullables;
            Nullables nullables = tempVar ? (Nullables)obj : null;
            return tempVar && this.equals(nullables);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                int hashCode = 0;
                hashCode = (hashCode * 397) ^ (this.NullBool == null ? null : this.NullBool.hashCode() != null ? this.NullBool.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.NullSet == null ? null : this.NullSet.hashCode() != null ? this.NullSet.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.NullArray == null ? null : this.NullArray.hashCode() != null ? this.NullArray.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.NullTuple == null ? null : this.NullTuple.hashCode() != null ? this.NullTuple.hashCode() : 0);
                hashCode = (hashCode * 397) ^ (this.NullMap == null ? null : this.NullMap.hashCode() != null ? this.NullMap.hashCode() : 0);
                return hashCode;
            }
        }

        private static <TKey, TValue> boolean MapEquals(HashMap<TKey, TValue> left, HashMap<TKey, TValue> right) {
            if (left.size() != right.size()) {
                return false;
            }

            for (Map.Entry<TKey, TValue> item : left.entrySet()) {
                TValue value;
                if (!(right.containsKey(item.getKey()) && (value = right.get(item.getKey())) == value)) {
                    return false;
                }

                if (!item.getValue().equals(value)) {
                    return false;
                }
            }

            return true;
        }

        private boolean equals(Nullables other) {
            //C# TO JAVA CONVERTER WARNING: Java AbstractList 'equals' is not always identical to LINQ 'SequenceEqual':
            //ORIGINAL LINE: return (object.ReferenceEquals(this.NullBool, other.NullBool) || ((this.NullBool != null) && (other.NullBool != null) && this.NullBool.SequenceEqual(other.NullBool))) && (object.ReferenceEquals(this.NullSet, other.NullSet) || ((this.NullSet != null) && (other.NullSet != null) && this.NullSet.SequenceEqual(other.NullSet))) && (object.ReferenceEquals(this.NullArray, other.NullArray) || ((this.NullArray != null) && (other.NullArray != null) && this.NullArray.SequenceEqual(other.NullArray))) && (object.ReferenceEquals(this.NullTuple, other.NullTuple) || ((this.NullTuple != null) && (other.NullTuple != null) && this.NullTuple.SequenceEqual(other.NullTuple))) && (object.ReferenceEquals(this.NullMap, other.NullMap) || ((this.NullMap != null) && (other.NullMap != null) && Nullables.MapEquals(this.NullMap, other.NullMap)));
            return (this.NullBool == other.NullBool || ((this.NullBool != null) && (other.NullBool != null) && this.NullBool.equals(other.NullBool))) && (this.NullSet == other.NullSet || ((this.NullSet != null) && (other.NullSet != null) && this.NullSet.equals(other.NullSet))) && (this.NullArray == other.NullArray || ((this.NullArray != null) && (other.NullArray != null) && this.NullArray.equals(other.NullArray))) && (this.NullTuple == other.NullTuple || ((this.NullTuple != null) && (other.NullTuple != null) && this.NullTuple.equals(other.NullTuple))) && (this.NullMap == other.NullMap || ((this.NullMap != null) && (other.NullMap != null) && Nullables.MapEquals(this.NullMap, other.NullMap)));
        }
    }
}