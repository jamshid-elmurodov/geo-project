package org.elec.geoproject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Geo Analytics API")
            .description("OpenStreetMap place import + PostGIS spatial analytics backend")
            .version("1.0.0")
            .contact(new Contact()
                .name("Geo Project")
                .email("contact@example.com")));
  }
}
