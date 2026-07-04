# Service Mesh Exercises

## Exercise 1: Install Istio
Install Istio on a Kubernetes cluster using the demo profile. Verify all components are running.

## Exercise 2: Deploy Bookinfo
Deploy the Bookinfo sample application. Verify sidecar injection and service connectivity.

## Exercise 3: Traffic Routing
Route 100% of reviews traffic to v1. Then split 50/50 between v1 and v3.

## Exercise 4: Fault Injection
Inject a 5-second delay into reviews v2. Observe the effect on the product page.

## Exercise 5: Circuit Breaking
Configure circuit breaking for the httpbin service. Test with high concurrency requests.

## Exercise 6: mTLS
Enable STRICT mTLS. Verify that a pod without sidecar cannot communicate.

## Exercise 7: Authorization Policies
Create a policy that allows only GET requests from the product page to reviews.

## Exercise 8: Telemetry
Open Kiali, Grafana, and Jaeger dashboards. Examine service graph and traces.

## Exercise 9: Egress Gateway
Configure an egress gateway to route external traffic through a controlled proxy.

## Exercise 10: Upgrade Istio
Upgrade Istio control plane and data plane to a newer version. Verify mesh integrity.
