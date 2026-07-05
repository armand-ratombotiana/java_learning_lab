# Flashcards: Search Engine

Front: What is an inverted index? | Back: A data structure mapping terms to the documents they appear in, enabling fast full-text search.

Front: What is BM25? | Back: Okapi BM25 — a ranking function that scores documents by term frequency saturation, inverse document frequency, and field length normalization.

Front: What is a tokenizer? | Back: Splits text into tokens (words) based on delimiters (whitespace, punctuation) for indexing and querying.

Front: What is steming? | Back: Reducing words to their root form (e.g., "running" -> "run", "ran" -> "run") to improve recall.

Front: What is an FST? | Back: Finite State Transducer — a compressed data structure for storing the terms dictionary, enabling fast prefix lookups.

Front: What is segment merging? | Back: Combining multiple small index segments into larger ones to reduce file count and improve query performance.
