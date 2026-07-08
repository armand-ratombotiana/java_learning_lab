package com.distributed.idgeneration;

import java.security.SecureRandom;
import java.util.UUID;

public class UuidV7Generator implements IdGenerator<UUID> {
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public UUID generate() {
        long timestamp = System.currentTimeMillis();
        long msb = (timestamp << 16) | 0x7000L;
        msb |= (RANDOM.nextLong() & 0x0FFFL) << 0;
        long lsb = RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL | 0x8000000000000000L;
        return new UUID(msb, lsb);
    }

    @Override
    public long extractTimestamp(UUID id) {
        return id.getMostSignificantBits() >>> 16;
    }
}
