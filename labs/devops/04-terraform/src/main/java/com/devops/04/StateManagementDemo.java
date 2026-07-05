package com.devops.terraform;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StateManagementDemo {
    private final File stateFile;
    private final Map<String, String> state = new ConcurrentHashMap<>();

    public StateManagementDemo(String stateFilePath) {
        this.stateFile = new File(stateFilePath);
        load();
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(stateFile))) {
            for (Map.Entry<String, String> e : state.entrySet()) {
                pw.println(e.getKey() + "=" + e.getValue());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
        if (!stateFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(stateFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    state.put(parts[0], parts[1]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void update(String key, String value) {
        state.put(key, value);
        save();
    }

    public static void main(String[] args) {
        StateManagementDemo sm = new StateManagementDemo("terraform.tfstate");
        sm.update("vpc_id", "vpc-12345");
        sm.update("subnet_id", "subnet-67890");
        System.out.println("State saved to file. " + sm.state.size() + " entries.");
    }
}
