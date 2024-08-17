package com.github.rskupnik.edgar;

import com.github.rskupnik.edgar.config.device.DeviceConfig;
import com.github.rskupnik.edgar.config.device.DeviceConfigStorage;
import com.github.rskupnik.edgar.config.device.EndpointConfig;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceDataRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.domain.*;
import com.github.rskupnik.edgar.tts.TextToSpeechAdapter;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Edgar {

    Either<String, Device> registerDevice(Device device);
    List<Device> getDevices();

    CommandResponse sendCommand(String deviceId, String commandName, Map<String, String> params);

    // TODO: This stores only last data, maybe add some date or integer
    CommandResponse storeData(String deviceId, byte[] data);
    byte[] getData(String deviceId);

    void loadDashboard(String name, String filename);
    Optional<Dashboard> getDashboard(String id);

    void loadDeviceConfig(String filename);

    void cleanupUnresponsiveDevices();
    void rediscoverUnresponsiveDevices();

    void ttsSpeak(String text);

    static Edgar defaultImplementation(
            DeviceRepository deviceRepository,
            DashboardRepository dashboardRepository,
            DeviceDataRepository deviceDataRepository,
            TextToSpeechAdapter ttsAdapter
    ) {
        var deviceConfigStorage = deviceConfigStorage();
        return new EdgarImpl(
                deviceRepository, dashboardRepository, deviceDataRepository,
                deviceConfigStorage,
                cachedDeviceClient(defaultDeviceClient(), deviceConfigStorage),
                ttsAdapter
        );
    }

    static DeviceConfigStorage deviceConfigStorage() {
        return new DeviceConfigStorage();
    }

    static DeviceClient cachedDeviceClient(DeviceClient delegate, DeviceConfigStorage deviceConfigStorage) {
        return new CachedDeviceClient(
                delegate,
                (key) -> {
                    String[] split = key.split(":");
                    var deviceId = split[0];
                    var endpointPath = split[1];
                    return deviceConfigStorage.get(deviceId).orElse(DeviceConfig.empty()).getEndpoints()
                            .stream()
                            .filter(c -> c.getPath().equals(endpointPath))
                            .findFirst()
                            .orElse(EndpointConfig.empty())
                            .getCachePeriod() * 1000;
                }
        );
    }

    static DeviceClient defaultDeviceClient() {
        return new ApacheHttpDeviceClient(
                HttpClientBuilder.create().setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(2000)
                                .setConnectionRequestTimeout(2000)
                                .setSocketTimeout(2000).build()
                ).build(),
                HttpClientBuilder.create().setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(4000)
                                .setConnectionRequestTimeout(4000)
                                .setSocketTimeout(4000).build()
                ).build()
        );
    }
}