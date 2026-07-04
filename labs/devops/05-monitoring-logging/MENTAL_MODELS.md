# Mental Models for Monitoring & Logging

## 1. Car Dashboard Analogy
- **Speedometer** = Request latency metric
- **Check Engine Light** = Error rate alert
- **Fuel Gauge** = Disk space metric
- **Odometer** = Request counter

You don't drive without a dashboard. Don't run production without monitoring.

## 2. Signal vs Noise
A good monitoring system surfaces signals (real problems) and filters noise (benign anomalies). Alert fatigue happens when noise drowns out signals.

## 3. USE Method (Brendan Gregg)
For every resource, check:
- **Utilization**: % of time resource is busy
- **Saturation**: amount of queued work
- **Errors**: error count

## 4. RED Method (Tom Wilkie)
For every service, track:
- **Rate**: requests per second
- **Errors**: failed requests per second
- **Duration**: latency distribution

## 5. Pet vs Cattle for Logs
logs are cattle — keep them for a finite period (30 days hot, 1 year warm, delete after). Don't treat every log line as a pet.
