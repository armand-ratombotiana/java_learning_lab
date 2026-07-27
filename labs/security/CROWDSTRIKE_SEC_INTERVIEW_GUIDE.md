# CrowdStrike Security Engineer — Interview Guide

> Complete preparation guide for security engineering roles at CrowdStrike.
> Covers endpoint security, Falcon platform, incident response, and threat intelligence.

---

## Role Overview

| Aspect | Detail |
|--------|--------|
| **Positions** | Security Engineer (Falcon OverWatch), Incident Response Consultant, Threat Intelligence Analyst |
| **Levels** | Associate to Principal / Director |
| **Locations** | Austin, Sunnyvale, London, Sydney, Tokyo, remote options |
| **Interview Difficulty** | High |
| **Coding Bar** | Practical — Python, PowerShell, Go |

## Interview Rounds

| Round | Focus | Duration | Key Topics |
|-------|-------|----------|------------|
| **Recruiter Screen** | Background, availability | 30 min | Experience, certifications |
| **Technical Screen** | Security scenario | 60 min | EDR concepts, incident response |
| **Onsite Technical** | Malware analysis, detection | 45 min | Malware reversing, detection patterns |
| **Onsite Scenario** | Case study | 60 min | Full incident response walkthrough |
| **Onsite Behavioral** | Leadership, culture | 45 min | Handling pressure, team collaboration |

## CrowdStrike-Specific Topics

### Falcon Platform
- **Sensor**: Lightweight agent, single sensor for all prevention/detection
- **Cloud-based**: Console in cloud, not on-prem
- **Machine Learning**: Static ML, runtime ML, behavioral ML
- **IOA vs IOC**: Indicator of Attack (behavioral, pattern-based) vs Indicator of Compromise (artifact-based)
- **Real-time Response**: Live shell, script execution, file quarantine

### Detection Techniques
- Process injection detection (CreateRemoteThread, APC injection, process hollowing)
- DLL sideloading detection
- Persistence mechanism detection (run keys, scheduled tasks, services, WMI)
- Credential access detection (LSASS, SAM, DPAPI)
- Lateral movement detection (RDP, SMB, WMI, PSExec)

### MITRE ATT&CK
- Mapping detections to ATT&CK techniques
- Coverage gap analysis
- Analytics-based detection vs signature-based
- Detection for each stage: Initial Access through Exfiltration

### Incident Response
- **NIST IR Framework**: Preparation, Detection, Containment, Eradication, Recovery, Lessons Learned
- **Ransomware response**: Isolation, backup assessment, extortion protection
- **Business email compromise**: Email tracing, account audit, recovery
- **Nation-state actor response**: Advanced persistent threat handling

### Malware Analysis
- Static analysis: PE structure, strings, imports, hashes
- Dynamic analysis: Sandbox execution, behavioral analysis
- Memory forensics: Volatility framework usage
- Packer identification: UPX, Themida, VMProtect

## Common Interview Questions

1. Walk through a complete ransomware incident response from detection to recovery
2. How does Falcon detect process injection vs legitimate process creation?
3. Design a detection rule for an adversary using WMI for lateral movement
4. What forensic artifacts are associated with a hands-on-keyboard attack?
5. How would you detect credential dumping via LSASS?
6. Design a threat hunting hypothesis for a supply chain compromise
7. How do you measure detection coverage against MITRE ATT&CK?
8. How would you handle a false positive impacting critical business systems?

## Behavioral Questions

1. Describe the most complex incident response you've led
2. How do you handle pressure during an active breach?
3. Tell me about a time you had to communicate a security incident to executives
4. How do you maintain technical depth while managing multiple cases?
5. Describe a detection you built that caught something significant

## Recommended Preparation

- CrowdStrike CTO blog (technical deep-dives)
- MITRE ATT&CK framework — memorize key techniques
- Windows Internals (Part 1) by Pavel Yosifovich
- Memory forensics with Volatility
- Practice malware analysis (MalwareBazaar, ANY.RUN)
- Understand EDR architectural differences (CrowdStrike vs SentinelOne vs Defender)
- Prepare for "while I'm on the phone with you" simulated IR scenarios
