package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.adapters.DeviceDataWrapper;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.WritableDeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DeviceDetector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DeviceInfoPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.PriorityEvidenceSelector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;

import java.util.function.Supplier;

public class DeviceDetectorImp implements DeviceDetector {
    private final Supplier<Pipeline> pipelineSupplier;
    private final PriorityEvidenceSelector priorityEvidenceSelector;
    private final DeviceInfoPatcher deviceInfoPatcher;

    public DeviceDetectorImp(
            Supplier<Pipeline> pipelineSupplier,
            PriorityEvidenceSelector priorityEvidenceSelector,
            DeviceInfoPatcher deviceInfoPatcher)
    {
        this.pipelineSupplier = pipelineSupplier;
        this.priorityEvidenceSelector = priorityEvidenceSelector;
        this.deviceInfoPatcher = deviceInfoPatcher;
    }

    @Override
    public boolean populateDeviceInfo(
            WritableDeviceInfo writableDeviceInfo,
            CollectedEvidence collectedEvidence,
            DevicePatchPlan devicePatchPlan,
            EnrichmentResult.EnrichmentResultBuilder<?> enrichmentResultBuilder)
    {
        try (FlowData data = pipelineSupplier.get().createFlowData()) {
            data.addEvidence(priorityEvidenceSelector.pickRelevantFrom(collectedEvidence));
            data.process();
            DeviceData device = data.get(DeviceData.class);
            if (device == null) {
                return false;
            }
            return deviceInfoPatcher.patchDeviceInfo(
                    writableDeviceInfo,
                    devicePatchPlan,
                    new DeviceDataWrapper(device),
                    enrichmentResultBuilder
            );
        } catch (Exception e) {
            enrichmentResultBuilder.processingException(e);
            return false;
        }
    }
}
