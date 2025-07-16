package org.ediae.tfm.crmapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                // aquí indicas tu URL completa con HTTPS
                new Server().url("https://crm-backend-production-5a64.up.railway.app")
            ))
            .info(new Info()
                .title("CRM API")
                .version("v1")
                .description("Documentación OpenAPI para CRM"));
    }
}
