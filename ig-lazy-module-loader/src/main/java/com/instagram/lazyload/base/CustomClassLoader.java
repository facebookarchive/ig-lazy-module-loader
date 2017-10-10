/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.annotation.Nullable;

/**
 * Class loader that is aware of additional paths to dex files for lazily loaded modules. This class
 * loader puts itself in the chain of class loaders between application class loader and system
 * class loader.
 */
public final class CustomClassLoader extends ClassLoader implements DexAdder {

  private static final ClassLoader APP_CLASSLOADER;
  private static final ClassLoader SYSTEM_CLASSLOADER;
  private static final Field CLASSLOADER_PARENT_FIELD;

  static {
    try {
      APP_CLASSLOADER = CustomClassLoader.class.getClassLoader();
      CLASSLOADER_PARENT_FIELD = ClassLoader.class.getDeclaredField("parent");
      CLASSLOADER_PARENT_FIELD.setAccessible(true);
      SYSTEM_CLASSLOADER = (ClassLoader) CLASSLOADER_PARENT_FIELD.get(APP_CLASSLOADER);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Nullable private static CustomClassLoader sInstalledClassLoader = null;

  private DexFile[] mDexFiles = new DexFile[0];
  private final ClassLoader mAppClassLoader;

  private CustomClassLoader() {
    super(SYSTEM_CLASSLOADER);
    mAppClassLoader = APP_CLASSLOADER;
  }

  /** Use this method to access CustomClassLoader */
  public static synchronized CustomClassLoader getInstance() {
    install();
    return sInstalledClassLoader;
  }

  /** Installs this class loader as a parent of application class loader */
  private static synchronized void install() {
    if (sInstalledClassLoader != null) {
      return;
    }
    CustomClassLoader cl = new CustomClassLoader();
    try {
      CLASSLOADER_PARENT_FIELD.set(cl.mAppClassLoader, cl);
      sInstalledClassLoader = cl;
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /** This method is called frequently and it should be free from synchronization or any locks. */
  @Override
  protected Class<?> findClass(String className) throws ClassNotFoundException {
    for (int i = 0; i < mDexFiles.length; ++i) {
      // when loading a class use app class loader instead of "this" class loader so that
      // class thinks that was loaded by app loader rather than this custom class loader.
      Class foundClass = mDexFiles[i].loadClass(className, mAppClassLoader);
      if (foundClass != null) {
        return foundClass;
      }
    }
    throw new ClassNotFoundException("CustomClassLoader didn't find " + className);
  }

  /** Lazily loaded modules should add their dex files using this method. */
  @Override
  public void addDex(File dexFile, File odexFile) throws IOException {
    // Adding a new dex file is a rare operation (one dex file per module) and the list will be
    // short so copying will be cheap. This is needed to avoid ConcurrentModificationException
    // when modifying and iterating over the list at the same time.
    ArrayList<DexFile> newDexList = new ArrayList<>(mDexFiles.length + 1);
    for (int i = 0; i < mDexFiles.length; ++i) {
      newDexList.add(mDexFiles[i]);
    }
    DexFile loadedDex =
        DexFile.loadDex(dexFile.getAbsolutePath(), odexFile.getAbsolutePath(), 0 /* flags */);
    newDexList.add(loadedDex);

    mDexFiles = newDexList.toArray(new DexFile[newDexList.size()]);
  }
}
