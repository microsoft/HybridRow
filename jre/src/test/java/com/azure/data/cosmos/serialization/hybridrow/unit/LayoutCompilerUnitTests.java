//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBit;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

// ReSharper disable CommentTypo
// ReSharper disable StringLiteralTypo
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1201 // Elements should appear in the correct order
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable IDE0008 // Use explicit type


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here
// are anonymous.")] public class LayoutCompilerUnitTests
public class LayoutCompilerUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void PackNullAndBoolBits()
    public final void PackNullAndBoolBits() {
        // Test that null bits and bool bits are packed tightly in the layout.
        Schema s = new Schema();
        s.setName("TestSchema");
        s.setSchemaId(new SchemaId(1));
        s.setType(TypeKind.Schema);
        for (int i = 0; i < 32; i++) {
            Property tempVar = new Property();
            tempVar.setPath(String.valueOf(i));
            PrimitivePropertyType tempVar2 = new PrimitivePropertyType();
            tempVar2.setType(TypeKind.Boolean);
            tempVar2.setStorage(StorageKind.Fixed);
            tempVar.setPropertyType(tempVar2);
            s.getProperties().add(tempVar);

            Namespace tempVar3 = new Namespace();
            tempVar3.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
            Layout layout = s.Compile(tempVar3);
            Assert.IsTrue(layout.getSize() == LayoutBit.DivCeiling((i + 1) * 2, LayoutType.BitsPerByte), "Size: {0}, " +
                "i: {1}", layout.getSize(), i);
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaFixed()
    public final void ParseSchemaFixed() {
        // Test all fixed column types.
        RoundTripFixed.Expected tempVar = new RoundTripFixed.Expected();
        tempVar.TypeName = "null";
        tempVar.Default = new NullValue();
        tempVar.Value = NullValue.Default;
        RoundTripFixed.Expected tempVar2 = new RoundTripFixed.Expected();
        tempVar2.TypeName = "bool";
        tempVar2.Default = false;
        tempVar2.Value = false;
        RoundTripFixed.Expected tempVar3 = new RoundTripFixed.Expected();
        tempVar3.TypeName = "int8";
        tempVar3.Default = 0;
        tempVar3.Value = (byte)42;
        RoundTripFixed.Expected tempVar4 = new RoundTripFixed.Expected();
        tempVar4.TypeName = "int16";
        tempVar4.Default = 0;
        tempVar4.Value = (short)42;
        RoundTripFixed.Expected tempVar5 = new RoundTripFixed.Expected();
        tempVar5.TypeName = "int32";
        tempVar5.Default = 0;
        tempVar5.Value = 42;
        RoundTripFixed.Expected tempVar6 = new RoundTripFixed.Expected();
        tempVar6.TypeName = "int64";
        tempVar6.Default = 0;
        tempVar6.Value = 42L;
        RoundTripFixed.Expected tempVar7 = new RoundTripFixed.Expected();
        tempVar7.TypeName = "uint8";
        tempVar7.Default = 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: tempVar7.Value = (byte)42;
        tempVar7.Value = (byte)42;
        RoundTripFixed.Expected tempVar8 = new RoundTripFixed.Expected();
        tempVar8.TypeName = "uint16";
        tempVar8.Default = 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: tempVar8.Value = (ushort)42;
        tempVar8.Value = (short)42;
        RoundTripFixed.Expected tempVar9 = new RoundTripFixed.Expected();
        tempVar9.TypeName = "uint32";
        tempVar9.Default = 0;
        tempVar9.Value = 42;
        RoundTripFixed.Expected tempVar10 = new RoundTripFixed.Expected();
        tempVar10.TypeName = "uint64";
        tempVar10.Default = 0;
        tempVar10.Value = 42;
        RoundTripFixed.Expected tempVar11 = new RoundTripFixed.Expected();
        tempVar11.TypeName = "float32";
        tempVar11.Default = 0;
        tempVar11.Value = 4.2F;
        RoundTripFixed.Expected tempVar12 = new RoundTripFixed.Expected();
        tempVar12.TypeName = "float64";
        tempVar12.Default = 0;
        tempVar12.Value = 4.2;
        RoundTripFixed.Expected tempVar13 = new RoundTripFixed.Expected();
        tempVar13.TypeName = "float128";
        tempVar13.Default = new Float128();
        tempVar13.Value = new Float128(0, 42);
        RoundTripFixed.Expected tempVar14 = new RoundTripFixed.Expected();
        tempVar14.TypeName = "decimal";
        tempVar14.Default = new BigDecimal(0);
        tempVar14.Value = 4.2;
        RoundTripFixed.Expected tempVar15 = new RoundTripFixed.Expected();
        tempVar15.TypeName = "datetime";
        tempVar15.Default = LocalDateTime.MIN;
        tempVar15.Value = LocalDateTime.UtcNow;
        RoundTripFixed.Expected tempVar16 = new RoundTripFixed.Expected();
        tempVar16.TypeName = "unixdatetime";
        tempVar16.Default = new UnixDateTime();
        tempVar16.Value = new UnixDateTime(42);
        RoundTripFixed.Expected tempVar17 = new RoundTripFixed.Expected();
        tempVar17.TypeName = "guid";
        tempVar17.Default = null;
        tempVar17.Value = UUID.NewGuid();
        RoundTripFixed.Expected tempVar18 = new RoundTripFixed.Expected();
        tempVar18.TypeName = "mongodbobjectid";
        tempVar18.Default = new MongoDbObjectId();
        tempVar18.Value = new MongoDbObjectId(0, 42);
        RoundTripFixed.Expected tempVar19 = new RoundTripFixed.Expected();
        tempVar19.TypeName = "utf8";
        tempVar19.Default = "\0\0";
        tempVar19.Value = "AB";
        tempVar19.Length = 2;
        RoundTripFixed.Expected tempVar20 = new RoundTripFixed.Expected();
        tempVar20.TypeName = "binary";
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: tempVar20.Default = new byte[] { 0x00, 0x00 };
        tempVar20.Default = new byte[] { 0x00, 0x00 };
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: tempVar20.Value = new byte[] { 0x01, 0x02 };
        tempVar20.Value = new byte[] { 0x01, 0x02 };
        tempVar20.Length = 2;
        RoundTripFixed.Expected[] expectedSchemas =
            new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripFixed.Expected[] { tempVar, tempVar2, tempVar3, tempVar4, tempVar5, tempVar6, tempVar7, tempVar8, tempVar9, tempVar10, tempVar11, tempVar12, tempVar13, tempVar14, tempVar15, tempVar16, tempVar17, tempVar18, tempVar19, tempVar20 };

        RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
        for (String nullable : new String[] { "true", "false" }) {
            for (RoundTripFixed.Expected exp : expectedSchemas) {
                RoundTripFixed.Expected expected = exp.clone();
                String typeSchema = String.format("{'type': '%1$s', 'storage': 'fixed', 'length': %2$s, 'nullable': " +
                    "%3$s", expected.TypeName, expected.Length, nullable
            }
        })
        expected.Json = typeSchema;
        String propSchema = String.format("{'path': 'a', 'type': %1$s", typeSchema
    }
});
    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",propSchema);
    try
    {
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar21=new Namespace();
    tempVar21.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar21);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    Assert.AreEqual(1,layout.getColumns().Length,"Json: {0}",expected.Json);
    Assert.AreEqual(s.getName(),layout.getName(),"Json: {0}",expected.Json);
    Assert.IsTrue(layout.toString().length()>0,"Json: {0}",expected.Json);
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out col);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Fixed,col.Storage,"Json: {0}",expected.Json);
    Assert.AreEqual(expected.Length==0,col.Type.IsFixed,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripFixed.Closure tempVar22=new RoundTripFixed.Closure();
    tempVar22.Col=col;
    tempVar22.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripFixed, RoundTripFixed.Closure>LayoutCodeSwitch(col.Type.LayoutCode,tempRef_row2,tempRef_root,
    tempVar22.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    catch(LayoutCompilationException e)
    {
    assert expected.TypeName.equals("null");
    assert"false"==nullable;
    }
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaVariable()
public final void ParseSchemaVariable()
    {
    // Helper functions to create sample arrays.
    // TODO: C# TO JAVA CONVERTER: Local functions are not converted by C# to Java Converter:
    //		string MakeS(int size)
    //			{
    //				StringBuilder ret = new StringBuilder(size);
    //				for (int i = 0; i < size; i++)
    //				{
    //					ret.Append(unchecked((char)('a' + (i % 26)))); // allow wrapping (this also allows \0 chars)
    //				}
    //
    //				return ret.ToString();
    //			}

    // TODO: C# TO JAVA CONVERTER: Local functions are not converted by C# to Java Converter:
    //		byte[] MakeB(int size)
    //			{
    //				byte[] ret = new byte[size];
    //				for (int i = 0; i < size; i++)
    //				{
    //					ret[i] = unchecked((byte)(i + 1)); // allow wrapping
    //				}
    //
    //				return ret;
    //			}

    // Test all variable column types.
    RoundTripVariable.Expected tempVar=new RoundTripVariable.Expected();
    tempVar.Json="{'type': 'utf8', 'storage': 'variable', 'length': 100}";
    tempVar.Short=MakeS(2);
    tempVar.Value=MakeS(20);
    tempVar.Long=MakeS(100);
    tempVar.TooBig=MakeS(200);
    RoundTripVariable.Expected tempVar2=new RoundTripVariable.Expected();
    tempVar2.Json="{'type': 'binary', 'storage': 'variable', 'length': 100}";
    tempVar2.Short=MakeB(2);
    tempVar2.Value=MakeB(20);
    tempVar2.Long=MakeB(100);
    tempVar2.TooBig=MakeB(200);
    RoundTripVariable.Expected tempVar3=new RoundTripVariable.Expected();
    tempVar3.Json="{'type': 'varint', 'storage': 'variable'}";
    tempVar3.Short=1L;
    tempVar3.Value=255L;
    tempVar3.Long=Long.MAX_VALUE;
    RoundTripVariable.Expected tempVar4=new RoundTripVariable.Expected();
    tempVar4.Json="{'type': 'varuint', 'storage': 'variable'}";
    tempVar4.Short=1;
    tempVar4.Value=255;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar4.Long = ulong.MaxValue;
    tempVar4.Long=Long.MAX_VALUE;
    RoundTripVariable.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripVariable.Expected[]{tempVar,tempVar2,tempVar3,tempVar4};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripVariable.Expected expected:expectedSchemas)
    {
    String propSchema=String.format("{'path': 'a', 'type': %1$s, ",expected.Json}})+"\r\n"+
    "                                       {{'path': 'b', 'type': {expected.Json}}}, "+"\r\n"+
    "                                       {{'path': 'c', 'type': {expected.Json}}}";

    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",propSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar5=new Namespace();
    tempVar5.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar5);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out col);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    assert col.Type.AllowVariable;
    Assert.AreEqual(StorageKind.Variable,col.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripVariable.Closure tempVar6=new RoundTripVariable.Closure();
    tempVar6.Layout=layout;
    tempVar6.Col=col;
    tempVar6.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<VariableInterleaving, RoundTripVariable.Closure>LayoutCodeSwitch(col.Type.LayoutCode,tempRef_row2,
    tempRef_root,tempVar6.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void SparseOrdering()
public final void SparseOrdering()
    {
    // Test various orderings of multiple sparse column types.
    RoundTripSparseOrdering.Expected tempVar=new RoundTripSparseOrdering.Expected();
    tempVar.Path="a";
    tempVar.Type=LayoutType.Utf8;
    tempVar.Value="aa";
    RoundTripSparseOrdering.Expected tempVar2=new RoundTripSparseOrdering.Expected();
    tempVar2.Path="b";
    tempVar2.Type=LayoutType.Utf8;
    tempVar2.Value="bb";
    RoundTripSparseOrdering.Expected tempVar3=new RoundTripSparseOrdering.Expected();
    tempVar3.Path="a";
    tempVar3.Type=LayoutType.VarInt;
    tempVar3.Value=42L;
    RoundTripSparseOrdering.Expected tempVar4=new RoundTripSparseOrdering.Expected();
    tempVar4.Path="b";
    tempVar4.Type=LayoutType.Int64;
    tempVar4.Value=43L;
    RoundTripSparseOrdering.Expected tempVar5=new RoundTripSparseOrdering.Expected();
    tempVar5.Path="a";
    tempVar5.Type=LayoutType.VarInt;
    tempVar5.Value=42L;
    RoundTripSparseOrdering.Expected tempVar6=new RoundTripSparseOrdering.Expected();
    tempVar6.Path="b";
    tempVar6.Type=LayoutType.Utf8;
    tempVar6.Value="aa";
    RoundTripSparseOrdering.Expected tempVar7=new RoundTripSparseOrdering.Expected();
    tempVar7.Path="c";
    tempVar7.Type=LayoutType.Null;
    tempVar7.Value=NullValue.Default;
    RoundTripSparseOrdering.Expected tempVar8=new RoundTripSparseOrdering.Expected();
    tempVar8.Path="d";
    tempVar8.Type=LayoutType.Boolean;
    tempVar8.Value=true;
    RoundTripSparseOrdering.Expected[][]expectedOrders=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseOrdering.Expected[][]
    {
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseOrdering.Expected[]{tempVar,tempVar2},
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseOrdering.Expected[]{tempVar3,tempVar4},
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseOrdering.Expected[]{tempVar5,tempVar6,tempVar7,tempVar8}
    };

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseOrdering.Expected[]expectedSet:expectedOrders)
    {
    for(java.lang.Iterable<RoundTripSparseOrdering.Expected>permutation:expectedSet.Permute())
    {
    // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to LINQ query syntax:
    String json=tangible.StringHelper.join(", ",from p in permutation select p.Path+": "+p.Type.Name);
    System.out.printf("%1$s"+"\r\n",json);

    row.Reset();
    row.InitLayout(HybridRowVersion.V1,Layout.Empty,SystemSchema.LayoutResolver);
    for(RoundTripSparseOrdering.Expected field:permutation)
    {
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripSparseOrdering.Closure tempVar9=new RoundTripSparseOrdering.Closure();
    tempVar9.Expected=field.clone();
    tempVar9.Json=json;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripSparseOrdering, RoundTripSparseOrdering.Closure>LayoutCodeSwitch(field.Type.LayoutCode,
    tempRef_row2,tempRef_root,tempVar9.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaSparseSimple()
public final void ParseSchemaSparseSimple()
    {
    // Test all sparse column types.
    RoundTripSparseSimple.Expected tempVar=new RoundTripSparseSimple.Expected();
    tempVar.Json="{'type': 'null', 'storage': 'sparse'}";
    tempVar.Value=NullValue.Default;
    RoundTripSparseSimple.Expected tempVar2=new RoundTripSparseSimple.Expected();
    tempVar2.Json="{'type': 'bool', 'storage': 'sparse'}";
    tempVar2.Value=true;
    RoundTripSparseSimple.Expected tempVar3=new RoundTripSparseSimple.Expected();
    tempVar3.Json="{'type': 'bool', 'storage': 'sparse'}";
    tempVar3.Value=false;
    RoundTripSparseSimple.Expected tempVar4=new RoundTripSparseSimple.Expected();
    tempVar4.Json="{'type': 'int8', 'storage': 'sparse'}";
    tempVar4.Value=(byte)42;
    RoundTripSparseSimple.Expected tempVar5=new RoundTripSparseSimple.Expected();
    tempVar5.Json="{'type': 'int16', 'storage': 'sparse'}";
    tempVar5.Value=(short)42;
    RoundTripSparseSimple.Expected tempVar6=new RoundTripSparseSimple.Expected();
    tempVar6.Json="{'type': 'int32', 'storage': 'sparse'}";
    tempVar6.Value=42;
    RoundTripSparseSimple.Expected tempVar7=new RoundTripSparseSimple.Expected();
    tempVar7.Json="{'type': 'int64', 'storage': 'sparse'}";
    tempVar7.Value=42L;
    RoundTripSparseSimple.Expected tempVar8=new RoundTripSparseSimple.Expected();
    tempVar8.Json="{'type': 'uint8', 'storage': 'sparse'}";
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar8.Value = (byte)42;
    tempVar8.Value=(byte)42;
    RoundTripSparseSimple.Expected tempVar9=new RoundTripSparseSimple.Expected();
    tempVar9.Json="{'type': 'uint16', 'storage': 'sparse'}";
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar9.Value = (ushort)42;
    tempVar9.Value=(short)42;
    RoundTripSparseSimple.Expected tempVar10=new RoundTripSparseSimple.Expected();
    tempVar10.Json="{'type': 'uint32', 'storage': 'sparse'}";
    tempVar10.Value=42;
    RoundTripSparseSimple.Expected tempVar11=new RoundTripSparseSimple.Expected();
    tempVar11.Json="{'type': 'uint64', 'storage': 'sparse'}";
    tempVar11.Value=42;
    RoundTripSparseSimple.Expected tempVar12=new RoundTripSparseSimple.Expected();
    tempVar12.Json="{'type': 'varint', 'storage': 'sparse'}";
    tempVar12.Value=42L;
    RoundTripSparseSimple.Expected tempVar13=new RoundTripSparseSimple.Expected();
    tempVar13.Json="{'type': 'varuint', 'storage': 'sparse'}";
    tempVar13.Value=42;
    RoundTripSparseSimple.Expected tempVar14=new RoundTripSparseSimple.Expected();
    tempVar14.Json="{'type': 'float32', 'storage': 'sparse'}";
    tempVar14.Value=4.2F;
    RoundTripSparseSimple.Expected tempVar15=new RoundTripSparseSimple.Expected();
    tempVar15.Json="{'type': 'float64', 'storage': 'sparse'}";
    tempVar15.Value=4.2;
    RoundTripSparseSimple.Expected tempVar16=new RoundTripSparseSimple.Expected();
    tempVar16.Json="{'type': 'float128', 'storage': 'sparse'}";
    tempVar16.Value=new Float128(0,42);
    RoundTripSparseSimple.Expected tempVar17=new RoundTripSparseSimple.Expected();
    tempVar17.Json="{'type': 'decimal', 'storage': 'sparse'}";
    tempVar17.Value=4.2;
    RoundTripSparseSimple.Expected tempVar18=new RoundTripSparseSimple.Expected();
    tempVar18.Json="{'type': 'datetime', 'storage': 'sparse'}";
    tempVar18.Value=LocalDateTime.UtcNow;
    RoundTripSparseSimple.Expected tempVar19=new RoundTripSparseSimple.Expected();
    tempVar19.Json="{'type': 'unixdatetime', 'storage': 'sparse'}";
    tempVar19.Value=new UnixDateTime(42);
    RoundTripSparseSimple.Expected tempVar20=new RoundTripSparseSimple.Expected();
    tempVar20.Json="{'type': 'guid', 'storage': 'sparse'}";
    tempVar20.Value=UUID.NewGuid();
    RoundTripSparseSimple.Expected tempVar21=new RoundTripSparseSimple.Expected();
    tempVar21.Json="{'type': 'mongodbobjectid', 'storage': 'sparse'}";
    tempVar21.Value=new MongoDbObjectId(0,42);
    RoundTripSparseSimple.Expected tempVar22=new RoundTripSparseSimple.Expected();
    tempVar22.Json="{'type': 'utf8', 'storage': 'sparse'}";
    tempVar22.Value="AB";
    RoundTripSparseSimple.Expected tempVar23=new RoundTripSparseSimple.Expected();
    tempVar23.Json="{'type': 'binary', 'storage': 'sparse'}";
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar23.Value = new byte[] { 0x01, 0x02 };
    tempVar23.Value=new byte[]{0x01,0x02};
    RoundTripSparseSimple.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseSimple.Expected[]{tempVar,tempVar2,tempVar3,tempVar4,tempVar5,tempVar6,tempVar7,tempVar8,tempVar9,tempVar10,tempVar11,tempVar12,tempVar13,tempVar14,tempVar15,tempVar16,tempVar17,tempVar18,tempVar19,tempVar20,tempVar21,tempVar22,tempVar23};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseSimple.Expected expected:expectedSchemas)
    {
    String propSchema=String.format("{'path': 'a', 'type': %1$s",expected.Json}});
    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",propSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar24=new Namespace();
    tempVar24.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar24);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    Assert.AreEqual(1,layout.getColumns().Length,"Json: {0}",expected.Json);
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out col);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,col.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripSparseSimple.Closure tempVar25=new RoundTripSparseSimple.Closure();
    tempVar25.Col=col;
    tempVar25.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripSparseSimple, RoundTripSparseSimple.Closure>LayoutCodeSwitch(col.Type.LayoutCode,tempRef_row2,
    tempRef_root,tempVar25.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaUDT()
public final void ParseSchemaUDT()
    {
    String namespaceJson=""+"\r\n"+
    "                {'name': 'myNamespace', "+"\r\n"+
    "                 'schemas': ["+"\r\n"+
    "                    {'name': 'udtA', 'id': 1, 'type': 'schema', 'options': { 'disallowUnschematized': false }, "
    +"\r\n"+
    "                     'properties': [ "+"\r\n"+
    "                        { 'path': 'a', 'type': { 'type': 'int8', 'storage': 'fixed' }}, "+"\r\n"+
    "                        { 'path': 'b', 'type': { 'type': 'utf8', 'storage': 'variable', 'length': 100 }} "+"\r\n"+
    "                     ]"+"\r\n"+
    "                    },"+"\r\n"+
    "                    {'name': 'udtB', 'id': 2, 'type': 'schema'},"+"\r\n"+
    "                    {'name': 'udtB', 'id': 3, 'type': 'schema'},"+"\r\n"+
    "                    {'name': 'udtB', 'id': 4, 'type': 'schema'},"+"\r\n"+
    "                    {'name': 'table', 'id': -1, 'type': 'schema', "+"\r\n"+
    "                     'properties': ["+"\r\n"+
    "                        { 'path': 'u', 'type': { 'type': 'schema', 'name': 'udtA' }}, "+"\r\n"+
    "                        { 'path': 'v', 'type': { 'type': 'schema', 'name': 'udtB', 'id': 3 }}, "+"\r\n"+
    "                     ] "+"\r\n"+
    "                    }"+"\r\n"+
    "                 ]"+"\r\n"+
    "                }";

    Namespace n1=Namespace.Parse(namespaceJson);

    String tag=String.format("Json: %1$s",namespaceJson);

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    Schema s=tangible.ListHelper.find(n1.getSchemas(),x->x.Name.equals("table"));
    assert s!=null;
    assert"table"==s.getName();
    Layout layout=s.Compile(n1);
    LayoutColumn udtACol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("u",out udtACol);
    Assert.IsTrue(found,tag);
    Assert.AreEqual(StorageKind.Sparse,udtACol.Storage,tag);

    Schema udtASchema=tangible.ListHelper.find(n1.getSchemas(),x->x.SchemaId==udtACol.TypeArgs.SchemaId);
    assert udtASchema!=null;
    assert"udtA"==udtASchema.getName();

    // Verify that UDT versioning works through schema references.
    LayoutColumn udtBCol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    found=layout.TryFind("v",out udtBCol);
    Assert.IsTrue(found,tag);
    Assert.AreEqual(StorageKind.Sparse,udtBCol.Storage,tag);
    Schema udtBSchema=tangible.ListHelper.find(n1.getSchemas(),x->x.SchemaId==udtBCol.TypeArgs.SchemaId);
    assert udtBSchema!=null;
    assert"udtB"==udtBSchema.getName();
    assert new SchemaId(3)==udtBSchema.getSchemaId().clone();

    LayoutResolver resolver=new LayoutResolverNamespace(n1);
    Layout udtLayout=resolver.Resolve(udtASchema.getSchemaId().clone());
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    // Verify the udt doesn't yet exist.
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor scope;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    RowCursor.Create(tempRef_row,out scope).Find(tempRef_row2,udtACol.Path);
    row=tempRef_row2.argValue;
    row=tempRef_row.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row3=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut__=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    Result r=LayoutType.UDT.ReadScope(tempRef_row3,tempRef_scope,tempOut__);
    _=tempOut__.argValue;
    scope=tempRef_scope.argValue;
    row=tempRef_row3.argValue;
    ResultAssert.NotFound(r,tag);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row4=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    RowCursor udtScope1;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut_udtScope1=
    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=LayoutType.UDT.WriteScope(tempRef_row4,tempRef_scope2,udtACol.TypeArgs,tempOut_udtScope1);
    udtScope1=tempOut_udtScope1.argValue;
    scope=tempRef_scope2.argValue;
    row=tempRef_row4.argValue;
    ResultAssert.IsSuccess(r,tag);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row5=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope3=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    RowCursor udtScope2;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut_udtScope2=
    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=LayoutType.UDT.ReadScope(tempRef_row5,tempRef_scope3,tempOut_udtScope2);
    udtScope2=tempOut_udtScope2.argValue;
    scope=tempRef_scope3.argValue;
    row=tempRef_row5.argValue;
    ResultAssert.IsSuccess(r,tag);
    Assert.AreSame(udtLayout,udtScope2.Layout,tag);
    Assert.AreEqual(udtScope1.ScopeType,udtScope2.ScopeType,tag);
    Assert.AreEqual(udtScope1.start,udtScope2.start,tag);
    Assert.AreEqual(udtScope1.Immutable,udtScope2.Immutable,tag);

    // TODO: C# TO JAVA CONVERTER: There is no equivalent to implicit typing in Java unless the Java 10 inferred
    // typing option is selected:
    var expectedSchemas=new[]{new{Storage=StorageKind.Fixed,Path="a",FixedExpected=new RoundTripFixed.Expected();
    FixedExpected.Json="{ 'type': 'int8', 'storage': 'fixed' }";
    FixedExpected.Value=(byte)42;

    // TODO: C# TO JAVA CONVERTER: There is no equivalent to implicit typing in Java unless the Java 10 inferred
    // typing option is selected:
    for(var expected:expectedSchemas)
    {
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    found=udtLayout.TryFind(expected.Path,out col);
    Assert.IsTrue(found,"Path: {0}",expected.Path);
    StorageKind storage=expected.Storage;
    switch(storage)
    {
    case Fixed:
    RoundTripFixed.Closure tempVar=new RoundTripFixed.Closure();
    tempVar.Col=col;
    tempVar.Expected=expected.FixedExpected;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row6=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_udtScope1=
    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(udtScope1);
    this.<RoundTripFixed, RoundTripFixed.Closure>LayoutCodeSwitch(col.Type.LayoutCode,tempRef_row6,tempRef_udtScope1,
    tempVar.clone());
    udtScope1=tempRef_udtScope1.argValue;
    row=tempRef_row6.argValue;
    break;
    case Variable:
    RoundTripVariable.Closure tempVar2=new RoundTripVariable.Closure();
    tempVar2.Col=col;
    tempVar2.Layout=layout;
    tempVar2.Expected=expected.VariableExpected;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row7=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_udtScope12=
    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(udtScope1);
    this.<RoundTripVariable, RoundTripVariable.Closure>LayoutCodeSwitch(col.Type.LayoutCode,tempRef_row7,
    tempRef_udtScope12,tempVar2.clone());
    udtScope1=tempRef_udtScope12.argValue;
    row=tempRef_row7.argValue;
    break;
    }
    }

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row8=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row9=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor roRoot;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    RowCursor.Create(tempRef_row8).AsReadOnly(out roRoot).Find(tempRef_row9,udtACol.Path);
    row=tempRef_row9.argValue;
    row=tempRef_row8.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row10=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot
    // be converted using the 'Ref' helper class unless the method is within the code being modified:
    ResultAssert.InsufficientPermissions(udtACol.<LayoutUDT>TypeAs().DeleteScope(tempRef_row10,ref roRoot));
    row=tempRef_row10.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row11=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot
    // be converted using the 'Ref' helper class unless the method is within the code being modified:
    ResultAssert.InsufficientPermissions(udtACol.<LayoutUDT>TypeAs().WriteScope(tempRef_row11,ref roRoot,
    udtACol.TypeArgs,out udtScope2));
    row=tempRef_row11.argValue;

    // Overwrite the whole scope.
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row12=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row13=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    RowCursor.Create(tempRef_row12,out scope).Find(tempRef_row13,udtACol.Path);
    row=tempRef_row13.argValue;
    row=tempRef_row12.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row14=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope4=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    r=LayoutType.Null.WriteSparse(tempRef_row14,tempRef_scope4,NullValue.Default);
    scope=tempRef_scope4.argValue;
    row=tempRef_row14.argValue;
    ResultAssert.IsSuccess(r,tag);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row15=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope5=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    RowCursor _;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut__2=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=LayoutType.UDT.ReadScope(tempRef_row15,tempRef_scope5,tempOut__2);
    _=tempOut__2.argValue;
    scope=tempRef_scope5.argValue;
    row=tempRef_row15.argValue;
    ResultAssert.TypeMismatch(r,tag);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row16=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope6=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    r=LayoutType.UDT.DeleteScope(tempRef_row16,tempRef_scope6);
    scope=tempRef_scope6.argValue;
    row=tempRef_row16.argValue;
    ResultAssert.TypeMismatch(r,tag);

    // Overwrite it again, then delete it.
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row17=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row18=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    RowCursor.Create(tempRef_row17,out scope).Find(tempRef_row18,udtACol.Path);
    row=tempRef_row18.argValue;
    row=tempRef_row17.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row19=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope7=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    RowCursor _;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut__3=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=LayoutType.UDT.WriteScope(tempRef_row19,tempRef_scope7,udtACol.TypeArgs,tempOut__3);
    _=tempOut__3.argValue;
    scope=tempRef_scope7.argValue;
    row=tempRef_row19.argValue;
    ResultAssert.IsSuccess(r,tag);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row20=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope8=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    r=LayoutType.UDT.DeleteScope(tempRef_row20,tempRef_scope8);
    scope=tempRef_scope8.argValue;
    row=tempRef_row20.argValue;
    ResultAssert.IsSuccess(r,tag);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row21=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row22=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    RowCursor.Create(tempRef_row21,out scope).Find(tempRef_row22,udtACol.Path);
    row=tempRef_row22.argValue;
    row=tempRef_row21.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row23=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope9=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    RowCursor _;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut__4=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=LayoutType.UDT.ReadScope(tempRef_row23,tempRef_scope9,tempOut__4);
    _=tempOut__4.argValue;
    scope=tempRef_scope9.argValue;
    row=tempRef_row23.argValue;
    ResultAssert.NotFound(r,tag);
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaSparseObject()
public final void ParseSchemaSparseObject()
    {
    // Test all fixed column types.
    RoundTripSparseObject.Expected tempVar=new RoundTripSparseObject.Expected();
    tempVar.Json="{'path': 'b', 'type': {'type': 'int8'}}";
    tempVar.Value=(byte)42;
    RoundTripSparseObject.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObject.Expected[]{tempVar};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseObject.Expected expected:expectedSchemas)
    {
    String objectColumnSchema=String.format("{'path': 'a', 'type': {'type': 'object', 'properties': [%1$s] } }",
    expected.Json);
    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",
    objectColumnSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar2=new Namespace();
    tempVar2.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar2);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    Assert.AreEqual(1,layout.getColumns().Length,"Json: {0}",expected.Json);
    LayoutColumn objCol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out objCol);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,objCol.Storage,"Json: {0}",expected.Json);
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    found=layout.TryFind("a.b",out col);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,col.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripSparseObject.Closure tempVar3=new RoundTripSparseObject.Closure();
    tempVar3.ObjCol=objCol;
    tempVar3.Col=col;
    tempVar3.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripSparseObject, RoundTripSparseObject.Closure>LayoutCodeSwitch(col.Type.LayoutCode,tempRef_row2,
    tempRef_root,tempVar3.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaSparseObjectMulti()
public final void ParseSchemaSparseObjectMulti()
    {
    // Test sparse object columns with various kinds of sparse column fields.
    RoundTripSparseObjectMulti.Expected tempVar=new RoundTripSparseObjectMulti.Expected();
    tempVar.Json="{'path': 'b', 'type': {'type': 'int8'}}";
    RoundTripSparseObjectMulti.Property tempVar2=new RoundTripSparseObjectMulti.Property();
    tempVar2.setPath("a.b");
    tempVar2.Value=(byte)42;
    tempVar.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectMulti.Property[]{tempVar2};
    RoundTripSparseObjectMulti.Expected tempVar3=new RoundTripSparseObjectMulti.Expected();
    tempVar3.Json="{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'utf8'}}";
    RoundTripSparseObjectMulti.Property tempVar4=new RoundTripSparseObjectMulti.Property();
    tempVar4.setPath("a.b");
    tempVar4.Value=(byte)42;
    RoundTripSparseObjectMulti.Property tempVar5=new RoundTripSparseObjectMulti.Property();
    tempVar5.setPath("a.c");
    tempVar5.Value="abc";
    tempVar3.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectMulti.Property[]{tempVar4,tempVar5};
    RoundTripSparseObjectMulti.Expected tempVar6=new RoundTripSparseObjectMulti.Expected();
    tempVar6.Json="{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'bool'}}, {'path': 'd', " +
    "'type': {'type': 'binary'}}, {'path': 'e', 'type': {'type': 'null'}}";
    RoundTripSparseObjectMulti.Property tempVar7=new RoundTripSparseObjectMulti.Property();
    tempVar7.setPath("a.b");
    tempVar7.Value=(byte)42;
    RoundTripSparseObjectMulti.Property tempVar8=new RoundTripSparseObjectMulti.Property();
    tempVar8.setPath("a.c");
    tempVar8.Value=true;
    RoundTripSparseObjectMulti.Property tempVar9=new RoundTripSparseObjectMulti.Property();
    tempVar9.setPath("a.d");
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar9.Value = new byte[] { 0x01, 0x02, 0x03 };
    tempVar9.Value=new byte[]{0x01,0x02,0x03};
    RoundTripSparseObjectMulti.Property tempVar10=new RoundTripSparseObjectMulti.Property();
    tempVar10.setPath("a.e");
    tempVar10.Value=NullValue.Default;
    tempVar6.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectMulti.Property[]{tempVar7,tempVar8,tempVar9,tempVar10};
    RoundTripSparseObjectMulti.Expected tempVar11=new RoundTripSparseObjectMulti.Expected();
    tempVar11.Json="{'path': 'b', 'type': {'type': 'object'}}";
    RoundTripSparseObjectMulti.Property tempVar12=new RoundTripSparseObjectMulti.Property();
    tempVar12.setPath("a.b");
    tempVar11.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectMulti.Property[]{tempVar12};
    RoundTripSparseObjectMulti.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectMulti.Expected[]{tempVar,tempVar3,tempVar6,tempVar11};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseObjectMulti.Expected expected:expectedSchemas)
    {
    String objectColumnSchema=String.format("{'path': 'a', 'type': {'type': 'object', 'properties': [%1$s] } }",
    expected.Json);
    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",
    objectColumnSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar13=new Namespace();
    tempVar13.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar13);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    Assert.AreEqual(1,layout.getColumns().Length,"Json: {0}",expected.Json);
    LayoutColumn objCol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out objCol);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,objCol.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    // Verify the object doesn't exist.
    LayoutObject objT=objCol.Type instanceof LayoutObject?(LayoutObject)objCol.Type:null;
    assert objT!=null;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor field;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    RowCursor.Create(tempRef_row,out field).Find(tempRef_row2,objCol.Path);
    row=tempRef_row2.argValue;
    row=tempRef_row.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row3=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    RowCursor scope;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut_scope=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    Result r=objT.ReadScope(tempRef_row3,tempRef_field,tempOut_scope);
    scope=tempOut_scope.argValue;
    field=tempRef_field.argValue;
    row=tempRef_row3.argValue;
    ResultAssert.NotFound(r,"Json: {0}",expected.Json);

    // Write the object and the nested column.
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row4=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut_scope2=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=objT.WriteScope(tempRef_row4,tempRef_field2,objCol.TypeArgs,tempOut_scope2);
    scope=tempOut_scope2.argValue;
    field=tempRef_field2.argValue;
    row=tempRef_row4.argValue;
    ResultAssert.IsSuccess(r,"Json: {0}",expected.Json);

    for(java.lang.Iterable<RoundTripSparseObjectMulti.Property>permutation:expected.Props.Permute())
    {
    for(RoundTripSparseObjectMulti.Property prop:permutation)
    {
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    found=layout.TryFind(prop.Path,out col);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,col.Storage,"Json: {0}",expected.Json);

    RoundTripSparseObjectMulti.Closure tempVar14=new RoundTripSparseObjectMulti.Closure();
    tempVar14.Col=col;
    tempVar14.Prop=prop.clone();
    tempVar14.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row5=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_scope=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(scope);
    this.<RoundTripSparseObjectMulti, RoundTripSparseObjectMulti.Closure>LayoutCodeSwitch(col.Type.LayoutCode,
    tempRef_row5,tempRef_scope,tempVar14.clone());
    scope=tempRef_scope.argValue;
    row=tempRef_row5.argValue;
    }
    }

    // Write something after the scope.
    UtfAnyString otherColumnPath="not-"+objCol.Path;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row6=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor otherColumn;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    field.Clone(out otherColumn).Find(tempRef_row6,otherColumnPath);
    row=tempRef_row6.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row7=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_otherColumn=
    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(otherColumn);
    r=LayoutType.Boolean.WriteSparse(tempRef_row7,tempRef_otherColumn,true);
    otherColumn=tempRef_otherColumn.argValue;
    row=tempRef_row7.argValue;
    ResultAssert.IsSuccess(r,"Json: {0}",expected.Json);

    // Overwrite the whole scope.
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row8=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field3=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    r=LayoutType.Null.WriteSparse(tempRef_row8,tempRef_field3,NullValue.Default);
    field=tempRef_field3.argValue;
    row=tempRef_row8.argValue;
    ResultAssert.IsSuccess(r,"Json: {0}",expected.Json);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row9=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field4=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    RowCursor _;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut__=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=objT.ReadScope(tempRef_row9,tempRef_field4,tempOut__);
    _=tempOut__.argValue;
    field=tempRef_field4.argValue;
    row=tempRef_row9.argValue;
    ResultAssert.TypeMismatch(r,"Json: {0}",expected.Json);

    // Read the thing after the scope and verify it is still there.
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row10=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    field.Clone(out otherColumn).Find(tempRef_row10,otherColumnPath);
    row=tempRef_row10.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row11=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_otherColumn2=
    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(otherColumn);
    boolean notScope;
    tangible.OutObject<Boolean> tempOut_notScope=new tangible.OutObject<Boolean>();
    r=LayoutType.Boolean.ReadSparse(tempRef_row11,tempRef_otherColumn2,tempOut_notScope);
    notScope=tempOut_notScope.argValue;
    otherColumn=tempRef_otherColumn2.argValue;
    row=tempRef_row11.argValue;
    ResultAssert.IsSuccess(r,"Json: {0}",expected.Json);
    assert notScope;
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaSparseObjectNested()
public final void ParseSchemaSparseObjectNested()
    {
    // Test nested sparse object columns with various kinds of sparse column fields.
    RoundTripSparseObjectNested.Expected tempVar=new RoundTripSparseObjectNested.Expected();
    tempVar.Json="{'path': 'c', 'type': {'type': 'int8'}}";
    RoundTripSparseObjectNested.Property tempVar2=new RoundTripSparseObjectNested.Property();
    tempVar2.setPath("a.b.c");
    tempVar2.Value=(byte)42;
    tempVar.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectNested.Property[]{tempVar2};
    RoundTripSparseObjectNested.Expected tempVar3=new RoundTripSparseObjectNested.Expected();
    tempVar3.Json="{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'utf8'}}";
    RoundTripSparseObjectNested.Property tempVar4=new RoundTripSparseObjectNested.Property();
    tempVar4.setPath("a.b.b");
    tempVar4.Value=(byte)42;
    RoundTripSparseObjectNested.Property tempVar5=new RoundTripSparseObjectNested.Property();
    tempVar5.setPath("a.b.c");
    tempVar5.Value="abc";
    tempVar3.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectNested.Property[]{tempVar4,tempVar5};
    RoundTripSparseObjectNested.Expected tempVar6=new RoundTripSparseObjectNested.Expected();
    tempVar6.Json="{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'bool'}}, {'path': 'd', " +
    "'type': {'type': 'binary'}}, {'path': 'e', 'type': {'type': 'null'}}";
    RoundTripSparseObjectNested.Property tempVar7=new RoundTripSparseObjectNested.Property();
    tempVar7.setPath("a.b.b");
    tempVar7.Value=(byte)42;
    RoundTripSparseObjectNested.Property tempVar8=new RoundTripSparseObjectNested.Property();
    tempVar8.setPath("a.b.c");
    tempVar8.Value=true;
    RoundTripSparseObjectNested.Property tempVar9=new RoundTripSparseObjectNested.Property();
    tempVar9.setPath("a.b.d");
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar9.Value = new byte[] { 0x01, 0x02, 0x03 };
    tempVar9.Value=new byte[]{0x01,0x02,0x03};
    RoundTripSparseObjectNested.Property tempVar10=new RoundTripSparseObjectNested.Property();
    tempVar10.setPath("a.b.e");
    tempVar10.Value=NullValue.Default;
    tempVar6.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectNested.Property[]{tempVar7,tempVar8,tempVar9,tempVar10};
    RoundTripSparseObjectNested.Expected tempVar11=new RoundTripSparseObjectNested.Expected();
    tempVar11.Json="{'path': 'b', 'type': {'type': 'object'}}";
    RoundTripSparseObjectNested.Property tempVar12=new RoundTripSparseObjectNested.Property();
    tempVar12.setPath("a.b.b");
    tempVar11.Props=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectNested.Property[]{tempVar12};
    RoundTripSparseObjectNested.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseObjectNested.Expected[]{tempVar,tempVar3,tempVar6,tempVar11};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseObjectNested.Expected expected:expectedSchemas)
    {
    String nestedColumnSchema=String.format("{'path': 'b', 'type': {'type': 'object', 'properties': [%1$s] } }",
    expected.Json);
    String objectColumnSchema=String.format("{'path': 'a', 'type': {'type': 'object', 'properties': [%1$s] } }",
    nestedColumnSchema);
    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",
    objectColumnSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar13=new Namespace();
    tempVar13.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar13);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    LayoutColumn objCol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out objCol);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,objCol.Storage,"Json: {0}",expected.Json);
    LayoutColumn objCol2;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    found=layout.TryFind("a.b",out objCol2);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,objCol2.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    // Write the object.
    LayoutObject objT=objCol.Type instanceof LayoutObject?(LayoutObject)objCol.Type:null;
    assert objT!=null;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor field;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    root.Clone(out field).Find(tempRef_row2,objCol.Path);
    row=tempRef_row2.argValue;
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row3=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    RowCursor _;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut__=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    Result r=objT.WriteScope(tempRef_row3,tempRef_field,objCol.TypeArgs,tempOut__);
    _=tempOut__.argValue;
    field=tempRef_field.argValue;
    row=tempRef_row3.argValue;
    ResultAssert.IsSuccess(r,"Json: {0}",expected.Json);

    for(java.lang.Iterable<RoundTripSparseObjectNested.Property>permutation:expected.Props.Permute())
    {
    for(RoundTripSparseObjectNested.Property prop:permutation)
    {
    LayoutColumn col;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    found=layout.TryFind(prop.Path,out col);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,col.Storage,"Json: {0}",expected.Json);

    RoundTripSparseObjectNested.Closure tempVar14=new RoundTripSparseObjectNested.Closure();
    tempVar14.Col=col;
    tempVar14.Prop=prop.clone();
    tempVar14.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row4=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripSparseObjectNested, RoundTripSparseObjectNested.Closure>LayoutCodeSwitch(col.Type.LayoutCode,
    tempRef_row4,tempRef_root,tempVar14.clone());
    root=tempRef_root.argValue;
    row=tempRef_row4.argValue;
    }
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaSparseArray()
public final void ParseSchemaSparseArray()
    {
    // Test all fixed column types.
    RoundTripSparseArray.Expected tempVar=new RoundTripSparseArray.Expected();
    tempVar.Json="array[null]";
    tempVar.Type=LayoutType.Null;
    tempVar.Value=new ArrayList<Object>(Arrays.asList(NullValue.Default,NullValue.Default,NullValue.Default));
    RoundTripSparseArray.Expected tempVar2=new RoundTripSparseArray.Expected();
    tempVar2.Json="array[bool]";
    tempVar2.Type=LayoutType.Boolean;
    tempVar2.Value=new ArrayList<Object>(Arrays.asList(true,false,true));
    RoundTripSparseArray.Expected tempVar3=new RoundTripSparseArray.Expected();
    tempVar3.Json="array[int8]";
    tempVar3.Type=LayoutType.Int8;
    tempVar3.Value=new ArrayList<Object>(Arrays.asList((byte)42,(byte)43,(byte)44));
    RoundTripSparseArray.Expected tempVar4=new RoundTripSparseArray.Expected();
    tempVar4.Json="array[int16]";
    tempVar4.Type=LayoutType.Int16;
    tempVar4.Value=new ArrayList<Object>(Arrays.asList((short)42,(short)43,(short)44));
    RoundTripSparseArray.Expected tempVar5=new RoundTripSparseArray.Expected();
    tempVar5.Json="array[int32]";
    tempVar5.Type=LayoutType.Int32;
    tempVar5.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseArray.Expected tempVar6=new RoundTripSparseArray.Expected();
    tempVar6.Json="array[int64]";
    tempVar6.Type=LayoutType.Int64;
    tempVar6.Value=new ArrayList<Object>(Arrays.asList(42L,43L,44L));
    RoundTripSparseArray.Expected tempVar7=new RoundTripSparseArray.Expected();
    tempVar7.Json="array[uint8]";
    tempVar7.Type=LayoutType.UInt8;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar7.Value = new List<object> { (byte)42, (byte)43, (byte)44 };
    tempVar7.Value=new ArrayList<Object>(Arrays.asList((byte)42,(byte)43,(byte)44));
    RoundTripSparseArray.Expected tempVar8=new RoundTripSparseArray.Expected();
    tempVar8.Json="array[uint16]";
    tempVar8.Type=LayoutType.UInt16;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar8.Value = new List<object> { (ushort)42, (ushort)43, (ushort)44 };
    tempVar8.Value=new ArrayList<Object>(Arrays.asList((short)42,(short)43,(short)44));
    RoundTripSparseArray.Expected tempVar9=new RoundTripSparseArray.Expected();
    tempVar9.Json="array[uint32]";
    tempVar9.Type=LayoutType.UInt32;
    tempVar9.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseArray.Expected tempVar10=new RoundTripSparseArray.Expected();
    tempVar10.Json="array[uint64]";
    tempVar10.Type=LayoutType.UInt64;
    tempVar10.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseArray.Expected tempVar11=new RoundTripSparseArray.Expected();
    tempVar11.Json="array[varint]";
    tempVar11.Type=LayoutType.VarInt;
    tempVar11.Value=new ArrayList<Object>(Arrays.asList(42L,43L,44L));
    RoundTripSparseArray.Expected tempVar12=new RoundTripSparseArray.Expected();
    tempVar12.Json="array[varuint]";
    tempVar12.Type=LayoutType.VarUInt;
    tempVar12.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseArray.Expected tempVar13=new RoundTripSparseArray.Expected();
    tempVar13.Json="array[float32]";
    tempVar13.Type=LayoutType.Float32;
    tempVar13.Value=new ArrayList<Object>(Arrays.asList(4.2F,4.3F,4.4F));
    RoundTripSparseArray.Expected tempVar14=new RoundTripSparseArray.Expected();
    tempVar14.Json="array[float64]";
    tempVar14.Type=LayoutType.Float64;
    tempVar14.Value=new ArrayList<Object>(Arrays.asList(4.2,4.3,4.4));
    RoundTripSparseArray.Expected tempVar15=new RoundTripSparseArray.Expected();
    tempVar15.Json="array[float128]";
    tempVar15.Type=LayoutType.Float128;
    tempVar15.Value=new ArrayList<Object>(Arrays.asList(new Float128(0,42),new Float128(0,43),new Float128(0,44)));
    RoundTripSparseArray.Expected tempVar16=new RoundTripSparseArray.Expected();
    tempVar16.Json="array[decimal]";
    tempVar16.Type=LayoutType.Decimal;
    tempVar16.Value=new ArrayList<Object>(Arrays.asList(4.2,4.3,4.4));
    RoundTripSparseArray.Expected tempVar17=new RoundTripSparseArray.Expected();
    tempVar17.Json="array[datetime]";
    tempVar17.Type=LayoutType.DateTime;
    tempVar17.Value=new ArrayList<Object>(Arrays.asList(LocalDateTime.UtcNow,LocalDateTime.UtcNow.AddTicks(1),
    LocalDateTime.UtcNow.AddTicks(2)));
    RoundTripSparseArray.Expected tempVar18=new RoundTripSparseArray.Expected();
    tempVar18.Json="array[unixdatetime]";
    tempVar18.Type=LayoutType.UnixDateTime;
    tempVar18.Value=new ArrayList<Object>(Arrays.asList(new UnixDateTime(1),new UnixDateTime(2),new UnixDateTime(3)));
    RoundTripSparseArray.Expected tempVar19=new RoundTripSparseArray.Expected();
    tempVar19.Json="array[guid]";
    tempVar19.Type=LayoutType.Guid;
    tempVar19.Value=new ArrayList<Object>(Arrays.asList(UUID.NewGuid(),UUID.NewGuid(),UUID.NewGuid()));
    RoundTripSparseArray.Expected tempVar20=new RoundTripSparseArray.Expected();
    tempVar20.Json="array[mongodbobjectid]";
    tempVar20.Type=LayoutType.MongoDbObjectId;
    tempVar20.Value=new ArrayList<Object>(Arrays.asList(new MongoDbObjectId(0,1),new MongoDbObjectId(0,2),
    new MongoDbObjectId(0,3)));
    RoundTripSparseArray.Expected tempVar21=new RoundTripSparseArray.Expected();
    tempVar21.Json="array[utf8]";
    tempVar21.Type=LayoutType.Utf8;
    tempVar21.Value=new ArrayList<Object>(Arrays.asList("abc","def","xyz"));
    RoundTripSparseArray.Expected tempVar22=new RoundTripSparseArray.Expected();
    tempVar22.Json="array[binary]";
    tempVar22.Type=LayoutType.Binary;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar22.Value = new List<object> { new byte[] { 0x01, 0x02 }, new byte[] { 0x03, 0x04 }, new
    // byte[] { 0x05, 0x06 } };
    tempVar22.Value=new ArrayList<Object>(Arrays.asList(new Byte[]{0x01,0x02},new byte[]{0x03,0x04},new byte[]{0x05,
    0x06}));
    RoundTripSparseArray.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseArray.Expected[]{tempVar,tempVar2,tempVar3,tempVar4,tempVar5,tempVar6,tempVar7,tempVar8,tempVar9,tempVar10,tempVar11,tempVar12,tempVar13,tempVar14,tempVar15,tempVar16,tempVar17,tempVar18,tempVar19,tempVar20,tempVar21,tempVar22};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseArray.Expected expected:expectedSchemas)
    {
    for(java.lang.Class arrT:new java.lang.Class[]{LayoutTypedArray.class,LayoutArray.class})
    {
    String arrayColumnSchema="{'path': 'a', 'type': {'type': 'array', 'items': {'type': 'any'}} }";
    if(arrT==LayoutTypedArray.class)
    {
    arrayColumnSchema=String.format("{'path': 'a', 'type': {'type': 'array',"+"\r\n"+
    "                                                                        'items': {'type': '%1$s', 'nullable': " +
    "false }} }",expected.Type.getName());
    }

    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",
    arrayColumnSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar23=new Namespace();
    tempVar23.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar23);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    LayoutColumn arrCol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out arrCol);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,arrCol.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripSparseArray.Closure tempVar24=new RoundTripSparseArray.Closure();
    tempVar24.ArrCol=arrCol;
    tempVar24.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripSparseArray, RoundTripSparseArray.Closure>LayoutCodeSwitch(expected.Type.LayoutCode,tempRef_row2,
    tempRef_root,tempVar24.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")][SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1139",
// Justification = "Need to control the binary ordering.")] public void ParseSchemaSparseSet()
public final void ParseSchemaSparseSet()
    {
    // Test all fixed column types.
    RoundTripSparseSet.Expected tempVar=new RoundTripSparseSet.Expected();
    tempVar.Json="set[null]";
    tempVar.Type=LayoutType.Null;
    tempVar.Value=new ArrayList<Object>(Arrays.asList(NullValue.Default));
    RoundTripSparseSet.Expected tempVar2=new RoundTripSparseSet.Expected();
    tempVar2.Json="set[bool]";
    tempVar2.Type=LayoutType.Boolean;
    tempVar2.Value=new ArrayList<Object>(Arrays.asList(false,true));
    RoundTripSparseSet.Expected tempVar3=new RoundTripSparseSet.Expected();
    tempVar3.Json="set[int8]";
    tempVar3.Type=LayoutType.Int8;
    tempVar3.Value=new ArrayList<Object>(Arrays.asList((byte)42,(byte)43,(byte)44));
    RoundTripSparseSet.Expected tempVar4=new RoundTripSparseSet.Expected();
    tempVar4.Json="set[int16]";
    tempVar4.Type=LayoutType.Int16;
    tempVar4.Value=new ArrayList<Object>(Arrays.asList((short)42,(short)43,(short)44));
    RoundTripSparseSet.Expected tempVar5=new RoundTripSparseSet.Expected();
    tempVar5.Json="set[int32]";
    tempVar5.Type=LayoutType.Int32;
    tempVar5.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseSet.Expected tempVar6=new RoundTripSparseSet.Expected();
    tempVar6.Json="set[int64]";
    tempVar6.Type=LayoutType.Int64;
    tempVar6.Value=new ArrayList<Object>(Arrays.asList(42L,43L,44L));
    RoundTripSparseSet.Expected tempVar7=new RoundTripSparseSet.Expected();
    tempVar7.Json="set[uint8]";
    tempVar7.Type=LayoutType.UInt8;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar7.Value = new List<object> { (byte)42, (byte)43, (byte)44 };
    tempVar7.Value=new ArrayList<Object>(Arrays.asList((byte)42,(byte)43,(byte)44));
    RoundTripSparseSet.Expected tempVar8=new RoundTripSparseSet.Expected();
    tempVar8.Json="set[uint16]";
    tempVar8.Type=LayoutType.UInt16;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar8.Value = new List<object> { (ushort)42, (ushort)43, (ushort)44 };
    tempVar8.Value=new ArrayList<Object>(Arrays.asList((short)42,(short)43,(short)44));
    RoundTripSparseSet.Expected tempVar9=new RoundTripSparseSet.Expected();
    tempVar9.Json="set[uint32]";
    tempVar9.Type=LayoutType.UInt32;
    tempVar9.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseSet.Expected tempVar10=new RoundTripSparseSet.Expected();
    tempVar10.Json="set[uint64]";
    tempVar10.Type=LayoutType.UInt64;
    tempVar10.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseSet.Expected tempVar11=new RoundTripSparseSet.Expected();
    tempVar11.Json="set[varint]";
    tempVar11.Type=LayoutType.VarInt;
    tempVar11.Value=new ArrayList<Object>(Arrays.asList(42L,43L,44L));
    RoundTripSparseSet.Expected tempVar12=new RoundTripSparseSet.Expected();
    tempVar12.Json="set[varuint]";
    tempVar12.Type=LayoutType.VarUInt;
    tempVar12.Value=new ArrayList<Object>(Arrays.asList(42,43,44));
    RoundTripSparseSet.Expected tempVar13=new RoundTripSparseSet.Expected();
    tempVar13.Json="set[float32]";
    tempVar13.Type=LayoutType.Float32;
    tempVar13.Value=new ArrayList<Object>(Arrays.asList(4.2F,4.3F,4.4F));
    RoundTripSparseSet.Expected tempVar14=new RoundTripSparseSet.Expected();
    tempVar14.Json="set[float64]";
    tempVar14.Type=LayoutType.Float64;
    tempVar14.Value=new ArrayList<Object>(Arrays.asList((double)0xAAAAAAAAAAAAAAAA,(double)0xBBBBBBBBBBBBBBBB,
    (double)0xCCCCCCCCCCCCCCCC));
    RoundTripSparseSet.Expected tempVar15=new RoundTripSparseSet.Expected();
    tempVar15.Json="set[decimal]";
    tempVar15.Type=LayoutType.Decimal;
    tempVar15.Value=new ArrayList<Object>(Arrays.asList(4.2,4.3,4.4));
    RoundTripSparseSet.Expected tempVar16=new RoundTripSparseSet.Expected();
    tempVar16.Json="set[datetime]";
    tempVar16.Type=LayoutType.DateTime;
    tempVar16.Value=new ArrayList<Object>(Arrays.asList(LocalDateTime.of(1,DateTimeKind.Unspecified),
    LocalDateTime.of(2,DateTimeKind.Unspecified),LocalDateTime.of(3,DateTimeKind.Unspecified)));
    RoundTripSparseSet.Expected tempVar17=new RoundTripSparseSet.Expected();
    tempVar17.Json="set[guid]";
    tempVar17.Type=LayoutType.Guid;
    tempVar17.Value=new ArrayList<Object>(Arrays.asList(UUID.fromString("AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA"),
    UUID.fromString("BBBBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB"),UUID.fromString("CCCCCCCC-CCCC-CCCC-CCCC-CCCCCCCCCCCC")));
    RoundTripSparseSet.Expected tempVar18=new RoundTripSparseSet.Expected();
    tempVar18.Json="set[utf8]";
    tempVar18.Type=LayoutType.Utf8;
    tempVar18.Value=new ArrayList<Object>(Arrays.asList("abc","def","xyz"));
    RoundTripSparseSet.Expected tempVar19=new RoundTripSparseSet.Expected();
    tempVar19.Json="set[binary]";
    tempVar19.Type=LayoutType.Binary;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: tempVar19.Value = new List<object> { new byte[] { 0x01, 0x02 }, new byte[] { 0x03, 0x04 }, new
    // byte[] { 0x05, 0x06 } };
    tempVar19.Value=new ArrayList<Object>(Arrays.asList(new Byte[]{0x01,0x02},new byte[]{0x03,0x04},new byte[]{0x05,
    0x06}));
    RoundTripSparseSet.Expected[]expectedSchemas=
    new azure.data.cosmos.serialization.hybridrow.unit.layoutcompilerunittests.RoundTripSparseSet.Expected[]{tempVar,tempVar2,tempVar3,tempVar4,tempVar5,tempVar6,tempVar7,tempVar8,tempVar9,tempVar10,tempVar11,tempVar12,tempVar13,tempVar14,tempVar15,tempVar16,tempVar17,tempVar18,tempVar19};

    RowBuffer row=new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
    for(RoundTripSparseSet.Expected expected:expectedSchemas)
    {
    for(java.lang.Class setT:new java.lang.Class[]{LayoutTypedSet.class})
    {
    String setColumnSchema="{'path': 'a', 'type': {'type': 'set', 'items': {'type': 'any'}} }";
    if(setT==LayoutTypedSet.class)
    {
    setColumnSchema=String.format("{'path': 'a', 'type': {'type': 'set', "+"\r\n"+
    "                                                                      'items': {'type': '%1$s', 'nullable': " +
    "false }} }",expected.Type.getName());
    }

    String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",
    setColumnSchema);
    Schema s=Schema.Parse(tableSchema);
    Namespace tempVar20=new Namespace();
    tempVar20.setSchemas(new ArrayList<Schema>(Arrays.asList(s)));
    LayoutResolverNamespace resolver=new LayoutResolverNamespace(tempVar20);
    Layout layout=resolver.Resolve(new SchemaId(-1));
    LayoutColumn setCol;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    boolean found=layout.TryFind("a",out setCol);
    Assert.IsTrue(found,"Json: {0}",expected.Json);
    Assert.AreEqual(StorageKind.Sparse,setCol.Storage,"Json: {0}",expected.Json);

    // Try writing a row using the layout.
    row.Reset();
    row.InitLayout(HybridRowVersion.V1,layout,resolver);

    HybridRowHeader header=row.getHeader().clone();
    assert HybridRowVersion.V1==header.getVersion();
    assert layout.getSchemaId().clone()==header.getSchemaId().clone();

    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    RowCursor root=RowCursor.Create(tempRef_row);
    row=tempRef_row.argValue;
    RoundTripSparseSet.Closure tempVar21=new RoundTripSparseSet.Closure();
    tempVar21.SetCol=setCol;
    tempVar21.Expected=expected.clone();
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>tempRef_row2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_root=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(root);
    this.<RoundTripSparseSet, RoundTripSparseSet.Closure>LayoutCodeSwitch(expected.Type.LayoutCode,tempRef_row2,
    tempRef_root,tempVar21.clone());
    root=tempRef_root.argValue;
    row=tempRef_row2.argValue;
    }
    }
    }

/**
 * Ensure that a parent scope exists in the row.
 *
 * @param row The row to create the desired scope.
 * @param root The root scope.
 * @param col The scope to create.
 * @param tag A string to tag errors with.
 * @return The enclosing scope.
 */
private static RowCursor EnsureScope(tangible.RefObject<RowBuffer> row,tangible.RefObject<RowCursor> root,
    LayoutColumn col,String tag)
    {
    if(col==null)
    {
    return root.argValue.clone();
    }

    RowCursor parentScope=LayoutCompilerUnitTests.EnsureScope(row,root,col.getParent(),tag);

    azure.data.cosmos.serialization.hybridrow.layouts.LayoutType tempVar=col.getType();
    LayoutObject pT=tempVar instanceof LayoutObject?(LayoutObject)tempVar:null;
    assert pT!=null;
    RowCursor field;
    // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot
    // be converted using the 'Out' helper class unless the method is within the code being modified:
    parentScope.Clone(out field).Find(row,col.getPath());
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    RowCursor scope;
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut_scope=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    Result r=pT.ReadScope(row,tempRef_field,tempOut_scope);
    scope=tempOut_scope.argValue;
    field=tempRef_field.argValue;
    if(r==Result.NotFound)
    {
    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempRef_field2=new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(field);
    tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>tempOut_scope2=new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
    r=pT.WriteScope(row,tempRef_field2,col.getTypeArgs().clone(),tempOut_scope2);
    scope=tempOut_scope2.argValue;
    field=tempRef_field2.argValue;
    }

    ResultAssert.IsSuccess(r,tag);
    return scope;
    }

// TODO: C# TO JAVA CONVERTER: The C# 'new()' constraint has no equivalent in Java:
//ORIGINAL LINE: private void LayoutCodeSwitch<TDispatcher, TClosure>(LayoutCode code, ref RowBuffer row, ref
// RowCursor scope, TClosure closure) where TDispatcher : TestActionDispatcher<TClosure>, new()
private<TDispatcher extends TestActionDispatcher<TClosure>, TClosure> void LayoutCodeSwitch(LayoutCode code,
    tangible.RefObject<RowBuffer> row,tangible.RefObject<RowCursor> scope,TClosure closure)
    {
    TDispatcher dispatcher=new TDispatcher();
    switch(code)
    {
    case Null:
    dispatcher.<LayoutNull, NullValue>Dispatch(row,scope,closure);
    break;
    case Boolean:
    dispatcher.<LayoutBoolean, Boolean>Dispatch(row,scope,closure);
    break;
    case Int8:
    dispatcher.<LayoutInt8, Byte>Dispatch(row,scope,closure);
    break;
    case Int16:
    dispatcher.<LayoutInt16, Short>Dispatch(row,scope,closure);
    break;
    case Int32:
    dispatcher.<LayoutInt32, Integer>Dispatch(row,scope,closure);
    break;
    case Int64:
    dispatcher.<LayoutInt64, Long>Dispatch(row,scope,closure);
    break;
    case UInt8:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: dispatcher.Dispatch<LayoutUInt8, byte>(ref row, ref scope, closure);
    dispatcher.<LayoutUInt8, Byte>Dispatch(row,scope,closure);
    break;
    case UInt16:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: dispatcher.Dispatch<LayoutUInt16, ushort>(ref row, ref scope, closure);
    dispatcher.<LayoutUInt16, Short>Dispatch(row,scope,closure);
    break;
    case UInt32:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: dispatcher.Dispatch<LayoutUInt32, uint>(ref row, ref scope, closure);
    dispatcher.<LayoutUInt32, Integer>Dispatch(row,scope,closure);
    break;
    case UInt64:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: dispatcher.Dispatch<LayoutUInt64, ulong>(ref row, ref scope, closure);
    dispatcher.<LayoutUInt64, Long>Dispatch(row,scope,closure);
    break;
    case VarInt:
    dispatcher.<LayoutVarInt, Long>Dispatch(row,scope,closure);
    break;
    case VarUInt:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: dispatcher.Dispatch<LayoutVarUInt, ulong>(ref row, ref scope, closure);
    dispatcher.<LayoutVarUInt, Long>Dispatch(row,scope,closure);
    break;
    case Float32:
    dispatcher.<LayoutFloat32, Float>Dispatch(row,scope,closure);
    break;
    case Float64:
    dispatcher.<LayoutFloat64, Double>Dispatch(row,scope,closure);
    break;
    case Float128:
    dispatcher.<LayoutFloat128, Float128>Dispatch(row,scope,closure);
    break;
    case Decimal:
    dispatcher.<LayoutDecimal, BigDecimal>Dispatch(row,scope,closure);
    break;
    case DateTime:
    dispatcher.<LayoutDateTime, LocalDateTime>Dispatch(row,scope,closure);
    break;
    case UnixDateTime:
    dispatcher.<LayoutUnixDateTime, UnixDateTime>Dispatch(row,scope,closure);
    break;
    case Guid:
    dispatcher.<LayoutGuid, UUID>Dispatch(row,scope,closure);
    break;
    case MongoDbObjectId:
    dispatcher.<LayoutMongoDbObjectId, MongoDbObjectId>Dispatch(row,scope,closure);
    break;
    case Utf8:
    dispatcher.<LayoutUtf8, String>Dispatch(row,scope,closure);
    break;
    case Binary:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: dispatcher.Dispatch<LayoutBinary, byte[]>(ref row, ref scope, closure);
    dispatcher.<LayoutBinary, byte[]>Dispatch(row,scope,closure);
    break;
    case ObjectScope:
    dispatcher.DispatchObject(row,scope,closure);
    break;
default:
    throw new IllegalStateException(lenientFormat("Unknown type will be ignored: %s",code));
    break;
    }
    }

private final static class RoundTripFixed extends TestActionDispatcher<RoundTripFixed.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn col = closure.Col;
        Expected expected = closure.Expected.clone();
        Result r;
        TValue value;

        System.out.printf("%1$s" + "\r\n", expected.Json);
        TLayout t = (TLayout)col.getType();
        if (LayoutBit.opNotEquals(col.getNullBit().clone(),
            LayoutBit.Invalid)) {
            Out<TValue> tempOut_value = new Out<TValue>();
            r = t.ReadFixed(row, root, col, tempOut_value);
            value = tempOut_value.get();
            ResultAssert.NotFound(r, "Json: {0}", expected.Json);
        } else {
            Out<TValue> tempOut_value2 = new Out<TValue>();
            r = t.ReadFixed(row, root, col, tempOut_value2);
            value = tempOut_value2.get();
            ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
            boolean tempVar = expected.Default instanceof Array;
            Array defaultArray = tempVar ? (Array)expected.Default : null;
            if (tempVar) {
                CollectionAssert.AreEqual(defaultArray, (Collection)value, "Json: {0}", expected.Json);
            } else {
                Assert.AreEqual(expected.Default, value, "Json: {0}", expected.Json);
            }
        }

        r = t.WriteFixed(row, root, col, (TValue)expected.Value);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Out<TValue> tempOut_value3 = new Out<TValue>();
        r = t.ReadFixed(row, root, col, tempOut_value3);
        value = tempOut_value3.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        boolean tempVar2 = expected.Value instanceof Array;
        Array array = tempVar2 ? (Array)expected.Value : null;
        if (tempVar2) {
            CollectionAssert.AreEqual(array, (Collection)value, "Json: {0}", expected.Json);
        } else {
            Assert.AreEqual(expected.Value, value, "Json: {0}", expected.Json);
        }

        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(t.WriteFixed(row, ref roRoot, col, (TValue)expected.Value));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(t.DeleteFixed(row, ref roRoot, col));

        if (LayoutBit.opNotEquals(col.getNullBit().clone(),
            LayoutBit.Invalid)) {
            ResultAssert.IsSuccess(t.DeleteFixed(row, root, col));
        } else {
            ResultAssert.TypeMismatch(t.DeleteFixed(row, root, col));
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn Col;
        public Expected Expected = new Expected();

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.Col = this.Col;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public Object Default;
        public String Json;
        public int Length;
        public String TypeName;
        public Object Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.TypeName = this.TypeName;
            varCopy.Json = this.Json;
            varCopy.Value = this.Value;
            varCopy.Default = this.Default;
            varCopy.Length = this.Length;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseArray extends TestActionDispatcher<RoundTripSparseArray.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn arrCol = closure.ArrCol;
        LayoutType tempVar = arrCol.getType();
        LayoutIndexedScope arrT = tempVar instanceof LayoutIndexedScope ? (LayoutIndexedScope)tempVar : null;
        Expected expected = closure.Expected.clone();
        String tag = String.format("Json: %1$s, Array: %2$s", expected.Json, arrCol.getType().getName());

        System.out.println(tag);
        Assert.IsNotNull(arrT, tag);

        TLayout t = (TLayout)expected.Type;

        // Verify the array doesn't yet exist.
        RowCursor field;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out field).Find(row, arrCol.getPath());
        Reference<RowCursor> tempReference_field =
            new Reference<RowCursor>(field);
        RowCursor scope;
        Out<RowCursor> tempOut_scope =
            new Out<RowCursor>();
        Result r = arrT.ReadScope(row, tempReference_field, tempOut_scope);
        scope = tempOut_scope.get();
        field = tempReference_field.get();
        ResultAssert.NotFound(r, tag);

        // Write the array.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = arrT.WriteScope(row, ref field, arrCol.getTypeArgs().clone(), out scope);
        ResultAssert.IsSuccess(r, tag);

        // Verify the nested field doesn't yet appear within the new scope.
        assert !scope.MoveNext(row);
        TValue value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref scope, out value);
        ResultAssert.NotFound(r, tag);

        // Write the nested fields.
        RowCursor elm;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.Clone(out elm);
        for (Object item : expected.Value) {
            // Write the ith index.
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.WriteSparse(row, ref elm, (TValue)item);
            ResultAssert.IsSuccess(r, tag);

            // Move cursor to the ith+1 index.
            assert !elm.MoveNext(row);
        }

        // Read the array and the nested column, validate the nested column has the proper value.
        Reference<RowCursor> tempReference_field2 =
            new Reference<RowCursor>(field);
        RowCursor scope2;
        Out<RowCursor> tempOut_scope2 =
            new Out<RowCursor>();
        r = arrT.ReadScope(row, tempReference_field2, tempOut_scope2);
        scope2 = tempOut_scope2.get();
        field = tempReference_field2.get();
        ResultAssert.IsSuccess(r, tag);
        Assert.AreEqual(scope.ScopeType, scope2.ScopeType, tag);
        Assert.AreEqual(scope.start(), scope2.start(), tag);
        Assert.AreEqual(scope.Immutable, scope2.Immutable, tag);

        // Read the nested fields
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope2.Clone(out elm);
        for (Object item : expected.Value) {
            assert elm.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref elm, out value);
            ResultAssert.IsSuccess(r, tag);
            boolean tempVar2 = item instanceof Array;
            Array array = tempVar2 ? (Array)item : null;
            if (tempVar2) {
                CollectionAssert.AreEqual(array, (Collection)value, tag);
            } else {
                Assert.AreEqual((TValue)item, value, tag);
            }
        }

        // Delete an item.
        int indexToDelete = 1;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert scope2.Clone(out elm).MoveTo(row, indexToDelete);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.DeleteSparse(row, ref elm);
        ResultAssert.IsSuccess(r, tag);
        ArrayList<Object> remainingValues = new ArrayList<Object>(expected.Value);
        remainingValues.remove(indexToDelete);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope2.Clone(out elm);
        for (Object item : remainingValues) {
            assert elm.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref elm, out value);
            ResultAssert.IsSuccess(r, tag);
            boolean tempVar3 = item instanceof Array;
            Array array = tempVar3 ? (Array)item : null;
            if (tempVar3) {
                CollectionAssert.AreEqual(array, (Collection)value, tag);
            } else {
                Assert.AreEqual(item, value, tag);
            }
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        assert !scope2.Clone(out elm).MoveTo(row, remainingValues.size());

        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot).Find(row, arrCol.getPath());
        Reference<RowCursor> tempReference_roRoot =
            new Reference<RowCursor>(roRoot);
        ResultAssert.InsufficientPermissions(arrT.DeleteScope(row, tempReference_roRoot));
        roRoot = tempReference_roRoot.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(arrT.WriteScope(row, ref roRoot, arrCol.getTypeArgs().clone(),
            out scope2));

        // Overwrite the whole scope.
        Reference<RowCursor> tempReference_field3 =
            new Reference<RowCursor>(field);
        r = LayoutType.Null.writeSparse(row, tempReference_field3, NullValue.Default);
        field = tempReference_field3.get();
        ResultAssert.IsSuccess(r, tag);
        Reference<RowCursor> tempReference_field4 =
            new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__ =
            new Out<RowCursor>();
        r = arrT.ReadScope(row, tempReference_field4, tempOut__);
        _ = tempOut__.get();
        field = tempReference_field4.get();
        ResultAssert.TypeMismatch(r, tag);
        Reference<RowCursor> tempReference_field5 =
            new Reference<RowCursor>(field);
        r = arrT.DeleteScope(row, tempReference_field5);
        field = tempReference_field5.get();
        ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

        // Overwrite it again, then delete it.
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = arrT.WriteScope(row, ref field, arrCol.getTypeArgs().clone(), out _, UpdateOptions.Update);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field6 =
            new Reference<RowCursor>(field);
        r = arrT.DeleteScope(row, tempReference_field6);
        field = tempReference_field6.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field7 =
            new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__2 =
            new Out<RowCursor>();
        r = arrT.ReadScope(row, tempReference_field7, tempOut__2);
        _ = tempOut__2.get();
        field = tempReference_field7.get();
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn ArrCol;
        public Expected Expected = new Expected();

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.ArrCol = this.ArrCol;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public LayoutType Type;
        public ArrayList<Object> Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Type = this.Type;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseObject extends TestActionDispatcher<RoundTripSparseObject.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn objCol = closure.ObjCol;
        LayoutType tempVar = objCol.getType();
        LayoutObject objT = tempVar instanceof LayoutObject ? (LayoutObject)tempVar : null;
        LayoutColumn col = closure.Col;
        Expected expected = closure.Expected.clone();

        System.out.printf("%1$s" + "\r\n", col.getType().getName());
        Assert.IsNotNull(objT, "Json: {0}", expected.Json);
        Assert.AreEqual(objCol, col.getParent(), "Json: {0}", expected.Json);

        TLayout t = (TLayout)col.getType();

        // Attempt to read the object and the nested column.
        RowCursor field;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out field).Find(row, objCol.getPath());
        Reference<RowCursor> tempReference_field =
            new Reference<RowCursor>(field);
        RowCursor scope;
        Out<RowCursor> tempOut_scope =
            new Out<RowCursor>();
        Result r = objT.ReadScope(row, tempReference_field, tempOut_scope);
        scope = tempOut_scope.get();
        field = tempReference_field.get();
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);

        // Write the object and the nested column.
        Reference<RowCursor> tempReference_field2 =
            new Reference<RowCursor>(field);
        Out<RowCursor> tempOut_scope2 =
            new Out<RowCursor>();
        r = objT.WriteScope(row, tempReference_field2, objCol.getTypeArgs().clone(), tempOut_scope2);
        scope = tempOut_scope2.get();
        field = tempReference_field2.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

        // Verify the nested field doesn't yet appear within the new scope.
        RowCursor nestedField;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.Clone(out nestedField).Find(row, col.getPath());
        TValue value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref nestedField, out value);
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);

        // Write the nested field.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.WriteSparse(row, ref nestedField, (TValue)expected.Value);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

        // Read the object and the nested column, validate the nested column has the proper value.
        Reference<RowCursor> tempReference_field3 =
            new Reference<RowCursor>(field);
        RowCursor scope2;
        Out<RowCursor> tempOut_scope2 =
            new Out<RowCursor>();
        r = objT.ReadScope(row, tempReference_field3, tempOut_scope2);
        scope2 = tempOut_scope2.get();
        field = tempReference_field3.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Assert.AreEqual(scope.ScopeType, scope2.ScopeType, "Json: {0}", expected.Json);
        Assert.AreEqual(scope.start(), scope2.start(), "Json: {0}", expected.Json);
        Assert.AreEqual(scope.Immutable, scope2.Immutable, "Json: {0}", expected.Json);

        // Read the nested field
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope2.Clone(out nestedField).Find(row, col.getPath());
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref nestedField, out value);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        boolean tempVar2 = expected.Value instanceof Array;
        Array array = tempVar2 ? (Array)expected.Value : null;
        if (tempVar2) {
            CollectionAssert.AreEqual(array, (Collection)value, "Json: {0}", expected.Json);
        } else {
            Assert.AreEqual(expected.Value, value, "Json: {0}", expected.Json);
        }

        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot).Find(row, objCol.getPath());
        Reference<RowCursor> tempReference_roRoot =
            new Reference<RowCursor>(roRoot);
        ResultAssert.InsufficientPermissions(objT.DeleteScope(row, tempReference_roRoot));
        roRoot = tempReference_roRoot.get();
        Reference<RowCursor> tempReference_roRoot2 =
            new Reference<RowCursor>(roRoot);
        Out<RowCursor> tempOut_scope22 =
            new Out<RowCursor>();
        ResultAssert.InsufficientPermissions(objT.WriteScope(row, tempReference_roRoot2, objCol.getTypeArgs().clone(),
            tempOut_scope22));
        scope2 = tempOut_scope22.get();
        roRoot = tempReference_roRoot2.get();

        // Overwrite the whole scope.
        Reference<RowCursor> tempReference_field4 =
            new Reference<RowCursor>(field);
        r = LayoutType.Null.writeSparse(row, tempReference_field4, NullValue.Default);
        field = tempReference_field4.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field5 =
            new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__ =
            new Out<RowCursor>();
        r = objT.ReadScope(row, tempReference_field5, tempOut__);
        _ = tempOut__.get();
        field = tempReference_field5.get();
        ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field6 =
            new Reference<RowCursor>(field);
        r = objT.DeleteScope(row, tempReference_field6);
        field = tempReference_field6.get();
        ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

        // Overwrite it again, then delete it.
        Reference<RowCursor> tempReference_field7 =
            new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__2 =
            new Out<RowCursor>();
        r = objT.WriteScope(row, tempReference_field7, objCol.getTypeArgs().clone(), tempOut__2, UpdateOptions.Update);
        _ = tempOut__2.get();
        field = tempReference_field7.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field8 =
            new Reference<RowCursor>(field);
        r = objT.DeleteScope(row, tempReference_field8);
        field = tempReference_field8.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field9 =
            new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__3 =
            new Out<RowCursor>();
        r = objT.ReadScope(row, tempReference_field9, tempOut__3);
        _ = tempOut__3.get();
        field = tempReference_field9.get();
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn Col;
        public Expected Expected = new Expected();
        public LayoutColumn ObjCol;

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.ObjCol = this.ObjCol;
            varCopy.Col = this.Col;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public Object Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseObjectMulti extends TestActionDispatcher<RoundTripSparseObjectMulti.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> scope,
                                           Closure closure) {
        LayoutColumn col = closure.Col;
        Property prop = closure.Prop.clone();
        Expected expected = closure.Expected.clone();
        String tag = String.format("Prop: %2$s: Json: %1$s", expected.Json, prop.Path);

        System.out.println(tag);

        TLayout t = (TLayout)col.getType();

        // Verify the nested field doesn't yet appear within the new scope.
        RowCursor nestedField;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.get().Clone(out nestedField).Find(row, col.getPath());
        TValue value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        Result r = t.ReadSparse(row, ref nestedField, out value);
        Assert.IsTrue(r == Result.NotFound || r == Result.TypeMismatch, tag);

        // Write the nested field.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.WriteSparse(row, ref nestedField, (TValue)prop.Value);
        ResultAssert.IsSuccess(r, tag);

        // Read the nested field
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref nestedField, out value);
        ResultAssert.IsSuccess(r, tag);
        boolean tempVar = prop.Value instanceof Array;
        Array array = tempVar ? (Array)prop.Value : null;
        if (tempVar) {
            CollectionAssert.AreEqual(array, (Collection)value, tag);
        } else {
            Assert.AreEqual(prop.Value, value, tag);
        }

        // Overwrite the nested field.
        if (t instanceof LayoutNull) {
            Reference<RowCursor> tempReference_nestedField =
                new Reference<RowCursor>(nestedField);
            r = LayoutType.Boolean.WriteSparse(row, tempReference_nestedField, false);
            nestedField = tempReference_nestedField.get();
            ResultAssert.IsSuccess(r, tag);
        } else {
            Reference<RowCursor> tempReference_nestedField2 =
                new Reference<RowCursor>(nestedField);
            r = LayoutType.Null.writeSparse(row, tempReference_nestedField2, NullValue.Default);
            nestedField = tempReference_nestedField2.get();
            ResultAssert.IsSuccess(r, tag);
        }

        // Verify nested field no longer there.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref nestedField, out value);
        ResultAssert.TypeMismatch(r, tag);
    }

    @Override
    public void DispatchObject(Reference<RowBuffer> row, Reference<RowCursor> scope,
                               Closure closure) {
        LayoutColumn col = closure.Col;
        Property prop = closure.Prop.clone();
        Expected expected = closure.Expected.clone();
        String tag = String.format("Prop: %2$s: Json: %1$s", expected.Json, prop.Path);

        System.out.println(tag);

        LayoutObject t = (LayoutObject)col.getType();

        // Verify the nested field doesn't yet appear within the new scope.
        RowCursor nestedField;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.get().Clone(out nestedField).Find(row, col.getPath());
        Reference<RowCursor> tempReference_nestedField =
            new Reference<RowCursor>(nestedField);
        RowCursor scope2;
        Out<RowCursor> tempOut_scope2 =
            new Out<RowCursor>();
        Result r = t.ReadScope(row, tempReference_nestedField, tempOut_scope2);
        scope2 = tempOut_scope2.get();
        nestedField = tempReference_nestedField.get();
        ResultAssert.NotFound(r, tag);

        // Write the nested field.
        Reference<RowCursor> tempReference_nestedField2 =
            new Reference<RowCursor>(nestedField);
        Out<RowCursor> tempOut_scope22 =
            new Out<RowCursor>();
        r = t.WriteScope(row, tempReference_nestedField2, col.getTypeArgs().clone(), tempOut_scope22);
        scope2 = tempOut_scope22.get();
        nestedField = tempReference_nestedField2.get();
        ResultAssert.IsSuccess(r, tag);

        // Read the nested field
        Reference<RowCursor> tempReference_nestedField3 =
            new Reference<RowCursor>(nestedField);
        RowCursor scope3;
        Out<RowCursor> tempOut_scope3 =
            new Out<RowCursor>();
        r = t.ReadScope(row, tempReference_nestedField3, tempOut_scope3);
        scope3 = tempOut_scope3.get();
        nestedField = tempReference_nestedField3.get();
        ResultAssert.IsSuccess(r, tag);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Assert.AreEqual(scope2.AsReadOnly(out _).ScopeType, scope3.ScopeType, tag);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Assert.AreEqual(scope2.AsReadOnly(out _).start, scope3.start(), tag);

        // Overwrite the nested field.
        Reference<RowCursor> tempReference_nestedField4 =
            new Reference<RowCursor>(nestedField);
        r = LayoutType.Null.writeSparse(row, tempReference_nestedField4, NullValue.Default);
        nestedField = tempReference_nestedField4.get();
        ResultAssert.IsSuccess(r, tag);

        // Verify nested field no longer there.
        Reference<RowCursor> tempReference_nestedField5 =
            new Reference<RowCursor>(nestedField);
        Out<RowCursor> tempOut_scope32 =
            new Out<RowCursor>();
        r = t.ReadScope(row, tempReference_nestedField5, tempOut_scope32);
        scope3 = tempOut_scope32.get();
        nestedField = tempReference_nestedField5.get();
        ResultAssert.TypeMismatch(r, tag);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn Col;
        public Expected Expected = new Expected();
        public Property Prop = new Property();

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.Col = this.Col;
            varCopy.Prop = this.Prop.clone();
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public Property[] Props;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Props = this.Props.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Property
    public final static class Property {
        public String Path;
        public Object Value;

        public Property clone() {
            Property varCopy = new Property();

            varCopy.Path = this.Path;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseObjectNested extends TestActionDispatcher<RoundTripSparseObjectNested.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn col = closure.Col;
        Property prop = closure.Prop.clone();
        Expected expected = closure.Expected.clone();
        String tag = String.format("Prop: %2$s: Json: %1$s", expected.Json, prop.Path);

        System.out.println(tag);

        TLayout t = (TLayout)col.getType();

        // Ensure scope exists.
        RowCursor scope = LayoutCompilerUnitTests.EnsureScope(row, root, col.getParent(), tag);

        // Write the nested field.
        RowCursor field;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.Clone(out field).Find(row, col.getPath());
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        Result r = t.WriteSparse(row, ref field, (TValue)prop.Value);
        ResultAssert.IsSuccess(r, tag);

        // Read the nested field
        TValue value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref field, out value);
        ResultAssert.IsSuccess(r, tag);
        boolean tempVar = prop.Value instanceof Array;
        Array array = tempVar ? (Array)prop.Value : null;
        if (tempVar) {
            CollectionAssert.AreEqual(array, (Collection)value, tag);
        } else {
            Assert.AreEqual(prop.Value, value, tag);
        }

        // Overwrite the nested field.
        if (t instanceof LayoutNull) {
            Reference<RowCursor> tempReference_field =
                new Reference<RowCursor>(field);
            r = LayoutType.Boolean.WriteSparse(row, tempReference_field, false);
            field = tempReference_field.get();
            ResultAssert.IsSuccess(r, tag);
        } else {
            Reference<RowCursor> tempReference_field2 =
                new Reference<RowCursor>(field);
            r = LayoutType.Null.writeSparse(row, tempReference_field2, NullValue.Default);
            field = tempReference_field2.get();
            ResultAssert.IsSuccess(r, tag);
        }

        // Verify nested field no longer there.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref field, out value);
        ResultAssert.TypeMismatch(r, tag);
    }

    @Override
    public void DispatchObject(Reference<RowBuffer> row, Reference<RowCursor> root, Closure closure) {
        LayoutColumn col = closure.Col;
        Property prop = closure.Prop.clone();
        Expected expected = closure.Expected.clone();
        String tag = String.format("Prop: %2$s: Json: %1$s", expected.Json, prop.Path);

        System.out.println(tag);

        // Ensure scope exists.
        RowCursor scope = LayoutCompilerUnitTests.EnsureScope(row, root, col, tag);
        assert root.get().clone() != scope.clone();
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn Col;
        public Expected Expected = new Expected();
        public Property Prop = new Property();

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.Col = this.Col;
            varCopy.Prop = this.Prop.clone();
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public Property[] Props;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Props = this.Props.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Property
    public final static class Property {
        public String Path;
        public Object Value;

        public Property clone() {
            Property varCopy = new Property();

            varCopy.Path = this.Path;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseOrdering extends TestActionDispatcher<RoundTripSparseOrdering.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutType type = closure.Expected.Type;
        String path = closure.Expected.Path;
        Object exValue = closure.Expected.Value;
        String json = closure.Json;

        TLayout t = (TLayout)type;
        TValue value = (TValue)exValue;
        RowCursor field;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out field).Find(row, path);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        Result r = t.WriteSparse(row, ref field, value);
        ResultAssert.IsSuccess(r, "Json: {0}", json);
        Out<TValue> tempOut_value = new Out<TValue>();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref field, tempOut_value);
        value = tempOut_value.get();
        ResultAssert.IsSuccess(r, "Json: {0}", json);
        boolean tempVar = exValue instanceof Array;
        Array array = tempVar ? (Array)exValue : null;
        if (tempVar) {
            CollectionAssert.AreEqual(array, (Collection)value, "Json: {0}", json);
        } else {
            Assert.AreEqual(exValue, value, "Json: {0}", json);
        }

        if (t instanceof LayoutNull) {
            Reference<RowCursor> tempReference_field =
                new Reference<RowCursor>(field);
            r = LayoutType.Boolean.WriteSparse(row, tempReference_field, false);
            field = tempReference_field.get();
            ResultAssert.IsSuccess(r, "Json: {0}", json);
            Out<TValue> tempOut_value2 = new Out<TValue>();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref field, tempOut_value2);
            value = tempOut_value2.get();
            ResultAssert.TypeMismatch(r, "Json: {0}", json);
        } else {
            Reference<RowCursor> tempReference_field2 =
                new Reference<RowCursor>(field);
            r = LayoutType.Null.writeSparse(row, tempReference_field2, NullValue.Default);
            field = tempReference_field2.get();
            ResultAssert.IsSuccess(r, "Json: {0}", json);
            Out<TValue> tempOut_value3 = new Out<TValue>();
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref field, tempOut_value3);
            value = tempOut_value3.get();
            ResultAssert.TypeMismatch(r, "Json: {0}", json);
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public Expected Expected = new Expected();
        public String Json;

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.Json = this.Json;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Path;
        public LayoutType Type;
        public Object Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Path = this.Path;
            varCopy.Type = this.Type;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseSet extends TestActionDispatcher<RoundTripSparseSet.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn setCol = closure.SetCol;
        LayoutType tempVar = setCol.getType();
        LayoutUniqueScope setT = tempVar instanceof LayoutUniqueScope ? (LayoutUniqueScope)tempVar : null;
        Expected expected = closure.Expected.clone();
        String tag = String.format("Json: %1$s, Set: %2$s", expected.Json, setCol.getType().getName());

        System.out.println(tag);
        Assert.IsNotNull(setT, tag);

        TLayout t = (TLayout)expected.Type;

        // Verify the Set doesn't yet exist.
        RowCursor field;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out field).Find(row, setCol.getPath());
        Reference<RowCursor> tempReference_field =
            new Reference<RowCursor>(field);
        RowCursor scope;
        Out<RowCursor> tempOut_scope =
            new Out<RowCursor>();
        Result r = setT.ReadScope(row, tempReference_field, tempOut_scope);
        scope = tempOut_scope.get();
        field = tempReference_field.get();
        ResultAssert.NotFound(r, tag);

        // Write the Set.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = setT.WriteScope(row, ref field, setCol.getTypeArgs().clone(), out scope);
        ResultAssert.IsSuccess(r, tag);

        // Verify the nested field doesn't yet appear within the new scope.
        assert !scope.MoveNext(row);
        TValue value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref scope, out value);
        ResultAssert.NotFound(r, tag);

        // Write the nested fields.
        for (Object v1 : expected.Value) {
            // Write the ith item into staging storage.
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.WriteSparse(row, ref tempCursor, (TValue)v1);
            ResultAssert.IsSuccess(r, tag);

            // Move item into the set.
            Reference<RowCursor> tempReference_scope =
                new Reference<RowCursor>(scope);
            Reference<RowCursor> tempReference_tempCursor =
                new Reference<RowCursor>(tempCursor);
            r = setT.MoveField(row, tempReference_scope, tempReference_tempCursor);
            tempCursor = tempReference_tempCursor.get();
            scope = tempReference_scope.get();
            ResultAssert.IsSuccess(r, tag);
        }

        // Attempts to insert the same items into the set again will fail.
        for (Object v2 : expected.Value) {
            // Write the ith item into staging storage.
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.WriteSparse(row, ref tempCursor, (TValue)v2);
            ResultAssert.IsSuccess(r, tag);

            // Move item into the set.
            Reference<RowCursor> tempReference_scope2 =
                new Reference<RowCursor>(scope);
            Reference<RowCursor> tempReference_tempCursor2 =
                new Reference<RowCursor>(tempCursor);
            r = setT.MoveField(row, tempReference_scope2, tempReference_tempCursor2, UpdateOptions.Insert);
            tempCursor = tempReference_tempCursor2.get();
            scope = tempReference_scope2.get();
            ResultAssert.Exists(r, tag);
        }

        // Read the Set and the nested column, validate the nested column has the proper value.
        Reference<RowCursor> tempReference_field2 =
            new Reference<RowCursor>(field);
        RowCursor scope2;
        Out<RowCursor> tempOut_scope2 =
            new Out<RowCursor>();
        r = setT.ReadScope(row, tempReference_field2, tempOut_scope2);
        scope2 = tempOut_scope2.get();
        field = tempReference_field2.get();
        ResultAssert.IsSuccess(r, tag);
        Assert.AreEqual(scope.ScopeType, scope2.ScopeType, tag);
        Assert.AreEqual(scope.start(), scope2.start(), tag);
        Assert.AreEqual(scope.Immutable, scope2.Immutable, tag);

        // Read the nested fields
        Reference<RowCursor> tempReference_field3 =
            new Reference<RowCursor>(field);
        Out<RowCursor> tempOut_scope2 =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(setT.ReadScope(row, tempReference_field3, tempOut_scope2));
        scope = tempOut_scope2.get();
        field = tempReference_field3.get();
        for (Object item : expected.Value) {
            assert scope.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref scope, out value);
            ResultAssert.IsSuccess(r, tag);
            boolean tempVar2 = item instanceof Array;
            Array array = tempVar2 ? (Array)item : null;
            if (tempVar2) {
                CollectionAssert.AreEqual(array, (Collection)value, tag);
            } else {
                Assert.AreEqual(item, value, tag);
            }
        }

        // Delete all of the items and then insert them again in the opposite order.
        Reference<RowCursor> tempReference_field4 =
            new Reference<RowCursor>(field);
        Out<RowCursor> tempOut_scope3 =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(setT.ReadScope(row, tempReference_field4, tempOut_scope3));
        scope = tempOut_scope3.get();
        field = tempReference_field4.get();
        for (int i = 0; i < expected.Value.size(); i++) {
            assert scope.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.DeleteSparse(row, ref scope);
            ResultAssert.IsSuccess(r, tag);
        }

        Reference<RowCursor> tempReference_field5 =
            new Reference<RowCursor>(field);
        Out<RowCursor> tempOut_scope4 =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(setT.ReadScope(row, tempReference_field5, tempOut_scope4));
        scope = tempOut_scope4.get();
        field = tempReference_field5.get();
        for (int i = expected.Value.size() - 1; i >= 0; i--) {
            // Write the ith item into staging storage.
            RowCursor tempCursor;
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            root.get().Clone(out tempCursor).Find(row, Utf8String.Empty);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.WriteSparse(row, ref tempCursor, (TValue)expected.Value.get(i));
            ResultAssert.IsSuccess(r, tag);

            // Move item into the set.
            Reference<RowCursor> tempReference_scope3 =
                new Reference<RowCursor>(scope);
            Reference<RowCursor> tempReference_tempCursor3 =
                new Reference<RowCursor>(tempCursor);
            r = setT.MoveField(row, tempReference_scope3, tempReference_tempCursor3);
            tempCursor = tempReference_tempCursor3.get();
            scope = tempReference_scope3.get();
            ResultAssert.IsSuccess(r, tag);
        }

        // Verify they still enumerate in sorted order.
        Reference<RowCursor> tempReference_field6 =
            new Reference<RowCursor>(field);
        Out<RowCursor> tempOut_scope5 =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(setT.ReadScope(row, tempReference_field6, tempOut_scope5));
        scope = tempOut_scope5.get();
        field = tempReference_field6.get();
        for (Object item : expected.Value) {
            assert scope.MoveNext(row);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref scope, out value);
            ResultAssert.IsSuccess(r, tag);
            boolean tempVar3 = item instanceof Array;
            Array array = tempVar3 ? (Array)item : null;
            if (tempVar3) {
                CollectionAssert.AreEqual(array, (Collection)value, tag);
            } else {
                Assert.AreEqual(item, value, tag);
            }
        }

        // Delete one item.
        if (expected.Value.size() > 1) {
            int indexToDelete = 1;
            Reference<RowCursor> tempReference_field7 =
                new Reference<RowCursor>(field);
            Out<RowCursor> tempOut_scope6 = new Out<RowCursor>();
            ResultAssert.IsSuccess(setT.ReadScope(row, tempReference_field7, tempOut_scope6));
            scope = tempOut_scope6.get();
            field = tempReference_field7.get();
            assert scope.MoveTo(row, indexToDelete);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
            r = t.DeleteSparse(row, ref scope);
            ResultAssert.IsSuccess(r, tag);
            ArrayList<Object> remainingValues = new ArrayList<Object>(expected.Value);
            remainingValues.remove(indexToDelete);

            Reference<RowCursor> tempReference_field8 = new Reference<RowCursor>(field);
            Out<RowCursor> tempOut_scope7 = new Out<RowCursor>();
            ResultAssert.IsSuccess(setT.ReadScope(row, tempReference_field8, tempOut_scope7));
            scope = tempOut_scope7.get();
            field = tempReference_field8.get();
            for (Object item : remainingValues) {
                assert scope.MoveNext(row);
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
                r = t.ReadSparse(row, ref scope, out value);
                ResultAssert.IsSuccess(r, tag);
                boolean tempVar4 = item instanceof Array;
                Array array = tempVar4 ? (Array)item : null;
                if (tempVar4) {
                    CollectionAssert.AreEqual(array, (Collection)value, tag);
                } else {
                    Assert.AreEqual(item, value, tag);
                }
            }

            assert !scope.MoveTo(row, remainingValues.size());
        }

        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot).Find(row, setCol.getPath());
        Reference<RowCursor> tempReference_roRoot = new Reference<RowCursor>(roRoot);
        ResultAssert.InsufficientPermissions(setT.DeleteScope(row, tempReference_roRoot));
        roRoot = tempReference_roRoot.get();
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(setT.WriteScope(row, ref roRoot, setCol.getTypeArgs().clone(), out _));

        // Overwrite the whole scope.
        Reference<RowCursor> tempReference_field9 = new Reference<RowCursor>(field);
        r = LayoutType.Null.writeSparse(row, tempReference_field9, NullValue.Default);
        field = tempReference_field9.get();
        ResultAssert.IsSuccess(r, tag);
        Reference<RowCursor> tempReference_field10 = new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__ = new Out<RowCursor>();
        r = setT.ReadScope(row, tempReference_field10, tempOut__);
        _ = tempOut__.get();
        field = tempReference_field10.get();
        ResultAssert.TypeMismatch(r, tag);
        Reference<RowCursor> tempReference_field11 = new Reference<RowCursor>(field);
        r = setT.DeleteScope(row, tempReference_field11);
        field = tempReference_field11.get();
        ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

        // Overwrite it again, then delete it.
        Reference<RowCursor> tempReference_field12 = new Reference<RowCursor>(field);
        RowCursor _;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        r = setT.WriteScope(row, tempReference_field12, setCol.getTypeArgs().clone(), out _, UpdateOptions.Update);
        field = tempReference_field12.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field13 = new Reference<RowCursor>(field);
        r = setT.DeleteScope(row, tempReference_field13);
        field = tempReference_field13.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        Reference<RowCursor> tempReference_field14 = new Reference<RowCursor>(field);
        RowCursor _;
        Out<RowCursor> tempOut__2 = new Out<RowCursor>();
        r = setT.ReadScope(row, tempReference_field14, tempOut__2);
        _ = tempOut__2.get();
        field = tempReference_field14.get();
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public Expected Expected = new Expected();
        public LayoutColumn SetCol;

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.SetCol = this.SetCol;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public LayoutType Type;
        public ArrayList<Object> Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Type = this.Type;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private final static class RoundTripSparseSimple extends TestActionDispatcher<RoundTripSparseSimple.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn col = closure.Col;
        Expected expected = closure.Expected.clone();

        System.out.printf("%1$s" + "\r\n", col.getType().getName());
        TLayout t = (TLayout)col.getType();
        RowCursor field;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().Clone(out field).Find(row, col.getPath());
        TValue value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        Result r = t.ReadSparse(row, ref field, out value);
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.WriteSparse(row, ref field, (TValue)expected.Value, UpdateOptions.Update);
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.WriteSparse(row, ref field, (TValue)expected.Value);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.WriteSparse(row, ref field, (TValue)expected.Value, UpdateOptions.Insert);
        ResultAssert.Exists(r, "Json: {0}", expected.Json);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref field, out value);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        boolean tempVar = expected.Value instanceof Array;
        Array array = tempVar ? (Array)expected.Value : null;
        if (tempVar) {
            CollectionAssert.AreEqual(array, (Collection)value, "Json: {0}", expected.Json);
        } else {
            Assert.AreEqual(expected.Value, value, "Json: {0}", expected.Json);
        }

        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot).Find(row, col.getPath());
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(t.DeleteSparse(row, ref roRoot));
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        ResultAssert.InsufficientPermissions(t.WriteSparse(row, ref roRoot, (TValue)expected.Value,
            UpdateOptions.Update));

        if (t instanceof LayoutNull) {
            Reference<RowCursor> tempReference_field =
                new Reference<RowCursor>(field);
            r = LayoutType.Boolean.WriteSparse(row, tempReference_field, false);
            field = tempReference_field.get();
            ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref field, out value);
            ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);
        } else {
            Reference<RowCursor> tempReference_field2 =
                new Reference<RowCursor>(field);
            r = LayoutType.Null.writeSparse(row, tempReference_field2, NullValue.Default);
            field = tempReference_field2.get();
            ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
            // cannot be converted using the 'Out' helper class unless the method is within the code being
            // modified:
            // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
            // cannot be converted using the 'Ref' helper class unless the method is within the code being
            // modified:
            r = t.ReadSparse(row, ref field, out value);
            ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);
        }

        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.DeleteSparse(row, ref field);
        ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

        // Overwrite it again, then delete it.
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.WriteSparse(row, ref field, (TValue)expected.Value, UpdateOptions.Update);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.DeleteSparse(row, ref field);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword - these
        // cannot be converted using the 'Ref' helper class unless the method is within the code being modified:
        r = t.ReadSparse(row, ref field, out value);
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn Col;
        public Expected Expected = new Expected();

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.Col = this.Col;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public Object Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Value = this.Value;

            return varCopy;
        }
    }
}

private static class RoundTripVariable extends TestActionDispatcher<RoundTripVariable.Closure> {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        LayoutColumn col = closure.Col;
        Expected expected = closure.Expected.clone();

        System.out.printf("%1$s" + "\r\n", expected.Json);

        this.<TLayout, TValue>RoundTrip(row, root, col, expected.Value, expected.clone());
    }

    protected final <TLayout extends LayoutType<TValue>, TValue> void Compare(Reference<RowBuffer> row,
                                                                              Reference<RowCursor> root, LayoutColumn col, Object exValue, Expected expected) {
        TLayout t = (TLayout)col.getType();
        TValue value;
        Out<TValue> tempOut_value = new Out<TValue>();
        Result r = t.readVariable(row, root, col, tempOut_value);
        value = tempOut_value.get();
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        boolean tempVar = exValue instanceof Array;
        Array array = tempVar ? (Array)exValue : null;
        if (tempVar) {
            CollectionAssert.AreEqual(array, (Collection)value, "Json: {0}", expected.Json);
        } else {
            Assert.AreEqual(exValue, value, "Json: {0}", expected.Json);
        }
    }

    protected final <TLayout extends LayoutType<TValue>, TValue> void RoundTrip(Reference<RowBuffer> row,
                                                                                Reference<RowCursor> root, LayoutColumn col, Object exValue, Expected expected) {
        TLayout t = (TLayout)col.getType();
        Result r = t.writeVariable(row, root, col, (TValue)exValue);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        this.<TLayout, TValue>Compare(row, root, col, exValue, expected.clone());

        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot);
        Reference<RowCursor> tempReference_roRoot =
            new Reference<RowCursor>(roRoot);
        ResultAssert.InsufficientPermissions(t.writeVariable(row, tempReference_roRoot, col, (TValue)expected.Value));
        roRoot = tempReference_roRoot.get();
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Closure
    public final static class Closure {
        public LayoutColumn Col;
        public Expected Expected = new Expected();
        public Layout Layout;

        public Closure clone() {
            Closure varCopy = new Closure();

            varCopy.Col = this.Col;
            varCopy.Layout = this.Layout;
            varCopy.Expected = this.Expected.clone();

            return varCopy;
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: public struct Expected
    public final static class Expected {
        public String Json;
        public Object Long;
        public Object Short;
        public Object TooBig;
        public Object Value;

        public Expected clone() {
            Expected varCopy = new Expected();

            varCopy.Json = this.Json;
            varCopy.Short = this.Short;
            varCopy.Value = this.Value;
            varCopy.Long = this.Long;
            varCopy.TooBig = this.TooBig;

            return varCopy;
        }
    }
}

private abstract static class TestActionDispatcher<TClosure> {
    public abstract <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> scope, TClosure closure);

    public void DispatchObject(Reference<RowBuffer> row, Reference<RowCursor> scope, TClosure closure) {
        Assert.Fail("not implemented");
    }
}

private final static class VariableInterleaving extends RoundTripVariable {
    @Override
    public <TLayout, TValue> void Dispatch(Reference<RowBuffer> row, Reference<RowCursor> root,
                                           Closure closure) {
        Layout layout = closure.Layout;
        Expected expected = closure.Expected.clone();

        System.out.printf("%1$s" + "\r\n", expected.Json);

        LayoutColumn a = this.<TLayout, TValue>Verify(row, root, layout, "a", expected.clone());
        LayoutColumn b = this.<TLayout, TValue>Verify(row, root, layout, "b", expected.clone());
        LayoutColumn c = this.<TLayout, TValue>Verify(row, root, layout, "c", expected.clone());

        this.<TLayout, TValue>RoundTrip(row, root, b, expected.Value, expected.clone());
        this.<TLayout, TValue>RoundTrip(row, root, a, expected.Value, expected.clone());
        this.<TLayout, TValue>RoundTrip(row, root, c, expected.Value, expected.clone());

        // Make the var column shorter.
        int rowSizeBeforeShrink = row.get().length();
        this.<TLayout, TValue>RoundTrip(row, root, a, expected.Short, expected.clone());
        this.<TLayout, TValue>Compare(row, root, c, expected.Value, expected.clone());
        int rowSizeAfterShrink = row.get().length();
        Assert.IsTrue(rowSizeAfterShrink < rowSizeBeforeShrink, "Json: {0}", expected.Json);

        // Make the var column longer.
        this.<TLayout, TValue>RoundTrip(row, root, a, expected.Long, expected.clone());
        this.<TLayout, TValue>Compare(row, root, c, expected.Value, expected.clone());
        int rowSizeAfterGrow = row.get().length();
        Assert.IsTrue(rowSizeAfterGrow > rowSizeAfterShrink, "Json: {0}", expected.Json);
        Assert.IsTrue(rowSizeAfterGrow > rowSizeBeforeShrink, "Json: {0}", expected.Json);

        // Check for size overflow errors.
        if (a.getSize() > 0) {
            this.<TLayout, TValue>TooBig(row, root, a, expected.clone());
        }

        // Delete the var column.
        this.<TLayout, TValue>Delete(row, root, b, expected.clone());
        this.<TLayout, TValue>Delete(row, root, c, expected.clone());
        this.<TLayout, TValue>Delete(row, root, a, expected.clone());
    }

    private <TLayout extends LayoutType<TValue>, TValue> void Delete(Reference<RowBuffer> row,
                                                                     Reference<RowCursor> root, LayoutColumn col, Expected expected) {
        TLayout t = (TLayout)col.getType();
        RowCursor roRoot;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        root.get().AsReadOnly(out roRoot);
        Reference<RowCursor> tempReference_roRoot =
            new Reference<RowCursor>(roRoot);
        ResultAssert.InsufficientPermissions(t.deleteVariable(row, tempReference_roRoot, col));
        roRoot = tempReference_roRoot.get();
        Result r = t.deleteVariable(row, root, col);
        ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
        TValue _;
        Out<TValue> tempOut__ = new Out<TValue>();
        r = t.readVariable(row, root, col, tempOut__);
        _ = tempOut__.get();
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
    }

    private <TLayout extends LayoutType<TValue>, TValue> void TooBig(Reference<RowBuffer> row,
                                                                     Reference<RowCursor> root, LayoutColumn col, Expected expected) {
        TLayout t = (TLayout)col.getType();
        Result r = t.writeVariable(row, root, col, (TValue)expected.TooBig);
        Assert.AreEqual(Result.TooBig, r, "Json: {0}", expected.Json);
    }

    private <TLayout extends LayoutType<TValue>, TValue> LayoutColumn Verify(Reference<RowBuffer> row,
                                                                             Reference<RowCursor> root, Layout layout, String path, Expected expected) {
        LayoutColumn col;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        boolean found = layout.TryFind(path, out col);
        Assert.IsTrue(found, "Json: {0}", expected.Json);
        assert col.Type.AllowVariable;
        TLayout t = (TLayout)col.Type;
        TValue _;
        Out<TValue> tempOut__ = new Out<TValue>();
        Result r = t.readVariable(row, root, col, tempOut__);
        _ = tempOut__.get();
        ResultAssert.NotFound(r, "Json: {0}", expected.Json);
        return col;
    }
}
}