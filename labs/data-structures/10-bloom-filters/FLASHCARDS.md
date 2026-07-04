# Flashcards: Bloom Filters

---

**Q**: What does a Bloom filter guarantee?
**A**: No false negatives — if an element was added, the filter always says maybe yes.

---

**Q**: What are false positives in Bloom filters?
**A**: The filter says "yes" for an element that was never added (tunable, probabilistic).

---

**Q**: What are the three parameters of a Bloom filter?
**A**: m = bit array size, n = expected insertions, k = number of hash functions.

---

**Q**: What is the optimal number of hash functions k?
**A**: k = (m/n) × ln(2)

---

**Q**: What is the false positive probability formula?
**A**: P = (1 - e^(-kn/m))^k

---

**Q**: How many bits per element for 1% FPP?
**A**: ~10 bits per element.

---

**Q**: What is a Counting Bloom filter?
**A**: Uses counters instead of bits — supports element deletion.

---

**Q**: Which Java library provides BloomFilter?
**A**: Guava (`com.google.common.hash.BloomFilter`)

---

**Q**: Can you remove elements from a standard Bloom filter?
**A**: No — standard Bloom filters don't support deletion.

---

**Q**: What happens if you insert more than expected elements?
**A**: False positive rate increases beyond the designed level.
