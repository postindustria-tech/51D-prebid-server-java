package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.adapters.DeviceDataWrapper;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.DeviceInfoClone;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DeviceDetector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DeviceInfoPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.PipelineSupplier;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.PriorityEvidenceSelector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;

public final class DeviceDetectorImp implements DeviceDetector {
    private final PipelineSupplier pipelineSupplier;
    private final PriorityEvidenceSelector priorityEvidenceSelector;
    private final DeviceInfoPatcher<DeviceInfoClone> deviceInfoPatcher;

    public DeviceDetectorImp(
            PipelineSupplier pipelineSupplier,
            PriorityEvidenceSelector priorityEvidenceSelector,
            DeviceInfoPatcher<DeviceInfoClone> deviceInfoPatcher)
    {
        this.pipelineSupplier = pipelineSupplier;
        this.priorityEvidenceSelector = priorityEvidenceSelector;
        this.deviceInfoPatcher = deviceInfoPatcher;
    }

    @Override
    public DeviceInfo apply(CollectedEvidence collectedEvidence, DevicePatchPlan patchPlan) {
        try (FlowData data = pipelineSupplier.get().createFlowData()) {
            data.addEvidence(priorityEvidenceSelector.pickRelevantFrom(collectedEvidence));
            data.process();
            DeviceData device = data.get(DeviceData.class);
            if (device == null) {
                return null;
            }
            return deviceInfoPatcher.patchDeviceInfo(
                    DeviceInfoClone.builder().build(),
                    patchPlan,
                    new DeviceDataWrapper(device)
            );
        } catch (Exception e) {
            // will be caught by `GroupResult.applyPayloadUpdate`
            throw new RuntimeException(e);
        }
    }
}
