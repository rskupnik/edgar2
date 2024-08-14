package com.github.rskupnik.edgar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rskupnik.edgar.config.device.DeviceConfig;
import com.github.rskupnik.edgar.config.device.DeviceConfigStorage;
import com.github.rskupnik.edgar.db.entity.DashboardEntity;
import com.github.rskupnik.edgar.db.entity.DeviceEntity;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.domain.CommandResponse;
import com.github.rskupnik.edgar.domain.Dashboard;
import com.github.rskupnik.edgar.domain.Device;
import com.github.rskupnik.edgar.domain.DeviceEndpoint;
import com.github.rskupnik.edgar.tts.TextToSpeechAdapter;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class EdgarImpl implements Edgar {

    private static final Logger logger = LoggerFactory.getLogger(Edgar.class);

    private final DeviceRepository deviceRepository;
    private final DashboardRepository dashboardRepository;
    private final DeviceClient deviceClient;
    private final DeviceConfigStorage deviceConfigStorage;
    private final TextToSpeechAdapter textToSpeech;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EdgarImpl(
            DeviceRepository deviceRepository,
            DashboardRepository dashboardRepository,
            DeviceConfigStorage deviceConfigStorage,
            DeviceClient deviceClient,
            TextToSpeechAdapter ttsAdapter
    ) {
        this.deviceRepository = deviceRepository;
        this.dashboardRepository = dashboardRepository;
        this.deviceConfigStorage = deviceConfigStorage;
        this.deviceClient = deviceClient;
        this.textToSpeech = ttsAdapter;
    }

    @Override
    public Either<String, Device> registerDevice(Device device) {
        var deviceEntity = deviceRepository.find(device.getId());
        if (deviceEntity.isPresent()) {
            deviceEntity.get().setResponsive(true);
            deviceRepository.save(device.getId(), deviceEntity.get());
        } else {
            deviceRepository.save(device.getId(), DeviceEntity.fromDomainObject(device));
        }
        return Either.right(device);
    }

    @Override
    public List<Device> getDevices() {
        return deviceRepository.findAll().stream()
                .map(Device::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CommandResponse sendCommand(String deviceId, String commandName, Map<String, String> params) {
        // Find device
        final Device device = deviceRepository.find(deviceId).map(Device::fromEntity).orElse(null);
        if (device == null) {
            logger.info("This device doesn't exist");
            return CommandResponse.error("This device doesn't exist");
        }

        // Reject if device is unresponsive
        if (!device.isResponsive()) {
            logger.info("Rejecting command call because device is unresponsive");
            return CommandResponse.error("This device is unresponsive");
        }

        // Find the command in the device
        final DeviceEndpoint endpoint = device.getEndpoints().stream().filter(e -> e.getPath().equals(commandName)).findFirst().orElse(null);
        if (endpoint == null) {
            logger.info("Endpoint " + commandName + " doesn't exist");
            return CommandResponse.error("Endpoint " + commandName + " doesn't exist");
        }

        // Filter out params that are not defined on the endpoint
        var filteredParams = params.entrySet().stream()
                .filter(e -> endpoint.getParams().stream().anyMatch(edp -> edp.getName().equals(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var response = deviceClient.sendCommand(device, endpoint, filteredParams);

        deviceRepository.find(device.getId()).ifPresent(e -> {
            if (response.isError()) {
                e.setResponsive(false);
            } else {
                e.setLastSuccessResponseTimestamp(Instant.now().toEpochMilli());
            }
            deviceRepository.save(e.getId(), e);
        });

        return response;
    }

    @Override
    public void cleanupUnresponsiveDevices() {
        deviceRepository.findAll()
                .stream()
                .filter(d -> !d.isResponsive())
                .map(d -> new Tuple2<>(deviceConfigStorage.get(d.getId()).orElse(DeviceConfig.empty()), d))
                .filter(t -> Instant.now().toEpochMilli() - (t._1.getUnresponsiveTimeout() * 1000) > t._2.getLastSuccessResponseTimestamp())
                .forEach(t -> {
                    logger.info("Removing unresponsive device: " + t._2.getId());
                    deviceRepository.delete(t._2.getId());
                });
    }

    @Override
    public void rediscoverUnresponsiveDevices() {
        deviceRepository.findAll()
                .stream()
                .filter(d -> !d.isResponsive())
                .map(d -> new Tuple2<>(d, deviceClient.isAlive(Device.fromEntity(d))))
                .forEach(d -> {
                    d._1.setResponsive(d._2);
                    deviceRepository.save(d._1.getId(), d._1);
                });
    }

    @Override
    public void loadDashboard(String id, String filename) {
        loadFile(filename, new TypeReference<DashboardEntity>() {}).ifPresent(f -> dashboardRepository.save(id, f));
        logger.info("Loaded Dashboard: " + id);
    }

    @Override
    public Optional<Dashboard> getDashboard(String id) {
        return dashboardRepository.find(id).map(Dashboard::fromEntity);
    }

    @Override
    public void loadDeviceConfig(String filename) {
        loadFile(filename, new TypeReference<List<DeviceConfig>>() {}).ifPresent(deviceConfigStorage::save);
        logger.info("Loaded Device Config");
    }

    @Override
    public void ttsSpeak(String text) {
        textToSpeech.speak(text);
    }

    private <T> Optional<T> loadFile(String filename, TypeReference<T> ref) {
        if (filename == null || !Files.exists(Path.of(filename)))
            return Optional.empty();

        try {
            var content = Files.readString(Path.of(filename));
            return Optional.of(objectMapper.readValue(content, ref));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void handleDeviceResponsivenessChange(String id, boolean responsive) {
        deviceRepository.find(id).ifPresent(e -> {
            e.setResponsive(responsive);
            deviceRepository.save(id, e);
        });
    }
}
