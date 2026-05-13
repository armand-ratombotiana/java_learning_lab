# Data Wrangling: Code Deep Dive

## 1. DataFrame Implementation

### Core DataFrame Class
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataFrame {
    private final List<String> columnNames;
    private final Map<String, Column> columns;
    private int rowCount;

    public DataFrame(List<String> columnNames) {
        this.columnNames = columnNames;
        this.columns = new LinkedHashMap<>();
        this.rowCount = 0;
        columnNames.forEach(name -> columns.put(name, new Column(name)));
    }

    public int[] shape() {
        return new int[]{rowCount, columnNames.size()};
    }

    public List<String> columns() {
        return new ArrayList<>(columnNames);
    }

    public Column column(String name) {
        return columns.get(name);
    }

    public DataFrame where(Predicate<Row> condition) {
        DataFrame result = new DataFrame(columnNames);
        for (int i = 0; i < rowCount; i++) {
            Row row = getRow(i);
            if (condition.test(row)) {
                result.addRow(row);
            }
        }
        return result;
    }

    public DataFrame select(String... colNames) {
        DataFrame result = new DataFrame(Arrays.asList(colNames));
        for (int i = 0; i < rowCount; i++) {
            Row row = getRow(i);
            Map<String, Object> values = new LinkedHashMap<>();
            for (String name : colNames) {
                values.put(name, row.get(name));
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    public DataFrame addColumn(String name, Function<Row, ?> fn) {
        DataFrame result = new DataFrame(new ArrayList<>(columnNames) {{ add(name); }});
        for (int i = 0; i < rowCount; i++) {
            Row row = getRow(i);
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            values.put(name, fn.apply(row));
            result.addRow(new Row(values));
        }
        return result;
    }

    public DataFrame groupBy(String... colNames) {
        return new GroupedDataFrame(this, Arrays.asList(colNames));
    }

    public DataFrame sortOn(String colName, SortOrder order) {
        DataFrame result = new DataFrame(columnNames);
        List<Row> rows = IntStream.range(0, rowCount)
            .mapToObj(this::getRow)
            .sorted((r1, r2) -> {
                Comparable v1 = (Comparable) r1.get(colName);
                Comparable v2 = (Comparable) r2.get(colName);
                return order == SortOrder.ASCENDING ? 
                    v1.compareTo(v2) : v2.compareTo(v1);
            })
            .collect(Collectors.toList());
        rows.forEach(result::addRow);
        return result;
    }

    public DataFrame dropDuplicateRows() {
        Set<String> seen = new HashSet<>();
        DataFrame result = new DataFrame(columnNames);
        for (int i = 0; i < rowCount; i++) {
            Row row = getRow(i);
            String key = row.toString();
            if (!seen.contains(key)) {
                seen.add(key);
                result.addRow(row);
            }
        }
        return result;
    }

    public DataFrame fillNA(Object value) {
        DataFrame result = new DataFrame(columnNames);
        for (int i = 0; i < rowCount; i++) {
            Row row = getRow(i);
            Map<String, Object> values = new LinkedHashMap<>();
            for (String col : columnNames) {
                Object val = row.get(col);
                values.put(col, val == null ? value : val);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    public void addRow(Row row) {
        rowCount++;
        for (String col : columnNames) {
            columns.get(col).add(row.get(col));
        }
    }

    public Row getRow(int index) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (String col : columnNames) {
            values.put(col, columns.get(col).get(index));
        }
        return new Row(values);
    }

    public List<Row> rows() {
        return IntStream.range(0, rowCount).mapToObj(this::getRow).collect(Collectors.toList());
    }
}

class Row {
    private final Map<String, Object> values;

    public Row(Map<String, Object> values) {
        this.values = values;
    }

    public Object get(String column) {
        return values.get(column);
    }

    public Map<String, Object> getValues() {
        return new LinkedHashMap<>(values);
    }

    @Override
    public String toString() {
        return values.values().stream()
            .map(v -> v == null ? "null" : v.toString())
            .collect(Collectors.joining(","));
    }
}

class Column {
    private final String name;
    private final List<Object> data;

    public Column(String name) {
        this.name = name;
        this.data = new ArrayList<>();
    }

    public void add(Object value) {
        data.add(value);
    }

    public Object get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }

    public Optional<Object> min() {
        return data.stream()
            .filter(Objects::nonNull)
            .map(o -> (Comparable) o)
            .min(Comparable::compareTo);
    }

    public Optional<Object> max() {
        return data.stream()
            .filter(Objects::nonNull)
            .map(o -> (Comparable) o)
            .max(Comparable::compareTo);
    }

    public OptionalDouble mean() {
        return data.stream()
            .filter(Objects::nonNull)
            .filter(o -> o instanceof Number)
            .mapToDouble(o -> ((Number) o).doubleValue())
            .average();
    }

    public OptionalDouble std() {
        OptionalDouble mean = mean();
        if (mean.isEmpty()) return OptionalDouble.empty();
        double m = mean.getAsDouble();
        double variance = data.stream()
            .filter(Objects::nonNull)
            .filter(o -> o instanceof Number)
            .mapToDouble(o -> ((Number) o).doubleValue())
            .map(v -> Math.pow(v - m, 2))
            .average()
            .orElse(0);
        return OptionalDouble.of(Math.sqrt(variance));
    }
}

enum SortOrder { ASCENDING, DESCENDING }
```

## 2. CSV Parser Implementation

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CSVParser {
    public static DataFrame readCSV(String path) {
        return readCSV(path, new CSVOptions());
    }

    public static DataFrame readCSV(String path, CSVOptions options) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            if (lines.isEmpty()) return new DataFrame(new ArrayList<>());

            String delimiter = options.delimiter;
            boolean hasHeader = options.hasHeader;

            List<String> headers = parseLine(lines.get(0), delimiter);
            DataFrame df = new DataFrame(headers);

            int startIndex = hasHeader ? 1 : 0;
            for (int i = startIndex; i < lines.size(); i++) {
                List<String> values = parseLine(lines.get(i), delimiter);
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    String value = j < values.size() ? values.get(j) : "";
                    rowMap.put(headers.get(j), convertValue(value, options));
                }
                df.addRow(new Row(rowMap));
            }
            return df;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV: " + e.getMessage(), e);
        }
    }

    private static List<String> parseLine(String line, String delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }

    private static Object convertValue(String value, CSVOptions options) {
        if (value.isEmpty() || value.equals("null") || value.equals("NA")) {
            return null;
        }
        try {
            if (options.inferTypes) {
                if (value.matches("-?\\d+")) return Long.parseLong(value);
                if (value.matches("-?\\d+\\.\\d+")) return Double.parseDouble(value);
                if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return LocalDate.parse(value);
                }
            }
        } catch (Exception e) {}
        return value;
    }

    public static void writeCSV(DataFrame df, String path) {
        writeCSV(df, path, new CSVOptions());
    }

    public static void writeCSV(DataFrame df, String path, CSVOptions options) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            String delimiter = options.delimiter;
            List<String> headers = df.columns();
            writer.println(String.join(delimiter, headers));

            for (Row row : df.rows()) {
                List<String> values = headers.stream()
                    .map(h -> row.get(h) == null ? "" : row.get(h).toString())
                    .map(v -> v.contains(delimiter) || v.contains("\"") ? 
                        "\"" + v.replace("\"", "\"\"") + "\"" : v)
                    .collect(Collectors.toList());
                writer.println(String.join(delimiter, values));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV: " + e.getMessage(), e);
        }
    }
}

class CSVOptions {
    String delimiter = ",";
    boolean hasHeader = true;
    boolean inferTypes = true;

    public CSVOptions delimiter(String d) { this.delimiter = d; return this; }
    public CSVOptions hasHeader(boolean h) { this.hasHeader = h; return this; }
    public CSVOptions inferTypes(boolean i) { this.inferTypes = i; return this; }
}
```

## 3. DataFrame Operations Deep Dive

```java
public class DataFrameOps {
    public static DataFrame filter(DataFrame df, String col, String op, Object value) {
        Column column = df.column(col);
        DataFrame result = new DataFrame(df.columns());
        
        for (int i = 0; i < column.size(); i++) {
            Object val = column.get(i);
            boolean include = false;
            
            switch (op) {
                case ">": include = compare(val, value) > 0; break;
                case "<": include = compare(val, value) < 0; break;
                case ">=": include = compare(val, value) >= 0; break;
                case "<=": include = compare(val, value) <= 0; break;
                case "==": include = Objects.equals(val, value); break;
                case "!=": include = !Objects.equals(val, value); break;
            }
            
            if (include) {
                result.addRow(df.getRow(i));
            }
        }
        return result;
    }

    private static int compare(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        if (a instanceof Comparable && b instanceof Comparable) {
            return ((Comparable) a).compareTo(b);
        }
        return 0;
    }

    public static DataFrame join(DataFrame left, DataFrame right, 
                                 String leftKey, String rightKey, JoinType type) {
        List<String> leftCols = left.columns();
        List<String> rightCols = right.columns().stream()
            .filter(c -> !c.equals(rightKey))
            .collect(Collectors.toList());

        DataFrame result = new DataFrame(Stream.concat(leftCols.stream(), rightCols.stream())
            .collect(Collectors.toList()));

        Map<Object, List<Row>> rightIndex = new HashMap<>();
        for (Row row : right.rows()) {
            rightIndex.computeIfAbsent(row.get(rightKey), k -> new ArrayList<>()).add(row);
        }

        Set<Row> matched = new HashSet<>();

        for (Row leftRow : left.rows()) {
            Object key = leftRow.get(leftKey);
            List<Row> matches = rightIndex.getOrDefault(key, Collections.emptyList());
            
            if (matches.isEmpty()) {
                if (type == JoinType.LEFT || type == JoinType.OUTER) {
                    Map<String, Object> values = new LinkedHashMap<>();
                    leftCols.forEach(c -> values.put(c, leftRow.get(c)));
                    rightCols.forEach(c -> values.put(c, null));
                    result.addRow(new Row(values));
                }
            } else {
                for (Row rightRow : matches) {
                    matched.add(rightRow);
                    Map<String, Object> values = new LinkedHashMap<>();
                    leftCols.forEach(c -> values.put(c, leftRow.get(c)));
                    rightCols.forEach(c -> values.put(c, rightRow.get(c)));
                    result.addRow(new Row(values));
                }
            }
        }

        if (type == JoinType.RIGHT || type == JoinType.OUTER) {
            for (Row rightRow : right.rows()) {
                if (!matched.contains(rightRow)) {
                    Map<String, Object> values = new LinkedHashMap<>();
                    leftCols.forEach(c -> values.put(c, null));
                    rightCols.forEach(c -> values.put(c, rightRow.get(c)));
                    result.addRow(new Row(values));
                }
            }
        }

        return result;
    }

    public static DataFrame pivot(DataFrame df, String indexCol, 
                                   String columnCol, String valueCol, String aggFunc) {
        Set<Object> indexValues = df.column(indexCol).data.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        Set<Object> columnValues = df.column(columnCol).data.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<String> headers = new ArrayList<>();
        headers.add(indexCol);
        headers.addAll(columnValues.stream().map(Object::toString).collect(Collectors.toList()));
        
        DataFrame result = new DataFrame(headers);

        for (Object idx : indexValues) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put(indexCol, idx);
            
            DataFrame subset = filter(df, indexCol, "==", idx);
            
            for (Object col : columnValues) {
                DataFrame colSubset = filter(subset, columnCol, "==", col);
                Object agg = aggregate(colSubset.column(valueCol).data, aggFunc);
                row.put(col.toString(), agg);
            }
            result.addRow(new Row(row));
        }
        
        return result;
    }

    private static Object aggregate(List<Object> values, String func) {
        List<Double> nums = values.stream()
            .filter(Objects::nonNull)
            .filter(o -> o instanceof Number)
            .map(o -> ((Number) o).doubleValue())
            .collect(Collectors.toList());

        if (nums.isEmpty()) return null;

        switch (func.toLowerCase()) {
            case "sum":
                return nums.stream().mapToDouble(Double::doubleValue).sum();
            case "mean":
            case "avg":
                return nums.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            case "min":
                return nums.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            case "max":
                return nums.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            case "count":
                return (double) nums.size();
            default:
                return nums.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        }
    }

    public static DataFrame melt(DataFrame df, List<String> idCols, 
                                 List<String> valueCols, 
                                 String varName, String valName) {
        List<String> newHeaders = new ArrayList<>(idCols);
        newHeaders.add(varName);
        newHeaders.add(valName);
        
        DataFrame result = new DataFrame(newHeaders);

        for (Row row : df.rows()) {
            for (String valCol : valueCols) {
                Map<String, Object> newRow = new LinkedHashMap<>();
                idCols.forEach(c -> newRow.put(c, row.get(c)));
                newRow.put(varName, valCol);
                newRow.put(valName, row.get(valCol));
                result.addRow(new Row(newRow));
            }
        }
        
        return result;
    }
}

enum JoinType { INNER, LEFT, RIGHT, OUTER }
```

## 4. Column Operations

```java
public class ColumnOperations {
    public static Column abs(Column col) {
        Column result = new Column(col.getName() + "_abs");
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                result.add(Math.abs(((Number) val).doubleValue()));
            }
        }
        return result;
    }

    public static Column round(Column col, int decimals) {
        Column result = new Column(col.getName() + "_rounded");
        double multiplier = Math.pow(10, decimals);
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                double rounded = Math.round(((Number) val).doubleValue() * multiplier) / multiplier;
                result.add(decimals == 0 ? (long) rounded : rounded);
            }
        }
        return result;
    }

    public static Column log(Column col, double base) {
        Column result = new Column(col.getName() + "_log");
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                result.add(v > 0 ? Math.log(v) / Math.log(base) : null);
            }
        }
        return result;
    }

    public static Column sqrt(Column col) {
        Column result = new Column(col.getName() + "_sqrt");
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                result.add(v >= 0 ? Math.sqrt(v) : null);
            }
        }
        return result;
    }

    public static Column power(Column col, double exponent) {
        Column result = new Column(col.getName() + "_pow" + (int) exponent);
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                result.add(Math.pow(((Number) val).doubleValue(), exponent));
            }
        }
        return result;
    }

    public static Column lag(Column col, int periods) {
        Column result = new Column(col.getName() + "_lag" + periods);
        for (int i = 0; i < col.size(); i++) {
            result.add(i - periods >= 0 ? col.get(i - periods) : null);
        }
        return result;
    }

    public static Column lead(Column col, int periods) {
        Column result = new Column(col.getName() + "_lead" + periods);
        for (int i = 0; i < col.size(); i++) {
            result.add(i + periods < col.size() ? col.get(i + periods) : null);
        }
        return result;
    }

    public static Column cumsum(Column col) {
        Column result = new Column(col.getName() + "_cumsum");
        double sum = 0;
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                sum += ((Number) val).doubleValue();
            }
            result.add(sum);
        }
        return result;
    }

    public static Column diff(Column col, int periods) {
        Column result = new Column(col.getName() + "_diff" + periods);
        for (int i = 0; i < col.size(); i++) {
            if (i - periods >= 0 && col.get(i) instanceof Number && col.get(i - periods) instanceof Number) {
                double curr = ((Number) col.get(i)).doubleValue();
                double prev = ((Number) col.get(i - periods)).doubleValue();
                result.add(curr - prev);
            } else {
                result.add(null);
            }
        }
        return result;
    }

    public static Column rank(Column col, boolean ascending) {
        Column result = new Column(col.getName() + "_rank");
        List<Integer> indices = IntStream.range(0, col.size())
            .boxed()
            .sorted((i1, i2) -> {
                Object v1 = col.get(i1);
                Object v2 = col.get(i2);
                int cmp = compare(v1, v2);
                return ascending ? cmp : -cmp;
            })
            .collect(Collectors.toList());

        for (int i = 0; i < col.size(); i++) {
            result.add(indices.indexOf(i) + 1);
        }
        return result;
    }

    public static Column percentileRank(Column col) {
        Column result = new Column(col.getName() + "_pct_rank");
        OptionalDouble mean = col.mean();
        OptionalDouble std = col.std();
        
        if (mean.isEmpty() || std.isEmpty()) return result;
        
        double m = mean.getAsDouble();
        double s = std.getAsDouble();
        
        if (s == 0) {
            for (int i = 0; i < col.size(); i++) {
                result.add(50.0);
            }
        } else {
            for (int i = 0; i < col.size(); i++) {
                Object val = col.get(i);
                if (val instanceof Number) {
                    double z = (((Number) val).doubleValue() - m) / s;
                    double percentile = 50 + 10 * z;
                    result.add(Math.max(0, Math.min(100, percentile)));
                }
            }
        }
        return result;
    }

    public static Column fillNa(Column col, Object value) {
        Column result = new Column(col.getName());
        for (int i = 0; i < col.size(); i++) {
            result.add(col.get(i) == null ? value : col.get(i));
        }
        return result;
    }

    public static Column fillNaForward(Column col) {
        Column result = new Column(col.getName());
        Object lastValid = null;
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val != null) lastValid = val;
            result.add(lastValid);
        }
        return result;
    }

    public static Column fillNaBackward(Column col) {
        List<Object> values = new ArrayList<>();
        Object nextValid = null;
        for (int i = col.size() - 1; i >= 0; i--) {
            Object val = col.get(i);
            if (val != null) nextValid = val;
            values.add(0, nextValid);
        }
        Column result = new Column(col.getName());
        values.forEach(result::add);
        return result;
    }

    public static Column rollingMean(Column col, int window) {
        Column result = new Column(col.getName() + "_rolling" + window);
        for (int i = 0; i < col.size(); i++) {
            double sum = 0;
            int count = 0;
            for (int j = Math.max(0, i - window + 1); j <= i; j++) {
                if (col.get(j) instanceof Number) {
                    sum += ((Number) col.get(j)).doubleValue();
                    count++;
                }
            }
            result.add(count > 0 ? sum / count : null);
        }
        return result;
    }

    public static Column rollingStd(Column col, int window) {
        Column result = new Column(col.getName() + "_std" + window);
        for (int i = 0; i < col.size(); i++) {
            List<Double> windowVals = new ArrayList<>();
            for (int j = Math.max(0, i - window + 1); j <= i; j++) {
                if (col.get(j) instanceof Number) {
                    windowVals.add(((Number) col.get(j)).doubleValue());
                }
            }
            if (windowVals.size() > 1) {
                double mean = windowVals.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                double variance = windowVals.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().getAsDouble();
                result.add(Math.sqrt(variance));
            } else {
                result.add(null);
            }
        }
        return result;
    }

    private static int compare(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        if (a == null) return b == null ? 0 : -1;
        if (b == null) return 1;
        return ((Comparable) a).compareTo(b);
    }
}
```

## 5. Advanced Transformations

```java
public class AdvancedTransformations {
    public static DataFrame normalize(DataFrame df, String col) {
        OptionalDouble mean = df.column(col).mean();
        OptionalDouble std = df.column(col).std();
        
        if (mean.isEmpty() || std.isEmpty() || std.getAsDouble() == 0) {
            return df;
        }
        
        double m = mean.getAsDouble();
        double s = std.getAsDouble();
        
        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object val = row.get(col);
            if (val instanceof Number) {
                values.put(col, (((Number) val).doubleValue() - m) / s);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    public static DataFrame minMaxScale(DataFrame df, String col) {
        Optional<Object> min = df.column(col).min();
        Optional<Object> max = df.column(col).max();
        
        if (min.isEmpty() || max.isEmpty()) return df;
        
        double minVal = ((Number) min.get()).doubleValue();
        double maxVal = ((Number) max.get()).doubleValue();
        
        if (maxVal == minVal) {
            return df;
        }
        
        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object val = row.get(col);
            if (val instanceof Number) {
                double scaled = (((Number) val).doubleValue() - minVal) / (maxVal - minVal);
                values.put(col, scaled);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    public static DataFrame oneHotEncode(DataFrame df, String col) {
        Set<Object> uniqueValues = df.column(col).data.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<String> headers = new ArrayList<>(df.columns());
        for (Object val : uniqueValues) {
            headers.add(col + "_" + val.toString().replaceAll("[^a-zA-Z0-9]", "_"));
        }

        DataFrame result = new DataFrame(headers);
        String safeColName = df.columns().contains(col) ? col : col;

        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object rowVal = row.get(col);
            for (Object uniqueVal : uniqueValues) {
                String newCol = safeColName + "_" + uniqueVal.toString().replaceAll("[^a-zA-Z0-9]", "_");
                values.put(newCol, Objects.equals(rowVal, uniqueVal) ? 1 : 0);
            }
            values.remove(col);
            result.addRow(new Row(values));
        }
        
        return result;
    }

    public static DataFrame labelEncode(DataFrame df, String col) {
        Set<Object> uniqueValues = df.column(col).data.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        Map<Object, Integer> labelMap = new HashMap<>();
        int label = 0;
        for (Object val : uniqueValues) {
            labelMap.put(val, label++);
        }

        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object rowVal = row.get(col);
            values.put(col, rowVal != null ? labelMap.getOrDefault(rowVal, -1) : null);
            result.addRow(new Row(values));
        }
        
        return result;
    }

    public static DataFrame binNumeric(DataFrame df, String col, int numBins) {
        Optional<Object> min = df.column(col).min();
        Optional<Object> max = df.column(col).max();
        
        if (min.isEmpty() || max.isEmpty()) return df;
        
        double minVal = ((Number) min.get()).doubleValue();
        double maxVal = ((Number) max.get()).doubleValue();
        double binWidth = (maxVal - minVal) / numBins;

        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object val = row.get(col);
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                int bin = (int) Math.min(numBins - 1, (v - minVal) / binWidth);
                values.put(col, bin);
            }
            result.addRow(new Row(values));
        }
        
        return result;
    }

    public static Column boxCoxTransform(Column col, double lambda) {
        Column result = new Column(col.getName() + "_boxcox");
        for (int i = 0; i < col.size(); i++) {
            Object val = col.get(i);
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                if (v > 0) {
                    if (lambda == 0) {
                        result.add(Math.log(v));
                    } else {
                        result.add((Math.pow(v, lambda) - 1) / lambda);
                    }
                } else {
                    result.add(null);
                }
            }
        }
        return result;
    }

    public static Column sqrtTransform(Column col) {
        return ColumnOperations.sqrt(col);
    }
}
```

## 6. Grouped DataFrame Operations

```java
import java.util.stream.Collectors;
import java.util.AbstractMap;

public class GroupedDataFrame {
    private final DataFrame parent;
    private final List<String> groupCols;

    public GroupedDataFrame(DataFrame parent, List<String> groupCols) {
        this.parent = parent;
        this.groupCols = groupCols;
    }

    public DataFrame sum(String... cols) {
        return aggregate("sum", Arrays.asList(cols));
    }

    public DataFrame mean(String... cols) {
        return aggregate("mean", Arrays.asList(cols));
    }

    public DataFrame median(String... cols) {
        return aggregate("median", Arrays.asList(cols));
    }

    public DataFrame min(String... cols) {
        return aggregate("min", Arrays.asList(cols));
    }

    public DataFrame max(String... cols) {
        return aggregate("max", Arrays.asList(cols));
    }

    public DataFrame count() {
        return aggregate("count", Collections.emptyList());
    }

    public DataFrame std(String... cols) {
        return aggregate("std", Arrays.asList(cols));
    }

    private DataFrame aggregate(String func, List<String> aggCols) {
        Map<List<Object>, List<Row>> groups = new LinkedHashMap<>();

        for (Row row : parent.rows()) {
            List<Object> key = groupCols.stream()
                .map(row::get)
                .collect(Collectors.toList());
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        List<String> headers = new ArrayList<>(groupCols);
        if (aggCols.isEmpty()) {
            headers.add("count");
        } else {
            for (String col : aggCols) {
                headers.add(col + "_" + func);
            }
        }

        DataFrame result = new DataFrame(headers);

        for (Map.Entry<List<Object>, List<Row>> entry : groups.entrySet()) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            List<Object> key = entry.getKey();
            for (int i = 0; i < groupCols.size(); i++) {
                rowMap.put(groupCols.get(i), key.get(i));
            }

            if (aggCols.isEmpty()) {
                rowMap.put("count", (double) entry.getValue().size());
            } else {
                for (String col : aggCols) {
                    List<Object> values = entry.getValue().stream()
                        .map(r -> r.get(col))
                        .collect(Collectors.toList());
                    rowMap.put(col + "_" + func, computeAggregation(values, func));
                }
            }

            result.addRow(new Row(rowMap));
        }

        return result;
    }

    private Object computeAggregation(List<Object> values, String func) {
        List<Double> nums = values.stream()
            .filter(Objects::nonNull)
            .filter(o -> o instanceof Number)
            .map(o -> ((Number) o).doubleValue())
            .collect(Collectors.toList());

        if (nums.isEmpty()) return null;

        switch (func.toLowerCase()) {
            case "sum":
                return nums.stream().mapToDouble(Double::doubleValue).sum();
            case "mean":
            case "avg":
                return nums.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            case "median":
                List<Double> sorted = nums.stream().sorted().collect(Collectors.toList());
                int mid = sorted.size() / 2;
                return sorted.size() % 2 == 0 ? 
                    (sorted.get(mid - 1) + sorted.get(mid)) / 2 : sorted.get(mid);
            case "min":
                return nums.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            case "max":
                return nums.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            case "count":
                return (double) nums.size();
            case "std":
                double mean = nums.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                double variance = nums.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0);
                return Math.sqrt(variance);
            default:
                return null;
        }
    }

    public Map<List<Object>, DataFrame> getGroups() {
        Map<List<Object>, DataFrame> groups = new LinkedHashMap<>();

        for (Row row : parent.rows()) {
            List<Object> key = groupCols.stream()
                .map(row::get)
                .collect(Collectors.toList());
            groups.computeIfAbsent(key, k -> new DataFrame(parent.columns())).addRow(row);
        }

        return groups;
    }

    public DataFrame filter(Predicate<List<Row>> condition) {
        Map<List<Object>, List<Row>> groups = new LinkedHashMap<>();

        for (Row row : parent.rows()) {
            List<Object> key = groupCols.stream()
                .map(row::get)
                .collect(Collectors.toList());
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        List<String> headers = parent.columns();
        DataFrame result = new DataFrame(headers);

        for (Map.Entry<List<Object>, List<Row>> entry : groups.entrySet()) {
            if (condition.test(entry.getValue())) {
                entry.getValue().forEach(result::addRow);
            }
        }

        return result;
    }

    public DataFrame apply(Function<List<Row>, Row> fn) {
        Map<List<Object>, List<Row>> groups = new LinkedHashMap<>();

        for (Row row : parent.rows()) {
            List<Object> key = groupCols.stream()
                .map(row::get)
                .collect(Collectors.toList());
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        List<String> headers = groupCols;
        DataFrame result = new DataFrame(headers);

        for (Map.Entry<List<Object>, List<Row>> entry : groups.entrySet()) {
            Row newRow = fn.apply(entry.getValue());
            result.addRow(newRow);
        }

        return result;
    }
}
```

## 7. Data Quality Checks

```java
public class DataQuality {
    public static class QualityReport {
        public int totalRows;
        public int totalColumns;
        public Map<String, Long> missingCounts;
        public Map<String, Long> uniqueCounts;
        public Map<String, String> typeInference;
        public List<String> duplicateRows;
        public Map<String, List<Object>> outliers;
        public List<String> warnings;

        public QualityReport() {
            missingCounts = new HashMap<>();
            uniqueCounts = new HashMap<>();
            typeInference = new HashMap<>();
            duplicateRows = new ArrayList<>();
            outliers = new HashMap<>();
            warnings = new ArrayList<>();
        }

        public void print() {
            System.out.println("=== Data Quality Report ===");
            System.out.println("Rows: " + totalRows);
            System.out.println("Columns: " + totalColumns);
            System.out.println("\nMissing Values:");
            missingCounts.forEach((k, v) -> 
                System.out.println("  " + k + ": " + v + " (" + (v * 100.0 / totalRows) + "%)"));
            System.out.println("\nUnique Values:");
            uniqueCounts.forEach((k, v) -> System.out.println("  " + k + ": " + v));
            System.out.println("\nType Inference:");
            typeInference.forEach((k, v) -> System.out.println("  " + k + ": " + v));
            if (!warnings.isEmpty()) {
                System.out.println("\nWarnings:");
                warnings.forEach(w -> System.out.println("  - " + w));
            }
        }
    }

    public static QualityReport generateReport(DataFrame df) {
        QualityReport report = new QualityReport();
        report.totalRows = df.shape()[0];
        report.totalColumns = df.shape()[1];

        for (String col : df.columns()) {
            Column column = df.column(col);
            
            long missing = column.data.stream().filter(Objects::isNull).count();
            report.missingCounts.put(col, missing);
            
            long unique = column.data.stream().filter(Objects::nonNull).distinct().count();
            report.uniqueCounts.put(col, unique);
            
            String inferredType = inferType(column);
            report.typeInference.put(col, inferredType);

            if (missing > report.totalRows * 0.5) {
                report.warnings.add("Column '" + col + "' has >50% missing values");
            }
            
            if (unique == 1 && report.totalRows > 1) {
                report.warnings.add("Column '" + col + "' has only one unique value (constant)");
            }

            if (inferredType.equals("numeric")) {
                List<Object> outlierList = detectOutliers(column);
                if (!outlierList.isEmpty()) {
                    report.outliers.put(col, outlierList);
                }
            }
        }

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < df.shape()[0]; i++) {
            String key = df.getRow(i).toString();
            if (seen.contains(key)) {
                report.duplicateRows.add("Row " + i);
            }
            seen.add(key);
        }

        if (!report.duplicateRows.isEmpty()) {
            report.warnings.add("Found " + report.duplicateRows.size() + " duplicate rows");
        }

        return report;
    }

    private static String inferType(Column col) {
        int numCount = 0;
        int strCount = 0;
        int dateCount = 0;
        int boolCount = 0;

        for (int i = 0; i < Math.min(col.size(), 100); i++) {
            Object val = col.get(i);
            if (val == null) continue;
            
            if (val instanceof Number) {
                numCount++;
            } else if (val instanceof Boolean) {
                boolCount++;
            } else if (val instanceof LocalDate || val instanceof LocalDateTime) {
                dateCount++;
            } else if (val instanceof String) {
                String s = (String) val;
                if (s.matches("\\d{4}-\\d{2}-\\d{2}")) dateCount++;
                else strCount++;
            }
        }

        if (numCount > (col.size() * 0.7)) return "numeric";
        if (boolCount > (col.size() * 0.7)) return "boolean";
        if (dateCount > (col.size() * 0.7)) return "date";
        return "string";
    }

    private static List<Object> detectOutliers(Column col) {
        OptionalDouble mean = col.mean();
        OptionalDouble std = col.std();
        
        if (mean.isEmpty() || std.isEmpty() || std.getAsDouble() == 0) {
            return Collections.emptyList();
        }

        double m = mean.getAsDouble();
        double s = std.getAsDouble();
        double threshold = 3;

        List<Object> outliers = new ArrayList<>();
        for (Object val : col.data) {
            if (val instanceof Number) {
                double v = ((Number) val).doubleValue();
                if (Math.abs(v - m) > threshold * s) {
                    outliers.add(val);
                }
            }
        }
        return outliers;
    }

    public static DataFrame removeOutliers(DataFrame df, String col) {
        OptionalDouble mean = df.column(col).mean();
        OptionalDouble std = df.column(col).std();
        
        if (mean.isEmpty() || std.isEmpty() || std.getAsDouble() == 0) {
            return df;
        }

        double m = mean.getAsDouble();
        double s = std.getAsDouble();
        double lower = m - 3 * s;
        double upper = m + 3 * s;

        return DataFrameOps.filter(df, col, ">=", lower)
                          .where(row -> {
                              Object val = row.get(col);
                              return val instanceof Number && 
                                     ((Number) val).doubleValue() <= upper;
                          });
    }
}
```