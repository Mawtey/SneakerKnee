package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sneakerKneeOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SneakerKnee API")
                        .description("API documentation for SneakerKnee online store")
                        .version("v1.0"));
    }
}