# Helm Exercises

## Exercise 1: Install a Pre-Made Chart
Install Bitnami Nginx chart with custom values (replicaCount=2, service.type=NodePort).

## Exercise 2: Create a Chart
Run `helm create myapp`. Examine the default chart structure. Install it.

## Exercise 3: Customize Values
Modify values.yaml to use a custom image. Upgrade the release.

## Exercise 4: Add a ConfigMap Template
Add a ConfigMap template to your chart with key-value pairs from values.yaml.

## Exercise 5: Use _helpers.tpl
Create helper templates for common labels. Use them in deployment.yaml and service.yaml.

## Exercise 6: Environment-Specific Values
Create values-dev.yaml, values-staging.yaml, values-prod.yaml. Deploy with `-f values-prod.yaml`.

## Exercise 7: Dependencies
Add a dependency on Bitnami Redis. Override Redis auth settings in parent values.

## Exercise 8: Hooks
Add a pre-upgrade hook that runs a database migration Job.

## Exercise 9: Package and Push
Package your chart. Push to an OCI registry. Install from the registry.
