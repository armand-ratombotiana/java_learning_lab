# Mock Interview: EBS Financials (ebs-financials)

**Role:** Oracle EBS Financials Functional Consultant  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main modules in Oracle EBS Financials?

**Candidate:** Oracle EBS Financials core modules:
- **General Ledger (GL):** Chart of accounts, journal entries, budgeting, consolidation, financial reporting
- **Accounts Payables (AP):** Invoice processing, payments, supplier management, expense reporting
- **Accounts Receivables (AR):** Customer invoicing, receipts, collections, credit management
- **Cash Management (CE):** Bank reconciliation, cash forecasting, bank statement processing
- **Fixed Assets (FA):** Asset tracking, depreciation, retirement, tax reporting
- **Advanced Collections (IEX):** Automated collection strategy and dunning
- **iReceivables:** Customer online payment portal
- **Financials Accounting Hub (FAH):** Subledger accounting integration

**Interviewer:** Explain the Order to Cash (O2C) cycle in EBS.

**Candidate:** The Order to Cash cycle:
1. **Sales Order:** Entered in Order Management (OE)
2. **Inventory:** Check availability, reserve, pick, pack, ship (INV)
3. **Invoicing:** Generate customer invoice in Accounts Receivable (AR)
4. **Receipts:** Customer payment application in AR
5. **Cash Application:** Auto-match receipts to invoices
6. **GL Posting:** Revenue recognition and cash entries posted to General Ledger (GL)
7. **Reporting:** Financial statements, AR aging, revenue analysis

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does the General Ledger consolidation process work?

**Candidate:** GL consolidation aggregates financial data from multiple entities into a single reporting entity:
1. **Set of Books (SOB) definition:** Each entity has its own SOB with chart of accounts, calendar, currency
2. **Mapping rules:** Define how source accounts map to target accounts
3. **Consolidation run:** Scheduled process that reads balances from child SOBs
4. **Currency translation:** Translates local currency to reporting currency
5. **Journal import:** Creates consolidated journal entries in the parent SOB
6. **Eliminations:** Intercompany transactions eliminated (via `GL_INTERCOMPANY` module)

In EBS R12, the **Multi-Org Access Control (MOAC)** and **Ledger** concept (replaced SOB) simplify consolidation. Subledger accounting (SLA) provides real-time accounting representation.

**Interviewer:** Explain the Procure to Pay (P2P) cycle.

**Candidate:** 
1. **Requisition:** Employee requests goods/services (iProcurement)
2. **Purchase Order:** Buyer creates PO in Purchasing (PO)
3. **Receiving:** Goods received in Inventory (INV)
4. **Invoice:** Supplier invoice entered in AP (manual, scanned, or EDI)
5. **Matching:** 2-way (PO-Invoice), 3-way (PO-Receipt-Invoice)
6. **Payment:** AP payment batch processes, payment format generation
7. **GL:** AP invoices, payments, and accruals posted to GL

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** A client is experiencing a 3-day delay in month-end close. They have 50+ legal entities across 20 countries. How do you optimize the close process using EBS Financials?

**Candidate:** 

**Close optimization strategy:**

**Phase 1 — Identify bottlenecks:**
Review current close process timeline. Common bottlenecks:
- AP invoice processing wait time (can't close until vendor invoices are entered)
- AR cash application delays (unapplied receipts)
- Intercompany reconciliation (manual, time-consuming)
- Manual journal entries for adjustments
- Bank reconciliation staleness

**Phase 2 — Implement acceleration features:**

1. **Subledger Accounting (SLA):** Automates accounting rules for all subledgers. Journals created in real-time, no batch post-processing.
2. **AutoInvoice (AR):** Automates customer invoice creation from external systems.
3. **AutoLockbox (AR):** Automates cash application from bank files (90%+ auto-match rate).
4. **AP Imaging:** OCR-based invoice processing reduces manual entry time by 70%.
5. **Intercompany Automation:** Use `GL_INTERCOMPANY` module to auto-create balancing entries.
6. **Mass Allocation:** Automate recurring adjusting entries with `GL_ALLOCATE` program.

**Phase 3 — Process changes:**
```sql
-- Close checklist automation (using EBS Workflow)
Step 1: AR Close → Run Revenue Recognition, AutoInvoice, Receipt Application
Step 2: AP Close → Run Payables Rollback, Accrual Reversal
Step 3: Inventory Close → Run Period Close, WIP Period Close
Step 4: Fixed Assets → Run Depreciation
Step 5: GL Consolidation → Run Translation, Consolidation, Eliminations
Step 6: Reports → Generate financial statements
```

**Phase 4 — Parallel processing:**
- Use multiple concurrent managers for GL consolidation across entities
- Run AR and AP close processes in parallel (they affect different GL accounts)
- Schedule entity-level consolidation to run concurrently on separate RAC nodes

**Result:** Close time reduced from 3 days to 8 hours.

**Interviewer:** How would you handle intercompany transactions in EBS?

**Candidate:** EBS Intercompany provides automated transaction and settlement:
1. **Intercompany transaction types:** Sales/Services, Loans, Cost Allocations, Dividends
2. **Flow:**
   - Source entity creates intercompany transaction in either AP, AR, or direct GL
   - System automatically generates dual entries (receivable in source, payable in target)
   - Settlement process manages cash transfer between entities
3. **Configuration:** Intercompany relationships, transaction types, and approval limits
4. **Benefits:** Eliminates manual reconciliations, ensures balancing, provides audit trail

For multi-currency intercompany, system uses agreed-upon exchange rates and automatically calculates gains/losses.

---

## Interviewer Feedback

**Strengths:** Excellent financial module knowledge, practical close optimization strategy  
**Areas to Improve:** Could discuss Oracle Financial Services Analytical Applications (OFSAA) integration  
**Verdict:** Strong Hire

---

*EBS Financials MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
