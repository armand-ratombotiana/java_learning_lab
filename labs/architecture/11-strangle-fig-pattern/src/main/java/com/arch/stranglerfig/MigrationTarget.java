package com.arch.stranglerfig;

public sealed interface MigrationTarget permits NewSystemModule {
    String getUser(String userId);
    String createUser(String userId, String userData);
    String updateUser(String userId, String userData);
    boolean deleteUser(String userId);
    boolean isHealthy();
}
