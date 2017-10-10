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
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class is responsible for preparing and computing internal paths to module dex files. It
 * would copy a dex file from assets directory into data directory because a dex file cannot be
 * loaded from assets directory. It will handle module versioning by including a hash of a module in
 * a directory name so as the modules change their hashes will change and new versions of modules
 * will be put in new directory
 */
public class DefaultModuleStore implements ModuleStore {

  private static final String MODULES_DIRECTORY = "modules";

  private static final String DEX_FILE_STORAGE = "dex_modules";
  private static final String OPTIMIZED_DEX_FILE_STORAGE = "opt_dex_modules";
  private static final String NATIVE_LIBS_STORAGE = "libs";

  private final Context mContext;

  public DefaultModuleStore(Context context) {
    mContext = context;
  }

  /** Returns a File handle to a directory which contains all modules sub-directories */
  public static File getDirectoryForAllModules(Context context) {
    return context.getDir(MODULES_DIRECTORY, Context.MODE_PRIVATE);
  }

  /** Returns a name of a directory where a modules will be located */
  public static String getDirectoryNameForModule(ModuleManifest moduleManifest) {
    return moduleManifest.getModuleName() + "_" + moduleManifest.getModuleHash();
  }

  /**
   * Creates all necessary paths and moves (if needed) a dex file from assets dir into data dir.
   * Note that a dex file cannot be loaded from assets/ dir, it needs to be copied first.
   *
   * @return a structure with paths to dex and optdex files
   * @throws IOException when any file operation fails
   */
  @Override
  public ModulePathsAndDependencies resolveModulePaths(
      ModuleManifestReader moduleManifestReader, String moduleName) throws IOException {
    ModuleManifest moduleManifest = moduleManifestReader.readModuleManifest(moduleName);
    File modulePath = getModulePathInDataDir(moduleManifest);
    FileIOUtils.createDirectoryOrThrow(modulePath);

    File dexFile = null;
    File optimizedDexFile = null;
    File nativeLibsDirectory = null;
    // A module could contain only native libraries.
    if (moduleManifest.containsDexFile()) {
      File dexDirectoryPath = new File(modulePath, DEX_FILE_STORAGE);
      FileIOUtils.createDirectoryOrThrow(dexDirectoryPath);

      File optimizedDexDirectoryPath = new File(modulePath, OPTIMIZED_DEX_FILE_STORAGE);
      FileIOUtils.createDirectoryOrThrow(optimizedDexDirectoryPath);
      optimizedDexFile = new File(optimizedDexDirectoryPath, moduleManifest.getDexFileName());

      dexFile = new File(dexDirectoryPath, moduleManifest.getDexFileName());

      if (!dexFile.exists()) {
        // Only copy a dex from assets into data dir if it has not been copied before
        copyDexToDataDirectory(dexFile, moduleManifest);
      }
    }

    if (moduleManifest.containsNativeLib()) {
      nativeLibsDirectory =
          new File(modulePath, NATIVE_LIBS_STORAGE + File.separator + Build.CPU_ABI);
    }

    return new ModulePathsAndDependencies(
        moduleName,
        dexFile,
        optimizedDexFile,
        nativeLibsDirectory,
        moduleManifest.getModuleDependencies());
  }

  private void copyDexToDataDirectory(File targetDexPath, ModuleManifest moduleManifest)
      throws IOException {
    // App can be killed when file is being copied, so we copy file first to the temporary file
    // and then do the atomic rename to the target file name.
    File tmpDexPath = new File(targetDexPath.getAbsolutePath() + ".tmp");
    FileIOUtils.copyFile(
        mContext
            .getAssets()
            .open(
                moduleManifest.getModuleName() + File.separator + moduleManifest.getDexFileName()),
        new FileOutputStream(tmpDexPath));
    if (!tmpDexPath.renameTo(targetDexPath)) {
      throw new IOException("Unable to rename a file");
    }
  }

  private File getModulePathInDataDir(ModuleManifest moduleManifest) {
    // path must contain a hash of the module so that when a modules changes (e.g. after app
    // upgrade) then a new module must be loaded - and this is detected by having a new directory
    // which contains a changed hash.
    return new File(getDirectoryForAllModules(mContext), getDirectoryNameForModule(moduleManifest));
  }
}
