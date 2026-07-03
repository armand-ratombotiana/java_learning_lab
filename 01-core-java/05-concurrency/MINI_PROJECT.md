# Module 05: Concurrency - Mini Project

**Project Name**: Multithreaded Web Scraper / Downloader  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Apply Java Concurrency APIs (Threads, `ExecutorService`, `Future`, and synchronized collections) to build an application that processes multiple network/I/O tasks concurrently, significantly speeding up execution compared to a single-threaded approach.

## 📝 Requirements

### Core Features
1. **URL Processing**:
   - Given a list of 20-30 dummy URLs (or local file paths representing images/documents).
   - Simulate "downloading" the file by making the thread sleep for a random time between 500ms and 2000ms.

2. **Thread Pool Management**:
   - Create a fixed thread pool using `Executors.newFixedThreadPool(int nThreads)`. Allow the user to specify the number of threads (e.g., 5).
   - Submit each download task to the `ExecutorService` using `Callable<String>` (returning a success or failure status).

3. **Tracking Progress (Futures)**:
   - Collect the returned `Future<String>` objects in a list.
   - Wait for all tasks to complete and print the result of each download.

4. **Thread-Safe Shared State**:
   - Maintain a global counter of total bytes downloaded (simulate random byte sizes for each download).
   - Use an `AtomicLong` OR a `synchronized` block to ensure multiple threads updating the counter simultaneously do not cause race conditions.

5. **Graceful Shutdown**:
   - Ensure the `ExecutorService` is properly shut down after all tasks are submitted and completed (`shutdown()` and `awaitTermination()`).

---

## 💡 Solution Blueprint

1. Create a `DownloadTask implements Callable<DownloadResult>` class.
2. Inside `call()`, simulate the delay, determine the "file size", safely add to the shared `AtomicLong totalBytes`, and return a `DownloadResult` object.
3. In `main()`, instantiate the `ExecutorService`.
4. Loop through the URLs, calling `executor.submit(new DownloadTask(url))` and saving the `Future`s.
5. Loop through the `Future`s, calling `future.get()` (this blocks until the specific thread finishes).
6. Print the results and the final value of `totalBytes`.
7. Call `executor.shutdown()`.