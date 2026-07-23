-- ============================================================================
-- PLSQL_StoredProcedures.sql
-- Oracle PL/SQL procedures, functions, packages, and triggers for APEX
-- Covers stored procedures, functions, packages, triggers, collections,
-- bulk operations, dynamic SQL, and error handling
-- ============================================================================

-- ============================================================================
-- SECTION 1: Stored Procedures and Functions
-- ============================================================================

-- 1.1 Simple stored procedure to update employee salary
CREATE OR REPLACE PROCEDURE update_employee_salary(
    p_empno    IN emp.empno%TYPE,
    p_new_sal  IN emp.sal%TYPE,
    p_comm_pct IN NUMBER DEFAULT NULL
) AS
    v_old_sal emp.sal%TYPE;
BEGIN
    -- Get current salary for audit
    SELECT sal INTO v_old_sal FROM emp WHERE empno = p_empno;

    -- Update salary
    UPDATE emp
    SET sal = p_new_sal,
        comm = CASE WHEN p_comm_pct IS NOT NULL
                    THEN p_new_sal * p_comm_pct / 100
                    ELSE comm
               END,
        updated_at = SYSDATE
    WHERE empno = p_empno;

    -- Audit log
    INSERT INTO salary_audit (empno, old_sal, new_sal, changed_by, change_date)
    VALUES (p_empno, v_old_sal, p_new_sal, USER, SYSDATE);

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Employee ' || p_empno || ' not found');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_employee_salary;
/

-- 1.2 Function to calculate annual compensation
CREATE OR REPLACE FUNCTION calculate_annual_comp(
    p_empno IN emp.empno%TYPE
) RETURN NUMBER DETERMINISTIC AS
    v_sal   emp.sal%TYPE;
    v_comm  emp.comm%TYPE;
BEGIN
    SELECT sal, NVL(comm, 0)
    INTO v_sal, v_comm
    FROM emp
    WHERE empno = p_empno;

    RETURN (v_sal * 12) + v_comm;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END calculate_annual_comp;
/

-- 1.3 Procedure with OUT parameters
CREATE OR REPLACE PROCEDURE get_dept_summary(
    p_deptno    IN  emp.deptno%TYPE,
    p_emp_count OUT NUMBER,
    p_avg_sal   OUT NUMBER,
    p_max_sal   OUT NUMBER,
    p_min_sal   OUT NUMBER,
    p_cursor    OUT SYS_REFCURSOR
) AS
BEGIN
    SELECT COUNT(*), AVG(sal), MAX(sal), MIN(sal)
    INTO p_emp_count, p_avg_sal, p_max_sal, p_min_sal
    FROM emp
    WHERE deptno = p_deptno;

    OPEN p_cursor FOR
        SELECT empno, ename, job, sal, comm
        FROM emp
        WHERE deptno = p_deptno
        ORDER BY sal DESC;
END get_dept_summary;
/

-- ============================================================================
-- SECTION 2: Packages
-- ============================================================================

-- 2.1 Package specification for employee management
CREATE OR REPLACE PACKAGE emp_mgmt_pkg AS
    -- Constants
    C_MAX_SALARY CONSTANT NUMBER := 50000;
    C_MIN_SALARY CONSTANT NUMBER := 1000;

    -- Types
    TYPE emp_record IS RECORD (
        empno   emp.empno%TYPE,
        ename   emp.ename%TYPE,
        job     emp.job%TYPE,
        sal     emp.sal%TYPE
    );

    TYPE emp_table IS TABLE OF emp_record;

    -- Functions
    FUNCTION get_salary(p_empno NUMBER) RETURN NUMBER;
    FUNCTION get_employee(p_empno NUMBER) RETURN emp_record;

    -- Procedures
    PROCEDURE hire_employee(
        p_ename    VARCHAR2,
        p_job      VARCHAR2,
        p_mgr      NUMBER DEFAULT NULL,
        p_sal      NUMBER,
        p_deptno   NUMBER
    );

    PROCEDURE fire_employee(p_empno NUMBER);

    PROCEDURE give_raise(
        p_empno   NUMBER,
        p_pct     NUMBER DEFAULT 10
    );

    -- Cursor
    CURSOR emp_by_dept_cur(p_deptno NUMBER) IS
        SELECT empno, ename, job, sal
        FROM emp
        WHERE deptno = p_deptno
        ORDER BY ename;
END emp_mgmt_pkg;
/

-- 2.2 Package body
CREATE OR REPLACE PACKAGE BODY emp_mgmt_pkg AS

    FUNCTION get_salary(p_empno NUMBER) RETURN NUMBER IS
        v_sal emp.sal%TYPE;
    BEGIN
        SELECT sal INTO v_sal FROM emp WHERE empno = p_empno;
        RETURN v_sal;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RETURN NULL;
    END get_salary;

    FUNCTION get_employee(p_empno NUMBER) RETURN emp_record IS
        v_rec emp_record;
    BEGIN
        SELECT empno, ename, job, sal
        INTO v_rec
        FROM emp
        WHERE empno = p_empno;
        RETURN v_rec;
    END get_employee;

    PROCEDURE hire_employee(
        p_ename    VARCHAR2,
        p_job      VARCHAR2,
        p_mgr      NUMBER DEFAULT NULL,
        p_sal      NUMBER,
        p_deptno   NUMBER
    ) IS
        v_empno NUMBER;
    BEGIN
        -- Validate salary range
        IF p_sal < C_MIN_SALARY OR p_sal > C_MAX_SALARY THEN
            RAISE_APPLICATION_ERROR(-20002, 'Salary outside allowed range');
        END IF;

        -- Generate new employee ID
        SELECT NVL(MAX(empno), 0) + 1 INTO v_empno FROM emp;

        INSERT INTO emp (empno, ename, job, mgr, hiredate, sal, deptno)
        VALUES (v_empno, p_ename, p_job, p_mgr, SYSDATE, p_sal, p_deptno);

        COMMIT;
    END hire_employee;

    PROCEDURE fire_employee(p_empno NUMBER) IS
    BEGIN
        -- Reassign direct reports to employee's manager
        UPDATE emp
        SET mgr = (SELECT mgr FROM emp WHERE empno = p_empno)
        WHERE mgr = p_empno;

        DELETE FROM emp WHERE empno = p_empno;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Employee not found');
        END IF;

        COMMIT;
    END fire_employee;

    PROCEDURE give_raise(
        p_empno   NUMBER,
        p_pct     NUMBER DEFAULT 10
    ) IS
        v_new_sal emp.sal%TYPE;
    BEGIN
        UPDATE emp
        SET sal = sal * (1 + p_pct / 100)
        WHERE empno = p_empno
        RETURNING sal INTO v_new_sal;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Employee not found');
        END IF;

        COMMIT;
    END give_raise;

END emp_mgmt_pkg;
/

-- ============================================================================
-- SECTION 3: Triggers
-- ============================================================================

-- 3.1 Audit trigger for salary changes
CREATE OR REPLACE TRIGGER trg_emp_salary_audit
    BEFORE UPDATE OF sal ON emp
    FOR EACH ROW
DECLARE
BEGIN
    IF :OLD.sal != :NEW.sal THEN
        INSERT INTO salary_audit (
            audit_id, empno, old_sal, new_sal,
            changed_by, change_date, change_type
        ) VALUES (
            salary_audit_seq.NEXTVAL,
            :OLD.empno,
            :OLD.sal,
            :NEW.sal,
            NVL(apex_util.get_session_user, USER),
            SYSDATE,
            'SALARY_CHANGE'
        );
    END IF;
END trg_emp_salary_audit;
/

-- 3.2 Before insert trigger for validation
CREATE OR REPLACE TRIGGER trg_emp_before_insert
    BEFORE INSERT ON emp
    FOR EACH ROW
BEGIN
    -- Auto-generate employee ID
    IF :NEW.empno IS NULL THEN
        SELECT NVL(MAX(empno), 0) + 1 INTO :NEW.empno FROM emp;
    END IF;

    -- Set default hire date
    IF :NEW.hiredate IS NULL THEN
        :NEW.hiredate := SYSDATE;
    END IF;

    -- Validate salary
    IF :NEW.sal < 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Salary cannot be negative');
    END IF;
END trg_emp_before_insert;
/

-- 3.3 Instead-of trigger for view
CREATE OR REPLACE VIEW emp_dept_view AS
SELECT e.empno, e.ename, e.job, e.sal, d.dname, d.loc
FROM emp e
JOIN dept d ON e.deptno = d.deptno;

CREATE OR REPLACE TRIGGER trg_emp_dept_view
    INSTEAD OF INSERT ON emp_dept_view
    FOR EACH ROW
DECLARE
    v_deptno dept.deptno%TYPE;
BEGIN
    -- Find or create department
    BEGIN
        SELECT deptno INTO v_deptno
        FROM dept
        WHERE dname = :NEW.dname;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT NVL(MAX(deptno), 0) + 10 INTO v_deptno FROM dept;
            INSERT INTO dept (deptno, dname) VALUES (v_deptno, :NEW.dname);
    END;

    INSERT INTO emp (empno, ename, job, sal, deptno)
    VALUES (:NEW.empno, :NEW.ename, :NEW.job, :NEW.sal, v_deptno);
END trg_emp_dept_view;
/

-- ============================================================================
-- SECTION 4: Collections (PL/SQL Tables)
-- ============================================================================

-- 4.1 Associative array (index-by table)
DECLARE
    TYPE salary_map IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    v_salaries salary_map;
    v_empno emp.empno%TYPE := 7369;
BEGIN
    -- Populate collection
    SELECT sal INTO v_salaries(v_empno) FROM emp WHERE empno = v_empno;
    v_salaries(7900) := 2500;
    v_salaries(7934) := 2800;

    -- Iterate
    v_empno := v_salaries.FIRST;
    WHILE v_empno IS NOT NULL LOOP
        DBMS_OUTPUT.PUT_LINE('Emp ' || v_empno || ': $' || v_salaries(v_empno));
        v_empno := v_salaries.NEXT(v_empno);
    END LOOP;
END;
/

-- 4.2 Nested table with BULK COLLECT
DECLARE
    TYPE emp_list IS TABLE OF emp%ROWTYPE;
    v_employees emp_list;
BEGIN
    SELECT *
    BULK COLLECT INTO v_employees
    FROM emp
    WHERE deptno = 20
    ORDER BY ename;

    FOR i IN 1..v_employees.COUNT LOOP
        DBMS_OUTPUT.PUT_LINE(v_employees(i).ename || ' - ' || v_employees(i).job);
    END LOOP;
END;
/

-- 4.3 Varrays (variable-size arrays)
DECLARE
    TYPE dept_list IS VARRAY(10) OF dept.dname%TYPE;
    v_depts dept_list := dept_list();
BEGIN
    SELECT dname
    BULK COLLECT INTO v_depts
    FROM dept
    ORDER BY dname;

    FOR i IN 1..v_depts.COUNT LOOP
        DBMS_OUTPUT.PUT_LINE('Department: ' || v_depts(i));
    END LOOP;
END;
/

-- ============================================================================
-- SECTION 5: Bulk Operations (FORALL)
-- ============================================================================

-- 5.1 BULK COLLECT with LIMIT clause
DECLARE
    CURSOR emp_cur IS SELECT * FROM emp WHERE deptno = 30;
    TYPE emp_tab IS TABLE OF emp_cur%ROWTYPE;
    v_emps emp_tab;
BEGIN
    OPEN emp_cur;
    LOOP
        FETCH emp_cur BULK COLLECT INTO v_emps LIMIT 100;
        EXIT WHEN v_emps.COUNT = 0;

        FOR i IN 1..v_emps.COUNT LOOP
            DBMS_OUTPUT.PUT_LINE('Processing: ' || v_emps(i).ename);
        END LOOP;
    END LOOP;
    CLOSE emp_cur;
END;
/

-- 5.2 FORALL for bulk DML
DECLARE
    TYPE empno_list IS TABLE OF emp.empno%TYPE;
    v_empnos empno_list := empno_list(7369, 7499, 7521);
BEGIN
    FORALL i IN 1..v_empnos.COUNT
        UPDATE emp SET sal = sal * 1.10
        WHERE empno = v_empnos(i);

    DBMS_OUTPUT.PUT_LINE('Updated ' || SQL%ROWCOUNT || ' employees');
    COMMIT;
END;
/

-- 5.3 FORALL with SAVE EXCEPTIONS
DECLARE
    TYPE empno_list IS TABLE OF emp.empno%TYPE;
    v_empnos empno_list := empno_list(7369, 9999, 7521); -- 9999 doesn't exist
    v_error_count NUMBER;
BEGIN
    FORALL i IN 1..v_empnos.COUNT SAVE EXCEPTIONS
        UPDATE emp SET sal = sal * 1.10
        WHERE empno = v_empnos(i);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        v_error_count := SQL%BULK_EXCEPTIONS.COUNT;
        DBMS_OUTPUT.PUT_LINE('Errors: ' || v_error_count);
        FOR i IN 1..v_error_count LOOP
            DBMS_OUTPUT.PUT_LINE('Error ' || i || ': ' ||
                SQLERRM(-SQL%BULK_EXCEPTIONS(i).ERROR_CODE));
        END LOOP;
END;
/

-- ============================================================================
-- SECTION 6: Dynamic SQL (EXECUTE IMMEDIATE)
-- ============================================================================

-- 6.1 Dynamic SELECT with EXECUTE IMMEDIATE
CREATE OR REPLACE PROCEDURE dynamic_employee_query(
    p_column   VARCHAR2,
    p_value    VARCHAR2,
    p_cursor   OUT SYS_REFCURSOR
) AS
    v_sql VARCHAR2(4000);
BEGIN
    -- Validate column name to prevent SQL injection
    IF p_column NOT IN ('ename', 'job', 'deptno') THEN
        RAISE_APPLICATION_ERROR(-20004, 'Invalid column name');
    END IF;

    v_sql := 'SELECT empno, ename, job, sal FROM emp WHERE ' ||
             DBMS_ASSERT.ENQUOTE_NAME(p_column, FALSE) ||
             ' = :val';

    OPEN p_cursor FOR v_sql USING p_value;
END dynamic_employee_query;
/

-- 6.2 Dynamic DDL
CREATE OR REPLACE PROCEDURE create_employee_table(
    p_table_name VARCHAR2
) AS
    v_sql VARCHAR2(4000);
BEGIN
    -- Validate table name
    v_sql := 'CREATE TABLE ' || DBMS_ASSERT.SIMPLE_SQL_NAME(p_table_name) || ' (
        empno    NUMBER(4) PRIMARY KEY,
        ename    VARCHAR2(10),
        job      VARCHAR2(9),
        sal      NUMBER(7,2),
        deptno   NUMBER(2)
    )';

    EXECUTE IMMEDIATE v_sql;
END create_employee_table;
/

-- 6.3 Dynamic PL/SQL block execution
CREATE OR REPLACE PROCEDURE execute_dynamic_raise(
    p_empno    NUMBER,
    p_percent  NUMBER
) AS
    v_block VARCHAR2(500);
BEGIN
    v_block := 'BEGIN
                    UPDATE emp SET sal = sal * (1 + :pct / 100)
                    WHERE empno = :eno;
                    COMMIT;
                END;';

    EXECUTE IMMEDIATE v_block USING p_percent, p_empno;
END execute_dynamic_raise;
/

-- ============================================================================
-- SECTION 7: Error Handling Patterns
-- ============================================================================

-- 7.1 Custom exception handling
CREATE OR REPLACE PROCEDURE safe_employee_update(
    p_empno    NUMBER,
    p_new_sal  NUMBER
) AS
    e_invalid_salary EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_invalid_salary, -20010);
BEGIN
    IF p_new_sal <= 0 THEN
        RAISE e_invalid_salary;
    END IF;

    UPDATE emp SET sal = p_new_sal WHERE empno = p_empno;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Employee not found');
    END IF;

    COMMIT;
EXCEPTION
    WHEN e_invalid_salary THEN
        DBMS_OUTPUT.PUT_LINE('Error: Invalid salary amount');
        RAISE;
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END safe_employee_update;
/

-- 7.2 Logging framework procedure
CREATE OR REPLACE PROCEDURE log_error(
    p_procedure_name VARCHAR2,
    p_error_code     NUMBER,
    p_error_message  VARCHAR2,
    p_error_stack    VARCHAR2 DEFAULT NULL
) AS
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    INSERT INTO error_log (
        log_id, procedure_name, error_code, error_message,
        error_stack, occurred_at, session_user
    ) VALUES (
        error_log_seq.NEXTVAL,
        p_procedure_name,
        p_error_code,
        p_error_message,
        p_error_stack,
        SYSDATE,
        USER
    );
    COMMIT;
END log_error;
/

-- ============================================================================
-- SECTION 8: APEX-Specific PL/SQL Patterns
-- ============================================================================

-- 8.1 APEX page process
BEGIN
    -- Insert order with session state values
    INSERT INTO orders (order_id, customer_id, order_date, status, created_by)
    VALUES (
        order_seq.NEXTVAL,
        :P101_CUSTOMER_ID,
        SYSDATE,
        'NEW',
        :APP_USER
    )
    RETURNING order_id INTO :P101_ORDER_ID;
END;

-- 8.2 APEX validation (return FALSE to fail)
BEGIN
    IF :P101_SALARY > 100000 THEN
        RETURN FALSE; -- Validation fails
    END IF;
    RETURN TRUE;
END;

-- 8.3 APEX computation
:P101_FULL_NAME := :P101_FIRST_NAME || ' ' || :P101_LAST_NAME;

-- 8.4 APEX dynamic action PL/SQL
BEGIN
    :P101_TOTAL := TO_NUMBER(:P101_QUANTITY) * TO_NUMBER(:P101_UNIT_PRICE);
END;

-- ============================================================================
-- END OF PL/SQL STORED PROCEDURES
-- ============================================================================
