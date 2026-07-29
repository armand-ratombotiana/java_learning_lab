# Deep Dive: File Visitor & Watcher

## 1. Files.walkFileTree

Walks a file tree recursively using a `FileVisitor`:

```java
Path start = Path.of(".");

Files.walkFileTree(start, new SimpleFileVisitor<>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        System.out.println("File: " + file);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        System.out.println("Directory: " + dir);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println("Failed: " + file + " — " + exc.getMessage());
        return FileVisitResult.CONTINUE;  // continue despite error
    }
});
```

### FileVisitResult options:
- `CONTINUE` — keep walking
- `SKIP_SIBLINGS` — skip remaining siblings
- `SKIP_SUBTREE` — don't enter this directory
- `TERMINATE` — stop immediately

## 2. Practical Visitor: Delete Directory

```java
Files.walkFileTree(root, new SimpleFileVisitor<>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
});
```

## 3. WatchService

Monitor a directory for changes (create, modify, delete):

```java
try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
    Path dir = Path.of(".");
    dir.register(watcher,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE);
    
    while (true) {
        WatchKey key = watcher.take();  // blocks until event
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            Path filename = (Path) event.context();
            System.out.println(kind.name() + ": " + filename);
        }
        key.reset();  // important: must reset to continue watching
    }
}
```

## 4. Files.walk vs walkFileTree

| Method | Returns | Use |
|--------|---------|-----|
| `Files.walk(path)` | `Stream<Path>` | Functional, quick traversal |
| `Files.walkFileTree(path, visitor)` | `void` | Visitor pattern, fine-grained control |
