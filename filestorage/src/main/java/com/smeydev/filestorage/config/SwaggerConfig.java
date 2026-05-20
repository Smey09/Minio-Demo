package com.smeydev.filestorage.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "File Storage API", version = "1.0.0", description = "MinIO-based File Storage API for uploading, downloading, and managing files", contact = @Contact(name = "SmeySoft", email = "support@smeysoft.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")), servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://api.example.com", description = "Production Server")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT authentication token")
public class SwaggerConfig {
}
