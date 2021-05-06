%NugetMachineInstallRoot%\google.protobuf.tools\3.5.1\tools\windows_x64\protoc.exe --csharp_out=CassandraProto TestData\CassandraHotelSchema.proto --proto_path=. --proto_path=%NugetMachineInstallRoot%\google.protobuf.tools\3.5.1\tools

move CassandraProto\CassandraHotelSchema.cs CassandraProto\CassandraHotelSchema.tmp
echo #pragma warning disable DontUseNamespaceAliases // Namespace Aliases should be avoided > CassandraProto\CassandraHotelSchema.cs
echo #pragma warning disable NamespaceMatchesFolderStructure // Namespace Declarations must match folder structure >> CassandraProto\CassandraHotelSchema.cs
cat CassandraProto\CassandraHotelSchema.tmp >> CassandraProto\CassandraHotelSchema.cs
del CassandraProto\CassandraHotelSchema.tmp
