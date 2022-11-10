package com.leonardolariu.cryptorecommendation.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "token")
@Component
public class TokenProperties {
    private List<String> symbols;
    private List<String> resources;
    private List<String> headers;
}
