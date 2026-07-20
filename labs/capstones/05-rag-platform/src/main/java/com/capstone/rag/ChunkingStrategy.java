package com.capstone.rag;

import java.util.*;
import java.util.regex.Pattern;

public abstract class ChunkingStrategy {
    protected final int maxChunkSize;
    protected final int overlap;

    protected ChunkingStrategy(int maxChunkSize, int overlap) {
        this.maxChunkSize = maxChunkSize;
        this.overlap = overlap;
    }

    public abstract List<DocumentIngestor.Chunk> chunk(DocumentIngestor.Document doc);

    public static class FixedSizeChunker extends ChunkingStrategy {
        public FixedSizeChunker() { this(500, 50); }
        public FixedSizeChunker(int maxChunkSize, int overlap) { super(maxChunkSize, overlap); }

        @Override
        public List<DocumentIngestor.Chunk> chunk(DocumentIngestor.Document doc) {
            List<DocumentIngestor.Chunk> chunks = new ArrayList<>();
            String content = doc.rawContent();
            int start = 0;
            int idx = 0;
            while (start < content.length()) {
                int end = Math.min(start + maxChunkSize, content.length());
                String chunkText = content.substring(start, end);
                chunks.add(new DocumentIngestor.Chunk(
                    doc.id() + "-chunk-" + idx, doc.id(), chunkText, idx++, Map.of("strategy", "fixed")));
                start = end - overlap;
                if (start >= content.length()) break;
            }
            return chunks;
        }
    }

    public static class SemanticChunker extends ChunkingStrategy {
        private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("[.!?]\\s+");

        public SemanticChunker() { this(500, 50); }
        public SemanticChunker(int maxChunkSize, int overlap) { super(maxChunkSize, overlap); }

        @Override
        public List<DocumentIngestor.Chunk> chunk(DocumentIngestor.Document doc) {
            List<DocumentIngestor.Chunk> chunks = new ArrayList<>();
            String[] sentences = SENTENCE_BOUNDARY.split(doc.rawContent());
            StringBuilder current = new StringBuilder();
            int idx = 0;
            for (String sentence : sentences) {
                if (current.length() + sentence.length() > maxChunkSize && !current.isEmpty()) {
                    chunks.add(new DocumentIngestor.Chunk(
                        doc.id() + "-chunk-" + idx, doc.id(), current.toString().trim(), idx++, Map.of("strategy", "semantic")));
                    current = new StringBuilder();
                }
                current.append(sentence).append(". ");
            }
            if (!current.isEmpty()) {
                chunks.add(new DocumentIngestor.Chunk(
                    doc.id() + "-chunk-" + idx, doc.id(), current.toString().trim(), idx, Map.of("strategy", "semantic")));
            }
            return chunks;
        }
    }

    public static class RecursiveChunker extends ChunkingStrategy {
        private static final List<Pattern> SEPARATORS = List.of(
            Pattern.compile("\\n\\n"), Pattern.compile("\\n"), Pattern.compile("\\. "), Pattern.compile(" "));

        public RecursiveChunker() { this(500, 0); }
        public RecursiveChunker(int maxChunkSize, int overlap) { super(maxChunkSize, overlap); }

        @Override
        public List<DocumentIngestor.Chunk> chunk(DocumentIngestor.Document doc) {
            List<DocumentIngestor.Chunk> chunks = new ArrayList<>();
            List<String> pieces = splitRecursive(doc.rawContent(), 0);
            int idx = 0;
            for (String piece : pieces) {
                chunks.add(new DocumentIngestor.Chunk(
                    doc.id() + "-chunk-" + idx, doc.id(), piece, idx++, Map.of("strategy", "recursive")));
            }
            return chunks;
        }

        private List<String> splitRecursive(String text, int depth) {
            if (text.length() <= maxChunkSize || depth >= SEPARATORS.size()) return List.of(text);
            Pattern sep = SEPARATORS.get(depth);
            String[] parts = sep.split(text);
            List<String> result = new ArrayList<>();
            for (String part : parts) {
                if (part.length() > maxChunkSize) result.addAll(splitRecursive(part, depth + 1));
                else result.add(part);
            }
            return result;
        }
    }
}
