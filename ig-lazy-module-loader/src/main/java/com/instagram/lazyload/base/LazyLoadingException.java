/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

/** Exception thrown when lazy loading fails, it contains a cause for failing */
public class LazyLoadingException extends Exception {

  public LazyLoadingException(Throwable cause) {
    super(cause);
  }
}
