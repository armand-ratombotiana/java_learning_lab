# 45 - Gatling Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Simulation | Load test configuration |
| Scenario | User behavior workflow |
| Feeder | Data injection |
| Check | Response validation |
| Chain | Sequential actions |

## Commands

```bash
# Run Gatling tests
cd gatling-load
mvn gatling:test

# Run specific simulation
mvn gatling:test -Dgatling.simulationClass=MySimulation

# Generate report
mvn gatling:report

# Report location
# target/gatling/<simulation-name>-<timestamp>/index.html
```

## Scenario Definition

```scala
class MySimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")

  val scn = scenario("User Journey")
    .exec(http("Get Users")
      .get("/api/users")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Create User")
      .post("/api/users")
      .body(StringBody("""{"name":"test"}"""))
      .check(status.is(201)))

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(10),
      rampUsers(50).during(30)
    )
  ).protocols(httpProtocol)
}
```

## Feeders

```scala
// CSV feeder
val csvFeeder = csv("data/users.csv").random

// JSON feeder
val jsonFeeder = jsonFile("data/users.json").random

// Iteration with feeder
scenario("With Data")
  .feed(csvFeeder)
  .exec(http("Request")
    .post("/api/user/${userId}")
    .body(StringBody("${userJson}"))
```

## Checks

```scala
.check(status.is(200))
.check(jsonPath("$.id").saveAs("userId"))
.check(jsonPath("$.name").is("John"))
.check(responseTimeInMillis.lte(500))
```

## Results

| Metric | Location |
|--------|----------|
| HTML Report | `target/gatling/` |
| Logs | `target/gatling/logs/` |
| Raw Data | `target/gatling/simulation-data/` |