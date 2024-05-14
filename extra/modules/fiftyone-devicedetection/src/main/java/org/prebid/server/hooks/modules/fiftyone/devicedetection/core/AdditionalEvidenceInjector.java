package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence.CollectedEvidenceBuilder;

import java.util.function.Consumer;

@FunctionalInterface
public interface AdditionalEvidenceInjector extends Consumer<CollectedEvidenceBuilder> {
    default void injectInto(CollectedEvidenceBuilder evidenceBuilder) {
        accept(evidenceBuilder);
    }
}
