package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.imps;

import com.iab.openrtb.request.BidRequest;
import com.iab.openrtb.request.Device;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.BidRequestEvidenceCollector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.BidRequestPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DeviceDetector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DevicePatchPlanner;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DeviceInfoPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence.CollectedEvidenceBuilder;
import org.prebid.server.util.ObjectUtil;

public final class BidRequestPatcherImp implements BidRequestPatcher {
    private final DevicePatchPlanner devicePatchPlanner;
    private final BidRequestEvidenceCollector bidRequestEvidenceCollector;
    private final DeviceDetector deviceDetector;
    private final DeviceInfoPatcher<Device> deviceInfoPatcher;

    public BidRequestPatcherImp(
            DevicePatchPlanner devicePatchPlanner,
            BidRequestEvidenceCollector bidRequestEvidenceCollector,
            DeviceDetector deviceDetector,
            DeviceInfoPatcher<Device> deviceInfoPatcher)
    {
        this.devicePatchPlanner = devicePatchPlanner;
        this.bidRequestEvidenceCollector = bidRequestEvidenceCollector;
        this.deviceDetector = deviceDetector;
        this.deviceInfoPatcher = deviceInfoPatcher;
    }

    @Override
    public BidRequest apply(BidRequest bidRequest, CollectedEvidence collectedEvidence) {
        if (bidRequest == null) {
            return null;
        }
        final Device existingDevice = ObjectUtil.firstNonNull(bidRequest::getDevice, () -> Device.builder().build());
        final DevicePatchPlan patchPlan = devicePatchPlanner.buildPatchPlanFor(new DeviceMirror(existingDevice));

        if (patchPlan == null || patchPlan.isEmpty()) {
            return null;
        }

        final CollectedEvidenceBuilder evidenceBuilder = collectedEvidence.toBuilder();
        bidRequestEvidenceCollector.evidenceFrom(bidRequest).injectInto(evidenceBuilder);
        final DeviceInfo detectedDevice = deviceDetector.inferProperties(evidenceBuilder.build(), patchPlan);
        if (detectedDevice == null) {
            return null;
        }

        Device mergedDevice = deviceInfoPatcher.patchDeviceInfo(existingDevice, patchPlan, detectedDevice);
        if (mergedDevice == null || mergedDevice == existingDevice) {
            return null;
        }

        return bidRequest.toBuilder()
                .device(mergedDevice)
                .build();
    }
}
