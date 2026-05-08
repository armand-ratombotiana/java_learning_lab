package com.learning.cloudnative;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Helm Chart Concepts ===\n");

        demonstrateCoreConcepts();
        demonstrateChartStructure();
        demonstrateTemplating();
        demonstrateValues();
        demonstrateReleases();
        demonstrateAdvanced();
    }

    private static void demonstrateCoreConcepts() {
        System.out.println("--- Core Concepts ---");
        System.out.println("Helm = Kubernetes package manager");
        System.out.println("Chart  -> A packaged K8s application (template + config)");
        System.out.println("Release-> A deployed instance of a chart");
        System.out.println("Repo   -> Chart repository (index.yaml + packaged charts)");
        System.out.println();
        System.out.println("Benefits: repeatable deployments, parameterization, rollbacks");
    }

    private static void demonstrateChartStructure() {
        System.out.println("\n--- Chart Directory Structure ---");
        System.out.println("mychart/");
        System.out.println("  Chart.yaml          -> Metadata (name, version, apiVersion)");
        System.out.println("  values.yaml         -> Default configuration values");
        System.out.println("  values.schema.json  -> JSON Schema for values validation");
        System.out.println("  templates/          -> Go template files (*.yaml, *.tpl)");
        System.out.println("    deployment.yaml   -> K8s Deployment template");
        System.out.println("    service.yaml      -> K8s Service template");
        System.out.println("    _helpers.tpl      -> Shared template functions");
        System.out.println("  charts/             -> Sub-chart dependencies");
        System.out.println("  crds/               -> Custom Resource Definitions");
    }

    private static void demonstrateTemplating() {
        System.out.println("\n--- Templating (Go Templates) ---");
        System.out.println("{{ .Values.replicaCount }}  -> reference values");
        System.out.println("{{ .Release.Name }}         -> release metadata");
        System.out.println("{{ .Chart.Name }}           -> chart metadata");
        System.out.println();
        System.out.println("Template functions:");
        System.out.println("  {{ default \"nginx\" .Values.image.repository }}");
        System.out.println("  {{ upper .Values.env }}   -> \"PRODUCTION\"");
        System.out.println("  {{ quote .Values.name }}  -> \"my-app\"");
        System.out.println("  {{ .Values.config | toYaml | indent 8 }}");
        System.out.println();
        System.out.println("Control: {{- if .Values.ingress.enabled }} ... {{- end }}");
        System.out.println("Range:   {{- range .Values.ports }} ... {{- end }}");
    }

    private static void demonstrateValues() {
        System.out.println("\n--- Values Precedence (Low to High) ---");
        System.out.println("1. values.yaml (built into chart)");
        System.out.println("2. Parent chart values (if sub-chart)");
        System.out.println("3. --values (or -f) file flag");
        System.out.println("4. --set key=value flag");
        System.out.println("5. --set-json flag");
        System.out.println();
        System.out.println("Best practice: environment-specific overrides");
        System.out.println("  helm install --values prod-values.yaml myapp ./mychart");
    }

    private static void demonstrateReleases() {
        System.out.println("\n--- Release Lifecycle ---");
        System.out.println("helm install   -> Deploy new release");
        System.out.println("helm upgrade   -> Upgrade to new chart/values version");
        System.out.println("helm rollback  -> Rollback to previous revision");
        System.out.println("helm uninstall -> Remove release");
        System.out.println("helm list      -> List releases");
        System.out.println("helm history   -> Show revision history");
        System.out.println();
        System.out.println("Upgrade strategies:");
        System.out.println("  --recreate-pods (hard), --atomic (rollback on failure)");
        System.out.println("  --cleanup-on-fail, --max-history (limit revisions)");
    }

    private static void demonstrateAdvanced() {
        System.out.println("\n--- Advanced Features ---");
        System.out.println("Hooks (pre/post install/upgrade/delete/rollback):");
        System.out.println("  helm.sh/hook: pre-install, post-install, pre-delete, etc.");
        System.out.println("  Hook weights for ordering, hook deletion policy");
        System.out.println();
        System.out.println("Sub-charts / Dependencies:");
        System.out.println("  dependencies in Chart.yaml (bitnami/postgresql, bitnami/redis)");
        System.out.println("  helm dependency update -> pulls + packages into charts/");
        System.out.println();
        System.out.println("Chart Testing:");
        System.out.println("  helm lint            -> validate chart");
        System.out.println("  helm template        -> render locally (dry-run)");
        System.out.println("  helm test            -> run pod test hooks");
    }
}
