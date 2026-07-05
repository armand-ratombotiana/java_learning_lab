# Reflection: Search Engine

## What I Learned
- Inverted index construction and optimization (delta encoding, VInt)
- BM25 ranking algorithm and its advantages over TF-IDF
- Segment-based index architecture for efficient updates
- Query parsing and Boolean query optimization
- The importance of the analyzer pipeline for search quality

## Challenges
- Implementing FST for the terms dictionary (complex data structure)
- Tuning BM25 parameters (k1, b) for different document types
- Debugging tokenizer edge cases (Unicode, compound words, URLs)
- Optimizing merge policy for write-heavy workloads

## What I'd Do Differently
- Implement FST earlier (terms dictionary lookup was a bottleneck)
- Build a more comprehensive test corpus with known relevance judgments
- Add skip lists in postings for faster Boolean intersection
- Implement query evaluation profiling early (find slow paths)
