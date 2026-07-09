# Mental Models — Spring Boot Internals

## Auto-Configuration: Smart Factory

Think of `AutoConfigurationImportSelector` as a "smart factory foreman":
- Knows all available machines (`AutoConfiguration.imports`)
- Checks each machine against a checklist of conditions (`@Conditional*`)
- Skips machines that don't meet conditions
- Produces only the right subset of beans

## Conditional Annotations: Guard Gates

Each `@Conditional*` annotation is a security gate:
- `@ConditionalOnClass` → "Is the part available?"
- `@ConditionalOnBean` → "Is the prerequisite installed?"
- `@ConditionalOnProperty` → "Is the configuration switch on?"

## BeanFactoryPostProcessor: Blueprint Editor

Like editing building blueprints before construction starts:
- You can change room sizes (bean scopes)
- Add new rooms (bean definitions)
- Remove rooms (remove beans)
- Structural changes before any real objects exist

## DispatcherServlet: Air Traffic Control

The `DispatcherServlet` is an air traffic controller:
- `HandlerMapping` → which runway? (which controller handles request)
- `HandlerAdapter` → how to communicate (how to invoke handler)
- `HandlerInterceptor` → weather check (authentication, logging)
- `ContentNegotiation` → which language? (JSON, XML)
- `MessageConverter` → translate to/from (serialization)

## Actuator: Dashboard Panel

Actuator is the airplane cockpit panel:
- `HealthIndicator` → engine status
- `Metrics` → fuel gauge
- `@Endpoint` → instrument panel switch
- `Info` → plane manifest

## EnvironmentPostProcessor: Custom Control Surface

Like adding a custom control before the cockpit is built:
- You can inject additional instruments (property sources)
- Override altitude settings (configure profiles)
- Before the plane is fully assembled

## ApplicationContextInitializer: Site Preparer

Before the building foundation:
- Prepares the ground (sets context's environment)
- Configures the plot (adds initializers)
- Before any construction (bean creation) begins