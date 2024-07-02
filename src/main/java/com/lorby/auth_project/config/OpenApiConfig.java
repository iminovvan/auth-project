package com.lorby.auth_project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Nargiza",
                        email = "nargizh03@gmail.com"
                ),
                title = "Lorby",
                description = "API documentation for authentication project",
                version = "0.0.1"
        ),
        servers = {
            @Server(
                    description = "Railway Server",
                    url = ""
            )
        }
)
public class OpenApiConfig {
}
