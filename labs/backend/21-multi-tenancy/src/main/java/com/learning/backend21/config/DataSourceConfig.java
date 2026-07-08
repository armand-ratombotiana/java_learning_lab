package com.learning.backend21.config;

import com.learning.backend21.tenant.TenantContext;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DataSourceConfig {

    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    @Bean
    public DataSource dataSource() {
        return new TenantAwareDataSource();
    }

    public DataSource getDataSourceForTenant(String tenantId) {
        return dataSourceMap.computeIfAbsent(tenantId, id -> {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:" + id);
            config.setUsername("sa");
            config.setPassword("");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("hikari-" + id);
            return new HikariDataSource(config);
        });
    }

    private class TenantAwareDataSource implements DataSource {
        @Override
        public java.sql.Connection getConnection() {
            return getDataSourceForTenant(TenantContext.getTenantId()).getConnection();
        }
        @Override
        public java.sql.Connection getConnection(String username, String password) {
            return getDataSourceForTenant(TenantContext.getTenantId()).getConnection(username, password);
        }
        @Override
        public java.io.PrintWriter getLogWriter() { return null; }
        @Override
        public void setLogWriter(java.io.PrintWriter out) {}
        @Override
        public void setLoginTimeout(int seconds) {}
        @Override
        public int getLoginTimeout() { return 0; }
        @Override
        public java.util.logging.Logger getParentLogger() { return null; }
        @Override
        public <T> T unwrap(Class<T> iface) { return null; }
        @Override
        public boolean isWrapperFor(Class<?> iface) { return false; }
        @Override
        public java.sql.ConnectionBuilder createConnectionBuilder() { return null; }
    }
}
