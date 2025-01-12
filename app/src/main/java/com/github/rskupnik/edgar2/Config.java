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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:real-credentials.properties")
public class Config {

    @Value("#{${discord.credentials}}")
    private Map<String,String> discordCredentials;

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
        return new DiscordUserMessageSender(discordCredentials.get("token"), discordCredentials.get("authorizedUser"));
    }
}
