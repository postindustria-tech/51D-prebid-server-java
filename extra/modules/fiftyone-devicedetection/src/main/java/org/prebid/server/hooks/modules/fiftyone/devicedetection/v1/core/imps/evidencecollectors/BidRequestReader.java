package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.imps.evidencecollectors;

import com.iab.openrtb.request.BidRequest;
import com.iab.openrtb.request.Device;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.BidRequestEvidenceCollector;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.imps.mergers.MergingConfiguratorImp;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.imps.mergers.PropertyMergeImp;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence.CollectedEvidenceBuilder;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.UserAgentEvidenceConverter;

import java.util.HashMap;
import java.util.List;

public final class BidRequestReader implements BidRequestEvidenceCollector {
    private final MergingConfiguratorImp<CollectedEvidenceBuilder, Device> merger;

    public BidRequestReader(UserAgentEvidenceConverter userAgentEvidenceConverter) {
         merger = new MergingConfiguratorImp<>(List.of(
                 new PropertyMergeImp<>(Device::getUa, ua -> true, CollectedEvidenceBuilder::deviceUA),
                 new PropertyMergeImp<>(Device::getSua, sua -> true, (builder, sua) -> {
                     final HashMap<String, String> secureHeaders = new HashMap<>();
                     userAgentEvidenceConverter.unpack(sua, secureHeaders);
                     builder.secureHeaders(secureHeaders);
                 })));
    }

    @Override
    public void accept(CollectedEvidenceBuilder evidenceBuilder, BidRequest bidRequest) {
        merger.applyProperties(evidenceBuilder, bidRequest.getDevice());
    }
}
