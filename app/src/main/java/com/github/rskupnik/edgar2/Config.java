package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.db.InMemoryDatabase;
import com.github.rskupnik.edgar.db.inmemory.InMemoryDeviceRepository;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Edgar edgar(Database database, DeviceRepository deviceRepository) {
        var edgar = Edgar.defaultImplementation(database, deviceRepository);

        return edgar;
    }

    @Bean
    public Database database() {
        return new InMemoryDatabase();
    }

    @Bean
    public DeviceRepository deviceRepository() {
        return new InMemoryDeviceRepository();
    }
}
