package org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.rawAcutionRequest.detection;

import com.fasterxml.jackson.databind.node.TextNode;
import com.iab.openrtb.request.Device;
import fiftyone.devicedetection.DeviceDetectionOnPremisePipelineBuilder;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.exceptions.NoValueException;
import org.junit.Test;
import org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.FiftyOneDeviceDetectionRawAuctionRequestHook;
import org.prebid.server.proto.openrtb.ext.request.ExtDevice;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.FiftyOneDeviceDetectionRawAuctionRequestHook.EXT_DEVICE_ID_KEY;
import static org.prebid.server.hooks.modules.fiftyone.devicedetection.v1.hooks.FiftyOneDeviceDetectionRawAuctionRequestHook.getDeviceId;

public class DevicePatchPlannerImpTest {
    private final static int PROPERTIES_COUNT = 10;

    private final static Device COMPLETE_DEVICE = Device.builder()
            .devicetype(1)
            .make("StarFleet")
            .model("communicator")
            .os("NeutronAI")
            .osv("X-502")
            .h(5051)
            .w(3001)
            .ppi(1010)
            .pxratio(BigDecimal.valueOf(1.5))
            .ext(ExtDevice.empty())
            .build();
    private final static DeviceData COMPLETE_DEVICE_DATA;
    static {
        COMPLETE_DEVICE_DATA = mock(DeviceData.class);
        when(COMPLETE_DEVICE_DATA.getDeviceType()).thenReturn(mockValue("Mobile"));
        when(COMPLETE_DEVICE_DATA.getHardwareVendor()).thenReturn(mockValue("StarFleet"));
        when(COMPLETE_DEVICE_DATA.getHardwareModel()).thenReturn(mockValue("communicator"));
        when(COMPLETE_DEVICE_DATA.getPlatformName()).thenReturn(mockValue("NeutronAI"));
        when(COMPLETE_DEVICE_DATA.getPlatformVersion()).thenReturn(mockValue("X-502"));
        when(COMPLETE_DEVICE_DATA.getScreenPixelsHeight()).thenReturn(mockValue(5051));
        when(COMPLETE_DEVICE_DATA.getScreenPixelsWidth()).thenReturn(mockValue(3001));
        when(COMPLETE_DEVICE_DATA.getScreenInchesHeight()).thenReturn(mockValue(5.0));
        when(COMPLETE_DEVICE_DATA.getPixelRatio()).thenReturn(mockValue(1.5));
        when(COMPLETE_DEVICE_DATA.getDeviceId()).thenReturn(mockValue("fake-device-id"));
        COMPLETE_DEVICE.getExt().addProperty(EXT_DEVICE_ID_KEY,new TextNode( "fake-device-id"));
    }
    private static <T> AspectPropertyValue<T> mockValue(T value) {
        return new AspectPropertyValue<T>() {
            @Override
            public boolean hasValue() {
                return true;
            }

            @Override
            public T getValue() throws NoValueException {
                return value;
            }

            @Override
            public void setValue(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNoValueMessage() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setNoValueMessage(String s) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private static FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult patchDevice(
            Device device,
            DeviceData deviceData) throws Exception {

        return new FiftyOneDeviceDetectionRawAuctionRequestHook(null) {
            @Override
            protected DeviceDetectionOnPremisePipelineBuilder makeBuilder() throws Exception {
                final DeviceDetectionOnPremisePipelineBuilder builder
                        = mock(DeviceDetectionOnPremisePipelineBuilder.class);
                when(builder.build()).thenReturn(null);
                return builder;
            }

            @Override
            public EnrichmentResult patchDevice(Device device, DeviceData deviceData) {
                return super.patchDevice(device, deviceData);
            }
        }.patchDevice(device, deviceData);
    }

    @Test
    public void shouldReturnAllPropertiesWhenDeviceIsEmpty() throws Exception {
        // given
        final Device device = Device.builder().build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(device, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(PROPERTIES_COUNT);
    }

    @Test
    public void shouldReturnNullWhenDeviceIsFull() throws Exception {
        // given and when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(COMPLETE_DEVICE, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnDeviceTypePatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .devicetype(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getDevicetype()).isEqualTo(COMPLETE_DEVICE.getDevicetype());
    }


    @Test
    public void shouldReturnMakePatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .make(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getMake()).isEqualTo(COMPLETE_DEVICE.getMake());
    }


    @Test
    public void shouldReturnHWNameForModelIfHWModelIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .model(null)
                .build();
        final DeviceData deviceData = mock(DeviceData.class);
        final String expectedModel = "NinjaTech8888";
        when(deviceData.getHardwareName()).thenReturn(mockValue(Collections.singletonList(expectedModel)));

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, deviceData);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getModel()).isEqualTo(expectedModel);
    }


    @Test
    public void shouldReturnModelPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .model(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getModel()).isEqualTo(COMPLETE_DEVICE.getModel());
    }


    @Test
    public void shouldReturnOsPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .os(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getOs()).isEqualTo(COMPLETE_DEVICE.getOs());
    }


    @Test
    public void shouldReturnOsvPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .osv(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getOsv()).isEqualTo(COMPLETE_DEVICE.getOsv());
    }


    @Test
    public void shouldReturnHPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .h(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getH()).isEqualTo(COMPLETE_DEVICE.getH());
    }


    @Test
    public void shouldReturnWPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .w(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getW()).isEqualTo(COMPLETE_DEVICE.getW());
    }


    @Test
    public void shouldReturnPpiPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .ppi(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getPpi()).isEqualTo(COMPLETE_DEVICE.getPpi());
    }


    @Test
    public void shouldReturnPXRatioPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .pxratio(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(result.enrichedDevice().getPxratio()).isEqualTo(COMPLETE_DEVICE.getPxratio());
    }

    @Test
    public void shouldReturnDeviceIDPatchWhenItIsMissing() throws Exception {
        // given
        final Device testDevice = COMPLETE_DEVICE.toBuilder()
                .ext(null)
                .build();

        // when
        final FiftyOneDeviceDetectionRawAuctionRequestHook.EnrichmentResult result
                = patchDevice(testDevice, COMPLETE_DEVICE_DATA);

        // then
        assertThat(result.enrichedFields()).hasSize(1);
        assertThat(getDeviceId(result.enrichedDevice())).isEqualTo(getDeviceId(COMPLETE_DEVICE));
    }
}
