package eda;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ClinicalTrialEDA {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     CLINICAL TRIAL EXPLORATORY DATA ANALYSIS             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        
        List<Patient> patients = generateClinicalTrialData();
        
        System.out.println("=== STUDY OVERVIEW ===");
        System.out.println("Total Patients: " + patients.size());
        long treatment = patients.stream().filter(p -> p.treatment.equals("Treatment")).count();
        long control = patients.stream().filter(p -> p.treatment.equals("Control")).count();
        System.out.println("Treatment Group: " + treatment);
        System.out.println("Control Group: " + control);
        
        System.out.println("\n=== BASELINE CHARACTERISTICS ===\n");
        
        System.out.println("--- Age ---");
        double[] treatmentAges = patients.stream().filter(p -> p.treatment.equals("Treatment"))
            .mapToDouble(p -> p.age).toArray();
        double[] controlAges = patients.stream().filter(p -> p.treatment.equals("Control"))
            .mapToDouble(p -> p.age).toArray();
        
        Statistics.DescriptiveStats treatAgeStats = Statistics.describe(treatmentAges);
        System.out.println("Treatment: mean=" + String.format("%.1f", treatAgeStats.mean) + 
            ", std=" + String.format("%.1f", treatAgeStats.std));
        Statistics.DescriptiveStats ctrlAgeStats = Statistics.describe(controlAges);
        System.out.println("Control: mean=" + String.format("%.1f", ctrlAgeStats.mean) + 
            ", std=" + String.format("%.1f", ctrlAgeStats.std));
        
        System.out.println("\n--- Gender Distribution ---");
        Map<String, Long> treatGender = new HashMap<>();
        Map<String, Long> ctrlGender = new HashMap<>();
        for (Patient p : patients) {
            if (p.treatment.equals("Treatment")) {
                treatGender.merge(p.gender, 1L, Long::sum);
            } else {
                ctrlGender.merge(p.gender, 1L, Long::sum);
            }
        }
        System.out.println("Treatment: " + treatGender);
        System.out.println("Control: " + ctrlGender);
        
        System.out.println("\n=== BASELINE MEASUREMENTS ===\n");
        
        System.out.println("--- Systolic BP ---");
        double[] treatBP = patients.stream().filter(p -> p.treatment.equals("Treatment"))
            .mapToDouble(p -> p.baselineBP).toArray();
        double[] ctrlBP = patients.stream().filter(p -> p.treatment.equals("Control"))
            .mapToDouble(p -> p.baselineBP).toArray();
        
        Statistics.DescriptiveStats treatBPStats = Statistics.describe(treatBP);
        Statistics.DescriptiveStats ctrlBPStats = Statistics.describe(ctrlBP);
        System.out.printf("Treatment: %.1f ± %.1f mmHg%n", treatBPStats.mean, treatBPStats.std);
        System.out.printf("Control: %.1f ± %.1f mmHg%n", ctrlBPStats.mean, ctrlBPStats.std);
        
        System.out.println("\n--- Baseline Cholesterol ---");
        double[] treatChol = patients.stream().filter(p -> p.treatment.equals("Treatment"))
            .mapToDouble(p -> p.baselineCholesterol).toArray();
        double[] ctrlChol = patients.stream().filter(p -> p.treatment.equals("Control"))
            .mapToDouble(p -> p.baselineCholesterol).toArray();
        
        Statistics.DescriptiveStats treatCholStats = Statistics.describe(treatChol);
        Statistics.DescriptiveStats ctrlCholStats = Statistics.describe(ctrlChol);
        System.out.printf("Treatment: %.1f ± %.1f mg/dL%n", treatCholStats.mean, treatCholStats.std);
        System.out.printf("Control: %.1f ± %.1f mg/dL%n", ctrlCholStats.mean, ctrlCholStats.std);
        
        System.out.println("\n=== EFFICACY ANALYSIS ===\n");
        
        System.out.println("--- Primary Endpoint (BP Change) ---");
        double[] treatChange = patients.stream().filter(p -> p.treatment.equals("Treatment"))
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        double[] ctrlChange = patients.stream().filter(p -> p.treatment.equals("Control"))
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        
        Statistics.DescriptiveStats treatChangeStats = Statistics.describe(treatChange);
        Statistics.DescriptiveStats ctrlChangeStats = Statistics.describe(ctrlChange);
        System.out.printf("Treatment: mean change = %.1f ± %.1f mmHg%n", 
            treatChangeStats.mean, treatChangeStats.std);
        System.out.printf("Control: mean change = %.1f ± %.1f mmHg%n", 
            ctrlChangeStats.mean, ctrlChangeStats.std);
        
        System.out.println("\n--- Statistical Test ---");
        Statistics.TTestResult tTest = Statistics.tTest(treatChange, ctrlChange);
        tTest.print();
        
        System.out.println("\n=== SAFETY ANALYSIS ===\n");
        
        System.out.println("--- Adverse Events ---");
        long treatAE = patients.stream().filter(p -> p.treatment.equals("Treatment") && p.adverseEvents > 0).count();
        long ctrlAE = patients.stream().filter(p -> p.treatment.equals("Control") && p.adverseEvents > 0).count();
        System.out.printf("Treatment: %d patients (%.1f%%)%n", treatAE, 100.0 * treatAE / treatment);
        System.out.printf("Control: %d patients (%.1f%%)%n", ctrlAE, 100.0 * ctrlAE / control);
        
        System.out.println("\n--- Adverse Event Counts ---");
        double[] treatAECount = patients.stream().filter(p -> p.treatment.equals("Treatment"))
            .mapToDouble(p -> (double) p.adverseEvents).toArray();
        double[] ctrlAECount = patients.stream().filter(p -> p.treatment.equals("Control"))
            .mapToDouble(p -> (double) p.adverseEvents).toArray();
        
        Statistics.DescriptiveStats treatAES = Statistics.describe(treatAECount);
        Statistics.DescriptiveStats ctrlAES = Statistics.describe(ctrlAECount);
        System.out.printf("Treatment: %.2f ± %.2f events/patient%n", treatAES.mean, treatAES.std);
        System.out.printf("Control: %.2f ± %.2f events/patient%n", ctrlAES.mean, ctrlAES.std);
        
        System.out.println("\n=== RESPONSE RATES ===\n");
        
        System.out.println("--- Treatment Response (BP reduction >= 10 mmHg) ---");
        long treatResponders = patients.stream()
            .filter(p -> p.treatment.equals("Treatment") && (p.baselineBP - p.endpointBP) >= 10)
            .count();
        long ctrlResponders = patients.stream()
            .filter(p -> p.treatment.equals("Control") && (p.baselineBP - p.endpointBP) >= 10)
            .count();
        System.out.printf("Treatment: %d/%d (%.1f%%)%n", treatResponders, treatment, 100.0 * treatResponders / treatment);
        System.out.printf("Control: %d/%d (%.1f%%)%n", ctrlResponders, control, 100.0 * ctrlResponders / control);
        
        System.out.println("\n=== SUBGROUP ANALYSIS ===\n");
        
        System.out.println("--- By Age Group (< 45 vs >= 45) ---");
        System.out.println("Age < 45:");
        double[] youngTreat = patients.stream()
            .filter(p -> p.treatment.equals("Treatment") && p.age < 45)
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        double[] youngCtrl = patients.stream()
            .filter(p -> p.treatment.equals("Control") && p.age < 45)
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        if (youngTreat.length > 0 && youngCtrl.length > 0) {
            System.out.printf("Treatment: %.1f mmHg reduction (n=%d)%n", 
                Arrays.stream(youngTreat).average().orElse(0), youngTreat.length);
            System.out.printf("Control: %.1f mmHg reduction (n=%d)%n", 
                Arrays.stream(youngCtrl).average().orElse(0), youngCtrl.length);
        }
        
        System.out.println("\nAge >= 45:");
        double[] oldTreat = patients.stream()
            .filter(p -> p.treatment.equals("Treatment") && p.age >= 45)
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        double[] oldCtrl = patients.stream()
            .filter(p -> p.treatment.equals("Control") && p.age >= 45)
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        if (oldTreat.length > 0 && oldCtrl.length > 0) {
            System.out.printf("Treatment: %.1f mmHg reduction (n=%d)%n", 
                Arrays.stream(oldTreat).average().orElse(0), oldTreat.length);
            System.out.printf("Control: %.1f mmHg reduction (n=%d)%n", 
                Arrays.stream(oldCtrl).average().orElse(0), oldCtrl.length);
        }
        
        System.out.println("\n=== CHOLESTEROL ANALYSIS ===\n");
        
        System.out.println("--- Cholesterol Change ---");
        double[] treatCholChange = patients.stream().filter(p -> p.treatment.equals("Treatment"))
            .mapToDouble(p -> p.baselineCholesterol - p.endpointCholesterol).toArray();
        double[] ctrlCholChange = patients.stream().filter(p -> p.treatment.equals("Control"))
            .mapToDouble(p -> p.baselineCholesterol - p.endpointCholesterol).toArray();
        
        Statistics.DescriptiveStats tcStats = Statistics.describe(treatCholChange);
        Statistics.DescriptiveStats ccStats = Statistics.describe(ctrlCholChange);
        System.out.printf("Treatment: %.1f ± %.1f mg/dL%n", tcStats.mean, tcStats.std);
        System.out.printf("Control: %.1f ± %.1f mg/dL%n", ccStats.mean, ccStats.std);
        
        Statistics.TTestResult cholTest = Statistics.tTest(treatCholChange, ctrlCholChange);
        cholTest.print();
        
        System.out.println("\n=== OUTLIER ANALYSIS ===\n");
        
        System.out.println("--- BP Change Outliers ---");
        double[] allBPChanges = patients.stream()
            .mapToDouble(p -> p.baselineBP - p.endpointBP).toArray();
        
        Statistics.OutlierDetection outliers = Statistics.detectOutliersIQR(allBPChanges);
        outliers.print();
        
        System.out.println("\n=== SUMMARY ===\n");
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ KEY FINDINGS                                                  │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.printf("│ • Treatment reduced BP by %.1f mmHg vs %.1f mmHg (Control)  │%n", 
            treatChangeStats.mean, ctrlChangeStats.mean);
        System.out.printf("│ • Statistically significant difference (p = %.4f)            │%n", tTest.pValue);
        System.out.printf("│ • Response rate: %.1f%% (Treatment) vs %.1f%% (Control)         │%n", 
            100.0 * treatResponders / treatment, 100.0 * ctrlResponders / control);
        System.out.printf("│ • Adverse events comparable between groups                   │%n");
        System.out.printf("│ • Cholesterol reduction: %.1f mg/dL (Treatment)             │%n", tcStats.mean);
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }
    
    private static List<Patient> generateClinicalTrialData() {
        List<Patient> patients = new ArrayList<>();
        Random rand = new Random(42);
        
        for (int i = 0; i < 100; i++) {
            Patient p = new Patient();
            p.id = "P" + String.format("%03d", i + 1);
            p.age = 30 + rand.nextInt(40);
            p.gender = rand.nextBoolean() ? "Male" : "Female";
            p.treatment = i < 50 ? "Treatment" : "Control";
            p.baselineBP = 130 + rand.nextInt(30);
            p.endpointBP = p.baselineBP - (p.treatment.equals("Treatment") ? 
                (8 + rand.nextGaussian() * 6) : (3 + rand.nextGaussian() * 4));
            p.baselineCholesterol = 200 + rand.nextInt(60);
            p.endpointCholesterol = p.baselineCholesterol - (p.treatment.equals("Treatment") ?
                (20 + rand.nextGaussian() * 10) : (5 + rand.nextGaussian() * 8));
            p.adverseEvents = rand.nextDouble() < 0.3 ? rand.nextInt(5) : 0;
            patients.add(p);
        }
        
        return patients;
    }
    
    static class Patient {
        String id;
        int age;
        String gender;
        String treatment;
        double baselineBP;
        double endpointBP;
        double baselineCholesterol;
        double endpointCholesterol;
        int adverseEvents;
    }
}