package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection;

import com.iab.openrtb.request.BidRequest;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.detection.imps.DeviceRefinerImp;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.patcher.DeviceInfoPatcher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceRefinerImpTest {
    private static DeviceRefiner buildRefiner(
            DevicePatchPlanner devicePatchPlanner,
            DeviceDetector deviceDetector)
    {
        return new DeviceRefinerImp(
                devicePatchPlanner,
                deviceDetector
        );
    }
}
