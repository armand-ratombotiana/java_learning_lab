# Oracle E-Business Suite — Interview Cheatsheet

<div align="center">

![Oracle EBS](https://img.shields.io/badge/Oracle_EBS-F80000?style=for-the-badge)
![Cheatsheet](https://img.shields.io/badge/Cheatsheet-FF6F00?style=for-the-badge)

**Quick reference for Oracle EBS interviews — modules, key tables, concurrent manager, ADOP**

</div>

---

## EBS Modules Reference

### Core Financials
| Module | Full Name | Key Business Process | Key Tables |
|--------|-----------|---------------------|------------|
| GL | General Ledger | Record-to-Report | GL_JE_HEADERS, GL_JE_LINES, GL_BALANCES |
| AP | Accounts Payable | Procure-to-Pay | AP_INVOICES_ALL, AP_INVOICE_PAYMENTS_ALL, AP_CHECKS_ALL |
| AR | Accounts Receivable | Order-to-Cash | RA_CUSTOMER_TRX_ALL, AR_PAYMENT_SCHEDULES_ALL, AR_CASH_RECEIPTS_ALL |
| CM | Cash Management | Bank Reconciliation | CE_BANK_ACCOUNTS, CE_STATEMENT_HEADERS, CE_STATEMENT_LINES |
| FA | Fixed Assets | Asset Lifecycle | FA_BOOKS, FA_ADDITIONS, FA_DEPRN_DETAIL |
| SLA | Subledger Accounting | Accounting Engine | SLA_SUBLEDGER_EVENTS, XL_EVENTS, XLA_DISTRIBUTION_LINKS |

### Supply Chain Management
| Module | Full Name | Key Business Process | Key Tables |
|--------|-----------|---------------------|------------|
| PO | Purchasing | Procure-to-Pay | PO_HEADERS_ALL, PO_LINES_ALL, PO_DISTRIBUTIONS_ALL |
| OM | Order Management | Order-to-Cash | OE_ORDER_HEADERS_ALL, OE_ORDER_LINES_ALL, OE_TRANSACTION_TYPES_TL |
| INV | Inventory | Inventory Management | MTL_SYSTEM_ITEMS_B, MTL_ONHAND_QUANTITIES, MTL_MATERIAL_TRANSACTIONS |
| WSH | Shipping | Shipment Processing | WSH_DELIVERY_DETAILS, WSH_DELIVERY_ASSIGNMENTS |
| WMS | Warehouse Management | Warehouse Operations | WMS_LICENSE_PLATE_NUMBERS, WMS_PUTAWAY_TASKS |
| MSC | Advanced Supply Chain Planning | Supply Planning | MSC_PLANS, MSC_SUPPLIES, MSC_DEMANDS |

### Manufacturing
| Module | Full Name | Key Tables |
|--------|-----------|------------|
| BOM | Bills of Material | BOM_BILLS_OF_MATERIAL, BOM_INVENTORY_COMPONENTS |
| WIP | Work in Process | WIP_ENTITIES, WIP_OPERATIONS, WIP_MOVE_TRANSACTIONS |
| MRP | Material Requirements Planning | MRP_RECOMMENDATIONS, MRP_SCHEDULE_ITEMS |
| QM | Quality Management | QA_PLANS, QA_RESULTS |

### HRMS
| Module | Full Name | Key Tables |
|--------|-----------|------------|
| PER | People | PER_ALL_PEOPLE_F, PER_ASSIGNMENTS_F |
| PAY | Payroll | PAY_RUN_RESULTS, PAY_ELEMENT_ENTRIES_VALUES |
| SSHR | Self-Service HR | IEX_* tables |

---

## Key Tables by Module

### GL Key Tables
| Table | Description |
|-------|-------------|
| GL_JE_HEADERS | Journal entry headers (JE name, category, currency, status) |
| GL_JE_LINES | Journal entry lines (account, amount, line description) |
| GL_BALANCES | Period balances (actual, budget, encumbrance) |
| GL_CODE_COMBINATIONS | Accounting flexfield combinations (CCID) |
| GL_SETS_OF_BOOKS | Set of books definition (calendar, currency, chart of accounts) |
| GL_LEDGER_R12_NEW | R12 ledger definition (supersedes SOB) |
| FND_FLEX_VALUES | Flexfield value sets |
| GL_IMPORT_REFERENCES | Journal import reference data |

### AP Key Tables
| Table | Description |
|-------|-------------|
| AP_INVOICES_ALL | Invoice headers (invoice number, date, amount, vendor) |
| AP_INVOICE_LINES_ALL | Invoice lines (line amount, description, distribution) |
| AP_INVOICE_PAYMENTS_ALL | Invoice payment schedules (due date, amount due) |
| AP_CHECKS_ALL | Payment checks (check number, amount, status) |
| AP_PAYMENT_SCHEDULES | Payment due dates and status |
| AP_HOLDS_ALL | Invoice holds (type, reason, release) |
| AP_SUPPLIERS | Supplier master data |
| AP_BANK_ACCOUNTS | Supplier bank account information |

### AR Key Tables
| Table | Description |
|-------|-------------|
| RA_CUSTOMER_TRX_ALL | Transaction headers (invoice, DM, CM) |
| RA_CUSTOMER_TRX_LINES_ALL | Transaction lines |
| AR_PAYMENT_SCHEDULES_ALL | Payment schedules (due dates, aging) |
| AR_CASH_RECEIPTS_ALL | Receipts (amount, status, bank) |
| HZ_CUST_ACCOUNTS | Customer accounts (R12 customer model) |
| HZ_PARTIES | Party registry (individuals, organizations) |
| AR_DISTRIBUTIONS_ALL | Accounting distributions |

### PO Key Tables
| Table | Description |
|-------|-------------|
| PO_HEADERS_ALL | Purchase order headers |
| PO_LINES_ALL | Purchase order lines |
| PO_LINE_LOCATIONS_ALL | PO line shipments |
| PO_DISTRIBUTIONS_ALL | Charge account distributions |
| PO_REQUISITION_HEADERS_ALL | Requisition headers |
| PO_REQUISITION_LINES_ALL | Requisition lines |
| PO_ASL_ATTRIBUTES | Approved supplier list |
| RCV_TRANSACTIONS | Receiving transactions |

### OM Key Tables
| Table | Description |
|-------|-------------|
| OE_ORDER_HEADERS_ALL | Order headers |
| OE_ORDER_LINES_ALL | Order lines |
| OE_TRANSACTION_TYPES_TL | Order types |
| OE_PRICE_ADJUSTMENTS | Price adjustments and discounts |
| OE_ORDER_HOLDS | Order holds |
| WSH_DELIVERY_DETAILS | Delivery details (pick, ship) |
| WSH_NEW_DELIVERIES | Delivery header |

### INV Key Tables
| Table | Description |
|-------|-------------|
| MTL_SYSTEM_ITEMS_B | Item master (base) |
| MTL_SYSTEM_ITEMS_TL | Item master (translations) |
| MTL_ONHAND_QUANTITIES | On-hand quantities |
| MTL_MATERIAL_TRANSACTIONS | Material transaction history |
| MTL_RESERVATIONS | Reservations |
| MTL_ITEM_CATEGORIES | Item categories |
| MTL_PARAMETERS | Organization parameters |

---

## Concurrent Manager Reference

### Request Types
| Type | Description | Example |
|------|-------------|---------|
| Single Request | Run one program | Submit individual report |
| Request Set | Run group sequentially | Month-end close set |
| Request Set Link | Chain request sets | Period-end processing chain |

### Concurrent Manager Types
| Manager Type | Function | Key Parameters |
|-------------|----------|----------------|
| Internal Manager | Coordinates all managers | Sleep seconds, cache size |
| Standard Manager | General request processing | Processes, sleep, threshold |
| Specialization Rules | Route requests to specific managers | Program name, request type |
| Transaction Managers | Specific module managers | AP, AR, GL transaction managers |

### Key Concurrent Program Setup Tables
| Table | Description |
|-------|-------------|
| FND_CONCURRENT_PROGRAMS | Concurrent program definitions |
| FND_CONCURRENT_REQUESTS | All submitted requests |
| FND_CONC_RELEASE_CLASSES | Release class definitions |
| FND_CONC_RELEASE_STATES | Release state rules |
| FND_CONFLICT_DOMAINS | Conflict resolution domains |
| FND_CONFLICT_RULES | Conflict resolution rules |

### Monitoring Concurrent Requests
```sql
-- Check running requests
SELECT request_id,
       concurrent_program_name,
       description,
       request_date,
       requested_start_date,
       hold_flag,
       phase_code,      -- P=Pending, R=Running, C=Completed
       status_code,     -- A=Active, E=Error, T=Terminated, N=Normal
       argument_text,
       completion_text,
       logfile, outfile
FROM fnd_concurrent_requests
WHERE phase_code = 'R'  -- Running
ORDER BY request_date;

-- Check failed requests in last 24 hours
SELECT request_id, concurrent_program_name,
       argument_text, completion_text,
       TO_CHAR(request_date, 'YYYY-MM-DD HH24:MI') AS request_time,
       TO_CHAR(completion_date, 'YYYY-MM-DD HH24:MI') AS end_time
FROM fnd_concurrent_requests
WHERE status_code IN ('E', 'T')  -- Error, Terminated
  AND request_date > SYSDATE - 1
ORDER BY request_date DESC;
```

---

## ADOP Reference

### ADOP Cycle Phases
| Phase | Command | What It Does | Downtime | Duration |
|-------|---------|-------------|----------|----------|
| Prepare | `adop phase=prepare` | Creates patch edition, copies data | None | 1-4 hours |
| Apply | `adop phase=apply patches=<list>` | Applies patches to patch edition | None | 1-8 hours |
| Finalize | `adop phase=finalize` | Prepares for cutover | None | 30-60 min |
| Cutover | `adop phase=cutover` | Switches to patch edition | 10-30 min | 30 min |
| Cleanup | `adop phase=cleanup` | Removes old run edition | None | 1-2 hours |

### Common ADOP Commands
```bash
# Initialize ADOP for first use
adop phase=prepare -initialize

# Apply a specific patch
adop phase=apply patches=12345678

# Apply multiple patches
adop phase=apply patches=12345678,23456789,34567890

# Apply bundled patch with hot patch
adop phase=apply patches=12345678 hot_patches=98765432

# Check ADOP status
adop phase=prepare -status

# Abort a failed ADOP session
adop phase=abort

# Abort and force cleanup
adop phase=abort -force_cleanup

# View ADOP log
adop phase=status -logfile
```

### ADOP Key Directories
| Directory | Purpose |
|-----------|---------|
| `$AD_TOP/sql/adop.sql` | ADOP main driver script |
| `$APPLRGF/$APPLRGF` | ADOP log files |
| `$INST_TOP/admin/log` | ADOP log files |
| `$PATCH_EDITION_DIR` | Patch edition application files |

### Edition-Based Redefinition (EBR)
| Concept | Description |
|---------|-------------|
| Run Edition | Current production version |
| Patch Edition | New version being patched (selective copy) |
| Edition Context | User session connects to specific edition |
| EBR Tables | Tables with private editioning (only changes are versioned) |
| Cross-Edition Triggers | Copy changes from patch to run edition |

### ADOP Troubleshooting
| Error | Cause | Solution |
|-------|-------|----------|
| ADOP session lock | Previous session crashed | `adop phase=abort` with force |
| Edition exists | Incomplete previous cycle | Drop patch edition manually |
| Inconsistent file system | File system sync issue | Run `adop phase=fs_clone` |
| ORA-04088 trigger error | Cross-edition trigger issue | Recompile triggers |
| Cutover timeout | Long-running sessions | Kill sessions, retry cutover |

---

## EBS SQL Quick Reference

### Common Diagnostic Queries
```sql
-- Check current EBS version
SELECT release_name FROM apps.fnd_product_groups;

-- Check application version
SELECT application_id, application_short_name, product_name, version
FROM fnd_product_installations
WHERE patch_level IS NOT NULL;

-- Check active users
SELECT user_name, description, last_logon_date, start_time
FROM fnd_logins
WHERE start_time > SYSDATE - 1
ORDER BY start_time DESC;

-- Check profile option values
SELECT profile_option_name, profile_option_value, level_type, level_value
FROM fnd_profile_option_values
WHERE profile_option_id = (
    SELECT profile_option_id
    FROM fnd_profile_options
    WHERE profile_option_name = 'FND_COLOR_SCHEME'
);

-- Check workflow status
SELECT item_key, item_type, process, owner, begin_date, end_date, run_mode
FROM wf_items
WHERE begin_date > SYSDATE - 1
ORDER BY begin_date DESC;
```

### Common EBS Interview SQL Patterns
```sql
-- Find invoices on hold
SELECT ai.invoice_num, ai.invoice_date, ai.invoice_amount,
       ah.hold_reason, ah.hold_date, ah.release_reason
FROM ap_invoices_all ai
JOIN ap_holds_all ah ON ai.invoice_id = ah.invoice_id
WHERE ah.release_date IS NULL
ORDER BY ai.invoice_num;

-- Find orders stuck in workflow
SELECT ooh.order_number, ooh.flow_status_code,
       ooh.ordered_date, wfi.item_key,
       wfi.end_date AS workflow_end_date
FROM oe_order_headers_all ooh
JOIN wf_items wfi ON wfi.item_key = to_char(ooh.header_id)
WHERE wfi.end_date IS NULL
  AND ooh.ordered_date > SYSDATE - 7
ORDER BY ooh.ordered_date;

-- Find AP invoice that hasn't been paid
SELECT ai.invoice_num, ai.invoice_amount, ai.invoice_date,
       aps.amount_due_remaining, aps.due_date
FROM ap_invoices_all ai
JOIN ap_payment_schedules_all aps ON ai.invoice_id = aps.invoice_id
WHERE aps.amount_due_remaining > 0
  AND aps.due_date < SYSDATE
ORDER BY aps.due_date;

-- Check concurrent request log for errors
SELECT request_id, user_concurrent_program_name,
       argument_text, completion_text, phase_code, status_code
FROM fnd_concurrent_requests
WHERE status_code = 'E'
  AND request_date > SYSDATE - 7
ORDER BY request_date DESC;
```

---

<div align="center">

**"EBS interviews test real-world experience — be prepared to walk through actual projects and problems."**

---

[Back to Top](#oracle-e-business-suite--interview-cheatsheet)

</div>
