package com.dsacademy.lab28.minhashsimhash;

import java.util.*;

public class LshIndex {

    private final int bands;
    private final int rowsPerBand;
    private final List<Map<Integer, List<String>>> bandTables;

    public LshIndex(int bands, int rowsPerBand) {
        this.bands = bands;
        this.rowsPerBand = rowsPerBand;
        this.bandTables = new ArrayList<>();
        for (int i = 0; i < bands; i++) {
            bandTables.add(new HashMap<>());
        }
    }

    public void insert(String docId, long fingerprint, int totalBits) {
        int rowsPerBand = totalBits / bands;
        if (rowsPerBand == 0) rowsPerBand = 1;
        for (int b = 0; b < bands; b++) {
            int bandHash = getBandHash(fingerprint, b, rowsPerBand);
            bandTables.get(b).computeIfAbsent(bandHash, k -> new ArrayList<>()).add(docId);
        }
    }

    public Set<String> query(long fingerprint, int totalBits) {
        int rowsPerBand = totalBits / bands;
        if (rowsPerBand == 0) rowsPerBand = 1;
        Set<String> candidates = new HashSet<>();
        for (int b = 0; b < bands; b++) {
            int bandHash = getBandHash(fingerprint, b, rowsPerBand);
            List<String> docs = bandTables.get(b).get(bandHash);
            if (docs != null) {
                candidates.addAll(docs);
            }
        }
        return candidates;
    }

    private int getBandHash(long fingerprint, int band, int rowsPerBand) {
        int start = band * rowsPerBand;
        int hashVal = 0;
        for (int i = 0; i < rowsPerBand && start + i < 64; i++) {
            int bit = (int)((fingerprint >> (start + i)) & 1);
            hashVal = (hashVal << 1) | bit;
        }
        return hashVal;
    }

    public int getBands() { return bands; }
}
