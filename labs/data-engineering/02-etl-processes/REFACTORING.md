# Refactoring ETL

## Before: Duplicated Logic
```java
public void runEtl1() { /* 200 lines */ }
public void runEtl2() { /* 190 similar lines */ }
```

## After: Abstract Base Class
```java
@Component
public abstract class BaseEtlJob {
    protected abstract Dataset<Row> extract();
    protected abstract Dataset<Row> transform(Dataset<Row>);
    protected abstract void load(Dataset<Row>);
    public void execute() { load(transform(extract())); }
}
```
