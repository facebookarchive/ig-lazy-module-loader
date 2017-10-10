/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */

package com.instagram.lazyload.lazyloadedlibrary;

/**
 * Class that is compiled to dex file and placed in the assets directory of demo app.
 * Class will be loaded in the demo app upon request (e.g. button click).
 *
 * This library needs to be first compiled to jar file and then to a dex file using a command:
 * dx --dex --output=library.dex library.jar
 */
public class LazyLoadedClass {

  public void runComplexAlgorithms() {
    System.out.println("I am a lazy loaded library!");
  }
}
