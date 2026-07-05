package com.learning.backend01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demonstrates @Configuration and @ConfigurationProperties.
 *
 * @Configuration marks the class as a source of bean definitions.
 * The @Bean annotation tells Spring that a method produces a bean to be
 * managed by the Spring container.
 */
@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    /**
     * Creates a AppSettings bean populated from properties prefixed with "app.settings".
     * In application.properties you would set:
     *   app.settings.name=MyApp
     *   app.settings.port=9090
     *   app.settings.features[0]=caching
     *   app.settings.features[1]=scheduling
     */
    @Bean
    @ConfigurationProperties(prefix = "app.settings")
    public AppSettings appSettings() {
        log.info("Creating AppSettings bean");
        return new AppSettings();
    }

    /**
     * Simple POJO holding configuration values.
     * Fields are automatically populated by Spring Boot's type-safe
     * configuration properties mechanism.
     */
    public static class AppSettings {
        private String name = "default";
        private int port = 8080;
        private java.util.List<String> features = new java.util.ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public java.util.List<String> getFeatures() { return features; }
        public void setFeatures(java.util.List<String> features) { this.features = features; }

        @Override
        public String toString() {
            return "AppSettings{name='" + name + "', port=" + port + ", features=" + features + "}";
        }
    }
}
