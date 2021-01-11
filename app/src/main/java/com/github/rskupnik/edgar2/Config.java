package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Database;
import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.db.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Edgar edgar(Database database) {
        return Edgar.defaultImplementation(database);
    }

    @Bean
    public Database database() {
        return new MongoDatabase();
    }
}
