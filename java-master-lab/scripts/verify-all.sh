#!/bin/bash

################################################################################
# Java Master Lab - Comprehensive Verification Script
# 
# This script verifies the entire Java Master Lab environment and all labs
# Usage: ./scripts/verify-all.sh [options]
# Options:
#   --quick     Run quick verification only
#   --full      Run full verification with tests
#   --lab N     Verify specific lab N
#   --help      Show this help message
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
LABS_DIR="$PROJECT_ROOT/labs"
LOG_FILE="$PROJECT_ROOT/verification.log"
FAILED_LABS=()
PASSED_LABS=()

# Default options
QUICK_MODE=false
FULL_MODE=true
SPECIFIC_LAB=""
VERBOSE=false

################################################################################
# Utility Functions
################################################################################

log() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$LOG_FILE"
}

print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}\n"
}

print_section() {
    echo -e "\n${YELLOW}--- $1 ---${NC}\n"
}

################################################################################
# Verification Functions
################################################################################

verify_java() {
    print_section "Verifying Java Installation"
    
    if ! command -v java &> /dev/null; then
        error "Java is not installed or not in PATH"
        return 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    log "Found: $JAVA_VERSION"
    
    # Check if Java 17+
    if java -version 2>&1 | grep -q "17\|18\|19\|20\|21"; then
        success "Java version is 17 or higher"
        return 0
    else
        warning "Java version is less than 17 (recommended: 17+)"
        return 0
    fi
}

verify_maven() {
    print_section "Verifying Maven Installation"
    
    if ! command -v mvn &> /dev/null; then
        error "Maven is not installed or not in PATH"
        return 1
    fi
    
    MVN_VERSION=$(mvn -version 2>&1 | head -n 1)
    log "Found: $MVN_VERSION"
    success "Maven is installed"
    return 0
}

verify_git() {
    print_section "Verifying Git Installation"
    
    if ! command -v git &> /dev/null; then
        error "Git is not installed or not in PATH"
        return 1
    fi
    
    GIT_VERSION=$(git --version)
    log "Found: $GIT_VERSION"
    success "Git is installed"
    return 0
}

verify_environment() {
    print_header "Environment Verification"
    
    local all_ok=true
    
    if ! verify_java; then
        all_ok=false
    fi
    
    if ! verify_maven; then
        all_ok=false
    fi
    
    if ! verify_git; then
        all_ok=false
    fi
    
    if [ "$all_ok" = true ]; then
        success "All environment requirements met"
        return 0
    else
        error "Some environment requirements are missing"
        return 1
    fi
}

verify_repository_structure() {
    print_header "Repository Structure Verification"
    
    local required_dirs=(
        "labs"
        "scripts"
        "templates"
        "docs"
        ".github"
    )
    
    local required_files=(
        "README.md"
        "CONTRIBUTING.md"
        "LICENSE"
        "pom.xml"
    )
    
    local all_ok=true
    
    print_section "Checking Required Directories"
    for dir in "${required_dirs[@]}"; do
        if [ -d "$PROJECT_ROOT/$dir" ]; then
            success "Found directory: $dir"
        else
            error "Missing directory: $dir"
            all_ok=false
        fi
    done
    
    print_section "Checking Required Files"
    for file in "${required_files[@]}"; do
        if [ -f "$PROJECT_ROOT/$file" ]; then
            success "Found file: $file"
        else
            error "Missing file: $file"
            all_ok=false
        fi
    done
    
    if [ "$all_ok" = true ]; then
        success "Repository structure is valid"
        return 0
    else
        error "Repository structure is incomplete"
        return 1
    fi
}

verify_lab() {
    local lab_num=$1
    local lab_dir=$(printf "%s/%02d-*" "$LABS_DIR" "$lab_num")
    
    # Expand glob pattern
    lab_dir=$(ls -d "$lab_dir" 2>/dev/null | head -n 1)
    
    if [ -z "$lab_dir" ]; then
        warning "Lab $lab_num not found"
        return 1
    fi
    
    local lab_name=$(basename "$lab_dir")
    print_section "Verifying Lab: $lab_name"
    
    # Check for README
    if [ ! -f "$lab_dir/README.md" ]; then
        error "Missing README.md in $lab_name"
        FAILED_LABS+=("$lab_name")
        return 1
    fi
    success "Found README.md"
    
    # Check for pom.xml (if it's a code lab)
    if [ -f "$lab_dir/pom.xml" ]; then
        log "Found pom.xml, verifying Maven project..."
        
        if [ "$FULL_MODE" = true ]; then
            # Build the lab
            if (cd "$lab_dir" && mvn clean compile -q 2>/dev/null); then
                success "Lab $lab_name compiles successfully"
                PASSED_LABS+=("$lab_name")
                return 0
            else
                error "Lab $lab_name failed to compile"
                FAILED_LABS+=("$lab_name")
                return 1
            fi
        else
            success "Lab $lab_name has valid pom.xml"
            PASSED_LABS+=("$lab_name")
            return 0
        fi
    else
        success "Lab $lab_name structure is valid"
        PASSED_LABS+=("$lab_name")
        return 0
    fi
}

verify_all_labs() {
    print_header "Lab Verification"
    
    if [ -n "$SPECIFIC_LAB" ]; then
        verify_lab "$SPECIFIC_LAB"
    else
        # Find all labs
        local lab_count=0
        for lab_dir in "$LABS_DIR"/*; do
            if [ -d "$lab_dir" ]; then
                local lab_name=$(basename "$lab_dir")
                local lab_num=$(echo "$lab_name" | grep -oE '^[0-9]+' || echo "")
                
                if [ -n "$lab_num" ]; then
                    verify_lab "$lab_num"
                    ((lab_count++))
                fi
            fi
        done
        
        log "Verified $lab_count labs"
    fi
}

verify_documentation() {
    print_header "Documentation Verification"
    
    print_section "Checking Documentation Files"
    
    local required_docs=(
        "docs/roadmap.md"
        "README.md"
        "CONTRIBUTING.md"
    )
    
    local all_ok=true
    
    for doc in "${required_docs[@]}"; do
        if [ -f "$PROJECT_ROOT/$doc" ]; then
            local lines=$(wc -l < "$PROJECT_ROOT/$doc")
            success "Found $doc ($lines lines)"
        else
            error "Missing documentation: $doc"
            all_ok=false
        fi
    done
    
    if [ "$all_ok" = true ]; then
        success "Documentation is complete"
        return 0
    else
        error "Documentation is incomplete"
        return 1
    fi
}

print_summary() {
    print_header "Verification Summary"
    
    local passed=${#PASSED_LABS[@]}
    local failed=${#FAILED_LABS[@]}
    local total=$((passed + failed))
    
    echo -e "${BLUE}Total Labs Verified:${NC} $total"
    echo -e "${GREEN}Passed:${NC} $passed"
    echo -e "${RED}Failed:${NC} $failed"
    
    if [ $failed -gt 0 ]; then
        echo -e "\n${RED}Failed Labs:${NC}"
        for lab in "${FAILED_LABS[@]}"; do
            echo -e "  ${RED}✗${NC} $lab"
        done
    fi
    
    if [ $passed -gt 0 ]; then
        echo -e "\n${GREEN}Passed Labs:${NC}"
        for lab in "${PASSED_LABS[@]}"; do
            echo -e "  ${GREEN}✓${NC} $lab"
        done
    fi
    
    echo -e "\n${BLUE}Log file:${NC} $LOG_FILE"
}

show_help() {
    cat << EOF
Java Master Lab - Verification Script

Usage: $0 [options]

Options:
    --quick         Run quick verification only (no compilation)
    --full          Run full verification with compilation (default)
    --lab N         Verify specific lab N (e.g., --lab 01)
    --verbose       Show verbose output
    --help          Show this help message

Examples:
    $0                      # Full verification of all labs
    $0 --quick              # Quick verification without compilation
    $0 --lab 01             # Verify only Lab 01
    $0 --lab 01 --full      # Full verification of Lab 01

EOF
}

################################################################################
# Main Script
################################################################################

main() {
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --quick)
                QUICK_MODE=true
                FULL_MODE=false
                shift
                ;;
            --full)
                QUICK_MODE=false
                FULL_MODE=true
                shift
                ;;
            --lab)
                SPECIFIC_LAB="$2"
                shift 2
                ;;
            --verbose)
                VERBOSE=true
                shift
                ;;
            --help)
                show_help
                exit 0
                ;;
            *)
                error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # Clear log file
    > "$LOG_FILE"
    
    # Print header
    print_header "Java Master Lab - Verification Script"
    log "Project Root: $PROJECT_ROOT"
    log "Mode: $([ "$FULL_MODE" = true ] && echo "FULL" || echo "QUICK")"
    
    # Run verifications
    verify_environment || exit 1
    verify_repository_structure || exit 1
    verify_documentation || exit 1
    verify_all_labs
    
    # Print summary
    print_summary
    
    # Exit with appropriate code
    if [ ${#FAILED_LABS[@]} -gt 0 ]; then
        exit 1
    else
        exit 0
    fi
}

# Run main function
main "$@"