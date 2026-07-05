# Step by Step: Search Engine

## Indexing a Document

1. User calls `IndexWriter.addDocument(doc)` where doc has fields (title, body)
2. For each field, `Analyzer.tokenize()` produces tokens: lowercase, stop-words removed, stemmed
3. For each token, writer checks current segment buffer
4. If term exists in buffer, append (docId, position) to postings
5. If term is new, add to terms dictionary
6. Document stored fields are written to segments.fld
7. Field norms computed: 1 / sqrt(length_of_field)
8. When buffer reaches threshold (maxBufferedDocs=100), flush to new segment
9. Segment is written to disk: terms.tim, postings.pos, norms.nvm, fields.fld
10. Commit point updated
