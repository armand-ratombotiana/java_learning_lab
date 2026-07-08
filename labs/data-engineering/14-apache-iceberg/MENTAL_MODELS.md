# Mental Models for Apache Iceberg

## 1. The Library Catalog
Table metadata = library catalog card. Snapshot = edition of catalog. Manifest = shelf list. Data files = books on shelves. Partition evolution = reorganizing shelves without moving books.

## 2. The ZIP Archive
Partition evolution is like adding files to a ZIP without recompressing the whole archive. Old files stay in old format, new files follow new format. Extracting still works seamlessly.

## 3. The Time Capsule
Each snapshot is a time capsule. You can open any capsule to see the table as it was. Compaction is like reorganizing (and dating) the contents.
