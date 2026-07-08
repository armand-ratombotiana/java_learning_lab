package com.arch.stranglerfig;

import java.util.function.Predicate;

public class StranglerRouter {
    private final LegacySystem legacySystem;
    private final NewSystemModule newSystem;
    private final Predicate<String> migrationPredicate;

    public StranglerRouter(LegacySystem legacySystem, NewSystemModule newSystem, Predicate<String> migrationPredicate) {
        this.legacySystem = legacySystem;
        this.newSystem = newSystem;
        this.migrationPredicate = migrationPredicate;
    }

    public String getUser(String userId) {
        if (migrationPredicate.test(userId)) {
            String result = newSystem.getUser(userId);
            if ("{}".equals(result)) {
                return legacySystem.getUser(userId);
            }
            return result;
        }
        return legacySystem.getUser(userId);
    }

    public String createUser(String userId, String userData) {
        if (migrationPredicate.test(userId)) {
            return newSystem.createUser(userId, userData);
        }
        return legacySystem.createUser(userId, userData);
    }

    public String updateUser(String userId, String userData) {
        if (migrationPredicate.test(userId)) {
            return newSystem.updateUser(userId, userData);
        }
        return legacySystem.updateUser(userId, userData);
    }

    public boolean deleteUser(String userId) {
        if (migrationPredicate.test(userId)) {
            return newSystem.deleteUser(userId);
        }
        return legacySystem.deleteUser(userId);
    }

    public boolean isFullyMigrated() {
        return newSystem.getUserCount() > 0 && legacySystem.getUserCount() > 0;
    }
}
