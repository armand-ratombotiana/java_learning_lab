# Debugging Apache Beam

## DirectRunner Execution
Use DirectRunner for local debugging: mvn compile exec:java -Dexec.args='--runner=DirectRunner'

## Metrics
Use Metrics API: Counters, Distributions, Gauages for pipeline monitoring

## Logging
Use @ProcessElement with LOG.debug() for element-level tracing; configure log level via --defaultSdkHarnessLogLevel

## Pipeline Graph
View pipeline graph via runner UI; Dataflow: Pipeline Graph in Console; Flink: execution plan
