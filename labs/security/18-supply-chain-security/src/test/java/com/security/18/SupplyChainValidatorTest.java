package com.security18;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupplyChainValidatorTest {
    @Test
    void testGenerateSbom() {
        SupplyChainValidator validator = new SupplyChainValidator();
        var deps = new java.util.ArrayList<SupplyChainValidator.Dependency>();
        var dep = new SupplyChainValidator.Dependency();
        dep.groupId = "com.example"; dep.artifactId = "lib"; dep.version = "1.0.0";
        dep.hashes.add("sha256:abc123");
        deps.add(dep);
        SupplyChainValidator.Sbom sbom = validator.generateSbom("com.example", "app", "1.0.0", deps);
        assertNotNull(sbom);
        assertEquals("CycloneDX", sbom.bomFormat);
    }

    @Test
    void testScanDependenciesPasses() {
        SupplyChainValidator validator = new SupplyChainValidator();
        var result = validator.scanDependencies(new java.util.ArrayList<>(), new java.util.HashMap<>());
        assertTrue(result.passed);
    }
}
