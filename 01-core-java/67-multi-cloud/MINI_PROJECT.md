# Module 67: Multi-Cloud Architectures - Mini Project

**Project Name**: Cloud-Agnostic Secret Management (HashiCorp Vault)  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Design a Spring Boot microservice that demonstrates multi-cloud readiness by abstracting away cloud-specific proprietary services (like AWS Secrets Manager or Azure Key Vault). You will integrate HashiCorp Vault to fetch database credentials dynamically, ensuring the application can be deployed identically to any cloud provider.

## 📝 Requirements

### Core Features

1. **Vault Infrastructure Setup**:
   - Run a local instance of HashiCorp Vault using Docker.
   - Start it in "dev mode" with a fixed root token: `docker run --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=myroot' -e 'VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200' -p 8200:8200 vault`
   - Access the Vault CLI or UI (`http://localhost:8200`) and create a new key-value secret path at `secret/myapp`.
   - Add two key-value pairs: `username=admin` and `password=supersecret`.

2. **Spring Boot Integration**:
   - Create a Spring Boot app with `spring-cloud-starter-vault-config` and `spring-boot-starter-web`.
   - Create an `application.yml` that configures Spring Cloud Vault to connect to your local Vault instance using the root token.

3. **Dynamic Secret Retrieval**:
   - Create a `@RestController` named `ConfigController`.
   - Inject the database username and password into the controller using `@Value("${username}")` and `@Value("${password}")`.
   - Create an endpoint `GET /api/config` that returns the injected username and the length of the password (never return the plaintext password).

4. **Multi-Cloud Simulation**:
   - Write a short `ARCHITECTURE.md` file within the project explaining how this setup achieves multi-cloud readiness. If deploying to AWS, you do not need to rewrite the Spring app to use the AWS SDK; you just deploy Vault on Kubernetes and the Spring app remains completely cloud-agnostic.

---

## 💡 Solution Blueprint

1. **Vault Properties (`bootstrap.yml` or `application.yml` for Spring Boot 2.4+)**:
   ```yaml
   spring:
     application:
       name: myapp
     cloud:
       vault:
         host: localhost
         port: 8200
         scheme: http
         authentication: TOKEN
         token: myroot
         kv:
           enabled: true
           backend: secret
           default-context: myapp
   ```

2. **The Controller**:
   ```java
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;

   @RestController
   public class ConfigController {

       // Injected seamlessly from HashiCorp Vault!
       @Value("${username}")
       private String dbUser;

       @Value("${password}")
       private String dbPassword;

       @GetMapping("/api/config")
       public String getConfig() {
           return "Connected as user: " + dbUser + " | Password Length: " + dbPassword.length();
       }
   }
   ```