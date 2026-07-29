# Interview Questions: File Visitor & Watcher

## Company-Specific Focus

### Amazon
- Design a file indexing service using walkFileTree
- WatchService for S3 bucket sync agent

### Google
- walkFileTree vs Files.walk(): when is each appropriate?
- FileVisitor for incremental backup systems

### Microsoft
- WatchService vs .NET FileSystemWatcher comparison
- Polling-based vs event-based directory monitoring

### Meta
- WatchService on macOS vs Linux vs Windows: implementation differences
- Handling missed events in WatchService

### Oracle
- BasicFileAttributes and DosFileAttributes for platform-specific file metadata
- FileVisitOption.FOLLOW_LINKS security considerations

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 588 Design In-Memory File System | Hard | Amazon, Google | File tree traversal |

## Real Production Scenarios
1. **Hot reload**: WatchService for config/class file changes
2. **Log directory monitoring**: WatchService for new log file detection
3. **Build system**: FileVisitor for incremental compilation
