# Lab 09: AI Security

## Learning Objectives
- Detect and prevent prompt injection attacks
- Implement data leakage prevention for sensitive information
- Design role-based access control for AI systems
- Build tamper-evident audit logging

## Concepts Covered
- **Prompt Injection**: Recognizing and blocking jailbreak attempts
- **Data Leakage**: Detecting PII/ secrets in prompts and outputs
- **Access Control**: RBAC for model endpoints and data
- **Audit Logging**: Immutable logs with integrity verification
- **Redaction**: Automatically sanitizing sensitive data

## Setup
```bash
cd lab09
javac src/com/aiengineering/lab09/AiSecurityDemo.java
java com.aiengineering.lab09.AiSecurityDemo
```

## Key Takeaways
- Prompt injection is the OWASP #1 LLM vulnerability
- Input sanitization is necessary but not sufficient
- Audit trails with integrity verification prevent tampering
