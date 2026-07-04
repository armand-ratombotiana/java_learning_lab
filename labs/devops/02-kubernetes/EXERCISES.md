# Kubernetes Exercises

## Exercise 1: Pod Basics
Create a pod running nginx:alpine. View logs. Execute bash inside it.

## Exercise 2: Deployments
Create a Deployment with 3 replicas of a custom app. Perform a rolling update. Roll back.

## Exercise 3: Services
Expose the deployment via ClusterIP, then change to NodePort. Test access.

## Exercise 4: ConfigMaps
Create a ConfigMap with app configuration. Mount it as a volume in a pod. Verify the file contents.

## Exercise 5: Secrets
Create a Secret with database credentials. Inject as environment variables. Verify it's base64-encoded but not encrypted.

## Exercise 6: Ingress
Install an Ingress controller (nginx-ingress). Create TLS certificate. Set up routing rules for two services.

## Exercise 7: Multi-Container Pods
Create a pod with a main container and a sidecar that tails logs and sends them to stdout.

## Exercise 8: Health Probes
Add liveness, readiness, and startup probes to a deployment. Intentionally break the health endpoint and observe behavior.
