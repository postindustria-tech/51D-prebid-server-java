package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher;

import com.iab.openrtb.request.Device;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.adapters.DeviceInfoBuilderMethodSet;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.adapters.DeviceMirror;

import java.util.AbstractMap;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DeviceInfoPatcherImpTest {
    private static Device patchDeviceInfo(Device rawDevice, DevicePatchPlan patchPlan, DeviceInfo newData) {
        final EnrichmentResult.EnrichmentResultBuilder<Device> resultBuilder = EnrichmentResult.builder();
        final DeviceInfoBuilderMethodSet<Device, ?>.Adapter adapter
                = DeviceMirror.BUILDER_METHOD_SET.makeAdapter(rawDevice);
        if (new DeviceInfoPatcherImp().patchDeviceInfo(
                adapter,
                patchPlan,
                newData,
                resultBuilder
        )) {
            return adapter.rebuildBox();
        }

        return null;
    }

    @Test
    public void shouldReturnNullIfPlanIsEmpty() {
        // given
        final Device oldDevice = Device.builder().build();
        final DevicePatchPlan patchPlan = new DevicePatchPlan(Collections.emptySet());

        // when
        final Device newDevice = patchDeviceInfo(oldDevice, patchPlan, null);

        // then
        assertThat(newDevice).isNull();
    }

    @Test
    public void shouldReturnNullIfPatchChangedNothing() {
        // given
        final Device oldDevice = Device.builder().build();
        final DevicePatchPlan patchPlan = simplePlan(((writableDeviceInfo, newData) -> false));

        // when
        final Device newDevice = patchDeviceInfo(oldDevice, patchPlan, null);

        // then
        assertThat(newDevice).isNull();
    }

    @Test
    public void shouldPassDeviceDataToPatch() {
        // given
        final Device oldDevice = Device.builder().build();
        final DeviceInfo mockedData = mock(DeviceInfo.class);

        // when
        final boolean[] dataPassed = { false };
        final DevicePatchPlan patchPlan = simplePlan(((writableDeviceInfo, newData) -> {
            assertThat(newData).isEqualTo(mockedData);
            dataPassed[0] = true;
            return false;
        }));
        final Device newDevice = patchDeviceInfo(oldDevice, patchPlan, mockedData);

        // then
        assertThat(newDevice).isNull();
        assertThat(dataPassed).containsExactly(true);
    }

    @Test
    public void shouldReturnNewDeviceWithPatchedData() {
        // given
        final Device oldDevice = Device.builder().build();
        final String newModel = "crafty";

        // when
        final DevicePatchPlan patchPlan = simplePlan(((writableDeviceInfo, newData) -> {
            writableDeviceInfo.setModel(newModel);
            return true;
        }));
        final Device newDevice = patchDeviceInfo(oldDevice, patchPlan, null);

        // then
        assertThat(newDevice).isNotEqualTo(oldDevice);
        assertThat(newDevice.getModel()).isEqualTo(newModel);
    }

    private static DevicePatchPlan simplePlan(DevicePatch patch) {
        return new DevicePatchPlan(Collections.singletonList(new AbstractMap.SimpleEntry<>("fakePatch", patch)));
    }
}
