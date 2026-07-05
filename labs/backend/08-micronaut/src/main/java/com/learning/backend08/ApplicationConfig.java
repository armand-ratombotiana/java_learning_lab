package com.learning.backend08;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates Micronaut configuration with @ConfigurationProperties
 * and bean factory with @Factory.
 *
 * @Factory tells Micronaut this class produces beans.
 * @Requires enables conditional bean creation.
 */
@Factory
public class ApplicationConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Singleton
    public AppInfo appInfo(AppSettings settings) {
        log.info("Creating AppInfo bean with settings: {}", settings);
        return new AppInfo(settings.getName(), settings.getVersion());
    }

    /**
     * Configuration properties — values from application.yml with "app" prefix.
     * Micronaut resolves these at compile time.
     */
    @ConfigurationProperties("app")
    public static class AppSettings {
        private String name = "MicronautLab";
        private String version = "1.0.0";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        @Override
        public String toString() {
            return "AppSettings{name='" + name + "', version='" + version + "'}";
        }
    }

    public record AppInfo(String name, String version) {}
}
