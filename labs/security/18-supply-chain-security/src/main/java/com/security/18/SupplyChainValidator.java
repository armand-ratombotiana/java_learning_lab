package com.security18;

import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class SupplyChainValidator {

    public Sbom generateSbom(String groupId, String artifactId, String version, List<Dependency> deps) {
        Sbom sbom = new Sbom();
        sbom.bomFormat = "CycloneDX";
        sbom.specVersion = "1.4";
        sbom.serialNumber = "urn:uuid:" + UUID.randomUUID();
        sbom.metadata = new Metadata(groupId + ":" + artifactId, version);
        sbom.components = deps.stream()
            .map(d -> new Component(d.groupId + ":" + d.artifactId, d.version, d.type, d.hashes))
            .collect(Collectors.toList());
        return sbom;
    }

    public ScanResult scanDependencies(List<Dependency> deps, Map<String, String> cveDatabase) {
        ScanResult result = new ScanResult();
        result.vulnerable = new ArrayList<>();
        result.total = deps.size();
        result.critical = 0; result.high = 0;

        for (Dependency dep : deps) {
            String key = dep.groupId + ":" + dep.artifactId + "@" + dep.version;
            if (cveDatabase.containsKey(key)) {
                dep.cveId = cveDatabase.get(key);
                dep.severity = "CRITICAL";
                result.critical++;
                result.vulnerable.add(dep);
            }
        }
        result.passed = result.critical == 0;
        return result;
    }

    public boolean verifySignature(byte[] artifact, byte[] signature, byte[] publicKey) throws Exception {
        java.security.Signature sig = java.security.Signature.getInstance("SHA256withRSA");
        sig.initVerify(java.security.KeyFactory.getInstance("RSA")
            .generatePublic(new java.security.spec.X509EncodedKeySpec(publicKey)));
        sig.update(artifact);
        return sig.verify(signature);
    }

    public static class Sbom {
        public String bomFormat, specVersion, serialNumber;
        public Metadata metadata;
        public List<Component> components;
    }
    public static class Metadata { public String component, version; public Metadata(String c, String v) { component = c; version = v; } }
    public static class Component {
        public String name, version, type; public List<String> hashes;
        public Component(String n, String v, String t, List<String> h) { name = n; version = v; type = t; hashes = h; }
    }
    public static class Dependency {
        public String groupId, artifactId, version, type, cveId, severity;
        public List<String> hashes = new ArrayList<>();
    }
    public static class ScanResult {
        public int total, critical, high;
        public boolean passed;
        public List<Dependency> vulnerable;
    }

    public static void main(String[] args) throws Exception {
        SupplyChainValidator validator = new SupplyChainValidator();
        var deps = List.of(new Dependency());
        deps.get(0).groupId = "com.example"; deps.get(0).artifactId = "lib"; deps.get(0).version = "1.0.0";
        Sbom sbom = validator.generateSbom("com.example", "app", "1.0.0", deps);
        System.out.println("SBOM: " + sbom.serialNumber);
    }
}
