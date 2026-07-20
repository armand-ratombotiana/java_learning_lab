package com.capstone.kafka;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class LogSegment {
    private final String topic;
    private final int partition;
    private final long baseOffset;
    private final Path segmentPath;
    private final List<LogEntry> entries = new CopyOnWriteArrayList<>();
    private final AtomicLong nextOffset;

    public record LogEntry(long offset, byte[] key, byte[] value, long timestamp, int size) {}

    public LogSegment(String topic, int partition, long baseOffset, Path storagePath) {
        this.topic = topic;
        this.partition = partition;
        this.baseOffset = baseOffset;
        this.nextOffset = new AtomicLong(baseOffset);
        String fileName = String.format("%s-%d-%020d.log", topic, partition, baseOffset);
        this.segmentPath = storagePath.resolve(fileName);
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create storage dir", e);
        }
    }

    public long append(byte[] key, byte[] value) {
        long offset = nextOffset.getAndIncrement();
        LogEntry entry = new LogEntry(offset, key, value, System.currentTimeMillis(),
            (key != null ? key.length : 0) + (value != null ? value.length : 0) + 32);
        entries.add(entry);
        return offset;
    }

    public List<LogEntry> read(long offset, int maxEntries) {
        int startIdx = (int)(offset - baseOffset);
        if (startIdx < 0 || startIdx >= entries.size()) return List.of();
        int endIdx = Math.min(startIdx + maxEntries, entries.size());
        return List.copyOf(entries.subList(startIdx, endIdx));
    }

    public boolean containsOffset(long offset) {
        return offset >= baseOffset && offset < nextOffset.get();
    }

    public long getBaseOffset() { return baseOffset; }

    public long getNextOffset() { return nextOffset.get(); }

    public int entryCount() { return entries.size(); }

    public long sizeBytes() {
        return entries.stream().mapToLong(LogEntry::size).sum();
    }

    public boolean isActive() {
        return nextOffset.get() - baseOffset < 1024 * 1024 * 100;
    }

    public void flushToDisk() {
        try {
            Path dataFile = segmentPath;
            try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                    Files.newOutputStream(dataFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)))) {
                for (LogEntry entry : entries) {
                    dos.writeLong(entry.offset());
                    dos.writeLong(entry.timestamp());
                    writeBytes(dos, entry.key());
                    writeBytes(dos, entry.value());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to flush segment", e);
        }
    }

    private void writeBytes(DataOutputStream dos, byte[] data) throws IOException {
        if (data == null) { dos.writeInt(-1); return; }
        dos.writeInt(data.length);
        dos.write(data);
    }

    public void clear() { entries.clear(); nextOffset.set(baseOffset); }
}
