This directory contains types derived from 
[dotnet/corefxlab](https://github.com/dotnet/corefxlab) repo. This repo contains designs
proposed by the CLR team but not yet committed for inclusion in either the C# language
or the standard .NET Framework.  The types included here (e.g. Utf8Span) may **never**
appear in the official standard.  Including the types here lays a foundation for adopting
these types **if** they do become standard in the future.

[[_TOC_]]


## Utf8Span
A readonly struct wrapping a sequence of bytes that are guaranteed to be a valid UTF8
encoded string.

A `Utf8Span` can be created over a `ReadOnlySpan<byte>` at the cost of validating the
byte sequence.  Once the byte sequence has been validated then a `Utf8Span` can be passed
around safely without re-validating the content as UTF8.  The type system is used to 
enforce the correctness.

## Utf8String
A readonly class wrapping a sequence of bytes that are guaranteed to be a valid UTF8 
encoded string.

`Utf8String` is the heap equivalent of `Utf8Span` and provides the same capabilities.
`Utf8String` can be implicitly converted to `Utf8Span`.  This conversion is guaranteed
to be cheap and non-allocating.  Converting from a `Utf8Span` to a `Utf8String`, however, 
requires a blittable copy of the content (but not re-validation).  Additionally, `Utf8String`
can be converted to a `string` object via the expected transcode process.  This operation is expensive.

## UtfAnyString
A readonly struct wrapping either a `Utf8String` or a `string` object.  The `UtfAnyString`
enables API's to accept both UTF8 and UTF16 encoded strings without requiring overloads.  
UtfAnyString provides implicit conversion **from** either type, but explicit convert **to** 
either type (because such a conversion *may* require a transcoding copy).