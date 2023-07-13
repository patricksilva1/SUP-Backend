package br.com.banco.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
@Configuration
public class OpenAPIConfig {

    @Autowired
    private Environment environment;

    @Bean
    OpenAPI myOpenAPI() {
//        String activeProfile = environment.getActiveProfiles()[0];
        String devUrl = environment.getProperty("supera.openapi.dev-url");
        String prodUrl = environment.getProperty("supera.openapi.prod-url");

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("comercial@supera.com.br");
        contact.setName("Supera Inovacao Tecnologia");
        contact.setUrl("https://www.supera.com.br/");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info().title("Endpoints Management").version("3.0").contact(contact)
                .description("This API exposes management endpoints of the company Supera Inovacao Tecnologia.")
                .termsOfService("https://www.supera.com.br/terms").license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}