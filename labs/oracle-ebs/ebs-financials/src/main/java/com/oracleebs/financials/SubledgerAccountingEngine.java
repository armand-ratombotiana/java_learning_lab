package com.oracleebs.financials;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SubledgerAccountingEngine {
    public enum AccountingEventType { INVOICE, PAYMENT, JOURNAL, ADJUSTMENT }

    public static class AccountingEntry {
        private final String id;
        private final AccountingEventType type;
        private final String accountCode;
        private final BigDecimal amount;
        private final String reference;
        private final Map<String, String> attributes;

        public AccountingEntry(String id, AccountingEventType type, String account, BigDecimal amount, String ref) {
            this.id = id;
            this.type = type;
            this.accountCode = account;
            this.amount = amount;
            this.reference = ref;
            this.attributes = new LinkedHashMap<>();
        }

        public void addAttribute(String key, String value) { attributes.put(key, value); }
        public String getId() { return id; }
        public AccountingEventType getType() { return type; }
        public String getAccountCode() { return accountCode; }
        public BigDecimal getAmount() { return amount; }
        public String getReference() { return reference; }
        public Map<String, String> getAttributes() { return Collections.unmodifiableMap(attributes); }
    }

    private final Map<String, AccountingEntry> entries;
    private final Map<String, String> accountMapping;

    public SubledgerAccountingEngine(Map<String, String> accountMapping) {
        this.entries = new ConcurrentHashMap<>();
        this.accountMapping = accountMapping;
    }

    public AccountingEntry createEntry(String id, AccountingEventType type, String sourceAccount, BigDecimal amount, String ref) {
        String targetAccount = accountMapping.getOrDefault(sourceAccount, sourceAccount);
        AccountingEntry entry = new AccountingEntry(id, type, targetAccount, amount, ref);
        entries.put(id, entry);
        return entry;
    }

    public Optional<AccountingEntry> getEntry(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public BigDecimal getTotalByType(AccountingEventType type) {
        return entries.values().stream()
            .filter(e -> e.getType() == type)
            .map(AccountingEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<AccountingEntry> getEntriesForAccount(String account) {
        return entries.values().stream()
            .filter(e -> e.getAccountCode().equals(account))
            .toList();
    }

    public int getEntryCount() {
        return entries.size();
    }

    public static SubledgerAccountingEngine createDefault() {
        Map<String, String> mapping = new LinkedHashMap<>();
        mapping.put("AP_INV", "01-000-2020");
        mapping.put("AP_PMT", "01-000-3030");
        mapping.put("AR_INV", "01-000-1010");
        mapping.put("AR_RCPT", "01-000-4040");
        return new SubledgerAccountingEngine(mapping);
    }
}
