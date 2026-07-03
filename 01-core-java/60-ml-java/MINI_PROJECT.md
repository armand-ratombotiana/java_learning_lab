# Module 60: Machine Learning in Java - Mini Project

**Project Name**: PMML Model Inference Service  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Bridge the gap between Python Data Science and Java Backend Engineering. You will load a pre-trained Machine Learning model (saved in PMML format) into a Spring Boot REST API and use it to execute real-time predictions without relying on network calls to a Python server.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Spring Boot Web application.
   - Add the `jpmml-evaluator` dependency to your `pom.xml`. This library executes PMML models completely within the JVM.

2. **The Pre-Trained Model**:
   - Create a mock `model.pmml` file in your `src/main/resources` folder. (For the sake of this project, you can download a sample Decision Tree or Logistic Regression PMML file from the internet, e.g., an Iris dataset classifier or a Housing Price predictor).

3. **The Inference Engine (Service)**:
   - Create a `@Service` class called `ModelScoringService`.
   - In the constructor (or via `@PostConstruct`), load the `model.pmml` file as an `InputStream`.
   - Parse the input stream into a JPMML `Evaluator` object.
   - Write a method `public Map<String, ?> predict(Map<String, Object> inputFeatures)`.
   - Inside the method, map the `inputFeatures` to the evaluator's input fields, execute the evaluation (`evaluator.evaluate(arguments)`), and extract the target output result.

4. **The REST Controller**:
   - Create a `@RestController` mapped to `/api/predict`.
   - Create a `POST` endpoint that accepts a JSON payload of features (e.g., `{"sepalLength": 5.1, "sepalWidth": 3.5, ...}`).
   - Pass the payload to the `ModelScoringService` and return the predicted classification as a JSON response.

5. **Execution**:
   - Send an HTTP request via Postman or `curl` to your endpoint. Observe that the Java application is executing a Python-trained model locally with sub-millisecond latency.

---

## 💡 Solution Blueprint

1. **POM Dependency**:
   ```xml
   <dependency>
       <groupId>org.jpmml</groupId>
       <artifactId>pmml-evaluator</artifactId>
       <version>1.6.4</version>
   </dependency>
   ```

2. **The Scoring Service**:
   ```java
   import org.jpmml.evaluator.*;
   import org.springframework.stereotype.Service;
   import jakarta.annotation.PostConstruct;

   @Service
   public class ModelScoringService {
       private Evaluator evaluator;

       @PostConstruct
       public void init() throws Exception {
           try (InputStream is = getClass().getClassLoader().getResourceAsStream("model.pmml")) {
               evaluator = new LoadingModelEvaluatorBuilder()
                   .load(is)
                   .build();
               evaluator.verify();
           }
       }

       public Object predict(Map<String, Object> inputData) {
           // Map input data to PMML Arguments
           Map<String, FieldValue> arguments = new LinkedHashMap<>();
           for (InputField inputField : evaluator.getInputFields()) {
               Object rawValue = inputData.get(inputField.getName());
               FieldValue fieldValue = inputField.prepare(rawValue);
               arguments.put(inputField.getName(), fieldValue);
           }

           // Execute the model
           Map<String, ?> results = evaluator.evaluate(arguments);

           // Extract target field
           TargetField targetField = evaluator.getTargetFields().get(0);
           return results.get(targetField.getName());
       }
   }
   ```