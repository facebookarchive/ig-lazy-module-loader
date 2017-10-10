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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.annotation.Nullable;

/**
 * Similar concept to {@link ServiceLike}. Classes that are instantiated by Android framework need
 * to be accessible from application class loader. Android framework may destroy and recreate
 * fragments due to memory pressure. By implementing FragmentProxy and SupportFragmentLike pattern
 * (an example is in {@link ServiceLike}) we make sure that things will work correctly:
 * FragmentProxy will extend Android fragment class and will be included in the main application dex
 * file and will be responsible for lazy-loading a SupportFragmentLike class.
 */
public abstract class SupportFragmentLike {

  /** Actual fragment that embeds this object */
  protected final Fragment mFragment;

  public SupportFragmentLike(Fragment fragment) {
    mFragment = fragment;
  }

  public void onAttach(Context context) {}

  public void onCreate(@Nullable Bundle savedInstanceState) {}

  @Nullable
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return null;
  }

  public void onActivityCreated(@Nullable Bundle savedInstanceState) {}

  public void onStart() {}

  public void onResume() {}

  public void onPause() {}

  public void onStop() {}

  public void onDestroyView() {}

  public void onDestroy() {}

  public void onDetach() {}
}
