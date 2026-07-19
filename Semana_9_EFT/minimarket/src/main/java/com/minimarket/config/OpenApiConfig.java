package com.minimarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minimarket Plus API")
                        .version("1.0.0")
                        .description(
                                "Documentación avanzada de los endpoints REST del sistema "
                                + "Minimarket Plus, implementada con OpenAPI y enlaces "
                                + "hipermedia HATEOAS para la actividad de Semana 8."
                        )
                        .contact(new Contact()
                                .name("Equipo Minimarket Plus")));
    }
}