# Security LeetCode — Pattern Cheatsheet

> Security-related coding patterns and problems for interview preparation.
> Covers cryptography, authentication, OWASP Top 10, and secure coding patterns in Java.

---

## Table of Contents

1. [Cryptography Patterns](#cryptography-patterns)
2. [Authentication Patterns](#authentication-patterns)
3. [OWASP Top 10 Coding Problems](#owasp-coding)
4. [Secure Coding Patterns in Java](#secure-coding-java)
5. [Common Security Algorithms](#security-algorithms)
6. [Coding Interview Templates](#coding-templates)

---

## Cryptography Patterns

### RSA

#### Key Generation (Java)

```java
import java.security.*;

KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
gen.initialize(2048);
KeyPair pair = gen.generateKeyPair();
PublicKey pub = pair.getPublic();
PrivateKey priv = pair.getPrivate();
```

#### Encrypt/Decrypt

```java
Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
cipher.init(Cipher.ENCRYPT_MODE, pubKey);
byte[] encrypted = cipher.doFinal(plaintext);

cipher.init(Cipher.DECRYPT_MODE, privKey);
byte[] decrypted = cipher.doFinal(encrypted);
```

#### Sign/Verify

```java
Signature sig = Signature.getInstance("SHA256withRSA");
sig.initSign(privKey);
sig.update(data);
byte[] signature = sig.sign();

sig.initVerify(pubKey);
sig.update(data);
boolean valid = sig.verify(signature);
```

### AES (Symmetric)

```java
// Generate key
KeyGenerator gen = KeyGenerator.getInstance("AES");
gen.init(256);
SecretKey key = gen.generateKey();

// Generate IV
byte[] iv = new byte[16];
SecureRandom.getInstanceStrong().nextBytes(iv);
IvParameterSpec ivSpec = new IvParameterSpec(iv);

// Encrypt
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
byte[] ciphertext = cipher.doFinal(plaintext);

// Decrypt
cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
byte[] decrypted = cipher.doFinal(ciphertext);
```

### Hashing

```java
// SHA-256
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] hash = md.digest(input);

// BCrypt
String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));

// Argon2 (preferred for passwords)
Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
    16, 32, 1, 60000, 10);
String hash = encoder.encode(password);
```

### HMAC

```java
Mac mac = Mac.getInstance("HmacSHA256");
SecretKeySpec key = new SecretKeySpec(
    secretKey.getBytes(), "HmacSHA256");
mac.init(key);
byte[] result = mac.doFinal(data.getBytes());
```

### Common Interview Problems

| Problem | Pattern | Complexity |
|---------|---------|------------|
| **RSA Encryption** | Generate keys, encrypt/decrypt | O(n) |
| **AES-GCM** | Symmetric encryption with auth tag | O(n) |
| **Password Hashing** | BCrypt/Argon2 comparison | O(2^k) work factor |
| **Digital Signature** | Sign and verify integrity | O(n) |
| **HMAC** | Keyed-hash message authentication | O(n) |
| **Key Derivation** | PBKDF2/scrypt/Argon2 | O(iterations) |
| **Certificate Chain Verification** | Validate trust chain | O(depth) |
| **JWT Sign & Verify** | Base64url + HMAC/RSA | O(n) |

---

## Authentication Patterns

### JWT Implementation

```java
// Generate JWT
String createJwt(String userId, String secret, long expiryMs) {
    return Jwts.builder()
        .subject(userId)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiryMs))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
}

// Validate JWT
Claims validateJwt(String token, String secret) {
    return Jwts.parser()
        .setSigningKey(secret)
        .build()
        .parseClaimsJws(token)
        .getBody();
}
```

### Session Management

```java
// Secure session token generation
String generateSessionToken() {
    byte[] bytes = new byte[32];
    SecureRandom.getInstanceStrong().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
}

// Session storage (in-memory for demo; use Redis in production)
public class Session {
    String token;
    String userId;
    Instant createdAt;
    Instant expiresAt;
    Map<String, Object> attributes;
}
```

### Rate Limiting

```java
// Token bucket algorithm
class TokenBucket {
    final long capacity;
    final long refillRate;  // tokens per second
    long tokens;
    long lastRefillTime;

    boolean tryConsume() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        long newTokens = elapsed / 1000 * refillRate;
        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTime = now;
    }
}
```

### Common Interview Problems

| Problem | Pattern | Complexity |
|---------|---------|------------|
| **JWT Verify** | Parse, validate signature, check expiry | O(n) |
| **Password Validation** | Min length, complexity, common pw check | O(n) |
| **Rate Limiter** | Token bucket / Sliding window / Fixed window | O(1) |
| **Session Store** | CRUD operations, TTL cleanup | O(1) avg |
| **OTP Generation** | TOTP (RFC 6238) | O(1) |
| **API Key Validation** | Hash comparison, key prefix lookup | O(1) |
| **Auth Middleware** | Header extraction, token validation chain | O(n) |

---

## OWASP Top 10 Coding Problems

### SQL Injection Prevention

```java
// BAD — vulnerable
String query = "SELECT * FROM users WHERE name = '" + input + "'";

// GOOD — parameterized
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE name = ?");
stmt.setString(1, input);
ResultSet rs = stmt.executeQuery();
```

**Interview Problem**: SQL injection detection via pattern matching

```java
boolean hasSqlInjection(String input) {
    String[] patterns = {
        "'\\s*OR\\s*'\\s*'\\s*=\\s*'",
        "';\\s*DROP\\s+TABLE",
        "';\\s*(SELECT|INSERT|UPDATE|DELETE|UNION)",
        "--",
        "\\bOR\\b.*\\b=\\b.*\\bOR\\b",
        "'\\s*OR\\s*1\\s*=\\s*1"
    };
    for (String p : patterns) {
        if (input.toUpperCase().matches(".*" + p.toUpperCase() + ".*")) {
            return true;
        }
    }
    return false;
}
```

### XSS Prevention

```java
public String sanitizeHtml(String input) {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("/", "&#x2F;");
}
```

### Path Traversal Prevention

```java
boolean isValidPath(String filename, String baseDir) {
    Path filePath = Paths.get(baseDir, filename).normalize();
    Path basePath = Paths.get(baseDir).normalize();
    return filePath.startsWith(basePath);
}
```

### CSRF Token

```java
public class CsrfToken {
    String token;
    Instant expiresAt;

    static CsrfToken generate() {
        byte[] bytes = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(bytes);
        String token = Base64.getUrlEncoder()
            .withoutPadding().encodeToString(bytes);
        return new CsrfToken(token,
            Instant.now().plus(30, ChronoUnit.MINUTES));
    }

    static boolean validate(String token, String expected) {
        return MessageDigest.isEqual(
            token.getBytes(StandardCharsets.UTF_8),
            expected.getBytes(StandardCharsets.UTF_8));
    }
}
```

### Secure Deserialization

```java
// Validate class allowlist before deserialization
class SecureObjectInputStream extends ObjectInputStream {
    private static final Set<String> ALLOWED = Set.of(
        "com.example.User", "java.util.ArrayList");

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {
        if (!ALLOWED.contains(desc.getName())) {
            throw new InvalidClassException(
                "Unauthorized deserialization", desc.getName());
        }
        return super.resolveClass(desc);
    }
}
```

### Access Control Check

```java
boolean checkAccess(User user, String resource, String action) {
    // Check authentication
    if (user == null) return false;

    // Check role-based access
    if (user.hasRole("ADMIN")) return true;

    // Check resource ownership
    if (action.equals("READ") && user.canRead(resource)) return true;
    if (action.equals("WRITE") && user.canWrite(resource)) return true;

    // Check attribute-based conditions
    return evaluatePolicy(user, resource, action);
}

record Policy(String resource, String action, String condition) {}
```

### Input Validation Framework

```java
class InputValidator {
    interface Rule { boolean validate(String input); }

    static Rule notEmpty() {
        return input -> input != null && !input.trim().isEmpty();
    }

    static Rule minLength(int min) {
        return input -> input.length() >= min;
    }

    static Rule matches(String regex) {
        return input -> input.matches(regex);
    }

    static Rule containsNoSql() {
        return input -> !input.toUpperCase()
            .matches(".*('|--|\\bUNION\\b|\\bDROP\\b).*");
    }

    static boolean validate(String input, List<Rule> rules) {
        return rules.stream().allMatch(r -> r.validate(input));
    }
}
```

### Secure Logger (No sensitive data)

```java
class SecureLogger {
    private static final Pattern SENSITIVE = Pattern.compile(
        "(password|secret|token|key|credential)=[^&\\s]+");

    static String sanitize(String message) {
        return SENSITIVE.matcher(message)
            .replaceAll("$1=***");
    }

    void info(String message, Object... params) {
        String safe = sanitize(String.format(message, params));
        // Log safe string only
    }
}
```

---

## Common Security Algorithms

### String Matching — Rabin-Karp (Pattern Detection)

```java
// Useful for detecting known malicious patterns in strings
public List<Integer> search(String text, String pattern) {
    int m = pattern.length(), n = text.length();
    int d = 256, q = 101;
    int h = 1, p = 0, t = 0;
    List<Integer> matches = new ArrayList<>();

    for (int i = 0; i < m - 1; i++)
        h = (h * d) % q;

    for (int i = 0; i < m; i++) {
        p = (d * p + pattern.charAt(i)) % q;
        t = (d * t + text.charAt(i)) % q;
    }

    for (int i = 0; i <= n - m; i++) {
        if (p == t) {
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j))
                j++;
            if (j == m) matches.add(i);
        }
        if (i < n - m) {
            t = (d * (t - text.charAt(i) * h) + text.charAt(i + m)) % q;
            if (t < 0) t += q;
        }
    }
    return matches;
}
```

### Trie — URL/Path Blocklist

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isBlocked;
}

class URLFilter {
    TrieNode root = new TrieNode();

    void addBlocked(String url) {
        TrieNode node = root;
        for (char c : url.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isBlocked = true;
    }

    boolean isBlocked(String url) {
        TrieNode node = root;
        for (char c : url.toCharArray()) {
            if (!node.children.containsKey(c)) return false;
            node = node.children.get(c);
            if (node.isBlocked) return true;
        }
        return node.isBlocked;
    }
}
```

### Bloom Filter — Blocklist (Memory Efficient)

```java
class BloomFilter {
    BitSet bitset;
    int size, hashCount;

    BloomFilter(int size, int hashCount) {
        this.size = size;
        this.hashCount = hashCount;
        this.bitset = new BitSet(size);
    }

    void add(String item) {
        for (int i = 0; i < hashCount; i++) {
            bitset.set(hash(item, i) % size);
        }
    }

    boolean mightContain(String item) {
        for (int i = 0; i < hashCount; i++) {
            if (!bitset.get(hash(item, i) % size))
                return false;
        }
        return true;
    }

    int hash(String item, int seed) {
        return (item.hashCode() ^ seed * 0x9E3779B97F4A7C15L) & 0x7FFFFFFF;
    }
}
```

### Levenshtein Distance — Password Similarity

```java
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1))
                dp[i][j] = dp[i - 1][j - 1];
            else
                dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                    Math.min(dp[i - 1][j], dp[i][j - 1]));
        }
    }
    return dp[m][n];
}
// Use: Reject passwords with edit distance < 3 from previous password
```

### LRU Cache — Session Cache

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

---

## Coding Interview Templates

### Security Code Review Template

```
// 1. Input Validation
//    - Check for SQL injection, XSS, path traversal
//    - Validate length, type, format

// 2. Authentication
//    - Is auth enforced? Can it be bypassed?
//    - Token validation? Session management?

// 3. Authorization
//    - Are access controls checked per operation?
//    - Vertical/horizontal privilege escalation?

// 4. Data Protection
//    - Sensitive data in logs, errors, responses?
//    - Encryption at rest? In transit?

// 5. Configuration
//    - Default credentials? Debug mode enabled?
//    - Secure defaults? Least privilege?
```

### Threat Modeling Template

```
// STRIDE per component:
// S — Spoofing: Authentication checks?
// T — Tampering: Integrity protections?
// R — Repudiation: Audit logging?
// I — Info Disclosure: Encryption, access control?
// D — DoS: Rate limiting, quotas?
// E — Elevation: Privilege separation, boundary checks?
```

### Trade-off Decision Template

```
// Option A: (+) Benefit 1, (+) Benefit 2
//           (-) Cost 1, (-) Risk 1
//
// Option B: (+) Benefit 3, (+) Benefit 4
//           (-) Cost 2, (-) Risk 2
//
// Recommendation: Option A because [reasoning],
//   mitigated by [compensating control]
//
// Decision criteria:
//   1. Security impact (severity x likelihood)
//   2. Implementation complexity
//   3. Operational overhead
//   4. User experience impact
```

---

## LeetCode Problem Map

### Easy (Security Context)

| Problem | Security Use Case |
|---------|------------------|
| 20. Valid Parentheses | Protocol parsing validation |
| 125. Valid Palindrome | Data normalization for comparison |
| 242. Valid Anagram | Hash comparison (integrity check) |
| 344. Reverse String | Base64 encoding concepts |
| 387. First Unique Character | Anomaly detection |

### Medium

| Problem | Security Use Case |
|---------|------------------|
| 3. Longest Substring without Repeating | Session token uniqueness |
| 5. Longest Palindromic Substring | Pattern matching concepts |
| 49. Group Anagrams | Hash-based classification |
| 139. Word Break | Malicious string parsing |
| 146. LRU Cache | Session cache management |
| 200. Number of Islands | Network segmentation |
| 208. Implement Trie | URL/domain filtering |
| 340. Longest Substring with K Distinct | Traffic classification |
| 355. Design Twitter | Access control, feed filtering |
| 380. Insert Delete GetRandom O(1) | Session/Token store |

### Hard

| Problem | Security Use Case |
|---------|------------------|
| 10. Regular Expression Matching | WAF rule matching |
| 76. Minimum Window Substring | Data leak detection |
| 211. Design Add and Search Words | Threat detection patterns |
| 212. Word Search II | Pattern matching at scale |
| 295. Find Median from Data Stream | Anomaly detection |
| 336. Palindrome Pairs | Cryptography concepts |
| 460. LFU Cache | Rate limiting cache |
| 588. Design In-Memory File System | File permission system |

---

## Secure Coding Patterns — Java

### Always Use

```java
// 1. PreparedStatement for SQL
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
ps.setLong(1, userId);

// 2. SecureRandom for crypto
byte[] key = new byte[32];
SecureRandom.getInstanceStrong().nextBytes(key);

// 3. Constant-time comparison
MessageDigest.isEqual(a, b);

// 4. Thread-safe Atomic operations
AtomicLong requestCount = new AtomicLong(0);
requestCount.incrementAndGet();

// 5. Immutable data for sensitive values
record Credential(String username, char[] password) {}

// 6. Zero out sensitive data
Arrays.fill(password, ' ');
```

### Never Do

```java
// 1. Don't log sensitive data
log.info("User password: {}", password);  // NO

// 2. Don't concatenate SQL
String sql = "SELECT * FROM users WHERE id = " + userId;  // NO

// 3. Don't use predictable random
Random rand = new Random();  // NO — use SecureRandom

// 4. Don't compare strings for tokens
if (token.equals(incoming))  // NO — use MessageDigest.isEqual

// 5. Don't expose stack traces
response.getWriter().print(e.toString());  // NO

// 6. Don't disable security
SSLSocketFactory.setDefault(null);  // NO
```

---

*Last updated: July 2026*
