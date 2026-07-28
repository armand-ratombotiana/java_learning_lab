# Lab 06: Mock Interview — Governance Lead

**Interviewer**: "Design a data governance framework for a financial services company with 1000+ datasets across 20 business units."

**Candidate**: "The foundation is a three-layer model: classification, policy, and audit. Classification assigns sensitivity labels (PII, confidential, internal, public) using automated detectors and manual curation. Policies are defined at the domain level using ABAC with attributes like data classification, user role, and processing purpose. All access is logged immutably for compliance."

**Interviewer**: "How do you handle cross-border data transfers (GDPR)?"

**Candidate**: "Each dataset is tagged with a data residency attribute. The policy engine checks both the classification and the residency. If a user in the US tries to access EU-citizen data, the policy engine checks if there's a legitimate purpose (e.g., legal requirement) and logs the cross-border transfer. We use a data residency map that tracks which regions store which data and ensures compliance with Schrems II."

**Interviewer**: "How do you implement column-level access control in a data lake that uses Parquet?"

**Candidate**: "We use a proxy layer between the query engine (Trino/Athena) and the storage. The proxy parses the query, identifies the columns being accessed, evaluates policies for each column, and either allows, denies, or masks the column. For Parquet, we leverage column pruning: restricted columns are simply omitted from the scan."

**Interviewer**: "How do you handle consumer data deletion requests across all systems?"

**Candidate**: "We maintain a data map: a graph of all systems, datasets, and their relationships (lineage). When a deletion request comes in, the governance system traces the user's data through the lineage graph, identifies all copies, and issues deletion jobs. We use a two-phase approach: first soft-delete (mark as deleted), then after a retention period, hard-delete. For backup snapshots, we use purge-on-read."
