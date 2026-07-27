# Stripe Architecture Interview Guide (Staff+)

> Staff+ Engineer system design and leadership evaluation at Stripe.

---

## Table of Contents

1. [Stripe's Engineering Culture](#1-stripes-engineering-culture)
2. [Staff+ Level Expectations](#2-staff-level-expectations)
3. [System Design Interview Format](#3-system-design-interview-format)
4. [Common Stripe System Design Questions](#4-common-stripe-system-design-questions)
5. [Deep Dive: Design Stripe Payment Processing](#5-deep-dive-design-stripe-payment-processing)
6. [Deep Dive: Design Stripe Connect](#6-deep-dive-design-stripe-connect)
7. [Financial Correctness and Idempotency](#7-financial-correctness-and-idempotency)
8. [API Design Philosophy](#8-api-design-philosophy)
9. [Security and Compliance](#9-security-and-compliance)
10. [Behavioral and Leadership Evaluation](#10-behavioral-and-leadership-evaluation)
11. [Evaluation Rubric](#11-evaluation-rubric)
12. [Preparation Strategy](#12-preparation-strategy)

---

## 1. Stripe's Engineering Culture

### Key Cultural Tenets

- **User empathy**: Every engineer thinks about the developer experience of their APIs
- **Technical rigor**: Correctness is paramount in financial systems
- **Communication**: Written communication is taken extremely seriously (docs, RFCs, design docs)
- **Transparency**: Open communication about decisions, failures, and strategy
- **Craftsmanship**: Pride in well-designed, well-tested, well-documented systems

### What Stripe Values at Staff+

- **Financial systems expertise**: Understanding payments, ledgers, compliance
- **API design excellence**: Creating interfaces that are intuitive and hard to misuse
- **Reliability at scale**: Building systems that never lose data and never produce incorrect results
- **Security mindset**: Thinking about threats, encryption, and access control from day one
- **Empathy for users and teammates**: Designing for the people who use your systems

---

## 2. Staff+ Level Expectations

### Staff Engineer (S3)

- Drives technically complex projects across multiple teams
- Sets technical direction and architecture standards for a domain
- Mentors senior engineers and conducts thorough design reviews
- Deep expertise in payments, infrastructure, or platform

### Senior Staff Engineer (S4)

- Sets technical vision across Stripe's engineering organization
- Drives multi-year platform-wide initiatives
- Recognized expert both internally and externally
- Shapes engineering culture, hiring standards, and technical strategy

---

## 3. System Design Interview Format

### Structure

- **Duration**: 60 minutes per round
- **Format**: Virtual whiteboard or written document
- **Focus**: Financial systems, API design, reliability, security

### Time Allocation

| Phase | Time | Activity |
|-------|------|----------|
| Requirements | 5 min | Functional and non-functional requirements, constraints |
| API design | 10 min | Define the API surface (endpoints, request/response, idempotency) |
| Data model | 10 min | Schema design, consistency requirements |
| High-level design | 10 min | Components and interactions |
| Deep dive | 15 min | Critical component deep discussion |
| Trade-offs | 10 min | Alternative approaches, why chosen |

### Key Considerations for Stripe Interviews

- **Idempotency**: Every mutating operation must be idempotent
- **Exactly-once processing**: Financial transactions cannot be lost or duplicated
- **Audit trail**: Every state change must be recorded for auditing
- **Consistency**: Strong consistency for balances; eventual consistency for reporting
- **Security**: PCI compliance, encryption, tokenization

---

## 4. Common Stripe System Design Questions

### Tier 1 (Core Payments)

| Question | Key Focus Areas |
|----------|----------------|
| Design Stripe Payment Processing | Authorization, capture, settlement, multi-provider routing |
| Design Stripe Connect | Marketplace payments, onboarding, KYC/AML, multi-party payments |
| Design Stripe Billing | Subscription management, invoicing, dunning, metered billing |
| Design Stripe Radar | Fraud detection, real-time ML, feature engineering |

### Tier 2 (Infrastructure and Platform)

| Question | Key Focus Areas |
|----------|----------------|
| Design Stripe API | Developer experience, versioning, rate limiting, 1000+ endpoints |
| Design Stripe Data Pipeline | Event streaming, analytics, reporting |
| Design Stripe Identity | Identity verification, document upload, compliance |

### Tier 3 (Advanced)

| Question | Key Focus Areas |
|----------|----------------|
| Design Stripe Treasury | Banking-as-a-service, ledger, interest calculation |
| Design Stripe Atlas | Company formation, banking, tax — multi-country |
| Design Stripe Terminal | In-person payments, hardware integration, offline mode |

---

## 5. Deep Dive: Design Stripe Payment Processing

### Requirements

**Functional:**
- Accept payments from multiple payment methods (cards, wallets, bank transfers)
- Authorize and capture payments
- Support multiple currencies and countries
- Handle refunds and partial refunds
- Webhook notifications for payment events

**Non-functional:**
- 99.999% uptime (payment systems cannot go down)
- P99 latency < 200ms for authorization
- Exactly-once processing (no duplicate charges)
- Strong consistency for balances
- PCI-compliant (card data never touches merchant servers)

### Payment Flow

```
Merchant API → [Stripe API Gateway] → [Payment Service]
                                         │
                              ┌──────────┼──────────┐
                              │          │          │
                        ┌─────▼──┐ ┌────▼────┐ ┌────▼────┐
                        │Auth    │ │Capture  │ │Refund   │
                        │Service │ │Service  │ │Service  │
                        └────┬───┘ └────┬────┘ └────┬────┘
                             │          │           │
                        ┌────▼──────────▼───────────▼────┐
                        │     Provider Router            │
                        │  (Select processor: Stripe,    │
                        │   Chase, Worldpay, Adyen, etc.)│
                        └────────────┬───────────────────┘
                                     │
                        ┌────────────▼───────────────────┐
                        │     Payment Provider Gateway    │
                        │  (ISO 8583, gRPC, REST)         │
                        └─────────────────────────────────┘
```

### Key Components

**Authorization:**
- Merchant sends card number (tokenized) + amount + currency
- Auth service checks: fraud score (Radar), balance, velocity, restrictions
- Routes to payment provider based on cost, reliability, region
- Returns auth ID for later capture

**Capture:**
- Merchant captures authorized payment
- Capture service settles the payment (moves money from card to merchant)
- Records transaction in ledger

**Idempotency:**
- Each request has an `Idempotency-Key` header
- If same key within 24 hours, return cached response
- Prevents double charges on retry

### Data Model

```sql
-- Core payment tables (simplified)
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(64) UNIQUE,
    amount BIGINT NOT NULL,          -- in cents
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,    -- requires_payment_method, processing, succeeded, failed
    merchant_id UUID NOT NULL,
    customer_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE payment_attempts (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_transaction_id VARCHAR(255),
    amount BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,    -- succeeded, failed, pending
    error_code VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE balance_transactions (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    amount BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,      -- charge, refund, payout, fee
    created_at TIMESTAMP NOT NULL
);
```

---

## 6. Deep Dive: Design Stripe Connect

### Requirements

**Functional:**
- Platform can accept payments on behalf of connected accounts
- Funds flow: customer → platform → connected account
- Onboarding with KYC/AML verification
- Payouts to connected accounts (daily, weekly, monthly)
- Multi-sided marketplace support

**Non-functional:**
- Support for 100+ countries and currencies
- Compliance with local regulations (KYC, AML, tax reporting)
- P99 payout latency < 24 hours
- Strong consistency for account balances

### Architecture

```
Platform API → [Connect Service] → [Account Onboarding]
                                      │
                          ┌───────────┼───────────┐
                          │           │           │
                    ┌─────▼───┐ ┌────▼────┐ ┌─────▼─────┐
                    │Payment  │ │Payout   │ │KYC/AML    │
                    │Service  │ │Service  │ │Service    │
                    │         │ │         │ │           │
                    └────┬────┘ └────┬────┘ └─────┬─────┘
                         │           │             │
                    ┌────▼───────────▼─────────────▼────┐
                    │         Ledger Service             │
                    │  (Master ledger per account)       │
                    └────────────┬──────────────────────┘
                                 │
                    ┌────────────▼──────────────────────┐
                    │         Payout Rails              │
                    │  (ACH, Wire, SEPA, Local methods) │
                    └───────────────────────────────────┘
```

### Fund Flow

```
Customer → [Charge] → Platform → Stripe Fee → Stripe
                ↓
          [Application Fee]
                ↓
        Connected Account → [Payout] → Bank Account
```

### Key Decisions

**Account onboarding:**
- Collect business information (EIN, SSN, address)
- KYC verification (document upload, verification)
- Risk assessment (business type, volume projection)
- Bank account verification (micro-deposits, instant verification)

**Payout scheduling:**
- Rolling reserve (hold funds for 7-14 days for risk management)
- Payout frequency (daily, weekly, monthly)
- Minimum payout threshold

---

## 7. Financial Correctness and Idempotency

### Idempotency Architecture

**Idempotency key flow:**
```
1. Client generates UUID for each request
2. Server checks if key exists:
   - If yes: return cached response (idempotent replay)
   - If no: process request, cache response
3. Cache response for 24 hours with TTL
4. On replay within 24 hours → return same response
```

**Key storage:**
```
┌─────────────────────────────────────────────────┐
│ Idempotency Store (Redis/Database)              │
│ Key: IdempotencyKey                             │
│ Value: { response_status, response_body,        │
│          created_at, locked_until }              │
│ TTL: 24 hours                                    │
│ Locking: Optimistic lock for concurrent requests │
└─────────────────────────────────────────────────┘
```

### Exactly-Once Processing

1. **Idempotency keys** on all mutating API operations
2. **Idempotent consumers** for all async event processing
3. **Deduplication** at the database level (unique constraints on idempotency_key)
4. **Two-phase verification** for critical operations (capture, refund, payout)
5. **Reconciliation** — daily matching against provider statements

### Double-Spend Prevention

```
1. Check balance before processing
2. Reserve funds atomically (balance - pending)
3. Process payment
4. If success: confirm reservation
5. If failure: release reservation
6. On timeout: check actual state before releasing
```

---

## 8. API Design Philosophy

### Stripe's API Design Principles

1. **Be predictable**: Consistent patterns across all resources
2. **Be idempotent**: Every mutating operation supports idempotency keys
3. **Provide clear errors**: Error messages that tell you exactly what to fix
4. **Version carefully**: Backward compatibility is sacred
5. **Document exhaustively**: Every endpoint, parameter, and response field documented

### API Design Example

```http
POST /v1/charges
Idempotency-Key: unique-key-123
Content-Type: application/x-www-form-urlencoded

amount=2000&currency=usd&source=tok_mastercard&description=Test%20Charge

Response 200:
{
  "id": "ch_123",
  "object": "charge",
  "amount": 2000,
  "amount_captured": 2000,
  "amount_refunded": 0,
  "currency": "usd",
  "status": "succeeded",
  "source": {
    "id": "card_456",
    "object": "card",
    "last4": "4242",
    "brand": "Visa",
    "exp_month": 12,
    "exp_year": 2025
  },
  "created": 1704067200,
  "metadata": {}
}
```

### API Versioning Strategy

- **Date-based versioning**: `2023-10-16`, `2024-01-01`
- **Backward compatible changes**: Added to current version
- **Breaking changes**: New API version, old version supported for 2+ years
- **Upgrade path**: Migration guides, automated upgrade tools

---

## 9. Security and Compliance

### PCI Compliance

- **Card data never touches merchant servers**: Tokenization at client side
- **Stripe.js** collects card data directly to Stripe's PCI-compliant infrastructure
- **Merchant receives tokens**, not card numbers
- **Point-to-point encryption** for card data in transit

### Encryption

- **At rest**: AES-256 encryption for all stored data
- **In transit**: TLS 1.3 for all API traffic
- **Key management**: HSM-backed key management, automatic key rotation
- **Tokenization**: Sensitive data replaced with tokens

### Fraud Detection (Radar)

- Real-time ML models scoring every transaction
- Rules engine for custom business logic
- 3D Secure authentication integration
- Block/allow lists, velocity checks, IP geolocation

---

## 10. Behavioral and Leadership Evaluation

### Key Behavioral Themes

**Technical rigor:**
- "Tell me about the most complex technical problem you've solved"
- "How do you ensure correctness in systems you build?"
- "Describe your testing philosophy"

**User empathy:**
- "How do you design APIs that are hard to misuse?"
- "Tell me about a time you improved the developer experience"
- "How do you gather feedback from users of your systems?"

**Communication:**
- "Tell me about a design document you wrote that influenced a critical decision"
- "How do you communicate technical decisions to non-technical stakeholders?"
- "Describe a situation where written communication was critical to success"

**Transparency:**
- "Tell me about a time you shared bad news with stakeholders"
- "How do you handle postmortems for production incidents?"
- "What's a technical mistake you've made and what did you learn?"

**Craftsmanship:**
- "What does well-designed code mean to you?"
- "Tell me about a project where you paid extraordinary attention to detail"
- "How do you balance speed of delivery with code quality?"

---

## 11. Evaluation Rubric

### Staff+ Scoring

| Criteria | Weight | Staff+ Expectation |
|----------|--------|-------------------|
| System Design | 35% | Financially correct, idempotent, reliable, secure |
| Technical Depth | 25% | Deep expertise in payments, infrastructure, or platform |
| API Design | 15% | Intuitive, consistent, developer-friendly APIs |
| Communication | 15% | Clear written and verbal communication |
| Craftsmanship | 10% | Attention to quality, testing, and documentation |

### Common Rejection Reasons

1. **Weak financial systems knowledge**: No understanding of payments, ledgers, reconciliation
2. **Poor idempotency design**: Not addressing exactly-once semantics
3. **Weak API design**: Inconsistent, unclear, hard-to-extend API design
4. **No security mindset**: Not considering encryption, PCI, tokenization
5. **Weak communication**: Poor written communication in design documents

---

## 12. Preparation Strategy

### Week 1-2: Foundation
- Read Stripe's API documentation (api.stripe.com)
- Understand payment flow: authorization, capture, settlement, refund, chargeback
- Study idempotency and exactly-once processing patterns

### Week 3-4: System Design Practice
- Design 5-7 Stripe-scale systems (payment processing, Connect, Billing, Radar, API)
- Practice API-first design: define the API before the implementation
- Time yourself (60 minutes per design)

### Week 5-6: Behavioral & Security
- Prepare stories demonstrating technical rigor and user empathy
- Practice writing design documents (Stripe is big on docs)
- Study PCI compliance requirements and security best practices

### Must-Know Stripe Concepts

| Concept | Category | Interview Relevance |
|---------|----------|-------------------|
| Idempotency Key | API | Every mutating operation |
| Payment Intent | Payments | Core payment abstraction |
| Connect Account | Platform | Marketplace payments |
| Radar | Fraud | ML-based fraud detection |
| Webhook | Events | Event-driven notification |
| Tokenization | Security | Card data handling |
| Payout | Payments | Funds settlement |
| Ledger | Accounting | Balance tracking |
| SKU/Plan | Billing | Subscription management |
| Invoice | Billing | Billing and invoicing |

---

*Combine this guide with the ACADEMY_INTERVIEW_GUIDE.md and COMPANY_INTERVIEW_GUIDE.md for complete Stripe Staff+ interview preparation.*
