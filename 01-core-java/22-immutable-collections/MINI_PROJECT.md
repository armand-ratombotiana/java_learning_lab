# Mini Project: Secure Configuration Manager

## Objective
Build a `ConfigurationManager` class that holds application settings. The manager must be completely immutable to ensure thread safety in a highly concurrent environment. It will demonstrate defensive copying, `Map.copyOf()`, and the difference between structural and element immutability.

## Prerequisites
*   Java 17+

## Step 1: Define a Mutable Configuration Element
Create a class that represents a configuration setting. Deliberately make it mutable to demonstrate the dangers of element immutability.

```java
public class Setting {
    private String value;

    public Setting(String value) { this.value = value; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; } // MUTABLE!

    @Override
    public String toString() { return value; }
}
```

## Step 2: Build the Configuration Manager (The Wrong Way)
First, build it using the pre-Java 9 approach to see why it fails.

```java
import java.util.Collections;
import java.util.Map;

public class BadConfigurationManager {
    private final Map<String, Setting> settings;

    public BadConfigurationManager(Map<String, Setting> settings) {
        // BAD: Just wrapping the reference. The caller still holds the original map!
        this.settings = Collections.unmodifiableMap(settings);
    }

    public Map<String, Setting> getSettings() {
        return settings;
    }
}
```

## Step 3: Build the Configuration Manager (The Right Way)
Now, build the secure version using Java 10+ `Map.copyOf()` and defensive copying of the elements themselves.

```java
import java.util.Map;
import java.util.stream.Collectors;

public final class SecureConfigurationManager {
    private final Map<String, Setting> settings;

    public SecureConfigurationManager(Map<String, Setting> originalSettings) {
        // 1. We must deep copy the elements because 'Setting' is a mutable class.
        // 2. We use Collectors.toUnmodifiableMap() to ensure the resulting map is structurally immutable.
        this.settings = originalSettings.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> new Setting(entry.getValue().getValue()) // Deep copy of the value
            ));
    }

    public Map<String, Setting> getSettings() {
        // We can safely return the map because it's structurally immutable.
        // HOWEVER, to prevent the caller from modifying the returned Setting objects,
        // we should ideally return a deep copy again, OR better yet, make 'Setting' an immutable record.
        // For this exercise, we will return a deep copy to demonstrate extreme safety.
        
        return settings.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> new Setting(entry.getValue().getValue())
            ));
    }
    
    // A safe accessor for a single value
    public String getSettingValue(String key) {
        Setting s = settings.get(key);
        return s != null ? s.getValue() : null; // Returning a String (immutable) is completely safe
    }
}
```

## Step 4: Test the Security
Write a test to prove that `BadConfigurationManager` leaks state, while `SecureConfigurationManager` does not.

```java
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Setting> initialSettings = new HashMap<>();
        initialSettings.put("timeout", new Setting("30s"));
        initialSettings.put("retries", new Setting("3"));

        System.out.println("--- Testing Bad Manager ---");
        BadConfigurationManager badMgr = new BadConfigurationManager(initialSettings);
        
        // Attack 1: Modify the original map
        initialSettings.put("hacked", new Setting("true"));
        System.out.println("BadMgr sees hacked key: " + badMgr.getSettings().containsKey("hacked")); // TRUE!
        
        // Attack 2: Modify the element inside the map
        badMgr.getSettings().get("timeout").setValue("0s");
        System.out.println("BadMgr timeout: " + badMgr.getSettings().get("timeout")); // 0s!

        System.out.println("\n--- Testing Secure Manager ---");
        // Reset data
        initialSettings.clear();
        initialSettings.put("timeout", new Setting("30s"));
        
        SecureConfigurationManager secureMgr = new SecureConfigurationManager(initialSettings);
        
        // Attack 1: Modify original map
        initialSettings.put("hacked", new Setting("true"));
        System.out.println("SecureMgr sees hacked key: " + (secureMgr.getSettingValue("hacked") != null)); // FALSE!
        
        // Attack 2: Modify the element returned by getter
        Map<String, Setting> retrievedSettings = secureMgr.getSettings();
        retrievedSettings.get("timeout").setValue("0s");
        
        System.out.println("SecureMgr internal timeout: " + secureMgr.getSettingValue("timeout")); // Still 30s!
    }
}
```

## Expected Output
```text
--- Testing Bad Manager ---
BadMgr sees hacked key: true
BadMgr timeout: 0s

--- Testing Secure Manager ---
SecureMgr sees hacked key: false
SecureMgr internal timeout: 30s
```