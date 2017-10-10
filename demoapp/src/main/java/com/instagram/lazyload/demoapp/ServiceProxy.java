/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */

package com.instagram.lazyload.demoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.instagram.lazyload.base.LazyLoadListener;
import com.instagram.lazyload.base.LazyLoadingException;
import com.instagram.lazyload.base.LazyModuleLoaderHelper;
import com.instagram.lazyload.base.ServiceLike;

/**
 * The only responsibility of this service is to load a implementation of the real service (from
 * a file from assets folder) and delegate all the calls to that service.
 * <p>
 * This service is located in the main classes.dex file and can be created any time (no need to
 * load a service form secondary dex file before creating this service).
 */
public class ServiceProxy extends Service {

  private final static String TAG = "ServiceProxy";

  private static final String CLASS_NAME =
      "com.instagram.lazyload.lazyloadedservice.LazyLoadedService";

  private static final LazyLoadListener mLazyLoadListener = new LazyLoadListener() {
    @Override
    public void moduleLazilyLoaded(String module, long loadTimeMs) {
      Log.i(TAG, "Service successfully loaded in " + loadTimeMs + "ms");
    }

    @Override
    public void moduleLazilyInstalled(String module, long loadTimeMs) {
      Log.i(TAG, "Service successfully installed in " + loadTimeMs + "ms");
    }
  };

  private ServiceLike mLazyLoadedService;

  @Override
  public void onCreate() {
    try {
      mLazyLoadedService = LazyModuleLoaderHelper.createLoaderWithoutNativeLibrariesSupport(
          this,
          new ManifestReader(),
          mLazyLoadListener).loadServiceModule(
          ManifestReader.LazyLoadedService,
          CLASS_NAME);
    } catch (LazyLoadingException e) {
      Log.e(TAG, "Failed to lazy loaded a service", e);
    }
    mLazyLoadedService.onCreate();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mLazyLoadedService.onBind(intent);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return mLazyLoadedService.onUnbind(intent);
  }

  @Override
  public void onDestroy() {
    mLazyLoadedService.onDestroy();
  }
}
