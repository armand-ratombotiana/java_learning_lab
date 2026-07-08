# Apache Iceberg Architecture

```
[Engines: Spark, Flink, Trino, Hive, Presto, Dremio]
                         |
                  [Iceberg Catalog]
            (Hive/REST/Nessie/JDBC/Glue)
                         |
            [Table Metadata (JSON)]
                         |
           [Snapshot List] [Manifests]
                         |
            [Data Files: Parquet/Avro/ORC]
                         |
               [Object Storage]
            (S3/ADLS/GCS/HDFS)
```
