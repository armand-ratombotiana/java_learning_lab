# Mock Interview: APEX RESTful APIs (Lab 06)

**Role:** Oracle APEX Developer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is ORDS and how does it relate to APEX?

**Candidate:** ORDS (Oracle REST Data Services) is a Java EE-based application that enables RESTful access to Oracle Database. It serves as the HTTP listener between clients and the database. ORDS provides:
- REST APIs for database objects (tables, views, procedures)
- WebSocket support for real-time data push
- Auto-REST enablement for tables/views
- Swagger/OpenAPI documentation generation
- Authentication and authorization for REST endpoints
- Transaction management via ETags

APEX applications are also served through ORDS. When you install APEX, ORDS is used as the web listener. Every APEX request goes through ORDS → APEX Engine → Oracle Database.

**Interviewer:** How do you create a RESTful service in APEX?

**Candidate:** RESTful services in APEX can be created via:
1. **APEX App Builder → Shared Components → RESTful Services:** Create modules, templates, and handlers
2. **SQL Workshop → RESTful Services:** Create services from SQL Workshop

Steps to create a simple REST API:
```sql
-- Enable REST for the ORDERS table
BEGIN
    ORDS.ENABLE_OBJECT(
        p_enabled => TRUE,
        p_schema => 'APEX_SCHEMA',
        p_object => 'ORDERS',
        p_object_type => 'TABLE',
        p_object_alias => 'orders'
    );
    COMMIT;
END;
```

Or create a custom REST handler:
```sql
BEGIN
    ORDS.DEFINE_TEMPLATE(
        p_module_name => 'order-api',
        p_pattern => 'orders/:id'
    );
    
    ORDS.DEFINE_HANDLER(
        p_module_name => 'order-api',
        p_pattern => 'orders/:id',
        p_method => 'GET',
        p_source_type => ORDS.SOURCE_TYPE_QUERY,
        p_source => 'SELECT * FROM orders WHERE order_id = :id'
    );
END;
```

The endpoint becomes: `GET https://host:port/ords/{schema}/order-api/orders/{id}`

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you consume an external REST API from an APEX application?

**Candidate:** APEX provides **Web Source Modules** for consuming REST APIs:
1. Navigate to **Shared Components → Web Source Modules**
2. Create a Web Source Module:
   - URL: `https://api.example.com/v2/`
   - Authentication: OAuth 2.0, Basic Auth, or API Key
   - Method: GET, POST, PUT, DELETE
   - Subscribe to operations: Create, Read, Update, Delete
   - Map JSON response fields to APEX items

3. Use the Web Source as a data source for regions (reports, interactive grids):
   - Create a region > Source > Type = "Web Source"
   - Select your Web Source Module and operation
   - Map parameters to page items

For custom REST consumption in PL/SQL:
```sql
DECLARE
    v_response CLOB;
BEGIN
    v_response := APEX_WEB_SERVICE.MAKE_REST_REQUEST(
        p_url => 'https://api.example.com/customers',
        p_http_method => 'GET',
        p_parm_name => APEX_UTIL.TABLE_TO_STRING(APEX_T_VARCHAR2('api_key')),
        p_parm_value => APEX_UTIL.TABLE_TO_STRING(APEX_T_VARCHAR2('sk_test_123'))
    );
    
    -- Parse JSON response
    :P1_RESPONSE := v_response;
END;
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a REST API system using ORDS for a banking application. Include authentication, transaction handling, and error management.

**Candidate:** 

**Architecture:**
```
Client → HTTPS → OAuth 2.0 Gateway → ORDS → Oracle Database
                             │
                     Rate Limiting Layer
```

**REST API endpoints:**
```sql
-- Module: banking-api
-- Template: /balances/:account_id
-- GET: Account balance
BEGIN
    ORDS.DEFINE_HANDLER(
        p_module_name => 'banking-api',
        p_pattern => 'balances/:account_id',
        p_method => 'GET',
        p_source_type => ORDS.SOURCE_TYPE_QUERY,
        p_source => 'SELECT account_id, balance, currency, last_updated 
                      FROM accounts_v 
                      WHERE account_id = :account_id 
                      AND has_access(:app_user, account_id) = 1',
        p_items_per_page => 0,
        p_comments => 'Get account balance'
    );
END;

-- Template: /transfers
-- POST: Create transfer (money between accounts)
BEGIN
    ORDS.DEFINE_HANDLER(
        p_module_name => 'banking-api',
        p_pattern => 'transfers',
        p_method => 'POST',
        p_source_type => ORDS.SOURCE_TYPE_PLSQL,
        p_source => 'BEGIN
            transfer_funds(
                p_from_account => :from_account,
                p_to_account => :to_account,
                p_amount => :amount,
                p_description => :description,
                p_ref_num => :reference_number,
                p_initiated_by => :app_user
            );
            :status_code := 201;
        END;'
    );
END;
```

**Authentication:** OAuth 2.0 Client Credentials flow:
```sql
BEGIN
    ORDS.DEFINE_AUTH(
        p_name => 'oauth_banking',
        p_type => 'OAUTH2',
        p_token_url => 'https://auth.bank.com/oauth2/token',
        p_scope => 'accounts:read transfers:execute',
        p_require_https => TRUE
    );
    
    ORDS.DEFINE_PRIVILEGE(
        p_name => 'api_access',
        p_roles => 'authenticated_client',
        p_labels => 'API Client',
        p_description => 'Access to banking REST APIs'
    );
END;
```

**Error handling (consistent response format):**
```sql
CREATE OR REPLACE PACKAGE banking_api AS
    FUNCTION handle_exception RETURN CLOB;
END;

CREATE OR REPLACE PACKAGE BODY banking_api AS
    FUNCTION handle_exception RETURN CLOB AS
        v_code NUMBER := SQLCODE;
        v_msg VARCHAR2(4000) := SQLERRM;
    BEGIN
        APEX_JSON.OPEN_OBJECT;
        APEX_JSON.WRITE('status', 'error');
        APEX_JSON.WRITE('code', CASE 
            WHEN v_code = -20001 THEN 'INSUFFICIENT_FUNDS'
            WHEN v_code = -20002 THEN 'ACCOUNT_CLOSED'
            WHEN v_code = -20003 THEN 'DAILY_LIMIT_EXCEEDED'
            ELSE 'INTERNAL_ERROR'
        END);
        APEX_JSON.WRITE('message', v_msg);
        APEX_JSON.WRITE('timestamp', SYSTIMESTAMP);
        APEX_JSON.WRITE('request_id', SYS_GUID());
        APEX_JSON.CLOSE_OBJECT;
        RETURN APEX_JSON.GET_CLOB;
    END;
END;
```

**Security considerations:**
- All endpoints use HTTPS only
- Rate limiting per client via API Gateway
- Idempotency keys on transfer endpoints (prevent double-transfers)
- Audit logging for every API call
- Row-level security in all queries (where clause filters by authenticated user)
- Sensitive data never logged (account numbers masked in logs)

**Interviewer:** How do you handle pagination in ORDS REST APIs?

**Candidate:** ORDS supports automatic pagination:
- Works for GET handlers with `SOURCE_TYPE_QUERY`
- URL parameters: `?offset=100&limit=50`
- Response includes: `hasMore`, `limit`, `offset`, `count`, `items` array
- Default: `limit=25`, max configurable
- For custom pagination, use `ROW_NUMBER()` with offset/limit in the query:
```sql
SELECT * FROM (
    SELECT t.*, ROW_NUMBER() OVER (ORDER BY created_at DESC) rn
    FROM transactions t 
    WHERE account_id = :account_id
) WHERE rn BETWEEN :offset + 1 AND :offset + :limit
```

---

## Interviewer Feedback

**Strengths:** Good ORDS understanding, practical banking API design, strong error handling patterns  
**Areas to Improve:** Could discuss ORDS WebSocket support for real-time updates  
**Verdict:** Hire

---

*Lab 06 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
