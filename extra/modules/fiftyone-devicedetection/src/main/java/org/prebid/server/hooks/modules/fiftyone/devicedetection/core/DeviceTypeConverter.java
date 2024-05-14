package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import java.util.function.Function;

@FunctionalInterface
public interface DeviceTypeConverter extends Function<String, Integer> {
}
