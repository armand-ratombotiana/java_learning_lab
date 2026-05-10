package com.learning.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;

public class KeycloakSolution {

    private final Keycloak keycloak;

    public KeycloakSolution(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealm(String realmName) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmName);
        realm.setEnabled(true);
        keycloak.realms().create(realm);
    }

    public RealmResource getRealm(String realmName) {
        return keycloak.realm(realmName);
    }

    public void createClient(String realm, ClientRepresentation client) {
        getRealm(realm).clients().create(client);
    }

    public void createUser(String realm, String username, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setCredentials(List.of(createCredential(password)));
        getRealm(realm).users().create(user);
    }

    private Map<String, Object> createCredential(String password) {
        return Map.of(
            "type", "password",
            "value", password,
            "temporary", false
        );
    }

    public List<UserRepresentation> getUsers(String realm) {
        return getRealm(realm).users().list();
    }

    public void updateUser(String realm, String userId, UserRepresentation user) {
        getRealm(realm).users().get(userId).update(user);
    }

    public void deleteUser(String realm, String userId) {
        getRealm(realm).users().get(userId).remove();
    }

    public String getToken(String realm, String clientId, String username, String password) {
        return keycloak.tokenManager().getAccessTokenString();
    }
}