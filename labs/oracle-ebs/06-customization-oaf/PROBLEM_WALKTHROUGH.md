# Problem Walkthrough: Customization (OAF)

## Problem 1: Custom Invoice Approval Page — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte implementing EBS for a healthcare client. The client's AP department needs a custom invoice approval page that shows invoice images alongside EBS invoice data. The standard Oracle AP approval page does not support image attachments. The client wants a single page where approvers can view the scanned invoice and approve/reject without switching applications."

### The Problem
Build an OAF page in JDeveloper that extends the standard APXINWKB (Invoice Workbench) functionality. The page must: (1) Display scanned invoice image from WebADI/attachments, (2) Show AP invoice header and line details read-only, (3) Provide Approve/Reject buttons with reason code dropdown, (4) Log all approval actions to a custom table, (5) Integrate with Oracle Approval Workflow. The client uses EBS 12.2.10 with OA Framework 12.2.10.

### Solution Walkthrough
- Step 1: Create custom OA page in JDeveloper 12.2.10 using BC4J components
- Step 2: Extend APXINWKB entity object to include custom attachment fields
- Step 3: Build a new region with image renderer for scanned invoice display
- Step 4: Create custom VO joining AP_INVOICES_ALL and FND_ATTACHMENTS
- Step 5: Implement controller class with approve/reject action handlers
- Step 6: Call WF_ENGINE to trigger approval workflow completion
- Step 7: Register custom page as function in Oracle Application Object Library
- Step 8: Grant access via custom responsibility or function security

### Code
```java
// Controller for custom invoice approval page
package oracle.apps.ap.oaf.custom.webui;

import oracle.apps.fnd.framework.webui.OAWebBeanConstants;
import oracle.apps.fnd.framework.webui.OAPageContext;
import oracle.apps.fnd.framework.webui.beans.OAWebBean;
import oracle.apps.fnd.framework.webui.beans.form.OAFormValueBean;
import oracle.jbo.domain.Number;

public class CustInvoiceApprovalCO extends OAPageController {
  
  public static final String REQ_CODE = "CustInvoiceApprovalCO";
  
  @Override
  protected void processRequest(OAPageContext pageContext, OAWebBean webBean) {
    super.processRequest(pageContext, webBean);
    
    String invoiceId = pageContext.getParameter("invoiceId");
    if (invoiceId != null) {
      // Load invoice data
      OAFormValueBean invoiceNum = 
        (OAFormValueBean) webBean.findChildRecursive("InvoiceNumber");
      invoiceNum.setValue(pageContext, getInvoiceNumber(invoiceId));
      
      // Load attachment image
      loadInvoiceImage(pageContext, webBean, invoiceId);
    }
  }
  
  private String getInvoiceNumber(String invoiceId) {
    // Business logic to retrieve invoice number
    return "INV-" + invoiceId;
  }
  
  private void loadInvoiceImage(OAPageContext pageContext, 
                                  OAWebBean webBean, 
                                  String invoiceId) {
    // Query FND_ATTACHMENTS for invoice image
    String attachmentUrl = getAttachmentUrl(invoiceId);
    OAWebBean imageBean = webBean.findChildRecursive("InvoiceImage");
    if (imageBean != null) {
      imageBean.setAttributeValue("source", attachmentUrl);
    }
  }
  
  public void handleApprove(OAPageContext pageContext, OAWebBean webBean) {
    String invoiceId = (String) pageContext.getParameter("invoiceId");
    String reasonCode = 
      (String) pageContext.getParameter("approvalReasonCode");
    
    // Update invoice status
    updateInvoiceStatus(invoiceId, "APPROVED", reasonCode);
    
    // Complete workflow
    completeWorkflow(invoiceId, "APPROVE");
    
    // Log to custom table
    logApprovalAction(invoiceId, "APPROVED", reasonCode, 
                      pageContext.getUserName());
    
    // Show confirmation
    pageContext.writeDiagnostics(this, "Invoice " + invoiceId + " approved", 
                                 0);
  }
  
  private void completeWorkflow(String invoiceId, String action) {
    // Call WF_ENGINE to complete worklist item
    String itemKey = "INV-" + invoiceId;
    wf_engine.completeActivity(
      itemType => 'APINV',
      itemKey  => itemKey,
      activity => action
    );
  }
}
```

### Company Evaluation
- Oracle: OA Framework architecture, BC4J patterns, JDeveloper configuration, OA Extension methodology.
- Deloitte: OAF extension methodology, CEMLI standards, user acceptance testing for custom pages.
- Accenture: Global rollout of customizations, multi-language support, personalization vs extension decisions.
- PwC: Code review for customizations, security access controls, SOX compliance for custom code.
- Amazon: Migration of OAF pages to APEX or Oracle JET, cloud-ready UI modernization.

---

## Problem 2: Custom Concurrent Program Request Page — Company: Oracle
### EBS Interview Scenario
"You're at Oracle providing support to a logistics client. Their shipping team runs 15 different concurrent programs every morning in a specific sequence. They manually submit each one and often make mistakes in parameter values, causing shipping delays. They need a single page to orchestrate all 15 programs."

### The Problem
Build an OAF page that serves as a batch job submission dashboard. The page must: (1) Display a parameter checklist for each of the 15 programs, (2) Validate parameter values before submission, (3) Submit programs in dependency order, (4) Show real-time request status, (5) Handle failures gracefully with rollback of dependent programs. The page must integrate with FND_REQUEST_SUBMIT API.

### Solution Walkthrough
- Step 1: Create a new OA page with table of programs and parameters
- Step 2: Build VO based on custom metadata table (XX_SHIPPING_BATCH_JOBS)
- Step 3: Implement parameter validation using dynamic PL/SQL validation rules
- Step 4: Create controller logic to submit programs in dependency order
- Step 5: Use FND_REQUEST_SUBMIT to submit each concurrent program
- Step 6: Poll FND_CONCURRENT_REQUESTS for status updates via AJAX
- Step 7: Implement status refresh with partial page rendering (PPR)

### Code
```sql
-- Custom metadata table for batch job orchestration
CREATE TABLE xx_shipping_batch_jobs (
  job_id           NUMBER PRIMARY KEY,
  job_name         VARCHAR2(100),
  program_short_name VARCHAR2(30),
  application_id   NUMBER,
  dependency_order NUMBER,
  enabled_flag     VARCHAR2(1) DEFAULT 'Y',
  rollback_on_error VARCHAR2(1) DEFAULT 'N',
  parameter_list   VARCHAR2(4000), -- JSON parameter defaults
  created_by       NUMBER,
  creation_date    DATE
);

-- Submit batch of programs
CREATE OR REPLACE PROCEDURE submit_shipping_batch (
  p_batch_name  IN VARCHAR2,
  p_request_ids OUT SYS_REFCURSOR
) IS
  l_request_id NUMBER;
  l_previous_req_id NUMBER;
BEGIN
  FOR job IN (
    SELECT * FROM xx_shipping_batch_jobs
    WHERE enabled_flag = 'Y'
    ORDER BY dependency_order
  ) LOOP
    l_request_id := fnd_request.submit_request(
      application => job.application_id,
      program     => job.program_short_name,
      description => 'Batch: ' || p_batch_name,
      start_time  => SYSDATE
    );
    
    -- Set dependency on previous job
    IF l_previous_req_id IS NOT NULL THEN
      fnd_request_relations.add_dependency(
        p_request_id      => l_request_id,
        p_depends_on      => l_previous_req_id,
        p_dependency_type => 'NORMAL'
      );
    END IF;
    
    l_previous_req_id := l_request_id;
  END LOOP;
  
  COMMIT;
  
  OPEN p_request_ids FOR
    SELECT request_id, request_date
    FROM   fnd_concurrent_requests
    WHERE  description = 'Batch: ' || p_batch_name;
END;
/
```

### Company Evaluation
- Oracle: FND_REQUEST_SUBMIT API, concurrent program dependencies, request chaining, OAF page flow.
- Deloitte: Process automation methodology, batch job design, error handling and recovery procedures.
- Accenture: Operational efficiency patterns, shipping logistics optimization, automation of manual processes.
- PwC: Transaction integrity controls, batch processing audit trails, segregation of batch processing duties.
- Amazon: Replacement of concurrent programs with AWS Batch, Step Functions for orchestration, SQS for queuing.

---

## Problem 3: OA Personalization — Company: Accenture
### EBS Interview Scenario
"You're at Accenture rolling out EBS to a retail chain. The purchasing team finds the standard PO Summary page too cluttered — they only need 5 fields out of 30 displayed. Additionally, they want color-coded rows (red for urgent, yellow for pending, green for approved). They need this deployed to 200 users without modifying the base Oracle code."

### The Problem
Use OA Personalization (not extension) to customize the PO Summary page (POXPOEPO). The team needs: (1) Hide 25 of 30 columns, (2) Reorder remaining 5 columns, (3) Add color-coding based on authorization_status, (4) Add a custom "Urgency" indicator based on need-by date and amount, (5) Deploy via personalization layer without touching JAR files.

### Solution Walkthrough
- Step 1: Access OA Personalization via "Personalize Page" link in EBS
- Step 2: Hide unwanted columns using "Rendered" property set to "false"
- Step 3: Reorder columns using "Order" property
- Step 4: Add client-side JavaScript for row color-coding via HTML injection
- Step 5: Create a custom CSS style sheet and upload as attachment
- Step 6: Use Profile Option "FND_CSS_CUSTOM" to inject custom styles
- Step 7: Export personalization as .mds file for deployment to all users
- Step 8: Test across browsers (IE, Chrome, Firefox) for compatibility

### Code
```xml
<!-- MDS Personalization snippet (exported from OA Personalization) -->
<?xml version="1.0" encoding="UTF-8"?>
<mds:persist xmlns:mds="http://xmlns.oracle.com/mds">
  <mds:doc path="/oracle/apps/po/supplychain/webui/POXPOEPOVO">
    <property name="Order" value="5">
      <display>promised_date</display>
    </property>
    <property name="Rendered" value="false">
      <display>header_id</display>
    </property>
    <property name="Rendered" value="false">
      <display>vendor_site_id</display>
    </property>
    <property name="Rendered" value="false">
      <display>ship_to_location_id</display>
    </property>
  </mds:doc>
</mds:persist>

<!-- Custom CSS for row color-coding -->
/* Inject via Profile FND_CSS_CUSTOM */
tr.po-row-status-APPROVED {
  background-color: #d4edda !important;
}
tr.po-row-status-IN_PROCESS {
  background-color: #fff3cd !important;
}
tr.po-row-status-REJECTED {
  background-color: #f8d7da !important;
}
/* Urgency indicator */
.po-urgent-flag {
  color: red;
  font-weight: bold;
  font-size: 16px;
}
```

### Company Evaluation
- Accenture: OA Personalization framework, MDS metadata management, UI/UX customization at scale.
- Oracle: OA Personalization architecture, MDS repository, Profile Option FND_CSS_CUSTOM, personalization layers.
- Deloitte: User experience design, business process optimization, training for personalized interfaces.
- PwC: Security review of personalizations, access control for personalization features, UI compliance.
- Amazon: Modernization of EBS UI with Oracle JET or React, headless EBS architecture with REST APIs.
