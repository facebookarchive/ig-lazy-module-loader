/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import java.io.IOException;

/** Interface for reading in a module manifest. */
public interface ModuleManifestReader {

  ModuleManifest readModuleManifest(String moduleName) throws IOException;
}
