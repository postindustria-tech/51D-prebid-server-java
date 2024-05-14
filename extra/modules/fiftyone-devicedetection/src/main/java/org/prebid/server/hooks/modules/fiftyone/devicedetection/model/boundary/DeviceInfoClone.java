package org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary;

import lombok.Builder;
import lombok.Value;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.DeviceInfo;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.core.imps.DeviceInfoBuilderMethodSet;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Value
public class DeviceInfoClone implements DeviceInfo {
    Integer deviceType;
    String make;
    String model;
    String os;
    String osv;
    Integer h;
    Integer w;
    Integer ppi;
    BigDecimal pixelRatio;
    String deviceId;

    public static final DeviceInfoBuilderMethodSet<DeviceInfoClone, DeviceInfoClone.DeviceInfoCloneBuilder>
            BUILDER_METHOD_SET = new DeviceInfoBuilderMethodSet<>(
                    DeviceInfoClone::toBuilder,
                    DeviceInfoClone.DeviceInfoCloneBuilder::build,
                    DeviceInfoClone.DeviceInfoCloneBuilder::deviceType,
                    DeviceInfoClone.DeviceInfoCloneBuilder::make,
                    DeviceInfoClone.DeviceInfoCloneBuilder::model,
                    DeviceInfoClone.DeviceInfoCloneBuilder::os,
                    DeviceInfoClone.DeviceInfoCloneBuilder::osv,
                    DeviceInfoClone.DeviceInfoCloneBuilder::h,
                    DeviceInfoClone.DeviceInfoCloneBuilder::w,
                    DeviceInfoClone.DeviceInfoCloneBuilder::ppi,
                    DeviceInfoClone.DeviceInfoCloneBuilder::pixelRatio,
                    oldDevice -> DeviceInfoClone.DeviceInfoCloneBuilder::deviceId
            );
}
