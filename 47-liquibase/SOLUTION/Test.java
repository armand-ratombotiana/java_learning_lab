package com.learning.liquibase;

import liquibase.Liquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiquibaseSolutionTest {

    private LiquibaseSolution solution;
    private Liquibase mockLiquibase;
    private DataSource mockDataSource;

    @BeforeEach
    void setUp() {
        solution = new LiquibaseSolution();
        mockDataSource = mock(DataSource.class);
        mockLiquibase = mock(Liquibase.class);
    }

    @Test
    void testCreateLiquibase() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mock(java.sql.Connection.class));
    }

    @Test
    void testUpdate() throws Exception {
        solution.update(mockLiquibase);
        verify(mockLiquibase).update(any(), any());
    }

    @Test
    void testRollback() throws Exception {
        solution.rollback(mockLiquibase, "v1.0");
        verify(mockLiquibase).rollback(eq("v1.0"), any(), any());
    }

    @Test
    void testRollbackCount() throws Exception {
        solution.rollbackCount(mockLiquibase, 3);
        verify(mockLiquibase).rollback(eq(3), any(), any());
    }

    @Test
    void testTag() throws Exception {
        solution.tag(mockLiquibase, "release-1.0");
        verify(mockLiquibase).tag("release-1.0");
    }

    @Test
    void testClearCheckSums() throws Exception {
        solution.clearCheckSums(mockLiquibase);
        verify(mockLiquibase).clearCheckSums();
    }

    @Test
    void testCreateTable() {
        var change = solution.createTable("users", "id", "name");
        assertNotNull(change);
    }
}