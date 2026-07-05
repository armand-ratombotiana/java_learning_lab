package com.ai18;

import java.util.*;

public class Tokenizer {
    private Map<String, Integer> vocab;
    private List<String> reverseVocab;
    private int vocabSize;

    public Tokenizer() {
        this.vocab = new HashMap<>();
        this.reverseVocab = new ArrayList<>();
        this.vocabSize = 0;
    }

    public void buildVocab(List<String> texts) {
        Set<String> tokens = new HashSet<>();
        for (String text : texts)
            for (String token : text.toLowerCase().split("\\s+"))
                if (!token.isEmpty()) tokens.add(token);
        vocab.clear();
        reverseVocab.clear();
        int idx = 0;
        vocab.put("<PAD>", idx++);
        vocab.put("<UNK>", idx++);
        vocab.put("<BOS>", idx++);
        vocab.put("<EOS>", idx++);
        for (String token : tokens)
            vocab.put(token, idx++);
        vocabSize = vocab.size();
        reverseVocab = new ArrayList<>(Collections.nCopies(vocabSize, ""));
        for (Map.Entry<String, Integer> e : vocab.entrySet())
            reverseVocab.set(e.getValue(), e.getKey());
    }

    public List<Integer> encode(String text) {
        List<Integer> ids = new ArrayList<>();
        ids.add(vocab.get("<BOS>"));
        for (String token : text.toLowerCase().split("\\s+")) {
            if (token.isEmpty()) continue;
            ids.add(vocab.getOrDefault(token, vocab.get("<UNK>")));
        }
        ids.add(vocab.get("<EOS>"));
        return ids;
    }

    public String decode(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int id : ids) {
            String token = reverseVocab.get(id);
            if (token.equals("<PAD>") || token.equals("<BOS>") || token.equals("<EOS>")) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(token);
        }
        return sb.toString();
    }

    public List<Integer> truncatePad(List<Integer> ids, int maxLength) {
        if (ids.size() > maxLength)
            return ids.subList(0, maxLength);
        List<Integer> result = new ArrayList<>(ids);
        while (result.size() < maxLength)
            result.add(vocab.get("<PAD>"));
        return result;
    }

    public int getVocabSize() { return vocabSize; }
    public Map<String, Integer> getVocab() { return vocab; }

    public static void main(String[] args) {
        System.out.println("=== Tokenizer Demo ===");
        Tokenizer tokenizer = new Tokenizer();
        List<String> corpus = List.of(
            "the cat sat on the mat",
            "the dog sat on the log",
            "the cat and the dog"
        );
        tokenizer.buildVocab(corpus);
        System.out.println("Vocabulary size: " + tokenizer.getVocabSize());
        System.out.println("Vocab: " + tokenizer.getVocab());
        String text = "the cat sat";
        List<Integer> encoded = tokenizer.encode(text);
        System.out.println("Encode '" + text + "': " + encoded);
        System.out.println("Decode: " + tokenizer.decode(encoded));
        List<Integer> padded = tokenizer.truncatePad(encoded, 8);
        System.out.println("Truncate/pad to 8: " + padded);
    }
}
