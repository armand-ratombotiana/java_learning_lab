package com.stream.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class LogSegment {

    private static final int MAX_SEGMENT_SIZE = 1024 * 1024 * 1024;
    private final String segmentFile;
    private final long baseOffset;
    private long nextOffset;
    private final List<Record> records;
    private final Path filePath;

    public LogSegment(String topic, int partition, long baseOffset, Path dataDir) throws IOException {
        this.baseOffset = baseOffset;
        this.nextOffset = baseOffset;
        this.records = new ArrayList<>();
        this.segmentFile = String.format("%s-%d-%d.log", topic, partition, baseOffset);
        this.filePath = dataDir.resolve(segmentFile);

        Files.createFile(filePath);
        log.info("Created log segment: {}", filePath);
    }

    public synchronized AppendResult append(byte[] key, byte[] value) {
        if (size() > MAX_SEGMENT_SIZE) {
            return new AppendResult(-1, false);
        }

        Record record = new Record(nextOffset, key, value, System.currentTimeMillis());
        records.add(record);

        try {
            writeToFile(record);
        } catch (IOException e) {
            log.error("Failed to write record to disk", e);
            return new AppendResult(-1, false);
        }

        long offset = nextOffset;
        nextOffset++;
        return new AppendResult(offset, true);
    }

    private void writeToFile(Record record) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile(), true)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeLong(record.offset());
            dos.writeLong(record.timestamp());
            dos.writeInt(record.key() != null ? record.key().length : -1);
            if (record.key() != null) {
                dos.write(record.key());
            }
            dos.writeInt(record.value().length);
            dos.write(record.value());

            byte[] data = baos.toByteArray();
            dos.writeInt(data.length);
            fos.write(data);
        }
    }

    public List<Record> read(long startOffset, int maxRecords) {
        List<Record> result = new ArrayList<>();
        for (Record record : records) {
            if (record.offset() >= startOffset && result.size() < maxRecords) {
                result.add(record);
            }
            if (result.size() >= maxRecords) break;
        }
        return result;
    }

    public long size() {
        return records.size();
    }

    public long baseOffset() {
        return baseOffset;
    }

    public long nextOffset() {
        return nextOffset;
    }

    public record Record(long offset, byte[] key, byte[] value, long timestamp) {}
    public record AppendResult(long offset, boolean success) {}
}