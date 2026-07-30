# Identity Management — Study Guide

## Core Concepts

### LDAP
- Hierarchical directory: dc=example,dc=com → ou=users → cn=john
- Bind: authenticate with DN + password
- Search: base DN, scope (base/one/sub), filter, attributes
- Common attributes: uid, cn, sn, mail, memberOf

### SAML 2.0
- IdP (Identity Provider): authenticates users
- SP (Service Provider): provides service
- Assertion: XML with subject, conditions, attributes, auth statement
- Bindings: HTTP-Redirect, HTTP-POST, Artifact

### SCIM 2.0
- REST API: /Users, /Groups, /ServiceProviderConfig
- Standard schema: userName, name, emails, roles
- CRUD operations with PATCH for partial updates
- Filtering: filter=userName eq "john"

## Implementation Checklist
1. Hash passwords with bcrypt/argon2 before LDAP storage
2. Validate SAML assertions: signature, NotOnOrAfter, audience
3. Use UTC timestamps in SAML conditions
4. SCIM: always return id (UUID), support pagination
5. TOTP: 30-second windows, SHA-1 default, 6 digits

## Common Pitfalls
- Not validating SAML assertion signatures (XML signature wrapping)
- LDAP injection via unsanitized search filters
- SCIM: not handling pagination for large directories
- TOTP: clock drift issues if server time is inaccurate
