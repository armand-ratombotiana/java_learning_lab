# ETL Theory

## ELT vs ETL
| ETL | ELT |
|-----|-----|
| Transform before load | Load then transform |
| Staging area | Warehouse compute |
| Lower volume | Higher volume |
| Spark/Spring Batch | dbt/Snowflake |

## Extraction Strategies
- **Full**: Entire source each run
- **Incremental**: Changed data only
- **CDC**: Capture inserts, updates, deletes
