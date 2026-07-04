# Security: MongoDB

## Authentication

```yaml
# mongod.conf
security:
  authorization: enabled
  # keyFile for replica set internal auth
  keyFile: /opt/mongodb/keyfile
```

```javascript
// Create admin user
use admin;
db.createUser({
  user: "admin",
  pwd: passwordPrompt(),
  roles: [{ role: "root", db: "admin" }]
});

// Create application user
use myapp;
db.createUser({
  user: "app_user",
  pwd: passwordPrompt(),
  roles: [
    { role: "readWrite", db: "myapp" },
    { role: "read", db: "reports" }
  ]
});
```

```java
// Java driver authentication
String uri = "mongodb://app_user:password@localhost:27017/myapp"
    + "?authSource=myapp";
MongoClient client = MongoClients.create(uri);
```

## TLS/SSL

```yaml
net:
  tls:
    mode: requireTLS
    certificateKeyFile: /etc/ssl/mongodb.pem
    CAFile: /etc/ssl/ca.pem
```

```java
// Java driver with TLS
String uri = "mongodb://user:pass@host:27017/db"
    + "?tls=true"
    + "&tlsCAFile=/path/to/ca.pem"
    + "&tlsCertificateKeyFile=/path/to/client.pem";
```

## Encryption at Rest

```javascript
// Enable encryption
mongod --enableEncryption \
  --encryptionKeyFile /data/encryption/keyfile
```

## Queryable Encryption (6.0+)

```java
// Automatic client-side encryption
AutoEncryptionSettings.Builder aes = AutoEncryptionSettings.builder()
    .keyVaultNamespace("encryption.__keyVault")
    .kmsProviders(Map.of("local",
        Map.of("key", localKey)))
    .schemaMap(Map.of("myapp.users",
        encryptedFieldsMap));

MongoClient client = MongoClients.create(
    MongoClientSettings.builder()
        .autoEncryptionSettings(aes.build())
        .build());
```

## Field-Level Redaction

```javascript
// Projection to exclude sensitive fields
db.users.find({}, {
  password: 0,
  ssn: 0,
  creditCard: 0
});
```

## Network Security

```yaml
net:
  bindIp: 10.0.0.1,10.0.0.2  # not 0.0.0.0
  port: 27017
```

## Audit Logging

```yaml
auditLog:
  destination: file
  format: JSON
  path: /var/log/mongodb/audit.log
  filter: '{ atype: { $in: ["authCheck", "createUser", "dropCollection"] } }'
```

## Injection Prevention

```java
// WRONG: string interpolation
String json = "{\"name\": \"" + userName + "\"}";
Document doc = Document.parse(json);

// RIGHT: use filters
Document doc = users.find(Filters.eq("name", userName)).first();
```
