/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import java.io.File;
import java.io.IOException;

/**
 * An interface for adding new dex files to the classpath in a lazy fashion. Instagram uses buck's
 * exopackage classloader during develpment to aid developer velocity, and {@link CustomClassLoader}
 * in release builds.
 */
public interface DexAdder {
  /** Support for lazily loaded dex files */
  void addDex(File dexFile, File odexFile) throws IOException;
}
