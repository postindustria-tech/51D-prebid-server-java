package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection;

import fiftyone.pipeline.core.flowelements.Pipeline;

import java.util.function.Supplier;

/**
 * A container with shared {@link Pipeline}
 */
@FunctionalInterface
public interface PipelineSupplier extends Supplier<Pipeline> {
}
