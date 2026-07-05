# Exercises: Search Engine

## Beginner
1. Implement a simple tokenizer that splits on whitespace and punctuation
2. Add a lowercase filter and a stop-word filter
3. Implement a basic inverted index using HashMap<String, List<Posting>>

## Intermediate
4. Implement BM25 scoring with field length normalization
5. Add Boolean query support (AND, OR, NOT)
6. Implement index segmentation and merge policy
7. Add phrase query using position data from postings

## Advanced
8. Implement an FST (Finite State Transducer) for the terms dictionary
9. Add fuzzy query support (Levenshtein automaton)
10. Implement real-time search (NRT with reopen)
11. Add highlighting (find and wrap query terms in results)
12. Build a distributed search engine with sharding and replication
