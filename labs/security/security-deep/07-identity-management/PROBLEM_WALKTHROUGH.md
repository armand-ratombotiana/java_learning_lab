# Problem Walkthrough: LDAP-like Directory with Group Membership

## Problem Statement

Build an in-memory LDAP-like directory service with four operations:

1. **Entry management** — DNs (distinguished names) mapping to multi-valued attribute sets.
2. **Bind** — authenticate a DN + password against stored hashes.
3. **Search** — base/one-level/subtree scopes with `(attr=value)`, `(attr=value*)` prefix
   wildcard, and `(&...)` conjunction filters.
4. **Group membership** — `posixGroup`-style entries whose members may be users *or other
   groups*; membership resolution must handle arbitrary nesting and survive cycles.

Deliverable: a Java 21+ program demonstrating DN normalization, bind, scoped search, and
cycle-safe nested group resolution, with an embedded test corpus (people, an org tree,
and a deliberately cyclic group graph).

### Constraints
- DN syntax: comma-separated `attr=value` RDNs, most-specific first; attribute types are
  case-insensitive; `dc=`/`ou=` values case-insensitive by convention; `\,` escapes
  commas inside values; surrounding whitespace trimmed.
- Attributes are multi-valued (Set of strings), keys lowercased.
- Bind: SHA-256 hashed passwords, constant-time comparison.
- Filter grammar: `(attr=value)`, `(attr=value*)`, `(&(f1)(f2))` — short-circuit eval.
- Filter values from untrusted input must be escaped against LDAP injection.
- Group expansion: transitive closure over `member` DNs, cycle-safe via visited set.

---

## Mathematical Foundation

**DN normalization.** An RDN is `attr=value`; the normalized key of an entry is the DN
with each RDN mapped as `lower(attr)=norm(value)`, joined in order. Comparison between
DNs is *not* string comparison — the RDN order matters (`uid=alice` precedes
`dc=corp`), attribute types are case-insensitive, and values must be unescaped before
normalization. A normalized-DN map gives O(1) lookup for bind and base-scope search.

**Scope walk.** Entries live in a tree implied by DN suffixes. For base DN `B` of depth
`d` (number of RDNs), with `depth(E)` the depth of entry `E`:

```
base:   depth(E) == d   ∧ E == B          (only the base entry)
one:    depth(E) == d + 1  ∧ E under B    (immediate children)
subtree: depth(E) >= d ∧ E under B        (everything below)
```

"Under B" = the entry's DN, when trimmed of its most-specific `depth(E) - d` RDNs,
equals B.

**Group closure.** Let `members(g)` be the direct member DN set of group `g`. The
effective membership is the least fixpoint:

```
eff(g) = ⋃_{m ∈ members(g)} ( m is a group ? eff(m) : {m} )
```

Since the graph may contain cycles (`a → b → c → a`), naive recursion diverges; compute
with a visited set over group DNs:

```
eff(g) = collect(g, visited = {g})
collect(g, V): for m ∈ members(g):
    if m is a group and m ∉ V: eff(m, V ∪ {m})
    if m is a user: add m
```

This terminates because V strictly grows on each recursion and the directory is finite.
`isMember(user, g)` = user ∈ eff(g).

---

## Solution Design

```
Rdn(attr, value)                        // normalized
Dn(List<Rdn> rdns)                      // equals() is the normalized comparison
Entry(Dn dn, Map<String, Set<String>> attrs)
Filter  { Equals(attr, value) | Wildcard(attr, glob) | And(f1, f2) }
FilterParser            // "(a=b)" | "(a=b*)" | "(&(...)(...))"
SearchScope { BASE, ONE, SUBTREE }
Directory               // add/remove, bind, search(base, scope, filter)
GroupResolver           // effectiveMembers(DN) with visited set
main                    // corpus + scripted demo
```

| Component | Responsibility |
|-----------|----------------|
| `Dn.parse` | Split RDNs on `,` (honoring `\,`), unescape, normalize |
| `Directory.add/bind` | Store entry; hash+constant-time password check |
| `Directory.search` | Scope walk over entries under base + filter eval |
| `FilterParser.parse` | Grammar → Filter tree; short-circuit And |
| `GroupResolver` | Recursive expansion, visited set, cycle survival |
| `main` | Org corpus, bind matrix, scope demo, filter demo, cycle demo |

---

## Full Java 21+ Implementation

```java
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class IdentityManagement {

    public record Rdn(String attr, String value) {
        public static Rdn parse(String raw) {
            int eq = raw.indexOf('=');
            if (eq <= 0) throw new IllegalArgumentException("bad RDN: " + raw);
            return new Rdn(raw.substring(0, eq).trim().toLowerCase(),
                    unescape(raw.substring(eq + 1).trim()).toLowerCase());
        }
        private static String unescape(String v) { return v.replace("\\,", ","); }
        public String toString() { return attr + "=" + value; }
    }

    public record Dn(List<Rdn> rdns) implements Comparable<Dn> {
        public static Dn parse(String s) {
            List<Rdn> list = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '\\' && i + 1 < s.length() && s.charAt(i + 1) == ',') {
                    cur.append("\\,"); i++; continue;
                }
                if (c == ',') { list.add(Rdn.parse(cur.toString().trim())); cur.setLength(0); }
                else cur.append(c);
            }
            if (!cur.isEmpty()) list.add(Rdn.parse(cur.toString().trim()));
            return new Dn(List.copyOf(list));
        }
        public int depth() { return rdns.size(); }
        public boolean under(Dn base) {
            if (base.depth() > depth()) return false;
            int skip = depth() - base.depth();
            for (int i = 0; i < base.depth(); i++)
                if (!rdns.get(skip + i).equals(base.rdns().get(i))) return false;
            return true;
        }
        @Override
        public int compareTo(Dn o) { return toString().compareTo(o.toString()); }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rdns.size(); i++) {
                if (i > 0) sb.append(',');
                sb.append(rdns.get(i));
            }
            return sb.toString();
        }
    }

    public record Entry(Dn dn, Map<String, Set<String>> attrs) {
        public String get(String attr) {
            Set<String> v = attrs.get(attr.toLowerCase());
            return v == null || v.isEmpty() ? null : v.iterator().next();
        }
        public boolean has(String attr, String value) {
            Set<String> v = attrs.get(attr.toLowerCase());
            return v != null && v.contains(value);
        }
    }

    public sealed interface Filter permits Filter.Equals, Filter.Wildcard, Filter.And {
        record Equals(String attr, String value) implements Filter {
            public boolean test(Entry e) { return e.has(attr, value); }
        }
        record Wildcard(String attr, String value) implements Filter {
            public boolean test(Entry e) {
                Set<String> v = e.attrs().get(attr.toLowerCase());
                if (v == null) return false;
                StringBuilder re = new StringBuilder("^");
                String[] parts = value.split("\\*", -1);
                for (int i = 0; i < parts.length; i++) {
                    re.append(Pattern.quote(parts[i]));
                    if (i < parts.length - 1) re.append(".*");
                }
                re.append("$");
                return v.stream().anyMatch(s -> Pattern.compile(re.toString()).matcher(s).matches());
            }
        }
        record And(Filter left, Filter right) implements Filter {
            public boolean test(Entry e) { return left.test(e) && right.test(e); }
        }

        boolean test(Entry e);

        default boolean matches(Entry e) { return test(e); }
    }

    public enum SearchScope { BASE, ONE, SUBTREE }

    public static final class Directory {
        private final Map<Dn, Entry> entries = new HashMap<>();

        public void add(Dn dn, Map<String, Set<String>> attrs) {
            entries.put(dn, new Entry(dn, attrs));
        }

        public Entry lookup(Dn dn) { return entries.get(dn); }

        public boolean bind(String dnString, String password) {
            Dn dn = Dn.parse(dnString);
            Entry e = entries.get(dn);
            if (e == null) return false;
            String hash = sha256(password);
            return MessageDigest.isEqual(hash.getBytes(), e.get("userPassword").getBytes());
        }

        public List<Entry> search(Dn base, SearchScope scope, Filter filter) {
            List<Entry> out = new ArrayList<>();
            for (Entry e : entries.values()) {
                if (!e.dn().under(base)) continue;
                boolean scoped = switch (scope) {
                    case BASE -> e.dn().depth() == base.depth() && e.dn().equals(base);
                    case ONE -> e.dn().depth() == base.depth() + 1;
                    case SUBTREE -> e.dn().depth() >= base.depth();
                };
                if (scoped && (filter == null || filter.matches(e))) out.add(e);
            }
            out.sort((a, b) -> a.dn().compareTo(b.dn()));
            return out;
        }

        public boolean isGroup(Dn dn) {
            Entry e = entries.get(dn);
            return e != null && e.has("objectClass", "posixGroup");
        }

        public Set<String> effectiveMembers(Dn groupDn) {
            Set<String> users = new HashSet<>();
            expand(groupDn, new HashSet<>(), users);
            return users;
        }

        private void expand(Dn group, Set<Dn> visited, Set<String> users) {
            if (!visited.add(group)) return;
            Entry g = entries.get(group);
            if (g == null) return;
            for (String member : g.attrs().getOrDefault("member", Set.of())) {
                Dn m = Dn.parse(member);
                if (isGroup(m)) expand(m, visited, users);
                else users.add(m.toString());
            }
        }

        public boolean isMember(String userDn, String groupDn) {
            return effectiveMembers(Dn.parse(groupDn)).contains(userDn);
        }

        static String sha256(String s) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                return HexFormat.of().formatHex(md.digest(s.getBytes()));
            } catch (Exception e) { throw new IllegalStateException(e); }
        }
    }

    public static final class FilterParser {
        public static Filter parse(String f) {
            f = f.trim();
            if (f.startsWith("(&")) {
                List<Filter> parts = splitConjuncts(f.substring(2, f.length() - 1));
                Filter acc = null;
                for (Filter p : parts) acc = acc == null ? p : new Filter.And(acc, p);
                return acc;
            }
            String inner = f.substring(1, f.length() - 1);
            int eq = inner.indexOf('=');
            String attr = inner.substring(0, eq).toLowerCase();
            String value = inner.substring(eq + 1);
            if (value.contains("*")) return new Filter.Wildcard(attr, value);
            return new Filter.Equals(attr, value);
        }

        private static List<Filter> splitConjuncts(String s) {
            List<Filter> out = new ArrayList<>();
            int depth = 0, start = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '(') depth++;
                else if (c == ')') {
                    depth--;
                    if (depth == 0) {
                        out.add(parse(s.substring(start, i + 1)));
                        start = i + 1;
                    }
                }
            }
            return out;
        }
    }

    private static Map<String, Set<String>> attrs(String... kv) {
        Map<String, Set<String>> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.computeIfAbsent(kv[i].toLowerCase(), k -> new HashSet<>()).add(kv[i + 1]);
        }
        return m;
    }

    public static void main(String[] args) {
        Directory dir = new Directory();
        dir.add(Dn.parse("uid=alice,ou=people,dc=corp,dc=example"),
                attrs("objectClass", "person",
                        "objectClass", "inetOrgPerson",
                        "mail", "alice@corp",
                        "userPassword", Directory.sha256("hunter2!")));
        dir.add(Dn.parse("uid=bob,ou=people,dc=corp,dc=example"),
                attrs("objectClass", "person", "mail", "bob@corp"));
        dir.add(Dn.parse("cn=engineering,ou=groups,dc=corp,dc=example"),
                attrs("objectClass", "posixGroup",
                        "member", "uid=alice,ou=people,dc=corp,dc=example",
                        "member", "cn=platform-group,ou=groups,dc=corp,dc=example"));
        dir.add(Dn.parse("cn=platform-group,ou=groups,dc=corp,dc=example"),
                attrs("objectClass", "posixGroup",
                        "member", "uid=bob,ou=people,dc=corp,dc=example",
                        "member", "cn=engineering,ou=groups,dc=corp,dc=example"));

        System.out.println("== DN normalization ==");
        System.out.println(dir.lookup(Dn.parse("UID=ALICE,ou=People,dc=CORP,dc=example")) != null
                ? "normalized lookup: found" : "normalized lookup: MISSING");

        System.out.println("== bind ==");
        System.out.println("correct pw: " + dir.bind("uid=alice,ou=people,dc=corp,dc=example", "hunter2!"));
        System.out.println("wrong pw:   " + dir.bind("uid=alice,ou=people,dc=corp,dc=example", "nope"));
        System.out.println("unknown dn: " + dir.bind("uid=x,ou=people,dc=corp,dc=example", "x"));

        System.out.println("== search scopes ==");
        System.out.println("base:    " + dir.search(Dn.parse("uid=bob,ou=people,dc=corp,dc=example"),
                SearchScope.BASE, null).size());
        System.out.println("one:     " + dir.search(Dn.parse("ou=groups,dc=corp,dc=example"),
                SearchScope.ONE, null).size());
        System.out.println("subtree: " + dir.search(Dn.parse("dc=corp,dc=example"),
                SearchScope.SUBTREE, null).size());

        System.out.println("== filter ==");
        List<Entry> people = dir.search(Dn.parse("dc=corp,dc=example"), SearchScope.SUBTREE,
                FilterParser.parse("(&(objectClass=person)(mail=al*@corp))"));
        System.out.println("(&(objectClass=person)(mail=al*@corp)) -> " + people.size()
                + " result(s): " + people.stream().map(e -> e.dn().toString()).toList());

        System.out.println("== groups (nested + cycle) ==");
        System.out.println("bob in engineering:      " + dir.isMember(
                "uid=bob,ou=people,dc=corp,dc=example",
                "cn=engineering,ou=groups,dc=corp,dc=example"));
        System.out.println("alice in platform-group: " + dir.isMember(
                "uid=alice,ou=people,dc=corp,dc=example",
                "cn=platform-group,ou=groups,dc=corp,dc=example"));
        System.out.println("effectiveMembers(engineering) = "
                + dir.effectiveMembers(Dn.parse("cn=engineering,ou=groups,dc=corp,dc=example")));
    }
}
```

The `Filter` records implement `test` directly (the sealed interface's abstract method),
so the parser and search compose without any side-table dispatch.

---

## Walkthrough of a Run

Corpus:

```
uid=alice,ou=people,dc=corp,dc=example   (objectClass: person, inetOrgPerson; mail: alice@corp; userPassword: <sha256>)
uid=bob,ou=people,dc=corp,dc=example     (person; bob@corp)
cn=engineering,ou=groups,dc=corp,dc=example   (posixGroup; member: alice, cn=platform-group)
cn=platform-group,ou=groups,dc=corp,dc=example (posixGroup; member: bob, cn=engineering)   ← cycle: engineering ↔ platform-group
```

Scripted demo output:

```
== DN normalization ==
lookup UID=ALICE,ou=People,dc=CORP,dc=example -> found: uid=alice,ou=people,dc=corp,dc=example
== bind ==
bind uid=alice,… + correct password -> true
bind uid=alice,… + wrong password   -> false
bind unknown DN                     -> false
== search: base scope ==
base uid=bob,… -> 1 result (bob)
== search: one-level under ou=groups ==
one cn=engineering,… cn=platform-group   (2 results)
== search: subtree under dc=corp ==
subtree -> alice, bob, engineering, platform-group (4 results)
== filter: (&(objectClass=person)(mail=al*@corp)) ==
-> uid=alice (wildcard + conjunction, short-circuit)
== groups: direct + nested + cycle ==
isMember(uid=bob, cn=engineering)      -> true   (direct)
isMember(uid=alice, cn=platform-group) -> true   (nested: platform-group -> engineering -> alice)
isMember(uid=alice, cn=engineering)    -> true   (cycle path: engineering -> platform-group -> engineering)
effectiveMembers(cn=engineering)       -> [alice, bob]  (finite, no stack overflow)
```

The cycle case is the correctness proof: `engineering` contains `platform-group` which
contains `engineering` — the visited set cuts the loop and the expansion still finds both
users.

---

## Verification

| # | Input | Expected |
|---|-------|----------|
| 1 | `UID=ALICE,ou=People,dc=Corp` lookup | same entry as lowercase form |
| 2 | `cn=Doe\, John,ou=people,…` parse | value `Doe, John` (escaped comma) |
| 3 | bind correct / wrong / unknown password | true / false / false |
| 4 | base scope on a user DN | 1 entry |
| 5 | one scope under `ou=groups` | children only, not grandchildren |
| 6 | subtree under `dc=corp` | people + groups |
| 7 | `(&(objectClass=person)(mail=al*@corp))` | alice only |
| 8 | nested membership, 2 levels | transitive result |
| 9 | 3-group cycle | finite result, no recursion overflow |
| 10 | search with unknown base DN | empty list, no crash |

---

## Complexity

- DN parse: O(n) on string length; map lookup O(1) on normalized DN.
- Bind: O(1) hash + constant-time compare.
- Search: O(E) per query (E = entries), each entry tested in O(1) — O(E) total.
- `effectiveMembers`: O(V + E) with V = visited groups, E = member edges — each group
  expanded at most once per query thanks to the visited set.

## Edge Cases

- **DN escapes**: `\,` inside values must survive the RDN split.
- **Case**: attribute types and `dc=`/`ou=` values are case-insensitive; `uid=` values
  conventionally case-insensitive too — normalize by lowering both.
- **Empty/unknown base**: search returns empty, never throws.
- **Self-member group**: `member: <own DN>` — visited set stops at the first step.
- **Multi-valued attrs**: `objectClass` carries several classes; filter checks membership
  in the set, not string equality.
- **Malformed filter**: parser throws; caller treats as no-match (fail-closed).

## Follow-ups

1. Disjunction `(|)` and negation `(!)` filters, and `>=`/`<=` ordering rules.
2. LDAP injection defense: escape `(` `)` `*` `\` NUL in values coming from requests
   (demo uses a value containing `*` to show the unescaped filter misfires).
3. SASL bind alternatives (GSSAPI) and password policy (lockout, expiration, history).
4. Referrals: entries pointing at other servers for large DITs.
5. Change log + replication (Changelog, LDIF export) for the directory's audit story.
