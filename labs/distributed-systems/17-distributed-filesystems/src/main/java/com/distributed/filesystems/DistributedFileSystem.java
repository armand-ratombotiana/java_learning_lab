package com.distributed.filesystems;

import java.io.InputStream;
import java.util.List;

public interface DistributedFileSystem {
    void write(String path, InputStream data, long size);
    InputStream read(String path);
    boolean delete(String path);
    List<FileInfo> list(String path);
    long getSize(String path);
    boolean exists(String path);
}
