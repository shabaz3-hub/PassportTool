package org.example.springbootpassport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "config")
public class AppConfig {
    private List<String> tags;
    private List<String> carriers;
    private Map<String, List<String>> shipMethods;

    @PostConstruct
    public void init() {
        if(shipMethods != null) {
            shipMethods = shipMethods.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .flatMap(str -> Arrays.stream(str.split(",")))
                                    .collect(Collectors.toList())
                    ));
        }
    }

    // Getters and Setters

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCarriers() {
        return carriers;
    }

    public void setCarriers(List<String> carriers) {
        this.carriers = carriers;
    }

    public Map<String, List<String>> getShipMethods() {
        return shipMethods;
    }

    public void setShipMethods(Map<String, List<String>> shipMethods) {
        this.shipMethods = shipMethods;
    }

}
