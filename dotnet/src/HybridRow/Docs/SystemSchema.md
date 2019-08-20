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

# SchemaId Reserved by the Cosmos DB application
*These must be within [2145473647..2146473647]*

* $2145473647$ - HybridRow Query Response
* $2145473648$ - Batch API Operation
* $2145473649$ - Batch API Result
* $2145473650$ - Patch Request
* $2145473651$ - Patch Operation
* $2145473652$ - Json Schema