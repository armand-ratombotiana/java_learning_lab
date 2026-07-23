# Mock Interview: EBS Setup & Configuration (ebs-setup-config)

**Role:** Oracle EBS System Administrator  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are profile options in EBS and how do you configure them?

**Candidate:** Profile options control application behavior at various levels:
- **Site:** Applies to all users across the instance
- **Application:** Applies to a specific application (e.g., General Ledger)
- **Responsibility:** Applies to users with that responsibility
- **User:** Applies to a specific user

Key profile options:
- `FND: Profile Option Hierarchy` — controls resolution order
- `ICX: Session Timeout` — idle session timeout in minutes
- `FND: Developer Mode` — enables debug features
- `PRINTER: Name` — default printer
- `FND: Lookup Codes Table Size` — cache size for lookups

Configuration: Responsibility → System Administrator → Profile → System.

**Interviewer:** How do you configure concurrent managers in EBS?

**Candidate:** Concurrent manager configuration:
1. **Define managers:** Standard Manager, Inventory Manager, Order Import Manager, etc.
2. **Workshift:** Define operating hours and workday calendar
3. **Processes:** Number of concurrent processes per manager
4. **Specialization rules:** Which requests each manager handles (by request type, priority, or data group)
5. **Sleep time:** Idle polling interval

Managed via `Concurrent > Manager > Define` in System Administrator responsibility.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** What is a value set and how is it different from a look-up code?

**Candidate:** 
- **Value Set:** Defines validation rules for a set of values. Used in:
  - Key Flexfields (KFF): Chart of Accounts segments
  - Descriptive Flexfields (DFF): Extra attributes
  - Standard request submission parameters
  - Concurrent program parameters

- **Look-up Code:** Pre-defined, static list of values within EBS. Types:
  - System lookups: Oracle-defined (e.g., System/User, Yes/No)
  - User-defined lookups: Custom lists for application use

Key difference: Lookups are hardcoded in seeded tables. Value sets are flexible validation structures used across flexfields and parameters.

**Interviewer:** Explain how Key Flexfields (KFF) work in EBS.

**Candidate:** Key Flexfields are intelligent key structures used for accounting, costing, and other identifiers. The Chart of Accounts (COA) is the most important KFF:
```
Company.Department.Account.Sub-Account.Product
(2 seg)  (3 seg)    (4 seg)  (5 seg)    (6 seg)
```

KFF configuration:
1. **Structure:** Define number of segments, segment order
2. **Segment qualifiers:** Mark segment as Balancing, Cost Center, Natural Account, etc.
3. **Value sets:** Attach validation rules per segment
4. **Cross-validation rules:** Define valid segment combinations
5. **Dynamic insertion:** Allow account creation on the fly (e.g., from AP invoice)

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a setup and configuration strategy for rolling out EBS to a new subsidiary in Brazil. Include localizations, fiscal requirements, and multi-org setup.

**Candidate:** 

**Phase 1 — Multi-Org Setup:**
```sql
-- Operating Unit creation
1. Define Legal Entity (BR_LEGAL_ENTITY)
2. Define Operating Unit (OU: BR_SALES, BR_PURCHASING)
3. Assign Inventory Organization to OU (BR_INV_ORG)
4. Define Ledger (BR_LEDGER) with:
   - Chart of Accounts (Brazilian COA with Legal Entity segment)
   - Calendar (Brazilian fiscal year: Jan-Dec)
   - Currency (BRL - Brazilian Real)
   - Accounting Method (Brazilian GAAP - BR GAAP)
```

**Phase 2 — Fiscal Configuration (Brazil-specific):**
Brazil has complex fiscal requirements (SPED, NF-e, PIS/COFINS):

1. **Tax configuration:**
   - Setup tax regimes (Simples Nacional, Lucro Presumido, Lucro Real)
   - Configure ICMS, IPI, PIS, COFINS, ISS tax codes
   - Create tax rules and determination matrix

2. **Electronic Invoicing (NF-e):**
   - Configure SEFAZ integration (Brazilian tax authority)
   - Setup digital certificate for NF-e signing
   - Enable Nota Fiscal Electronica in Order Management
   - Configure DANFE (Documento Auxiliar da Nota Fiscal Eletrônica) template

3. **SPED (Sistema Público de Escrituração Digital):**
   - SPED Fiscal: ICMS/IPI bookkeeping
   - SPED PIS/COFINS: Contribution tax reporting
   - SPED ECD (Contábil): Digital accounting bookkeeping
   - SPED EFD-Contribuições: Digital tax assessment

4. **Fiscal books:**
   - Setup fiscal calendar and fiscal periods
   - Configure fiscal document categories (CFOP codes)
   - Setup BR-specific reports (Livro Fiscal, Apuração de ICMS)

**Phase 3 — Profile and Security:**
```sql
-- Brazilian localization profiles
SET_PROFILE('BR: Tax Calculation Mode', 'BATCH', 'SITE');
SET_PROFILE('BR: Legal Entity Classification', 'SIMPLES_NACIONAL', 'SITE');
SET_PROFILE('BR: NF-e Environment', 'PRODUCTION', 'SITE');
SET_PROFILE('BR: Digital Certificate Path', '/u01/certs/br_cert.p12', 'SITE');
```

**Phase 4 — Testing:**
- Complete fiscal cycle test: PO → Receipt → Invoice → Payment → GL
- Complete fiscal document test: Order → Ship → Invoice → NF-e → SEFAZ
- SPED file generation and validation
- Integration tests with Brazilian banks for payments
- Tax calculation audit: Manual calculation vs EBS calculation

**Interviewer:** How do you clone EBS environments for testing?

**Candidate:** EBS cloning process:
1. **Source preparation:** Shut down apps tier, backup source
2. **Database clone:** RMAN duplicate or hot backup
3. **Apps tier clone:**
   - Run `adcfgclone.pl` for apps tier
   - Run `adautocfg.sh` to reconfigure
4. **Post-clone steps:**
   - Change passwords: `FNDCPASS`
   - Reconfigure profile options for test environment
   - Purge sensitive data using data masking
   - Run concurrent managers in test mode

Best practices:
- Use scripts for automated cloning
- Reconfigure email servers for test (don't send real emails)
- Mask sensitive data (credit cards, bank accounts, SSN)
- Update all URLs and hostnames in profile options
- Disable integrations with external systems

---

## Interviewer Feedback

**Strengths:** Strong setup knowledge, practical localization design, clear cloning process  
**Areas to Improve:** Could discuss Oracle EBS Cloud Manager for automated provisioning  
**Verdict:** Hire

---

*EBS Setup & Configuration MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
