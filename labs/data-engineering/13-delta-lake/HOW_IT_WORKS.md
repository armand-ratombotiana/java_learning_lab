# How Delta Lake Works

1. When writing, Delta reads latest version of transaction log
2. Determines files to add or remove based on operation
3. Writes new commit JSON to _delta_log/ atomically
4. If conflict detected (another writer committed), retry with latest
5. Readers read latest version and construct file list from log
6. Time travel: reconstruct file list from historical version
7. Optimize: rewrite small files into larger ones, update log
8. Vacuum: delete files unreferenced by any version within retention
