package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.adapters.DeviceInfoBuilderMethodSet;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;

import java.util.Map;
import java.util.function.Function;

public final class DeviceInfoPatcherImp<DeviceInfoBox, DeviceInfoBoxBuilder> implements DeviceInfoPatcher<DeviceInfoBox>
{
    private final Function<DeviceInfoBox, DeviceInfoBuilderMethodSet<DeviceInfoBox, DeviceInfoBoxBuilder>.Adapter> adapterFactory;

    public DeviceInfoPatcherImp(Function<DeviceInfoBox,
            DeviceInfoBuilderMethodSet<DeviceInfoBox, DeviceInfoBoxBuilder>.Adapter> adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public DeviceInfoBox patchDeviceInfo(
            DeviceInfoBox rawDevice,
            DevicePatchPlan patchPlan,
            DeviceInfo newData)
    {
        DeviceInfoBuilderMethodSet<DeviceInfoBox, DeviceInfoBoxBuilder>.Adapter writableDevice = adapterFactory.apply(rawDevice);
        boolean didChange = false;
        for (Map.Entry<String, DevicePatch> namedPatch : patchPlan.patches()) {
            final boolean propChanged = namedPatch.getValue().patch(writableDevice, newData);
            if (propChanged) {
                didChange = true;
            }
        }
        return didChange ? writableDevice.rebuildBox() : rawDevice;
    }
}
