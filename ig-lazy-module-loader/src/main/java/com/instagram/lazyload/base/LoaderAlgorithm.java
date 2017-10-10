/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import java.io.IOException;

/** Interface allows to inject and experiment with different loading algorithms. */
public interface LoaderAlgorithm {

  /** Loads a modules into memory and returns a class representing an entry point to that modules */
  Class loadModule(String moduleName, String className) throws LazyLoadingException;

  /** Loads a module into memory, client can load any class from that module */
  void installModule(String moduleName) throws IOException;
}
