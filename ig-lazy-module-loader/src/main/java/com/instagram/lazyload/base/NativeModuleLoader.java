/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

/**
 * Implement this interface for injecting native libraries into JVM's native libs loader. Instagram
 * uses Facebook's SoLoader to load .so files.
 *
 * @see <a href="https://github.com/facebook/SoLoader" />
 */
public interface NativeModuleLoader {

  void load(ModulePathsAndDependencies modulePathsAndDependencies);
}
