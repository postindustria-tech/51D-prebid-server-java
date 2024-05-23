package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.rawAcutionRequest.detection;

import com.iab.openrtb.request.Device;
import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.FiftyOneDeviceDetectionRawAuctionRequestHook;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeviceEnrichmentTest {
    private static BiFunction<
            Device,
            CollectedEvidence,
            FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult> buildHook(
                    Supplier<Pipeline> pipelineSupplier,
                    BiFunction<
                            Device,
                            DeviceData,
                            FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult> patcher) throws Exception {

        return new FiftyOneDeviceDetectionRawAuctionRequestHook(null) {
            @Override
            protected DeviceDetectionOnPremisePipelineBuilder makeBuilder() throws Exception {
                final DeviceDetectionOnPremisePipelineBuilder builder
                        = mock(DeviceDetectionOnPremisePipelineBuilder.class);
                when(builder.build()).thenReturn(null);
                return builder;
            }

            @Override
            protected EnrichmentResult patchDevice(Device device, DeviceData deviceData) {
                return patcher.apply(device, deviceData);
            }

            @Override
            protected Pipeline getPipeline() {
                return pipelineSupplier.get();
            }

            @Override
            public EnrichmentResult populateDeviceInfo(
                    Device device,
                    CollectedEvidence collectedEvidence)
            {
                return super.populateDeviceInfo(device, collectedEvidence);
            }
        }::populateDeviceInfo;
    }

    @Test
    public void shouldReportErrorOnPipelineException() throws Exception {
        // given
        final Pipeline pipeline = mock(Pipeline.class);
        final Exception e = new RuntimeException();
        when(pipeline.createFlowData()).thenThrow(e);

        // when
        final BiFunction<
                Device,
                CollectedEvidence,
                FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult> hook
                = buildHook(() -> pipeline, null);
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result = hook.apply(null, null);

        // then
        assertThat(result.processingException()).isEqualTo(e);
    }

    @Test
    public void shouldReportErrorOnProcessException() throws Exception {
        // given
        final Pipeline pipeline = mock(Pipeline.class);
        final FlowData flowData = mock(FlowData.class);
        final Exception e = new RuntimeException();
        when(pipeline.createFlowData()).thenReturn(flowData);
        doThrow(e).when(flowData).process();

        // when
        final BiFunction<
                Device,
                CollectedEvidence,
                FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult> hook
                = buildHook(() -> pipeline, null);
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result = hook.apply(
                null,
                CollectedEvidence.builder().build());

        // then
        assertThat(result.processingException()).isEqualTo(e);
    }

    @Test
    public void shouldReturnNullOnNullDeviceData() throws Exception {
        // given
        final Pipeline pipeline = mock(Pipeline.class);
        final FlowData flowData = mock(FlowData.class);
        final Exception e = new RuntimeException();
        when(pipeline.createFlowData()).thenReturn(flowData);
        when(flowData.get(DeviceData.class)).thenReturn(null);

        // when
        final BiFunction<
                Device,
                CollectedEvidence,
                FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult> hook
                = buildHook(() -> pipeline, null);
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result = hook.apply(
                null,
                CollectedEvidence.builder().build());

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnPatchResult() throws Exception {
        // given
        final Pipeline pipeline = mock(Pipeline.class);
        final FlowData flowData = mock(FlowData.class);
        when(pipeline.createFlowData()).thenReturn(flowData);
        final Device device = Device.builder().build();
        final DeviceData deviceData = mock(DeviceData.class);
        when(flowData.get(DeviceData.class)).thenReturn(deviceData);
        final Device mergedDevice = Device.builder().build();
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult preparedResult
                = FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult.builder()
                .enrichedDevice(mergedDevice)
                .build();

        // when
        final boolean[] patcherCalled = { false };
        final BiFunction<
                Device,
                CollectedEvidence,
                FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult> hook
                = buildHook(() -> pipeline,
                (dev, devData) -> {
                    patcherCalled[0] = true;
                    assertThat(dev).isEqualTo(device);
                    assertThat(devData).isEqualTo(deviceData);
                    return preparedResult;
                });
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result = hook.apply(
                device,
                CollectedEvidence.builder().build());

        // then
        assertThat(patcherCalled).containsExactly(true);
        assertThat(result).isEqualTo(preparedResult);
    }
}
