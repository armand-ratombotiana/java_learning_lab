package com.security.deep.lab05;

import java.util.*;
import java.util.regex.*;

public class ContainerSecurity {

    public static List<String> validateDockerfile(String dockerfile) {
        List<String> violations = new ArrayList<>();
        String[] lines = dockerfile.split("\n");
        boolean hasNonRoot = false;
        boolean hasExactTag = false;
        boolean hasMultiStage = false;
        boolean hasLatestTag = false;

        for (String line : lines) {
            String trimmed = line.strip();
            String upper = trimmed.toUpperCase();

            if (upper.startsWith("FROM ")) {
                if (trimmed.matches(".*:\\s*latest\\s*$")) hasLatestTag = true;
                else if (!trimmed.matches(".*:[a-zA-Z0-9.\\-_]+\\s*$")) hasLatestTag = true;
                else hasExactTag = true;
                if (trimmed.contains(" AS ")) hasMultiStage = true;
            }

            if (upper.startsWith("USER ")) {
                String user = trimmed.substring(5).trim();
                if (!user.equals("root")) hasNonRoot = true;
            }
        }

        if (!hasNonRoot) violations.add("MISSING_NON_ROOT: Use 'USER' directive to run as non-root");
        if (hasLatestTag) violations.add("LATEST_TAG: Avoid ':latest' tag; use specific version");
        if (!hasExactTag) violations.add("NO_VERSION: Pin base image to specific version");
        if (!hasMultiStage) violations.add("MULTI_STAGE: Use multi-stage builds to reduce image size");
        if (dockerfile.contains("ADD")) violations.add("ADD_USAGE: Prefer COPY over ADD (ADD has auto-extract and URL risks)");

        return violations;
    }

    public static record ImageVulnerability(String packageName, String version,
                                              String severity, String fixedVersion) {}

    public static List<ImageVulnerability> mockImageScan(String imageName, String tag) {
        List<ImageVulnerability> vulns = new ArrayList<>();
        if (imageName.contains("ubuntu") || imageName.contains("debian")) {
            vulns.add(new ImageVulnerability("libssl", "1.1.1", "HIGH", "1.1.1t"));
            vulns.add(new ImageVulnerability("curl", "7.68.0", "MEDIUM", "7.74.0"));
            vulns.add(new ImageVulnerability("bash", "5.0", "LOW", "5.1"));
        }
        if (imageName.contains("alpine")) {
            vulns.add(new ImageVulnerability("apk-tools", "2.12.7", "MEDIUM", "2.12.9"));
        }
        if (imageName.contains("nginx")) {
            vulns.add(new ImageVulnerability("nginx", "1.21.0", "CRITICAL", "1.25.0"));
        }
        return vulns;
    }

    public static String generateSecureDockerfile(String baseImage, boolean useDistroless) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Auto-generated secure Dockerfile\n");
        if (useDistroless) {
            sb.append("FROM ").append(baseImage).append(" AS build\n");
            sb.append("RUN ... build application ...\n");
            sb.append("FROM gcr.io/distroless/java21-debian12:nonroot\n");
            sb.append("COPY --from=build /app/app.jar /app/app.jar\n");
        } else {
            sb.append("FROM ").append(baseImage).append("\n");
        }
        sb.append("WORKDIR /app\n");
        sb.append("COPY app.jar .\n");
        sb.append("RUN chmod 644 /app/app.jar\n");
        sb.append("USER 10001:10001\n");
        sb.append("EXPOSE 8080\n");
        sb.append("ENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]\n");
        return sb.toString();
    }

    public static Map<String, Object> kubernetesRbacCheck(List<String> roleYamlLines) {
        Map<String, Object> results = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();
        boolean hasWildcard = false;
        boolean hasClusterAdmin = false;
        for (String line : roleYamlLines) {
            if (line.contains("*")) hasWildcard = true;
            if (line.contains("cluster-admin")) hasClusterAdmin = true;
        }
        results.put("hasWildcard", hasWildcard);
        results.put("hasClusterAdmin", hasClusterAdmin);
        if (hasWildcard) warnings.add("Wildcard '*' in RBAC rules — grant specific resources only");
        if (hasClusterAdmin) warnings.add("Cluster-admin binding detected — use least privilege");
        if (warnings.isEmpty()) warnings.add("RBAC rules appear minimal — review for least privilege");
        results.put("warnings", warnings);
        return results;
    }
}
