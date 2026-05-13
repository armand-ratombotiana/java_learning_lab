package com.rag.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DocumentProcessor {

    private final Tika tika = new Tika();
    private static final int CHUNK_SIZE = 500;
    private static final int CHUNK_OVERLAP = 50;

    public String extractText(InputStream inputStream, String fileName) {
        try {
            String text = tika.parseToString(inputStream);
            log.info("Extracted {} characters from {}", text.length(), fileName);
            return text;
        } catch (Exception e) {
            log.error("Failed to extract text from {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Text extraction failed", e);
        }
    }

    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return chunks;
        }

        String[] sentences = text.split("[.!?]+\\s*");
        StringBuilder currentChunk = new StringBuilder();
        int currentTokens = 0;

        for (String sentence : sentences) {
            int sentenceTokens = sentence.split("\\s+").length;

            if (currentTokens + sentenceTokens > CHUNK_SIZE && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                String overlap = currentChunk.toString();
                int overlapStart = Math.max(0, overlap.length() - CHUNK_OVERLAP);
                currentChunk = new StringBuilder(overlap.substring(overlapStart));
                currentTokens = currentChunk.toString().split("\\s+").length;
            }

            currentChunk.append(sentence).append(". ");
            currentTokens += sentenceTokens;
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        log.info("Created {} chunks from text", chunks.size());
        return chunks;
    }

    public List<String> chunkByParagraphs(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        String[] paragraphs = text.split("\\n\\n+");
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (currentChunk.length() + paragraph.length() > CHUNK_SIZE * 6) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                }
                currentChunk = new StringBuilder();
            }
            currentChunk.append(paragraph).append("\n\n");
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }
}