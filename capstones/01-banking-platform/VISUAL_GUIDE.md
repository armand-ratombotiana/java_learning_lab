# Visual Guide: Banking Platform

```
[Client] --> [Gateway Service] --> [Payment Service] --> [Account Service]
                                         |                      |
                                         v                      v
                                   [Fraud Service]       [Ledger DB]
                                         |
                                         v
                               [Notification Service]
```

## Architecture Diagram (ASCII)

```
                    +------------------+
                    |   Gateway API    |
                    +--------+---------+
                             |
          +------------------+--------------------+
          |         |                |              |
          v         v                v              v
   +----------+ +----------+ +-----------+ +-----------+
   | Account  | | Payment  | | Fraud     | |Notification|
   | Service  | | Service  | | Service   | | Service    |
   +----+-----+ +----+-----+ +-----+-----+ +-----+------+
        |            |              |              |
   [PostgreSQL] [PostgreSQL]  [Redis/ML]    [SES/SNS]
        |            |              |
   [Ledger DB] [Idempotency]  [Rule Engine]
```

## Event Flow Diagram
```
PaymentService --(payment.initiated)--> Kafka
    FraudService <--(payment.initiated)--> Kafka
    FraudService --(fraud.result)--> Kafka
PaymentService <--(fraud.result)--> Kafka
PaymentService --(payment.completed)--> Kafka
AccountService <--(payment.completed)--> Kafka
NotificationService <--(payment.completed)--> Kafka
```
