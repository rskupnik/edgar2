package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDashboardRepository;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDeviceRepository;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Edgar edgar(DeviceRepository deviceRepository, DashboardRepository dashboardRepository) {
        return Edgar.defaultImplementation(deviceRepository, dashboardRepository);
    }

    @Bean
    public DeviceRepository deviceRepository() {
        return new InMemoryDeviceRepository();
    }

    @Bean
    public DashboardRepository dashboardRepository() {
        return new InMemoryDashboardRepository();
    }
}
