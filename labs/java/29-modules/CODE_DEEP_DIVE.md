# Code Deep Dive: Modules

## Application Module Structure
```
myapp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   ├── com/myapp/
│   │   │   │   ├── Main.java
│   │   │   │   ├── api/
│   │   │   │   │   └── UserService.java
│   │   │   │   ├── internal/
│   │   │   │   │   └── UserRepository.java
│   │   │   │   └── model/
│   │   │   │       └── User.java
```

## module-info.java
```java
module com.example.myapp {
    requires java.sql;
    requires com.example.database;
    requires transitive com.example.common;
    
    exports com.example.myapp.api;
    opens com.example.myapp.model to com.example.orm;
    
    uses com.example.plugin.Plugin;
}
```

## Service Provider Interface
```java
// module-info.java for plugin module:
module com.example.plugin {
    exports com.example.plugin;
    provides com.example.plugin.Plugin 
        with com.example.plugin.impl.PluginImpl;
}
```

## jlink Custom Runtime
```bash
jlink --module-path $JAVA_HOME/jmods:mods \
      --add-modules com.example.myapp \
      --output myapp-runtime \
      --launcher myapp=com.example.myapp
```

## Migration Example (java.xml.bind)
```java
// Before Java 9 (classpath):
import javax.xml.bind.JAXBContext;

// After Java 9 (module or --add-modules):
// --add-modules java.xml.bind (deprecated in Java 9, removed in Java 11)
// Use Jakarta XML Binding for Java 11+
```
