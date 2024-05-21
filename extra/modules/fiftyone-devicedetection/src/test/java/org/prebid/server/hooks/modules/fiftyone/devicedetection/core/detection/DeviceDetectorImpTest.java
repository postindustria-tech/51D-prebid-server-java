package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection;

import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.DeviceDetectorImp;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.device.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DeviceInfoPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.DeviceInfoClone;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeviceDetectorImpTest {
    private static DeviceDetector buildDeviceDetector(
            PipelineSupplier pipelineSupplier,
            PriorityEvidenceSelector priorityEvidenceSelector,
            DeviceInfoPatcher<DeviceInfoClone> deviceInfoPatcher)
    {
        return new DeviceDetectorImp(pipelineSupplier, priorityEvidenceSelector, deviceInfoPatcher);
    }

    @Test
    public void shouldRethrowOnFailure() throws Exception {
        // given
        try(final Pipeline pipeline = mock(Pipeline.class);
            final FlowData flowData = mock(FlowData.class)) {
            final RuntimeException e = new RuntimeException();
            when(pipeline.createFlowData()).thenThrow(e);

            // given
            final DeviceDetector deviceDetector = buildDeviceDetector(
                    () -> pipeline,
                    null,
                    null);

            // when and then
            Assertions.assertThatThrownBy(() -> deviceDetector.inferProperties(null, null)).hasCause(e);
        }
    }

    @Test
    public void shouldGetDeviceDataFromPipeline() throws Exception {
        // given
        try(final Pipeline pipeline = mock(Pipeline.class)) {
            final FlowData flowData = mock(FlowData.class);
            when(pipeline.createFlowData()).thenReturn(flowData);

            final boolean[] getDeviceDataCalled = {false};
            when(flowData.get(DeviceData.class)).then(i -> {
                getDeviceDataCalled[0] = true;
                return null;
            });

            final DeviceDetector deviceDetector = buildDeviceDetector(
                    () -> pipeline,
                    evidence -> Collections.emptyMap(),
                    null
            );

            // when and then
            assertThat(deviceDetector.inferProperties(null, null)).isNull();
            assertThat(getDeviceDataCalled).containsExactly(true);
        }
    }

    @Test
    public void shouldReturnPatchedDevice() throws Exception {
        // given
        try(final Pipeline pipeline = mock(Pipeline.class)) {
            final FlowData flowData = mock(FlowData.class);
            when(pipeline.createFlowData()).thenReturn(flowData);
            when(flowData.get(DeviceData.class)).thenReturn(mock(DeviceData.class));
            final DeviceInfoClone device = DeviceInfoClone.builder()
                    .make("Pumpkin&Co")
                    .build();

            final DeviceDetector deviceDetector = buildDeviceDetector(
                    () -> pipeline,
                    evidence -> Collections.emptyMap(),
                    (rawDevice, patchPlan, newData) -> device
            );

            // when
            final DeviceInfo newDevice = deviceDetector.inferProperties(null, null);

            assertThat(newDevice).isNotNull();
            assertThat(newDevice.getMake()).isEqualTo(device.getMake());
        }
    }
}
