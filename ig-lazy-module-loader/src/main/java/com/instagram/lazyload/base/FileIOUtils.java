/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

/** Utility methods for file I/O */
public class FileIOUtils {

  // Should be same as memory block size
  private static int BUFFER_SIZE = 8 * 1024;

  /** This method allows to a copy a file from one stream to another */
  public static void copyFile(InputStream from, FileOutputStream to) throws IOException {
    BufferedInputStream reader = null;
    BufferedOutputStream writer = null;
    try {
      reader = new BufferedInputStream(from);
      writer = new BufferedOutputStream(to);
      byte[] buf = new byte[BUFFER_SIZE];
      int len;
      while ((len = reader.read(buf, 0, BUFFER_SIZE)) > 0) {
        writer.write(buf, 0, len);
      }
    } finally {
      if (writer != null) {
        writer.flush();
        to.getFD().sync();
        writer.close();
      }
      if (reader != null) {
        reader.close();
      }
    }
  }

  /**
   * Deletes modules used by old app versions. This method can be called on app upgrade (e.g. on
   * MY_PACKAGE_REPLACED broadcast received).
   *
   * @param parentDirectory directory which contains module sub-directories
   * @param moduleDirectoriesToKeep name of directories that should be kept, others will be deleted
   */
  public static void deleteModulesOtherThan(
      File parentDirectory, Set<String> moduleDirectoriesToKeep) {
    if (!parentDirectory.exists() || !parentDirectory.isDirectory()) {
      return;
    }
    String[] modules = parentDirectory.list();
    if (modules == null) {
      return;
    }
    for (String module : modules) {
      if (!moduleDirectoriesToKeep.contains(module)) {
        deletePathRecursively(new File(parentDirectory, module).getAbsolutePath());
      }
    }
  }

  private static void deletePathRecursively(String directory) {
    File fileOrDirectory = new File(directory);
    if (fileOrDirectory.isDirectory()) {
      File[] files = fileOrDirectory.listFiles();
      if (files != null) {
        for (File child : files) {
          deletePathRecursively(child.toString());
        }
      }
    }
    fileOrDirectory.delete();
  }

  /** A helper method to create a directory, it will throw exception if creation fails. */
  public static void createDirectoryOrThrow(File directory) throws IOException {
    // mkdir will return false either when directory already exists or failed to create,
    // hence we need to call isDirectory to differentiate those two use cases.
    if (!(directory.mkdirs() || directory.isDirectory())) {
      // Trying to find out why creating a directory failed (mkdirs does not provide detailed
      // information) and java.nio.files.Files.createDirectories() with more detailed exceptions
      // will be available from Android O.
      boolean exists = directory.exists();
      boolean canRead = directory.canRead();
      boolean canWrite = directory.canWrite();
      long freeSpace = directory.getFreeSpace();
      throw new IOException(
          String.format(
              (Locale) null,
              "Unable to create directory %b %b %b %d",
              exists,
              canRead,
              canWrite,
              freeSpace));
    }
  }
}
