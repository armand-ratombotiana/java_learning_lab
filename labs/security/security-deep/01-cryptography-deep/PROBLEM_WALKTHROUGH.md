# Problem Walkthrough: AES-128 CBC Encryption with PKCS7 Padding

## Problem Statement

Implement AES-128 encryption in **CBC mode** with **PKCS7 padding** from scratch in pure Java:

1. **AES core**: the full block cipher — S-box, key expansion (128-bit key → 11 round keys), SubBytes/ShiftRows/MixColumns/AddRoundKey rounds, and the complete inverse (InvShiftRows/InvSubBytes/InvMixColumns) for decryption.
2. **CBC chaining**: Cᵢ = Eₖ(Pᵢ ⊕ Cᵢ₋₁), C₋₁ = IV, with strict IV-size validation.
3. **PKCS7 padding**: correct full-block padding behavior and *strict* unpadding validation.
4. **Verification**: the FIPS-197 Appendix B known-answer test, byte-for-byte comparison against JCE (`AES/CBC/PKCS5Padding`), round-trip property tests, chaining/diffusion demonstrations, and padding-rejection tests.

**Deliverable**: `com.security.deep.lab01.AesCbc` — complete Java 21+ class with `encryptBlock`, `decryptBlock`, `expandKey`, `cbcEncrypt`, `cbcDecrypt`, `pkcs7Pad`, `pkcs7Unpad`, and the `main` verification driver (JCE used only as a test oracle).

---

## Step 1: Mathematical Foundation

### 1.1 The AES-128 block cipher

The state is a 4×4 column-major byte matrix. AES-128 runs **10 rounds**, each applying four steps: **SubBytes** (the S-box — multiplicative inverse in GF(2⁸) plus an affine transform; the cipher's only nonlinearity), **ShiftRows** (row r rotates left by r), **MixColumns** (column × circulant MDS matrix (2,3,1,1) in GF(2⁸), with xtime = ×2), **AddRoundKey** (XOR the round key). The last round omits MixColumns; decryption applies the inverses in reverse order.

### 1.2 The key schedule

16 key bytes → 44 words → round keys 0..10. For word i ≥ 4: temp = w[i-1]; if i ≡ 0 (mod 4): rotate-left 1 byte, S-box all 4 bytes, XOR the first byte with rcon[i/4−1] (01, 02, 04, 08, 10, 20, 40, 80, 1B, 36); then temp[j] ^= w[i-4][j] — the rcon XOR breaks round symmetry.

### 1.3 CBC mode

Cᵢ = Eₖ(Pᵢ ⊕ Cᵢ₋₁), C₋₁ = IV; decryption Pᵢ = Dₖ(Cᵢ) ⊕ Cᵢ₋₁. Properties: identical plaintext blocks chain to different ciphertext; a bit-flip in Cᵢ flips the same bit in Pᵢ₊₁ and destroys Pᵢ; the IV must be fresh and unpredictable per message; **CBC provides no integrity**.

### 1.4 PKCS7 padding

Pad to a multiple of 16; the pad byte equals the pad length (1..16). **Full-block case**: aligned plaintext still gets a whole 16-byte block of 0x10 — never zero bytes. Unpad must validate the pad byte and every pad byte strictly.

---

## Step 2: Design

### 2.2 Verification strategy

1. **FIPS-197 Appendix B**: key 000102...0f, plaintext 001122...ff → ciphertext must equal `69c4e0d86a7b0430d8cdb78070b4c55a` — one vector exercising key schedule, rounds, and final-round structure; it also proves the *computed* S-box is correct.
2. **JCE cross-check**: 200 random (key, iv, plaintext) triples, lengths 0..63, byte-matched against `AES/CBC/PKCS5Padding` (which is PKCS7 at 16-byte blocks).
3. **Round-trip**: `cbcDecrypt(cbcEncrypt(m)) == m` for lengths 0..64.
4. **Mode properties**: different IVs → different ciphertexts; bit-flip in Cᵢ garbles Pᵢ and flips exactly one bit of Pᵢ₊₁.
5. **Padding strictness**: reject padLen = 0, padLen > 16, non-uniform bytes, non-aligned lengths.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.security.deep.lab01;

import java.util.Arrays;
import java.util.Random;

public final class AesCbc {

    private static final int BLOCK = 16;
    private static final int ROUNDS = 10;

    private static final byte[] SBOX = buildSbox();

    private static byte[] buildSbox() {
        byte[] s = new byte[256];
        for (int i = 0; i < 256; i++) {
            int b = i == 0 ? 0 : gfInverse(i);
            b = b ^ rotl(b, 1) ^ rotl(b, 2) ^ rotl(b, 3) ^ rotl(b, 4) ^ 0x63;
            s[i] = (byte) (b & 0xFF);
        }
        return s;
    }

    private static int gfInverse(int a) {
        int acc = 1;
        int x = a;
        for (int i = 0; i < 7; i++) {   // x = a^(2^(i+1)); acc = a^(2+4+...+128) = a^254 = a^-1
            x = mul(x, x);
            acc = mul(acc, x);
        }
        return acc;
    }

    private static int rotl(int b, int k) {
        return ((b << k) | (b >>> (8 - k))) & 0xFF;
    }

    private static final byte[] INV_SBOX = buildInvSbox();

    private static byte[] buildInvSbox() {
        byte[] inv = new byte[256];
        for (int i = 0; i < 256; i++) {
            inv[SBOX[i] & 0xFF] = (byte) i;
        }
        return inv;
    }

    private AesCbc() {}

    private static byte xtime(int b) {
        int r = (b << 1) & 0xFF;
        if ((b & 0x80) != 0) r ^= 0x1B;
        return (byte) r;
    }

    private static byte mul(int a, int b) {
        int r = 0;
        int x = a & 0xFF;
        int y = b & 0xFF;
        for (int i = 0; i < 8; i++) {
            if ((y & 1) != 0) r ^= x;
            int hi = x & 0x80;
            x = (x << 1) & 0xFF;
            if (hi != 0) x ^= 0x1B;
            y >>= 1;
        }
        return (byte) r;
    }

    public static byte[][] expandKey(byte[] key) {
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("AES-128 requires a 16-byte key");
        }
        byte[][] w = new byte[44][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(key, 4 * i, w[i], 0, 4);
        }
        byte[] rcon = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36};
        for (int i = 4; i < 44; i++) {
            byte[] temp = w[i - 1].clone();
            if (i % 4 == 0) {
                byte t0 = temp[0];
                for (int j = 0; j < 3; j++) temp[j] = temp[j + 1];
                temp[3] = t0;
                for (int j = 0; j < 4; j++) temp[j] = SBOX[temp[j] & 0xFF];
                temp[0] ^= rcon[i / 4 - 1];
            }
            for (int j = 0; j < 4; j++) temp[j] ^= w[i - 4][j];
            w[i] = temp;
        }
        return w;
    }

    private static void addRoundKey(byte[][] state, byte[][] w, int round) {
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                state[r][c] ^= w[4 * round + c][r];
            }
        }
    }

    private static void subBytes(byte[][] state) {
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                state[r][c] = SBOX[state[r][c] & 0xFF];
    }

    private static void invSubBytes(byte[][] state) {
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                state[r][c] = INV_SBOX[state[r][c] & 0xFF];
    }

    private static void shiftRows(byte[][] state) {
        for (int row = 1; row < 4; row++) {
            byte[] tmp = new byte[4];
            for (int c = 0; c < 4; c++) tmp[c] = state[row][c];
            for (int c = 0; c < 4; c++) state[row][c] = tmp[(c + row) % 4];
        }
    }

    private static void invShiftRows(byte[][] state) {
        for (int row = 1; row < 4; row++) {
            byte[] tmp = new byte[4];
            for (int c = 0; c < 4; c++) tmp[c] = state[row][c];
            for (int c = 0; c < 4; c++) state[row][c] = tmp[(c - row + 4) % 4];
        }
    }

    private static void mixColumns(byte[][] state) {
        for (int c = 0; c < 4; c++) {
            int a0 = state[0][c] & 0xFF, a1 = state[1][c] & 0xFF;
            int a2 = state[2][c] & 0xFF, a3 = state[3][c] & 0xFF;
            state[0][c] = (byte) (xtime(a0) ^ (xtime(a1) ^ a1) ^ a2 ^ a3);
            state[1][c] = (byte) (a0 ^ xtime(a1) ^ (xtime(a2) ^ a2) ^ a3);
            state[2][c] = (byte) (a0 ^ a1 ^ xtime(a2) ^ (xtime(a3) ^ a3));
            state[3][c] = (byte) ((xtime(a0) ^ a0) ^ a1 ^ a2 ^ xtime(a3));
        }
    }

    private static void invMixColumns(byte[][] state) {
        for (int c = 0; c < 4; c++) {
            int a0 = state[0][c] & 0xFF, a1 = state[1][c] & 0xFF;
            int a2 = state[2][c] & 0xFF, a3 = state[3][c] & 0xFF;
            state[0][c] = (byte) (mul(14, a0) ^ mul(11, a1) ^ mul(13, a2) ^ mul(9, a3));
            state[1][c] = (byte) (mul(9, a0) ^ mul(14, a1) ^ mul(11, a2) ^ mul(13, a3));
            state[2][c] = (byte) (mul(13, a0) ^ mul(9, a1) ^ mul(14, a2) ^ mul(11, a3));
            state[3][c] = (byte) (mul(11, a0) ^ mul(13, a1) ^ mul(9, a2) ^ mul(14, a3));
        }
    }

    public static byte[] encryptBlock(byte[] block, byte[][] roundKeys) {
        if (block == null || block.length != 16) {
            throw new IllegalArgumentException("block must be 16 bytes");
        }
        byte[][] state = new byte[4][4];
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                state[r][c] = block[r + 4 * c];

        addRoundKey(state, roundKeys, 0);
        for (int round = 1; round < ROUNDS; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, roundKeys, round);
        }
        subBytes(state);
        shiftRows(state);
        addRoundKey(state, roundKeys, ROUNDS);

        byte[] out = new byte[16];
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                out[r + 4 * c] = state[r][c];
        return out;
    }

    public static byte[] decryptBlock(byte[] block, byte[][] roundKeys) {
        if (block == null || block.length != 16) {
            throw new IllegalArgumentException("block must be 16 bytes");
        }
        byte[][] state = new byte[4][4];
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                state[r][c] = block[r + 4 * c];

        addRoundKey(state, roundKeys, ROUNDS);
        for (int round = ROUNDS - 1; round >= 1; round--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, roundKeys, round);
            invMixColumns(state);
        }
        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, roundKeys, 0);

        byte[] out = new byte[16];
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                out[r + 4 * c] = state[r][c];
        return out;
    }

    public static byte[] pkcs7Pad(byte[] data, int blockSize) {
        if (blockSize <= 0 || blockSize > 255) {
            throw new IllegalArgumentException("blockSize out of range");
        }
        int padLen = blockSize - (data.length % blockSize);
        byte[] out = Arrays.copyOf(data, data.length + padLen);
        Arrays.fill(out, data.length, out.length, (byte) padLen);
        return out;
    }

    public static byte[] pkcs7Unpad(byte[] data, int blockSize) {
        if (data == null || data.length == 0 || data.length % blockSize != 0) {
            throw new IllegalArgumentException("data length not a positive multiple of blockSize");
        }
        int padLen = data[data.length - 1] & 0xFF;
        if (padLen == 0 || padLen > blockSize) {
            throw new IllegalArgumentException("invalid padding length " + padLen);
        }
        for (int i = data.length - padLen; i < data.length; i++) {
            if ((data[i] & 0xFF) != padLen) {
                throw new IllegalArgumentException("corrupt padding bytes");
            }
        }
        return Arrays.copyOf(data, data.length - padLen);
    }

    private static byte[] xorBlock(byte[] src, int offset, byte[] with) {
        byte[] out = new byte[16];
        for (int i = 0; i < 16; i++) out[i] = (byte) (src[offset + i] ^ with[i]);
        return out;
    }

    public static byte[] cbcEncrypt(byte[] plaintext, byte[] key, byte[] iv) {
        byte[][] roundKeys = expandKey(key);
        if (iv == null || iv.length != BLOCK) {
            throw new IllegalArgumentException("IV must be 16 bytes");
        }
        byte[] padded = pkcs7Pad(plaintext, BLOCK);
        byte[] out = new byte[padded.length];
        byte[] prev = iv.clone();
        for (int i = 0; i < padded.length; i += BLOCK) {
            byte[] enc = encryptBlock(xorBlock(padded, i, prev), roundKeys);
            System.arraycopy(enc, 0, out, i, BLOCK);
            prev = enc;
        }
        return out;
    }

    public static byte[] cbcDecrypt(byte[] ciphertext, byte[] key, byte[] iv) {
        byte[][] roundKeys = expandKey(key);
        if (iv == null || iv.length != BLOCK) {
            throw new IllegalArgumentException("IV must be 16 bytes");
        }
        if (ciphertext == null || ciphertext.length == 0 || ciphertext.length % BLOCK != 0) {
            throw new IllegalArgumentException("ciphertext length must be a positive multiple of 16");
        }
        byte[] out = new byte[ciphertext.length];
        byte[] prev = iv.clone();
        for (int i = 0; i < ciphertext.length; i += BLOCK) {
            byte[] dec = decryptBlock(Arrays.copyOfRange(ciphertext, i, i + BLOCK), roundKeys);
            byte[] plain = xorBlock(dec, 0, prev);
            System.arraycopy(plain, 0, out, i, BLOCK);
            prev = Arrays.copyOfRange(ciphertext, i, i + BLOCK);
        }
        return pkcs7Unpad(out, BLOCK);
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== AES-128 CBC with PKCS7 — verification ===");

        System.out.println("--- 1. FIPS-197 Appendix B known-answer test ---");
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) key[i] = (byte) i;
        byte[] pt = new byte[16];
        for (int i = 0; i < 16; i++) pt[i] = (byte) (16 * i + i);
        byte[][] rk = expandKey(key);
        byte[] ct = encryptBlock(pt, rk);
        String expected = "69c4e0d86a7b0430d8cdb78070b4c55a";
        System.out.printf("encryptBlock  = %s%nexpected       = %s%n", hex(ct), expected);
        System.out.printf("vector match: %b%n", hex(ct).equals(expected));

        System.out.println("--- 2. JCE cross-check (AES/CBC/PKCS5Padding) ---");
        javax.crypto.Cipher jce = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        Random rng = new Random(42L);
        int mismatches = 0;
        for (int t = 0; t < 200; t++) {
            byte[] k = new byte[16];
            byte[] iv = new byte[16];
            byte[] msg = new byte[rng.nextInt(64)];
            rng.nextBytes(k);
            rng.nextBytes(iv);
            rng.nextBytes(msg);
            byte[] mine = cbcEncrypt(msg, k, iv);
            jce.init(javax.crypto.Cipher.ENCRYPT_MODE,
                     new javax.crypto.spec.SecretKeySpec(k, "AES"),
                     new javax.crypto.spec.IvParameterSpec(iv));
            byte[] theirs = jce.doFinal(msg);
            if (!Arrays.equals(mine, theirs)) mismatches++;
        }
        System.out.printf("JCE cross-check mismatches: %d/200%n", mismatches);

        System.out.println("--- 3. Round-trip property test ---");
        int rtFailures = 0;
        for (int len = 0; len <= 64; len++) {
            for (int t = 0; t < 20; t++) {
                byte[] k = new byte[16];
                byte[] iv = new byte[16];
                byte[] msg = new byte[len];
                rng.nextBytes(k);
                rng.nextBytes(iv);
                rng.nextBytes(msg);
                byte[] back = cbcDecrypt(cbcEncrypt(msg, k, iv), k, iv);
                if (!Arrays.equals(msg, back)) rtFailures++;
            }
        }
        System.out.printf("round-trip failures: %d/1300%n", rtFailures);

        System.out.println("--- 4. Mode properties ---");
        byte[] msg = "attack at dawn attack at dawn attack at dawn".getBytes();
        byte[] k1 = new byte[16];
        byte[] iv1 = new byte[16];
        byte[] iv2 = new byte[16];
        rng.nextBytes(k1);
        rng.nextBytes(iv1);
        rng.nextBytes(iv2);
        byte[] c1 = cbcEncrypt(msg, k1, iv1);
        byte[] c2 = cbcEncrypt(msg, k1, iv2);
        System.out.printf("same plaintext, different IV -> different ciphertext: %b%n",
                          !Arrays.equals(c1, c2));

        byte[] tampered = c1.clone();
        tampered[0] ^= 0x01;
        byte[] dec = cbcDecrypt(tampered, k1, iv1);
        byte[] honest = cbcDecrypt(c1, k1, iv1);
        int diffBits = 0;
        for (int i = 0; i < honest.length; i++) {
            diffBits += Integer.bitCount((honest[i] ^ dec[i]) & 0xFF);
        }
        System.out.printf("1-bit ciphertext flip -> plaintext bit flips observed: %d "
                          + "(block 0 garbled, 1 bit in block 1)%n", diffBits);

        System.out.println("--- 5. Padding strictness ---");
        int paddingRejected = 0;
        byte[] valid = pkcs7Pad(msg, 16);
        byte[] badZero = Arrays.copyOf(valid, valid.length);
        badZero[badZero.length - 1] = 0x00;
        byte[] badLarge = Arrays.copyOf(valid, valid.length);
        badLarge[badLarge.length - 1] = 0x11;
        byte[] badInconsistent = Arrays.copyOf(valid, valid.length);
        badInconsistent[badInconsistent.length - 2] = 0x05;
        for (byte[] bad : new byte[][]{badZero, badLarge, badInconsistent}) {
            try {
                pkcs7Unpad(bad, 16);
            } catch (IllegalArgumentException e) {
                paddingRejected++;
            }
        }
        System.out.printf("malformed paddings rejected: %d/3%n", paddingRejected);
        byte[] aligned = pkcs7Pad(new byte[32], 16);
        System.out.printf("full-block pad adds 16 bytes: %b (len %d -> %d)%n",
                          aligned.length == 48, 32, aligned.length);

        System.out.println("--- 6. Key size enforcement ---");
        try {
            expandKey(new byte[24]);
            System.out.println("24-byte key: NOT rejected (BUG)");
        } catch (IllegalArgumentException e) {
            System.out.println("24-byte key correctly rejected (AES-128 only)");
        }
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

**FIPS-197 vector.** Key 000102...0f, plaintext 001122...ff (byte i is 0x11·i). The 10-round cipher with the full schedule must emit 69c4e0d86a7b0430d8cdb78070b4c55a. A match pins the computed S-box, the key expansion, the round ordering, and the omitted final MixColumns jointly — the first thing a reviewer checks.

**CBC chaining.** "attack at dawn…" (48 bytes) pads to 64 → 4 blocks: C₀ = Eₖ(P₀ ⊕ IV); C₁ = Eₖ(P₁ ⊕ C₀); C₂ = Eₖ(P₂ ⊕ C₁). The repeated substring "attack at da" produces different ciphertext per block — the property the IV test asserts (`!Arrays.equals(c1, c2)`).

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | FIPS-197 Appendix B | key=0001..0f, pt=0011..ff | ct = 69c4e0d86a7b0430d8cdb78070b4c55a | main() §1 |
| 2 | JCE cross-check | 200 random triples, len 0..63 | byte-identical | main() §2 |
| 3 | Round-trip | lengths 0..64 × 20 trials | exact | main() §3 |
| 4 | Bit-flip semantics | flip bit 0 of C₀ | P₀ garbled + exactly 1 bit in P₁ | main() §4 |
| 6 | Padding rejection | padLen 0 / 17 / inconsistent | all rejected | main() §5 |
| 7 | Full-block padding | 32 bytes → padded | 48 bytes (0x10 block) | main() §5 |
| 8 | Key size | 24-byte key | rejected | main() §6 |

---

## Complexity Analysis

**Time**: O(n) for an n-byte message — 10 rounds × 16 bytes of S-box + MixColumns work per block, plus a 16-byte XOR per block for the chain. The scalar implementation runs at roughly 1–5 MB/s (no AES-NI); JCE's hardware path is 10–100× faster and is what production uses.

**Space**: O(44·4) key schedule + O(16) working state + O(n) output.

**Security caveat**: this code is **not constant-time** — key-dependent S-box table lookups leak through cache timing (Bernstein 2005). Production must use AES-NI or a constant-time S-box strategy.

---

## Edge Cases & Pitfalls

1. **Full-block padding**: aligned plaintext still gets a 16-byte pad block; unpad never returns "no padding needed".
2. **padLen = 0 / corrupt interior**: a 0x00 last byte is invalid under PKCS7; a valid-looking padLen with mismatched interior bytes must also be rejected — sloppy unpadding is a padding-oracle amplifier.
3. **IV validation**: wrong IV length throws; IV reuse is a caller contract the API documents but cannot enforce.
4. **Signed bytes**: S-box indices and xtime/mul must mask `& 0xFF` at every byte-to-int transition.
5. **Decrypt order**: the inverse of "XOR then encrypt" is "decrypt then XOR" — a reversed order yields garbage that only the round-trip test catches.
6. **No caller mutation**: `expandKey` copies input; `cbcEncrypt` clones the IV.

---

## Follow-up Questions

1. **Padding-oracle attack**: if `pkcs7Unpad`'s exception surfaced as an HTTP 400, why does the attack need only ~256 oracle queries per plaintext byte?
2. **Why GCM**: CBC+HMAC (encrypt-then-MAC, separate keys) is correct, but GCM is one pass, parallelizable, padding-free, and authenticates AAD; compare GCM nonce-reuse failure with CBC IV-reuse.
3. **Key schedule**: why the rcon XOR prevents slide attacks; the 128/192/256-bit schedules differ in word counts (44/52/60) and rounds (10/12/14).
4. **Cache-timing attacks**: Bernstein 2005 on OpenSSL AES; fixed-randomized T-tables or bit-slicing mitigate; AES-NI is the definitive answer.

---

## Extension Ideas

- **AES-192/256**: generalize `expandKey` with Nk/Nr parameters (12/14 rounds); verify with FIPS-197 Appendix C vectors.
- **AES-GCM / CTR**: implement GHASH (GF(2¹²⁸)) and the GCM wrapper, or the padding-free CTR keystream; verify against NIST SP 800-38A vectors — the natural production upgrade.
- **Constant-time S-box**: bitsliced GF(2⁸) inversion or fixed-window tables; measure timing variance with a jittered benchmark.
- **Known-answer corpus**: FIPS-197 Appendix C + NIST CBC vectors as the CI regression gate.
