# Lab 05: APEX Security

## Focus
Security is critical for APEX applications. This lab covers authentication schemes, authorization schemes, session state protection, access control, and vulnerability prevention.

## Topics Covered
- APEX security architecture
- Authentication schemes: Application Express Accounts, LDAP, SAML, OpenID Connect, Custom
- Authorization schemes: role-based, value-based, PL/SQL function returning Boolean
- Access control and Access Control Administration page
- Session state protection: page-level, item-level
- Page access protection: arguments checksum, URL validation
- SQL injection prevention: bind variables, APEX_ITEM, validation
- APEX_ACL and VPD integration
- CSRF protection: session state protection, page checksums
- XSS prevention: escaping, sanitization
- Security best practices for APEX applications

## Prerequisites
- Labs 01-04
- Understanding of authentication and authorization concepts

## Learning Objectives
- Configure authentication schemes for different requirements
- Implement role-based and value-based authorization
- Protect session state at page and item level
- Prevent common web vulnerabilities (SQL injection, XSS, CSRF)
- Integrate VPD with APEX for row-level security
- Audit and monitor security events
