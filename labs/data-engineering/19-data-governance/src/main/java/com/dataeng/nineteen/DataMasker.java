package com.dataeng.nineteen;

import java.util.*;

public class DataMasker {
    public enum MaskingType { EMAIL, SSN, PHONE, CREDIT_CARD, GENERAL, PASSTHROUGH }

    public String mask(String value, MaskingType type) {
        if (value == null || value.isEmpty()) return value;
        return switch (type) {
            case EMAIL -> maskEmail(value);
            case SSN -> maskSSN(value);
            case PHONE -> maskPhone(value);
            case CREDIT_CARD -> maskCreditCard(value);
            case GENERAL -> maskGeneral(value);
            case PASSTHROUGH -> value;
        };
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex < 2) return email;
        return email.charAt(0) + "***" + email.substring(atIndex - 1);
    }

    private String maskSSN(String ssn) {
        if (ssn.length() < 4) return "***-**-****";
        return "***-**-" + ssn.substring(Math.max(0, ssn.length() - 4));
    }

    private String maskPhone(String phone) {
        String cleaned = phone.replaceAll("[^\\d]", "");
        if (cleaned.length() < 4) return phone;
        String last4 = cleaned.substring(cleaned.length() - 4);
        return "***-***-" + last4;
    }

    private String maskCreditCard(String cc) {
        String cleaned = cc.replaceAll("[^\\d]", "");
        if (cleaned.length() < 4) return cc;
        String last4 = cleaned.substring(cleaned.length() - 4);
        return "****-****-****-" + last4;
    }

    private String maskGeneral(String value) {
        if (value.length() <= 4) return "****";
        return value.substring(0, 1) + "***" + value.substring(value.length() - 1);
    }

    public String maskByRole(String value, String role, MaskingType type) {
        return switch (role) {
            case "admin" -> value;
            case "analyst" -> type == MaskingType.PASSTHROUGH ? value : mask(value, type);
            case "viewer" -> mask(value, type);
            default -> mask(value, MaskingType.GENERAL);
        };
    }
}
