package com.arch.platform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

class DeveloperPortalTest {
    private DeveloperPortal portal;

    @BeforeEach
    void setUp() {
        portal = new DeveloperPortal();
        portal.registerComponent(new ServiceComponent("svc-1", "order-service", "service", "Handles orders", "production"));
    }

    @Test
    void shouldRegisterAndRetrieveComponent() {
        var component = portal.getComponent("svc-1");
        assertTrue(component.isPresent());
        assertEquals("order-service", component.get().name());
    }

    @Test
    void shouldSearchByName() {
        assertEquals(1, portal.searchByName("order").size());
    }

    @Test
    void shouldFilterByType() {
        assertEquals(1, portal.getComponentsByType("service").size());
    }
}

class GoldenPathTemplateTest {
    @Test
    void shouldGenerateProjectFromTemplate() {
        GoldenPathTemplate template = new GoldenPathTemplate();
        template.registerTemplate("microservice", new TemplateDefinition(
            "microservice", "New microservice",
            List.of("serviceName", "team"),
            List.of("language"),
            Map.of("language", "java"),
            List.of("Create repo", "Setup CI", "Deploy")
        ));
        GeneratedProject project = template.generate("microservice", Map.of("serviceName", "orders", "team", "platform"));
        assertEquals(3, project.steps().size());
    }

    @Test
    void shouldRejectMissingParameters() {
        GoldenPathTemplate template = new GoldenPathTemplate();
        template.registerTemplate("test", new TemplateDefinition("test", "desc", List.of("required"), List.of(), Map.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> template.generate("test", Map.of()));
    }
}

class SelfServiceCatalogTest {
    @Test
    void shouldProvisionApprovedItems() {
        SelfServiceCatalog catalog = new SelfServiceCatalog();
        catalog.registerItem(new CatalogItem("db-prod", "Production DB", "Provision RDS", true, List.of("size")));
        ProvisionResult result = catalog.provision("db-prod", "dev-user", Map.of("size", "medium"));
        assertEquals(ProvisionStatus.PENDING_APPROVAL, result.status());
    }
}
