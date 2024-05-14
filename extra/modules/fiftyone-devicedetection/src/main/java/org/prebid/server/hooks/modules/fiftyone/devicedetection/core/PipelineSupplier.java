package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import fiftyone.pipeline.core.flowelements.Pipeline;

import java.util.function.Supplier;

@FunctionalInterface
public interface PipelineSupplier extends Supplier<Pipeline> {
}
