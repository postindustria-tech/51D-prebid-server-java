package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence.CollectedEvidenceBuilder;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface EvidenceCollector <T> extends BiConsumer<CollectedEvidenceBuilder, T> {
    default AdditionalEvidenceInjector evidenceFrom(T availableData) {
        return evidenceBuilder -> accept(evidenceBuilder, availableData);
    }
}
