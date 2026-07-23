# Mock Interview: EBS Supply Chain (ebs-supply-chain)

**Role:** Oracle EBS Supply Chain Functional Consultant  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What modules make up Oracle EBS Supply Chain Management?

**Candidate:** Oracle EBS SCM modules:
- **Purchasing (PO):** Purchase orders, RFQs, sourcing, approval
- **Inventory (INV):** Stock management, transfers, cycle counts, locators
- **Order Management (OM):** Sales orders, pricing, availability, fulfillment
- **Advanced Supply Chain Planning (ASCP):** Supply/demand planning, constrained planning
- **Demand Management (DM):** Demand forecasting
- **Global Order Promising (GOP):** ATP/CTP availability checking
- **Oracle Transportation Management (OTM):** Logistics and freight
- **Warehouse Management (WMS):** Advanced warehousing, labor management
- **Mobile Supply Chain (MSCA):** RF/scanning-based warehouse operations

**Interviewer:** Explain the Purchase Order lifecycle.

**Candidate:** 
1. **Demand:** Generated from MRP, requisition, or manual entry
2. **Requisition:** Employee requests items through iProcurement
3. **Sourcing:** Automatic matching to supplier, contract, or RFQ
4. **Purchase Order creation:** Standard, Planned, Blanket, or Contract PO
5. **PO approval:** Workflow-based approval routing
6. **PO transmission:** EDI, email, or portal to supplier
7. **Receiving:** Goods receipt (quantity, quality inspection)
8. **Invoice matching:** 2-way (PO-Invoice) or 3-way (PO-Receipt-Invoice)
9. **Payment:** Supplier payment processing

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Oracle Inventory manage items across multiple organizations?

**Candidate:** Oracle Inventory uses a master item/item template approach:
- **Master Organization:** Defines items centrally
- **Item attributes:** Control behavior per organization (costing, planning, ordering)
- **Item templates:** Pre-defined attribute sets for item categories

Multi-org visibility:
- **MSCA (Mobile SCM):** Real-time inventory queries across orgs
- **ASCP (Advanced Supply Chain Planning):** Supply/demand across orgs
- **Intransit inventory:** Transfer between orgs tracked via intransit shipment
- **ATP (Available to Promise):** Global availability checking across orgs

**Interviewer:** How do you set up Order Management for a configure-to-order (CTO) business?

**Candidate:** Configure-to-order (CTO) configuration:
1. **Model item:** Configurable top-level item (e.g., a server)
2. **Option classes:** Grouping of optional features (processor, memory, storage)
3. **Options:** Individual choices within option classes
4. **Configurator rules:** Valid option combinations, prerequisites, exclusions
5. **Pricing:** Option-dependent pricing (base price + option surcharges)

Flow: Customer orders model → Configurator selects options → Bill of Materials exploded → WIP generates build jobs → Ship final configured product.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design an end-to-end supply chain for a global electronics company that sources components from Asia, assembles in Mexico, and distributes to customers in North America and Europe.

**Candidate:** 

**Supply chain architecture:**
```
Suppliers (Asia) → Buffer Warehouse (LA) → Assembly Plant (Mexico) → 
    Distribution Centers (US East, EU West) → Customers (NA, EU)
```

**Module configuration:**

1. **Purchasing (PO):**
   - **Suppliers:** Contract management with Asian suppliers
   - **Blanket POs:** Pre-negotiated pricing for components (ETAs, chips)
   - **ASN (Advanced Shipment Notice):** EDI 856 from suppliers
   - **Inbound logistics:** 3PL manages freight from Asia to LA buffer
   - **Inspection:** Quality inspection at LA warehouse (Oracle Quality)

2. **Inventory (INV):**
   - **Multi-org view:**
     ```
     Org: LA_BUFFER — Consignment inventory (vendor-owned)
     Org: MX_PLANT — Raw materials, WIP, finished goods
     Org: DC_USEAST — Regional finished goods stock
     Org: DC_EUROPE — Regional finished goods stock
     ```
   - **Transfer rules:** Auto-transfer LA_Buffer → MX_Plant weekly
   - **Cycle counting:** ABC analysis with cycle count frequencies

3. **Order Management (OM):**
   - **Order types:** Standard, Rush, Backorder
   - **ATP checking:**
     - US customers: ATP against DC_USEAST → if unavailable, MX_Plant
     - EU customers: ATP against DC_EUROPE → if unavailable, MX_Plant (longer lead time)
   - **Price lists:** Customer-specific pricing, contract pricing
   - **Shipping:** Integration with UPS/FedEx for rates and labels

4. **Advanced Supply Chain Planning (ASCP):**
   - **Planning horizon:** 12 months
   - **MPS/MRP:** Weekly planning run (Sunday night)
   - **Constraints:** Supplier capacity, plant capacity, DC capacity
   - **Sourcing rules:**
     - Components: Asian suppliers
     - Assembly: MX_Plant only (single source)
     - Distribution: Market region (NA → DC_US, EU → DC_EU)
   - **Exception messages:** Late supply, excess inventory, demand shortfalls

5. **Integration:**
   - **Supplier portal:** Oracle Sourcing for RFQs, POs, ASNs
   - **3PL integration:** EDI 856 (ASN), 214 (shipment status), 210 (freight invoice)
   - **ERP integration:** GL receiving, AP invoice matching, AR revenue recognition

**Performance metrics:**
- Inventory turns: Target 6x/year
- Order fulfillment rate: Target 98%+
- Perfect order rate: Target 95%
- On-time delivery: Target 96%
- Cash-to-cash cycle: Target 45 days

**Interviewer:** How would you handle a supply chain disruption (e.g., port strike preventing Asian shipments)?

**Candidate:** **Disruption response plan:**
1. **Demand prioritization:** Freeze priority orders for high-value customers
2. **Inventory reallocation:** ATP from DC stocks for critical customers
3. **Alternate sourcing:** Activate secondary suppliers (may cost more but ensures supply)
4. **Air freight escalation:** Switch from ocean to air freight for critical components
5. **ASCP re-plan:** Run constrained plan with reduced supply, identify shortages
6. **Supply chain control tower:** Real-time dashboard of inventory, orders in pipeline
7. **Customer communication:** Proactive lead-time updates via OM workflow

---

## Interviewer Feedback

**Strengths:** Excellent supply chain design, practical multi-org strategy, strong disruption planning  
**Areas to Improve:** Could discuss Oracle IoT Production Monitoring for real-time visibility  
**Verdict:** Strong Hire

---

*EBS Supply Chain MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
