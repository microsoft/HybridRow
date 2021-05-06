This *glossary* defines some terms used either in the HybirdRow Library code, its
documentation, or in its applications.  

The definitions here are meant only to provide a common understanding when
implementing or consuming the HybridRow Library.

[[_TOC_]]

# Glossary

## General Terms

###### DDL
A Data Definition Language operation is one that defines a new [schema](#schema) or
redefines (ALTERs) an existing [schema](#schema). 

Since HybridRow [schema](#schema) are themselves immutable, a 
DDL operation that ALTERs a [schema](#schema) always defines a new [schema](#schema)
 with a distinct [SchemaId](#schemaid).  By convention the new [schema](#schema) has
the same name as previous schema being ALTERed and a new SchemaId whose absolutely value
is monotonically increasing relative to the old schema.  See [SchemaId.md](./SchemaId.md)
for details on how [SchemaId](#schemaid) are allocated.

###### Namespace
A set of [schema](#schema) with non-overlapping [SchemaId](#schemaid).

###### Schema
Describes the logical structure of a row at a particular point in time (relative to the
DDL operation history).

###### SchemaHash
A 128-bit hash of a HybridRow [schema](#schema). The hash captures only the logical
elements of a schema. Whitespace elements such as formatting or comments have no impact
on the hash.  See [SchemaHash.md](./SchemaHash.md) for more details.

###### SchemaId
An integer that uniquely defines a particular version of a schema within a
Namespace.


## Schema Versioning Terms

###### Latest (Schema) Version
The [SchemaId](#schemaid) of the latest known version of the schema (from the Backend's perspective). 

###### Row (Schema) Version
The [SchemaId](#schemaid) at which the stored row was encoded. When a row is read it is
**upgraded** through a process called **row upgrade** when the 
`Row Version < Latest Version`. 

###### Target (Schema) Version
An operation performed by the Front End (FE) or Client (referred to collectively as FE
below) is done in the context of its understanding of the current schema. 

The FE's view may trail the true Latest Version (`Target Version < Latest Version`) 
if the FE's schema cache is stale. The FE's view may lead the true Latest Version
(`Target Version > Latest Version`) if a DDL operation has happened but has not
yet propagated to the specific BE in question (propagation is asynchronous and
concurrent). 

The FE provides its view as the Target Version for each request. A Target Version 
is always relative to a request and describes the version targeted by that request. 
When the `Target Version < Latest Version`, the BE can still accept the request by
**upgrading** the request *before* applying it to the row. When the 
`Target Version > Latest Version`, the BE must reject the request. 
The FE is free to resubmit the request some time later in hopes that the BE has by then 
seen the DDL operation and subsequently raised the Latest Version to match. The BE will,
of course, continue to reject requests whose Target Version is greater than its 
Latest Version until the relavant DDL operation has successfully propagated.
This protection prevents the BE from writing rows that it would be unable to
immediately read.
