package com.distributed.filesystems;

import java.time.Instant;

public record FileInfo(String path, long size, Instant lastModified, boolean isDirectory) {
    @Override
    public String toString() {
        return (isDirectory ? "D " : "F ") + path + " (" + size + " bytes, " + lastModified + ")";
    }
}
