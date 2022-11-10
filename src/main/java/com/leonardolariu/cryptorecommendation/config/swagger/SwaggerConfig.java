package com.leonardolariu.cryptorecommendation.config.swagger;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            Map<String, Example> headerExamples = Map.of(
                    "valid", new Example().value("192.158.1.41"),
                    "blacklisted", new Example().value("192.158.1.39"));

            Parameter header = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .schema(new StringSchema())
                    .name("x-forwarded-for")
                    .examples(headerExamples)
                    .description("Client IP")
                    .required(true);

            operation.addParametersItem(header);

            return operation;
        };
    }
}
