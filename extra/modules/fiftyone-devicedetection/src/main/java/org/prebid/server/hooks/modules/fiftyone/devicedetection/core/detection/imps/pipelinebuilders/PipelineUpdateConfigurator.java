package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.pipeline.engines.services.DataUpdateServiceDefault;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.MergingConfigurator;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.mergers.PropertyMerge;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.DataFileUpdate;

import java.util.List;

public final class PipelineUpdateConfigurator implements PipelineConfigurator<DataFileUpdate> {
    private static final MergingConfigurator<DeviceDetectionOnPremisePipelineBuilder, DataFileUpdate> MERGER = new MergingConfigurator<>(List.of(
            new PropertyMerge<>(DataFileUpdate::getAuto, v -> true, DeviceDetectionOnPremisePipelineBuilder::setAutoUpdate),
            new PropertyMerge<>(DataFileUpdate::getOnStartup, v -> true, DeviceDetectionOnPremisePipelineBuilder::setDataUpdateOnStartup),
            new PropertyMerge<>(DataFileUpdate::getUrl, s -> !s.isEmpty(), DeviceDetectionOnPremisePipelineBuilder::setDataUpdateUrl),
            new PropertyMerge<>(DataFileUpdate::getLicenseKey, s -> !s.isEmpty(), DeviceDetectionOnPremisePipelineBuilder::setDataUpdateLicenseKey),
            new PropertyMerge<>(DataFileUpdate::getWatchFileSystem, v -> true, DeviceDetectionOnPremisePipelineBuilder::setDataFileSystemWatcher),
            new PropertyMerge<>(DataFileUpdate::getPollingInterval, v -> true, DeviceDetectionOnPremisePipelineBuilder::setUpdatePollingInterval)));

    @Override
    public void accept(DeviceDetectionOnPremisePipelineBuilder pipelineBuilder, DataFileUpdate updateConfig) {
        pipelineBuilder.setDataUpdateService(new DataUpdateServiceDefault());
        MERGER.test(pipelineBuilder, updateConfig);
    }
}
