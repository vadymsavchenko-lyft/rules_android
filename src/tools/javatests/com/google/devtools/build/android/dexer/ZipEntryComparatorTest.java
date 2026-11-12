// Copyright 2026 The Bazel Authors. All rights reserved.
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

import static com.google.common.truth.Truth.assertThat;

import java.util.zip.ZipEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link ZipEntryComparator}. */
@RunWith(JUnit4.class)
public class ZipEntryComparatorTest {

  @Test
  public void testCompareClassNames_outerBeforeInner() {
    // In ASCII, '$' (0x24) precedes '.' (0x2E).
    // ZipEntryComparator normalizes '$' to '0' (0x30) so outer classes sort before inner classes.
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo.class", "com/example/Foo$Inner.class"))
        .isLessThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo$Inner.class", "com/example/Foo.class"))
        .isGreaterThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo.class", "com/example/Foo$1.class"))
        .isLessThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo$1.class", "com/example/Foo.class"))
        .isGreaterThan(0);
  }

  @Test
  public void testCompareClassNames_innerClassOrdering() {
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo$A.class", "com/example/Foo$B.class"))
        .isLessThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo$B.class", "com/example/Foo$A.class"))
        .isGreaterThan(0);
  }

  @Test
  public void testCompareClassNames_packageInfoPrecedesAll() {
    // "package-info" is removed so "com/example/.class" precedes "com/example/A.class"
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/package-info.class", "com/example/A.class"))
        .isLessThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/A.class", "com/example/package-info.class"))
        .isGreaterThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/package-info.class", "com/example/Foo$Inner.class"))
        .isLessThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo$Inner.class", "com/example/package-info.class"))
        .isGreaterThan(0);
  }

  @Test
  public void testCompareClassNames_regularClasses() {
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Alpha.class", "com/example/Beta.class"))
        .isLessThan(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Beta.class", "com/example/Alpha.class"))
        .isGreaterThan(0);
  }

  @Test
  public void testCompareClassNames_equality() {
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Alpha.class", "com/example/Alpha.class"))
        .isEqualTo(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/Foo$Inner.class", "com/example/Foo$Inner.class"))
        .isEqualTo(0);
    assertThat(
            ZipEntryComparator.compareClassNames(
                "com/example/package-info.class", "com/example/package-info.class"))
        .isEqualTo(0);
  }

  @Test
  public void testCompareClassNames_normalizationCollisionDistinct() {
    // Normalization replaces '$' with '0', so Foo$2$1$1 and Foo$2$101 both normalize to Foo020101.
    // The comparator must not return 0 so distinct entries are not collapsed in TreeMaps.
    assertThat(
            ZipEntryComparator.compareClassNames(
                "Foo$2$1$1.class.dex", "Foo$2$101.class.dex"))
        .isNotEqualTo(0);
  }

  @Test
  public void testLikeDxZipEntryComparator() {
    ZipEntry outer = new ZipEntry("com/example/Foo.class");
    ZipEntry inner = new ZipEntry("com/example/Foo$Inner.class");
    ZipEntry pkgInfo = new ZipEntry("com/example/package-info.class");

    assertThat(ZipEntryComparator.LIKE_DX.compare(outer, inner)).isLessThan(0);
    assertThat(ZipEntryComparator.LIKE_DX.compare(inner, outer)).isGreaterThan(0);
    assertThat(ZipEntryComparator.LIKE_DX.compare(pkgInfo, outer)).isLessThan(0);
    assertThat(ZipEntryComparator.LIKE_DX.compare(outer, outer)).isEqualTo(0);
  }
}
