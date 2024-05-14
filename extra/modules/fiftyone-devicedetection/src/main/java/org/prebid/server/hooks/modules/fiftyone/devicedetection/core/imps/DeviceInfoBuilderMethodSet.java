package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.imps;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record DeviceInfoBuilderMethodSet<Box, BoxBuilder>(
        Function<Box, BoxBuilder> builderFactory,
        Function<BoxBuilder, Box> builderMethod,
        BiConsumer<BoxBuilder, Integer> deviceTypeSetter,
        BiConsumer<BoxBuilder, String> makeSetter,
        BiConsumer<BoxBuilder, String> modelSetter,
        BiConsumer<BoxBuilder, String> osSetter,
        BiConsumer<BoxBuilder, String> osvSetter,
        BiConsumer<BoxBuilder, Integer> hSetter,
        BiConsumer<BoxBuilder, Integer> wSetter,
        BiConsumer<BoxBuilder, Integer> ppiSetter,
        BiConsumer<BoxBuilder, BigDecimal> pixelRatioSetter,
        Function<Box, BiConsumer<BoxBuilder, String>> deviceIdSetterFactory
) {
    public DeviceInfoBuilderAdapter<Box, BoxBuilder> makeAdapter(Box box) {
        return new DeviceInfoBuilderAdapter<>(box, this);
    }
}
