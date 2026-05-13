import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class HealthcarePipeline {
    private static final Logger logger = Logger.getLogger(HealthcarePipeline.class.getName());
    private Config config;
    private List<String> auditLog = new ArrayList<>();
    private DataQuality.QualityReport finalReport;

    public static void main(String[] args) {
        try {
            setupLogging();
            HealthcarePipeline pipeline = new HealthcarePipeline();
            pipeline.run(args);
        } catch (Exception e) {
            logger.severe("Pipeline failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void setupLogging() throws IOException {
        FileHandler fh = new FileHandler("logs/healthcare_pipeline_%g.log", 5 * 1024 * 1024, 5);
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        logger.setLevel(Level.ALL);
    }

    public void run(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        audit("Pipeline starting");

        config = new Config(args.length > 0 ? args[0] : "config.properties");

        DataFrame patients = phase1Ingestion();
        DataFrame cleaned = phase2Cleaning(patients);
        DataFrame enriched = phase3Enrichment(cleaned);
        phase4Analysis(enriched);
        phase5Output(enriched);

        audit("Pipeline completed in " + (System.currentTimeMillis() - startTime) + "ms");
        printSummary();

        sendNotifications(true);
    }

    private DataFrame phase1Ingestion() throws Exception {
        audit("Phase 1: Data Ingestion starting");
        logger.info("Loading patient data...");
        
        DataFrame patients = CSVParser.readCSV(config.get("input.patients"));
        audit("Loaded " + patients.shape()[0] + " patient records");
        
        DataFrame insurance = CSVParser.readCSV(config.get("input.insurance"));
        audit("Loaded " + insurance.shape()[0] + " insurance providers");
        
        DataFrame icd10 = CSVParser.readCSV(config.get("input.icd10"));
        audit("Loaded " + icd10.shape()[0] + " ICD-10 codes");
        
        DataFrame cpt = CSVParser.readCSV(config.get("input.cpt"));
        audit("Loaded " + cpt.shape()[0] + " CPT codes");

        Map<String, DataFrame> cache = new HashMap<>();
        cache.put("insurance", insurance);
        cache.put("icd10", icd10);
        cache.put("cpt", cpt);
        config.setCache(cache);

        DataQuality.QualityReport report = DataQuality.generateReport(patients);
        report.print();
        finalReport = report;

        return patients;
    }

    private DataFrame phase2Cleaning(DataFrame patients) throws Exception {
        audit("Phase 2: Data Cleaning starting");
        logger.info("Cleaning patient data...");

        DataFrame df = patients;
        int originalCount = df.shape()[0];

        df = deduplicatePatients(df);
        df = standardizeDates(df);
        df = cleanPhoneNumbers(df);
        df = standardizeAddresses(df);
        df = validateMedicalCodes(df);
        df = handleMissingCriticalFields(df);

        audit("Cleaning complete: " + originalCount + " -> " + df.shape()[0] + " records");

        return df;
    }

    private DataFrame deduplicatePatients(DataFrame df) {
        audit("Deduplicating patients...");
        Set<String> seen = new HashSet<>();
        List<String> cols = Arrays.asList("mrn", "last_name", "dob");

        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            String key = cols.stream()
                .map(c -> row.get(c) != null ? row.get(c).toString() : "")
                .collect(Collectors.joining("|"));

            if (!seen.contains(key)) {
                seen.add(key);
                result.addRow(row);
            }
        }

        int duplicates = df.shape()[0] - result.shape()[0];
        if (duplicates > 0) {
            audit("Removed " + duplicates + " duplicate records");
        }
        return result;
    }

    private DataFrame standardizeDates(DataFrame df) {
        audit("Standardizing date formats...");
        DateTimeFormatter[] inputFormats = {
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy")
        };
        DateTimeFormatter outputFormat = DateTimeFormatter.ISO_LOCAL_DATE;

        List<String> dateCols = Arrays.asList("dob", "admission_date", "discharge_date");
        List<String> newColumns = new ArrayList<>(df.columns());

        for (String col : dateCols) {
            if (!newColumns.contains(col + "_parsed")) {
                newColumns.add(col + "_parsed");
            }
        }

        DataFrame result = new DataFrame(newColumns);
        int errors = 0;

        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            for (String col : dateCols) {
                Object dateObj = row.get(col);
                if (dateObj != null) {
                    LocalDate parsed = parseDateSafe(dateObj.toString(), inputFormats);
                    if (parsed != null) {
                        values.put(col + "_parsed", parsed);
                    } else {
                        errors++;
                    }
                }
            }
            result.addRow(new Row(values));
        }

        if (errors > 0) {
            audit("Date parsing errors: " + errors);
        }
        return result;
    }

    private LocalDate parseDateSafe(String dateStr, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter f : formatters) {
            try {
                return LocalDate.parse(dateStr.trim(), f);
            } catch (Exception e) {}
        }
        return null;
    }

    private DataFrame cleanPhoneNumbers(DataFrame df) {
        audit("Cleaning phone numbers...");
        DataFrame result = new DataFrame(df.columns());
        
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object phone = row.get("phone");
            if (phone != null) {
                String cleaned = phone.toString().replaceAll("\\D+", "");
                if (cleaned.length() == 10) {
                    values.put("phone", formatPhone(cleaned));
                } else if (cleaned.length() > 10) {
                    values.put("phone", formatPhone(cleaned.substring(cleaned.length() - 10)));
                }
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private String formatPhone(String digits) {
        return String.format("(%s) %s-%s", 
            digits.substring(0, 3), 
            digits.substring(3, 6), 
            digits.substring(6));
    }

    private DataFrame standardizeAddresses(DataFrame df) {
        audit("Standardizing addresses...");
        StateNormalizer normalizer = new StateNormalizer();

        DataFrame result = new DataFrame(df.columns());
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object state = row.get("state");
            if (state != null) {
                values.put("state", normalizer.normalize(state.toString()));
            }
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame validateMedicalCodes(DataFrame df) {
        audit("Validating medical codes...");
        @SuppressWarnings("unchecked")
        Map<String, DataFrame> cache = config.getCache();
        DataFrame icd10 = cache.get("icd10");

        Set<String> validCodes = icd10.column("code").data.stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .collect(Collectors.toSet());

        DataFrame result = new DataFrame(new ArrayList<>(df.columns()) {{ add("primary_diagnosis_valid"); }});
        int invalidCount = 0;

        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object diag = row.get("primary_diagnosis");
            boolean valid = diag != null && validCodes.contains(diag.toString());
            values.put("primary_diagnosis_valid", valid);
            if (!valid) invalidCount++;
            result.addRow(new Row(values));
        }

        audit("Invalid primary diagnoses: " + invalidCount);
        return result;
    }

    private DataFrame handleMissingCriticalFields(DataFrame df) {
        audit("Handling missing critical fields...");
        DataFrame result = new DataFrame(df.columns());

        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());

            if (values.get("discharge_date") == null && values.get("admission_date_parsed") != null) {
                values.put("discharge_date", LocalDate.now());
                values.put("status", "inpatient");
            } else {
                values.put("status", "discharged");
            }

            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame phase3Enrichment(DataFrame df) throws Exception {
        audit("Phase 3: Data Enrichment starting");
        logger.info("Enriching patient data...");

        @SuppressWarnings("unchecked")
        Map<String, DataFrame> cache = config.getCache();

        df = enrichICDDescriptions(df, cache.get("icd10"));
        df = enrichInsuranceInfo(df, cache.get("insurance"));
        df = calculateDerivedFeatures(df);

        audit("Enrichment complete");
        return df;
    }

    private DataFrame enrichICDDescriptions(DataFrame df, DataFrame icd10) {
        audit("Enriching ICD descriptions...");
        Map<String, String> codeMap = new HashMap<>();
        for (Row row : icd10.rows()) {
            codeMap.put(row.get("code").toString(), row.get("description").toString());
        }

        List<String> newCols = new ArrayList<>(df.columns());
        newCols.add("primary_diagnosis_desc");

        DataFrame result = new DataFrame(newCols);
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object code = row.get("primary_diagnosis");
            values.put("primary_diagnosis_desc", codeMap.getOrDefault(code != null ? code.toString() : "", "Unknown"));
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame enrichInsuranceInfo(DataFrame df, DataFrame insurance) {
        audit("Enriching insurance info...");
        Map<String, String[]> insMap = new HashMap<>();
        for (Row row : insurance.rows()) {
            insMap.put(row.get("insurance_id").toString(), 
                new String[]{row.get("provider_name").toString(), row.get("plan_type").toString()});
        }

        List<String> newCols = new ArrayList<>(df.columns());
        newCols.add("insurance_provider");
        newCols.add("insurance_plan_type");

        DataFrame result = new DataFrame(newCols);
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());
            Object insId = row.get("insurance_id");
            String[] info = insId != null ? insMap.get(insId.toString()) : null;
            values.put("insurance_provider", info != null ? info[0] : "Unknown");
            values.put("insurance_plan_type", info != null ? info[1] : "Unknown");
            result.addRow(new Row(values));
        }
        return result;
    }

    private DataFrame calculateDerivedFeatures(DataFrame df) {
        audit("Calculating derived features...");
        LocalDate today = LocalDate.now();

        List<String> newCols = new ArrayList<>(df.columns());
        newCols.add("age");
        newCols.add("length_of_stay");
        newCols.add("readmission_risk_score");

        DataFrame result = new DataFrame(newCols);
        for (Row row : df.rows()) {
            Map<String, Object> values = new LinkedHashMap<>(row.getValues());

            Object dob = row.get("dob_parsed");
            if (dob instanceof LocalDate) {
                long years = Period.between((LocalDate) dob, today).getYears();
                values.put("age", years);
            }

            Object admit = row.get("admission_date_parsed");
            Object discharge = row.get("discharge_date_parsed");
            if (admit instanceof LocalDate && discharge instanceof LocalDate) {
                long days = Period.between((LocalDate) admit, (LocalDate) discharge).getDays();
                values.put("length_of_stay", Math.max(1, days));
            }

            Object charges = row.get("total_charges");
            double riskScore = 0;
            if (charges instanceof Number) {
                riskScore = Math.min(10, ((Number) charges).doubleValue() / 10000);
            }
            values.put("readmission_risk_score", Math.round(riskScore * 10) / 10.0);

            result.addRow(new Row(values));
        }
        return result;
    }

    private void phase4Analysis(DataFrame df) {
        audit("Phase 4: Analysis starting");
        logger.info("Running analytics...");

        Map<String, Long> genderDist = new HashMap<>();
        Map<String, Long> diagnosisFreq = new LinkedHashMap<>();
        Map<String, Double> avgChargesByInsurance = new LinkedHashMap<>();

        for (Row row : df.rows()) {
            Object gender = row.get("gender");
            if (gender != null) {
                genderDist.merge(gender.toString(), 1L, Long::sum);
            }

            Object diag = row.get("primary_diagnosis_desc");
            if (diag != null && !diag.toString().equals("Unknown")) {
                diagnosisFreq.merge(diag.toString(), 1L, Long::sum);
            }

            Object ins = row.get("insurance_provider");
            Object charges = row.get("total_charges");
            if (ins != null && charges instanceof Number) {
                avgChargesByInsurance.merge(ins.toString(), 
                    ((Number) charges).doubleValue(), (a, b) -> (a + b) / 2);
            }
        }

        System.out.println("\n=== ANALYSIS RESULTS ===");
        System.out.println("\nGender Distribution:");
        genderDist.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        System.out.println("\nTop 10 Diagnoses:");
        diagnosisFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .forEach(e -> System.out.println("  " + e.getKey() + ": " + e.getValue()));

        System.out.println("\nAverage Charges by Insurance:");
        avgChargesByInsurance.forEach((k, v) -> 
            System.out.println("  " + k + ": $" + String.format("%.2f", v)));

        audit("Analysis complete");
    }

    private void phase5Output(DataFrame df) throws Exception {
        audit("Phase 5: Output starting");
        logger.info("Generating outputs...");

        new File("output").mkdirs();
        CSVParser.writeCSV(df, "output/patients_cleaned.csv");

        DataQuality.QualityReport report = DataQuality.generateReport(df);
        report.print();

        try (PrintWriter pw = new PrintWriter("output/quality_certification.txt")) {
            pw.println("=== DATA QUALITY CERTIFICATION ===");
            pw.println("Generated: " + LocalDateTime.now());
            pw.println("Total Records: " + df.shape()[0]);
            pw.println("Total Columns: " + df.shape()[1]);
            pw.println("\nMissing Values:");
            report.missingCounts.forEach((k, v) -> pw.println("  " + k + ": " + v));
        }

        try (PrintWriter pw = new PrintWriter("output/audit_trail.txt")) {
            for (String entry : auditLog) {
                pw.println(entry);
            }
        }

        audit("Output complete");
    }

    private void audit(String message) {
        String entry = LocalDateTime.now() + " | " + message;
        auditLog.add(entry);
        logger.info(message);
    }

    private void printSummary() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PIPELINE SUMMARY");
        System.out.println("=".repeat(50));
        System.out.println("Audit entries: " + auditLog.size());
        System.out.println("Final records: " + (finalReport != null ? finalReport.totalRows : 0));
    }

    private void sendNotifications(boolean success) {
        if (config.get("notification.email") != null) {
            logger.info("Would send notification to: " + config.get("notification.email"));
        }
    }
}

class Config {
    private Map<String, String> props = new HashMap<>();
    private Map<String, DataFrame> cache = new HashMap<>();

    public Config(String configFile) throws IOException {
        if (new File(configFile).exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            props.put(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
            }
        } else {
            setDefaults();
        }
    }

    private void setDefaults() {
        props.put("input.patients", "data/patients_raw.csv");
        props.put("input.insurance", "data/insurance_providers.csv");
        props.put("input.icd10", "data/icd10_codes.csv");
        props.put("input.cpt", "data/cpt_codes.csv");
        props.put("output.dir", "output");
    }

    public String get(String key) {
        return props.get(key);
    }

    public void setCache(Map<String, DataFrame> cache) {
        this.cache = cache;
    }

    public Map<String, DataFrame> getCache() {
        return cache;
    }
}

class StateNormalizer {
    private static final Map<String, String> STATES = new HashMap<>();
    static {
        STATES.put("CA", "CA");
        STATES.put("California", "CA");
        STATES.put("TX", "TX");
        STATES.put("Texas", "TX");
        STATES.put("NY", "NY");
        STATES.put("New York", "NY");
        STATES.put("FL", "FL");
        STATES.put("Florida", "FL");
    }

    public String normalize(String state) {
        return STATES.getOrDefault(state, state);
    }
}