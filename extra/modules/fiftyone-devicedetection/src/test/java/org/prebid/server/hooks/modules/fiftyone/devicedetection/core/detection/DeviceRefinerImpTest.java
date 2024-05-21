package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection;

import com.iab.openrtb.request.Device;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DevicePatchPlan;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.EnrichmentResult;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.adapters.DeviceMirror;

import java.util.Collections;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

public class DeviceRefinerImpTest {
    private static DeviceRefiner buildRefiner(
            DevicePatchPlanner devicePatchPlanner,
            DeviceDetector deviceDetector)
    {
        return new DeviceRefinerImp(
                devicePatchPlanner,
                deviceDetector
        );
    }

    @Test
    public void shouldReturnNoDeviceIfPlanIsNull() {
        // given

        // when
        final DeviceRefiner deviceRefiner = buildRefiner(
                deviceInfo -> null,
                null
        );
        final EnrichmentResult<Device> result = deviceRefiner.enrichDeviceInfo(
                null,
                null,
                DeviceMirror.BUILDER_METHOD_SET.makeAdapter(Device.builder().build())
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrichedDevice()).isNull();
    }

    @Test
    public void shouldReturnNoDeviceIfPlanIsEmpty() {
        // given

        // when
        final DeviceRefiner deviceRefiner = buildRefiner(
                deviceInfo -> new DevicePatchPlan(Collections.emptySet()),
                null
        );
        final EnrichmentResult<Device> result = deviceRefiner.enrichDeviceInfo(
                null,
                null,
                DeviceMirror.BUILDER_METHOD_SET.makeAdapter(Device.builder().build())
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrichedDevice()).isNull();
    }

    @Test
    public void shouldReturnNoDeviceIfPropertiesNotFound() {
        // given

        // when
        final DeviceRefiner deviceRefiner = buildRefiner(
                deviceInfo -> new DevicePatchPlan(Collections.singletonList(
                        entry("dummy", (writableDeviceInfo, newData) -> false))),
                (writableDeviceInfo, collectedEvidence, devicePatchPlan, enrichmentResultBuilder) -> false
        );
        final EnrichmentResult<Device> result = deviceRefiner.enrichDeviceInfo(
                null,
                null,
                DeviceMirror.BUILDER_METHOD_SET.makeAdapter(Device.builder().build())
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrichedDevice()).isNull();
    }

    @Test
    public void shouldReturnNewDeviceIfPropertiesAreFound() {
        // given

        // when
        final DeviceRefiner deviceRefiner = buildRefiner(
                deviceInfo -> new DevicePatchPlan(Collections.singletonList(
                        entry("dummy", (writableDeviceInfo, newData) -> false))),
                (writableDeviceInfo, collectedEvidence, devicePatchPlan, enrichmentResultBuilder) -> true
        );
        final EnrichmentResult<Device> result = deviceRefiner.enrichDeviceInfo(
                null,
                null,
                DeviceMirror.BUILDER_METHOD_SET.makeAdapter(Device.builder().build())
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrichedDevice()).isNotNull();
    }
}
