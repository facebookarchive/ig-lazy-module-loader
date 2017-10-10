/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

/** Interface to implement by clients to track lazy loading and its performance */
public interface LazyLoadListener {

  /** Loading means installing and then instantiating a class. */
  void moduleLazilyLoaded(String module, long loadTimeMs);

  /** Installing means adding a module dex class to class loader */
  void moduleLazilyInstalled(String module, long loadTimeMs);
}
