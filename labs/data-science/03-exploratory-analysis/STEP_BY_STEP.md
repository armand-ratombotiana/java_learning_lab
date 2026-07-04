# Step-by-Step: EDA on a Customer Churn Dataset

**Goal**: Profile a telecom customer dataset to understand churn patterns before modeling.

## Step 1: Load and Scan

```java
Table data = Table.read().csv("telco_churn.csv");
System.out.println(data.shape());           // 7043 x 21
System.out.println(data.structure());
System.out.println(data.first(5));
System.out.println(data.missingCount());
```

**Findings**: 7043 rows, 21 columns. `TotalCharges` has 11 missing.

## Step 2: Target Variable Analysis

```java
Table churnDist = data.groupBy("Churn").count();
System.out.println(churnDist);
// Churn = Yes: 1869 (26.5%), Churn = No: 5174 (73.5%)
// Imbalanced but not extreme
```

## Step 3: Numeric Feature Distributions

```java
for (String col : Arrays.asList("tenure", "MonthlyCharges", "TotalCharges")) {
    DoubleColumn c = data.doubleColumn(col);
    // Filter out churn groups
    DoubleColumn churners = c.where(data.stringColumn("Churn").equals("Yes"));
    DoubleColumn nonChurners = c.where(data.stringColumn("Churn").equals("No"));
    
    System.out.println("=== " + col + " ===");
    System.out.printf("Churners:   mean=%.1f, median=%.1f%n",
        churners.mean(), churners.median());
    System.out.printf("Non-churn:  mean=%.1f, median=%.1f%n",
        nonChurners.mean(), nonChurners.median());
}
// Key finding: churners have much lower median tenure (10 months vs 38 months)
```

## Step 4: Categorical Feature Analysis

```java
// Contract type vs churn
Table contractChurn = data.groupBy("Contract").apply(
    table -> {
        double rate = table.where(table.stringColumn("Churn").equals("Yes")).rowCount()
                    / (double) table.rowCount();
        return DoubleColumn.create("churn_rate", rate);
    }
);
// Month-to-month: 42.7% churn
// One year: 11.3% churn
// Two year: 2.8% churn
```

## Step 5: Correlation Heatmap

```java
// Compute correlation matrix among numeric columns
List<String> numCols = data.numericColumnNames();
int n = numCols.size();
double[][] corrMatrix = new double[n][n];
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        double[] xi = data.doubleColumn(numCols.get(i)).asDoubleArray();
        double[] xj = data.doubleColumn(numCols.get(j)).asDoubleArray();
        corrMatrix[i][j] = Smile.correlation(xi, xj);
    }
}
// Note: tenure and TotalCharges are highly correlated (r ≈ 0.83)
```

## Step 6: Summary of Findings

```
Key Insights:
1. Churn concentrated in month-to-month customers (42.7% vs <10% for long-term)
2. No internet service → much lower churn
3. Fiber optic internet → higher churn
4. Electronic check payment → higher churn
5. Short tenure + high monthly charges = churn risk profile

Recommended Model Features:
tenure, contract_type, internet_service, payment_method, monthly_charges, senior_citizen
```
