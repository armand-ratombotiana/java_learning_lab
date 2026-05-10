# Data Pipeline Projects - Module 62

This module covers ETL pipelines, Apache Airflow, and Dataflow for data processing.

## Mini-Project: Simple ETL Pipeline (2-4 hours)

### Overview
Build a data pipeline using Spring Batch for ETL operations with CSV input, transformation, and database output.

### Project Structure
```
data-pipeline-demo/
├── src/main/java/com/learning/pipeline/
│   ├── DataPipelineApplication.java
│   ├── config/BatchConfig.java
│   ├── reader/CsvItemReader.java
│   ├── processor/DataProcessor.java
│   ├── writer/DatabaseItemWriter.java
│   └── model/Product.java
├── src/main/resources/
│   └── schema.sql
├── data/
│   └── products.csv
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>data-pipeline-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-batch</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>

// DataPipelineApplication.java
package com.learning.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataPipelineApplication {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(
            DataPipelineApplication.class, args)));
    }
}

// BatchConfig.java
package com.learning.pipeline.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
    
    @Bean
    public FlatFileItemReader<Product> reader() {
        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("data/products.csv"));
        reader.setLinesToSkip(1);
        reader.setLineTokenizer(new DelimitedLineTokenizer());
        
        BeanWrapperFieldSetMapper<Product> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Product.class);
        reader.setFieldSetMapper(mapper);
        
        return reader;
    }
    
    @Bean
    public ItemProcessor<Product, Product> processor() {
        return new DataProcessor();
    }
    
    @Bean
    public ItemWriter<Product> writer(EntityManagerFactory entityManagerFactory) {
        return items -> {
            for (Product item : items) {
                entityManagerFactory.createEntityManager()
                    .persist(item);
            }
        };
    }
    
    @Bean
    public Step step1(JobRepository jobRepository, 
                      PlatformTransactionManager transactionManager,
                      ItemReader<Product> reader,
                      ItemProcessor<Product, Product> processor,
                      ItemWriter<Product> writer) {
        
        return new StepBuilder("step1", jobRepository)
            .<Product, Product>chunk(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
    
    @Bean
    public Job etlJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("etlJob", jobRepository)
            .start(step1)
            .build();
    }
}

// Product.java
package com.learning.pipeline.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String category;
    private Double price;
    private Integer stock;
    private String processedAt;
    
    @PrePersist
    public void prePersist() {
        this.processedAt = java.time.LocalDateTime.now().toString();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getProcessedAt() { return processedAt; }
    public void setProcessedAt(String processedAt) { this.processedAt = processedAt; }
}

// DataProcessor.java
package com.learning.pipeline;

import org.springframework.batch.item.ItemProcessor;

public class DataProcessor implements ItemProcessor<Product, Product> {
    
    @Override
    public Product process(Product product) throws Exception {
        if (product.getPrice() == null || product.getPrice() < 0) {
            return null;
        }
        
        if (product.getCategory() != null) {
            product.setCategory(product.getCategory().toUpperCase());
        }
        
        if (product.getStock() != null && product.getStock() < 0) {
            product.setStock(0);
        }
        
        return product;
    }
}

// application.yml
spring:
  batch:
    job:
      enabled: false
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver

# run.sh
#!/bin/bash
mvn clean package -DskipTests
java -jar target/data-pipeline-demo-1.0.0.jar
```

---

## Real-World Project: Enterprise Data Pipeline with Airflow (8+ hours)

### Overview
Build a comprehensive data pipeline using Apache Airflow for orchestrating ETL jobs, with data validation, monitoring, and error handling.

### Project Structure
```
enterprise-data-pipeline/
├── dags/
│   ├── data_pipeline_dag.py
│   ├── validation_dag.py
│   └── reporting_dag.py
├── operators/
│   ├── extract_operator.py
│   ├── transform_operator.py
│   ├── load_operator.py
│   └── validation_operator.py
├── hooks/
│   ├── jdbc_hook.py
│   ├── s3_hook.py
│   └── http_hook.py
├── plugins/
├── src/main/java/com/learning/
│   ├── DataPipelineApplication.java
│   ├── service/TransformationService.java
│   ├── service/ValidationService.java
│   └── service/ReportingService.java
├── tests/
├── docker-compose.yml
├── requirements.txt
└── pom.xml
```

### Implementation

#### Airflow DAG
```python
# dags/data_pipeline_dag.py
from airflow import DAG
from airflow.operators.python import PythonOperator, BranchPythonOperator
from airflow.operators.dummy import DummyOperator
from airflow.providers.postgres.operators.postgres import PostgresOperator
from datetime import datetime, timedelta
import logging

default_args = {
    'owner': 'data-team',
    'depends_on_past': False,
    'start_date': datetime(2024, 1, 1),
    'email_on_failure': True,
    'email_on_retry': False,
    'retries': 3,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'enterprise_data_pipeline',
    default_args=default_args,
    description='Enterprise ETL Data Pipeline',
    schedule_interval='0 2 * * *',
    catchup=False,
    max_active_runs=1
)

def extract_data(**context):
    logging.info("Starting data extraction...")
    from hooks.jdbc_hook import JdbcHook
    
    hook = JdbcHook(jdbc_conn_id='source_database')
    records = hook.get_records("SELECT * FROM source_table WHERE status = 'PENDING'")
    
    ti = context['ti']
    ti.xcom_push(key='extracted_records', value=records)
    logging.info(f"Extracted {len(records)} records")

def transform_data(**context):
    ti = context['ti']
    records = ti.xcom_pull(key='extracted_records')
    
    from src.service.transformation_service import TransformationService
    service = TransformationService()
    
    transformed = service.transform(records)
    
    ti.xcom_push(key='transformed_data', value=transformed)
    logging.info(f"Transformed {len(transformed)} records")

def validate_data(**context):
    ti = context['ti']
    transformed = ti.xcom_pull(key='transformed_data')
    
    from src.service.validation_service import ValidationService
    service = ValidationService()
    
    valid, errors = service.validate(transformed)
    
    if not valid:
        logging.error(f"Validation failed: {errors}")
        return 'handle_errors'
    
    ti.xcom_push(key='validated_data', value=valid)
    return 'load_data'

def load_data(**context):
    ti = context['ti']
    validated = ti.xcom_pull(key='validated_data')
    
    from hooks.s3_hook import S3Hook
    hook = S3Hook(aws_conn_id='aws_s3')
    
    hook.upload_data(validated, 'bucket', f'data/{datetime.now().strftime("%Y%m%d")}.json')
    logging.info("Data loaded to S3")

def handle_errors(**context):
    logging.error("Pipeline failed - sending alert")
    raise Exception("Validation or processing failed")

def generate_report(**context):
    from src.service.reporting_service import ReportingService
    service = ReportingService()
    
    report = service.generate_daily_report()
    logging.info(f"Report generated: {report}")

start = DummyOperator(task_id='start', dag=dag)

extract = PythonOperator(
    task_id='extract',
    python_callable=extract_data,
    dag=dag
)

transform = PythonOperator(
    task_id='transform',
    python_callable=transform_data,
    dag=dag
)

validate = BranchPythonOperator(
    task_id='validate',
    python_callable=validate_data,
    dag=dag
)

load = PythonOperator(
    task_id='load_data',
    python_callable=load_data,
    dag=dag
)

handle_errors_task = PythonOperator(
    task_id='handle_errors',
    python_callable=handle_errors,
    dag=dag
)

report = PythonOperator(
    task_id='report',
    python_callable=generate_report,
    dag=dag
)

end = DummyOperator(task_id='end', dag=dag)

start >> extract >> transform >> validate
validate >> [load, handle_errors_task]
load >> report >> end
```

#### Java Transformation Service
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>enterprise-data-pipeline</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-s3</artifactId>
            <version>1.12.600</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
</project>

// TransformationService.java
package com.learning.service;

import com.learning.model.RawRecord;
import com.learning.model.TransformResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransformationService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    public List<Map<String, Object>> transform(List<Map<String, Object>> records) {
        return records.stream()
            .map(this::transformRecord)
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> transformRecord(Map<String, Object> record) {
        Map<String, Object> transformed = new HashMap<>();
        
        transformed.put("id", UUID.randomUUID().toString());
        transformed.put("originalId", record.get("id"));
        transformed.put("name", normalizeString((String) record.get("name")));
        transformed.put("email", normalizeEmail((String) record.get("email")));
        transformed.put("age", normalizeAge(record.get("age")));
        transformed.put("salary", normalizeSalary(record.get("salary")));
        transformed.put("department", normalizeDepartment((String) record.get("department")));
        transformed.put("joinDate", parseDate(record.get("joinDate")));
        transformed.put("status", determineStatus(record));
        transformed.put("riskScore", calculateRiskScore(record));
        transformed.put("transformedAt", LocalDateTime.now().format(FORMATTER));
        
        return transformed;
    }
    
    private String normalizeString(String value) {
        if (value == null || value.isEmpty()) return "UNKNOWN";
        return value.trim().toUpperCase();
    }
    
    private String normalizeEmail(String email) {
        if (email == null || email.isEmpty()) return null;
        return email.trim().toLowerCase();
    }
    
    private Integer normalizeAge(Object age) {
        if (age == null) return 0;
        if (age instanceof Number) {
            return Math.max(0, ((Number) age).intValue());
        }
        try {
            return Math.max(0, Integer.parseInt(age.toString()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private Double normalizeSalary(Object salary) {
        if (salary == null) return 0.0;
        if (salary instanceof Number) {
            return ((Number) salary).doubleValue();
        }
        try {
            return Double.parseDouble(salary.toString().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private String normalizeDepartment(String dept) {
        if (dept == null) return "UNKNOWN";
        
        Map<String, String> mapping = new HashMap<>();
        mapping.put("ENG", "ENGINEERING");
        mapping.put("SALES", "SALES");
        mapping.put("HR", "HUMAN_RESOURCES");
        mapping.put("MARKETING", "MARKETING");
        
        return mapping.getOrDefault(dept.toUpperCase(), "OTHER");
    }
    
    private String parseDate(Object date) {
        if (date == null) return null;
        try {
            return date.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    private String determineStatus(Map<String, Object> record) {
        Object salary = record.get("salary");
        Object age = record.get("age");
        
        if (salary == null || age == null) return "UNKNOWN";
        
        double salaryValue = normalizeSalary(salary);
        int ageValue = normalizeAge(age);
        
        if (salaryValue > 100000) return "HIGH_INCOME";
        if (ageValue > 50) return "SENIOR";
        return "REGULAR";
    }
    
    private double calculateRiskScore(Map<String, Object> record) {
        double score = 0;
        
        if (record.get("email") == null) score += 30;
        if (record.get("salary") != null) {
            double salary = normalizeSalary(record.get("salary"));
            if (salary < 20000) score += 20;
            if (salary > 200000) score += 10;
        }
        
        return Math.min(100, score);
    }
}

// ValidationService.java
package com.learning.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ValidationService {
    
    public ValidationResult validate(List<Map<String, Object>> records) {
        List<String> errors = new ArrayList<>();
        List<Map<String, Object>> validRecords = new ArrayList<>();
        
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);
            List<String> recordErrors = validateRecord(record, i);
            
            if (recordErrors.isEmpty()) {
                validRecords.add(record);
            } else {
                errors.addAll(recordErrors);
            }
        }
        
        return new ValidationResult(!errors.isEmpty(), errors, validRecords);
    }
    
    private List<String> validateRecord(Map<String, Object> record, int index) {
        List<String> errors = new ArrayList<>();
        
        if (!record.containsKey("name") || record.get("name") == null) {
            errors.add("Record " + index + ": name is required");
        }
        
        if (record.containsKey("email")) {
            String email = (String) record.get("email");
            if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.add("Record " + index + ": invalid email format");
            }
        }
        
        if (record.containsKey("age")) {
            Object age = record.get("age");
            if (age instanceof Number) {
                if (((Number) age).intValue() < 0 || ((Number) age).intValue() > 150) {
                    errors.add("Record " + index + ": age out of valid range");
                }
            }
        }
        
        if (record.containsKey("salary")) {
            Object salary = record.get("salary");
            if (salary instanceof Number) {
                if (((Number) salary).doubleValue() < 0) {
                    errors.add("Record " + index + ": salary cannot be negative");
                }
            }
        }
        
        return errors;
    }
}

record ValidationResult(boolean hasErrors, List<String> errors, 
                       List<Map<String, Object>> validRecords) {}

// ReportingService.java
package com.learning.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ReportingService {
    
    public String generateDailyReport() {
        return "Daily report generated for " + LocalDate.now();
    }
    
    public Map<String, Object> generateSummary(Map<String, Object> data) {
        return Map.of(
            "date", LocalDate.now(),
            "status", "COMPLETED",
            "recordsProcessed", data.getOrDefault("count", 0),
            "errors", data.getOrDefault("errors", 0)
        );
    }
}

// docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: airflow
      POSTGRES_PASSWORD: airflow
      POSTGRES_DB: airflow
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  airflow-webserver:
    image: apache/airflow:2.8.1
    environment:
      AIRFLOW__CORE__EXECUTOR: LocalExecutor
      AIRFLOW__DATABASE__SQL_ALCHEMY_CONN: postgresql+psycopg2://airflow:airflow@postgres/airflow
      AIRFLOW__CORE__FERNET_KEY: ''
    ports:
      - "8080:8080"
    volumes:
      - ./dags:/opt/airflow/dags
      - ./plugins:/opt/airflow/plugins
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Build and Deploy
```bash
# Install dependencies
pip install -r requirements.txt

# Initialize Airflow database
airflow db init

# Create connection
airflow connections add 'source_database' \
    --conn-type 'postgres' \
    --conn-host 'localhost' \
    --conn-login 'admin' \
    --conn-password 'password' \
    --conn-schema 'source'

# Build Java application
mvn clean package

# Run Airflow
docker-compose up -d

# Trigger DAG manually
airflow dags trigger enterprise_data_pipeline

# Monitor execution
airflow tasks list enterprise_data_pipeline
airflow dags list-runs -d enterprise_data_pipeline

# View logs
docker logs airflow-webserver
```

### Learning Outcomes
- Build ETL pipelines with Spring Batch
- Orchestrate data workflows with Apache Airflow
- Implement data validation and error handling
- Create data transformation services
- Set up monitoring and reporting
- Deploy data pipelines to production