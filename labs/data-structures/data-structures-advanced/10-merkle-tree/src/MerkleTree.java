package com.ds.advanced.lab10;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MerkleTree {
    private Node root;

    private static class Node {
        String hash;
        Node left, right;
        Node(String hash) { this.hash = hash; }
    }

    public MerkleTree(List<String> data) {
        if (data == null || data.isEmpty()) return;
        List<Node> leaves = new ArrayList<>();
        for (String d : data) leaves.add(new Node(hash(d)));
        root = build(leaves);
    }

    private Node build(List<Node> nodes) {
        if (nodes.size() == 1) return nodes.get(0);
        List<Node> parents = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += 2) {
            Node left = nodes.get(i);
            Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : nodes.get(i);
            Node parent = new Node(hash(left.hash + right.hash));
            parent.left = left;
            parent.right = right;
            parents.add(parent);
        }
        return build(parents);
    }

    public String rootHash() { return root == null ? "" : root.hash; }

    public boolean verify(List<String> data) {
        MerkleTree other = new MerkleTree(data);
        return rootHash().equals(other.rootHash());
    }

    public List<String> getProof(String leafData) {
        String targetHash = hash(leafData);
        List<String> proof = new ArrayList<>();
        findProof(root, targetHash, proof);
        return proof;
    }

    private boolean findProof(Node node, String target, List<String> proof) {
        if (node == null) return false;
        if (node.left == null && node.right == null) return node.hash.equals(target);
        if (node.left != null && findProof(node.left, target, proof)) {
            proof.add("R:" + (node.right != null ? node.right.hash : node.left.hash));
            return true;
        }
        if (node.right != null && findProof(node.right, target, proof)) {
            proof.add("L:" + (node.left != null ? node.left.hash : node.right.hash));
            return true;
        }
        return false;
    }

    private static String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }
}