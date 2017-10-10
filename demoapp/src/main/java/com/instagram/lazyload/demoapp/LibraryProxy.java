/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */

package com.instagram.lazyload.demoapp;

import android.content.Context;
import android.util.Log;

import com.instagram.lazyload.base.LazyLoadListener;
import com.instagram.lazyload.base.LazyModuleLoaderHelper;
import com.instagram.lazyload.lazyloadedlibrary.LazyLoadedClass;

public class LibraryProxy {

  private final static String TAG = "LibraryProxy";

  private final static LazyLoadListener mLazyLoadListener = new LazyLoadListener() {
    @Override
    public void moduleLazilyLoaded(String module, long loadTimeMs) {
      Log.i(TAG, "Library successfully loaded in " + loadTimeMs + "ms");
    }

    @Override
    public void moduleLazilyInstalled(String module, long loadTimeMs) {
      Log.i(TAG, "Library successfully installed in " + loadTimeMs + "ms");
    }
  };

  private static LibraryProxy sInstance;
  private LazyLoadedClass mLazyLoadedClass;

  private LibraryProxy() {
    mLazyLoadedClass = new LazyLoadedClass();
  }

  public static LibraryProxy getsInstance(Context context) {
    if (sInstance != null) {
      return sInstance;
    }

    installLibrary(context);
    sInstance = new LibraryProxy();
    return sInstance;
  }

  private static void installLibrary(Context context) {
    try {
      LazyModuleLoaderHelper.createLoaderWithoutNativeLibrariesSupport(
          context,
          new ManifestReader(),
          mLazyLoadListener).installModule(ManifestReader.LazyLoadedModule);
    } catch (Exception e) {
      Log.e(TAG, "Failed to install a module", e);
    }
  }

  public void runComplicatedAlgorithms() {
    mLazyLoadedClass.runComplexAlgorithms();
  }
}
