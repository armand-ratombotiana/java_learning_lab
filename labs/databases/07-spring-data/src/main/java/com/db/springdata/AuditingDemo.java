package com.db.springdata;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Demonstrates Spring Data JPA Auditing.
 *
 * Auditing automatically populates created/updated timestamps
 * and the user who performed the operation.
 *
 * To enable: @EnableJpaAuditing on a @Configuration class.
 */
@Entity
@Table(name = "audited_entities")
@EntityListeners(AuditingEntityListener.class)
public class AuditingDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    // --- Auditing fields (auto-populated) ---

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    public AuditingDemo() {}

    public AuditingDemo(String data) {
        this.data = data;
    }

    public Long getId() { return id; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }

    /**
     * To wire up the 'createdBy' / 'updatedBy' fields, create a bean:
     *
     * &#64;Bean
     * AuditorAware<String> auditorProvider() {
     *     return () -> Optional.ofNullable(SecurityContextHolder
     *         .getContext().getAuthentication()?.getName());
     * }
     */

    public static void showInfo() {
        System.out.println("=== Spring Data JPA Auditing ===");
        System.out.println();
        System.out.println("@EntityListeners(AuditingEntityListener.class)");
        System.out.println();
        System.out.println("Auto-populated fields:");
        System.out.println("  @CreatedDate      → LocalDateTime createdAt (set once)");
        System.out.println("  @LastModifiedDate → LocalDateTime updatedAt (updated each save)");
        System.out.println("  @CreatedBy        → String createdBy (creator username)");
        System.out.println("  @LastModifiedBy   → String updatedBy (last modifier)");
        System.out.println();
        System.out.println("Configuration needed:");
        System.out.println("  1. @EnableJpaAuditing on a @Configuration class");
        System.out.println("  2. AuditorAware<String> bean providing current user");
        System.out.println();
        System.out.println("Benefits:");
        System.out.println("  - No manual timestamp management");
        System.out.println("  - Full audit trail for every entity");
        System.out.println("  - Works with Spring Security for user tracking");
    }

    public static void main(String[] args) {
        showInfo();
    }
}
