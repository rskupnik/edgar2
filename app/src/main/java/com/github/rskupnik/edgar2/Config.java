package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.EdgarDep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Edgar edgar(EdgarDep dep) {
        return new Edgar(dep);
    }

    @Bean
    public EdgarDep edgarDep() {
        return new EdgarDep();
    }
}
