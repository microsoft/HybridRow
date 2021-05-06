// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_core
{
  // ReSharper disable once CppInconsistentNaming
  template<typename T>
  class ref_ptr final
  {
  public:

    template<class... ArgsType>
    // ReSharper disable once CppInconsistentNaming
    [[nodiscard]] constexpr static ref_ptr<T> make(ArgsType&&... args);

    constexpr ref_ptr() noexcept
    {
      m_p = nullptr;
    }

    constexpr ref_ptr(T* lp) noexcept
    {
      m_p = lp;

      if (m_p != nullptr)
      {
        m_p->AddRef();
      }
    }

    constexpr ref_ptr(const ref_ptr<T>& lp) noexcept
    {
      m_p = lp.m_p;

      if (m_p != nullptr)
      {
        m_p->AddRef();
      }
    }

    constexpr ref_ptr(ref_ptr<T>&& lp) noexcept
    {
      m_p = lp.m_p;
      lp.m_p = nullptr;
    }

    ~ref_ptr() noexcept
    {
      if (m_p != nullptr)
      {
        m_p->Release();
        m_p = nullptr;   // Make sure we AV in case someone is using CComObjectPtr after Release
      }
    }

    constexpr const T& operator*() const noexcept
    {
      return *m_p;
    }

    constexpr T& operator*() noexcept
    {
      return *m_p;
    }

    constexpr const T* operator->() const noexcept
    {
      return m_p;
    }

    constexpr T* operator->() noexcept
    {
      return m_p;
    }

    constexpr bool operator!() const noexcept
    {
      return (m_p == nullptr);
    }

    constexpr bool operator<(T* pT) const noexcept
    {
      return (m_p < pT);
    }

    constexpr bool operator>(T* pT) const noexcept
    {
      return (m_p < pT);
    }

    constexpr bool operator<=(T* pT) const noexcept
    {
      return (m_p <= pT);
    }

    constexpr bool operator>=(T* pT) const noexcept
    {
      return (m_p < pT);
    }

    constexpr bool operator==(T* pT) const noexcept
    {
      return (m_p == pT);
    }

    constexpr bool operator!=(T* pT) const noexcept
    {
      return (m_p != pT);
    }

    constexpr bool operator==(ref_ptr<T> pT) const noexcept
    {
      return (m_p == pT.m_p);
    }

    constexpr bool operator!=(ref_ptr<T> pT) const noexcept
    {
      return (m_p != pT.m_p);
    }

    constexpr void reset(T* lp) noexcept
    {
      if (m_p != lp)
      {
        if (lp != nullptr)
        {
          lp->AddRef();
        }

        if (m_p != nullptr)
        {
          m_p->Release();
        }

        m_p = lp;
      }
    }

    // Release the interface and set to nullptr
    constexpr void reset() noexcept
    {
      T* pTemp = m_p;
      if (pTemp != nullptr)
      {
        m_p = nullptr;
        pTemp->Release();
      }
    }

    constexpr ref_ptr& operator=(T* lp) noexcept
    {
      reset(lp);
      return *this;
    }

    constexpr ref_ptr& operator=(const ref_ptr<T>& lp) noexcept  // NOLINT(bugprone-unhandled-self-assignment, cert-oop54-cpp)
    {
      reset(lp.m_p);
      return *this;
    }

    constexpr ref_ptr& operator=(ref_ptr<T>&& lp) noexcept
    {
      reset();
      m_p = lp.m_p;
      lp.m_p = nullptr;
      return *this;
    }

    // Attach to an existing interface (does not AddRef)
    constexpr void attach(T* p2) noexcept
    {
      if (m_p != nullptr)
      {
        m_p->Release();
      }
      m_p = p2;
    }

    // Detach the interface (does not Release)
    constexpr T* release() noexcept
    {
      T* pt = m_p;
      m_p = nullptr;
      return pt;
    }

    [[nodiscard]] constexpr T* get() const noexcept
    {
      return m_p;
    }

  private:
    T* m_p;
  };

  template<typename T>
  template<class... ArgsType>
  [[nodiscard]] constexpr ref_ptr<T> ref_ptr<T>::make(ArgsType&&... args)
  {
    return std::move(ref_ptr<T>(new T(std::forward<ArgsType>(args)...)));
  }
}
