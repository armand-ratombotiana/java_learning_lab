# Abstraction & Interfaces — Mental Models

## Model 1: The Restaurant Menu

An interface is a menu. It lists dishes available (method signatures) but doesn't reveal recipes. The kitchen (implementation) decides exactly how to prepare each dish. You can swap chefs (implementations) and your dining experience (code that uses the interface) remains the same as long as the menu is unchanged.

## Model 2: The Electrical Outlet

An interface is a power outlet (standardized contract). Any device with the right plug can connect: a toaster, a lamp, a phone charger. The outlet doesn't care what's connected — it just provides power. New devices can be added without changing the outlet. Default methods are like outlets with USB ports — they provide additional convenience without breaking older devices.

## Model 3: The Building Blueprint

Abstract classes are like a building foundation and framework (partial blueprint). You can build different buildings on the same foundation (apartment, office, school). Interfaces are like certifications — "LEED Certified" guarantees certain capabilities but doesn't dictate structure.

## Model 4: The USB Standard

USB Type-C (the interface) defines the connector shape and protocol. USB-C cables implement this interface. Multiple devices (laptops, phones, monitors) use the same interface. The interface evolves: USB 3.0 added features but USB 2.0 devices still work (backward compatibility via default methods).
