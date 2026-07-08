package com.devops.thirteen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationSetGenerator {
    public enum GeneratorType { LIST, GIT, CLUSTER, MATRIX, MERGE, SCM, PULL_REQUEST }

    private final String name;
    private final GeneratorType type;
    private final List<Map<String, String>> generatedParams = new ArrayList<>();

    public ApplicationSetGenerator(String name, GeneratorType type) {
        this.name = name;
        this.type = type;
    }

    public void generateFromList(List<Map<String, String>> elements) {
        System.out.printf("ApplicationSet '%s' using LIST generator with %d elements%n", name, elements.size());
        for (var element : elements) {
            Map<String, String> params = new HashMap<>(element);
            generatedParams.add(params);
            System.out.printf("  Generated: cluster=%s, env=%s%n",
                element.get("cluster"), element.get("environment"));
        }
    }

    public void generateFromGit(String repoUrl, String directory) {
        System.out.printf("ApplicationSet '%s' using GIT generator: %s/%s%n", name, repoUrl, directory);
        Map<String, String> params = new HashMap<>();
        params.put("path", directory);
        params.put("cluster", "production");
        params.put("environment", "prod");
        generatedParams.add(params);
        System.out.printf("  Generated: path=%s, cluster=%s%n", directory, "production");
    }

    public void generateFromClusters(List<String> clusterUrls) {
        System.out.printf("ApplicationSet '%s' using CLUSTER generator with %d clusters%n", name, clusterUrls.size());
        for (String url : clusterUrls) {
            Map<String, String> params = new HashMap<>();
            params.put("server", url);
            params.put("name", url.replaceAll("https?://", "").replaceAll("\\..*", ""));
            generatedParams.add(params);
            System.out.printf("  Generated: server=%s, name=%s%n", url, params.get("name"));
        }
    }

    public List<ArgoCDApplication> generateApplications(String repoUrl, String basePath, String targetRevision, String namespace) {
        List<ArgoCDApplication> apps = new ArrayList<>();
        for (Map<String, String> params : generatedParams) {
            String appName = name + "-" + params.getOrDefault("cluster", "default");
            ArgoCDApplication app = new ArgoCDApplication(appName, repoUrl,
                basePath + "/" + params.getOrDefault("environment", "dev"), targetRevision, namespace);
            params.forEach(app::setParameter);
            apps.add(app);
            System.out.printf("Created Application '%s' from template%n", appName);
        }
        return apps;
    }

    public static void main(String[] args) {
        ApplicationSetGenerator gen = new ApplicationSetGenerator("my-appset", GeneratorType.LIST);
        List<Map<String, String>> clusters = List.of(
            Map.of("cluster", "us-east-1", "environment", "prod"),
            Map.of("cluster", "us-west-2", "environment", "staging"),
            Map.of("cluster", "eu-west-1", "environment", "dev"));
        gen.generateFromList(clusters);
        List<ArgoCDApplication> apps = gen.generateApplications(
            "https://github.com/org/config.git", "k8s/overlays", "main", "default");
        apps.forEach(a -> { a.sync(); System.out.println(a.status()); });
    }
}
