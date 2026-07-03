# Module 21: Logging & Monitoring - Mini Project

**Project Name**: Application Health Monitor & Custom Logger  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Configure an SLF4J + Logback environment with varying log levels and rolling file appenders, and create a custom JMX MBean to expose runtime metrics to an external monitoring tool (like JConsole).

## 📝 Requirements

### Core Features
1. **Logging Configuration (`logback.xml`)**:
   - Create a `src/main/resources/logback.xml` file.
   - Configure a **ConsoleAppender** that prints logs with a pattern including the time, thread name, log level, logger name, and message.
   - Configure a **RollingFileAppender** that writes to a file named `app.log`. Set it to roll over daily and keep a history of 7 days.
   - Set the Root logger level to `INFO`.
   - Set a specific logger (e.g., `com.example.monitor`) to `DEBUG` to allow more granular logging for your code.

2. **Application Logic**:
   - Create a class `WorkerService`.
   - Initialize an SLF4J `Logger`.
   - Write a loop that executes every 2 seconds, incrementing a task counter. Inside the loop, log a `DEBUG` message detailing the execution, and an `INFO` message every 5 executions.

3. **JMX Monitoring (MBean)**:
   - Create an interface `AppMonitorMBean` with methods `int getTaskCount()`, `void resetTaskCount()`, and `void setLogLevel(String level)`.
   - Implement the interface in `AppMonitor`. The `getTaskCount` should return the counter from the `WorkerService`.
   - The `setLogLevel` method should use the underlying Logback API to programmatically change the root log level at runtime without restarting the application.

4. **Execution**:
   - In `main`, register the `AppMonitor` with the `MBeanServer` using `ManagementFactory.getPlatformMBeanServer()`.
   - Start the `WorkerService` thread.
   - Open a terminal and run `jconsole`. Connect to your running Java process, navigate to the MBeans tab, and view/modify the task count and log levels in real-time.

---

## 💡 Solution Blueprint

1. **JMX Setup**:
   ```java
   public interface AppMonitorMBean {
       int getTaskCount();
       void resetTaskCount();
       void setLogLevel(String level);
   }

   public class AppMonitor implements AppMonitorMBean {
       private WorkerService worker;
       // ... constructor

       public int getTaskCount() { return worker.getCount(); }
       public void resetTaskCount() { worker.resetCount(); }
       
       public void setLogLevel(String level) {
           LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
           ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
           logger.setLevel(Level.toLevel(level));
       }
   }
   ```

2. **Main Registration**:
   ```java
   MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
   ObjectName name = new ObjectName("com.example:type=AppMonitor");
   mbs.registerMBean(new AppMonitor(workerService), name);
   ```