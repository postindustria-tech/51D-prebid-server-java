package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

@FunctionalInterface
public interface DevicePatch {
    boolean patch(WritableDeviceInfo writableDeviceInfo, DeviceInfo newData);
}
