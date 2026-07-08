package com.devops.fourteen;

import java.util.ArrayList;
import java.util.List;

public class HelmHookManager {
    public enum HookType { PRE_INSTALL, POST_INSTALL, PRE_UPGRADE, POST_UPGRADE, PRE_DELETE, POST_DELETE, TEST }

    private final List<HelmHook> hooks = new ArrayList<>();

    public record HelmHook(String name, HookType type, String apiVersion, String kind, String image, String command) {}

    public void addHook(String name, HookType type, String apiVersion, String kind, String image, String command) {
        hooks.add(new HelmHook(name, type, apiVersion, kind, image, command));
    }

    public String renderPreInstallJob() {
        StringBuilder sb = new StringBuilder();
        for (HelmHook hook : hooks) {
            if (hook.type() == HookType.PRE_INSTALL || hook.type() == HookType.POST_INSTALL) {
                sb.append("apiVersion: ").append(hook.apiVersion()).append("\n");
                sb.append("kind: ").append(hook.kind()).append("\n");
                sb.append("metadata:\n");
                sb.append("  name: ").append(hook.name()).append("\n");
                sb.append("  annotations:\n");
                sb.append("    \"helm.sh/hook\": ").append(hook.type().name().toLowerCase().replace("_", "-")).append("\n");
                sb.append("    \"helm.sh/hook-weight\": \"0\"\n");
                sb.append("    \"helm.sh/hook-delete-policy\": hook-succeeded\n");
                sb.append("spec:\n");
                sb.append("  template:\n");
                sb.append("    spec:\n");
                sb.append("      containers:\n");
                sb.append("        - name: ").append(hook.name()).append("\n");
                sb.append("          image: ").append(hook.image()).append("\n");
                sb.append("          command: [\"/bin/sh\", \"-c\"]\n");
                sb.append("          args: [\"").append(hook.command()).append("\"]\n");
                sb.append("      restartPolicy: Never\n");
                sb.append("---\n");
            }
        }
        return sb.toString();
    }

    public void executePreInstallHooks() {
        for (HelmHook hook : hooks) {
            if (hook.type() == HookType.PRE_INSTALL) {
                System.out.printf("Executing pre-install hook: %s%n", hook.name());
                System.out.printf("  Running: %s%n", hook.command());
            }
        }
    }

    public void executePostInstallHooks() {
        for (HelmHook hook : hooks) {
            if (hook.type() == HookType.POST_INSTALL) {
                System.out.printf("Executing post-install hook: %s%n", hook.name());
                System.out.printf("  Running: %s%n", hook.command());
            }
        }
    }

    public static void main(String[] args) {
        HelmHookManager manager = new HelmHookManager();
        manager.addHook("db-migration", HookType.PRE_INSTALL, "batch/v1", "Job", "myapp-migrations:1.0", "flyway migrate");
        manager.addHook("post-deploy-check", HookType.POST_INSTALL, "batch/v1", "Job", "curlimages/curl", "curl -f http://myapp:8080/health");
        System.out.println("=== Hooks YAML ===");
        System.out.println(manager.renderPreInstallJob());
        manager.executePreInstallHooks();
        manager.executePostInstallHooks();
    }
}
