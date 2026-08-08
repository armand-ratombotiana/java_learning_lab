package com.learning.backend21.tenant;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TenantIdentifierResolver implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
            new org.hibernate.context.spi.CurrentTenantIdentifierResolver<String>() {
                @Override
                public String resolveCurrentTenantIdentifier() {
                    return TenantContext.getTenantId();
                }

                @Override
                public boolean validateExistingCurrentSessions() {
                    return false;
                }
            });
    }
}
