package com.oracleebs.architecture;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileSystemStructure {
    public enum FileSystemType { APPL_TOP, COMMON_TOP, INST_TOP, DB_HOME, ORACLE_HOME }

    private final Map<FileSystemType, Path> roots;
    private final Map<String, Path> symbolicLinks;
    private final List<String> adminScripts;
    private final Set<String> logDirectories;

    public FileSystemStructure() {
        this.roots = new EnumMap<>(FileSystemType.class);
        this.symbolicLinks = new LinkedHashMap<>();
        this.adminScripts = new ArrayList<>();
        this.logDirectories = new LinkedHashSet<>();
    }

    public FileSystemStructure setRoot(FileSystemType type, String path) {
        roots.put(type, Paths.get(path));
        return this;
    }

    public FileSystemStructure addAdminScript(String script) {
        adminScripts.add(script);
        return this;
    }

    public FileSystemStructure addLogDirectory(String dir) {
        logDirectories.add(dir);
        return this;
    }

    public FileSystemStructure addSymbolicLink(String name, String target) {
        symbolicLinks.put(name, Paths.get(target));
        return this;
    }

    public Optional<Path> getRoot(FileSystemType type) {
        return Optional.ofNullable(roots.get(type));
    }

    public List<String> getAdminScripts() {
        return Collections.unmodifiableList(adminScripts);
    }

    public Set<String> getLogDirectories() {
        return Collections.unmodifiableSet(logDirectories);
    }

    public Map<String, Path> getSymbolicLinks() {
        return Collections.unmodifiableMap(symbolicLinks);
    }

    public static FileSystemStructure createStandard() {
        return new FileSystemStructure()
            .setRoot(FileSystemType.APPL_TOP, "/u01/inst/apps/ERSDEV/apps/apps_st/appl")
            .setRoot(FileSystemType.COMMON_TOP, "/u01/inst/apps/ERSDEV/apps/apps_st/comn")
            .setRoot(FileSystemType.INST_TOP, "/u01/inst/apps/ERSDEV/apps/apps_st/inst")
            .setRoot(FileSystemType.ORACLE_HOME, "/u01/inst/apps/ERSDEV/tech_st/10.1.2")
            .setRoot(FileSystemType.DB_HOME, "/u01/inst/apps/ERSDEV/db/tech_st/19.0.0")
            .addAdminScript("adadmin")
            .addAdminScript("adpatch")
            .addAdminScript("adop")
            .addAdminScript("adstrtal.sh")
            .addAdminScript("adstpall.sh")
            .addLogDirectory("/u01/inst/apps/ERSDEV/apps/apps_st/appl/admin/log")
            .addLogDirectory("/u01/inst/apps/ERSDEV/apps/apps_st/appl/conc/log")
            .addLogDirectory("/u01/inst/apps/ERSDEV/apps/apps_st/appl/out")
            .addSymbolicLink("APPL_TOP", "/u01/inst/apps/ERSDEV/apps/apps_st/appl")
            .addSymbolicLink("LOG_HOME", "/u01/inst/apps/ERSDEV/apps/apps_st/appl/admin/log");
    }

    public Map<FileSystemType, Path> getAllRoots() {
        return Collections.unmodifiableMap(roots);
    }
}
