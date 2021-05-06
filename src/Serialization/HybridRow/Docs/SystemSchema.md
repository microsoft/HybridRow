System Schema are globally available HybridRow Schema definitions. This
document summarizes the available schema namespaces and their reserved SchemaId
allocated from the [System Schema](./SchemaId.md) address space.

[[_TOC_]]

# System Schema Catalog
The following are System Schema namespaces defined by the HybridRow runtime:

* [**Microsoft.Azure.Cosmos.HybridRow.RecordIO**](./RecordIO.md): 
  Defines types used in streaming record-oriented files containing HybridRows.

# SchemaId Reserved by the HybridRow Runtime

* $2147473648$ - RecordIO Segment
* $2147473649$ - RecordIO Record
* $2147473650$ - Empty Schema
* $2147473651$ - Namespace Schema
* $2147473652$ - Schema Schema
* $2147473653$ - SchemaOptions Schema
* $2147473654$ - PartitionKey Schema
* $2147473655$ - PrimarySortKey Schema
* $2147473656$ - StaticKey Schema
* $2147473657$ - Property Schema
* $2147473658$ - PropertyType Schema
* $2147473659$ - PrimitivePropertyType Schema
* $2147473660$ - ScopePropertyType Schema
* $2147473661$ - ArrayPropertyType Schema
* $2147473662$ - ObjectPropertyType Schema
* $2147473663$ - UdtPropertyType Schema
* $2147473664$ - SetPropertyType Schema
* $2147473665$ - MapPropertyType Schema
* $2147473666$ - TuplePropertyType Schema
* $2147473667$ - TaggedPropertyType Schema
* $2147473668$ - EnumSchema Schema
* $2147473669$ - EnumValue Schema

# SchemaId Reserved by the Cosmos DB application
*These must be within [2145473647..2146473647]*

* $2145473647$ - HybridRow Query Response
* $2145473648$ - Batch API Operation
* $2145473649$ - Batch API Result
* $2145473650$ - Legacy Patch Request (to be deprecated)
* $2145473651$ - Legacy Patch Operation (to be deprecated)
* $2145473652$ - Json Schema
* $2145473653$ - Partition Key Delete Request
* $2145473654$ - Patch operation
* $2145473655$ - Patch Request
