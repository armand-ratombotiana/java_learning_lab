# Problem Walkthrough: Integrations

## Problem 1: Web Service Integration with Salesforce — Company: Accenture
### EBS Interview Scenario
"You're at Accenture integrating EBS with Salesforce for a manufacturing client. Sales reps create opportunities in Salesforce that need to become quotes in EBS. After quoting, the order flows back to Salesforce. The current manual double-entry process causes a 2-day delay in quote generation, and 15% of orders have data entry errors. The client wants real-time sync."

### The Problem
Design and implement a bi-directional SOAP web service integration between EBS 12.2 and Salesforce Sales Cloud. The integration must: (1) Expose EBS Order Management as a SOAP web service (Oracle Workflow Business Event System), (2) Consume Salesforce Opportunity data via Salesforce REST API, (3) Transform Salesforce Account/Contact to EBS Customer, (4) Create EBS Quote via OE_QUOTE_PUB, (5) Convert Quote to Order via OE_ORDER_PUB, (6) Push Order status back to Salesforce via Outbound Message, (7) Handle errors with retry logic and dead letter queue.

### Solution Walkthrough
- Step 1: Design the integration architecture — SOAP/XML with Oracle SOA Suite or custom PL/SQL gateway
- Step 2: Create EBS business events for Quote Created, Order Booked, Order Shipped
- Step 3: Build PL/SQL web service using UTL_HTTP or Oracle Workflow Agent Listener
- Step 4: Create Salesforce Connected App for OAuth2 authentication
- Step 5: Implement data transformation (Salesforce Account → HZ_CUST_ACCOUNTS)
- Step 6: Build error handling with FND_LOG and custom error table
- Step 7: Create monitoring dashboard showing integration statistics

### Code
```sql
-- Create EBS business event
BEGIN
  wf_event_services.create_event(
    p_guid       => 'oracle.apps.oe.order.booked',
    p_name       => 'oracle.apps.oe.order.booked',
    p_display_name => 'Order Booked Event',
    p_description => 'Fired when an order is booked in Order Management'
  );
  
  wf_event_services.add_parameter(
    p_event_guid => 'oracle.apps.oe.order.booked',
    p_name       => 'ORDER_HEADER_ID',
    p_type       => 'NUMBER',
    p_description => 'Header ID of the booked order'
  );
  COMMIT;
END;
/

-- PL/SQL web service to push order status to Salesforce
CREATE OR REPLACE PROCEDURE push_order_to_salesforce (
  p_order_header_id IN NUMBER,
  p_sf_opportunity_id IN VARCHAR2,
  p_status OUT VARCHAR2
) IS
  l_http_request  UTL_HTTP.req;
  l_http_response UTL_HTTP.resp;
  l_url           VARCHAR2(500) := 'https://your-instance.salesforce.com/services/data/v58.0/sobjects/Opportunity/' || p_sf_opportunity_id;
  l_access_token  VARCHAR2(500);
  l_request_body  VARCHAR2(4000);
  l_response_text VARCHAR2(4000);
BEGIN
  -- Get OAuth token
  l_access_token := get_sf_access_token();
  
  -- Build JSON payload
  l_request_body := '{"StageName": "Closed Won", "EBS_Order_Number__c": "' || p_order_header_id || '"}';
  
  -- Make HTTP PATCH request
  l_http_request := UTL_HTTP.begin_request(l_url, 'PATCH', 'HTTP/1.1');
  UTL_HTTP.set_header(l_http_request, 'Authorization', 'Bearer ' || l_access_token);
  UTL_HTTP.set_header(l_http_request, 'Content-Type', 'application/json');
  UTL_HTTP.write_text(l_http_request, l_request_body);
  
  l_http_response := UTL_HTTP.get_response(l_http_request);
  UTL_HTTP.read_text(l_http_response, l_response_text);
  UTL_HTTP.end_response(l_http_response);
  
  -- Log success
  INSERT INTO xx_integration_log
    (integration_name, direction, payload, status, created_date)
  VALUES
    ('SF_ORDER_PUSH', 'OUTBOUND', l_request_body, 'SUCCESS', SYSDATE);
  
  p_status := 'SUCCESS';
EXCEPTION
  WHEN OTHERS THEN
    p_status := 'ERROR: ' || SQLERRM;
    INSERT INTO xx_integration_log
      (integration_name, direction, payload, status, error_msg, created_date)
    VALUES
      ('SF_ORDER_PUSH', 'OUTBOUND', l_request_body, 'FAILED', SQLERRM, SYSDATE);
END;
/
```

### Company Evaluation
- Accenture: Integration architecture, SOA/ESB patterns, CRM-to-ERP integration, Salesforce expertise.
- Oracle: Business Event System, Workflow Agent Listener, UTL_HTTP, SOA Suite, OE_ORDER_PUB APIs.
- Deloitte: Integration testing methodology, data mapping workshops, cutover planning for go-live.
- PwC: Data integrity controls, transaction reconciliation, SOX compliance for automated integrations.
- Amazon: AWS AppFlow for Salesforce integration, API Gateway + Lambda for serverless EBS connectors.

---

## Problem 2: Open Interface Tables — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte implementing EBS for a hospital. They have a legacy lab system that produces HL7 files with patient billing data. These files need to be imported into Oracle AR and GL every night. The legacy system cannot be modified — it outputs fixed-format text files. You need to build an interface using EBS Open Interface tables."

### The Problem
The HL7-to-EBS interface must: (1) Parse fixed-format text files from the lab system, (2) Stage data in AR_PAYMENTS_INTERFACE_ALL for receipts, (3) Stage data in GL_INTERFACE for journal entries, (4) Run interface programs in correct sequence, (5) Handle duplicate detection (same lab file processed twice should not create duplicates), (6) Generate an interface summary report showing records loaded, errors, and exceptions.

### Solution Walkthrough
- Step 1: Design staging tables matching the source file format
- Step 2: Create a SQL*Loader control file for parsing fixed-format data
- Step 3: Write a PL/SQL validation program to check data before loading to interface tables
- Step 4: Insert validated records into AR_PAYMENTS_INTERFACE_ALL
- Step 5: Insert validated records into GL_INTERFACE
- Step 6: Run AutoLockbox (ARXALCK) to process AR interface records
- Step 7: Run Journal Import (GLLEZL) to process GL interface records
- Step 8: Check interface status tables for errors and reprocess as needed

### Code
```sql
-- Load data to AR Payments Interface
CREATE OR REPLACE PROCEDURE load_ar_payments_interface (
  p_batch_name   IN VARCHAR2,
  p_file_content IN CLOB
) IS
  l_line VARCHAR2(4000);
  l_status VARCHAR2(10);
BEGIN
  FOR line_rec IN (
    SELECT REGEXP_SUBSTR(p_file_content, '[^' || CHR(10) || ']+', 1, LEVEL) AS line_text
    FROM DUAL
    CONNECT BY LEVEL <= REGEXP_COUNT(p_file_content, CHR(10)) + 1
  ) LOOP
    -- Parse fixed-format: positions 1-10 = patient_id, 11-25 = amount, 26-45 = date
    INSERT INTO ar_payments_interface_all (
      interface_line_id,
      batch_name,
      customer_bank_account_id,
      amount,
      payment_date,
      attribute1,  -- patient_id
      status,
      created_by,
      creation_date
    ) VALUES (
      ar_payments_interface_s.NEXTVAL,
      p_batch_name,
      TO_NUMBER(SUBSTR(line_rec.line_text, 1, 10)),
      TO_NUMBER(SUBSTR(line_rec.line_text, 11, 15)),
      TO_DATE(SUBSTR(line_rec.line_text, 26, 20), 'YYYY-MM-DD'),
      SUBSTR(line_rec.line_text, 1, 10),
      'NEW',
      -1,
      SYSDATE
    );
  END LOOP;
  COMMIT;
END;
/

-- Check interface errors after processing
SELECT interface_line_id,
       message_text,
       message_type,
       create_date
FROM   ar_interface_errors_all
WHERE  batch_name = 'LAB_SYSTEM_20240727'
ORDER  BY interface_line_id;
```

### Company Evaluation
- Oracle: Open Interface architecture — AR_PAYMENTS_INTERFACE, GL_INTERFACE, RA_INTERFACE_LINES, interface program execution flow.
- Deloitte: Interface design methodology, source-to-target mapping, error handling and reconciliation strategy.
- Accenture: Healthcare HL7 standards, HIPAA compliance, legacy system integration patterns.
- PwC: Interface data integrity testing, reconciliation controls, audit trail for data movement.
- Amazon: Cloud ETL with AWS Glue, S3 for file staging, Lambda for transformation, RDS for EBS integration.

---

## Problem 3: Oracle Workflow Integration — Company: Oracle
### EBS Interview Scenario
"You're at Oracle consulting for a telecom client. They have a custom provisioning system that needs to receive order notifications from EBS Order Management in real-time. When an order for a new cell phone plan is booked in EBS, the provisioning system must activate the SIM card within 5 minutes. The current solution polls the database every 15 minutes — too slow."

### The Problem
Implement a real-time integration using Oracle Workflow Business Event System. When an order is booked (OE_BOOKING event fires), a subscription must: (1) Capture the order details via WF_EVENT_T, (2) Transform order data to XML, (3) Publish to an Oracle Advanced Queue (AQ), (4) The provisioning system consumes from the AQ, (5) Handle acknowledgments with retry (max 3 attempts, 60-second intervals), (6) Dead letter queue for permanent failures, (7) Alert operations team on failure.

### Solution Walkthrough
- Step 1: Analyze existing OE_BOOKING business event structure
- Step 2: Create subscription for OE_BOOKING using WF_EVENT_SUBSCRIPTIONS
- Step 3: Build PL/SQL rule function to filter order types (only cell phone plans)
- Step 4: Create AQ queue and queue table for message queuing
- Step 5: Implement subscription callback procedure that enqueues to AQ
- Step 6: Build dequeue procedure for provisioning system to consume
- Step 7: Implement retry logic in callback with delay queue
- Step 8: Monitor with ADM (Application Diagnostics Manager)

### Code
```sql
-- Create Advanced Queue for provisioning messages
BEGIN
  DBMS_AQADM.create_queue_table(
    queue_table => 'xx_provisioning_qt',
    queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE',
    sort_list => 'ENQ_TIME',
    multiple_consumers => FALSE
  );
  
  DBMS_AQADM.create_queue(
    queue_name => 'xx_provisioning_order_q',
    queue_table => 'xx_provisioning_qt'
  );
  
  DBMS_AQADM.start_queue(queue_name => 'xx_provisioning_order_q');
  COMMIT;
END;
/

-- Event subscription callback procedure
CREATE OR REPLACE PROCEDURE handle_order_booked (
  p_subscription_guid IN RAW,
  p_event IN OUT NOCOPY WF_EVENT_T
) IS
  l_enqueue_options    DBMS_AQ.enqueue_options_t;
  l_message_properties DBMS_AQ.message_properties_t;
  l_msg_id             RAW(16);
  l_payload            SYS.AQ$_JMS_TEXT_MESSAGE;
  l_order_header_id    NUMBER;
  l_order_type         VARCHAR2(30);
  l_xml_payload        CLOB;
BEGIN
  -- Parse event data
  l_order_header_id := TO_NUMBER(p_event.GetValueForParameter('ORDER_HEADER_ID'));
  
  -- Get order type
  SELECT order_type_id INTO l_order_type
  FROM oe_order_headers_all
  WHERE header_id = l_order_header_id;
  
  -- Filter: only cell phone plan orders
  IF l_order_type = 'CELL_PLAN' THEN
    -- Build XML payload
    l_xml_payload := '<OrderBooked>' ||
                     '<OrderHeaderId>' || l_order_header_id || '</OrderHeaderId>' ||
                     '<OrderNumber>' || get_order_number(l_order_header_id) || '</OrderNumber>' ||
                     '<OrderDate>' || TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') || '</OrderDate>' ||
                     '</OrderBooked>';
    
    -- Enqueue to AQ
    l_payload := SYS.AQ$_JMS_TEXT_MESSAGE.construct();
    l_payload.set_text(l_xml_payload);
    
    DBMS_AQ.enqueue(
      queue_name         => 'xx_provisioning_order_q',
      enqueue_options    => l_enqueue_options,
      message_properties => l_message_properties,
      payload            => l_payload,
      msgid              => l_msg_id
    );
  END IF;
  
  -- Log success
  INSERT INTO xx_integration_log
    (integration_name, source, target, payload, status, created_date)
  VALUES
    ('OE_PROVISIONING', 'ORDER_MGMT', 'PROV_SYSTEM',
     l_xml_payload, 'ENQUEUED', SYSDATE);
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: Business Event System, Advanced Queuing, Workflow Engine, event subscriptions, delivery patterns.
- Deloitte: Real-time integration methodology, latency requirements, SLAs for message processing.
- Accenture: Enterprise messaging patterns, publish/subscribe architecture, JMS integration with third-party systems.
- PwC: Message integrity, guaranteed delivery, transaction reconciliation, audit logging for automated processes.
- Amazon: Amazon MQ or SQS/SNS for message queuing, EventBridge for event-driven integration, Lambda consumers.
