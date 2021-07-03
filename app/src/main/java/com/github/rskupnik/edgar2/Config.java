package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.db.InMemoryDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Edgar edgar(Database database) {
        var edgar = Edgar.defaultImplementation(database);

        return edgar;
    }

    @Bean
    public Database database() {
        return new InMemoryDatabase();
    }
}
