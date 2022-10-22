package com.github.rskupnik.edgar2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.github.rskupnik.edgar2", "com.github.rskupnik.edgar2.web"})
@EnableScheduling
public class Edgar2Application {

	public static void main(String[] args) {
		SpringApplication.run(Edgar2Application.class, args);
	}

}
