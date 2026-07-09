# How PL/SQL Works

## Basic Block
```sql
DECLARE
  v_emp_id    employees.emp_id%TYPE;      -- anchored type
  v_salary    NUMBER(8,2) NOT NULL := 0;  -- NOT NULL with default
  v_last_name VARCHAR2(100);
  e_invalid EXCEPTION;                     -- user-defined exception
BEGIN
  SELECT emp_id, salary, last_name
  INTO v_emp_id, v_salary, v_last_name
  FROM employees WHERE emp_id = 100;
  
  IF v_salary > 10000 THEN
    RAISE e_invalid;
  END IF;
  
  DBMS_OUTPUT.PUT_LINE(v_last_name || ': ' || v_salary);
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    DBMS_OUTPUT.PUT_LINE('Employee not found');
  WHEN e_invalid THEN
    DBMS_OUTPUT.PUT_LINE('Salary exceeds threshold');
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
```

## Select Into
```sql
DECLARE
  CURSOR emp_cur IS
    SELECT emp_id, ename, salary FROM employees WHERE dept_id = 10;
  v_rec emp_cur%ROWTYPE;
BEGIN
  OPEN emp_cur;
  LOOP
    FETCH emp_cur INTO v_rec;
    EXIT WHEN emp_cur%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE(v_rec.ename || ' earns ' || v_rec.salary);
  END LOOP;
  CLOSE emp_cur;
END;
```

## Cursor FOR Loop (Automatic)
```sql
BEGIN
  FOR rec IN (SELECT emp_id, ename, salary FROM employees WHERE dept_id = 10) LOOP
    DBMS_OUTPUT.PUT_LINE(rec.ename || ' earns ' || rec.salary);
  END LOOP;
END;
```

Functions:
```sql
CREATE OR REPLACE FUNCTION get_emp_salary(p_emp_id NUMBER) RETURN NUMBER DETERMINISTIC IS
  v_salary employees.salary%TYPE;
BEGIN
  SELECT salary INTO v_salary FROM employees WHERE emp_id = p_emp_id;
  RETURN v_salary;
END get_emp_salary;
```

## Procedures with IN/OUT/IN OUT
```sql
CREATE OR REPLACE PROCEDURE update_salary(
  p_emp_id   IN  employees.emp_id%TYPE,
  p_increase IN  NUMBER,
  p_new_sal  OUT employees.salary%TYPE
) AS
BEGIN
  UPDATE employees SET salary = salary + p_increase
  WHERE emp_id = p_emp_id
  RETURNING salary INTO p_new_sal;
END update_salary;
```

## Packages
```sql
CREATE OR REPLACE PACKAGE emp_pkg AS
  c_max_salary CONSTANT NUMBER := 50000;
  FUNCTION get_salary(p_emp_id NUMBER) RETURN NUMBER DETERMINISTIC;
  PROCEDURE raise_salary(p_emp_id NUMBER, p_amount NUMBER);
  PROCEDURE print_salary(p_emp_id NUMBER);
END emp_pkg;
/
CREATE OR REPLACE PACKAGE BODY emp_pkg AS
  g_last_access TIMESTAMP;
  
  FUNCTION get_salary(p_emp_id NUMBER) RETURN NUMBER DETERMINISTIC IS
    v_sal NUMBER;
  BEGIN
    SELECT salary INTO v_sal FROM employees WHERE emp_id = p_emp_id;
    g_last_access := SYSTIMESTAMP;
    RETURN v_sal;
  END;
  
  PROCEDURE raise_salary(p_emp_id NUMBER, p_amount NUMBER) IS
  BEGIN
    UPDATE employees SET salary = salary + p_amount WHERE emp_id = p_emp_id;
  END;
  
  PROCEDURE print_salary(p_emp_id NUMBER) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE(get_salary(p_emp_id));
  END;
BEGIN
  g_last_access := SYSTIMESTAMP;
END emp_pkg;
/
```

## Triggers
```sql
CREATE OR REPLACE TRIGGER validate_salary_trg
  BEFORE INSERT OR UPDATE OF salary ON employees
  FOR EACH ROW
BEGIN
  IF :NEW.salary < 0 THEN
    RAISE_APPLICATION_ERROR(-20001, 'Salary cannot be negative');
  END IF;
END;
/
```

## Dynamic SQL
```sql
CREATE OR REPLACE FUNCTION count_rows(p_table_name VARCHAR2) RETURN NUMBER IS
  v_cnt NUMBER;
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM ' || DBMS_ASSERT.SQL_OBJECT_NAME(p_table_name) INTO v_cnt;
  RETURN v_cnt;
END;
```

## Bulk Collect
```sql
DECLARE
  TYPE t_emp_tab IS TABLE OF employees%ROWTYPE;
  l_emps t_emp_tab;
BEGIN
  SELECT * BULK COLLECT INTO l_emps FROM employees WHERE dept_id = 50;
  FOR i IN 1..l_emps.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE(l_emps(i).ename);
  END LOOP;
END;
```

## FORALL
```sql
DECLARE
  TYPE t_num IS TABLE OF NUMBER;
  l_ids t_num := t_num(101, 102, 103);
  l_inc t_num := t_num(500, 1000, 750);
BEGIN
  FORALL i IN 1..l_ids.COUNT SAVE EXCEPTIONS
    UPDATE employees SET salary = salary + l_inc(i) WHERE emp_id = l_ids(i);
  -- Check exceptions
  FOR i IN 1..SQL%BULK_EXCEPTIONS.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE('Error on row ' || SQL%BULK_EXCEPTIONS(i).ERROR_INDEX);
  END LOOP;
END;
```