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
import android.content.Intent;
import android.os.IBinder;

/**
 * Class to extend for Services that will be lazily loaded.
 *
 * <p>A Service class is created by the Android framework. Framework uses application class loader
 * to search for a service class. It may happen that the service is destroyed (due to memory
 * pressure) and recreated later by the framework. This means that Service class needs be reachable
 * from the application class loader to instantiate it. Depending on how lazy-loading is implemented
 * it may be the case that service dex file is not installed before framework tries to instantiate
 * it. One solution to this problem is to use a ServiceProxy with ServiceLike classes.
 *
 * <p>ServiceProxy would extend Android Service class and will be included in the primary dex file
 * and will be accessible from Application class loader. ServiceLike will be the entry point to the
 * lazy-loaded module and will implement all Service lifecycle methods as if it was a Service.
 *
 * <p>ServiceProxy in its first lifecycle method (onCreate) will lazy-load an implementation of
 * ServiceLike class and will delegate all lifecycle methods to it. Hence there will be no need to
 * worry about when to install a dex file with lazily-loaded module as it will happen in the
 * ServiceProxy class when first needed.
 *
 * <p>An example: let's assume there is a service called: PhotoPrefetchService.
 *
 * <p>To make this lazily-loaded:
 *
 * <p>(1) create a new class PhotoPrefetchServiceProxy which extends a Service
 *
 * <p>(2) make PhotoPrefetchService extend ServiceLike instead of Android Service
 *
 * <p>(3) In PhotoPrefetchServiceProxy.onCreate() call {@link LazyModuleLoader#loadServiceModule}
 * which returns a handle to ServiceLike
 *
 * <p>(4) In all PhotoPrefetchServiceProxy lifecycle methods delegate calls to ServiceLike which in
 * fact will delegate calls to PhotoPrefetchService
 */
public abstract class ServiceLike {

  protected final Context mContext;

  public ServiceLike(Context context) {
    mContext = context;
  }

  public void onCreate() {}

  public int onStartCommand(Intent intent, int flags, int startId) {
    return 0;
  }

  public IBinder onBind(Intent intent) {
    return null;
  }

  public boolean onUnbind(Intent intent) {
    return false;
  }

  public void onDestroy() {}
}
