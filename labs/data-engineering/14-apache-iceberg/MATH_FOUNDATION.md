# Math Foundation for Apache Iceberg

## Metadata Size
```
ManifestSize = NumPartitions * NumDataFiles * StatsSizePerFile
MetadataOverhead = ManifestSizes + SnapshotMetadataJson
OptimalManifestSize = ~8MB (before requiring compaction)
```

## Partition Pruning
```
FilesScanned = TotalFiles * (1 - PartitionSelectivity)
PartitionSelectivity = FilteredPartitions / TotalPartitions
PruningEfficiency = 1 - FilesScanned/TotalFiles
```

## Snapshot Retention
```
Storage = SUM(snapshot_file_sets)
Savings = ExpiredSnapshots / TotalSnapshots * TotalStorage
RetentionCatch = ActiveSnapshots * ActiveFiles
```

## Compaction
```
TargetFileSize = 512MB (default)
CompactionReduction = OriginalFiles / (TotalBytes / TargetFileSize)
IO_Reduction = 1 - (FilesAfter / FilesBefore)
```
