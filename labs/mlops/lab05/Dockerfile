# Multi-stage build for Java ML model server
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY src/com/mlops/lab05/ModelServingLab.java ./com/mlops/lab05/ModelServingLab.java
RUN javac com/mlops/lab05/ModelServingLab.java

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app .
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=5s --retries=3 \
    CMD java -cp . com.mlops.lab05.ModelServingLab --health || exit 1
CMD ["java", "com.mlops.lab05.ModelServingLab"]
