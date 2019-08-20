This document contains a brief EBNF-like grammar for describing the structure of a Hybrid Row.

[[_TOC_]]

# HybridRow Format
HybridRows are described by the following
[EBNF grammar](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form):

````ebnf
hybrid_row = hybrid_header, hybrid_body;
hybrid_header = version, schema_id;
hybrid_body = [schematized_fragment], { sparse_fragment };
schematized_fragment = presence_and_bool_bits, fixed_fields, var_fields;
version = uint8;
schema_id = int32; 
````


The presence of a *Schematized Fragment* is defined by the schema referenced by the schema_id.  
If the schema defines a Schematized Fragment, then one MUST be present.  If it does NOT define 
a Schematized Fragment, then one MUST NOT be present.  The order of fields within a 
Schematized Fragment, both fixed and variable length fields, is strictly defined by the schema 
and all fields MUST be present, or its absence MUST be indicated by unsetting the corresponding 
presence bit in the presence_bits field of the fragment.  A presence bit is defined for all 
nullable schematized fields.  Non-nullable fields are **always** present, and if unwritten contain
their type's default value.  

The formal specification of the Schematized Fragment is:

```ebnf
presence_and_bool_bits = ? 0+ bits, one for each nullable schematized field.  Additionally one for each boolean field ?;
fixed_fields = { fixed_field };
fixed_field = finite_field | bounded_field;
finite_field = literal_null_field 
    | bool_field 
    | integer_field 
    | float_field 
    | guid_field 
    | datetime_field
    | object_id_field;
bounded_field = string_field | binary_field;
var_fields = { var_field };
var_field = field_length, bounded_field | varint_field;
````

With the following primitive type definitions.  Note, unless otherwise states, all values are stored little-endian.

````ebnf
literal_null_field = ? no encoded payload ?;
bool_field = ? 1 byte, 0 FALSE, 1 TRUE ?;
integer_field = int8 | int16 | int32 | int64 | uint8 | uint16 | uint32 | uint64;
float_field = 
      float32 	(? 4 byte IEEE 754 floating point value ?)
    | float64 	(? 8 byte IEEE 754 floating point value ?)
    | float128 	(? 16 byte IEEE 754 floating point value ?)
    | decimal;	(? 16 byte System.Decimal value ?)
guid_field = ? 16 byte, little-endian ?;
datetime_field = precise_datatime_field | unix_datetime_field;
precise_datatime_field = ? 8 byte, 100ns since 00:00:00, January 1, 1 CE UTC ?;
unix_datetime_field = ? 8 byte, milliseconds since Unix Epoch (midnight, January 1, 1970 UTC) ?;
object_id_field = ? 12 unsigned bytes (in big-endian order)?;
string_field = ? UTF-8, not null-terminated ?;
binary_field = ? unsigned bytes ?;
field_length = varuint; 
varint_field = varint | varuint;
varint = ? varint – variable length signed integer encoding, sign-bit rotated ?;
varuint = ? varuint – variable length unsigned integer encoding ?;
````

Additionally, a row may contain zero or more *Sparse Fragments*.  The structure of Sparse Fragment 
may be described in full or in part within the schema.  Describing Sparse Fragments within the schema 
allows both for optional schema validation and path interning (See Paths).  

And Sparse Fragments:

```ebnf
sparse_fragment = { sparse_field };
sparse_field = type, path, sparse_value;
type = type_code | generic_type;
generic_type = 
      typed_array_type_code, type
    | nullable_type_code, type
    | typed_tuple_type_code, type, {type}
    | typed_set_type_code, type
    | typed_map_type_code, type, type 
    | hybrid_row_type_code, schema_id;
sparse_value = 
      null_field 
    | finite_field
    | var_field
    | array_field	    (? typed arrays Generic N = 1 ?)
    | obj_field
    | tuple_field    (? typed tuples are Generic N ?)
    | set_field      (? Generic N = 1 ?)
    | map_field		(? Generic N = 2 ?)
    | hybrid_body;	(? Generic N = 1 ?)
null_field = (? empty production ?);
array_field = typed_array_field | sparse_array_field;
typed_array_field = field_count, { sparse_value };
sparse_array_field = { type, sparse_value }, scope_end_symbol;
obj_field = { sparse_field }, scope_end_symbol;
nullable_field = bool_field, sparse_value;
tuple_field = sparse_value, {sparse_value};
set_field = field_count, { sparse_value };
map_field = field_count, { map_field_body };
map_field_body = sparse_value (? key ?), sparse_value (? value ?);
path = ? dictionary (@) encoded varint ? | field_length, string_lob_field;

````
