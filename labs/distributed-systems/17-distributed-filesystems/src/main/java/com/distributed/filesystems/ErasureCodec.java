package com.distributed.filesystems;

import java.util.Arrays;

public class ErasureCodec {
    private final int dataFragments;
    private final int parityFragments;
    private final int totalFragments;

    public ErasureCodec(int dataFragments, int parityFragments) {
        this.dataFragments = dataFragments;
        this.parityFragments = parityFragments;
        this.totalFragments = dataFragments + parityFragments;
    }

    public byte[][] encode(byte[] data) {
        int fragmentSize = (int) Math.ceil((double) data.length / dataFragments);
        byte[][] fragments = new byte[totalFragments][fragmentSize];
        for (int i = 0; i < dataFragments; i++) {
            int offset = i * fragmentSize;
            int len = Math.min(fragmentSize, data.length - offset);
            if (len > 0) {
                System.arraycopy(data, offset, fragments[i], 0, len);
            }
        }
        for (int i = 0; i < parityFragments; i++) {
            fragments[dataFragments + i] = new byte[fragmentSize];
            for (int j = 0; j < fragmentSize; j++) {
                byte xor = 0;
                for (int k = 0; k < dataFragments; k++) {
                    xor ^= fragments[k][j];
                }
                fragments[dataFragments + i][j] = xor;
            }
        }
        return fragments;
    }

    public byte[] decode(byte[][] fragments, boolean[] available) {
        int availableCount = 0;
        for (boolean a : available) if (a) availableCount++;
        if (availableCount < dataFragments) {
            throw new IllegalStateException("Not enough fragments: " + availableCount + " < " + dataFragments);
        }
        int fragmentSize = fragments[0].length;
        byte[] result = new byte[dataFragments * fragmentSize];
        for (int i = 0; i < dataFragments; i++) {
            int idx = findAvailable(available, i);
            System.arraycopy(fragments[idx], 0, result, i * fragmentSize, fragmentSize);
        }
        int lastNonNull = result.length - 1;
        while (lastNonNull >= 0 && result[lastNonNull] == 0) lastNonNull--;
        return Arrays.copyOf(result, lastNonNull + 1);
    }

    private int findAvailable(boolean[] available, int start) {
        for (int i = start; i < available.length; i++) {
            if (available[i]) return i;
        }
        throw new IllegalStateException("No available fragment found starting at " + start);
    }

    public int getDataFragments() { return dataFragments; }
    public int getParityFragments() { return parityFragments; }
    public double getStorageOverhead() { return (double) totalFragments / dataFragments; }
}
