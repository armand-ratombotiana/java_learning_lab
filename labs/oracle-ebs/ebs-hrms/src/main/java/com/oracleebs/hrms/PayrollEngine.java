package com.oracleebs.hrms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PayrollEngine {
    public enum PayrollFrequency { WEEKLY, BIWEEKLY, MONTHLY, SEMIMONTHLY }

    public static class Employee {
        private final String employeeId;
        private final String name;
        private final BigDecimal salary;
        private final PayrollFrequency frequency;
        private final String legislativeGroup;
        private final BigDecimal taxRate;
        private final int allowances;

        public Employee(String id, String name, BigDecimal salary, PayrollFrequency freq, String legGroup, BigDecimal taxRate, int allowances) {
            this.employeeId = id;
            this.name = name;
            this.salary = salary;
            this.frequency = freq;
            this.legislativeGroup = legGroup;
            this.taxRate = taxRate;
            this.allowances = allowances;
        }

        public String getEmployeeId() { return employeeId; }
        public String getName() { return name; }
        public BigDecimal getSalary() { return salary; }
        public PayrollFrequency getFrequency() { return frequency; }
        public String getLegislativeGroup() { return legislativeGroup; }
        public BigDecimal getTaxRate() { return taxRate; }
        public int getAllownces() { return allowances; }
    }

    public static class PayrollResult {
        private final String employeeId;
        private final String periodName;
        private final BigDecimal grossPay;
        private final BigDecimal taxWithheld;
        private final BigDecimal netPay;
        private final Map<String, BigDecimal> elements;

        public PayrollResult(String empId, String period, BigDecimal gross, BigDecimal tax, BigDecimal net) {
            this.employeeId = empId;
            this.periodName = period;
            this.grossPay = gross;
            this.taxWithheld = tax;
            this.netPay = net;
            this.elements = new LinkedHashMap<>();
        }

        public void addElement(String name, BigDecimal amount) { elements.put(name, amount); }
        public String getEmployeeId() { return employeeId; }
        public String getPeriodName() { return periodName; }
        public BigDecimal getGrossPay() { return grossPay; }
        public BigDecimal getTaxWithheld() { return taxWithheld; }
        public BigDecimal getNetPay() { return netPay; }
        public Map<String, BigDecimal> getElements() { return Collections.unmodifiableMap(elements); }
    }

    private final Map<String, Employee> employees;
    private static final BigDecimal EXEMPTION_AMOUNT = BigDecimal.valueOf(4000);

    public PayrollEngine() {
        this.employees = new ConcurrentHashMap<>();
    }

    public void addEmployee(Employee emp) {
        employees.put(emp.getEmployeeId(), emp);
    }

    public PayrollResult runPayroll(String employeeId, String periodName) {
        Employee emp = employees.get(employeeId);
        if (emp == null) throw new IllegalArgumentException("Employee not found: " + employeeId);

        BigDecimal grossPay = calculatePeriodicSalary(emp);
        BigDecimal taxWithheld = calculateTax(emp, grossPay);
        BigDecimal netPay = grossPay.subtract(taxWithheld).max(BigDecimal.ZERO);

        PayrollResult result = new PayrollResult(employeeId, periodName, grossPay, taxWithheld, netPay);
        result.addElement("Base Salary", grossPay);
        result.addElement("Tax Withholding", taxWithheld.negate());
        return result;
    }

    private BigDecimal calculatePeriodicSalary(Employee emp) {
        return switch (emp.getFrequency()) {
            case WEEKLY -> emp.getSalary().divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
            case BIWEEKLY -> emp.getSalary().divide(BigDecimal.valueOf(26), 2, RoundingMode.HALF_UP);
            case MONTHLY -> emp.getSalary().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case SEMIMONTHLY -> emp.getSalary().divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);
        };
    }

    private BigDecimal calculateTax(Employee emp, BigDecimal gross) {
        BigDecimal allowedExemptions = EXEMPTION_AMOUNT.multiply(BigDecimal.valueOf(emp.getAllownces()));
        BigDecimal taxableIncome = gross.subtract(allowedExemptions).max(BigDecimal.ZERO);
        return taxableIncome.multiply(emp.getTaxRate()).setScale(2, RoundingMode.HALF_UP);
    }

    public static PayrollEngine createDefault() {
        PayrollEngine eng = new PayrollEngine();
        eng.addEmployee(new Employee("E001", "Alice Smith", BigDecimal.valueOf(120000), PayrollFrequency.MONTHLY, "US", BigDecimal.valueOf(0.22), 2));
        eng.addEmployee(new Employee("E002", "Bob Jones", BigDecimal.valueOf(75000), PayrollFrequency.BIWEEKLY, "US", BigDecimal.valueOf(0.18), 1));
        eng.addEmployee(new Employee("E003", "Carol Lee", BigDecimal.valueOf(95000), PayrollFrequency.SEMIMONTHLY, "UK", BigDecimal.valueOf(0.20), 0));
        return eng;
    }
}
