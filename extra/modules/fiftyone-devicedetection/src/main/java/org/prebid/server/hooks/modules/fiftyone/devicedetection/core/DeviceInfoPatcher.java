package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

@FunctionalInterface
public interface DeviceInfoPatcher<DeviceInfoBox> {
    DeviceInfoBox patchDeviceInfo(DeviceInfoBox rawDevice, DevicePatchPlan patchPlan, DeviceInfo newData);
}
