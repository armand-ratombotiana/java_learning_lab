# Module 68: Istio & Linkerd - Mini Project

**Project Name**: Canary Deployment with Istio  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Use Istio's Traffic Management capabilities to perform a zero-downtime Canary Deployment. You will deploy two versions of a Java microservice and dynamically shift traffic between them without altering Kubernetes Deployment replicas.

## 📝 Requirements

### Core Features

1. **The Application Deployments**:
   - Create a simple Spring Boot app returning "Hello from V1". Build and push the Docker image.
   - Change the return string to "Hello from V2". Build and push as V2.
   - Create two Kubernetes deployments (`app-v1` and `app-v2`). Ensure both deployments have the label `app: myapp`, but give them distinct version labels: `version: v1` and `version: v2`.
   - Create a single Kubernetes Service that selects `app: myapp`.

2. **Istio Injection**:
   - Ensure the namespace has `istio-injection=enabled`.
   - Deploy the YAMLs. Verify 2/2 containers are running in both pods.

3. **Destination Rule**:
   - Create an Istio `DestinationRule`.
   - Define subsets mapped to the `version` labels.

4. **Virtual Service (The Canary)**:
   - Create an Istio `VirtualService`.
   - Configure the routing so that 90% of requests go to the `v1` subset, and 10% go to the `v2` subset.

5. **Testing & Promotion**:
   - Write a bash script that curls the service 100 times. Observe that ~10 requests hit V2.
   - Edit the VirtualService YAML to shift 50% to V2, then 100% to V2. Observe the traffic shift seamlessly without any pods restarting.

---

## 💡 Solution Blueprint

1. **DestinationRule (`destination-rule.yaml`)**:
   ```yaml
   apiVersion: networking.istio.io/v1alpha3
   kind: DestinationRule
   metadata:
     name: myapp-destination
   spec:
     host: myapp-service # The K8s Service name
     subsets:
     - name: v1
       labels:
         version: v1
     - name: v2
       labels:
         version: v2
   ```

2. **VirtualService (`virtual-service.yaml`)**:
   ```yaml
   apiVersion: networking.istio.io/v1alpha3
   kind: VirtualService
   metadata:
     name: myapp-route
   spec:
     hosts:
     - myapp-service
     http:
     - route:
       - destination:
           host: myapp-service
           subset: v1
         weight: 90
       - destination:
           host: myapp-service
           subset: v2
         weight: 10
   ```