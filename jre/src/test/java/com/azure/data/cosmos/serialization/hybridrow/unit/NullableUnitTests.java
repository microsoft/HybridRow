// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;

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
        row.initLayout(HybridRowVersion.V1, this.layout, this.resolver);

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

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        this.WriteNullables(tempReference_row, RowCursor.Create(tempReference_row2, out _), t1);
        row = tempReference_row2.get();
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Nullables t2 = this.ReadNullables(tempReference_row3, RowCursor.Create(tempReference_row4, out _));
        row = tempReference_row4.get();
        row = tempReference_row3.get();
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
    private static <TValue> Result ReadNullable(Reference<RowBuffer> row,
                                                Reference<RowCursor> scope, TypeArgument itemType,
                                                Out<TValue> item,
                                                Out<RowCursor> nullableScope) {
        TValue value;
        Out<TValue> tempOut_value = new Out<TValue>();
        Result r = NullableUnitTests.ReadNullableImpl(row, scope, itemType.clone(), tempOut_value,
            nullableScope.clone());
        value = tempOut_value.get();
        if ((r != Result.Success) && (r != Result.NotFound)) {
            item.setAndGet(null);
            return r;
        }

        item.setAndGet((r == Result.NotFound) ? null : value);
        return Result.Success;
    }

    private static <TValue> Result ReadNullable(Reference<RowBuffer> row,
                                                Reference<RowCursor> scope, TypeArgument itemType,
                                                Out<TValue> item,
                                                Out<RowCursor> nullableScope) {
        Result r = NullableUnitTests.ReadNullableImpl(row, scope, itemType.clone(), item, nullableScope.clone());
        return (r == Result.NotFound) ? Result.Success : r;
    }

    private static <TValue> Result ReadNullableImpl(Reference<RowBuffer> row,
                                                    Reference<RowCursor> scope, TypeArgument itemType,
                                                    Out<TValue> item,
                                                    Out<RowCursor> nullableScope) {
        Result r = itemType.getType().<LayoutNullable>TypeAs().ReadScope(row, scope, nullableScope.clone());
        if (r != Result.Success) {
            item.setAndGet(null);
            return r;
        }

        if (RowCursors.moveNext(nullableScope.get().clone(), row)) {
            ResultAssert.IsSuccess(LayoutNullable.HasValue(row, nullableScope.clone()));
            return itemType.getTypeArgs().get(0).getType().<LayoutType<TValue>>TypeAs().ReadSparse(row,
                nullableScope.clone(), item);
        }

        ResultAssert.NotFound(LayoutNullable.HasValue(row, nullableScope.clone()));
        item.setAndGet(null);
        return Result.NotFound;
    }

    private Nullables ReadNullables(Reference<RowBuffer> row, Reference<RowCursor> root) {
        Nullables value = new Nullables();

        LayoutColumn c;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullbool", out c);
        RowCursor scope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullBool = new ArrayList<Boolean>();
            RowCursor nullableScope = null;
            Reference<RowCursor> tempReference_nullableScope =
                new Reference<RowCursor>(nullableScope);
            while (scope.MoveNext(row, tempReference_nullableScope)) {
                nullableScope = tempReference_nullableScope.get();
                Reference<RowCursor> tempReference_scope =
                    new Reference<RowCursor>(scope);
                Nullable<Boolean> item;
                Out<TValue> tempOut_item = new Out<TValue>();
                Out<RowCursor> tempOut_nullableScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_scope, c.TypeArgs[0], tempOut_item
                    , tempOut_nullableScope));
                nullableScope = tempOut_nullableScope.get();
                item = tempOut_item.get();
                scope = tempReference_scope.get();
                value.NullBool.add(item);
            }
            nullableScope = tempReference_nullableScope.get();
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullarray", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullArray = new ArrayList<Float>();
            RowCursor nullableScope = null;
            Reference<RowCursor> tempReference_nullableScope2 =
                new Reference<RowCursor>(nullableScope);
            while (scope.MoveNext(row, tempReference_nullableScope2)) {
                nullableScope = tempReference_nullableScope2.get();
                Reference<RowCursor> tempReference_scope2 =
                    new Reference<RowCursor>(scope);
                Nullable<Float> item;
                Out<TValue> tempOut_item2 = new Out<TValue>();
                Out<RowCursor> tempOut_nullableScope2 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_scope2, c.TypeArgs[0],
                    tempOut_item2, tempOut_nullableScope2));
                nullableScope = tempOut_nullableScope2.get();
                item = tempOut_item2.get();
                scope = tempReference_scope2.get();
                value.NullArray.add(item);
            }
            nullableScope = tempReference_nullableScope2.get();
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullset", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedSet>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullSet = new ArrayList<String>();
            RowCursor nullableScope = null;
            Reference<RowCursor> tempReference_nullableScope3 =
                new Reference<RowCursor>(nullableScope);
            while (scope.MoveNext(row, tempReference_nullableScope3)) {
                nullableScope = tempReference_nullableScope3.get();
                Reference<RowCursor> tempReference_scope3 =
                    new Reference<RowCursor>(scope);
                String item;
                Out<TValue> tempOut_item3 = new Out<TValue>();
                Out<RowCursor> tempOut_nullableScope3 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_scope3, c.TypeArgs[0],
                    tempOut_item3, tempOut_nullableScope3));
                nullableScope = tempOut_nullableScope3.get();
                item = tempOut_item3.get();
                scope = tempReference_scope3.get();
                value.NullSet.add(item);
            }
            nullableScope = tempReference_nullableScope3.get();
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nulltuple", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutTypedArray>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            value.NullTuple = new ArrayList<(Integer, Long) > ();
            RowCursor tupleScope = null;
            TypeArgument tupleType = c.TypeArgs[0];
            Reference<RowCursor> tempReference_tupleScope =
                new Reference<RowCursor>(tupleScope);
            while (scope.MoveNext(row, tempReference_tupleScope)) {
                tupleScope = tempReference_tupleScope.get();
                Reference<RowCursor> tempReference_scope4 =
                    new Reference<RowCursor>(scope);
                Out<RowCursor> tempOut_tupleScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row, tempReference_scope4,
                    tempOut_tupleScope));
                tupleScope = tempOut_tupleScope.get();
                scope = tempReference_scope4.get();

                assert RowCursors.moveNext(tupleScope.clone()
                    , row);
                Reference<RowCursor> tempReference_tupleScope2 =
                    new Reference<RowCursor>(tupleScope);
                Nullable<Integer> item1;
                Out<TValue> tempOut_item1 = new Out<TValue>();
                RowCursor nullableScope;
                Out<RowCursor> tempOut_nullableScope4 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_tupleScope2,
                    tupleType.getTypeArgs().get(0).clone(), tempOut_item1, tempOut_nullableScope4));
                nullableScope = tempOut_nullableScope4.get();
                item1 = tempOut_item1.get();
                tupleScope = tempReference_tupleScope2.get();
                Reference<RowCursor> tempReference_nullableScope4 =
                    new Reference<RowCursor>(nullableScope);
                assert RowCursors.moveNext(tupleScope.clone()
                    , row, tempReference_nullableScope4);
                nullableScope = tempReference_nullableScope4.get();
                Reference<RowCursor> tempReference_tupleScope3 =
                    new Reference<RowCursor>(tupleScope);
                Nullable<Long> item2;
                Out<TValue> tempOut_item2 = new Out<TValue>();
                Out<RowCursor> tempOut_nullableScope5 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_tupleScope3,
                    tupleType.getTypeArgs().get(1).clone(), tempOut_item2, tempOut_nullableScope5));
                nullableScope = tempOut_nullableScope5.get();
                item2 = tempOut_item2.get();
                tupleScope = tempReference_tupleScope3.get();

                Reference<RowCursor> tempReference_nullableScope5 =
                    new Reference<RowCursor>(nullableScope);
                assert !RowCursors.moveNext(tupleScope.clone(), row, tempReference_nullableScope5);
                nullableScope = tempReference_nullableScope5.get();
                value.NullTuple.add((item1, item2))
            }
            tupleScope = tempReference_tupleScope.get();
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert this.layout.TryFind("nullmap", out c);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out scope).Find(row, c.Path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        if (c.<LayoutUniqueScope>TypeAs().ReadScope(row, ref scope, out scope) == Result.Success) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: value.NullMap = new Dictionary<Guid, Nullable<byte>>();
            value.NullMap = new HashMap<UUID, Byte>();
            RowCursor tupleScope = null;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(ref scope);
            Reference<RowCursor> tempReference_tupleScope4 =
                new Reference<RowCursor>(tupleScope);
            while (scope.MoveNext(row, tempReference_tupleScope4)) {
                tupleScope = tempReference_tupleScope4.get();
                Reference<RowCursor> tempReference_scope5 =
                    new Reference<RowCursor>(scope);
                Out<RowCursor> tempOut_tupleScope2 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().ReadScope(row, tempReference_scope5,
                    tempOut_tupleScope2));
                tupleScope = tempOut_tupleScope2.get();
                scope = tempReference_scope5.get();

                assert RowCursors.moveNext(tupleScope.clone()
                    , row);
                Reference<RowCursor> tempReference_tupleScope5 =
                    new Reference<RowCursor>(tupleScope);
                Nullable<java.util.UUID> itemKey;
                Out<TValue> tempOut_itemKey = new Out<TValue>();
                RowCursor nullableScope;
                Out<RowCursor> tempOut_nullableScope6 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_tupleScope5,
                    tupleType.getTypeArgs().get(0).clone(), tempOut_itemKey, tempOut_nullableScope6));
                nullableScope = tempOut_nullableScope6.get();
                itemKey = tempOut_itemKey.get();
                tupleScope = tempReference_tupleScope5.get();

                Reference<RowCursor> tempReference_nullableScope6 =
                    new Reference<RowCursor>(nullableScope);
                assert RowCursors.moveNext(tupleScope.clone()
                    , row, tempReference_nullableScope6);
                nullableScope = tempReference_nullableScope6.get();
                Reference<RowCursor> tempReference_tupleScope6 =
                    new Reference<RowCursor>(tupleScope);
                Nullable<Byte> itemValue;
                Out<TValue> tempOut_itemValue = new Out<TValue>();
                Out<RowCursor> tempOut_nullableScope7 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(row, tempReference_tupleScope6,
                    tupleType.getTypeArgs().get(1).clone(), tempOut_itemValue, tempOut_nullableScope7));
                nullableScope = tempOut_nullableScope7.get();
                itemValue = tempOut_itemValue.get();
                tupleScope = tempReference_tupleScope6.get();

                Reference<RowCursor> tempReference_nullableScope7 =
                    new Reference<RowCursor>(nullableScope);
                assert !RowCursors.moveNext(tupleScope.clone(), row, tempReference_nullableScope7);
                nullableScope = tempReference_nullableScope7.get();
                value.NullMap.put(itemKey != null ? itemKey : UUID.Empty, itemValue);
            }
            tupleScope = tempReference_tupleScope4.get();
        }

        return value;
    }

    // TODO: C# TO JAVA CONVERTER: The C# 'struct' constraint has no equivalent in Java:
    //ORIGINAL LINE: private static Result WriteNullable<TValue>(ref RowBuffer row, ref RowCursor scope, TypeArgument
    // itemType, Nullable<TValue> item, out RowCursor nullableScope) where TValue : struct
    private static <TValue> Result WriteNullable(Reference<RowBuffer> row,
                                                 Reference<RowCursor> scope, TypeArgument itemType,
                                                 TValue item, Out<RowCursor> nullableScope) {
        return NullableUnitTests.WriteNullableImpl(row, scope, itemType.clone(), item != null, item != null ? item :
        default,nullableScope.clone())
    }

    private static <TValue> Result WriteNullable(Reference<RowBuffer> row,
                                                 Reference<RowCursor> scope, TypeArgument itemType,
                                                 TValue item, Out<RowCursor> nullableScope) {
        return NullableUnitTests.WriteNullableImpl(row, scope, itemType.clone(), item != null, item,
            nullableScope.clone());
    }

    private static <TValue> Result WriteNullableImpl(Reference<RowBuffer> row,
                                                     Reference<RowCursor> scope, TypeArgument itemType,
                                                     boolean hasValue, TValue item,
                                                     Out<RowCursor> nullableScope) {
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

    private void WriteNullables(Reference<RowBuffer> row, Reference<RowCursor> root,
                                Nullables value) {
        LayoutColumn c;

        if (value.NullBool != null) {
            Out<LayoutColumn> tempOut_c =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("nullbool", tempOut_c);
            c = tempOut_c.get();
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out outerScope).Find(row, c.getPath());
            Reference<RowCursor> tempReference_outerScope =
                new Reference<RowCursor>(outerScope);
            Out<RowCursor> tempOut_outerScope =
                new Out<RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, tempReference_outerScope,
                c.getTypeArgs().clone(), tempOut_outerScope));
            outerScope = tempOut_outerScope.get();
            outerScope = tempReference_outerScope.get();
            for (Boolean item : value.NullBool) {
                Reference<RowCursor> tempReference_outerScope2 =
                    new Reference<RowCursor>(outerScope);
                RowCursor innerScope;
                Out<RowCursor> tempOut_innerScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_outerScope2,
                    c.getTypeArgs().get(0).clone(), item, tempOut_innerScope));
                innerScope = tempOut_innerScope.get();
                outerScope = tempReference_outerScope2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !outerScope.MoveNext(row, ref innerScope);
            }
        }

        if (value.NullArray != null) {
            Out<LayoutColumn> tempOut_c2 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("nullarray", tempOut_c2);
            c = tempOut_c2.get();
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out outerScope).Find(row, c.getPath());
            Reference<RowCursor> tempReference_outerScope3 =
                new Reference<RowCursor>(outerScope);
            Out<RowCursor> tempOut_outerScope2 =
                new Out<RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, tempReference_outerScope3,
                c.getTypeArgs().clone(), tempOut_outerScope2));
            outerScope = tempOut_outerScope2.get();
            outerScope = tempReference_outerScope3.get();
            for (Float item : value.NullArray) {
                Reference<RowCursor> tempReference_outerScope4 =
                    new Reference<RowCursor>(outerScope);
                RowCursor innerScope;
                Out<RowCursor> tempOut_innerScope2 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_outerScope4,
                    c.getTypeArgs().get(0).clone(), item, tempOut_innerScope2));
                innerScope = tempOut_innerScope2.get();
                outerScope = tempReference_outerScope4.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !outerScope.MoveNext(row, ref innerScope);
            }
        }

        if (value.NullSet != null) {
            Out<LayoutColumn> tempOut_c3 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("nullset", tempOut_c3);
            c = tempOut_c3.get();
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out outerScope).Find(row, c.getPath());
            Reference<RowCursor> tempReference_outerScope5 =
                new Reference<RowCursor>(outerScope);
            Out<RowCursor> tempOut_outerScope3 =
                new Out<RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedSet>TypeAs().WriteScope(row, tempReference_outerScope5,
                c.getTypeArgs().clone(), tempOut_outerScope3));
            outerScope = tempOut_outerScope3.get();
            outerScope = tempReference_outerScope5.get();
            for (String item : value.NullSet) {
                RowCursor temp;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                RowCursor.CreateForAppend(row, out temp).Find(row, "");
                Reference<RowCursor> tempReference_temp =
                    new Reference<RowCursor>(temp);
                RowCursor _;
                Out<RowCursor> tempOut__ =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_temp,
                    c.getTypeArgs().get(0).clone(), item, tempOut__));
                _ = tempOut__.get();
                temp = tempReference_temp.get();
                Reference<RowCursor> tempReference_outerScope6 =
                    new Reference<RowCursor>(outerScope);
                Reference<RowCursor> tempReference_temp2 =
                    new Reference<RowCursor>(temp);
                ResultAssert.IsSuccess(c.<LayoutTypedSet>TypeAs().MoveField(row, tempReference_outerScope6,
                    tempReference_temp2));
                temp = tempReference_temp2.get();
                outerScope = tempReference_outerScope6.get();
            }
        }

        if (value.NullTuple != null) {
            Out<LayoutColumn> tempOut_c4 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("nulltuple", tempOut_c4);
            c = tempOut_c4.get();
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out outerScope).Find(row, c.getPath());
            Reference<RowCursor> tempReference_outerScope7 =
                new Reference<RowCursor>(outerScope);
            Out<RowCursor> tempOut_outerScope4 =
                new Out<RowCursor>();
            ResultAssert.IsSuccess(c.<LayoutTypedArray>TypeAs().WriteScope(row, tempReference_outerScope7,
                c.getTypeArgs().clone(), tempOut_outerScope4));
            outerScope = tempOut_outerScope4.get();
            outerScope = tempReference_outerScope7.get();
            for ((Integer item1,Long item2) :value.NullTuple)
            {
                TypeArgument tupleType = c.getTypeArgs().get(0).clone();
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().WriteScope(row, ref outerScope,
                    tupleType.getTypeArgs().clone(), out tupleScope));

                Reference<RowCursor> tempReference_tupleScope =
                    new Reference<RowCursor>(tupleScope);
                RowCursor nullableScope;
                Out<RowCursor> tempOut_nullableScope =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_tupleScope,
                    tupleType.getTypeArgs().get(0).clone(), item1, tempOut_nullableScope));
                nullableScope = tempOut_nullableScope.get();
                tupleScope = tempReference_tupleScope.get();

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert tupleScope.MoveNext(row, ref nullableScope);
                Reference<RowCursor> tempReference_tupleScope2 =
                    new Reference<RowCursor>(tupleScope);
                Out<RowCursor> tempOut_nullableScope2 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_tupleScope2,
                    tupleType.getTypeArgs().get(1).clone(), item2, tempOut_nullableScope2));
                nullableScope = tempOut_nullableScope2.get();
                tupleScope = tempReference_tupleScope2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !tupleScope.MoveNext(row, ref nullableScope);

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !outerScope.MoveNext(row, ref tupleScope);
            }
        }

        if (value.NullMap != null) {
            Out<LayoutColumn> tempOut_c5 =
                new Out<LayoutColumn>();
            assert this.layout.TryFind("nullmap", tempOut_c5);
            c = tempOut_c5.get();
            RowCursor outerScope;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out outerScope).Find(row, c.getPath());
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().WriteScope(row, ref outerScope,
                c.getTypeArgs().clone(), out outerScope));
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: foreach ((Guid key, Nullable<byte> itemValue) in value.NullMap)
            for ((UUID key,Byte itemValue) :value.NullMap)
            {
                Reference<RowCursor> tempReference_outerScope8 =
                    new Reference<RowCursor>(outerScope);
                TypeArgument tupleType = c.<LayoutUniqueScope>TypeAs().FieldType(tempReference_outerScope8).clone();
                outerScope = tempReference_outerScope8.get();
                RowCursor temp;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                RowCursor.CreateForAppend(row, out temp).Find(row, "");
                RowCursor tupleScope;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                ResultAssert.IsSuccess(tupleType.<LayoutIndexedScope>TypeAs().WriteScope(row, ref temp,
                    tupleType.getTypeArgs().clone(), out tupleScope));

                UUID itemKey = key.equals(UUID.Empty) ? null : key;
                Reference<RowCursor> tempReference_tupleScope3 =
                    new Reference<RowCursor>(tupleScope);
                RowCursor nullableScope;
                Out<RowCursor> tempOut_nullableScope3 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_tupleScope3,
                    tupleType.getTypeArgs().get(0).clone(), itemKey, tempOut_nullableScope3));
                nullableScope = tempOut_nullableScope3.get();
                tupleScope = tempReference_tupleScope3.get();

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert tupleScope.MoveNext(row, ref nullableScope);
                Reference<RowCursor> tempReference_tupleScope4 =
                    new Reference<RowCursor>(tupleScope);
                Out<RowCursor> tempOut_nullableScope4 =
                    new Out<RowCursor>();
                ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(row, tempReference_tupleScope4,
                    tupleType.getTypeArgs().get(1).clone(), itemValue, tempOut_nullableScope4));
                nullableScope = tempOut_nullableScope4.get();
                tupleScope = tempReference_tupleScope4.get();

                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                assert !tupleScope.MoveNext(row, ref nullableScope);

                Reference<RowCursor> tempReference_outerScope9 =
                    new Reference<RowCursor>(outerScope);
                Reference<RowCursor> tempReference_temp3 =
                    new Reference<RowCursor>(temp);
                ResultAssert.IsSuccess(c.<LayoutUniqueScope>TypeAs().MoveField(row, tempReference_outerScope9,
                    tempReference_temp3));
                temp = tempReference_temp3.get();
                outerScope = tempReference_outerScope9.get();
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