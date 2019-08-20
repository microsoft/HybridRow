//------------------------------------------------------------
// Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

// General Information about an assembly is controlled through the following
// set of attributes. Change these attribute values to modify the information
// associated with an assembly.
[assembly: AssemblyTitle("Microsoft.Azure.Cosmos.Serialization.HybridRow")]

// Setting ComVisible to false makes the types in this assembly not visible
// to COM components.  If you need to access a type in this assembly from
// COM, set the ComVisible attribute to true on that type.
[assembly: ComVisible(false)]

// The following GUID is for the ID of the typelib if this project is exposed to COM
[assembly: Guid("490D42EE-1FEF-47CC-97E4-782A353B4D58")]

// Allow tests to see internals.
[assembly: InternalsVisibleTo("Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit" + AssemblyRef.TestPublicKey)]
[assembly: InternalsVisibleTo("Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf" + AssemblyRef.TestPublicKey)]
[assembly: InternalsVisibleTo("Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator" + AssemblyRef.TestPublicKey)]
[assembly: InternalsVisibleTo("Microsoft.Azure.Cosmos.Serialization.HybridRowStress" + AssemblyRef.TestPublicKey)]
