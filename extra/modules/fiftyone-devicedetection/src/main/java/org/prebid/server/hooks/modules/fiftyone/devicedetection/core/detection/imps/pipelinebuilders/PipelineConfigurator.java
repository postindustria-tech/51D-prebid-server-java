package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface PipelineConfigurator<ConfigFragment>
        extends BiConsumer<DeviceDetectionOnPremisePipelineBuilder, ConfigFragment> {
}
