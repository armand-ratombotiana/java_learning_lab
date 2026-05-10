package com.learning.flyway;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlywaySolutionTest {

    private FlywaySolution solution;
    private Flyway mockFlyway;
    private DataSource mockDataSource;

    @BeforeEach
    void setUp() {
        solution = new FlywaySolution();
        mockDataSource = mock(DataSource.class);
        mockFlyway = mock(Flyway.class);
    }

    @Test
    void testCreateFlyway() {
        when(Flyway.configure().dataSource(any()).locations(any()).load()).thenReturn(mockFlyway);
        Flyway flyway = solution.createFlyway(mockDataSource);
        assertNotNull(flyway);
    }

    @Test
    void testMigrate() {
        solution.migrate(mockFlyway);
        verify(mockFlyway).migrate();
    }

    @Test
    void testClean() {
        solution.clean(mockFlyway);
        verify(mockFlyway).clean();
    }

    @Test
    void testValidate() {
        solution.validate(mockFlyway);
        verify(mockFlyway).validate();
    }

    @Test
    void testGetPendingMigrations() {
        when(mockFlyway.info()).thenReturn(mock(org.flywaydb.core.api.info.MigrationInfo.class));
    }

    @Test
    void testRepair() {
        solution.repair(mockFlyway);
        verify(mockFlyway).repair();
    }

    @Test
    void testConfigure() {
        var config = solution.configure("schema_version");
        assertNotNull(config);
    }
}