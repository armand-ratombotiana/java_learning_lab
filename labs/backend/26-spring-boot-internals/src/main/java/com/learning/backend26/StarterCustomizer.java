package com.learning.backend26;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@ConditionalOnClass(StarterCustomizer.class)
@EnableConfigurationProperties(StarterCustomizer.CustomStarterProperties.class)
public class StarterCustomizer {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "custom.starter.enabled", havingValue = "true", matchIfMissing = true)
    public CustomStarterService customStarterService(CustomStarterProperties properties) {
        return new CustomStarterService(properties.getPrefix(), properties.getSuffix());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "custom.starter.enabled", havingValue = "true")
    public CustomStarterHealthIndicator customStarterHealthIndicator() {
        return new CustomStarterHealthIndicator();
    }

    @ConfigurationProperties(prefix = "custom.starter")
    public record CustomStarterProperties(
            String prefix,
            String suffix,
            boolean enabled
    ) {
        public CustomStarterProperties() {
            this("Hello, ", "!", false);
        }
    }

    public record CustomStarterService(String prefix, String suffix) {
        public String greet(String name) {
            return prefix + name + suffix;
        }
    }

    public record CustomStarterHealthIndicator() {
        public boolean isHealthy() {
            return true;
        }
    }
}