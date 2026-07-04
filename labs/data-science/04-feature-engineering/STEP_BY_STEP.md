# Step-by-Step: Feature Engineering for House Price Prediction

**Goal**: Engineer features from a raw real estate dataset to predict sale price.

## Step 1: Load and Inspect

```java
Table raw = Table.read().csv("houses.csv");
System.out.println(raw.shape());  // 1460 x 81
```

## Step 2: Handle Missing Values Strategically

```java
// For numeric: missing often means "none" (e.g., no basement)
raw.doubleColumn("LotFrontage").setMissingTo(raw.doubleColumn("LotFrontage").median());
raw.doubleColumn("MasVnrArea").setMissingTo(0.0);
```

## Step 3: Create Transformations

```java
// Log transform skewed numeric features
raw.addColumn("log_lot_area", logTransform(raw.doubleColumn("LotArea")));
raw.addColumn("log_sale_price", logTransform(raw.doubleColumn("SalePrice")));

// Total square footage
raw.addColumn("total_sf", raw.doubleColumn("TotalBsmtSF")
    .add(raw.doubleColumn("1stFlrSF"))
    .add(raw.doubleColumn("2ndFlrSF")));

// Age at sale
raw.addColumn("age_at_sale", raw.doubleColumn("YrSold")
    .subtract(raw.doubleColumn("YearBuilt")));

// Remodel indicator
raw.addColumn("has_remodel", raw.doubleColumn("YearRemodAdd")
    .isGreaterThan(raw.doubleColumn("YearBuilt")));
```

## Step 4: Encode Categorical Variables

```java
// Ordinal encoding for ordered categories
Map<String, Integer> qualityMap = new HashMap<>();
qualityMap.put("Ex", 5); qualityMap.put("Gd", 4);
qualityMap.put("TA", 3); qualityMap.put("Fa", 2); qualityMap.put("Po", 1);

IntColumn overallQual = IntColumn.create("OverallQual_encoded", raw.rowCount());
for (int i = 0; i < raw.rowCount(); i++) {
    overallQual.set(i, qualityMap.getOrDefault(raw.getString(i, "OverallQual"), 0));
}
raw.addColumn(overallQual);
// Drop original string columns after encoding
```

## Step 5: Create Interaction Features

```java
// Interaction: overall quality × total SF
raw.addColumn("qual_x_sf", raw.doubleColumn("OverallQual_encoded")
    .multiply(raw.doubleColumn("total_sf")));

// Neighborhood × zone (if sparse, keep only top 10 neighborhoods)
```

## Step 6: Scale Numeric Features

```java
StandardScaler scaler = new StandardScaler();
scaler.fit(raw.selectColumns("total_sf", "LotArea", "age_at_sale", "log_lot_area"));
Table scaled = scaler.transform(raw);
```

## Step 7: Final Feature Set

```
Final features (42): log_sale_price (target), total_sf, age_at_sale,
has_remodel, OverallQual_encoded, qual_x_sf, log_lot_area,
Neighborhood_encoded, BldgType_encoded, GarageCars, Fireplaces,
BsmtUnfSF, ... + 30 more
```
