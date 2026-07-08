package com.learning.backend17;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "API Versioning & Documentation Lab",
        version = "2.0.0",
        description = "Comprehensive API demonstrating versioning strategies and OpenAPI documentation",
        contact = @Contact(
            name = "Backend Academy",
            email = "dev@learning.com",
            url = "https://learning.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development"),
        @Server(url = "https://api.learning.com", description = "Production")
    }
)
public class Backend17Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend17Application.class, args);
        System.out.println("=== API Versioning & Documentation Lab is running ===");
    }
}
