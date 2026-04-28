#!/bin/bash

###############################################################################
# Find Missing Module Implementations
# 
# This script scans the repository to identify modules that are documented
# but not yet implemented.
#
# Usage: ./find-missing-modules.sh
###############################################################################

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

# Counters
TOTAL_EXPECTED=0
TOTAL_IMPLEMENTED=0
TOTAL_MISSING=0

echo -e "${CYAN}"
echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║                                                                  ║"
echo "║           🔍 Missing Module Implementation Scanner 🔍           ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

###############################################################################
# Core Java Modules (01-core-java)
###############################################################################

echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${MAGENTA}  📦 Core Java Modules (01-core-java)${NC}"
echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo ""

CORE_JAVA_MODULES=(
    "01-java-basics"
    "02-oop-concepts"
    "03-collections-framework"
    "04-streams-api"
    "05-lambda-expressions"
    "06-concurrency"
    "07-java-io-nio"
    "08-generics"
    "09-reflection-annotations"
    "10-java-21-features"
)

for module in "${CORE_JAVA_MODULES[@]}"; do
    TOTAL_EXPECTED=$((TOTAL_EXPECTED + 1))
    if [ -d "01-core-java/$module" ] && [ -f "01-core-java/$module/pom.xml" ]; then
        echo -e "  ${GREEN}✓${NC} $module - IMPLEMENTED"
        TOTAL_IMPLEMENTED=$((TOTAL_IMPLEMENTED + 1))
    else
        echo -e "  ${RED}✗${NC} $module - MISSING"
        TOTAL_MISSING=$((TOTAL_MISSING + 1))
    fi
done

echo ""

###############################################################################
# Spring Boot Modules (02-spring-boot)
###############################################################################

echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${MAGENTA}  📦 Spring Boot Modules (02-spring-boot)${NC}"
echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo ""

SPRING_BOOT_MODULES=(
    "01-spring-boot-basics"
    "02-spring-data-jpa"
    "03-spring-security"
    "04-spring-rest-api"
    "05-spring-cloud"
    "06-spring-batch"
    "07-spring-integration"
    "08-spring-webflux"
    "09-spring-actuator"
    "10-spring-testing"
)

for module in "${SPRING_BOOT_MODULES[@]}"; do
    TOTAL_EXPECTED=$((TOTAL_EXPECTED + 1))
    if [ -d "02-spring-boot/$module" ] && [ -f "02-spring-boot/$module/pom.xml" ]; then
        echo -e "  ${GREEN}✓${NC} $module - IMPLEMENTED"
        TOTAL_IMPLEMENTED=$((TOTAL_IMPLEMENTED + 1))
    else
        echo -e "  ${RED}✗${NC} $module - MISSING"
        TOTAL_MISSING=$((TOTAL_MISSING + 1))
    fi
done

echo ""

###############################################################################
# Micronaut Modules
###############################################################################

echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${MAGENTA}  📦 Micronaut Modules (micronaut-learning)${NC}"
echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo ""

MICRONAUT_MODULES=(
    "01-hello-micronaut"
    "02-dependency-injection"
    "03-rest-api"
    "04-data-access"
    "05-security"
)

for module in "${MICRONAUT_MODULES[@]}"; do
    TOTAL_EXPECTED=$((TOTAL_EXPECTED + 1))
    if [ -d "micronaut-learning/$module" ] && [ -f "micronaut-learning/$module/pom.xml" ]; then
        echo -e "  ${GREEN}✓${NC} $module - IMPLEMENTED"
        TOTAL_IMPLEMENTED=$((TOTAL_IMPLEMENTED + 1))
    else
        echo -e "  ${RED}✗${NC} $module - MISSING"
        TOTAL_MISSING=$((TOTAL_MISSING + 1))
    fi
done

echo ""

###############################################################################
# Vert.x Incomplete Modules
###############################################################################

echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${MAGENTA}  📦 Vert.x Incomplete Modules (EclipseVert.XLearning)${NC}"
echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
echo ""

VERTX_INCOMPLETE=(
    "16-consul"
    "18-rate-limiting"
    "19-file-upload"
    "20-email"
    "21-scheduled-jobs"
    "22-oauth2"
    "23-sse"
    "24-health-metrics"
    "25-testing"
    "26-clustering"
    "27-multi-tenancy"
    "30-jpa-hibernate"
    "31-advanced-caching"
)

for module in "${VERTX_INCOMPLETE[@]}"; do
    TOTAL_EXPECTED=$((TOTAL_EXPECTED + 1))
    if [ -d "EclipseVert.XLearning/$module" ] && [ -f "EclipseVert.XLearning/$module/pom.xml" ]; then
        echo -e "  ${GREEN}✓${NC} $module - IMPLEMENTED"
        TOTAL_IMPLEMENTED=$((TOTAL_IMPLEMENTED + 1))
    else
        echo -e "  ${RED}✗${NC} $module - MISSING"
        TOTAL_MISSING=$((TOTAL_MISSING + 1))
    fi
done

echo ""

###############################################################################
# Summary
###############################################################################

echo -e "${CYAN}╔══════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║${NC}                    📊 SUMMARY REPORT                            ${CYAN}║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════════════════════════╝${NC}"
echo ""

IMPLEMENTATION_RATE=0
if [ $TOTAL_EXPECTED -gt 0 ]; then
    IMPLEMENTATION_RATE=$((TOTAL_IMPLEMENTED * 100 / TOTAL_EXPECTED))
fi

echo "Total Expected Modules: $TOTAL_EXPECTED"
echo -e "${GREEN}Implemented: $TOTAL_IMPLEMENTED${NC}"
echo -e "${RED}Missing: $TOTAL_MISSING${NC}"
echo "Implementation Rate: ${IMPLEMENTATION_RATE}%"
echo ""

# Progress bar
echo -n "Progress: ["
FILLED=$((IMPLEMENTATION_RATE / 2))
for ((i=0; i<50; i++)); do
    if [ $i -lt $FILLED ]; then
        echo -n "█"
    else
        echo -n "░"
    fi
done
echo "] ${IMPLEMENTATION_RATE}%"
echo ""

###############################################################################
# Priority Recommendations
###############################################################################

echo -e "${YELLOW}╔══════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${YELLOW}║${NC}              🎯 PRIORITY IMPLEMENTATION RECOMMENDATIONS          ${YELLOW}║${NC}"
echo -e "${YELLOW}╚══════════════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${BLUE}High Priority (Foundation):${NC}"
echo "  1. Core Java Modules (10 modules) - Essential foundation"
echo "  2. Spring Boot Modules (10 modules) - Most popular framework"
echo ""

echo -e "${BLUE}Medium Priority (Expansion):${NC}"
echo "  3. Micronaut Modules (5 modules) - Modern alternative"
echo "  4. Vert.x Incomplete Modules (13 modules) - Complete existing work"
echo ""

echo -e "${BLUE}Suggested Implementation Order:${NC}"
echo "  Phase 1: Core Java (01-java-basics to 05-lambda-expressions)"
echo "  Phase 2: Spring Boot Basics (01-spring-boot-basics to 04-spring-rest-api)"
echo "  Phase 3: Core Java Advanced (06-concurrency to 10-java-21-features)"
echo "  Phase 4: Spring Boot Advanced (05-spring-cloud to 10-spring-testing)"
echo "  Phase 5: Micronaut & Vert.x Completion"
echo ""

###############################################################################
# Generate Implementation Script
###############################################################################

echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║${NC}              🚀 QUICK IMPLEMENTATION COMMANDS                    ${GREEN}║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════╝${NC}"
echo ""

echo "To implement missing modules, run:"
echo ""
echo -e "${CYAN}# Generate all Core Java modules${NC}"
echo "./scripts/generate-core-java-modules.sh"
echo ""
echo -e "${CYAN}# Generate all Spring Boot modules${NC}"
echo "./scripts/generate-spring-boot-modules.sh"
echo ""
echo -e "${CYAN}# Generate all Micronaut modules${NC}"
echo "./scripts/generate-micronaut-modules.sh"
echo ""
echo -e "${CYAN}# Complete Vert.x modules${NC}"
echo "./scripts/complete-vertx-modules.sh"
echo ""

###############################################################################
# Save Report
###############################################################################

REPORT_FILE="missing-modules-report.txt"

cat > "$REPORT_FILE" <<EOF
╔══════════════════════════════════════════════════════════════════╗
║           Missing Module Implementation Report                   ║
║           Generated: $(date)                      ║
╚══════════════════════════════════════════════════════════════════╝

SUMMARY
-------
Total Expected Modules: $TOTAL_EXPECTED
Implemented: $TOTAL_IMPLEMENTED
Missing: $TOTAL_MISSING
Implementation Rate: ${IMPLEMENTATION_RATE}%

MISSING MODULES BY CATEGORY
----------------------------

Core Java (01-core-java):
EOF

for module in "${CORE_JAVA_MODULES[@]}"; do
    if [ ! -d "01-core-java/$module" ] || [ ! -f "01-core-java/$module/pom.xml" ]; then
        echo "  ✗ $module" >> "$REPORT_FILE"
    fi
done

cat >> "$REPORT_FILE" <<EOF

Spring Boot (02-spring-boot):
EOF

for module in "${SPRING_BOOT_MODULES[@]}"; do
    if [ ! -d "02-spring-boot/$module" ] || [ ! -f "02-spring-boot/$module/pom.xml" ]; then
        echo "  ✗ $module" >> "$REPORT_FILE"
    fi
done

cat >> "$REPORT_FILE" <<EOF

Micronaut (micronaut-learning):
EOF

for module in "${MICRONAUT_MODULES[@]}"; do
    if [ ! -d "micronaut-learning/$module" ] || [ ! -f "micronaut-learning/$module/pom.xml" ]; then
        echo "  ✗ $module" >> "$REPORT_FILE"
    fi
done

cat >> "$REPORT_FILE" <<EOF

Vert.x Incomplete (EclipseVert.XLearning):
EOF

for module in "${VERTX_INCOMPLETE[@]}"; do
    if [ ! -d "EclipseVert.XLearning/$module" ] || [ ! -f "EclipseVert.XLearning/$module/pom.xml" ]; then
        echo "  ✗ $module" >> "$REPORT_FILE"
    fi
done

echo ""
echo -e "${GREEN}Report saved to: $REPORT_FILE${NC}"
echo ""

if [ $TOTAL_MISSING -gt 0 ]; then
    echo -e "${YELLOW}⚠ Action Required: $TOTAL_MISSING modules need implementation${NC}"
    exit 1
else
    echo -e "${GREEN}✓ All modules are implemented!${NC}"
    exit 0
fi