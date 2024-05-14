package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import java.util.function.Function;

@FunctionalInterface
public interface DevicePatchPlanner extends Function<DeviceInfo, DevicePatchPlan> {
    default DevicePatchPlan buildPatchPlanFor(DeviceInfo deviceInfo) {
        return apply(deviceInfo);
    }
}
