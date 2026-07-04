# Internals: R2DBC

## SPI Architecture

```
ConnectionFactory (SPI)
    └→ Publisher<Connection>
         └→ Connection
              ├→ createStatement(sql) → Statement
              │    └→ bind(param, value)
              │    └→ execute() → Publisher<Result>
              ├→ beginTransaction() → Publisher<Void>
              ├→ commitTransaction() → Publisher<Void>
              ├→ rollbackTransaction() → Publisher<Void>
              └→ close() → Publisher<Void>

Result
    ├→ getRowsUpdated() → Publisher<Integer>
    └→ map(mappingFn) → Publisher<T>
```

## Key SPI Classes

| Interface | Purpose |
|---|---|
| `ConnectionFactory` | Entry point, creates connections |
| `ConnectionFactoryMetadata` | Database product name/version |
| `Statement` | Parameterized SQL execution |
| `Result` | Reactive query outcome |
| `Row` | Single row access by index/name |
| `RowMetadata` | Column metadata |
| `Batch` | Multiple statement execution |

## Driver Implementations
- `r2dbc-postgresql`: io.r2dbc.postgresql
- `r2dbc-h2`: io.r2dbc.h2
- `r2dbc-mysql`: dev.miku.r2dbc.mysql
- `r2dbc-mssql`: io.r2dbc.mssql
- `r2dbc-spi`: Oracle, MariaDB community drivers
