/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import android.content.Context;

/** Helps to create LazyModuleLoader with default implementation. */
public class LazyModuleLoaderHelper {

  public static LazyModuleLoader createLoaderWithNativeLibrariesSupport(
      Context context,
      ModuleManifestReader moduleManifestReader,
      LazyLoadListener lazyLoadListener,
      NativeModuleLoader nativeModuleLoader) {
    return new LazyModuleLoader(
        context,
        new DefautlLoaderAlgorithm(
            context,
            new DefaultModuleStore(context),
            moduleManifestReader,
            lazyLoadListener,
            nativeModuleLoader,
            CustomClassLoader.getInstance(),
            true));
  }

  public static LazyModuleLoader createLoaderWithoutNativeLibrariesSupport(
      Context context,
      ModuleManifestReader moduleManifestReader,
      LazyLoadListener lazyLoadListener) {
    return new LazyModuleLoader(
        context,
        new DefautlLoaderAlgorithm(
            context,
            new DefaultModuleStore(context),
            moduleManifestReader,
            lazyLoadListener,
            null,
            CustomClassLoader.getInstance(),
            true));
  }
}
