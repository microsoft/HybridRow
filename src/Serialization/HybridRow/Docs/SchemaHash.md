`SchemaHash` is a 128-bit hash of a HybridRow schema.

[[_TOC_]]

# Schema Evolution
During schema evolution scenarios (e.g. DDL) it is necessary to ensure that the
historical record of previous schema versions has not been improperly altered
and that the new schema introduced (e.g. as a result of the DDL operation) are
a proper and valid superset of that history.  

If the historical record were altered or truncated then existing "old" rows
within the table might no longer be parsable, or their values may be
misinterpreted.  Because of potential changes to the schema language version,
and non-determinism in the natural encoding of JSON-based schemas (e.g.
ordering, commas, comments) textual comparison is both insufficient and
incorrect.  A logical comparison of the relevant schema structure using only
canonical formulizations that will be true across schema language versions
should be used.

`SchemaHash` defines exactly and only the necessary structural elements and
thus implements a method for calculating the logical schema version (hash) of a
schema given a namespace that contains that schema and its dependent
closure of types.  

Notes:
* The provided Namespace may contain additional schemas not related to the
  given schema (including other versions of that schema or other versions of
  its dependent types).
* `SchemaHash` applies to a particular schema at a time, not an entire
  namespace of schema.
* `SchemaHash` incorporates recursively the `SchemaHash` of each nested
  schema (aka UDTs) that appear in the schema closure of the type.  Thus a
  `SchemaHash` provides a snapshot *version* that uniquely describes a row's 
  metadata at a specific point in time.

# Algorithm
`SchemaHash` is computed as an accumulated Murmur hash.  The Little Endian,
x64, 128-bit [MurmurHash3](https://en.wikipedia.org/wiki/MurmurHash) algorithm
is to be used.  The hash is computed over the relevant structural elements.

The hash is accumulated by passing the current accumulated
hash as the seed for the next round of hashing, thus chaining the hash results
until all structures are hashed.

All structural elements of the schema are hashed as individual blocks encoding
each block as the little-endian byte sequence with the following caveats:

* null values are skipped (contribute nothing to the accumulation).
* strings are hashed as their canonicalized UTF8 byte sequence without either
  the length or null-termination.
* bools are hashed as a single byte: 1 for true and 0 for false.
* Lists are hashed by hashing their element in the ordered sequence in which they
  appear in the SDL.  Order matters.  Empty lists are treated as nulls.