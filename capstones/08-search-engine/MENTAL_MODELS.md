# Mental Models: Search Engine

- **Inverted Index = Book Index**: Like book's index at the back: "Java -> pages 42, 100, 203."
- **Tokenization = Chopping Words**: Split text into meaningful pieces (words, not spaces).
- **BM25 = Relevance Score**: Not just how many times a word appears, but how rare and how well it matches.
- **Index Segment = Diary Entry**: New diary pages are added as new segments; periodically bound into a book.
- **Merge = Cleanup**: Combine small segments into big ones; remove deleted documents.
- **Query Parser = Interpreter**: Translates "java AND spring NOT hibernate" into a search tree.
