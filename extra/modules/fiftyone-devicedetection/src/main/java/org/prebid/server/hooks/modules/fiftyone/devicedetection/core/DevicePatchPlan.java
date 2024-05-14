package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import java.util.Collection;
import java.util.Map;

public record DevicePatchPlan(Collection<Map.Entry<String, DevicePatch>> patches) {
    public boolean isEmpty() {
        return patches == null || patches.isEmpty();
    }
}
