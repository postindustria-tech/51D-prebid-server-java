package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.imps;

import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.AdditionalEvidenceInjector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.ModuleContextPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.model.ModuleContext;

public final class ModuleContextPatcherImp implements ModuleContextPatcher {
    public ModuleContext apply(ModuleContext moduleContext, AdditionalEvidenceInjector evidenceInjector)
    {
        ModuleContext.ModuleContextBuilder contextBuilder;
        CollectedEvidence.CollectedEvidenceBuilder evidenceBuilder = null;
        if (moduleContext != null) {
            contextBuilder = moduleContext.toBuilder();
            final CollectedEvidence lastEvidence = moduleContext.collectedEvidence();
            if (lastEvidence != null) {
                evidenceBuilder = lastEvidence.toBuilder();
            }
        } else {
            contextBuilder = ModuleContext.builder();
        }
        if (evidenceBuilder == null) {
            evidenceBuilder = CollectedEvidence.builder();
        }
        evidenceInjector.injectInto(evidenceBuilder);
        return contextBuilder
                .collectedEvidence(evidenceBuilder.build())
                .build();
    }
}
