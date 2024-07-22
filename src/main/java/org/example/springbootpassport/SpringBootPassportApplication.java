package org.example.springbootpassport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
@RestController
public class SpringBootPassportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootPassportApplication.class, args);
    }
}
