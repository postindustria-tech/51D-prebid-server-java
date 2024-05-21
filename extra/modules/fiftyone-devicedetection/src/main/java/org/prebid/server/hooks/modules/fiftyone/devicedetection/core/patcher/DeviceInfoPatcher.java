package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.WritableDeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;

import java.util.Collection;

@FunctionalInterface
public interface DeviceInfoPatcher {
    boolean patchDeviceInfo(
            WritableDeviceInfo writableDeviceInfo,
            DevicePatchPlan patchPlan,
            DeviceInfo newData,
            EnrichmentResult.EnrichmentResultBuilder<?> enrichmentResultBuilder);
}
