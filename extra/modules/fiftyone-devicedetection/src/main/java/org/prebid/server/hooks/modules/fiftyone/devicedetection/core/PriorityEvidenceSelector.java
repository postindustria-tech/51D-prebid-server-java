package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;

import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface PriorityEvidenceSelector extends Function<CollectedEvidence, Map<String, String>> {
    default Map<String, String> pickRelevantFrom(CollectedEvidence collectedEvidence) {
        return apply(collectedEvidence);
    }
}
