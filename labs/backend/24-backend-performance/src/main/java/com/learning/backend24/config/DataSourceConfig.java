package com.learning.backend24.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:perfdb");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300_000);
        config.setConnectionTimeout(2_000);
        config.setMaxLifetime(1_800_000);
        config.setLeakDetectionThreshold(60_000);
        config.setPoolName("HikariPool-Perf");
        return new HikariDataSource(config);
    }
}
