package com.dsacademy.lab22.merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

public class MerkleTree {

    private final List<String> leaves;
    private final List<List<String>> levels;
    private String rootHash;
    private final MessageDigest digest;

    public MerkleTree(List<String> data) {
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
        this.leaves = new ArrayList<>(data);
        this.levels = new ArrayList<>();
        buildTree();
    }

    private void buildTree() {
        levels.clear();
        List<String> currentLevel = new ArrayList<>();
        for (String leaf : leaves) {
            currentLevel.add(hash(leaf));
        }
        levels.add(new ArrayList<>(currentLevel));
        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;
                nextLevel.add(hash(left + right));
            }
            levels.add(nextLevel);
            currentLevel = nextLevel;
        }
        rootHash = currentLevel.isEmpty() ? "" : currentLevel.get(0);
    }

    private String hash(String input) {
        byte[] bytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String getRootHash() {
        return rootHash;
    }

    public int getLeafCount() {
        return leaves.size();
    }

    public MerkleProof generateProof(int leafIndex) {
        if (leafIndex < 0 || leafIndex >= leaves.size()) {
            throw new IndexOutOfBoundsException("Leaf index out of range");
        }
        List<String> auditPath = new ArrayList<>();
        int idx = leafIndex;
        for (int level = 0; level < levels.size() - 1; level++) {
            List<String> currentLevel = levels.get(level);
            int siblingIdx = (idx % 2 == 0) ? idx + 1 : idx - 1;
            if (siblingIdx < currentLevel.size()) {
                auditPath.add(currentLevel.get(siblingIdx));
            } else {
                auditPath.add(currentLevel.get(idx));
            }
            idx /= 2;
        }
        return new MerkleProof(leafIndex, auditPath, leaves.size());
    }
}
