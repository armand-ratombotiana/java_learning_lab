# Mock Interview: EBS Manufacturing (ebs-manufacturing)

**Role:** Oracle EBS Manufacturing Functional Consultant  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main manufacturing modules in Oracle EBS?

**Candidate:** Oracle EBS Manufacturing modules:
- **Bill of Materials (BOM):** Product structure, routing, engineering change management
- **Work in Process (WIP):** Manufacturing orders, shop floor control, discrete and process manufacturing
- **Cost Management (CST):** Standard cost, actual cost, cost rollup, cost analysis
- **Quality (QA):** Quality inspections, collection plans, statistical process control
- **Oracle Flow Manufacturing:** Line-based (repetitive) manufacturing
- **Oracle Process Manufacturing (OPM):** For process industries (chemical, food, pharma)
- **Oracle Manufacturing Execution System (MES):** Shop floor execution

**Interviewer:** Explain the difference between Discrete and Process manufacturing in EBS.

**Candidate:** 
- **Discrete manufacturing:** Produces distinct items (cars, electronics, machinery). Uses discrete jobs in WIP. Tracks by lot/serial.
- **Flow/repetitive manufacturing:** High-volume production on assembly lines. Uses schedules, not discrete jobs. Tracks by production line.
- **Process manufacturing:** Produces in bulk (chemicals, food, pharmaceuticals). Uses batches, formulas, bulk inventory. Requires potency, shelf life, and quality testing.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does the Work in Process (WIP) module work?

**Candidate:** WIP manages the manufacturing process from issuing raw materials to completing finished goods:
1. **Job creation:** Generate discrete jobs from MPS/MRP, manually, or from sales orders
2. **Job release:** Make the job available for shop floor processing
3. **Material issuance:** Issue components to the job (backflush or pick)
4. **Operation completion:** Report operation completion and move to next operation
5. **Resource charging:** Charge labor and machine time to jobs
6. **Job completion:** Complete assemblies into inventory
7. **Job close:** Final accounting and variance analysis

Key accounting flows:
- Material issued: Debit WIP, Credit Inventory
- Resource charged: Debit WIP, Credit Resource accrual
- Completion: Debit Finished Goods, Credit WIP
- Variance: Any difference between actual and standard cost flows to variance accounts

**Interviewer:** How is manufacturing cost calculated in EBS?

**Candidate:** Costing methods in EBS:
1. **Standard Costing:** Pre-determined standard costs (material, resource, overhead). Variances tracked.
2. **Actual Costing:** Actual costs from purchases and resources. Period-end averaging.
3. **Average Costing:** Weighted average cost per unit.

**Cost rollup:** BOM and routing explosion creates rolled-up costs:
- Item cost = BOM component costs + routing operation resource costs + overhead costs
- Cost types: Frozen (current standard), Pending (proposed changes), Average, Actual
- Cost update: Transfer pending cost to frozen cost

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a manufacturing execution system for a factory that produces 1000 units/day across 3 production lines, with quality checks after each operation and real-time labor tracking.

**Candidate:** 

**System architecture:**
```
Sales Orders → MPS/MRP → Planned Orders → WIP Discrete Jobs
                                               │
                    ┌──────────────────────────┴──────────┐
                    │  Shop Floor Control (MES Interface)  │
                    │  ├── Line 1: Assembly Line A       │
                    │  ├── Line 2: Assembly Line B       │
                    │  └── Line 3: Testing & Packaging    │
                    └──────────────────┬──────────────────┘
                                       │
  ┌────┬────┬────┬────┬────┬────┬──────┴────┐
  │OP10│OP20│OP30│OP40│QC1 │OP50│QC2 │COMPLETE│
  └────┴────┴────┴────┴────┴────┴────┴──────┘
```

**Implementation in EBS:**

1. **BOM and Routing:**
```sql
-- Routing: Assembly_line_A
OP10: Material_Prep       (5 min/unit)
OP20: Subassembly_A        (10 min/unit)
OP30: Subassembly_B        (8 min/unit)
OP40: Final_Assembly       (12 min/unit)
QC1:  Quality_Check_Point  (3 min/unit)
OP50: Calibration          (4 min/unit)
QC2:  Final_Inspection     (5 min/unit)
COMPLETE: Pack_and_Ship    (2 min/unit)
```

2. **Mobile interface for shop floor:**
- Scanners/barcode readers at each station
- Mobile web UI (Oracle MES) for operation sign-on/off
- Real-time job updates via concurrent processing
- Quality data collection at QC points using Oracle Quality collection plans

3. **Quality integration:**
- Each operation triggers collection plan for inspection characteristics
- If QC fails: Stop job, generate NCR (Non-Conformance Report), route to rework station
- Statistical Process Control (SPC): Real-time charting of process capability (Cp, Cpk)

4. **Labor tracking:**
- Employees scan badge at each operation → Resource charging to WIP job
- Real-time labor efficiency %: (Standard hours / Actual hours) × 100
- Overtime premiums calculated and charged to jobs

5. **Performance dashboards:**
- OEE (Overall Equipment Effectiveness): Availability × Performance × Quality
- Real-time job progress: Completed units per station
- Yield analysis: Pass/fail percentage at each quality gate

**Interviewer:** How do you handle engineering changes in EBS Manufacturing?

**Candidate:** EBS Engineering Change Order (ECO) process:
1. **ECO creation:** Request change with reason code, affected items, new BOM/routing
2. **Impact analysis:** Reports showing where the changed item is used
3. **Approval:** Workflow-based approval routing
4. **Effective dates:** Effectivity control (date-based, serial-based)
5. **Implementation:** Update BOM and routing
6. **Cost impact:** Re-roll standard cost
7. **Audit trail:** Full history of changes with before/after comparisons

Oracle Engineering Data Management (EDM) tracks ECOs and associated documents (CAD files, specifications).

---

## Interviewer Feedback

**Strengths:** Deep manufacturing knowledge, practical MES design, strong BOM/routing understanding  
**Areas to Improve:** Could discuss Oracle Manufacturing Cloud vs EBS Manufacturing  
**Verdict:** Strong Hire

---

*EBS Manufacturing MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
