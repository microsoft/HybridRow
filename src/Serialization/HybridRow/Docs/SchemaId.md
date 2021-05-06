`SchemaId` are 32-bit unique identifiers used in Hybrid Row to uniquely
reference a Schema type within a Schema Namespace.  Schema have names, but
since multiple revisions of the same Schema may be defined within the same
Schema Namespace, the `SchemaId` distiguishes the revisions from each other.

[[_TOC_]]

# Hybrid Row Runtime
There is no `SchemaId` allocation policy imposed directly by the Hybrid Row
runtime, however, the runtime does require that all policies meet the
following requirements:

 * All `SchemaId` **MUST** be unique **within** a Schema Namespace.
 * The `SchemaId` of `0` is reserved as the `Invalid` `SchemaId` and 
 must never be assigned.


# Cosmos DB 
This section describes the set of convention used for `SchemaId` allocation
by [Azure Cosmos DB](https://azure.microsoft.com/en-us/services/cosmos-db/).

Cosmos DB's `SchemaId` policy sub-divides the available 32-bit numeric address
space for `SchemaId` into distinct non-overlapping regions and assigns distinct
semantics for each region:

Name | Range | Description
--- | --- | ---
Invalid | **0** | Reserved (by runtime) [\*](#bugs-and-known-issues)
Table Schema | [$-1000000$ .. $-1$] | Each revision of the table schema has a distinct *monotonically decreasing* numeric value.
UDT Schema | [$1$ .. $1000000$] | Each revision of each UDT schema has a distinct *monotonically increasing* numeric value.
System Schema | [$2147473648$ .. $2147483647$] <br/><br/> [`Int32.Max` - $9,999$ .. `Int32.Max`] | Reserved for system defined schema types.
Dynamic Schema |  [$2146473647$ .. $2147473647$]  <br/><br/> [`Int32.Max` - $1,010,000$ .. `Int32.Max` - $10,000$] | Reserved for context-specific schema generated dynamically (e.g. result set schema scoped to a channel.)
App-Specific Schema |  [$2145473647$ .. $2146473647$]  <br/><br/> [`Int32.Max` - $2,010,000$ .. `Int32.Max` - $1,009,999$] | Reserved for app-specific schema. (e.g. Batch API schema used by the Cosmos DB application/sdk.)

## Monotonicity
Cosmos DB allows for the evolution of Schema over time. As Schema evolve new 
revisions of their Schema are committed to the Schema Namespace. Because
existing encoded rows remain in the store referencing older revisions, all 
revisions of a given Schema referenced by at least one existing row **MUST**
be retained in the Schema Namespace. As Schema are evolved, later revisions
of a Schema are assigned `SchemaId` whose (*absolute*) value is larger than
any previous revision of that Schema. The latest revision of any given Schema
is the Schema within the Schema Namespace with both a matching name and the 
largest (*absolute*) `SchemaId` value.


## Bugs and Known Issues
A bug in the initial Cassandra GA code allocated some type schemas using the 
`SchemaId` `0` accidentally.