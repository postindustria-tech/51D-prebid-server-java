package org.prebid.server.hooks.modules.fiftyone.devicedetection.core;

import java.math.BigDecimal;

public interface WritableDeviceInfo {
    void setDeviceType(Integer deviceType);
    void setMake(String make);
    void setModel(String model);
    void setOs(String os);
    void setOsv(String osv);
    void setH(Integer h);
    void setW(Integer w);
    void setPpi(Integer ppi);
    void setPixelRatio(BigDecimal pixelRatio);

    void setDeviceId(String deviceId);
}
