/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */

package com.instagram.lazyload.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(com.instagram.lazyload.demoapp.R.layout.activity_main);
  }

  @Override
  protected void onStart() {
    super.onStart();

    findViewById(R.id.button_service).setOnClickListener(new View
        .OnClickListener() {
      @Override
      public void onClick(View view) {
        startService(new Intent(MainActivity.this, ServiceProxy.class));
      }
    });

    findViewById(R.id.button_library).setOnClickListener(new View
        .OnClickListener() {
      @Override
      public void onClick(View view) {
        LibraryProxy.getsInstance(MainActivity.this).runComplicatedAlgorithms();
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    stopService(new Intent(MainActivity.this, ServiceProxy.class));
  }
}
