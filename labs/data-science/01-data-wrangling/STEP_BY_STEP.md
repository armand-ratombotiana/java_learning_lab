# Step-by-Step: Wrangling a Customer Dataset

**Scenario**: You receive `customers.csv` from three merged sources. It has 50,000 rows and 22 columns. Walk through the wrangling.

## Step 1: Load and Profile

```java
Table raw = Table.read().csv("customers.csv");
System.out.println(raw.shape());                // 50000 x 22
System.out.println(raw.structure());            // column names + types
System.out.println(raw.missingCount());         // shows missing per column
```

### Expected discovery:
- `age`: 2000 missing, 50 values > 120
- `email`: 500 missing, 300 with no "@"
- `salary`: 8000 missing, some stored as strings ("90,000")
- `signup_date`: 3 different date formats
- No unique ID — need to deduplicate on `email + name`

## Step 2: Fix Types

```java
// Parse salary strings to doubles
StringColumn salaryRaw = raw.stringColumn("salary");
DoubleColumn salaryClean = salaryRaw.replaceAll("[^\\d.]", "")
    .asDoubleColumn("salary_clean");
raw.replaceColumn("salary", salaryClean);

// Unify date formats
DateTimeFormatter[] fmts = {
    DateTimeFormatter.ofPattern("MM/dd/yyyy"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    DateTimeFormatter.ofPattern("MMM d, yyyy")
};
LocalDateColumn dates = LocalDateColumn.create("parsed_date", raw.rowCount());
for (int i = 0; i < raw.rowCount(); i++) {
    String val = raw.getString(i, "signup_date");
    for (DateTimeFormatter fmt : fmts) {
        try { dates.set(i, LocalDate.parse(val, fmt)); break; }
        catch (Exception ignored) { }
    }
}
raw.replaceColumn("signup_date", dates);
```

## Step 3: Handle Missing Values

```java
// Impute age with median
double medianAge = raw.doubleColumn("age").median();
raw.doubleColumn("age").setMissingTo(medianAge);

// Drop rows where email is missing (critical column)
raw = raw.where(raw.stringColumn("email").isNotMissing());

// Fill salary with group median by state
for (String state : raw.stringColumn("state").unique()) {
    Selection inState = raw.stringColumn("state").equalsIgnoreCase(state);
    DoubleColumn salaries = raw.doubleColumn("salary").where(inState);
    double groupMedian = salaries.median();
    // Apply to missing rows in that state
    // (implementation depends on Tablesaw API version)
}
```

## Step 4: Remove Duplicates

```java
int before = raw.rowCount();
raw = raw.sortOn("signup_date");  // keep earliest entry
raw = raw.dropDuplicateRows("email", "full_name");
int after = raw.rowCount();
System.out.println("Removed " + (before - after) + " duplicates");
```

## Step 5: Validate and Export

```java
assert raw.doubleColumn("age").min() >= 0  : "Negative ages found";
assert raw.doubleColumn("salary").min() >= 0 : "Negative salaries found";
assert raw.rowCount() > 40000 : "Too many rows dropped";

raw.write().csv("customers_clean.csv");
```
