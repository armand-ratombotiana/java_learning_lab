package com.learning.k8s.operators;

public class K8sOperatorsLab {

    public static void main(String[] args) {
        System.out.println("=== Kubernetes Operators Lab ===\n");

        System.out.println("1. Operator Pattern:");
        System.out.println("   - Controller watches for CRD changes");
        System.out.println("   - Reconcile loop ensures desired state");
        System.out.println("   - Custom Resource Definitions extend K8s API");

        System.out.println("\n2. Java Operator SDK Example:");
        System.out.println("   @Controller(name = \"myapp\")");
        System.out.println("   public class MyController implements Reconciler<MyApp> {");
        System.out.println("       public UpdateControl<MyApp> reconcile(MyApp app) {");
        System.out.println("           // Ensure deployment, services, configmaps exist");
        System.out.println("           return UpdateControl.updateStatus(app);");
        System.out.println("       }");
        System.out.println("   }");

        System.out.println("\n3. Popular Operators:");
        System.out.println("   - Prometheus Operator");
        System.out.println("   - Elastic Operator");
        System.out.println("   - Postgres Operator");
        System.out.println("   - Cert Manager");

        System.out.println("\n=== K8s Operators Lab Complete ===");
    }
}