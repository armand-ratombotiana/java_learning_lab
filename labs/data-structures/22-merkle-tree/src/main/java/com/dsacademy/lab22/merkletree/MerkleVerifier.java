package com.dsacademy.lab22.merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MerkleVerifier {

    private final MessageDigest digest;

    public MerkleVerifier() {
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public boolean verifyProof(String leafData, MerkleProof proof, String rootHash) {
        String hash = hash(leafData);
        int idx = proof.getLeafIndex();
        for (String siblingHash : proof.getAuditPath()) {
            if (idx % 2 == 0) {
                hash = hash(hash + siblingHash);
            } else {
                hash = hash(siblingHash + hash);
            }
            idx /= 2;
        }
        return hash.equals(rootHash);
    }

    private String hash(String input) {
        byte[] bytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
