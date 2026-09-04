# Description:
#   Tests for the android_host_hybrid_mode transition.

load("@bazel_skylib//:bzl_library.bzl", "bzl_library")
load(
    ":test.bzl",
    "enabled_when_opted_in_test",
    "enabled_without_the_attribute_test",
    "incoming_configuration_kept_when_opted_out_test",
)

package(default_applicable_licenses = ["//:license"])

licenses(["notice"])

enabled_without_the_attribute_test(
    name = "enabled_without_the_attribute_test",
)

enabled_when_opted_in_test(
    name = "enabled_when_opted_in_test",
)

incoming_configuration_kept_when_opted_out_test(
    name = "incoming_configuration_kept_when_opted_out_test",
)

bzl_library(
    name = "bzl",
    srcs = glob(["*.bzl"]),
    visibility = ["//visibility:private"],
    deps = [
        "//rules:android_host_hybrid_mode_transition_bzl",
        "//rules:visibility_bzl",
    ],
)
