# Code Deep Dive: Search Engine

## Inverted Index Building

`IndexWriter` processes documents through the `Analyzer` pipeline. The `Tokenizer` splits text on whitespace/punctuation, then `LowerCaseFilter`, `StopWordFilter`, and `PorterStemmer` transform tokens. For each token, the writer appends (docId, position) to the postings list in the current segment's buffer.

## Query Execution

`IndexSearcher` converts a parsed `Query` tree into a `Weight` object. `Weight.createWeight()` creates `Scorer` instances per segment. For `TermQuery`, the scorer iterates the postings list matching the term. For `BooleanQuery`, scorers are combined. All matching documents with their scores are collected into a `TopDocs` collector.

## BM25 Scoring

`BM25Similarity` implements the BM25 formula. It loads document length from field norms (stored per doc in segment). IDF is computed from terms dictionary (total doc count and term doc frequency). TF comes from the postings list. The score is computed per match in the scorer.

## Segment Merging

`MergeScheduler` runs in background thread. When a segment has > 10 documents and there are > 5 segments with < 50 docs each, it merges them. Merge reads all postings from candidate segments, merges by sorting all term-doc pairs, writes a new segment, then removes old segments via commit.
