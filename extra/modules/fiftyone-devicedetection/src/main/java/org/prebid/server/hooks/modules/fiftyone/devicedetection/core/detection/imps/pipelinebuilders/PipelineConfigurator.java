package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.MergingConfigurator;

@FunctionalInterface
public interface PipelineConfigurator<ConfigFragment>
        extends MergingConfigurator<DeviceDetectionOnPremisePipelineBuilder, ConfigFragment> {
}
