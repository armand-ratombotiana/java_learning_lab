# Module 65: Incident Response & SRE - Edge Cases & Pitfalls

---

## Pitfall 1: Attempting to Fix Forward During an Outage

### ❌ Wrong
A bad deployment crashes production. The developer jumps into the IDE, furiously writes a patch, tries to compile it, runs tests locally, and attempts to push a hotfix to production 45 minutes later while the system remains completely down.

### ✅ Correct
During a SEV-1 outage, the immediate goal is **Mitigation**, not resolution. You must stop the bleeding as fast as possible. The correct action is almost always an immediate **Rollback** to the previously known good state (or scaling up, or disabling the feature flag). Only attempt to "fix forward" if a rollback is mathematically impossible (e.g., a destructive database schema change occurred).

---

## Pitfall 2: The "Hero" Engineer (Single Point of Failure)

### ❌ Wrong
Relying on one senior engineer ("Bob") who holds all the passwords, SSH keys, and architectural knowledge in his head. When the system goes down, the entire team stands around waiting for Bob to wake up and fix it.

### ✅ Correct
SRE demands automation and shared knowledge. Use automated runbooks/playbooks, Infrastructure as Code, and cross-train engineers so that any on-call developer can resolve a standard incident. The "Hero" culture is an anti-pattern that leads to massive burnout.

---

## Pitfall 3: Treating Alerts as Log Statements

### ❌ Wrong
Configuring a PagerDuty or Opsgenie alert to fire a text message to the on-call engineer every time an `IllegalStateException` occurs in the staging or production logs. The engineer gets 500 texts a day and sets their phone to Do Not Disturb.

### ✅ Correct
Alerts that page humans must be actionable, critical, and rare. If an alert fires, a human *must* take action immediately to prevent customer impact. If the alert requires no action, it should be deleted or downgraded to a simple dashboard metric.