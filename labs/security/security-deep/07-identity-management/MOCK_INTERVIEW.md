# Mock Interview: LDAP-like Directory with Group Membership

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Security Engineer (Identity & Access Management Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Directory semantics, DN normalization, filter evaluation, group nesting, cycles
**Problem**: Implement an in-memory LDAP-like directory: entries with DNs and attributes, a bind (password) operation, subtree search with simple filters, and groups with nested membership — cycle-safe.
**Language**: Java 21+ (records, no external libs)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. What is a DN and why does `uid=ALICE,ou=People` equal `uid=alice,ou=people`?
2. How is a subtree search scoped — base, one-level, whole-subtree?
3. What does a filter like `(&(objectClass=person)(mail=*@corp.example))` mean?
4. How do you compute group membership when groups contain groups?
5. Why do cycles happen in nested groups, and how do you survive them?
6. Follow-up: bind vs SASL, delegation, LDAP injection in filters.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We're replacing a legacy identity store with an LDAP-compatible directory so existing apps can keep binding and searching. Scope: entries, bind, search, groups."

**Candidate**: "I'll implement four operations over an in-memory tree. (1) **Entry store**: a DN → entry map with attribute multivalues; DN normalization so lookups are case-insensitive the way LDAP does it. (2) **Bind**: DN + password, with a hashed-password comparison — the directory's version of login. (3) **Search**: given a base DN, scope (base / one-level / subtree) and a simple filter — attribute equality and `(attr=value*)` prefix wildcards, plus `&` conjunction. (4) **Groups**: entries with a `member` attribute that can point at users *and* other groups — and membership resolution must expand nesting and survive cycles, because someone will always create a group that contains itself."

**Interviewer**: "What's the correctness core of each?"

**Candidate**: "DN normalization for lookups; scoped iteration for search; recursive expansion with a visited set for groups. The group expansion is the interview-grade piece: `memberOf(user)` must be a set that includes transitive group ancestors, computed with cycle detection."

### Part 2: Theory — DN Semantics (8 minutes)

**Interviewer**: "Why is DN comparison not a string compare?"

**Candidate**: "Three reasons. (1) **Attribute type is case-insensitive**: `UID=alice` ≡ `uid=alice`. (2) **Some values are case-insensitive by convention**: `dc=EXAMPLE` ≡ `dc=example`, `ou=People` ≡ `ou=people` — so a normalized form lowercases types and values. (3) **Order**: `uid=alice,ou=people,dc=corp` — the *most specific* RDN comes first; string comparison of DNs orders them wrong... 'uid=alice' vs 'dc=corp' as strings gives nonsense. And a real directory also handles `,` escaping inside values (`cn=Doe\, John`) and trimmed whitespace — I'll support the escaped comma so multi-word CNs parse. The punchline: *lookups are by normalized DN*, so `UID=alice,ou=people,dc=corp` and `uid=alice,ou=people,dc=corp` hit the same entry."

**Interviewer**: "Entry model?"

**Candidate**: "A DN plus a map from attribute name (lowercased) to a set of values — LDAP attributes are multivalued, and `objectClass` is the classic example (a person is `top`, `person`, `organizationalPerson`, `inetOrgPerson` at once). The search filter runs against this multivalue model: `(objectClass=person)` matches if the set contains `person`."

### Part 3: Theory — Search Scopes and Filters (8 minutes)

**Interviewer**: "Define the three scopes."

**Candidate**: "**base**: only the entry whose DN equals the base — used for reads of a known object. **one**: the base plus its immediate children — one level. **subtree**: base and everything below — the coarse authorization sweep. Scope selection is a cost decision: an ACL check wants base (O(1)); a 'list everyone in the org' wants subtree; a delegation boundary wants one. My iterator: walk entries whose DN is within the base subtree, then filter by scope — depth == base depth → base; depth == base depth + 1 → one; depth ≥ base depth → subtree."

**Interviewer**: "Filter grammar you'll support?"

**Candidate**: "Three clauses: equality `(attr=value)`, prefix `(attr=value*)`, and conjunction `(&(...)(...))` — disjunction `(|(...)(...))` and negation `(!(...))` noted as extensions. Evaluation is short-circuit on `&`. And an **LDAP injection** guard, the XSS of directories: a filter value coming from a request must be escaped — `(` `)` `*` `\` `\0` get backslash-escaped — or a user with a `*` in their name can rewrite the filter and read the tree. I'll add an `escapeFilterValue` helper and use it in the demo."

### Part 4: Theory — Group Membership (8 minutes)

**Interviewer**: "Groups of groups. Solve it."

**Candidate**: "Two shapes. A **posixGroup-style** entry carries `member: <user DN>` and `memberUid: alice`; an **organizationalRole/groupOfNames** entry can itself be a member of another group — that's the nesting. `effectiveMembers(group)` = fixpoint: collect members; for each member that is itself a group, recurse; track visited DNs; stop when a DN reappears — that's the cycle guard. Membership query direction: `isMember(user, group)` via `effectiveMembers`, and `groupsOf(user)` = all groups whose expansion contains the user — computed with a visited set per chain. Cost note: cache the expansion per group with an invalidation on write; the naive recompute is O(groups × users) per query."

**Interviewer**: "Why do cycles actually happen?"

**Candidate**: "Bulk importers and admin errors — 'make engineering contain all of platform' while someone else makes platform contain engineering; then a membership sweep that doesn't guard recursion stack-overflows. The `visited` set is the fix, and the demo will create a deliberate 3-group cycle to prove the resolver survives it."

### Part 5: Implementation (15 minutes)

**Interviewer**: "Code the pieces."

**Candidate**: "Records: `Dn(List<Rdn> rdns)` normalized, `Entry(Dn dn, Map<String, Set<String>> attrs)`, `BindResult`/`SearchResult`. The directory holds `Map<Dn, Entry>`. Bind: look up, constant-time compare the SHA-256 of the presented password against the stored hash. Search: scope walk + filter eval. Groups: `effectiveMembers` with a visited set."

### Part 6: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "DN normalization: `UID=ALICE,ou=People,dc=Corp` resolves. Bind: right password ok, wrong fails, unknown DN fails. Search: base on a user; one-level under `ou=groups`; subtree from `dc=corp` returns people and groups; `(&(objectClass=person)(mail=al*@corp))` returns exactly the matching set; a `*`-wildcard name proves the escape guard. Groups: direct membership, nested (2 levels), and the cycle case — a 3-group loop — returns finite results, no stack overflow. Empty subtree search on unknown base: no results, not a crash."

---

## Extended Q&A: Follow-up Round

**Q: LDAP directory vs relational database — when does each win?**

**A**: LDAP is read-heavy and hierarchical, with a schema built for identity: fast subtree searches over deep organizational trees, standardized object classes (person, organizationalPerson, posixGroup, organizationalUnit), and bind semantics that slot into Kerberos/SASL stacks. RDBMS wins for transactional writes, heterogeneous joins, and analytics. The legacy-app reality: applications speak LDAP, so the directory must exist — even when an SQL store powers it underneath, and the sync layer is the real engineering.

**Q: What is a referral, and why does search design need it?**

**A**: A referral is a DN pointing elsewhere — "ou=asia,dc=corp lives on server B". Large directories partition the tree across servers, so a proper subtree search must either chase referrals or hand them to the client to follow. This lab returns empty for unknown subtrees, which is correct in-memory but a documented design decision for distributed deployments.

**Q: What are the LDAP matching rules, exactly?**

**A**: Each attribute's schema declares a syntax and a matching rule: case-exact or case-ignore equality (uid, cn, ou are case-ignore in practice; userPassword is octet-exact), ordering rules for ranges (numeric for integers, case-ignore for strings), and binary for octets. The filter grammar in this lab implements equality and prefix matching; a full directory consults the schema so comparisons are well-defined per attribute.

**Q: What is an LDAP injection, concretely?**

**A**: An attacker sends a filter value that smuggles filter syntax: `alice)(|(objectClass=*` closes the intended clause and opens an OR that returns the whole tree. Escaping `( ) * \` and NUL in values before interpolation is the fix — the same discipline as SQL injection, with different metacharacters. The `escapeFilterValue` helper in the demo is the concrete defense.

**Q: Why are attributes multi-valued?**

**A**: objectClass is the canonical example — a person carries top, person, organizationalPerson, and inetOrgPerson simultaneously — but mail, telephoneNumber, and member are multi-valued too. LDAP is not a flat key-value store; the Set<String> per attribute in the entry record models the real data model faithfully.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| DN handling | Normalized parse, escapes, case rules | String compare | Raw lookup |
| Bind | Hashed, constant-time | Plaintext | Missing |
| Search | Scopes + conjunction + wildcard + injection guard | Equality only | None |
| Groups | Nested expansion, cycle-safe | Direct members | None |
| Tests | Normalization, cycles, injection corpus | Happy path | None |

## Red Flags
- DN lookup by raw string (case/order/escape bugs).
- Passwords stored or compared in plaintext.
- Recursive group expansion without a visited set (cycle → stack overflow).
- Filter values concatenated unescaped (LDAP injection).

## Key Takeaways
- Normalize DNs (case-insensitive types/values, RDN order, escaped commas).
- Bind = lookup + hash compare; search = scope walk + filter eval.
- Nested groups: fixpoint expansion with visited-set cycle guard.
- Escape filter values from requests; multi-valued attributes everywhere.

## Glossary

- **DN / RDN** — distinguished name / relative distinguished name; `uid=alice,ou=people,dc=corp`.
- **Normalization** — the canonical form of a DN: lowercased types, case rules, unescaped values.
- **Attribute** — a name with a set of values; LDAP is multi-valued by design.
- **objectClass** — the attribute declaring an entry's type (person, posixGroup, organizationalUnit).
- **Bind** — the directory's authentication operation: DN + password against a stored hash.
- **Scope** — base (self), one (children), subtree (everything below).
- **Filter** — the search predicate: equality, prefix wildcard, `(&...)` conjunction.
- **Referral** — a pointer to another server holding part of the tree.
- **Matching rule** — per-attribute comparison semantics (case-ignore, case-exact, binary).
- **posixGroup** — the object class for groups with `member` attributes.
- **LDAP injection** — smuggling filter syntax through unescaped values.
- **SASL** — the framework for strong binds (GSSAPI, SCRAM) beyond plaintext passwords.
- **uid / cn / dc / ou** — common attribute names: user id, common name, domain component, organizational unit.
- **memberOf** — the virtual attribute listing a user's groups, computed from group memberships.
- **Changelog** — the change record a directory publishes for replication and audit.
- **LDIF** — LDAP Data Interchange Format: the standard text representation of entries.
- **Subschema** — the subentry defining the directory's attribute syntaxes and matching rules.
- **Entry vs subentry** — operational entries (schema, changelog) that are not user data.
- **Group of names** — the groupOfNames object class: a membership list via `member` attributes.
