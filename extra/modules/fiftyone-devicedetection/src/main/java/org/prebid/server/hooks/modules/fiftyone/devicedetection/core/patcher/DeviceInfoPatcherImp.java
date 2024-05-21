package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.WritableDeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DeviceInfoPatcherImp implements DeviceInfoPatcher {
    @Override
    public boolean patchDeviceInfo(
            WritableDeviceInfo writableDeviceInfo,
            DevicePatchPlan patchPlan,
            DeviceInfo newData,
            EnrichmentResult.EnrichmentResultBuilder<?> enrichmentResultBuilder)
    {
        final List<String> patchedFields = new ArrayList<>();
        for (Map.Entry<String, DevicePatch> namedPatch : patchPlan.patches()) {
            final boolean propChanged = namedPatch.getValue().patch(writableDeviceInfo, newData);
            if (propChanged) {
                patchedFields.add(namedPatch.getKey());
            }
        }
        if (patchedFields.isEmpty()) {
            return false;
        }

        enrichmentResultBuilder.enrichedFields(patchedFields);
        return true;
    }
}
