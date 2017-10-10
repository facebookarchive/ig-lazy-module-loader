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
import android.os.SystemClock;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/** Default algorithm used for module lazy loading. */
public class DefautlLoaderAlgorithm implements LoaderAlgorithm {

  private final Context mContext;
  private final DexAdder mDexAdder;
  private final LazyLoadListener mLazyLoadListener;
  private final boolean mAreAppModulesEnabled;
  private final ModuleStore mModuleStore;
  private final ModuleManifestReader mModuleManifestReader;
  @Nullable private final NativeModuleLoader mNativeModuleLoader;
  private final Set<String> mLazilyLoadedModules = new HashSet<>();

  public DefautlLoaderAlgorithm(
      Context context,
      ModuleStore moduleStore,
      ModuleManifestReader moduleManifestReader,
      LazyLoadListener lazyLoadListener,
      @Nullable NativeModuleLoader nativeModuleLoader,
      DexAdder dexAdder,
      boolean areAppModulesEnabled) {
    mContext = context;
    mLazyLoadListener = lazyLoadListener;
    mAreAppModulesEnabled = areAppModulesEnabled;
    mModuleStore = moduleStore;
    mModuleManifestReader = moduleManifestReader;
    mNativeModuleLoader = nativeModuleLoader;
    mDexAdder = dexAdder;
  }

  @Override
  public Class loadModule(String moduleName, String className) throws LazyLoadingException {
    try {
      final Class implClass;
      if (!mAreAppModulesEnabled || mLazilyLoadedModules.contains(moduleName)) {
        implClass = mContext.getClassLoader().loadClass(className);
      } else {
        ModulePathsAndDependencies modulePathsAndDependencies =
            mModuleStore.resolveModulePaths(mModuleManifestReader, moduleName);
        installDependentModules(modulePathsAndDependencies.getModuleDependencies());
        final long loadStartTime = SystemClock.uptimeMillis();
        injectModule(modulePathsAndDependencies);

        implClass = mContext.getClassLoader().loadClass(className);

        final long loadEndTime = SystemClock.uptimeMillis();
        mLazyLoadListener.moduleLazilyLoaded(moduleName, loadEndTime - loadStartTime);
      }
      return implClass;
    } catch (ClassNotFoundException | IOException e) {
      throw new LazyLoadingException(e);
    }
  }

  @Override
  public void installModule(String moduleName) throws IOException {
    if (!mAreAppModulesEnabled || mLazilyLoadedModules.contains(moduleName)) {
      return;
    }
    ModulePathsAndDependencies modulePathsAndDependencies =
        mModuleStore.resolveModulePaths(mModuleManifestReader, moduleName);
    installDependentModules(modulePathsAndDependencies.getModuleDependencies());
    final long loadStartTime = SystemClock.uptimeMillis();
    injectModule(modulePathsAndDependencies);
    final long loadEndTime = SystemClock.uptimeMillis();
    mLazyLoadListener.moduleLazilyInstalled(moduleName, loadEndTime - loadStartTime);
  }

  private void installDependentModules(List<String> dependentModules) throws IOException {
    for (String module : dependentModules) {
      installModule(module);
    }
  }

  private synchronized void injectModule(ModulePathsAndDependencies modulePathsAndDependencies)
      throws IOException {
    if (modulePathsAndDependencies.containsDexFile()) {
      // inject .dex file into ClassLoader
      mDexAdder.addDex(
          modulePathsAndDependencies.getDexFile(),
          modulePathsAndDependencies.getOptimizedDexFile());
    }
    if (modulePathsAndDependencies.containsNativeLib()) {
      if (mNativeModuleLoader == null) {
        throw new IllegalArgumentException("Native loader must not be null");
      }
      // Inject .so files into System's native lib loader
      mNativeModuleLoader.load(modulePathsAndDependencies);
    }
    mLazilyLoadedModules.add(modulePathsAndDependencies.getModuleName());
  }
}
