package com.algo.lab40;

import java.util.*;

/**
 * Huffman coding for lossless compression. Builds a binary tree based on
 * character frequencies, assigning shorter codes to more frequent characters.
 * Supports encoding and decoding of strings using the generated prefix codes.
 */
public class HuffmanCoding {
    private final Map<Character, String> encodingMap = new HashMap<>();
    private Node root;
    private String text;

    private static class Node implements Comparable<Node> {
        char ch; int freq; Node left, right;
        Node(char ch, int freq) { this.ch = ch; this.freq = freq; }
        Node(int freq, Node left, Node right) { this.freq = freq; this.left = left; this.right = right; }
        boolean isLeaf() { return left == null && right == null; }
        public int compareTo(Node o) { return Integer.compare(this.freq, o.freq); }
    }

    public void buildTree(String text) {
        this.text = text;
        Map<Character, Integer> freq = new HashMap<>();
        for (char ch : text.toCharArray()) freq.merge(ch, 1, Integer::sum);

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (var e : freq.entrySet()) pq.add(new Node(e.getKey(), e.getValue()));

        if (pq.size() == 1) {
            root = new Node(pq.poll().freq, new Node(pq.peek().ch, 0), null);
            encodingMap.clear();
            buildEncoding(root, "");
            return;
        }

        while (pq.size() > 1) {
            Node left = pq.poll(), right = pq.poll();
            pq.add(new Node(left.freq + right.freq, left, right));
        }
        root = pq.poll();
        encodingMap.clear();
        buildEncoding(root, "");
    }

    private void buildEncoding(Node node, String code) {
        if (node.isLeaf()) { encodingMap.put(node.ch, code); return; }
        if (node.left != null) buildEncoding(node.left, code + "0");
        if (node.right != null) buildEncoding(node.right, code + "1");
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) sb.append(encodingMap.get(ch));
        return sb.toString();
    }

    public String decode(String encoded) {
        StringBuilder sb = new StringBuilder();
        Node cur = root;
        for (char bit : encoded.toCharArray()) {
            cur = bit == '0' ? cur.left : cur.right;
            if (cur.isLeaf()) { sb.append(cur.ch); cur = root; }
        }
        return sb.toString();
    }

    public double compressionRatio() {
        int originalBits = text.length() * 16;
        int compressedBits = encode().length();
        return (double) compressedBits / originalBits;
    }

    public Map<Character, String> getEncodingMap() { return encodingMap; }
}
