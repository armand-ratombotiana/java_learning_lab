# Step-by-Step — Spring Boot + RDS + ElastiCache

## Step 1: Launch RDS MySQL Instance
```powershell
# Create RDS subnet group
aws rds create-db-subnet-group --db-subnet-group-name java-db-subnet `
    --subnet-ids subnet-xxx subnet-yyy --db-subnet-group-description "Java DB subnet"

# Launch RDS instance
aws rds create-db-instance --db-instance-identifier java-db `
    --db-instance-class db.t3.micro --engine mysql `
    --master-username app_admin --master-user-password ChangeMe123! `
    --allocated-storage 20 --db-subnet-group-name java-db-subnet `
    --vpc-security-group-ids sg-xxx --multi-az --backup-retention-period 7
```

## Step 2: Configure Security Group
```powershell
# Allow EC2 app servers to connect to RDS
aws ec2 authorize-security-group-ingress --group-id sg-rds-xxx `
    --protocol tcp --port 3306 --source-group sg-app-xxx
```

## Step 3: Create Spring Boot Data Source
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://java-db.xxxxx.us-east-1.rds.amazonaws.com:3306/mydb
    username: app_admin
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 3000
      idle-timeout: 600000
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

## Step 4: Deploy ElastiCache Redis
```powershell
aws elasticache create-cache-cluster --cache-cluster-id java-cache `
    --cache-node-type cache.t3.micro --engine redis `
    --num-cache-nodes 1 --security-group-ids sg-cache-xxx
```

## Step 5: Configure Redis Session Caching
```java
// RedisConfig.java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
            "java-cache.xxxxx.ng.0001.use1.cache.amazonaws.com", 6379);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

## Step 6: Test the Setup
```bash
# Compile and run
./mvnw spring-boot:run

# Test session caching
curl -X POST http://localhost:8080/login -d "user=test"
curl -b cookies.txt http://localhost:8080/dashboard
```

## Step 7: Clean Up
```powershell
aws rds delete-db-instance --db-instance-identifier java-db --skip-final-snapshot
aws elasticache delete-cache-cluster --cache-cluster-id java-cache
```
