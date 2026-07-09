package com.oracleebs.hrms;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbsenceCalculator {
    public enum AbsenceType { SICKNESS, VACATION, PERSONAL, MATERNITY, PATERNITY, BEREAVEMENT }
    public enum AbsenceStatus { SUBMITTED, APPROVED, REJECTED, CANCELLED }

    public static class AbsenceEntry {
        private final String absenceId;
        private final String employeeId;
        private final AbsenceType type;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private AbsenceStatus status;
        private final String reason;
        private long durationDays;

        public AbsenceEntry(String id, String empId, AbsenceType type, LocalDate start, LocalDate end, String reason) {
            this.absenceId = id;
            this.employeeId = empId;
            this.type = type;
            this.startDate = start;
            this.endDate = end;
            this.reason = reason;
            this.status = AbsenceStatus.SUBMITTED;
            this.durationDays = ChronoUnit.DAYS.between(start, end) + 1;
        }

        public String getAbsenceId() { return absenceId; }
        public String getEmployeeId() { return employeeId; }
        public AbsenceType getType() { return type; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public AbsenceStatus getStatus() { return status; }
        public String getReason() { return reason; }
        public long getDurationDays() { return durationDays; }
        public void setStatus(AbsenceStatus s) { status = s; }
    }

    public static class AbsenceEntitlement {
        private final String employeeId;
        private final AbsenceType type;
        private final int totalDays;
        private final int usedDays;

        public AbsenceEntitlement(String empId, AbsenceType type, int total, int used) {
            this.employeeId = empId;
            this.type = type;
            this.totalDays = total;
            this.usedDays = used;
        }

        public String getEmployeeId() { return employeeId; }
        public AbsenceType getType() { return type; }
        public int getTotalDays() { return totalDays; }
        public int getUsedDays() { return usedDays; }
        public int getRemainingDays() { return totalDays - usedDays; }
    }

    private final Map<String, AbsenceEntry> absences;
    private final Map<String, List<AbsenceEntitlement>> entitlements;

    public AbsenceCalculator() {
        this.absences = new ConcurrentHashMap<>();
        this.entitlements = new ConcurrentHashMap<>();
    }

    public AbsenceEntry recordAbsence(String id, String empId, AbsenceType type, LocalDate start, LocalDate end, String reason) {
        AbsenceEntry entry = new AbsenceEntry(id, empId, type, start, end, reason);
        absences.put(id, entry);
        return entry;
    }

    public void setEntitlement(String empId, AbsenceEntitlement ent) {
        entitlements.computeIfAbsent(empId, k -> new ArrayList<>()).add(ent);
    }

    public boolean approveAbsence(String absenceId) {
        AbsenceEntry entry = absences.get(absenceId);
        if (entry == null) return false;
        var ents = entitlements.getOrDefault(entry.getEmployeeId(), List.of());
        var matching = ents.stream().filter(e -> e.getType() == entry.getType()).findFirst();
        if (matching.isPresent() && matching.get().getRemainingDays() < entry.getDurationDays()) {
            entry.setStatus(AbsenceStatus.REJECTED);
            return false;
        }
        entry.setStatus(AbsenceStatus.APPROVED);
        return true;
    }

    public int getTotalAbsenceDays(String employeeId, AbsenceType type, AbsenceStatus status) {
        return absences.values().stream()
            .filter(a -> a.getEmployeeId().equals(employeeId))
            .filter(a -> a.getType() == type)
            .filter(a -> a.getStatus() == status)
            .mapToLong(AbsenceEntry::getDurationDays)
            .mapToInt(l -> (int) l)
            .sum();
    }

    public List<AbsenceEntry> getAbsencesForEmployee(String empId) {
        return absences.values().stream()
            .filter(a -> a.getEmployeeId().equals(empId))
            .toList();
    }

    public static AbsenceCalculator createDefault() {
        AbsenceCalculator calc = new AbsenceCalculator();
        calc.setEntitlement("E001", new AbsenceEntitlement("E001", AbsenceType.VACATION, 20, 5));
        calc.setEntitlement("E001", new AbsenceEntitlement("E001", AbsenceType.SICKNESS, 10, 2));
        calc.setEntitlement("E002", new AbsenceEntitlement("E002", AbsenceType.VACATION, 15, 3));
        return calc;
    }
}
