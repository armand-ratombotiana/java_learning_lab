# Module 69: Data Governance & Privacy - Edge Cases & Pitfalls

---

## Pitfall 1: Leaking PII into Application Logs

### ❌ Wrong
Writing a `toString()` method for a User entity that outputs all fields, and then logging `logger.info("Updating user: " + user)` during a REST call. This writes plaintext passwords, SSNs, and emails into centralized log aggregators (like ELK or Datadog), making them readable by hundreds of developers.

### ✅ Correct
Use logging filters, regex masking patterns in `logback.xml`, or custom `@ToString.Exclude` annotations (if using Lombok) to ensure sensitive fields are obfuscated or dropped before the string is ever sent to the logging appender.

---

## Pitfall 2: Pseudo-Anonymization vs True Anonymization

### ❌ Wrong
Taking a dataset of hospital records, deleting the "Name" column, and handing it to an external data science vendor, assuming the data is now anonymous and safe from GDPR.

### ✅ Correct
Removing names does not anonymize data. If the dataset still contains "Zip Code," "Gender," and "Date of Birth," demographic studies show that ~87% of the US population can be uniquely re-identified by cross-referencing those three columns with public voter records. 
True anonymization requires techniques like **Data Blurring** (e.g., changing "Age 34" to an age range "30-40") or **K-Anonymity**, guaranteeing that an individual cannot be mathematically isolated from the group.

---

## Pitfall 3: Storing Immutable PII in Kafka or Blockchains

### ❌ Wrong
Publishing an event like `{"eventType": "UserRegistered", "ssn": "123-456-7890"}` to an Apache Kafka topic configured with infinite retention, or storing it on a blockchain.

### ✅ Correct
When the user files a GDPR "Right to be Forgotten" request, you must delete their data. You cannot modify messages inside an immutable Kafka log. 
Always use **Crypto-Shredding**. Encrypt the SSN with a unique per-user key. Publish the encrypted string to Kafka. To "forget" the user, you delete their unique key from the Key Management System (KMS). The message in Kafka remains, but it is cryptographically rendered into permanent gibberish.