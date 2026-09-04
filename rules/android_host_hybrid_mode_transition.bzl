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
"""
Transition that sets android_host_hybrid_mode to true.

This is used to signal that code is being built for Android tests but running on a non-Android host.
"""

load("//rules:visibility.bzl", "PROJECT_VISIBILITY")

visibility(PROJECT_VISIBILITY)

def _android_host_hybrid_mode_transition_impl(settings, attr):
    # Rules that opt out keep the incoming configuration. This transition runs on an incoming edge,
    # so on a test rule it also relocates the test's own outputs (test.xml, test.log, test.outputs)
    # into the transitioned configuration's testlogs tree. The bazel-testlogs convenience symlink
    # can only name one configuration, so an invocation mixing Android and plain JVM tests leaves
    # those outputs unreachable through it. Repositories that do not select() on
    # //rules/flags:android_host_hybrid_mode can pass host_hybrid_mode = False to avoid that.
    if not getattr(attr, "host_hybrid_mode", True):
        return None

    return {
        "//rules/flags:android_host_hybrid_mode": True,
    }

android_host_hybrid_mode_transition = transition(
    implementation = _android_host_hybrid_mode_transition_impl,
    inputs = [],
    outputs = [
        "//rules/flags:android_host_hybrid_mode",
    ],
)

testing = struct(
    impl = _android_host_hybrid_mode_transition_impl,
)
