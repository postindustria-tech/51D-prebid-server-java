package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders.PipelineSupplierImp;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PipelineSupplierImpTest {
    private static Supplier<Pipeline> makeSupplier(DeviceDetectionOnPremisePipelineBuilder builder) throws Exception {
        return new PipelineSupplierImp(builder);
    }

    @Test
    public void shouldReturnBuiltPipeline() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        // when
        final Pipeline mockedPipeline = mock(Pipeline.class);
        when(builder.build()).thenReturn(mockedPipeline);

        final Supplier<Pipeline> pipelineSupplier = makeSupplier(builder);
        final Pipeline pipeline = pipelineSupplier.get();

        // then
        assertThat(pipeline).isEqualTo(mockedPipeline);
    }
}
