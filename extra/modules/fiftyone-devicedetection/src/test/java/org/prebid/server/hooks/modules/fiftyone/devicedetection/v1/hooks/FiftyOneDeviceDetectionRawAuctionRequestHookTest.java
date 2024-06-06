package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks;

import com.iab.openrtb.request.BidRequest;
import com.iab.openrtb.request.Device;
import fiftyone.pipeline.core.flowelements.Pipeline;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.prebid.server.auction.model.AuctionContext;
import org.prebid.server.hooks.execution.v1.auction.AuctionInvocationContextImpl;
import org.prebid.server.hooks.execution.v1.auction.AuctionRequestPayloadImpl;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.boundary.CollectedEvidence;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.AccountFilter;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.model.config.ModuleConfig;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.FiftyOneDeviceDetectionModule;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.DeviceEnricher;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.core.EnrichmentResult;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.model.ModuleContext;
import org.prebid.server.hooks.v1.InvocationAction;
import org.prebid.server.hooks.v1.auction.AuctionInvocationContext;
import org.prebid.server.hooks.v1.auction.AuctionRequestPayload;
import org.prebid.server.hooks.v1.auction.RawAuctionRequestHook;
import org.prebid.server.settings.model.Account;

import java.util.Collections;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FiftyOneDeviceDetectionRawAuctionRequestHookTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private ModuleConfig moduleConfig;
    private RawAuctionRequestHook target;
    private BiFunction<Device, CollectedEvidence, EnrichmentResult> deviceRefiner;

    @Before
    public void setUp() {
        moduleConfig = new ModuleConfig();
        deviceRefiner = (bidRequest, evidence) -> null;
        target = new FiftyOneDeviceDetectionRawAuctionRequestHook(
                moduleConfig,
                new DeviceEnricher(mock(Pipeline.class)) {
                    @Override
                    public EnrichmentResult populateDeviceInfo(
                            Device device,
                            CollectedEvidence collectedEvidence) {
                        return deviceRefiner.apply(device, collectedEvidence);
                    }
                });
    }

    // MARK: - enrichDevice

    @Test
    public void payloadUpdateShouldReturnNullWhenRequestIsNull() throws Exception {
        // given
        final AuctionRequestPayload auctionRequestPayload = AuctionRequestPayloadImpl.of(null);
        final AuctionInvocationContext invocationContext = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                ModuleContext.builder()
                        .collectedEvidence(null)
                        .build()
        );

        // when
        final BidRequest newBidRequest = target.call(auctionRequestPayload, invocationContext)
                .result()
                .payloadUpdate()
                .apply(auctionRequestPayload)
                .bidRequest();

        // then
        assertThat(newBidRequest).isNull();
    }

    @Test
    public void payloadUpdateShouldReturnOldRequestWhenMergedDeviceIsNull() throws Exception {
        // given
        final BidRequest bidRequest = BidRequest.builder().build();
        final CollectedEvidence savedEvidence = CollectedEvidence.builder().build();
        final AuctionRequestPayload auctionRequestPayload = AuctionRequestPayloadImpl.of(bidRequest);
        final AuctionInvocationContext invocationContext = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                ModuleContext.builder()
                        .collectedEvidence(savedEvidence)
                        .build()
        );

        // when
        final boolean[] refinerCalled = {false};
        deviceRefiner = (device, evidence) -> {
            refinerCalled[0] = true;
            return EnrichmentResult.builder().build();
        };
        final BidRequest newBidRequest = target.call(auctionRequestPayload, invocationContext)
                .result()
                .payloadUpdate()
                .apply(auctionRequestPayload)
                .bidRequest();

        // then
        assertThat(newBidRequest).isEqualTo(bidRequest);
        assertThat(refinerCalled).containsExactly(true);
    }

    @Test
    public void payloadUpdateShouldPassMergedEvidenceToDeviceRefiner() throws Exception {
        // given
        final BidRequest bidRequest = BidRequest.builder().build();
        final String fakeUA = "crystal-ball-navigator";
        final CollectedEvidence savedEvidence = CollectedEvidence.builder()
                .rawHeaders(Collections.emptySet())
                .deviceUA(fakeUA)
                .build();
        final AuctionRequestPayload auctionRequestPayload = AuctionRequestPayloadImpl.of(bidRequest);
        final AuctionInvocationContext invocationContext = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                ModuleContext.builder()
                        .collectedEvidence(savedEvidence)
                        .build()
        );

        // when
        final boolean[] refinerCalled = {false};
        deviceRefiner = (device, collectedEvidence) -> {
            assertThat(collectedEvidence.rawHeaders()).isEqualTo(savedEvidence.rawHeaders());
            assertThat(collectedEvidence.deviceUA()).isEqualTo(fakeUA);
            refinerCalled[0] = true;
            return null;
        };
        final BidRequest newBidRequest = target.call(auctionRequestPayload, invocationContext)
                .result()
                .payloadUpdate()
                .apply(auctionRequestPayload)
                .bidRequest();

        // then
        assertThat(newBidRequest).isEqualTo(bidRequest);
        assertThat(refinerCalled).containsExactly(true);
    }

    @Test
    public void payloadUpdateShouldInjectReturnedDevice() throws Exception {
        // given
        final BidRequest bidRequest = BidRequest.builder().build();
        final CollectedEvidence savedEvidence = CollectedEvidence.builder().build();
        final Device mergedDevice = Device.builder().build();
        final AuctionRequestPayload auctionRequestPayload = AuctionRequestPayloadImpl.of(bidRequest);
        final AuctionInvocationContext invocationContext = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                ModuleContext.builder()
                        .collectedEvidence(savedEvidence)
                        .build()
        );

        // when
        deviceRefiner = (device, collectedEvidence) -> EnrichmentResult
                .builder()
                .enrichedDevice(mergedDevice)
                .build();
        final BidRequest newBidRequest = target.call(auctionRequestPayload, invocationContext)
                .result()
                .payloadUpdate()
                .apply(auctionRequestPayload)
                .bidRequest();

        // then
        assertThat(newBidRequest.getDevice()).isEqualTo(mergedDevice);
    }

    // MARK: - code

    @Test
    public void codeShouldStartWithModuleCode() throws Exception {
        // when and then
        assertThat(target.code()).startsWith(FiftyOneDeviceDetectionModule.CODE);
    }

    // MARK: - isAccountAllowed

    @Test
    public void callShouldReturnUpdateActionWhenFilterIsNull() throws Exception {
        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, null)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndNoAuctionInvocationContext() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, null)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndNoAuctionInvocationContext() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, null)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnNoUpdateActionWhenWhitelistFilledAndNoAuctionInvocationContext() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, null)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.no_action);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndNoAuctionContext() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndNoAuctionContext() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnNoUpdateActionWhenWhitelistFilledAndNoAuctionContext() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                null,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.no_action);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndNoAccount() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        final AuctionContext auctionContext = AuctionContext.builder().build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndNoAccount() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        final AuctionContext auctionContext = AuctionContext.builder().build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnNoUpdateActionWhenWhitelistFilledAndNoAccount() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        final AuctionContext auctionContext = AuctionContext.builder().build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.no_action);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndNoAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndNoAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnNoUpdateActionWhenWhitelistFilledAndNoAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.no_action);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndEmptyAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndEmptyAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnNoUpdateActionWhenWhitelistFilledAndEmptyAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.no_action);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndAllowedAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("42")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndAllowedAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("42")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistFilledAndAllowedAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("42")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenNoWhitelistAndNotAllowedAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("29")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnUpdateActionWhenWhitelistEmptyAndNotAllowedAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.emptyList());

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("29")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.update);
    }

    @Test
    public void callShouldReturnNoUpdateActionWhenWhitelistFilledAndNotAllowedAccountID() throws Exception {
        // given
        moduleConfig.setAccountFilter(new AccountFilter());
        moduleConfig.getAccountFilter().setAllowList(Collections.singletonList("42"));

        final AuctionContext auctionContext = AuctionContext.builder()
                .account(Account.builder()
                        .id("29")
                        .build())
                .build();
        final AuctionInvocationContext context = AuctionInvocationContextImpl.of(
                null,
                auctionContext,
                false,
                null,
                null
        );

        // when
        final AuctionRequestPayload payload = AuctionRequestPayloadImpl.of(BidRequest.builder().build());
        final InvocationAction invocationAction = target.call(payload, context)
                .result()
                .action();

        // then
        assertThat(invocationAction).isEqualTo(InvocationAction.no_action);
    }
}
