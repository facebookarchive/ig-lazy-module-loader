/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */

package com.instagram.lazyload.demoapp;

import com.instagram.lazyload.base.ModuleManifest;
import com.instagram.lazyload.base.ModuleManifestReader;

import java.io.IOException;

/**
 * Class that creates manifests for lazy loaded modules. Manifests contains metadata about modules.
 */
class ManifestReader implements ModuleManifestReader {

  public final static String LazyLoadedService = "LazyLoadedService";
  public final static String LazyLoadedModule = "LazyLoadedModule";

  // Those files need to be placed in the assets/ folder of demo app
  private final static String ServiceFileName = "lazyservice.apk";
  private final static String LibraryFileName = "lazylibrary.dex";

  // Hash of the module may be useful when versioning of modules needs to be added
  private final static String ModuleHash = "null";

  @Override
  public ModuleManifest readModuleManifest(String moduleName) throws IOException {
    if (moduleName.equals(LazyLoadedService)) {
      return new ModuleManifest.Builder(moduleName).setModuleHash(ModuleHash).setDexFileName
          (ServiceFileName).build();
    } else if (moduleName.equals(LazyLoadedModule)) {
      return new ModuleManifest.Builder(moduleName).setModuleHash(ModuleHash).setDexFileName
          (LibraryFileName).build();
    } else {
      return null;
    }
  }
}
