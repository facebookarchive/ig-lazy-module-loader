/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Class responsible for loading modules on demand. It provides a method to load a module and get
 * access to a Class that represents that module's API. It also provides convenience methods to load
 * modules that are known to be Android components like Service or a Fragment. Lastly, it provides a
 * method to install a module without actually using the modules - this aims to optimize the first
 * use of the module which is longer than any next load - see {@link
 * LazyModuleLoader#installModule(String)}
 */
public class LazyModuleLoader {

  private final Context mContext;
  private final LoaderAlgorithm mLoaderAlgorithm;

  public LazyModuleLoader(Context context, LoaderAlgorithm loaderAlgorithm) {
    Context appContext = context.getApplicationContext();
    mContext = appContext != null ? appContext : context;
    mLoaderAlgorithm = loaderAlgorithm;
  }

  /** Convenience methods that loads a module that is known to be a service */
  public synchronized ServiceLike loadServiceModule(String moduleName, String className)
      throws LazyLoadingException {
    try {
      Class lazyLoadedClass = mLoaderAlgorithm.loadModule(moduleName, className);
      Constructor c = lazyLoadedClass.getConstructor(Context.class);
      ServiceLike serviceLike = (ServiceLike) c.newInstance(mContext);
      return serviceLike;
    } catch (Throwable t) {
      throw new LazyLoadingException(t);
    }
  }

  /** Convenience methods that loads a module that is known to be a fragment */
  public synchronized SupportFragmentLike loadSupportFragmentModule(
      Fragment hostingFragment, String moduleName, String className) throws LazyLoadingException {
    try {
      Class lazyLoadedClass = mLoaderAlgorithm.loadModule(moduleName, className);
      Constructor c = lazyLoadedClass.getConstructor(Fragment.class);
      SupportFragmentLike fragmentLike = (SupportFragmentLike) c.newInstance(hostingFragment);
      return fragmentLike;
    } catch (Throwable t) {
      throw new LazyLoadingException(t);
    }
  }

  /** Convenience methods that loads a module that is known to be a fragment */
  public synchronized FragmentLike loadFragmentModule(
      android.app.Fragment hostingFragment, String moduleName, String className)
      throws LazyLoadingException {
    try {
      Class lazyLoadedClass = mLoaderAlgorithm.loadModule(moduleName, className);
      Constructor c = lazyLoadedClass.getConstructor(android.app.Fragment.class);
      FragmentLike fragmentLike = (FragmentLike) c.newInstance(hostingFragment);
      return fragmentLike;
    } catch (Throwable t) {
      throw new LazyLoadingException(t);
    }
  }

  /** Convenience methods that loads a module that is known to be an activity */
  public synchronized ActivityLike loadActivityModule(
      Activity activity, String moduleName, String className) throws LazyLoadingException {
    try {
      Class lazyLoadedClass = mLoaderAlgorithm.loadModule(moduleName, className);
      Constructor c = lazyLoadedClass.getConstructor(Activity.class);
      ActivityLike activityLike = (ActivityLike) c.newInstance(activity);
      return activityLike;
    } catch (Throwable t) {
      throw new LazyLoadingException(t);
    }
  }

  /** Loads any type of module - it could custom class or Android component */
  public synchronized Class loadModule(String moduleName, String className)
      throws LazyLoadingException {
    try {
      return mLoaderAlgorithm.loadModule(moduleName, className);
    } catch (Throwable t) {
      throw new LazyLoadingException(t);
    }
  }

  /**
   * Installs a module without returning a Class that is an entry point to this module. First time
   * module load takes longer than next loads because a dex file needs to be optimized before it can
   * be used (dexopt or dexoat). A solution to that problem might be installing modules on app
   * upgrade. App upgrade usually happen in the background so an app may listen for
   * MY_PACKAGE_REPLACED broadcast.
   */
  public synchronized void installModule(String moduleName) throws IOException {
    mLoaderAlgorithm.installModule(moduleName);
  }
}
