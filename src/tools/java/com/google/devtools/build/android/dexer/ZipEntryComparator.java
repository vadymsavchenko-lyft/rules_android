// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.android.dexer;

import com.google.common.annotations.VisibleForTesting;
import java.util.Comparator;
import java.util.zip.ZipEntry;

/**
 * Comparator that orders {@link ZipEntry ZipEntries} {@link #LIKE_DX like Android's dx tool}.
 */
enum ZipEntryComparator implements Comparator<ZipEntry> {
  /**
   * Comparator to order more or less order alphabetically by file name.  See
   * {@link #compareClassNames} for the exact name comparison.
   */
  LIKE_DX;

  @Override
  // Copied from com.android.dx.cf.direct.ClassPathOpener
  public int compare(ZipEntry a, ZipEntry b) {
    return compareClassNames(a.getName(), b.getName());
  }

  /**
   * Sorts java class names such that outer classes precede their inner classes and "package-info"
   * precedes all other classes in its package.
   *
   * @param a {@code non-null;} first class name
   * @param b {@code non-null;} second class name
   * @return {@code compareTo()}-style result
   */
  // Copied from com.android.dx.cf.direct.ClassPathOpener
  // See
  // https://cs.android.com/android/platform/superproject/+/android-latest-release:dalvik/dx/src/com/android/dx/cf/direct/ClassPathOpener.java;l=187-200;drc=9dbd802c8c96c3a66873bc600bc7d1374a1d08e5
  @VisibleForTesting
  static int compareClassNames(String a, String b) {
    boolean aSpecial = a.indexOf('$') >= 0 || a.contains("package-info");
    boolean bSpecial = b.indexOf('$') >= 0 || b.contains("package-info");
    if (!aSpecial && !bSpecial) {
      return a.compareTo(b);
    }
    String normA = aSpecial ? normalize(a) : a;
    String normB = bSpecial ? normalize(b) : b;
    int normalizedResult = normA.compareTo(normB);
    if (normalizedResult != 0) {
      return normalizedResult;
    }

    // Normalization is lossy. Keep distinct raw names distinct when this comparator is used as a
    // TreeMap key comparator, otherwise entries such as Foo$2$1$1 and Foo$2$101 collapse.
    return a.compareTo(b);
  }

  private static String normalize(String s) {
    if (s.indexOf('$') >= 0) {
      s = s.replace('$', '0');
    }
    if (s.contains("package-info")) {
      s = s.replace("package-info", "");
    }
    return s;
  }
}
