package org.prebid.server.hooks.modules.fiftyone.devicedetection.core.imps.mergers;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface PropertyMerge<Target, Source> extends BiConsumer<Target, Source> {
    default void copySetting(Target target, Source source) {
        accept(target, source);
    }
}
