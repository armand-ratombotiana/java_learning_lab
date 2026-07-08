package com.arch.platform;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

class SelfServiceCatalogUnitTest {
    @Test
    void shouldRegisterAndListItems() {
        SelfServiceCatalog catalog = new SelfServiceCatalog();
        catalog.registerItem(new CatalogItem("vm", "VM", "Provision VM", false, List.of("size")));
        assertEquals(1, catalog.getAvailableItems().size());
    }

    @Test
    void shouldProvisionWithoutApproval() {
        SelfServiceCatalog catalog = new SelfServiceCatalog();
        catalog.registerItem(new CatalogItem("vm", "VM", "Provision VM", false, List.of("size")));
        ProvisionResult result = catalog.provision("vm", "dev", Map.of("size", "small"));
        assertEquals(ProvisionStatus.COMPLETED, result.status());
    }
}
