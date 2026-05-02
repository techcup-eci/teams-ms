package com.microservice.Servicio_TeamReadbull.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                .title("Team Readbull API")
                .version("1.0.0")
                .description("Interactive documentation with Swagger UI for the Team microservice"));
    }
}
