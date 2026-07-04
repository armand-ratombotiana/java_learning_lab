# Visual Guide

## Spring Boot Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Your Application                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │Controller │  │  Service  │  │ Repository       │  │
│  └─────┬────┘  └─────┬────┘  └────────┬─────────┘  │
├────────┼──────────────┼────────────────┼────────────┤
│        │   Spring Boot Auto-Configuration           │
│  ┌─────┴────────────────────────────────┴─────────┐ │
│  │ WebMvcAutoConfig │ DataSourceAutoConfig │ ...  │ │
│  └──────────────────┴──────────────────────┘     │ │
├───────────────────────────────────────────────────┤
│               Spring Framework Core                │
│  ┌─────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │  DI/AOP  │  │   MVC    │  │     Data Access   │  │
│  └─────────┘  └──────────┘  └──────────────────┘  │
├───────────────────────────────────────────────────┤
│               Embedded Container (Tomcat)          │
└───────────────────────────────────────────────────┘
```

## Property Resolution Chain

```
CLI Args → OS Env → Profile YML → Default YML → @PropertySource → Defaults
  (highest)                                              (lowest priority)
```

## Startup Flow

```
main() → SpringApplication.run() → 
  Environment prepared → 
  ApplicationContext created → 
  Auto-configuration class loading → 
  @Conditional evaluation → 
  Bean creation → 
  Context refresh → 
  Runners executed → 
  Application started
```
