package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.rawAcutionRequest.pipeline;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.pipeline.engines.Constants;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.DataFile;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.DataFileUpdate;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.ModuleConfig;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.PerformanceConfig;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.PipelineBuilderBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PipelinePerformanceConfiguratorTest {
    private static void applyPerformanceOptions(
            DeviceDetectionOnPremisePipelineBuilder pipelineBuilder,
            PerformanceConfig performanceConfig) throws Exception {
        final ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setDataFile(new DataFile());
        moduleConfig.getDataFile().setUpdate(new DataFileUpdate());
        moduleConfig.setPerformance(performanceConfig);
        new PipelineBuilderBuilder()
                .withPremadeBuilder(pipelineBuilder)
                .build(moduleConfig);
    }

    @Test
    public void shouldIgnoreUnknownProfile() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setProfile("ghost");

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder, never()).setPerformanceProfile(any());
    }

    @Test
    public void shouldIgnoreEmptyProfile() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setProfile("");

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder, never()).setPerformanceProfile(any());
    }

    @Test
    public void shouldAssignMaxPerformance() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setProfile("mAxperFORMance");

        final ArgumentCaptor<Constants.PerformanceProfiles> profilesArgumentCaptor
                = ArgumentCaptor.forClass(Constants.PerformanceProfiles.class);

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder).setPerformanceProfile(profilesArgumentCaptor.capture());
        assertThat(profilesArgumentCaptor.getAllValues()).containsExactly(Constants.PerformanceProfiles.MaxPerformance);
    }

    @Test
    public void shouldAssignConcurrency() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setConcurrency(398476);

        final ArgumentCaptor<Integer> concurrenciesArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder).setConcurrency(concurrenciesArgumentCaptor.capture());
        assertThat(concurrenciesArgumentCaptor.getAllValues()).containsExactly(config.getConcurrency());
    }

    @Test
    public void shouldAssignDifference() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setDifference(498756);

        final ArgumentCaptor<Integer> profilesArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder).setDifference(profilesArgumentCaptor.capture());
        assertThat(profilesArgumentCaptor.getAllValues()).containsExactly(config.getDifference());
    }

    @Test
    public void shouldAssignAllowUnmatched() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setAllowUnmatched(true);

        final ArgumentCaptor<Boolean> allowUnmatchedArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder).setAllowUnmatched(allowUnmatchedArgumentCaptor.capture());
        assertThat(allowUnmatchedArgumentCaptor.getAllValues()).containsExactly(config.getAllowUnmatched());
    }

    @Test
    public void shouldAssignDrift() throws Exception {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);

        final PerformanceConfig config = new PerformanceConfig();
        config.setDrift(1348);

        final ArgumentCaptor<Integer> driftsArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // when
        applyPerformanceOptions(builder, config);

        // then
        verify(builder).setDrift(driftsArgumentCaptor.capture());
        assertThat(driftsArgumentCaptor.getAllValues()).containsExactly(config.getDrift());
    }
}
