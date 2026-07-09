# Interview: EBS Manufacturing

## Technical Questions

### Q1: Explain EBS multi-tier architecture.

A: Three tiers: desktop (Forms/OAF), application (Forms server, OHS, concurrent managers), database (Oracle DB 19c).

### Q2: What is MOAC?

A: Multi-Org Access Control lets one EBS instance serve multiple operating units using VPD with FND_MOBS.

### Q3: Describe EBR in R12.2.

A: Edition-Based Redefinition allows multiple object versions. ADOP manages prepare, apply, finalize, cutover, cleanup.

### Q4: Explain concurrent processing.

A: Manager polls FND_CONCURRENT_REQUESTS for pending requests, spawns workers, updates status when done.

### Q5: What is CEMLI?

A: Configuration, Extension, Modification, Localization, Integration - five customization categories.

### Q6: How does VPD work?

A: Adds WHERE clause via fine-grained access control. FND_MOBS adds org_id filtering.

### Q7: ADOP phases?

A: Prepare (create edition), Apply (apply patches), Finalize (ready), Cutover (switch), Cleanup (remove old).

## Behavioral Questions

- Describe a challenging EBS implementation.

- How to handle production issues?

- Approach to EBS security?

- How to stay current with EBS updates?
