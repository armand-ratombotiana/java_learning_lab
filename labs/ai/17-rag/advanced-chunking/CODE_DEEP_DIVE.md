# Advanced Chunking Code Deep Dive

This lab provides a pure Java implementation of a Recursive Character Text Splitter. It attempts to split text at paragraph boundaries first, then falls back to sentence boundaries, and finally word boundaries if necessary to stay under the chunk size limit.

## 💻 Pure Java Implementation

```java file="labs/ai/17-rag/advanced-chunking/SOLUTION/RecursiveTextSplitter.java"
package ai.genai.rag.chunking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fundamental implementation of a Recursive Character Text Splitter.
 */
public class RecursiveTextSplitter {

    private final int chunkSize;
    private final int chunkOverlap;
    
    // The hierarchy of separators: Paragraphs -> Sentences -> Words -> Characters
    private final List<String> separators = Arrays.asList("\n\n", "\n", " ", "");

    public RecursiveTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    /**
     * Splits the text recursively based on the hierarchy of separators.
     */
    public List<String> splitText(String text) {
        return splitRecursively(text, separators);
    }

    private List<String> splitRecursively(String text, List<String> currentSeparators) {
        List<String> finalChunks = new ArrayList<>();

        // If the text is already small enough, return it as a single chunk
        if (text.length() <= chunkSize) {
            finalChunks.add(text);
            return finalChunks;
        }

        // Find the highest-level separator that actually exists in the text
        String separator = "";
        List<String> nextSeparators = new ArrayList<>();
        
        for (int i = 0; i < currentSeparators.size(); i++) {
            String s = currentSeparators.get(i);
            if (s.equals("") || text.contains(s)) {
                separator = s;
                // The next level of recursion will try smaller separators
                nextSeparators = currentSeparators.subList(i + 1, currentSeparators.size());
                break;
            }
        }

        // Split the text using the chosen separator
        String[] splits;
        if (separator.equals("")) {
            // Fallback: split character by character
            splits = text.split("");
        } else {
            // Use regex quote to avoid regex syntax errors on literal strings
            splits = text.split(java.util.regex.Pattern.quote(separator));
        }

        // Merge the splits back together into chunks of appropriate size
        List<String> currentMergedChunk = new ArrayList<>();
        int currentLength = 0;

        for (String split : splits) {
            if (split.trim().isEmpty()) continue;

            // If a single split is STILL larger than the chunk size, we must recurse deeper
            if (split.length() > chunkSize) {
                if (!nextSeparators.isEmpty()) {
                    List<String> recursiveChunks = splitRecursively(split, nextSeparators);
                    finalChunks.addAll(recursiveChunks);
                } else {
                    // We ran out of separators (e.g., a single word is > chunkSize)
                    // Force a hard crop (rare in natural language)
                    finalChunks.add(split.substring(0, chunkSize));
                }
                continue;
            }

            // Calculate length if we add this split + the separator
            int newLength = currentLength + split.length() + (currentMergedChunk.isEmpty() ? 0 : separator.length());

            if (newLength > chunkSize) {
                // The chunk is full. Save it.
                String mergedText = String.join(separator, currentMergedChunk);
                finalChunks.add(mergedText);

                // Handle Overlap for the next chunk
                // We keep removing from the beginning of the current merged chunk until it's small enough to be the overlap
                while (currentLength > chunkOverlap && currentMergedChunk.size() > 1) {
                    String removed = currentMergedChunk.remove(0);
                    currentLength -= (removed.length() + separator.length());
                }
            }

            currentMergedChunk.add(split);
            currentLength += split.length() + (currentMergedChunk.size() > 1 ? separator.length() : 0);
        }

        // Add any remaining text as the final chunk
        if (!currentMergedChunk.isEmpty()) {
            finalChunks.add(String.join(separator, currentMergedChunk));
        }

        return finalChunks;
    }

    public static void main(String[] args) {
        String document = "This is the first paragraph. It contains some general information.\n\n" +
                          "This is the second paragraph. It is much longer and contains highly specific details that we want to ensure stay together. However, if it gets too long, it must be split.\n\n" +
                          "This is the third paragraph.";

        // Target: 80 characters max per chunk, 20 characters of overlap
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(80, 20);
        
        List<String> chunks = splitter.splitText(document);
        
        System.out.println("Total Chunks: " + chunks.size() + "\n");
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- Chunk " + (i + 1) + " (Length: " + chunks.get(i).length() + ") ---");
            System.out.println(chunks.get(i) + "\n");
        }
    }
}
```

## 🔍 Key Takeaways
1. **The Separator Hierarchy**: Notice `Arrays.asList("\n\n", "\n", " ", "")`. The algorithm respects the author's original formatting. It will always try to keep paragraphs intact. Only if a paragraph exceeds the `chunkSize` will it break the paragraph down into sentences (`\n`), and only if a sentence is too long will it break it into words (` `).
2. **The Overlap Logic**: Look at the `while (currentLength > chunkOverlap)` loop. When a chunk is full, we don't just clear the list and start fresh. We remove elements from the *beginning* of the list until the remaining elements roughly equal the desired `chunkOverlap` size. This ensures the end of Chunk 1 becomes the beginning of Chunk 2, preserving context across boundaries.