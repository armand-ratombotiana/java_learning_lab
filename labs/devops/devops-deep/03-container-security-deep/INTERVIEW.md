# Interview Questions — Container Security

## Q1: What is the difference between seccomp and AppArmor?
**A:** Seccomp restricts Linux syscalls a process can make. AppArmor provides mandatory access control (files, network, capabilities). Both can be used together for defense in depth.

## Q2: Why should containers run as non-root?
**A:** Root in a container has the same UID 0 as root on the host (though namespaced). If an attacker escapes the container, they gain root access on the host. Using a non-root user (e.g., UID 10001) limits the blast radius.

## Q3: How does Falco detect runtime threats?
**A:** Falco uses kernel eBPF or a kernel module to intercept syscalls. It checks each syscall against rules (e.g., "shell spawned in container") and generates alerts for violations.

## Q4: What is an SBOM and why is it important?
**A:** Software Bill of Materials — a list of all components in a container image. Important for vulnerability tracking, license compliance, and supply chain security.
