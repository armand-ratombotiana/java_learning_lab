# Architecture

```
[Feature Registry]    [Online Store]     [Offline Store]
    (Metadata)          (Redis)         (S3/Delta)
       |                    |                |
       +--------[Feature Engineering]-------+
                        |
                   [Sources]
```
