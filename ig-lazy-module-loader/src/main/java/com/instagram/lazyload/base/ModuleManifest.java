/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/** Metadata describing a module. */
public class ModuleManifest {

  private final String mModuleName;
  @Nullable private final String mDexFileName;
  private final String mModuleHash;
  private final boolean mContainsNativeLib;

  /** A module can be dependent on other modules that need to be loaded prior to this module */
  private final List<String> moduleDependencies = new ArrayList<>();

  private ModuleManifest(
      String moduleName,
      String moduleHash,
      String dexFileName,
      List<String> moduleDependencies,
      boolean containsNativeLib) {
    this.mModuleName = moduleName;
    this.mDexFileName = dexFileName;
    this.mModuleHash = moduleHash;
    this.moduleDependencies.addAll(moduleDependencies);
    this.mContainsNativeLib = containsNativeLib;
  }

  public String getModuleName() {
    return mModuleName;
  }

  public String getDexFileName() {
    return mDexFileName;
  }

  public String getModuleHash() {
    return mModuleHash;
  }

  public boolean containsNativeLib() {
    return mContainsNativeLib;
  }

  public boolean containsDexFile() {
    return mDexFileName != null;
  }

  public List<String> getModuleDependencies() {
    return new ArrayList<>(moduleDependencies);
  }

  public static class Builder {
    private final String moduleName;
    private String moduleHash;
    private String dexFileName;
    private boolean containsNativeLib;
    private final List<String> moduleDependencies = new ArrayList<>();

    public Builder(String moduleName) {
      this.moduleName = moduleName;
    }

    public Builder setDexFileName(String dexFileName) {
      this.dexFileName = dexFileName;
      return this;
    }

    public Builder setContainsNativeLib(boolean containsNativeLib) {
      this.containsNativeLib = containsNativeLib;
      return this;
    }

    public Builder setModuleHash(String moduleHash) {
      this.moduleHash = moduleHash;
      return this;
    }

    public Builder addDependency(String dependency) {
      moduleDependencies.add(dependency);
      return this;
    }

    public ModuleManifest build() {
      return new ModuleManifest(
          moduleName, moduleHash, dexFileName, moduleDependencies, containsNativeLib);
    }
  }
}
