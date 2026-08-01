# Lab 08: Problem Walkthrough — Row-Level Security with Policy Evaluation

## Problem Statement

**Title**: RowLevelSecurityEngine — Policy Evaluation, Masking, and Bypass Guard

**Difficulty**: Medium-Hard

**Category**: Databases, Security, Authorization

---

### Problem

Implement a row-level security engine over an in-memory table:

1. **`Policy`** — a predicate evaluated per row: `(column, op, value)` with ops `EQ, NE, LT, GT, IN` — plus optional masking (replace a column's value with a mask like `"****"` for non-privileged roles)
2. **`RlsTable`** — a table with columns, rows (`Map<String, Object>`), and policies:
   - `addPolicy(name, role, predicate, withCheck)` — `withCheck` is a separate predicate enforced on INSERT/UPDATE (a row that fails it is rejected, not just filtered)
   - `select(role, where)` — returns only rows where the *policy predicate* AND the query predicate hold
   - `insert(role, row)` — rejects the row if it violates any applicable `withCheck` policy
   - `update(role, key, changes)` — only rows visible *and* check-valid may be updated
   - `delete(role, key)` — only visible rows may be deleted
3. **Session context**: `setTenant(role, tenantId)` — policies can reference the session's tenant via a special predicate column `__tenant`
4. **Bypass guard**: a role with `bypassRls=true` ignores policies (simulating `BYPASSRLS`) — and the engine **alerts** (returns a warning) when a bypassing role touches the table
5. A `main` demo: multi-tenant isolation (tenant A cannot see tenant B's rows), cross-tenant insert rejected by `withCheck`, masking for support roles, and the bypass alert.

### Constraints

- Predicates: simple column comparisons; columns are `String`, `Long`, or `Double`
- One table per engine (multi-table is a trivial extension)
- Role context is set via `setTenant` before queries (session-scoped)
- Java 21+ standard library only

### Examples

**Example 1 (tenant isolation):**
```
table orders(id, tenant, amount)
policy: ALL roles: tenant == session.__tenant
setTenant("app", "T1"); select → only T1 rows
setTenant("app", "T2"); select → only T2 rows
```

**Example 2 (withCheck blocks cross-tenant insert):**
```
setTenant("app", "T1")
insert(row {id=9, tenant="T2"}) → REJECTED (row fails withCheck)
```

**Example 3 (masking):**
```
policy: support role sees amount as "****"
setTenant("support", "T1"); select → amounts masked
setTenant("admin", "T1"); select → real amounts
```

**Example 4 (bypass alert):**
```
role "audit" has bypassRls=true; any select logs: "WARNING: role audit BYPASSED RLS on orders"
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

Row-level security is a *predicate sandwich*:

- **On read**: visible rows = (policy predicate AND query predicate). The policy is implicitly ANDed — the user cannot opt out.
- **On write**: every inserted/updated row must additionally satisfy the policy as a `WITH CHECK` predicate. Filtering alone would let a user *write* rows that others can't see — check prevents *both* cross-tenant reads and writes.
- **On delete**: only rows that pass the read predicate can be deleted (delete what you can't see = your deletion is a leak vector).

Two design details matter:

1. **Policy must be re-evaluated on write**: an INSERT with `tenant != session.tenant` fails even though "inserting your own row" might otherwise look fine.
2. **Bypass roles are logged**: `BYPASSRLS` is a maintenance escape hatch; the engine must make its use *observable* (audit trail), which is what the alert simulates.

### Step 2: Naive Approach and Why It Fails

**Naive — application-level filtering:**
```java
List<Row> rows = table.stream().filter(r -> r.tenant().equals(currentTenant)).toList();
```
- The filter lives in one caller; any code path that forgets it leaks rows — exactly the bug RLS exists to make impossible.
- No `withCheck` on writes: `INSERT` can create rows invisible to others (and worse, visible to the *wrong* tenant).
- No masking, no bypass accounting.

**Naive RLS — policy only on SELECT:** the classic half-implementation. Reads are filtered, but UPDATE/DELETE/INSERT bypass → cross-tenant write attack. Our engine enforces all four DML paths.

### Step 3: Design Decisions

1. **Predicate as a small evaluator**: `Predicate(column, Op, value)` with an optional `__tenant` pseudo-column resolved from session state. Keeps evaluation simple, explicit, and testable.
2. **Policies per role**: `Map<String, PolicySet>` where `PolicySet` = `(readPredicate, checkPredicate, masks)`. Multiple policies for the same role AND together (we keep one policy per role for clarity; the engine supports a list).
3. **Masking as a projection step**: after filtering, replace masked columns for roles that lack the `unmasked` capability — mirroring dynamic data masking semantics (display-time only).
4. **Session**: `currentRole` + `currentTenant` on the engine — mimics `current_setting('app.tenant_id')`.
5. **Bypass**: a role flag; every operation logs a warning to the engine's audit list.

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab08;

import java.util.*;

/**
 * RowLevelSecurityEngine — RLS with read predicates, WITH CHECK on writes,
 * column masking, session tenant context, and observable bypass.
 */
public class DatabaseSecurityLab {

    enum Op { EQ, NE, LT, GT, IN }

    record Predicate(String column, Op op, Object value) {
        static final String TENANT = "__tenant";

        /** Evaluate against a row plus the session's tenant. */
        boolean eval(Map<String, Object> row, String sessionTenant) {
            Object actual = column.equals(TENANT) ? sessionTenant : row.get(column);
            if (actual == null) return false;
            return switch (op) {
                case EQ -> Objects.equals(actual, value);
                case NE -> !Objects.equals(actual, value);
                case LT -> compare(actual, value) < 0;
                case GT -> compare(actual, value) > 0;
                case IN -> value instanceof Collection<?> c && c.contains(actual);
            };
        }

        @SuppressWarnings("unchecked")
        private static int compare(Object a, Object b) {
            if (a instanceof Comparable && b instanceof Comparable
                    && a.getClass().isInstance(b)) {
                return ((Comparable<Object>) a).compareTo(b);
            }
            throw new IllegalArgumentException("incomparable: " + a + " vs " + b);
        }
    }

    record Policy(Predicate read, Predicate withCheck, Map<String, String> masks) {
        static Policy of(Predicate read, Predicate check) {
            return new Policy(read, check, Map.of());
        }
        Policy withMask(String column, String mask) {
            var m = new HashMap<>(masks);
            m.put(column, mask);
            return new Policy(read, withCheck, Map.copyOf(m));
        }
    }

    static final class RlsTable {
        private final String name;
        private final Map<String, Policy> policies = new HashMap<>();
        private final Map<String, Boolean> bypassRoles = new HashMap<>();
        private final Map<Long, Map<String, Object>> rows = new LinkedHashMap<>();
        private final List<String> audit = new ArrayList<>();
        private long nextId = 1;

        private String currentRole = "public";
        private String currentTenant = null;

        RlsTable(String name) { this.name = name; }

        // ---------- Configuration ----------

        RlsTable addPolicy(String role, Policy policy) {
            policies.put(role, policy);
            return this;
        }

        RlsTable setBypass(String role, boolean bypass) {
            bypassRoles.put(role, bypass);
            return this;
        }

        RlsTable setSession(String role, String tenant) {
            this.currentRole = role;
            this.currentTenant = tenant;
            return this;
        }

        // ---------- DML ----------

        /** SELECT: policy predicate AND query predicate; masks applied. */
        List<Map<String, Object>> select(String queryColumn, Object queryValue) {
            if (isBypass(currentRole)) audit("WARNING: role " + currentRole + " BYPASSED RLS on " + name);
            Policy policy = effectivePolicy();
            List<Map<String, Object>> result = new ArrayList<>();
            for (var row : rows.values()) {
                if (!passesRead(policy, row)) continue;
                if (queryColumn != null
                        && !Objects.equals(row.get(queryColumn), queryValue)) continue;
                result.add(mask(policy, row));
            }
            return result;
        }

        /** INSERT: row must satisfy the WITH CHECK predicate. */
        boolean insert(Map<String, Object> row) {
            if (isBypass(currentRole)) audit("WARNING: role " + currentRole + " BYPASSED RLS on " + name);
            Policy policy = effectivePolicy();
            if (!passesCheck(policy, row)) {
                audit("BLOCKED insert by " + currentRole + " on " + name + ": check violation");
                return false;
            }
            rows.put(nextId++, new LinkedHashMap<>(row));
            return true;
        }

        /** UPDATE: only rows visible AND check-valid may be changed. */
        boolean update(long id, Map<String, Object> changes) {
            if (isBypass(currentRole)) audit("WARNING: role " + currentRole + " BYPASSED RLS on " + name);
            Policy policy = effectivePolicy();
            Map<String, Object> row = rows.get(id);
            if (row == null) return false;
            if (!passesRead(policy, row)) return false;                    // invisible -> no update
            Map<String, Object> merged = new LinkedHashMap<>(row);
            merged.putAll(changes);
            if (!passesCheck(policy, merged)) {                            // check applies to result
                audit("BLOCKED update by " + currentRole + " on " + name + ": check violation");
                return false;
            }
            rows.put(id, merged);
            return true;
        }

        /** DELETE: only visible rows may be deleted. */
        boolean delete(long id) {
            if (isBypass(currentRole)) audit("WARNING: role " + currentRole + " BYPASSED RLS on " + name);
            Policy policy = effectivePolicy();
            Map<String, Object> row = rows.get(id);
            if (row == null) return false;
            return passesRead(policy, row) && rows.remove(id) != null;
        }

        long rowCount() { return rows.size(); }
        List<String> auditLog() { return List.copyOf(audit); }

        // ---------- Internals ----------

        private boolean isBypass(String role) {
            return Boolean.TRUE.equals(bypassRoles.getOrDefault(role, false));
        }

        private Policy effectivePolicy() {
            return policies.get(currentRole);       // null = no policy for role
        }

        private boolean passesRead(Policy p, Map<String, Object> row) {
            return p == null || p.read().eval(row, currentTenant);
        }

        private boolean passesCheck(Policy p, Map<String, Object> row) {
            return p == null || p.withCheck() == null
                    || p.withCheck().eval(row, currentTenant);
        }

        private Map<String, Object> mask(Policy p, Map<String, Object> row) {
            if (p == null || p.masks().isEmpty()) return row;
            Map<String, Object> masked = new LinkedHashMap<>(row);
            p.masks().forEach((col, mask) -> masked.put(col, mask));
            return masked;
        }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var orders = new RlsTable("orders");

        // Policy: rows belong to the session's tenant; writes must match it.
        // The __tenant pseudo-column resolves to the session tenant at eval time.
        var policy = new Policy(
                new Predicate(Predicate.TENANT, Op.EQ, null),   // read predicate
                new Predicate(Predicate.TENANT, Op.EQ, null),   // WITH CHECK
                Map.of());
        orders.addPolicy("app", policy);
        orders.addPolicy("support", policy.withMask("amount", "****"));
        orders.setBypass("audit", true);

        // Seed data as app role
        orders.setSession("app", "T1");
        orders.insert(Map.of("id", 1L, "tenant", "T1", "amount", 100.0));
        orders.insert(Map.of("id", 2L, "tenant", "T1", "amount", 200.0));
        orders.setSession("app", "T2");
        orders.insert(Map.of("id", 3L, "tenant", "T2", "amount", 999.0));

        // Example 1: tenant isolation on read
        orders.setSession("app", "T1");
        var t1Rows = orders.select("id", 1L);
        System.out.println("T1 sees rows: " + t1Rows.size() + " (expect 2; T2's row hidden)");
        System.out.println("  T1 sees T2's id=3? " + !orders.select(null, null).isEmpty());

        // Example 2: withCheck blocks cross-tenant insert
        boolean blocked = orders.insert(Map.of("id", 9L, "tenant", "T2", "amount", 1.0));
        System.out.println("cross-tenant insert accepted? " + blocked + " (expect false)");

        // Example 3: masking for support role
        orders.setSession("support", "T1");
        var masked = orders.select(null, null);
        System.out.println("support sees amount: " + masked.getFirst().get("amount")
                + " (expect ****)");
        orders.setSession("app", "T1");
        var real = orders.select(null, null);
        System.out.println("app sees amount: " + real.getFirst().get("amount")
                + " (expect 100.0)");

        // Example 4: bypass is observable
        orders.setSession("audit", null);
        orders.select(null, null);
        System.out.println("audit log entries:");
        orders.auditLog().forEach(e -> System.out.println("  " + e));

        // Example 5: update blocked when it would escape the tenant
        orders.setSession("app", "T1");
        boolean moved = orders.update(1L, Map.of("tenant", "T2"));
        System.out.println("update moving row to T2 accepted? " + moved + " (expect false)");
        System.out.println("update within tenant: " + orders.update(1L, Map.of("amount", 150.0))
                + " (expect true)");
    }
}
```

### Step 5: Walk the Examples

**Example 1**: The `app` role's read predicate is `__tenant == session.tenant`. When the session is T1, rows 1 and 2 pass; row 3 (tenant T2) fails the predicate and is invisible — the query never sees it, regardless of what the caller's `where` clause says. Switching the session to T2 flips visibility. This is the guarantee: *the policy applies even when the query omits the tenant condition*.

**Example 2**: `insert` evaluates the `withCheck` predicate — the same `__tenant == session` rule against the *incoming row*. A row declaring `tenant="T2"` while the session is T1 fails and is rejected with an audit entry. Filtering alone would not have caught this.

**Example 3**: The `support` policy adds `masks: amount -> "****"`. The projection step replaces the amount column after filtering — the support role can count and filter on amounts but cannot see them. The `app` role (no mask) sees real values. (Display-time masking only — see the follow-up about why encryption is the real confidentiality control.)

**Example 4**: The `audit` role has `bypassRls=true`. Its select bypasses the policy *but is recorded*: the engine writes `WARNING: role audit BYPASSED RLS on orders` into the audit log — the property that makes bypass an *observed escape hatch* rather than a silent one.

**Example 5**: `update(1, tenant → T2)` — the merged row is checked: it fails `withCheck` → rejected + audit entry. An update *within* the tenant (amount change) passes and succeeds. Update of an invisible row (id=3 while session is T1) returns false — the engine never even looks at the changes.

### Step 6: Compile & Run

```bash
javac --release 21 DatabaseSecurityLab.java
java com.databases.deep.lab08.DatabaseSecurityLab
```

Expected output shape:

```
T1 sees rows: 2 (expect 2; T2's row hidden)
cross-tenant insert accepted? false (expect false)
support sees amount: **** (expect ****)
app sees amount: 100.0 (expect 100.0)
audit log entries:
  WARNING: role audit BYPASSED RLS on orders
  ...
update moving row to T2 accepted? false (expect false)
update within tenant: true (expect true)
```

> **Note on the `__tenant` predicate**: the `Predicate("__tenant", EQ, null)` uses a pseudo-column; `eval` resolves it from the session, so the `value` field is ignored for that column. A real engine (PostgreSQL) instead references `current_setting('app.tenant_id')` inside a SQL expression — the mechanics are the same, the binding is textual.

---

## Complexity Analysis

- **select**: O(R) — R rows evaluated against the predicate (plus masking O(R·M) for M masked columns). With an index on the policy column a real engine makes this O(log R + K) — the predicate pushes into the scan.
- **insert**: O(1) — check predicate on the incoming row.
- **update/delete**: O(1) — lookup by id + predicate checks.
- **Audit**: O(1) append per audited operation.
- **Space**: O(P) policies, O(R) rows, O(A) audit entries.

## Edge Cases & Failure Handling

1. **Role with no policy** — `effectivePolicy()` returns null → full visibility (matches PostgreSQL: no policy = no restriction, *unless* FORCE is set — a follow-up improvement).
2. **Null session tenant** — predicate `__tenant == null` → `Objects.equals(null, value)` fails → all rows filtered. Safe default: deny.
3. **Missing column in row** — `row.get(column)` returns null → predicate false → row hidden (deny by default, never leak).
4. **Masks on non-existent columns** — the mask writes a new key; harmless but log it in production (schema drift detection).
5. **Update to invisible row** — returns false without error: indistinguishable from "row doesn't exist" — an intentional information-hiding property (no existence oracle).
6. **Insert returning false** — caller sees failure; audit explains why.
7. **Bypass with tenant context** — bypass roles can still set session state; the audit trail is the control.

## Follow-up Questions

1. **FORCE ROW LEVEL SECURITY**: add a `forceRls` flag so even table *owners* are subject to policies (PostgreSQL semantics) — closes the "owner bypass" hole.
2. **Policy composition**: support multiple policies per role ANDed together, and policy *priority* (PostgreSQL applies all matching policies with OR for read, AND for check — model and justify which semantics you pick).
3. **Indexed evaluation**: keep a `TreeMap` on the policy column so `select` prunes by the predicate before scanning — the planner integration story.
4. **Column-level GRANTs**: layer column permissions on top — `select("id, amount")` rejects projections of columns the role lacks.
5. **Audit beyond warnings**: record `(role, operation, rowId, timestamp, policyVersion)` per DML on sensitive tables — the FGA-style trail for compliance.
6. **RLS and views**: extend the engine so `SECURITY INVOKER` view queries re-evaluate policies per view call, while `SECURITY DEFINER` views run with their owner's policy — the classic escape hatch to test against.
7. **Property test**: random role/tenant/row sequences; invariants — (a) a row with tenant X is returned only when session tenant == X, (b) every accepted insert satisfies the check, (c) bypass operations always appear in the audit log.

## References

- PostgreSQL docs: "Row Security Policies", `ALTER TABLE ... FORCE ROW LEVEL SECURITY`, `BYPASSRLS`
- Oracle docs: Virtual Private Database (VPD) and Application Contexts
- SQL Server docs: Row-Level Security (security predicates) and Dynamic Data Masking
- ISO/IEC 27001 and PCI DSS guidance on least privilege and audit logging
