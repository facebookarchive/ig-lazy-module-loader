/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.annotation.Nullable;

/** Similar concept to {@link SupportFragmentLike}. */
public abstract class FragmentLike {

  /** Actual fragment that embeds this object */
  protected final Fragment mFragment;

  public FragmentLike(Fragment fragment) {
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
