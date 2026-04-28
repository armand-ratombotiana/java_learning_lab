@echo off
echo ================================================================
echo    VALIDATING ALL MODULES - Elite Interview Preparation
echo ================================================================
echo.

echo [1/3] Module 01: Java Basics
echo --------------------------------
cd 01-core-java\01-java-basics
call mvn clean test -q
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Module 01 - All tests passing
) else (
    echo [FAIL] Module 01 - Check errors above
)
cd ..\..
echo.

echo [2/3] Module 02: OOP Concepts
echo --------------------------------
cd 01-core-java\02-oop-concepts
call mvn clean test -q
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Module 02 - All tests passing
) else (
    echo [FAIL] Module 02 - Check errors above
)
cd ..\..
echo.

echo [3/3] Module 03: Collections Framework
echo --------------------------------
cd 01-core-java\03-collections-framework
call mvn clean test -q
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Module 03 - All tests passing
) else (
    echo [FAIL] Module 03 - Check errors above
)
cd ..\..
echo.

echo ================================================================
echo    VALIDATION COMPLETE
echo ================================================================
echo.
echo Expected Results:
echo   - Module 01: 260 tests passing
echo   - Module 02: 91 tests passing
echo   - Module 03: 138 tests passing
echo   - Total: 489 tests passing
echo.
echo Next Steps:
echo   1. Review QUICK_START_GUIDE.md
echo   2. Study ELITE_INTERVIEW_PREPARATION_GUIDE.md
echo   3. Practice coding exercises daily
echo.
pause
