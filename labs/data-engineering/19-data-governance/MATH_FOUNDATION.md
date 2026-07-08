# Math Foundation for Data Governance

## PII Coverage
```
PII_Coverage = IdentifiedPII / TotalPII * 100
Confidence = Average(confidence_score) across all columns
FalsePositiveRate = FalsePositives / TotalAlerts
```

## Access Control
```
EffectivePermissions = permissions_granted + inherited_permissions - restrictions
LeastPrivilegeScore = actual_permissions / required_permissions
Goal: Minimize LeastPrivilegeScore while maintaining functionality
```

## Compliance
```
ComplianceRate = CompliantAssets / TotalAssets * 100
AuditCoverage = AuditedData / TotalData * 100
RiskScore = Sensitivity * (1 - ComplianceRate) * ExposureFactor
```

## Retention
```
StorageUsed = SUM(data_by_age_bucket)
RetentionSavings = DataBeyondRetention * CostPerGB
DeletionImpact = DataDeletionRequests * AvgCostPerRequest
```
