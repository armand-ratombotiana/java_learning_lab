# Mock Interview: AES-128 CBC with PKCS7 Padding

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Application Security Engineer (Crypto Engineering Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Symmetric cryptography, modes of operation, padding, real-world crypto failures
**Problem**: Implement AES-128 encryption in CBC mode with PKCS7 padding — including the block cipher core, key schedule, mode chaining, and padding — with verification against an independent oracle (JCE) and a published test vector.
**Language**: Java 21+ (JCE available for verification; the implementation itself is from scratch)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Walk through the AES round: SubBytes, ShiftRows, MixColumns, AddRoundKey. What is each step's purpose?
2. Why must CBC use a random IV, and why must it never be reused?
3. What is the padding-oracle attack, and why does CBC with PKCS7 enable it?
4. Why does a bit-flip in ciphertext block i corrupt blocks i and i+1 — which parts?
5. Why is encryption without authentication (CBC + no MAC) dangerous? What does GCM add?
6. Follow-up: key schedule structure, side channels, and when you must NOT hand-roll AES.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We store encrypted blobs for a document service and the security team wants an in-house, dependency-free implementation of AES-128 in CBC mode with PKCS7 padding — with tests proving it's correct. Clarify before you start."

**Candidate**: "Three questions. First, scope of 'implement AES': do you want the full block cipher from scratch — S-box, key schedule, the rounds — or a wrapper around JCE with the CBC/PKCS7 logic hand-written? I'll assume from scratch, since that's the interesting part and you said dependency-free. Second, verification oracle: I want to compare against JCE's AES/CBC/PKCS5Padding on random inputs plus the FIPS-197 Appendix B vector — if I match both, the implementation is demonstrably correct. Third: do you want a decrypt path too — it doubles the surface for bugs (InvMixColumns, inverse S-box) — and I assume yes, since encryption-only is useless."

**Interviewer**: "Full stack, both directions. The JCE comparison is the acceptance test."

**Candidate**: "Then the architecture is: AES core (encryptBlock/decryptBlock), key expansion to 11 round keys, CBC chaining on top, and PKCS7 pad/unpad with strict validation. I'll also demonstrate the mode-level properties — IV chaining, block-wise diffusion — since those are the security properties the team actually relies on."

### Part 2: Theory — The AES Round (10 minutes)

**Interviewer**: "Walk me through the four steps of an AES round and why each exists."

**Candidate**: "The state is a 4×4 column-major byte matrix. Each round applies four transformations. SubBytes: a fixed nonlinear byte substitution (the S-box, derived from the multiplicative inverse in GF(2⁸) plus an affine transformation) — this is the *only* nonlinear step; without it, the whole cipher would be a linear map and trivially breakable. ShiftRows: cyclically shifts row r left by r — it *diffuses* bytes across columns. MixColumns: each column is multiplied by the MDS matrix (2, 3, 1, 1) — it *mixes* bytes within a column, so a 1-byte change in a column spreads to all 4 bytes after one round. AddRoundKey: XOR the round key. The structure alternates the S-box (confusion) with the shifting/mixing (diffusion) — that's the Shannon avalanche pattern: after two rounds, every output bit depends on every input bit and every key bit."

**Interviewer**: "And the key schedule for AES-128?"

**Candidate**: "16 bytes of key expand to 44 words = 11 round keys. Each 4-word group derives from the previous: rotate the last word, pass each byte through the S-box, XOR with an rcon constant (1, 2, 4, 8, ..., a pattern in GF(2⁸)), then XOR with the word 4 positions back. The rcon constants prevent the rounds from being symmetric — without them, related-key attacks get much easier."

### Part 3: Theory — CBC Mode (8 minutes)

**Interviewer**: "Why CBC, and what are its failure modes?"

**Candidate**: "CBC chains: Cᵢ = Eₖ(Pᵢ ⊕ Cᵢ₋₁) with C₋₁ = IV. Encryption of block i depends on all previous plaintext — identical plaintext blocks produce different ciphertext, eliminating the ECB pattern leak. The requirements: a **random, unpredictable IV per message** (an attacker who can choose the IV can control the first block's decryption), IV uniqueness across messages with the same key (reuse leaks the XOR of plaintext prefixes — the classic 'two-time pad' style leak), and — critically — the mode provides **no integrity**: an attacker can flip bit j of Cᵢ and the same bit flips in Pᵢ₊₁, predictably. Block i itself gets garbage, which is how the padding-oracle attack works: flip bits in Cᵢ, observe whether decryption reports a padding error, and leak plaintext byte-by-byte with ~256 oracle queries per byte. The fix is authentication: encrypt-then-MAC (e.g. HMAC over the ciphertext) or, better, a dedicated AEAD mode — GCM."

**Interviewer**: "What does the padding error reveal, exactly?"

**Candidate**: "With PKCS7, the last block's final byte v must satisfy: the last v bytes are all v. If the decrypted last block fails that, the oracle says 'bad padding'. The attacker modifies the second-to-last ciphertext block byte-by-byte; each guess makes the decrypted final byte equal to guess ⊕ known quantities — when the oracle stops complaining, the attacker has recovered the plaintext byte. This is a *chosen-ciphertext* attack that works purely on the oracle's yes/no answer — hence 'oracle' in the name. The defense: authenticated encryption, and never distinguishing 'bad padding' from 'bad MAC' in error messages."

### Part 4: Implementation (15 minutes)

**Interviewer**: "Show me the CBC chaining and padding — the parts with the sharpest edges."

**Candidate**:

```java
public static byte[] cbcEncrypt(byte[] plaintext, byte[] key, byte[] iv) {
    requireKeySize(key);
    if (iv.length != 16) throw new IllegalArgumentException("IV must be 16 bytes");
    byte[] padded = pkcs7Pad(plaintext, 16);
    byte[][] roundKeys = expandKey(key);
    byte[] out = new byte[padded.length];
    byte[] prev = iv.clone();
    for (int i = 0; i < padded.length; i += 16) {
        byte[] block = xorBlock(padded, i, prev);
        byte[] enc = encryptBlock(block, roundKeys);
        System.arraycopy(enc, 0, out, i, 16);
        prev = enc;
    }
    return out;
}
```

**Candidate**: "And the padding — the full-block case is the classic trap: when the plaintext is already a multiple of 16, PKCS7 appends a *whole* padding block of 16 bytes, never zero bytes."

```java
public static byte[] pkcs7Pad(byte[] data, int blockSize) {
    int padLen = blockSize - (data.length % blockSize);
    byte[] out = Arrays.copyOf(data, data.length + padLen);
    Arrays.fill(out, data.length, out.length, (byte) padLen);
    return out;
}

public static byte[] pkcs7Unpad(byte[] data, int blockSize) {
    if (data.length == 0 || data.length % blockSize != 0) throw new IllegalArgumentException("bad length");
    int padLen = data[data.length - 1] & 0xFF;
    if (padLen == 0 || padLen > blockSize) throw new IllegalArgumentException("invalid pad length");
    for (int i = data.length - padLen; i < data.length; i++)
        if ((data[i] & 0xFF) != padLen) throw new IllegalArgumentException("corrupt padding");
    return Arrays.copyOf(data, data.length - padLen);
}
```

**Interviewer**: "Note the validation strictness — good. And the decrypt path?"

**Candidate**: "Mirror the chain: decrypt block i, XOR with Cᵢ₋₁ (for i=0 with the IV), then strict PKCS7 unpad. The subtle bit is the order of operations on the decrypt path: the inverse of (encrypt then XOR-chain) is (decrypt then XOR) — you must apply the AES inverse *before* the XOR, since the chain XOR happens after encryption on the forward path."

### Part 5: Testing (5 minutes)

**Interviewer**: "The acceptance criteria — what are the tests?"

**Candidate**: "Five layers. (1) **Published vector**: FIPS-197 Appendix B — key 000102...0f encrypting 001122...ff must yield 69c4e0d86a7b0430d8cdb78070b4c55a. This pins the S-box, key schedule, and round structure independently of any oracle. (2) **JCE cross-check**: random keys/IVs/plaintexts (including lengths 0..63), my CBC+PKCS7 must byte-match `AES/CBC/PKCS5Padding`. (3) **Round-trip**: random messages round-trip exactly. (4) **Mode properties**: same plaintext with different IVs gives different ciphertexts; a 1-bit ciphertext flip in block i garbles block i+1's corresponding bit and destroys block i — demonstrable with a printout. (5) **Padding strictness**: every malformed padding (padLen = 0, padLen > 16, inconsistent bytes) is rejected."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "When should a team *not* hand-roll AES?"

**Candidate**: "Almost always. The risk isn't the algorithm — it's the ecosystem: constant-time behavior (a data-dependent table lookup in the S-box leaks key material through cache timing — the AES-NI instructions or T-table-with-fixed-randomization exist precisely for this), the mode choice (CBC needs an authentication companion), the padding oracle surface, and the key management around it. Hand-rolling is justified here as a *learning lab* and because the company has a hard no-dependency policy — but production encryption should use JCE with AES-GCM, a random 96-bit nonce, and AAD binding context. GCM is the correct 2026-era answer: authenticated, one pass, and no padding oracle to exploit."

---

## Extended Q&A: Follow-up Round

**Q: Why is the S-box built from the multiplicative inverse in GF(2⁸)?**

**A**: Inversion is the strongest nonlinearity available on a byte: flipping one input bit changes the inverse unpredictably — on average half the output bits flip — which is exactly the "confusion" requirement. The affine transform applied afterwards removes the two algebraic weak spots (the fixed points S(x) = x and the zero map), so the cipher has no trivial structure to exploit. A linear S-box would make the entire cipher a linear map over GF(2), breakable by Gaussian elimination — nonlinearity is the whole game.

**Q: What does the avalanche effect mean here, concretely?**

**A**: Change one plaintext bit and, after enough rounds, each ciphertext bit flips with probability ≈ 1/2. AES reaches full avalanche in about two rounds: the S-box turns a one-bit change into a byte-scale change, and ShiftRows + MixColumns then spread that byte across the whole state. The lab demonstrates it by flipping a single ciphertext bit and counting how many output bits differ.

**Q: Why must the CBC IV be unpredictable, not merely unique?**

**A**: In CBC, P₁ = Dₖ(C₁) ⊕ IV. If an attacker can choose the IV, they can set IV = Dₖ(C₁) ⊕ target and thereby force P₁ = target — a chosen-plaintext control of the first block. Uniqueness alone prevents the prefix-leak between messages; unpredictability is what blocks first-block manipulation. This is why the IV is conventionally generated as 16 random bytes, never as a counter.

**Q: GCM vs CBC+HMAC — when is the classic composition still right?**

**A**: In 2026, JCE's GCM is everywhere, so the composition is only for legacy interop. When you must do it by hand: encrypt-then-MAC with *separate* keys (never the same key for both), MAC over the ciphertext including the IV, and verify the MAC before decrypting or parsing padding. GCM wins on every axis: one pass, authenticated, no padding oracle by construction.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | Explains all four round steps, avalanche, rcon purpose | Names the steps | Cannot explain SubBytes' role |
| Modes | IV requirements, bit-flip semantics, padding-oracle mechanics | CBC chaining correct | ECB-style thinking |
| Padding | Full-block pad case, strict unpad validation | Pads but sloppy unpad | No validation |
| Verification | FIPS-197 vector + JCE cross-check + property tests | One oracle comparison | No tests |
| Security judgement | Recommends GCM/authenticated encryption, side-channel awareness | Mentions MAC | No integrity discussion |

## Red Flags
- Reusing the IV across messages with the same key.
- Unpad that accepts padLen = 0 or silently ignores corrupt padding.
- Claiming CBC provides integrity or authenticity.
- Data-dependent timing paths without acknowledgment.

## Key Takeaways
- AES round = SubBytes (confusion) + ShiftRows/MixColumns (diffusion) + AddRoundKey.
- CBC needs a fresh random IV per message and never provides integrity.
- PKCS7 appends a full block when length is already aligned; unpad must validate strictly.
- Padding oracles break CBC; the modern answer is AEAD (GCM).
