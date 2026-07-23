# Mock Interview: PL/SQL Foundations (Lab 22)

**Role:** PL/SQL Developer (Mid-Level)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main components of a PL/SQL block?

**Candidate:** A PL/SQL block has three sections:
1. **Declaration section (DECLARE):** Variables, constants, cursors, exceptions
2. **Execution section (BEGIN...END):** Executable statements (mandatory)
3. **Exception section (EXCEPTION):** Error handling (optional)

```sql
DECLARE
    v_employee_name employees.last_name%TYPE;
    v_salary employees.salary%TYPE;
BEGIN
    SELECT last_name, salary INTO v_employee_name, v_salary
    FROM employees WHERE employee_id = 100;
    
    DBMS_OUTPUT.PUT_LINE('Name: ' || v_employee_name || ', Salary: ' || v_salary);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Employee not found');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Multiple employees found');
END;
```

**Interviewer:** Explain the different types of cursors in PL/SQL.

**Candidate:**
1. **Implicit cursor:** Automatically created for single-row SELECT or DML operations. Attributes: `SQL%FOUND`, `SQL%NOTFOUND`, `SQL%ROWCOUNT`, `SQL%ISOPEN`.
2. **Explicit cursor:** User-defined for multi-row queries:
   ```sql
   CURSOR emp_cursor IS SELECT employee_id, salary FROM employees;
   OPEN emp_cursor;
   LOOP FETCH emp_cursor INTO v_id, v_salary; EXIT WHEN emp_cursor%NOTFOUND; ... END LOOP;
   CLOSE emp_cursor;
   ```
3. **Cursor FOR loop:** Simplified explicit cursor iteration:
   ```sql
   FOR rec IN (SELECT employee_id, salary FROM employees) LOOP ... END LOOP;
   ```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Write a PL/SQL procedure that processes employees in bulk for a salary increase, with error logging.

**Candidate:**
```sql
CREATE OR REPLACE PROCEDURE process_salary_increase(
    p_department_id IN employees.department_id%TYPE,
    p_percentage IN NUMBER
) AS
    CURSOR emp_cursor IS
        SELECT employee_id, salary 
        FROM employees 
        WHERE department_id = p_department_id
        FOR UPDATE OF salary;
    
    v_counter NUMBER := 0;
    v_error_count NUMBER := 0;
BEGIN
    FOR rec IN emp_cursor LOOP
        BEGIN
            UPDATE employees 
            SET salary = salary * (1 + p_percentage / 100)
            WHERE CURRENT OF emp_cursor;
            
            v_counter := v_counter + 1;
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := v_error_count + 1;
                INSERT INTO salary_update_errors(employee_id, error_msg, timestamp)
                VALUES (rec.employee_id, SQLERRM, SYSTIMESTAMP);
        END;
    END LOOP;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Processed: ' || v_counter || ', Errors: ' || v_error_count);
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Salary increase failed: ' || SQLERRM);
END;
```

**Interviewer:** Explain exception handling in PL/SQL. What are user-defined exceptions?

**Candidate:** Exception handling in the EXCEPTION block. Types:
- **Predefined exceptions:** `NO_DATA_FOUND`, `TOO_MANY_ROWS`, `DUP_VAL_ON_INDEX`, `ZERO_DIVIDE`, `INVALID_CURSOR`
- **Non-predefined:** SQLCODE/SQLERRM for system exceptions without names
- **User-defined:** Declared in DECLARE section, raised with `RAISE`:
```sql
DECLARE
    e_insufficient_funds EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_insufficient_funds, -20001);
BEGIN
    IF balance < amount THEN
        RAISE e_insufficient_funds;
    END IF;
EXCEPTION
    WHEN e_insufficient_funds THEN
        DBMS_OUTPUT.PUT_LINE('Insufficient funds');
END;
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Write a PL/SQL package that provides a complete employee management interface with secure access control and audit logging.

**Candidate:** 
```sql
CREATE OR REPLACE PACKAGE emp_mgmt AS
    FUNCTION get_employee(p_emp_id NUMBER) RETURN SYS_REFCURSOR;
    PROCEDURE update_salary(p_emp_id NUMBER, p_new_salary NUMBER);
    PROCEDURE transfer_department(p_emp_id NUMBER, p_new_dept NUMBER);
    FUNCTION get_audit_trail(p_emp_id NUMBER DEFAULT NULL) RETURN SYS_REFCURSOR;
END emp_mgmt;
/

CREATE OR REPLACE PACKAGE BODY emp_mgmt AS
    -- Private: check user permissions
    FUNCTION check_access(p_emp_id NUMBER) RETURN BOOLEAN IS
    BEGIN
        -- Only HR managers or the employee themselves
        RETURN has_role('HR_MANAGER') OR 
               (has_role('EMPLOYEE') AND get_current_emp() = p_emp_id);
    END;
    
    -- Private: audit log
    PROCEDURE log_action(p_emp_id NUMBER, p_action VARCHAR2, p_details VARCHAR2) IS
        PRAGMA AUTONOMOUS_TRANSACTION;
    BEGIN
        INSERT INTO emp_audit(employee_id, action, details, user_name, timestamp)
        VALUES (p_emp_id, p_action, p_details, USER, SYSTIMESTAMP);
        COMMIT; -- Autonomous transaction can commit independently
    END;
    
    -- Public: get employee details
    FUNCTION get_employee(p_emp_id NUMBER) RETURN SYS_REFCURSOR IS
        v_cursor SYS_REFCURSOR;
    BEGIN
        IF NOT check_access(p_emp_id) THEN
            RAISE_APPLICATION_ERROR(-20001, 'Access denied');
        END IF;
        
        OPEN v_cursor FOR
            SELECT employee_id, first_name, last_name, email, 
                   department_id, salary
            FROM employees
            WHERE employee_id = p_emp_id;
        
        log_action(p_emp_id, 'SELECT', 'Employee details viewed');
        RETURN v_cursor;
    END;
    
    -- Public: update salary with validation
    PROCEDURE update_salary(p_emp_id NUMBER, p_new_salary NUMBER) IS
        v_old_salary employees.salary%TYPE;
    BEGIN
        IF NOT has_role('HR_MANAGER') THEN
            RAISE_APPLICATION_ERROR(-20001, 'Only HR managers can update salaries');
        END IF;
        
        IF p_new_salary < 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Salary cannot be negative');
        END IF;
        
        SELECT salary INTO v_old_salary FROM employees 
        WHERE employee_id = p_emp_id FOR UPDATE;
        
        UPDATE employees SET salary = p_new_salary 
        WHERE employee_id = p_emp_id;
        
        log_action(p_emp_id, 'SALARY_UPDATE', 
                   'Old: ' || v_old_salary || ', New: ' || p_new_salary);
    END;
    
    -- Public: transfer department
    PROCEDURE transfer_department(p_emp_id NUMBER, p_new_dept NUMBER) IS
        v_old_dept employees.department_id%TYPE;
    BEGIN
        SELECT department_id INTO v_old_dept FROM employees 
        WHERE employee_id = p_emp_id;
        
        UPDATE employees SET department_id = p_new_dept
        WHERE employee_id = p_emp_id;
        
        log_action(p_emp_id, 'DEPARTMENT_TRANSFER', 
                   'From dept: ' || v_old_dept || ', To dept: ' || p_new_dept);
    END;
    
    -- Public: view audit trail
    FUNCTION get_audit_trail(p_emp_id NUMBER DEFAULT NULL) RETURN SYS_REFCURSOR IS
        v_cursor SYS_REFCURSOR;
    BEGIN
        IF NOT has_role('AUDITOR') AND NOT has_role('HR_MANAGER') THEN
            RAISE_APPLICATION_ERROR(-20001, 'Access denied');
        END IF;
        
        IF p_emp_id IS NULL THEN
            OPEN v_cursor FOR SELECT * FROM emp_audit ORDER BY timestamp DESC;
        ELSE
            OPEN v_cursor FOR 
                SELECT * FROM emp_audit 
                WHERE employee_id = p_emp_id 
                ORDER BY timestamp DESC;
        END IF;
        
        RETURN v_cursor;
    END;
END emp_mgmt;
/
```

---

## Interviewer Feedback

**Strengths:** Solid PL/SQL fundamentals, good package design with security, practical autonomous transaction usage  
**Areas to Improve:** Could discuss bulk COLLECT and FORALL for performance  
**Verdict:** Hire

---

*Databases Lab 22 MOCK_INTERVIEW.md*
