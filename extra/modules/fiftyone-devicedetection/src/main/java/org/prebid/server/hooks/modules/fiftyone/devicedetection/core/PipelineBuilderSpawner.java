package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.DataFile;

@FunctionalInterface
public interface PipelineBuilderSpawner<T> {
    T makeBuilder(DataFile dataFile) throws Exception;
}
