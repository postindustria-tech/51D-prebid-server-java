package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.pipeline.engines.Constants;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.MergingConfigurator;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.PropertyMerge;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.PerformanceConfig;

import java.util.List;

public final class PipelinePerformanceConfigurator implements PipelineConfigurator<PerformanceConfig> {
    private static final MergingConfigurator<DeviceDetectionOnPremisePipelineBuilder, PerformanceConfig> MERGER = new MergingConfigurator<>(List.of(
            new PropertyMerge<>(PerformanceConfig::getProfile, s -> !s.isEmpty(), (pipelineBuilder, profile) -> {
                final String lowercasedProfile = profile.toLowerCase();
                for (Constants.PerformanceProfiles nextProfile: Constants.PerformanceProfiles.values()) {
                    if (nextProfile.name().toLowerCase().equals(lowercasedProfile)) {
                        pipelineBuilder.setPerformanceProfile(nextProfile);
                        return;
                    }
                }
            }),
            new PropertyMerge<>(PerformanceConfig::getConcurrency, v -> true, DeviceDetectionOnPremisePipelineBuilder::setConcurrency),
            new PropertyMerge<>(PerformanceConfig::getDifference, v -> true, DeviceDetectionOnPremisePipelineBuilder::setDifference),
            new PropertyMerge<>(PerformanceConfig::getAllowUnmatched, v -> true, DeviceDetectionOnPremisePipelineBuilder::setAllowUnmatched),
            new PropertyMerge<>(PerformanceConfig::getDrift, v -> true, DeviceDetectionOnPremisePipelineBuilder::setDrift)));

    @Override
    public void accept(DeviceDetectionOnPremisePipelineBuilder pipelineBuilder, PerformanceConfig performanceConfig) {
        MERGER.test(pipelineBuilder, performanceConfig);
    }
}
