package com.distributed.idgeneration;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class UlidGenerator implements IdGenerator<String> {
    private static final String ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final AtomicReference<byte[]> lastRand = new AtomicReference<>();
    private final AtomicLong lastTimestamp = new AtomicLong(-1);

    @Override
    public String generate() {
        long timestamp = System.currentTimeMillis();
        byte[] rand;
        long lastTs = lastTimestamp.get();

        if (timestamp == lastTs) {
            byte[] prev = lastRand.get();
            rand = increment(prev);
        } else {
            rand = new byte[10];
            RANDOM.nextBytes(rand);
        }

        lastTimestamp.set(timestamp);
        lastRand.set(rand);

        char[] chars = new char[26];
        encodeTimestamp(chars, timestamp);
        encodeRandom(chars, rand);
        return new String(chars);
    }

    @Override
    public long extractTimestamp(String ulid) {
        long ts = 0;
        for (int i = 0; i < 10; i++) {
            ts = ts * 32 + ENCODING.indexOf(ulid.charAt(i));
        }
        return ts;
    }

    private void encodeTimestamp(char[] chars, long timestamp) {
        long ts = timestamp;
        for (int i = 9; i >= 0; i--) {
            chars[i] = ENCODING.charAt((int)(ts % 32));
            ts /= 32;
        }
    }

    private void encodeRandom(char[] chars, byte[] rand) {
        long hi = ((rand[0] & 0xFFL) << 32) | ((rand[1] & 0xFFL) << 24)
                | ((rand[2] & 0xFFL) << 16) | ((rand[3] & 0xFFL) << 8) | (rand[4] & 0xFFL);
        long lo = ((rand[5] & 0xFFL) << 32) | ((rand[6] & 0xFFL) << 24)
                | ((rand[7] & 0xFFL) << 16) | ((rand[8] & 0xFFL) << 8) | (rand[9] & 0xFFL);
        for (int i = 15; i >= 10; i--) {
            chars[i + 10] = ENCODING.charAt((int)(lo % 32));
            lo /= 32;
        }
        for (int i = 9; i >= 0; i--) {
            chars[i + 10] = ENCODING.charAt((int)(hi % 32));
            hi /= 32;
        }
    }

    private byte[] increment(byte[] prev) {
        byte[] next = prev.clone();
        for (int i = 9; i >= 0; i--) {
            next[i]++;
            if (next[i] != 0) break;
        }
        return next;
    }
}
