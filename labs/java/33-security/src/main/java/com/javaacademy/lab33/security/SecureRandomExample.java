package com.javaacademy.lab33.security;

import java.security.*;
import java.util.Arrays;

public class SecureRandomExample {

    public byte[] generateRandomBytes(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public int generateRandomInt(int bound) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(bound);
    }

    public long generateRandomLong() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextLong();
    }

    public String generateSessionId() {
        byte[] bytes = generateRandomBytes(32);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public boolean isCryptographicallyRandom(byte[] bytes) {
        return bytes != null && bytes.length > 0 && !allZeros(bytes);
    }

    private boolean allZeros(byte[] bytes) {
        return Arrays.stream(bytes).allMatch(b -> b == 0);
    }
}
