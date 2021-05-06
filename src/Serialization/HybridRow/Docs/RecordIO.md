RecordIO refers to a class of streaming file formats that consist of
linear sequences of flat records. The metadata describing the records and the
encoding format of the records vary between different RecordIO incarnations.
This document describes a HybridRow RecordIO format.

[[_TOC_]]

# HybridRow RecordIO Stream Format
HybridRow RecordIO streams are described by the following
[EBNF grammar](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form):

```ebnf
record_io_stream  = record_io_segment, {record_io_segment};
record_io_segment = record_io_header, [record];
record_io_header  = "RecordIO Segment HybridRow";
record            = record_header, record_body;
record_header     = "RecordIO Record HybridRow";
record_body       = "Any HybridRow enocded row";
```

A HybridRow RecordIO stream consists of one or more RecordIO segments, each
segment consisting of a RecordIO segment header followed by zero or more 
records.

Each record consists of a record header which includes both the length
(in bytes) of the record body and an optional CRC. The record body can be any
encoded HybridRow.

Record bodies (serialized HybridRows) are limited to 2GB in length.

If a schema namespace is provided in the segment header, then all records
within the segment **MUST** conform to a schema type defined within that
schema namespace.


# RecordIO Schema
The segment and record headers are themselves HybridRow serialized values
described by [RecordIO System Schema](../SystemSchemas/RecordIOSchema.json).
