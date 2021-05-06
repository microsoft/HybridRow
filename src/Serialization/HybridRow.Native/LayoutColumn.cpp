// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include <sstream>
#include "LayoutType.h"
#include "LayoutColumn.h"
#include "LayoutCode.h"
#include "LayoutCodeTraits.h"

namespace cdb_hr
{
  LayoutColumn::LayoutColumn(std::string_view path, const LayoutType* type, TypeArgumentList typeArgs,
                             StorageKind storage, const LayoutColumn* parent,
                             uint32_t index, uint32_t offset, LayoutBit nullBit, LayoutBit boolBit,
                             uint32_t length) noexcept :
    m_size(type->IsFixed() ? type->GetSize() : length),
    m_path(path),
    m_fullPath(GetFullPath(parent, m_path)),
    m_typeArg(type, std::move(typeArgs)),
    m_storage(storage),
    m_parent(parent),
    m_index(index),
    m_offset(offset),
    m_nullBit(nullBit),
    m_boolBit(boolBit) { }

  tla::string LayoutColumn::GetFullPath(const LayoutColumn* parent, std::string_view path) noexcept
  {
    if (parent != nullptr)
    {
      switch (LayoutCodeTraits::ClearImmutableBit(parent->m_typeArg.GetType()->GetLayoutCode()))
      {
      case LayoutCode::ObjectScope:
      case LayoutCode::Schema:
        try
        {
          return tla::string((std::ostringstream() << parent->GetFullPath() << "."sv << path).str());
        }
        catch (std::bad_alloc&)
        {
          cdb_core::Contract::Fail("Unable to allocate memory.");
        }
      case LayoutCode::ArrayScope:
      case LayoutCode::TypedArrayScope:
      case LayoutCode::TypedSetScope:
      case LayoutCode::TypedMapScope:
        try
        {
          return tla::string((std::ostringstream() << parent->GetFullPath() << "[]"sv << path).str());
        }
        catch (std::bad_alloc&)
        {
          cdb_core::Contract::Fail("Unable to allocate memory.");
        }
      default:
        cdb_core::Contract::Fail(cdb_core::make_string<tla::string>("Parent scope type not supported: %d",
          parent->m_typeArg.GetType()->GetLayoutCode()));
      }
    }

    return tla::string(path);
  }
}
