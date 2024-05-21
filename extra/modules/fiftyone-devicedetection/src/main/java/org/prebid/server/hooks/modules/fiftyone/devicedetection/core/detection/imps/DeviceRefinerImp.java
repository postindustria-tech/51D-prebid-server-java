package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.adapters.DeviceInfoBuilderMethodSet;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DeviceDetector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DevicePatchPlanner;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DeviceRefiner;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;

public class DeviceRefinerImp implements DeviceRefiner {
    private final DevicePatchPlanner devicePatchPlanner;
    private final DeviceDetector deviceDetector;

    public DeviceRefinerImp(
            DevicePatchPlanner devicePatchPlanner,
            DeviceDetector deviceDetector
    ) {
        this.devicePatchPlanner = devicePatchPlanner;
        this.deviceDetector = deviceDetector;
    }

    public <DeviceInfoBox, DeviceInfoBoxBuilder> EnrichmentResult<DeviceInfoBox> enrichDeviceInfo(
            DeviceInfo rawDeviceInfo,
            CollectedEvidence collectedEvidence,
            DeviceInfoBuilderMethodSet<DeviceInfoBox, DeviceInfoBoxBuilder>.Adapter writableAdapter)
    {
        final EnrichmentResult.EnrichmentResultBuilder<DeviceInfoBox> resultBuilder = EnrichmentResult.builder();

        final DevicePatchPlan patchPlan = devicePatchPlanner.buildPatchPlanFor(rawDeviceInfo);
        if (patchPlan == null || patchPlan.isEmpty()) {
            return resultBuilder.build();
        }

        if (deviceDetector.populateDeviceInfo(writableAdapter, collectedEvidence, patchPlan, resultBuilder)) {
            resultBuilder.enrichedDevice(writableAdapter.rebuildBox());
        }

        return resultBuilder.build();
    }
}
