# Performance

## Scheduler Tuning
```bash
aiflow config set scheduler parsing_processes 4
aiflow config set scheduler min_file_process_interval 30
```

## Database Cleanup
```sql
DELETE FROM task_instance WHERE start_date < NOW() - INTERVAL '30 days';
DELETE FROM log WHERE dttm < NOW() - INTERVAL '30 days';
```

## Deferrable Operators
Use mode='reschedule' for long-running sensors.
