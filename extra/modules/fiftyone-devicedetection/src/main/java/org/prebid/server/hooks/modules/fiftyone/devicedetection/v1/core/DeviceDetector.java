package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;

import java.util.function.BiFunction;

@FunctionalInterface
public interface DeviceDetector extends BiFunction<CollectedEvidence, DevicePatchPlan, DeviceInfo> {
    default DeviceInfo inferProperties(CollectedEvidence collectedEvidence, DevicePatchPlan devicePatchPlan) {
        return apply(collectedEvidence, devicePatchPlan);
    }
}
