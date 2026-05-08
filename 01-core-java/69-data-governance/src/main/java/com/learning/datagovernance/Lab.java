package com.learning.datagovernance;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    record DataField(String name, String type, boolean required, String description) {}
    record DataAsset(String id, String name, String owner, String classification, List<DataField> fields) {}

    static class DataCatalog {
        private final Map<String, DataAsset> assets = new ConcurrentHashMap<>();

        void register(DataAsset asset) {
            assets.put(asset.id(), asset);
        }

        DataAsset lookup(String id) {
            return assets.get(id);
        }

        List<DataAsset> searchByClassification(String classification) {
            return assets.values().stream()
                .filter(a -> a.classification().equals(classification))
                .toList();
        }

        void report() {
            System.out.println("  Data catalog (" + assets.size() + " assets):");
            assets.forEach((id, asset) -> {
                System.out.printf("    %s: %s (%s, %d fields)%n",
                    id, asset.name(), asset.classification(), asset.fields().size());
            });
        }
    }

    static class DataMasking {
        static String maskEmail(String email) {
            var parts = email.split("@");
            if (parts.length != 2) return email;
            return parts[0].charAt(0) + "***@" + parts[1];
        }

        static String maskPhone(String phone) {
            if (phone.length() < 4) return phone;
            return "***-***-" + phone.substring(phone.length() - 4);
        }

        static String maskSSN(String ssn) {
            return "***-**-" + ssn.substring(ssn.length() - 4);
        }
    }

    enum Classification {
        PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED
    }

    public static void main(String[] args) {
        System.out.println("=== Data Governance Lab ===\n");

        dataClassification();
        dataCatalog();
        dataMasking();
        gdprCompliance();
        dataLifecycle();
    }

    static void dataClassification() {
        System.out.println("--- Data Classification ---");
        System.out.println("""
  Classification levels:

  PUBLIC:
    - Marketing materials, company addresses, job postings
    - No access control needed
    - Example: product catalog, press releases

  INTERNAL:
    - Internal documentation, org charts, project plans
    - Access: all employees
    - Example: code repositories, meeting notes

  CONFIDENTIAL:
    - Customer PII, financial data, trade secrets
    - Access: need-to-know basis
    - Example: SSN, credit cards, source code

  RESTRICTED:
    - Highly sensitive, legal/compliance regulated
    - Access: explicit approval required
    - Example: HIPAA health records, classified IP

  Data owner: responsible for classification
  Data steward: maintains data quality & lineage
    """);
    }

    static void dataCatalog() {
        System.out.println("\n--- Data Catalog ---");
        var catalog = new DataCatalog();

        catalog.register(new DataAsset("USR-001", "Customer Profiles", "DataEngineering",
            "CONFIDENTIAL", List.of(
                new DataField("user_id", "STRING", true, "Unique user identifier"),
                new DataField("email", "STRING", true, "User email (PII)"),
                new DataField("phone", "STRING", true, "Phone number (PII)"),
                new DataField("ssn", "STRING", false, "Social security number (Sensitive PII)")
            )));

        catalog.register(new DataAsset("PROD-001", "Product Catalog", "ProductTeam",
            "PUBLIC", List.of(
                new DataField("product_id", "STRING", true, "SKU"),
                new DataField("name", "STRING", true, "Product name"),
                new DataField("price", "DECIMAL", true, "Current price")
            )));

        catalog.register(new DataAsset("FIN-001", "Revenue Reports", "Finance",
            "RESTRICTED", List.of(
                new DataField("quarter", "STRING", true, "Fiscal quarter"),
                new DataField("revenue", "DECIMAL", true, "Total revenue"),
                new DataField("cost", "DECIMAL", true, "Operating cost")
            )));

        catalog.report();
        System.out.println("  Confidential assets: " +
            catalog.searchByClassification("CONFIDENTIAL").size());
    }

    static void dataMasking() {
        System.out.println("\n--- Data Masking ---");
        var emails = List.of("alice@example.com", "bob@test.com", "carol@company.org");
        var phones = List.of("555-123-4567", "555-987-6543");
        var ssns = List.of("123-45-6789", "987-65-4321");

        System.out.println("  Email masking:");
        emails.forEach(e -> System.out.println("    " + e + " -> " + DataMasking.maskEmail(e)));

        System.out.println("  Phone masking:");
        phones.forEach(p -> System.out.println("    " + p + " -> " + DataMasking.maskPhone(p)));

        System.out.println("  SSN masking:");
        ssns.forEach(s -> System.out.println("    " + s + " -> " + DataMasking.maskSSN(s)));

        System.out.println("""
  Masking techniques:
  - Static masking: replace with fake but realistic data
  - Dynamic masking: mask based on user role at query time
  - Encryption: AES-256 for storage, decrypt on need
  - Tokenization: replace with non-sensitive token (PCI DSS)
  - Differential privacy: add noise to aggregate queries
    """);
    }

    static void gdprCompliance() {
        System.out.println("\n--- GDPR Compliance ---");
        System.out.println("""
  Key GDPR requirements for data governance:

  Right to be forgotten:
    DELETE FROM users WHERE user_id = ?
    Must delete all related data in all systems
    (logs, backups, analytics)

  Right to data portability:
    SELECT * FROM users WHERE user_id = ?
    Export as JSON/CSV for the user

  Data Processing Agreement (DPA):
    Required with all data processors (cloud providers, SaaS)

  Data Protection Impact Assessment (DPIA):
    Required for high-risk processing (profiling, biometrics)

  Breach notification:
    Notify DPA within 72 hours
    Notify affected users without undue delay

  Data mapping:
    Know where every piece of PII is stored
    Including third-party processors

  Records of Processing Activities (ROPA):
    Document all data processing activities
    """);
    }

    static void dataLifecycle() {
        System.out.println("\n--- Data Lifecycle Management ---");
        System.out.println("""
  1. CREATE/INGEST
     - Validate schema, classify sensitivity
     - Encrypt at rest (AES-256)
     - Log lineage (source, timestamp, transformation)

  2. STORE
     - Tiered storage: hot (SSD), warm (HDD), cold (archive)
     - Retention policy: how long to keep?
     - Backup schedule: daily incremental, weekly full

  3. USE/PROCESS
     - Access control: RBAC / ABAC
     - Audit logging: who accessed what, when
     - Mask/PII-redact for non-privileged access

  4. ARCHIVE
     - Move to cold storage (S3 Glacier, Azure Archive)
     - Compress for long-term retention
     - Metadata preserved in catalog

  5. DELETE/DESTROY
     - Secure deletion (overwrite + verify)
     - Certificate of destruction for compliance
     - Verify no residual copies in backups

  Tools: Apache Atlas, Collibra, Alation, Datadog
    """);
    }
}
