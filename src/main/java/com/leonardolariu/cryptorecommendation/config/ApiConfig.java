package com.leonardolariu.cryptorecommendation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "api")
@Component
public class ApiConfig {
    List<String> blacklistedIps;
}
