# Test Plan — Lab 26

## Unit Tests

| Test Class | Test Method | What It Tests |
|-----------|------------|---------------|
| AutoConfigurationExplorerTest | contextLoads | Application context starts |
| AutoConfigurationExplorerTest | autoConfigurationExplorerBeanExists | Explorer bean registered |
| AutoConfigurationExplorerTest | beanDefinitionsAreRegistered | Bean definitions exist |
| AutoConfigurationExplorerTest | exploreConditionsRunsWithoutError | Condition evaluation works |
| ActuatorInternalsTest | healthIndicatorUpByDefault | Default UP status |
| ActuatorInternalsTest | healthIndicatorDownWhenSet | DOWN status when set |
| ActuatorInternalsTest | customMetricsRecordsRequests | Counter increment |
| ActuatorInternalsTest | customMetricsTracksSessions | Gauge tracking |
| ActuatorInternalsTest | customEndpointReadOperation | Read operation returns data |
| ActuatorInternalsTest | customEndpointWriteOperation | Write operation updates state |
| ActuatorInternalsTest | starterServiceGreets | Starter service works |
| ActuatorInternalsTest | starterHealthIndicatorIsHealthy | Starter health check |

## Integration Tests

- Full context load with `@SpringBootTest`
- Conditional evaluation with property overrides
- Endpoint exposure via `@WebMvcTest`
- Embedded container port detection

## Expected Coverage

- AutoConfigurationExplorer: 85%+
- DispatcherChainAnalyzer: 70%+ (requires web context)
- StarterCustomizer: 80%+
- ActuatorInternals: 90%+