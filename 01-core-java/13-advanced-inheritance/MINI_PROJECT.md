# Module 13: Advanced Inheritance Patterns - Mini Project

**Project Name**: Extensible Plugin Architecture  
**Difficulty Level**: Advanced  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Design a core system that can be extended via plugins, demonstrating the use of `sealed` classes, default interface methods, and composition over inheritance.

## 📝 Requirements

### Core Features
1. **Plugin Interface**:
   - Create an interface `Plugin`.
   - Provide a `default` method `void initialize()` that prints a setup message.
   - Provide an abstract method `String execute(String input)`.

2. **Sealed Class Hierarchy**:
   - Create a `sealed class BasePlugin implements Plugin permits TextPlugin, ImagePlugin`.
   - Implement common logic in `BasePlugin` if necessary.
   - Create `final class TextPlugin extends BasePlugin` which reverses the input text.
   - Create `final class ImagePlugin extends BasePlugin` which appends "[Processed Image]" to the input.

3. **Composition over Inheritance**:
   - Create a `PluginManager` class.
   - Instead of extending `PluginManager`, compose it with a `List<Plugin>`.
   - Provide a method `void loadPlugin(Plugin p)` and `void runAll(String input)`.

---

## 💡 Solution Blueprint
1. Define the `Plugin` interface with its `default` method.
2. Define the `sealed class BasePlugin` and its permitted subclasses.
3. In `PluginManager`, iterate over loaded plugins, calling `initialize()` and then `execute()`.
4. In `main()`, instantiate the manager, load `TextPlugin` and `ImagePlugin`, and run them.