# Theory: Search Engine

## Inverted Index
The core data structure of search engines. Maps terms (words) to the documents they appear in, along with positions and frequencies. Enables fast full-text search: given a query term, look up the inverted index to find all matching documents.

## TF-IDF (Term Frequency-Inverse Document Frequency)
- TF: frequency of term in document (higher = more relevant)
- IDF: inverse of document frequency across corpus (rare terms = higher weight)
- Score = TF * IDF

## BM25 (Okapi BM25)
State-of-the-art ranking function that improves upon TF-IDF:
- Saturation: TF contribution plateaus at high frequencies
- Field-length normalization: shorter documents get preference
- score(D,Q) = sum over terms of IDF(q) * (TF(q,D) * (k1+1) / (TF(q,D) + k1 * (1 - b + b * |D|/avgdl)))

## Boolean Retrieval
Documents match if they satisfy a Boolean expression (AND, OR, NOT). Modern search engines combine Boolean (filtering) with ranked (scoring) retrieval.

## Tokenization
Process of splitting text into tokens (words). Involves: lowercasing, removing punctuation, handling compound words, Unicode normalization, and language-specific rules.

## Index Segmentation
The inverted index is divided into segments. Each segment is an independent index. New documents create new segments. Segments are periodically merged to keep the index efficient.
