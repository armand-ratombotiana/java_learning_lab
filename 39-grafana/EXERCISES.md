# Exercises - Grafana

## Exercise 1: Basic Dashboard
Create a monitoring dashboard:

1. Add Prometheus data source
2. Create a graph panel for request rate
3. Add a stat panel for total requests
4. Create a gauge for error rate percentage

## Exercise 2: Variables and Templating
Build dynamic dashboards:

1. Create a variable for service selection
2. Use variable in PromQL queries
3. Add dropdown for time range selection
4. Implement drill-down links between panels

## Exercise 3: Advanced Panels
Create rich visualizations:

1. Build a heatmap for latency distribution
2. Create a pie chart for request breakdown
3. Implement table with multiple metrics
4. Add alert status panel

## Exercise 4: Alerting
Set up monitoring alerts:

1. Configure notification channel (email, Slack)
2. Create alert rule in dashboard
3. Set threshold conditions
4. Test alert firing and notification delivery

## Exercise 5: Provisioning
Automate dashboard deployment:

1. Create dashboard JSON definition
2. Configure dashboard provisioning in Grafana
3. Set up automated data source provisioning
4. Version control and CI/CD integration

## Bonus Challenge
Build a complete SRE dashboard with: SLA tracking (uptime %), error budget burn rate, latency heatmap, and on-call rotation panel.