// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <stdexcept>

namespace cdb_hr
{
  class Schema;
  class Namespace;
  class Layout;

  /// <summary>Converts a logical schema into a physical layout.</summary>
  class LayoutCompiler final
  {
  public:
    LayoutCompiler() = delete; // static class

    /// <summary>Compiles a logical schema into a physical layout that can be used to read and write rows.</summary>
    /// <param name="ns">The namespace within which <paramref name="schema" /> is defined.</param>
    /// <param name="schema">The logical schema to produce a layout for.</param>
    /// <returns>The layout for the schema.</returns>
    static std::unique_ptr<Layout> Compile(const Namespace& ns, const Schema& schema) noexcept(false);

    struct LayoutCompilationException : std::runtime_error
    {
      LayoutCompilationException(const std::string& message) noexcept : std::runtime_error(message) { }
      LayoutCompilationException(const char* message) noexcept : std::runtime_error(message) { }
    };
  };
}
