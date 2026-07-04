# Common Orchestration Mistakes

1. **Not Using Idempotent Tasks**: Append without checking for duplicates
2. **Hardcoding Dependencies**: Rigid task ordering
3. **No Task Timeouts**: Tasks can run forever
4. **Too Many Tasks in One DAG**: 200+ tasks unwieldy to monitor
5. **Ignoring Catchup**: Missing backfill logic
