package com.learning.blockchain.web3;

import java.security.*;
import java.util.*;

public class Web3Lab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Web3 Java Lab ===\n");

        System.out.println("1. Simple Blockchain:");
        Block genesis = new Block(0, "Genesis Block", "0");
        System.out.println("   Genesis: " + genesis.hash.substring(0, 16) + "...");

        Block block1 = new Block(1, "Transaction: Alice -> Bob: 5 ETH", genesis.hash);
        block1.mineBlock(4);
        System.out.println("   Block 1: " + block1.hash.substring(0, 16) + "... (nonce: " + block1.nonce + ")");

        Block block2 = new Block(2, "Transaction: Bob -> Charlie: 2 ETH", block1.hash);
        block2.mineBlock(4);
        System.out.println("   Block 2: " + block2.hash.substring(0, 16) + "... (nonce: " + block2.nonce + ")");

        System.out.println("\n2. Web3 Java Libraries:");
        System.out.println("   - web3j: Ethereum client library");
        System.out.println("   - ethers-java: Ethereum utilities");
        System.out.println("   - besu: Enterprise Ethereum client");

        System.out.println("\n=== Web3 Java Lab Complete ===");
    }

    static class Block {
        final int index;
        final long timestamp;
        final String data;
        final String previousHash;
        String hash;
        int nonce;

        Block(int index, String data, String previousHash) throws Exception {
            this.index = index;
            this.timestamp = System.currentTimeMillis();
            this.data = data;
            this.previousHash = previousHash;
            this.hash = calculateHash();
        }

        String calculateHash() throws Exception {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = index + timestamp + data + previousHash + nonce;
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        }

        void mineBlock(int difficulty) throws Exception {
            String target = "0".repeat(difficulty);
            while (!hash.substring(0, difficulty).equals(target)) {
                nonce++;
                hash = calculateHash();
            }
        }
    }
}