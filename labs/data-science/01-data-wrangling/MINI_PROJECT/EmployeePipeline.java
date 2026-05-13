import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeePipeline {
    private DataFrame employees;
    private DataFrame departments;
    private DataFrame salaryBands;
    private List<String> log = new ArrayList<>();

    public static void main(String[] args) {
        EmployeePipeline pipeline = new EmployeePipeline();
        pipeline.run();
    }

    public void run() {
        log("Starting Employee Data Pipeline");
        long startTime = System.currentTimeMillis();

        try {
            loadData();
            inspectData();
            DataFrame cleaned = cleanEmployeeData();
            DataFrame enriched = joinAndEnrich(cleaned);
            DataFrame featured = engineerFeatures(enriched);
            DataFrame summary = aggregateAndSummarize(featured);
            exportResults(featured, summary);

            printDataQualityReport();
            log("Pipeline completed successfully in " + 
                (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            log("Pipeline failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadData() {
        log("Loading data files...");
        employees = CSVParser.readCSV("data/employees_raw.csv");
        departments = CSVParser.readCSV("data/departments.csv");
        salaryBands = CSVParser.readCSV("data/salary_bands.csv");
        log("Loaded " + employees.shape()[0] + " employees, " +
            departments.shape()[0] + " departments, " +
            salaryBands.shape()[0] + " salary bands");
    }

    private void inspectData() {
        log("\n=== DATA INSPECTION ===");
        log("Employees shape: " + Arrays.toString(employees.shape()));
        log("Columns: " + employees.columns());

        DataQuality.QualityReport report = DataQuality.generateReport(employees);
        report.print();
    }

    private DataFrame cleanEmployeeData() {
        log("\n=== CLEANING EMPLOYEE DATA ===");

        DataFrame df = employees.dropDuplicateRows();
        log("After removing duplicates: " + df.shape()[0] + " rows");

        df = cleanDates(df);
        df = cleanPhoneNumbers(df);
        df = standardizeNames(df);
        df = handleMissingSalaries(df);
        df = handleMissingDepartments(df);
        df = handleOutliers(df);

        return df;
    }

    private DataFrame cleanDates(DataFrame df) {
        log("Cleaning date formats...");
        List<String> newColumns = new ArrayList<>(df.columns());
        newColumns.add("hire_date_parsed");

        DataFrame result = new DataFrame(newColumns);
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy")
        };

        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object dateObj = row.get("hire_date");
            LocalDate parsed = parseDate(dateObj, formatters);
            values.put("hire_date_parsed", parsed);
            result.addRow(new Row(values));
        }
        return result;
    }

    private LocalDate parseDate(Object dateObj, DateTimeFormatter[] formatters) {
        String dateStr = dateObj != null ? dateObj.toString() : "";
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {}
        }
        return null;
    }

    private DataFrame cleanPhoneNumbers(DataFrame df) {
        log("Cleaning phone numbers...");
        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object phone = row.get("phone");
            if (phone != null) {
                String cleaned = phone.toString().replaceAll("\\D+", "");
                if (cleaned.length() == 10) {
                    values.put("phone", cleaned);
                } else if (cleaned.length() > 10) {
                    values.put("phone", cleaned.substring(cleaned.length() - 10));
                }
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame standardizeNames(DataFrame df) {
        log("Standardizing names...");
        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            values.put("name", toProperCase(row.get("name")));
            result.addRow(new Row(values));
        }
        return result;
    }

    private String toProperCase(Object obj) {
        if (obj == null) return null;
        String[] parts = obj.toString().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                sb.append(Character.toUpperCase(part.charAt(0)))
                  .append(part.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private DataFrame handleMissingSalaries(DataFrame df) {
        log("Handling missing salaries...");
        Map<Object, Double> medianByDept = new HashMap<>();

        for (Object dept : df.column("department").data.stream()
                .filter(Objects::nonNull).collect(Collectors.toSet())) {
            List<Double> salaries = df.rows().stream()
                .filter(r -> Objects.equals(r.get("department"), dept))
                .filter(r -> r.get("salary") instanceof Number)
                .map(r -> ((Number) r.get("salary")).doubleValue())
                .collect(Collectors.toList());

            if (!salaries.isEmpty()) {
                Collections.sort(salaries);
                int mid = salaries.size() / 2;
                medianByDept.put(dept, salaries.size() % 2 == 0 ?
                    (salaries.get(mid - 1) + salaries.get(mid)) / 2 :
                    salaries.get(mid));
            }
        }

        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            if (values.get("salary") == null) {
                Object dept = row.get("department");
                Double median = medianByDept.getOrDefault(dept, 50000.0);
                values.put("salary", median);
                log("Imputed salary " + median + " for department " + dept);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame handleMissingDepartments(DataFrame df) {
        log("Handling missing departments...");
        Map<Object, Object> lastKnownDept = new LinkedHashMap<>();

        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object empId = row.get("employee_id");
            Object dept = values.get("department");

            if (dept == null) {
                dept = lastKnownDept.get(empId);
                if (dept != null) {
                    log("Forward-filled department " + dept + " for employee " + empId);
                } else {
                    dept = "Unassigned";
                }
            } else {
                lastKnownDept.put(empId, dept);
            }
            values.put("department", dept);
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame handleOutliers(DataFrame df) {
        log("Handling salary outliers...");
        Column salaryCol = df.column("salary");
        OptionalDouble mean = salaryCol.mean();
        OptionalDouble std = salaryCol.std();

        if (mean.isEmpty() || std.isEmpty() || std.getAsDouble() == 0) {
            return df;
        }

        double lowerBound = mean.getAsDouble() - 3 * std.getAsDouble();
        double upperBound = mean.getAsDouble() + 3 * std.getAsDouble();

        DataFrame result = new DataFrame(df.columns());
        int outliers = 0;
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object salary = values.get("salary");
            if (salary instanceof Number) {
                double v = ((Number) salary).doubleValue();
                if (v < lowerBound || v > upperBound) {
                    values.put("is_outlier", true);
                    outliers++;
                }
            }
            result.addRow(new Row(values));
        }
        log("Identified " + outliers + " outlier records");
        return result;
    }

    private DataFrame joinAndEnrich(DataFrame df) {
        log("\n=== JOINING AND ENRICHING ===");

        DataFrame enriched = DataFrameOps.join(
            df, departments, "department", "department_name", JoinType.LEFT);

        enriched = enriched.dropColumns("department");
        enriched = enriched.renameColumn("department_name", "department");

        log("Enriched with department info: " + enriched.shape()[0] + " rows, " +
            enriched.columns().size() + " columns");

        return enriched;
    }

    private DataFrame engineerFeatures(DataFrame df) {
        log("\n=== ENGINEERING FEATURES ===");

        df = addTenure(df);
        df = addSalaryPercentile(df);
        df = addHighPerformerFlag(df);
        df = addTenureCategory(df);
        df = addDateComponents(df);

        return df;
    }

    private DataFrame addTenure(DataFrame df) {
        LocalDate today = LocalDate.now();
        DataFrame result = new DataFrame(new ArrayList<>(df.columns()) {{ add("tenure_years"); }});
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object hireDate = row.get("hire_date_parsed");
            if (hireDate instanceof LocalDate) {
                long days = java.time.temporal.ChronoUnit.DAYS.between((LocalDate) hireDate, today);
                values.put("tenure_years", Math.round(days / 365.25 * 10) / 10.0);
            } else {
                values.put("tenure_years", null);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame addSalaryPercentile(DataFrame df) {
        List<Double> salaries = df.column("salary").data.stream()
            .filter(Objects::nonNull)
            .filter(o -> o instanceof Number)
            .map(o -> ((Number) o).doubleValue())
            .sorted()
            .collect(Collectors.toList());

        DataFrame result = new DataFrame(new ArrayList<>(df.columns()) {{ add("salary_percentile"); }});
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object salary = values.get("salary");
            if (salary instanceof Number) {
                double v = ((Number) salary).doubleValue();
                int rank = (int) salaries.stream().filter(s -> s < v).count();
                double percentile = (rank * 100.0) / (salaries.size() - 1);
                values.put("salary_percentile", Math.round(percentile));
            } else {
                values.put("salary_percentile", null);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame addHighPerformerFlag(DataFrame df) {
        DataFrame result = new DataFrame(new ArrayList<>(df.columns()) {{ add("is_high_performer"); }});
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object percentile = values.get("salary_percentile");
            boolean isHigh = percentile instanceof Number && 
                             ((Number) percentile).doubleValue() >= 90;
            values.put("is_high_performer", isHigh);
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame addTenureCategory(DataFrame df) {
        DataFrame result = new DataFrame(new ArrayList<>(df.columns()) {{ add("tenure_category"); }});
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object tenure = values.get("tenure_years");
            String category;
            if (tenure instanceof Number) {
                double t = ((Number) tenure).doubleValue();
                if (t < 2) category = "new";
                else if (t < 5) category = "mid";
                else if (t < 10) category = "senior";
                else category = "veteran";
            } else {
                category = "unknown";
            }
            values.put("tenure_category", category);
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame addDateComponents(DataFrame df) {
        List<String> newCols = new ArrayList<>(df.columns());
        newCols.add("hire_year");
        newCols.add("hire_month");
        newCols.add("hire_quarter");

        DataFrame result = new DataFrame(newCols);
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object hireDate = row.get("hire_date_parsed");
            if (hireDate instanceof LocalDate) {
                LocalDate d = (LocalDate) hireDate;
                values.put("hire_year", d.getYear());
                values.put("hire_month", d.getMonthValue());
                values.put("hire_quarter", (d.getMonthValue() - 1) / 3 + 1);
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame aggregateAndSummarize(DataFrame df) {
        log("\n=== AGGREGATING AND SUMMARIZING ===");

        DataFrame deptSummary = df.groupBy("department").agg(
            "salary", Arrays.asList("mean", "count"),
            "tenure_years", Arrays.asList("mean")
        );

        System.out.println("\n=== DEPARTMENT SUMMARY ===");
        System.out.println(deptSummary);

        return deptSummary;
    }

    private void exportResults(DataFrame cleaned, DataFrame summary) {
        log("\n=== EXPORTING RESULTS ===");
        CSVParser.writeCSV(cleaned, "output/employees_cleaned.csv");
        CSVParser.writeCSV(summary, "output/department_summary.csv");

        List<Row> highPerformers = cleaned.rows().stream()
            .filter(r -> Boolean.TRUE.equals(r.get("is_high_performer")))
            .collect(Collectors.toList());

        DataFrame hp = new DataFrame(cleaned.columns());
        highPerformers.forEach(hp::addRow);
        CSVParser.writeCSV(hp, "output/high_performers.csv");

        log("Exported: employees_cleaned.csv, department_summary.csv, high_performers.csv");
    }

    private void printDataQualityReport() {
        log("\n=== FINAL DATA QUALITY REPORT ===");
        DataQuality.QualityReport report = DataQuality.generateReport(
            CSVParser.readCSV("output/employees_cleaned.csv"));
        report.print();
    }

    private void log(String message) {
        System.out.println("[LOG] " + message);
        this.log.add(message);
    }
}