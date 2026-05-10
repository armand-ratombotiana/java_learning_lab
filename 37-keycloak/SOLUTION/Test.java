package com.learning.keycloak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeycloakSolutionTest {

    private Keycloak mockKeycloak;
    private RealmResource mockRealm;
    private UsersResource mockUsers;
    private KeycloakSolution solution;

    @BeforeEach
    void setUp() {
        mockKeycloak = mock(Keycloak.class);
        mockRealm = mock(RealmResource.class);
        mockUsers = mock(UsersResource.class);
        solution = new KeycloakSolution(mockKeycloak);
    }

    @Test
    void testCreateRealm() {
        solution.createRealm("test-realm");
        verify(mockKeycloak.realms()).create(any());
    }

    @Test
    void testGetRealm() {
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);
        RealmResource result = solution.getRealm("test-realm");
        assertNotNull(result);
    }

    @Test
    void testCreateUser() {
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);
        when(mockRealm.users()).thenReturn(mockUsers);
        solution.createUser("test-realm", "testuser", "test@test.com", "password");
        verify(mockUsers).create(any());
    }

    @Test
    void testGetUsers() {
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);
        when(mockRealm.users()).thenReturn(mockUsers);
        when(mockUsers.list()).thenReturn(List.of());
        List<UserRepresentation> users = solution.getUsers("test-realm");
        assertNotNull(users);
    }

    @Test
    void testDeleteUser() {
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);
        when(mockRealm.users()).thenReturn(mockUsers);
        solution.deleteUser("test-realm", "user-id");
        verify(mockUsers).get("user-id").remove();
    }
}