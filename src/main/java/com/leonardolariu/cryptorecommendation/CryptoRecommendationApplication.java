package com.leonardolariu.cryptorecommendation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@OpenAPIDefinition(info = @Info(title = "Crypto Recommendation API", version = "1.0", description = "This service is meant to provide crypto data insights"))
public class CryptoRecommendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoRecommendationApplication.class, args);
	}

}
