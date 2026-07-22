# Production Scenarios: EBS Financials

## Scenario 1: GL Period Close Fails Due to Unposted SLA Entries
**Context**: A multinational company was closing the monthly GL period for Q4 financial reporting.
**Problem**: The period close process failed with "There are unposted subledger entries for this period". 15,000 SLA entries from AP and AR were stuck in "Unposted" status. The close deadline was missed.
**Root Cause**: A concurrent program `SLA: Unpost Entries` had run incorrectly, unposting entries that should have been posted. The workflow for SLA posting had a bug triggered by a recent patch. The entries were in `XLA_AE_HEADERS` with `ACCOUNTING_ENTRY_STATUS_CODE = 'U'`.
**Solution**: 1) Identified stuck entries: `SELECT * FROM XLA_AE_HEADERS WHERE ACCOUNTING_ENTRY_STATUS_CODE = 'U' AND GL_DATE BETWEEN :start_date AND :end_date`. 2) Re-ran the SLA posting program: `SLA: Post Entries` concurrent program. 3) For entries that failed posting, used `XLA_UTILITIES_PKG.POST_ENTRIES` in PL/SQL. 4) Verified posting status and re-ran period close. 5) Rolled back the problematic patch.
**Lessons Learned**: Monitor SLA posting status before running period close. Implement pre-close validation checks. Test patches that affect SLA posting in lower environments first. Maintain a monitoring view for unposted SLA entries.

## Scenario 2: AP Invoice Validation Taking Hours
**Context**: A retail company processes 50,000 AP invoices monthly through EBS Payables.
**Problem**: AP Invoice Validation concurrent program ran for 6 hours during month-end, delaying the payment cycle. The program timed out and had to be restarted.
**Root Cause**: The invoice validation process was performing full table scans on `AP_INVOICES_ALL` and `AP_INVOICE_LINES_ALL`. The `AP_TAX_VALUES` table join was missing an index. The validation was running sequentially without parallel processing.
**Solution**: 1) Identified top SQL using `V$SQL` — the tax validation query was the bottleneck. 2) Added index on `AP_INVOICE_LINES_ALL(INVOICE_ID, LINE_TYPE_LOOKUP_CODE)`. 3) Changed validation to run with `INVOICE_DATE` range to process in batches. 4) Increased `DOP` (Degree of Parallelism) for the validation process. 5) Scheduled validation to run incrementally throughout the month instead of all at month-end.
**Lessons Learned**: Tune AP Invoice Validation queries for large volumes. Use batch processing for large invoice loads. Maintain proper indexes on AP tables. Consider incremental validation to distribute processing load.

## Scenario 3: Corrupted GL Balances After Consolidation
**Context**: A corporate GL consolidation from 10 subsidiary ledgers to the corporate ledger failed midway.
**Problem**: GL balances for two subsidiaries were partially consolidated. Some accounts showed duplicate balances, others showed zero. The quarter-end financial statements could not be generated.
**Root Cause**: The consolidation process was killed mid-execution due to a database timeout. The `GL_BALANCES` table had partial data from the consolidation run. The transfer process did not use a transaction boundary. There was no rollback mechanism.
**Solution**: 1) Restored GL_BALANCES for the affected subsidiaries from the pre-consolidation backup. 2) Used `GL_PERIOD_STATUSES` to identify the partially consolidated period. 3) Re-ran the consolidation process with increased timeout. 4) Implemented consolidation checkpoint logic: run one subsidiary at a time with commit after each. 5) Added pre-and-post consolidation validation queries to verify balance equality.
**Lessons Learned**: Run consolidations with checkpoints — one subsidiary per transaction. Monitor consolidation progress with balance comparison queries. Implement automated validation after consolidation. Always have rollback procedures.

## Scenario 4: Payment File Intercepted During PPR Processing
**Context**: A company uses EBS Payables to generate ACH payment files for vendor payments.
**Problem**: An internal audit found that the PPR payment file (containing bank account numbers and payment amounts) was transmitted over unencrypted FTP to the bank. The file was accessible to unauthorized personnel on the file server.
**Root Cause**: The Payment Process Request output was configured to write to a shared file system directory without access controls. The FTP transmission to the bank did not use SFTP or encryption. The file permissions were set to 777.
**Solution**: 1) Immediately restricted file permissions to 600 (owner-only access). 2) Changed FTP to SFTP with SSH key authentication. 3) Implemented PGP encryption for all payment files. 4) Moved payment file output to a secure, audited directory. 5) Configured automatic cleanup of payment files after 24 hours. 6) Implemented log monitoring for payment file access.
**Lessons Learned**: Payment files must be encrypted at rest and in transit. Implement strict file permissions and access auditing. Use SFTP/FTPS instead of plain FTP. Automate secure file cleanup. Conduct regular security reviews of payment processing.
