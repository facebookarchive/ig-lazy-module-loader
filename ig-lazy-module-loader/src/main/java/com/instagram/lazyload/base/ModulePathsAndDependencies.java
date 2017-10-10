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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/** Class that stores paths to module dex files and dependent modules (if any) */
public class ModulePathsAndDependencies {

  private final String mModuleName;

  @Nullable private final File mDexFile;
  @Nullable private final File mOptimizedDexFile;
  @Nullable private final File mNativeLibsDirectory;

  /** A module can be dependent on other modules that need to be loaded prior to this module */
  private final List<String> mModuleDependencies = new ArrayList<>();

  /**
   * Some of the arguments might be null. If module loads only dex file then nativeLibsDirectory
   * will be null. If module loads only native code then both dexFile and optimizedDexFile will be
   * null. If Module loads both dex and native code then no argument will be null. List of module
   * dependencies can be empty if module does not depend on any other module.
   */
  public ModulePathsAndDependencies(
      String moduleName,
      File dexFile,
      File optimizedDexFile,
      File nativeLibsDirectory,
      List<String> moduleDependencies) {
    mModuleName = moduleName;
    mDexFile = dexFile;
    mOptimizedDexFile = optimizedDexFile;
    mNativeLibsDirectory = nativeLibsDirectory;
    mModuleDependencies.addAll(moduleDependencies);
  }

  public String getModuleName() {
    return mModuleName;
  }

  public boolean containsDexFile() {
    return mDexFile != null;
  }

  @Nullable
  public File getDexFile() {
    return mDexFile;
  }

  @Nullable
  public File getOptimizedDexFile() {
    return mOptimizedDexFile;
  }

  @Nullable
  public File getNativeLibsDirectory() {
    return mNativeLibsDirectory;
  }

  public boolean containsNativeLib() {
    return mNativeLibsDirectory != null;
  }

  public List<String> getModuleDependencies() {
    return new ArrayList<>(mModuleDependencies);
  }
}
