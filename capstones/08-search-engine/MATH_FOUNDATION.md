# Math Foundation: Search Engine

## BM25 Score
score(D,Q) = sum_{q in Q} IDF(q) * (TF(q,D) * (k1+1)) / (TF(q,D) + k1 * (1 - b + b * |D|/avgdl))

- k1 = 1.2 (saturation parameter)
- b = 0.75 (length normalization)
- |D| = document length (in terms)
- avgdl = average document length in corpus

## IDF
IDF(q) = log(1 + (N - n(q) + 0.5) / (n(q) + 0.5))
- N = total documents
- n(q) = documents containing term q

## TF Saturation
TF contribution plateaus: doubling TF from 10 to 20 doesn't double the score. Controlled by k1.

## Field Normalization
Short documents get a boost over long documents for the same TF. Controlled by b.
