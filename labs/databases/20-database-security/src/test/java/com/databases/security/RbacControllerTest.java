package com.databases.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RbacControllerTest {
    private RbacController rbac;
    @BeforeEach void setUp() { rbac = RbacController.createDefault(); }

    @Test void shouldAllowAdminAccess() {
        assertTrue(rbac.checkAccess("user1", "orders", "read"));
        assertTrue(rbac.checkAccess("user1", "orders", "write"));
        assertTrue(rbac.checkAccess("user1", "orders", "delete"));
    }

    @Test void shouldAllowWriterAccess() {
        assertTrue(rbac.checkAccess("user2", "orders", "read"));
        assertTrue(rbac.checkAccess("user2", "orders", "write"));
        assertFalse(rbac.checkAccess("user2", "orders", "delete"));
    }

    @Test void shouldAllowReaderAccess() {
        assertTrue(rbac.checkAccess("user3", "orders", "read"));
        assertFalse(rbac.checkAccess("user3", "orders", "write"));
    }

    @Test void shouldDenyUnknownUser() {
        assertFalse(rbac.checkAccess("unknown", "orders", "read"));
    }

    @Test void shouldListPermissions() {
        var perms = rbac.getUserPermissions("user3");
        assertTrue(perms.contains("orders:read"));
        assertFalse(perms.contains("orders:write"));
    }

    @Test void shouldHandleAdminPermission() {
        assertTrue(rbac.checkAccess("user1", "config", "admin"));
    }
}
