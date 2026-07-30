# Incident Management — Step-by-Step Guide

## 1. Severity Levels
- **SEV1**: Critical — service down, all hands on deck (SLA breach).
- **SEV2**: Major — degraded but not fully down.
- **SEV3**: Minor — cosmetic, non-critical.
- **SEV4**: Low — question, feature request.

## 2. Incident Commander Model
- **IC**: owns the incident, coordinates response, makes decisions.
- **Deputy**: shadows IC, handles logistics.
- **Scribe**: documents timeline and actions in real-time.
- **SMEs**: subject matter experts who debug and fix.

## 3. Communication
- Initial alert: severity, impact, affected service, IC name.
- Updates: every 30min (SEV1) or 60min (SEV2) via status page.
- Resolution: summary of root cause, fix applied, next steps.

## 4. RCA & Blameless Culture
- RCA focuses on processes, not people.
- 5 Whys technique to find systemic root cause.
- Action items are treated as safety improvements.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab08/*.java
java --enable-preview -cp out com.devops.deep.lab08.IncidentLab
```
