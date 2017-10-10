/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */

package com.instagram.lazyload.lazyloadedservice;

import android.content.Context;
import android.widget.Toast;

import com.instagram.lazyload.base.ServiceLike;

/**
 * Service that is compiled to apk file and placed in the assets directory of demo app.
 * Service will be loaded in the demo app upon request (e.g. button click).
 */
public class LazyLoadedService extends ServiceLike {

  public LazyLoadedService(Context context) {
    super(context);
  }

  public void onCreate() {
    Toast.makeText(mContext, "I am a lazy loaded service!", Toast.LENGTH_LONG).show();
  }
}
