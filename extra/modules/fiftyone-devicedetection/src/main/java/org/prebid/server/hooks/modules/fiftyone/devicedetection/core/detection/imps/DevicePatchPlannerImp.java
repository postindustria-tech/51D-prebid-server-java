package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.WritableDeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.PropertyMerge;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatch;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.DevicePatchPlanner;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public final class DevicePatchPlannerImp implements DevicePatchPlanner {
    private static final Map<String, PropertyMerge<WritableDeviceInfo, DeviceInfo, ?>> propertiesToMerge = Map.ofEntries(
            entry("DeviceType", new PropertyMerge<>(DeviceInfo::getDeviceType, v -> v > 0, WritableDeviceInfo::setDeviceType)),
            entry("Make", new PropertyMerge<>(DeviceInfo::getMake, s -> !s.isEmpty(), WritableDeviceInfo::setMake)),
            entry("Model", new PropertyMerge<>(DeviceInfo::getModel, s -> !s.isEmpty(), WritableDeviceInfo::setModel)),
            entry("Os", new PropertyMerge<>(DeviceInfo::getOs, s -> !s.isEmpty(), WritableDeviceInfo::setOs)),
            entry("Osv", new PropertyMerge<>(DeviceInfo::getOsv, s -> !s.isEmpty(), WritableDeviceInfo::setOsv)),
            entry("H", new PropertyMerge<>(DeviceInfo::getH, v -> v > 0, WritableDeviceInfo::setH)),
            entry("W", new PropertyMerge<>(DeviceInfo::getW, v -> v > 0, WritableDeviceInfo::setW)),
            entry("Ppi", new PropertyMerge<>(DeviceInfo::getPpi, v -> v > 0, WritableDeviceInfo::setPpi)),
            entry("PixelRatio", new PropertyMerge<>(DeviceInfo::getPixelRatio, (BigDecimal v) -> v.intValue() > 0, WritableDeviceInfo::setPixelRatio)),
            entry("DeviceID", new PropertyMerge<>(DeviceInfo::getDeviceId, s -> !s.isEmpty(), WritableDeviceInfo::setDeviceId))
    );

    @Override
    public DevicePatchPlan apply(DeviceInfo deviceInfo) {
        return new DevicePatchPlan(propertiesToMerge.entrySet().stream()
                .filter(nextMerge -> nextMerge.getValue().shouldReplacePropertyIn(deviceInfo))
                .map(e -> new AbstractMap.SimpleEntry<String, DevicePatch>(e.getKey(), e.getValue()::copySingleValue))
                .collect(Collectors.toUnmodifiableList())
        );
    }
}
