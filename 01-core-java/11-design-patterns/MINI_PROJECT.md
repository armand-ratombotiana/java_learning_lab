# Module 11: Design Patterns - Mini Project

**Project Name**: Smart Home Automation System  
**Difficulty Level**: Advanced  
**Estimated Time**: 4-5 hours

---

## 🎯 Objective
Design a modular, scalable architecture by applying at least four distinct GoF (Gang of Four) Design Patterns (Creational, Structural, and Behavioral) in a unified project.

## 📝 Requirements

### Core Features

1. **Singleton Pattern (Creational)**:
   - Create a `SmartHomeHub` class. This is the central controller of the house.
   - There must only ever be ONE instance of the Hub.
   - It should maintain a registry of connected devices.

2. **Factory Method Pattern (Creational)**:
   - Create an interface `SmartDevice` with methods `turnOn()` and `turnOff()`.
   - Create implementations: `SmartLight`, `SmartThermostat`.
   - Create a `DeviceFactory` that takes a string (e.g., `"LIGHT"`) and returns the appropriate instantiated device.

3. **Observer Pattern (Behavioral)**:
   - Make the `SmartHomeHub` act as the Subject/Publisher.
   - Whenever the Hub receives a "Security Alert" event, it must notify all connected devices.
   - Update the `SmartDevice` interface so they act as Observers. (e.g., Lights flash red, Thermostat shuts down).

4. **Command Pattern (Behavioral)**:
   - Create an interface `Command` with an `execute()` method.
   - Create concrete commands: `TurnOnLightCommand`, `SetTemperatureCommand`.
   - Create a `RemoteControl` class (Invoker) with programmable slots. You can assign commands to buttons and press them.

5. **Adapter Pattern (Structural) - Bonus**:
   - Introduce a legacy device: `OldTelevision` which only has methods `power(boolean state)`.
   - Create a `TVAdapter` that implements `SmartDevice` but wraps `OldTelevision`, translating `turnOn()` to `power(true)`.

---

## 💡 Solution Blueprint

1. **Singleton Hub**:
   ```java
   public class SmartHomeHub {
       private static final SmartHomeHub INSTANCE = new SmartHomeHub();
       private List<SmartDevice> devices = new ArrayList<>();
       private SmartHomeHub() {}
       public static SmartHomeHub getInstance() { return INSTANCE; }
       
       public void triggerSecurityAlert() {
           devices.forEach(SmartDevice::onSecurityAlert);
       }
   }
   ```

2. **Factory & Devices**:
   ```java
   public interface SmartDevice {
       void turnOn();
       void turnOff();
       void onSecurityAlert();
   }
   
   public class DeviceFactory {
       public static SmartDevice createDevice(String type) {
           if (type.equals("LIGHT")) return new SmartLight();
           // ...
       }
   }
   ```

3. **Command & Remote**:
   ```java
   public class TurnOnLightCommand implements Command {
       private SmartLight light;
       public void execute() { light.turnOn(); }
   }
   ```