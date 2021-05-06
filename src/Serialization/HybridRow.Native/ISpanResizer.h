// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
namespace cdb_hr
{
  template<class T>
  struct ISpanResizer
  {
    ISpanResizer() noexcept = default;
    ISpanResizer(const ISpanResizer& other) noexcept = default;
    ISpanResizer(ISpanResizer&& other) noexcept = default;
    ISpanResizer& operator=(const ISpanResizer& other) noexcept = default;
    ISpanResizer& operator=(ISpanResizer&& other) noexcept = default;
    virtual ~ISpanResizer() = default;

    /// <summary>Resizes an existing a buffer.</summary>
    /// <typeparam name="T">The type of the elements of the memory.</typeparam>
    /// <param name="minimumLength">The minimum required length (in elements) of the memory.</param>
    /// <param name="buffer">
    /// Optional existing memory to be copied to the new buffer.  Ownership of <paramref name="buffer" /> is
    /// transferred as part of this call and it should not be used by the caller after this call completes.
    /// </param>
    /// <returns>
    /// A new memory whose size is <em>at least as big</em> as <paramref name="minimumLength" />
    /// and containing the content of <paramref name="buffer" />.
    /// </returns>
    virtual cdb_core::Span<T> Resize(uint32_t minimumLength, cdb_core::Span<T> buffer = cdb_core::Span<T>()) = 0;
  };
}
