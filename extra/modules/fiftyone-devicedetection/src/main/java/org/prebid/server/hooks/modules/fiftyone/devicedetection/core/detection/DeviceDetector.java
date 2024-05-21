package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.WritableDeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;

@FunctionalInterface
public interface DeviceDetector {
    boolean populateDeviceInfo(
            WritableDeviceInfo writableDeviceInfo,
            CollectedEvidence collectedEvidence,
            DevicePatchPlan devicePatchPlan,
            EnrichmentResult.EnrichmentResultBuilder<?> enrichmentResultBuilder);
}
