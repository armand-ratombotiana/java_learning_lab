# Mock Interview: Data Lake (07-data-lake)

## Scenario: Design a data lake for a healthcare organization
Your healthcare company needs a data lake on AWS S3 storing EHR (electronic health records), lab results, and imaging metadata. HIPAA compliance required. Data volume: 50TB initially, growing 5TB/month.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Data Lake Architecture (15 min)

**Folder/partition structure:**
```
s3://healthcare-data-lake/
├── raw/                          # Immutable raw data
│   ├── ehr/
│   │   ├── source=epic/
│   │   │   ├── dt=2024-01-01/
│   │   │   │   ├── patient_demographics.parquet
│   │   │   │   ├── clinical_notes.parquet
│   │   │   │   └── medications.parquet
│   │   │   └── dt=2024-01-02/
│   │   └── source=athenahealth/
│   │       └── ...
│   ├── lab_results/
│   │   ├── source=laboratory_corp/
│   │   │   └── dt=2024-01-01/
│   │   └── source=quest/
│   │       └── dt=2024-01-01/
│   └── imaging_metadata/
│       └── dt=2024-01-01/
├── stage/                        # Transient, processing zone
│   ├── pipeline_id=1234/
│   └── pipeline_id=1235/
├── curated/                      # Cleaned, validated, PHI-masked
│   ├── patient/
│   │   ├── dim_patient/
│   │   └── fact_encounters/
│   ├── clinical/
│   │   ├── dim_diagnosis/
│   │   └── fact_medications/
│   └── lab/
│       └── fact_lab_results/
├── analytics/                    # Aggregated, de-identified for BI
│   ├── population_health/
│   ├── quality_metrics/
│   └── operational_reports/
└── archive/                      # Compressed, Glacier
    └── year=2023/
```

**Format decisions:**
| Data Type | Format | Compression | Rationale |
|-----------|--------|------------|-----------|
| EHR (structured) | Parquet | ZSTD | Columnar, efficient for analytics |
| Clinical notes | Parquet | ZSTD | Columnar, but NLP still parses |
| Lab results | Parquet | ZSTD | Numeric, columnar compression |
| Imaging metadata | Parquet | ZSTD | Structured metadata |
| Raw FHIR JSON | JSON (gzip) | Gzip | Schema-on-read, flexibility |
| Audit logs | Parquet | Snappy | Fast writes, columnar queries |

---

## Part 2: Security & Compliance (10 min)

**HIPAA compliance requirements:**

| Requirement | Implementation |
|-------------|---------------|
| Encryption at rest | SSE-S3 with KMS (Customer-managed key) |
| Encryption in transit | TLS 1.2+ for all data movement |
| Access control | IAM policies + S3 bucket policies + Lake Formation |
| PHI identification | Auto-classify using Macie + custom regex patterns |
| Audit logging | CloudTrail + S3 server access logs |
| Data retention | 6 years minimum (HIPAA), longer policies configurable |
| Access reviews | Quarterly IAM access review + automated reports |

**PHI/PII handling:**
```
raw (full PHI) → tokenized/stage (PHI masked) → curated (de-identified ID)
1. Tokenize: map real patient_id → surrogate_id (hash with salt)
2. Mask: SSN → XXX-XX-1234, DOB → year only (for analytics)
3. Redact: Free text fields scanned for PHI (regex + NLP model)
4. Audit: Track who accessed PHI, when, for what purpose
5. Anonymize: Remove direct identifiers for analytics layer
```

**Access control model:**
```
Admin: Full access to raw + curated (limited team)
Analyst: Access to curated (de-identified) only
Researcher: Access to analytics (aggregated) only
Auditor: Read-only access to CloudTrail + access logs
External: No direct S3 access, data shared via Snowflake/Databricks with row-level security
```

---

## Part 3: Ingestion Pipeline (10 min)

**HL7 FHIR data ingestion:**

```
Hospital EHR → FHIR API → Lambda → S3 (raw)
  │                              │
  │                              └── S3 Event → Glue ETL
  │                                           │
  │                                    └── Parse FHIR JSON
  │                                    └── Extract resources (Patient, Encounter, Observation)
  │                                    └── Flatten nested structures
  │                                    └── Validate against FHIR schema
  │                                    └── Mask PHI fields
  │                                    └── Write to curated (Parquet)
  │
  └── Streaming (Kinesis for real-time):
      FHIR webhook → Kinesis → Lambda (validate) → S3 (raw) → Glue ETL
```

**FHIR resource mapping:**
```json
// Raw FHIR Patient resource
{
  "resourceType": "Patient",
  "id": "12345",
  "name": [{"given": ["John"], "family": "Doe"}],
  "birthDate": "1980-01-15",
  "identifier": [{"system": "SSN", "value": "123-45-6789"}]
}

// Curated Parquet schema (PHI masked)
patient_sk INT, patient_id STRING, birth_year INT, gender STRING,
postal_code_prefix STRING, source_system STRING, ingested_at TIMESTAMP
```

---

## Part 4: Query Performance & Governance (10 min)

**Query strategies:**

| User | Query Tool | Data Source | Performance |
|------|-----------|-------------|-------------|
| Data Analyst | Athena | Curated (Parquet) | 10-30 sec (partition pruning) |
| Data Scientist | EMR (Spark) | Raw + Curated | 1-5 min (complex joins) |
| BI Dashboard | Redshift Spectrum | Analytics | 1-5 sec (aggregates) |
| Researcher | Athena | Analytics (de-identified) | 30-60 sec |
| Compliance | CloudTrail + Athena | Audit logs | 1-5 min |

**Performance optimizations:**
- Partition by dt (date) for all datasets
- Use Hive-style partitions for Athena compatibility
- Compaction: 15-minute Glue job to merge small files into 128MB files
- Vacuum: weekly clean of deleted/marked files
- Columnar projection: always specify columns in queries

**Data catalog (AWS Glue Data Catalog):**
- Auto-crawl raw/curated directories every 6 hours
- Track: table schema, partition count, row count, last updated, data quality scores
- Tag datasets: PHI flag, retention policy, data steward, source system
- Search: by dataset name, tag, or full-text description

**Data retention & archival:**
| Layer | Retention | Storage Class | Access |
|-------|-----------|---------------|--------|
| Raw | 90 days | S3 Standard | Infrequent |
| Stage | 7 days | S3 Standard | Processing only |
| Curated | 2 years | S3 Infrequent Access | Frequent queries |
| Analytics | 6 months | S3 Standard | BI dashboards |
| Archive | 6+ years | S3 Glacier Deep Archive | Annual audits |

---

## Follow-up Questions

**S3 vs ADLS vs GCS for healthcare:**
| Feature | S3 | ADLS Gen2 | GCS |
|---------|-----|-----------|-----|
| Encryption | SSE-S3/KMS | SSE-Azure/CMK | CMEK/CSEK |
| Compliance | HIPAA eligible (BAA required) | HIPAA (BAA) | HIPAA (BAA) |
| Access control | IAM + Bucket policies | RBAC + ACLs | IAM + ACLs |
| Object lock | Yes (WORM) | Yes (WORM) | Yes (hold) |
| Strong consistency | Yes (since Dec 2020) | Yes | Yes |
| Best for | AWS ecosystem | Microsoft ecosystem | Google Cloud ecosystem |

**PHI discovery automation:**
- AWS Macie: auto-discover PHI patterns (SSN, DOB, medical terms)
- Custom regex: ICD-10 codes, NPI numbers, RxNorm
- ML models: Named Entity Recognition for clinical notes
- Classification: auto-tag datasets with PHI risk level (high/medium/low)

**Batch vs streaming for EHR:**
- Batch: Bulk FHIR export nightly (preferred for EHR)
- Streaming: Real-time ADT feeds (admission, discharge, transfer)
- Hybrid: Batch for full refresh, streaming for near-real-time updates

