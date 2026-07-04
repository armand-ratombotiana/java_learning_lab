# Visual Guide

## ETL Flow
```
[Source DB] -> [Extract] -> [Staging] -> [Transform] -> [Load] -> [Warehouse]
```

## Transform Details
```
Raw Stage      Clean Stage       Enriched Stage
id:string      id:string         id:string
name:string -> name:string    -> name:string
amount:string  amount:decimal    amount:decimal
raw_data:json                   category:string
```
