# Step-by-Step: Creating Modules

## Step 1: Project Structure
```
myapp/
├── src/
│   ├── com.example.myapp/
│   │   ├── module-info.java
│   │   └── com/example/myapp/Main.java
│   └── com.example.database/
│       ├── module-info.java
│       └── com/example/database/Database.java
└── mods/
```

## Step 2: module-info.java (Main Application)
```java
module com.example.myapp {
    requires com.example.database;
    exports com.example.myapp.api;
}
```

## Step 3: module-info.java (Database Library)
```java
module com.example.database {
    requires java.sql;
    exports com.example.database;
}
```

## Step 4: Compile Database Module
```bash
javac -d mods/com.example.database \
    src/com.example.database/module-info.java \
    src/com.example.database/com/example/database/Database.java
```

## Step 5: Compile Main Application
```bash
javac --module-path mods \
    -d mods/com.example.myapp \
    src/com.example.myapp/module-info.java \
    src/com.example.myapp/com/example/myapp/Main.java
```

## Step 6: Package into Modular JARs
```bash
jar --create --file mods/com.example.database.jar \
    -C mods/com.example.database .

jar --create --file mods/com.example.myapp.jar \
    --main-class com.example.myapp.Main \
    -C mods/com.example.myapp .
```

## Step 7: Run
```bash
java --module-path mods --module com.example.myapp
```

## Step 8: Create Custom Runtime
```bash
jlink --module-path $JAVA_HOME/jmods:mods \
    --add-modules com.example.myapp \
    --output myapp-runtime \
    --launcher myapp=com.example.myapp
```

## Step 9: Test the Runtime
```bash
./myapp-runtime/bin/myapp
```
