package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDashboardRepository;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDeviceRepository;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import com.github.rskupnik.edgar.tts.TextToSpeechAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Edgar edgar(
            DeviceRepository deviceRepository,
            DashboardRepository dashboardRepository,
            TextToSpeechAdapter textToSpeech
    ) {
        return Edgar.defaultImplementation(deviceRepository, dashboardRepository, textToSpeech);
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
    public TextToSpeechAdapter textToSpeechAdapter() {
        return new TTSVoiceRSS("blep");
        // TODO: Hide the API Key!
    }
}
