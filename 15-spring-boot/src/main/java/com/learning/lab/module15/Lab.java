package com.learning.lab.module15;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 15: Spring Boot ===");
        springBootBasicsDemo();
        autoConfigurationDemo();
        propertiesDemo();
        actuatorDemo();
    }

    static void springBootBasicsDemo() {
        System.out.println("\n--- Spring Boot Basics ---");
        System.out.println("Spring Boot Features:");
        System.out.println("- Auto-configuration");
        System.out.println("- Embedded servers (Tomcat/Jetty/Netty)");
        System.out.println("- Starter dependencies");
        System.out.println("- Production-ready features");
        System.out.println("- Simplified configuration");
        
        System.out.println("\nTypical Spring Boot App Structure:");
        System.out.println("src/main/java -> Application class + @SpringBootApplication");
        System.out.println("src/main/resources -> application.properties/yaml");
        System.out.println("src/test/java -> Unit tests");
    }

    static void autoConfigurationDemo() {
        System.out.println("\n--- Auto Configuration Demo ---");
        System.out.println("Spring Boot auto-configures:");
        System.out.println("1. DataSource (if spring.data.* properties set)");
        System.out.println("2. JPA EntityManagerFactory (if jpa properties set)");
        System.out.println("3. RedisConnectionFactory (if redis properties set)");
        System.out.println("4. Kafka/KafkaTemplate (if kafka properties set)");
        System.out.println("5. WebMvc/WebFlux (based on classpath)");
        
        System.out.println("\nStarter dependencies:");
        System.out.println("- spring-boot-starter-web");
        System.out.println("- spring-boot-starter-data-jpa");
        System.out.println("- spring-boot-starter-security");
        System.out.println("- spring-boot-starter-actuator");
    }

    static void propertiesDemo() {
        System.out.println("\n--- Properties Configuration ---");
        System.out.println("application.properties:");
        System.out.println("app.name=MyApplication");
        System.out.println("app.version=1.0.0");
        System.out.println("server.port=8080");
        System.out.println("spring.datasource.url=jdbc:mysql://localhost:3306/db");
        System.out.println("\napplication.yml:");
        System.out.println("app:");
        System.out.println("  name: MyApplication");
        System.out.println("  version: 1.0.0");
    }

    static void actuatorDemo() {
        System.out.println("\n--- Spring Boot Actuator ---");
        System.out.println("Add dependency: spring-boot-starter-actuator");
        System.out.println("Endpoints:");
        System.out.println("- /actuator/health - Health check");
        System.out.println("- /actuator/info - Application info");
        System.out.println("- /actuator/metrics - Metrics");
        System.out.println("- /actuator/env - Environment properties");
        System.out.println("- /actuator/beans - List all beans");
        System.out.println("- /actuator/configprops - Configuration properties");
        
        System.out.println("\nSecurity configuration:");
        System.out.println("management.endpoints.web.exposure.include=health,info,metrics");
        System.out.println("management.endpoint.health.show-details=when_authorized");
    }
}

class AppConfig {
    private String name;
    private String version;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}

class DataSourceConfig {
    private String url;
    private String username;
    private String password;
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
