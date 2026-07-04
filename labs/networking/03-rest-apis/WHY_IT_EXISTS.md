# REST APIs - Why It Exists

## The Problem REST Solved

Before REST, web APIs used various ad-hoc approaches like SOAP (complex, XML-heavy), XML-RPC (rigid), and custom protocols. REST provided:
1. **Uniform interface** across all services
2. **Leverages existing HTTP** infrastructure
3. **Scalability** through statelessness and caching
4. **Discoverability** via hypermedia (HATEOAS)
5. **Simplicity** compared to SOAP

## REST vs SOAP
| Aspect | REST | SOAP |
|--------|------|------|
| Protocol | HTTP | HTTP, SMTP, JMS |
| Format | JSON, XML, YAML | XML only |
| State | Stateless | Can be stateful |
| Caching | Built-in | Must implement |
| Tooling | Simple HTTP tools | Complex WS-* stack |
| Performance | Lightweight | Heavy (XML parsing) |
