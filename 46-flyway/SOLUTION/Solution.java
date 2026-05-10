package com.learning.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import javax.sql.DataSource;
import java.util.List;

public class FlywaySolution {

    public Flyway createFlyway(DataSource dataSource) {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load();
    }

    public Flyway createFlywayWithSchema(DataSource dataSource, String schema) {
        return Flyway.configure()
            .dataSource(dataSource)
            .schemas(schema)
            .load();
    }

    public void migrate(Flyway flyway) {
        flyway.migrate();
    }

    public void clean(Flyway flyway) {
        flyway.clean();
    }

    public void validate(Flyway flyway) {
        flyway.validate();
    }

    public List<String> getPendingMigrations(Flyway flyway) {
        return flyway.info().getPending();
    }

    public List<String> getAppliedMigrations(Flyway flyway) {
        return flyway.info().getApplied().stream()
            .map(m -> m.getVersion() + " - " + m.getDescription())
            .toList();
    }

    public int getCurrentVersion(Flyway flyway) {
        return flyway.info().getCurrent() != null ?
            Integer.parseInt(flyway.info().getCurrent().getVersion().getVersion()) : 0;
    }

    public void repair(Flyway flyway) {
        flyway.repair();
    }

    public FluentConfiguration configure(String table) {
        return Flyway.configure()
            .table(table)
            .baselineOnMigrate(true);
    }
}