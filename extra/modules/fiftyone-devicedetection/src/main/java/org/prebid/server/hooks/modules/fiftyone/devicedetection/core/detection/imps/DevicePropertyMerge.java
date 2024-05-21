package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.WritableDeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatch;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.PropertyMerge;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class DevicePropertyMerge<T> implements DevicePatch, DevicePropertyMergeCondition {
    private final PropertyMerge<WritableDeviceInfo, DeviceInfo, T> baseMerge;

    public DevicePropertyMerge(
            BiConsumer<WritableDeviceInfo, T> setter,
            Function<DeviceInfo, T> getter,
            Predicate<T> isUsable)
    {
        this.baseMerge = new PropertyMerge<>(getter, isUsable, setter);
    }

    @Override
    public boolean patch(WritableDeviceInfo writableDeviceInfo, DeviceInfo newData) {
        return baseMerge.test(writableDeviceInfo, newData);
    }

    @Override
    public boolean test(DeviceInfo deviceInfo) {
        final T value = baseMerge.getter().apply(deviceInfo);
        return (value == null || !baseMerge.isUsable().test(value));
    }
}
