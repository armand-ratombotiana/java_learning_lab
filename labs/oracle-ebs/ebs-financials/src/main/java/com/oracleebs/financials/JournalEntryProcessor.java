package com.oracleebs.financials;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JournalEntryProcessor {
    public enum JournalStatus { DRAFT, POSTED, REVERSED, ERROR }

    public static class JournalLine {
        private final String accountCode;
        private final BigDecimal amount;
        private final boolean debit;
        private final String description;

        public JournalLine(String accountCode, BigDecimal amount, boolean debit, String description) {
            this.accountCode = accountCode;
            this.amount = amount;
            this.debit = debit;
            this.description = description;
        }

        public String getAccountCode() { return accountCode; }
        public BigDecimal getAmount() { return amount; }
        public boolean isDebit() { return debit; }
        public String getDescription() { return description; }
    }

    public static class JournalEntry {
        private final String batchName;
        private final String journalName;
        private final LocalDate accountingDate;
        private final String currency;
        private JournalStatus status;
        private final List<JournalLine> lines;
        private String errorMessage;

        public JournalEntry(String batch, String journal, LocalDate date, String currency) {
            this.batchName = batch;
            this.journalName = journal;
            this.accountingDate = date;
            this.currency = currency;
            this.status = JournalStatus.DRAFT;
            this.lines = new ArrayList<>();
        }

        public void addLine(JournalLine line) { lines.add(line); }
        public String getBatchName() { return batchName; }
        public String getJournalName() { return journalName; }
        public LocalDate getAccountingDate() { return accountingDate; }
        public String getCurrency() { return currency; }
        public JournalStatus getStatus() { return status; }
        public List<JournalLine> getLines() { return Collections.unmodifiableList(lines); }
        public String getErrorMessage() { return errorMessage; }

        public void setStatus(JournalStatus s) { this.status = s; }
        public void setErrorMessage(String msg) { this.errorMessage = msg; }
    }

    private final Map<String, JournalEntry> journals;
    private final Set<String> validAccounts;

    public JournalEntryProcessor(Set<String> validAccounts) {
        this.journals = new ConcurrentHashMap<>();
        this.validAccounts = validAccounts;
    }

    public JournalEntry createJournal(String batch, String journal, LocalDate date, String currency) {
        JournalEntry je = new JournalEntry(batch, journal, date, currency);
        journals.put(journal, je);
        return je;
    }

    public ValidationResult validateAndPost(String journalName) {
        JournalEntry je = journals.get(journalName);
        if (je == null) return new ValidationResult(false, "Journal not found");

        for (JournalLine line : je.getLines()) {
            if (!validAccounts.contains(line.getAccountCode())) {
                je.setStatus(JournalStatus.ERROR);
                je.setErrorMessage("Invalid account: " + line.getAccountCode());
                return new ValidationResult(false, "Invalid account: " + line.getAccountCode());
            }
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        for (JournalLine line : je.getLines()) {
            if (line.isDebit()) totalDebit = totalDebit.add(line.getAmount());
            else totalCredit = totalCredit.add(line.getAmount());
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            je.setStatus(JournalStatus.ERROR);
            je.setErrorMessage("Debit/Credit imbalance: " + totalDebit + " vs " + totalCredit);
            return new ValidationResult(false, "Debit/Credit imbalance");
        }

        je.setStatus(JournalStatus.POSTED);
        return new ValidationResult(true, "Journal posted successfully");
    }

    public record ValidationResult(boolean success, String message) {}

    public static JournalEntryProcessor createDefault() {
        Set<String> accounts = Set.of("01-000-1010", "01-000-2020", "01-000-3030", "01-000-4040", "01-000-5050");
        return new JournalEntryProcessor(accounts);
    }
}
