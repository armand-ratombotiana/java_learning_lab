package com.learning.backend21.tenant;

public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final String DEFAULT_TENANT = "public";

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId != null ? tenantId : DEFAULT_TENANT);
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get() != null ? CURRENT_TENANT.get() : DEFAULT_TENANT;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
