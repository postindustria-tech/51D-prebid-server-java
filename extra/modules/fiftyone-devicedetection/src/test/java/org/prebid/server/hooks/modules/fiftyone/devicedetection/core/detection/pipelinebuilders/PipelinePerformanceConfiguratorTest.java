package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.pipeline.engines.Constants;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders.PipelinePerformanceConfigurator;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.PerformanceConfig;

import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PipelinePerformanceConfiguratorTest {
    private static BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> makeConfigurator() {
        return new PipelinePerformanceConfigurator();
    }
    
    @Test
    public void shouldIgnoreUnknownProfile() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setProfile("ghost");

        // when
        configurator.accept(builder, config);

        // then
        verify(builder, never()).setPerformanceProfile(any());
    }

    @Test
    public void shouldIgnoreEmptyProfile() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setProfile("");

        // when
        configurator.accept(builder, config);

        // then
        verify(builder, never()).setPerformanceProfile(any());
    }

    @Test
    public void shouldAssignMaxPerformance() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setProfile("mAxperFORMance");

        final ArgumentCaptor<Constants.PerformanceProfiles> profilesArgumentCaptor
                = ArgumentCaptor.forClass(Constants.PerformanceProfiles.class);

        // when
        configurator.accept(builder, config);

        // then
        verify(builder).setPerformanceProfile(profilesArgumentCaptor.capture());
        assertThat(profilesArgumentCaptor.getAllValues()).containsExactly(Constants.PerformanceProfiles.MaxPerformance);
    }

    @Test
    public void shouldAssignConcurrency() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setConcurrency(398476);

        final ArgumentCaptor<Integer> concurrenciesArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // when
        configurator.accept(builder, config);

        // then
        verify(builder).setConcurrency(concurrenciesArgumentCaptor.capture());
        assertThat(concurrenciesArgumentCaptor.getAllValues()).containsExactly(config.getConcurrency());
    }

    @Test
    public void shouldAssignDifference() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setDifference(498756);

        final ArgumentCaptor<Integer> profilesArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // when
        configurator.accept(builder, config);

        // then
        verify(builder).setDifference(profilesArgumentCaptor.capture());
        assertThat(profilesArgumentCaptor.getAllValues()).containsExactly(config.getDifference());
    }

    @Test
    public void shouldAssignAllowUnmatched() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setAllowUnmatched(true);

        final ArgumentCaptor<Boolean> allowUnmatchedArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        // when
        configurator.accept(builder, config);

        // then
        verify(builder).setAllowUnmatched(allowUnmatchedArgumentCaptor.capture());
        assertThat(allowUnmatchedArgumentCaptor.getAllValues()).containsExactly(config.getAllowUnmatched());
    }

    @Test
    public void shouldAssignDrift() {
        // given
        final DeviceDetectionOnPremisePipelineBuilder builder = mock(DeviceDetectionOnPremisePipelineBuilder.class);
        final BiConsumer<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> configurator
                = makeConfigurator();

        final PerformanceConfig config = new PerformanceConfig();
        config.setDrift(1348);

        final ArgumentCaptor<Integer> driftsArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        // when
        configurator.accept(builder, config);

        // then
        verify(builder).setDrift(driftsArgumentCaptor.capture());
        assertThat(driftsArgumentCaptor.getAllValues()).containsExactly(config.getDrift());
    }
}
