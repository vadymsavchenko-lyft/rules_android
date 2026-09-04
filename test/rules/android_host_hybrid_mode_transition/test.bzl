# Copyright 2026 The Bazel Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Tests for the android_host_hybrid_mode transition."""

load("//rules:android_host_hybrid_mode_transition.bzl", "testing")
load("//rules:visibility.bzl", "PROJECT_VISIBILITY")
load(
    "//test/utils:lib.bzl",
    "asserts",
    "unittest",
)

visibility(PROJECT_VISIBILITY)

_HOST_HYBRID_MODE = "//rules/flags:android_host_hybrid_mode"

def _enabled_without_the_attribute_test_impl(ctx):
    env = unittest.begin(ctx)

    # Rules that do not declare host_hybrid_mode keep the original behavior.
    asserts.equals(
        env,
        {_HOST_HYBRID_MODE: True},
        testing.impl(settings = {}, attr = struct()),
    )

    return unittest.end(env)

enabled_without_the_attribute_test = unittest.make(
    impl = _enabled_without_the_attribute_test_impl,
)

def _enabled_when_opted_in_test_impl(ctx):
    env = unittest.begin(ctx)

    asserts.equals(
        env,
        {_HOST_HYBRID_MODE: True},
        testing.impl(settings = {}, attr = struct(host_hybrid_mode = True)),
    )

    return unittest.end(env)

enabled_when_opted_in_test = unittest.make(
    impl = _enabled_when_opted_in_test_impl,
)

def _incoming_configuration_kept_when_opted_out_test_impl(ctx):
    env = unittest.begin(ctx)

    # Returning None leaves the configuration untouched, so the target keeps the top-level
    # configuration and a test rule's outputs stay in the default testlogs tree.
    asserts.equals(
        env,
        None,
        testing.impl(settings = {}, attr = struct(host_hybrid_mode = False)),
    )

    return unittest.end(env)

incoming_configuration_kept_when_opted_out_test = unittest.make(
    impl = _incoming_configuration_kept_when_opted_out_test_impl,
)
