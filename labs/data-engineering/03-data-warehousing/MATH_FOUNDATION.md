# Math Foundation

## Star Join Cost
Query = FactScan + DimLookups
FactScan = RowCount * RowSize / IORate

## Storage
Raw = sum(row sizes) * count * repl
Columnar compression = 3-10x reduction
Star schema: Fact ~60-80% of total
