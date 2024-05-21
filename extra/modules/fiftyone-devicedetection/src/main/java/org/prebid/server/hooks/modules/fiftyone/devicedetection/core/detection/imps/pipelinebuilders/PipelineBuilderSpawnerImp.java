package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.pipelinebuilders;

import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.devicedetection.DeviceDetectionPipelineBuilder;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.PipelineBuilderSpawner;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.DataFile;

public final class PipelineBuilderSpawnerImp
        implements PipelineBuilderSpawner<DeviceDetectionOnPremisePipelineBuilder> {

    @Override
    public DeviceDetectionOnPremisePipelineBuilder makeBuilder(DataFile dataFile) throws Exception {
        final Boolean shouldMakeDataCopy = dataFile.getMakeTempCopy();
        return new DeviceDetectionPipelineBuilder()
                .useOnPremise(dataFile.getPath(), shouldMakeDataCopy != null && shouldMakeDataCopy);
    }
}
