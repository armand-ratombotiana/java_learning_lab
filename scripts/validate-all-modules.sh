#!/bin/bash

###############################################################################
# Multi-Agent All Modules Validation Script
# 
# This script validates ALL modules in the Java Learning Journey using
# the multi-agent validation system.
#
# Usage: ./validate-all-modules.sh
###############################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORT_DIR="$PROJECT_ROOT/validation-reports"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
SUMMARY_FILE="$REPORT_DIR/summary_${TIMESTAMP}.txt"

# Statistics
TOTAL_MODULES=0
PASSED_MODULES=0
FAILED_MODULES=0

###############################################################################
# Module Lists
###############################################################################

# Core Java Modules
CORE_JAVA_MODULES=(
    "01-core-java/01-hello-world"
    "01-core-java/02-variables-datatypes"
    "01-core-java/03-operators"
    "01-core-java/04-control-flow"
    "01-core-java/05-arrays"
    "01-core-java/06-strings"
    "01-core-java/07-oop-basics"
    "01-core-java/08-inheritance"
    "01-core-java/09-polymorphism"
    "01-core-java/10-abstraction"
)

# Spring Boot Modules
SPRING_BOOT_MODULES=(
    "02-spring-boot/01-spring-basics"
    "02-spring-boot/02-dependency-injection"
    "02-spring-boot/03-rest-api"
    "02-spring-boot/04-data-jpa"
    "02-spring-boot/05-security"
    "02-spring-boot/06-testing"
    "02-spring-boot/07-microservices"
    "02-spring-boot/08-messaging"
    "02-spring-boot/09-caching"
    "02-spring-boot/10-monitoring"
)

# Quarkus Modules
QUARKUS_MODULES=(
    "quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus"
    "quarkus-learning/02-Quarkus-Core/quarkus-config-demo"
    "quarkus-learning/03-Dependency-Injection/cdi-demo"
    "quarkus-learning/04-REST-Services/user-management-api"
    "quarkus-learning/05-Database-Panache/book-management-api"
    "quarkus-learning/06-DevServices/product-catalog"
    "quarkus-learning/07-Reactive-Programming/reactive-api"
    "quarkus-learning/08-Kafka-Messaging/event-driven-app"
    "quarkus-learning/09-Security-JWT/secure-api"
    "quarkus-learning/10-Testing-Strategies/testing-demo"
    "quarkus-learning/11-Quarkus-Cloud-Native/cloud-native-app"
    "quarkus-learning/12-Advanced-Topics/advanced-app"
    "quarkus-learning/13-WebSockets-RealTime/realtime-chat-app"
    "quarkus-learning/15-File-Upload-Storage/file-storage-app"
    "quarkus-learning/16-Caching-Strategies/caching-demo"
    "quarkus-learning/17-Rate-Limiting-Throttling/rate-limiting-demo"
    "quarkus-learning/19-Email-Notification-Services/email-notification-service"
)

# Vert.x Modules
VERTX_MODULES=(
    "EclipseVert.XLearning/01-vertx-basics"
    "EclipseVert.XLearning/02-event-bus"
    "EclipseVert.XLearning/03-http-server"
    "EclipseVert.XLearning/04-async-futures"
    "EclipseVert.XLearning/05-database-integration"
    "EclipseVert.XLearning/06-websockets"
    "EclipseVert.XLearning/07-microservices"
    "EclipseVert.XLearning/08-auth-jwt"
    "EclipseVert.XLearning/09-security"
    "EclipseVert.XLearning/10-kafka"
    "EclipseVert.XLearning/11-rabbitmq"
    "EclipseVert.XLearning/12-redis"
    "EclipseVert.XLearning/13-mongodb"
    "EclipseVert.XLearning/14-graphql"
    "EclipseVert.XLearning/15-grpc"
    "EclipseVert.XLearning/28-advanced-testing"
    "EclipseVert.XLearning/29-data-validation"
    "EclipseVert.XLearning/32-api-versioning"
)

# Micronaut Modules
MICRONAUT_MODULES=(
    "micronaut-learning/01-hello-micronaut"
    "micronaut-learning/02-dependency-injection"
    "micronaut-learning/03-rest-api"
    "micronaut-learning/04-data-access"
    "micronaut-learning/05-security"
)

###############################################################################
# Helper Functions
###############################################################################

print_banner() {
    echo -e "${CYAN}"
    echo "╔══════════════════════════════════════════════════════════════════╗"
    echo "║                                                                  ║"
    echo "║        🤖 Multi-Agent All Modules Validation System 🤖          ║"
    echo "║                                                                  ║"
    echo "║              Validating Every Module with 10+ Agents            ║"
    echo "║                                                                  ║"
    echo "╚══════════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo ""
}

print_category_header() {
    local category=$1
    echo ""
    echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${MAGENTA}  📦 $category${NC}"
    echo -e "${MAGENTA}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

validate_module() {
    local module_path=$1
    local module_name=$(basename "$module_path")
    
    TOTAL_MODULES=$((TOTAL_MODULES + 1))
    
    echo -e "${BLUE}▶ Validating: $module_name${NC}"
    
    # Check if module exists
    if [ ! -d "$PROJECT_ROOT/$module_path" ]; then
        echo -e "${YELLOW}  ⚠ Module not found: $module_path${NC}"
        echo "  ⚠ $module_name - NOT FOUND" >> "$SUMMARY_FILE"
        return
    fi
    
    # Run validation
    if bash "$SCRIPT_DIR/validate-module.sh" "$module_path" > /dev/null 2>&1; then
        echo -e "${GREEN}  ✓ $module_name - PASSED${NC}"
        echo "  ✓ $module_name - PASSED" >> "$SUMMARY_FILE"
        PASSED_MODULES=$((PASSED_MODULES + 1))
    else
        echo -e "${RED}  ✗ $module_name - FAILED${NC}"
        echo "  ✗ $module_name - FAILED" >> "$SUMMARY_FILE"
        FAILED_MODULES=$((FAILED_MODULES + 1))
    fi
}

validate_category() {
    local category_name=$1
    shift
    local modules=("$@")
    
    print_category_header "$category_name"
    
    for module in "${modules[@]}"; do
        validate_module "$module"
    done
}

print_final_summary() {
    echo ""
    echo -e "${CYAN}╔══════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║${NC}                    📊 FINAL VALIDATION SUMMARY                   ${CYAN}║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    local pass_rate=0
    if [ $TOTAL_MODULES -gt 0 ]; then
        pass_rate=$((PASSED_MODULES * 100 / TOTAL_MODULES))
    fi
    
    echo "Total Modules Validated: $TOTAL_MODULES"
    echo -e "${GREEN}Passed: $PASSED_MODULES${NC}"
    echo -e "${RED}Failed: $FAILED_MODULES${NC}"
    echo "Pass Rate: ${pass_rate}%"
    echo ""
    
    if [ $FAILED_MODULES -eq 0 ]; then
        echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║${NC}                  🎉 ALL MODULES PASSED! 🎉                      ${GREEN}║${NC}"
        echo -e "${GREEN}║${NC}              Every module is production-ready!                  ${GREEN}║${NC}"
        echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════╝${NC}"
    else
        echo -e "${YELLOW}╔══════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${YELLOW}║${NC}              ⚠ Some modules need improvement ⚠                 ${YELLOW}║${NC}"
        echo -e "${YELLOW}║${NC}          Please review the validation reports                  ${YELLOW}║${NC}"
        echo -e "${YELLOW}╚══════════════════════════════════════════════════════════════════╝${NC}"
    fi
    
    echo ""
    echo "Detailed reports saved to: $REPORT_DIR"
    echo "Summary saved to: $SUMMARY_FILE"
    echo ""
}

###############################################################################
# Main Execution
###############################################################################

main() {
    print_banner
    
    # Create report directory
    mkdir -p "$REPORT_DIR"
    
    # Initialize summary file
    echo "╔══════════════════════════════════════════════════════════════════╗" > "$SUMMARY_FILE"
    echo "║        Multi-Agent Module Validation Summary                    ║" >> "$SUMMARY_FILE"
    echo "║        Generated: $(date)                      ║" >> "$SUMMARY_FILE"
    echo "╚══════════════════════════════════════════════════════════════════╝" >> "$SUMMARY_FILE"
    echo "" >> "$SUMMARY_FILE"
    
    # Validate all categories
    validate_category "Core Java Modules" "${CORE_JAVA_MODULES[@]}"
    validate_category "Spring Boot Modules" "${SPRING_BOOT_MODULES[@]}"
    validate_category "Quarkus Modules" "${QUARKUS_MODULES[@]}"
    validate_category "Vert.x Modules" "${VERTX_MODULES[@]}"
    validate_category "Micronaut Modules" "${MICRONAUT_MODULES[@]}"
    
    # Print final summary
    print_final_summary
    
    # Return exit code
    if [ $FAILED_MODULES -eq 0 ]; then
        exit 0
    else
        exit 1
    fi
}

# Execute main function
main