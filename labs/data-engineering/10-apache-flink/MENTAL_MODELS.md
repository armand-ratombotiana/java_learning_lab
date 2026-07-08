# Mental Models for Apache Flink

## 1. The Assembly Line
Operators are stations on an assembly line. Events move down the line, being processed at each station. Watermarks are clock ticks telling stations how far the shift has progressed.

## 2. The Bookmark Analogy
Checkpoints are like bookmarking your page. If the book falls (failure), you resume from the bookmark. Savepoints are taking a photo for later reference.

## 3. The River and Dams
Data flows like a river. Windows are dams that collect water for a period, then release the accumulated measurement. Watermarks show how far upstream the river has been measured.
