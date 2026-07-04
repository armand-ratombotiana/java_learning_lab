# Pipeline Architecture Flashcards

## Q: What is a pipeline?
**A:** A sequence of processing stages where each stage transforms data and passes it to the next.

## Q: What is Chain of Responsibility?
**A:** A behavioral pattern where multiple handlers each decide to process or pass the request to the next.

## Q: What is a filter stage?
**A:** A stage that removes data items based on specified criteria.

## Q: What is a transformer stage?
**A:** A stage that converts data from one format to another.

## Q: What is an enricher stage?
**A:** A stage that adds data from external sources to the pipeline data.

## Q: What is an aggregator stage?
**A:** A stage that combines multiple data items into a single item.

## Q: What is a splitter stage?
**A:** A stage that divides a data item into multiple items.

## Q: Why should stages be stateless?
**A:** Stateless stages are composable, testable, and safe for parallel execution.

## Q: What is a dead letter queue?
**A:** A destination for failed pipeline items that cannot be processed.

## Q: What is a branching pipeline?
**A:** A pipeline that routes data to different branches based on conditions.
