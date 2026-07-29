# Mock Interview Transcript: File Visitor & Watcher

## Interviewer: Senior SWE, Microsoft
## Candidate: Mid-level Java developer
## Time: 25 minutes
## Focus: File tree traversal, directory monitoring, WatchService

---

**Q1: How do you recursively traverse a directory tree in Java?**

**Candidate**: NIO.2 offers two approaches: `Files.walkFileTree()` with a `FileVisitor` callback, and `Files.walk()` returning a `Stream<Path>`. `walkFileTree` gives fine-grained control with `preVisitDirectory`, `visitFile`, `visitFileFailed`, and `postVisitDirectory` callbacks. `Files.walk()` is more functional but offers less control over individual events.

**Interviewer**: How do you handle permission errors during tree traversal?

**Candidate**: Override `visitFileFailed()` in `SimpleFileVisitor`. Return `FileVisitResult.CONTINUE` to skip the problematic file and continue. For directories, override `preVisitDirectory()` and catch `AccessDeniedException` — return `SKIP_SUBTREE` to avoid entering inaccessible directories.

**Interviewer**: How does WatchService work?

**Candidate**: `WatchService` uses OS-native file system events. A `Watchable` (like `Path`) registers with the service for specific event types (`ENTRY_CREATE`, `ENTRY_MODIFY`, `ENTRY_DELETE`). The service returns `WatchKey` instances via `take()` (blocking) or `poll()` (non-blocking). Each key holds a list of events. After processing, call `key.reset()` to continue watching.

**Interviewer**: What are the limitations of WatchService?

**Candidate**: (1) It only watches immediate children of a directory, not the subtree. You must manually register each subdirectory. (2) Events can be coalesced or dropped under heavy load — don't rely on every event. (3) On macOS, the default implementation polls, so there's latency. (4) It doesn't report which file changed in `ENTRY_MODIFY` on some platforms — just the directory. (5) There's no recursive watching; you need to watch each subdirectory separately.

**Interviewer**: Design a file synchronization tool that watches a directory and syncs changes.

**Candidate**: I'd use a `WatchService` that recursively registers all directories. On creation of a new subdirectory, register it too. On file changes, compute the diff or copy the file to the sync target. For initial sync, `walkFileTree` to list all files. Handle edge cases: temp files (watch for rename events), large files (wait for WRITE events to settle), and deletion races.
