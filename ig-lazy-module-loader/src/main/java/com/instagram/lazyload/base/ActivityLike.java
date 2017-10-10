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
import android.os.Bundle;
import android.os.PersistableBundle;
import javax.annotation.Nullable;

/**
 * Similar concept to {@link ServiceLike}. Classes that are instantiated by Android framework need
 * to be accessible from application class loader. Android framework may destroy and recreate
 * activities due to memory pressure. By implementing ActivityProxy and ActivityLike pattern (an
 * example is in {@link ServiceLike}) we make sure that things will work correctly: ActivityProxy
 * will extend Android activity class and will be included in the main application dex file and will
 * be responsible for lazy-loading a ActivityLike class.
 */
public abstract class ActivityLike {

  /** Actual activity that embeds this object */
  protected final Activity mActivity;

  public ActivityLike(Activity activity) {
    mActivity = activity;
  }

  public void onCreate(
      @Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {}

  public void onStart() {}

  public void onResume() {}

  public void onRestart() {}

  public void onPause() {}

  public void onStop() {}

  void onDestroy() {}
}
