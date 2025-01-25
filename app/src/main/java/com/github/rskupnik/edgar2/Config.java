package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.UserMessageSender;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDashboardRepository;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDeviceDataRepository;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDeviceRepository;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceDataRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.discord.DiscordUserMessageSender;
import com.github.rskupnik.edgar.tts.TextToSpeechAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({CredentialsConfig.class})
public class Config {

    @Autowired
    private CredentialsConfig credentialsConfig;

    @Bean
    public Edgar edgar(
            DeviceRepository deviceRepository,
            DashboardRepository dashboardRepository,
            DeviceDataRepository deviceDataRepository,
            TextToSpeechAdapter textToSpeech,
            UserMessageSender userMessageSender
    ) {
        return Edgar.defaultImplementation(deviceRepository,
                dashboardRepository, deviceDataRepository, textToSpeech,
                userMessageSender
        );
    }

    @Bean
    public DeviceRepository deviceRepository() {
        return new InMemoryDeviceRepository();
    }

    @Bean
    public DashboardRepository dashboardRepository() {
        return new InMemoryDashboardRepository();
    }

    @Bean
    public DeviceDataRepository deviceDataRepository() {
        return new InMemoryDeviceDataRepository();
    }

    @Bean
    public TextToSpeechAdapter textToSpeechAdapter() {
        return new TTSVoiceRSS("blep");
        // TODO: Hide the API Key!
    }

    @Bean
    public UserMessageSender userMessageSender() {
        var flattenedProps = flattenProperties("credentials", credentialsConfig.getCredentials(), new HashMap<>());
        return new DiscordUserMessageSender(flattenedProps.get("credentials.discord.token"));
    }

    private Map<String, String> flattenProperties(String parentKey, Map<String, Object> source, Map<String, String> target) {
        flatten(parentKey, source, target);
        return target;
    }

    // Helper recursive function to flatten the map
    private void flatten(String parentKey, Map<String, Object> source, Map<String, String> target) {
        source.forEach((key, value) -> {
            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;
            if (value instanceof Map) {
                // Recursive call for nested maps
                flatten(fullKey, (Map<String, Object>) value, target);
            } else {
                // Add leaf values to the flat map
                target.put(fullKey, value.toString());
            }
        });
    }
}
