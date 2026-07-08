# Math Foundation for Data Catalog

## Coverage
```
CatalogedAssets = AssetsWithMetadata / TotalAssets
Coverage = CatalogedAssets / TotalDataAssets * 100
Goal: >90% coverage for trusted data platform
```

## Lineage Completeness
```
LineageCoverage = AssetsWithLineage / CatalogedAssets
ColumnLevelCoverage = ColsWithLineage / TotalColumns
Goal: 100% for critical data, 80% for all data
```

## Search Effectiveness
```
TimeToFind = Avg(query_execution_time + results_browsing)
ResultRelevance = ClickedResults / TotalResultsShown
ZeroResultRate = QueriesWithNoResults / TotalQueries
```

## Adoption
```
ActiveUsers = UsersWithRecentSearches / TotalDataUsers
WeeklyActiveRate = WeeklyActiveUsers / TotalUsers
AdoptionGoal: >60% weekly active users
```
