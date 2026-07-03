# Why OOP Basics Exist

## The Problem OOP Solves

Procedural programming organizes code as functions operating on data. As programs grow, data structures proliferate and functions become scattered. OOP bundles data and behavior together in cohesive units (classes), making large-scale software more manageable.

## Historical Context

Simula (1967) introduced classes and objects. Smalltalk (1972) refined the paradigm. C++ (1985) brought OOP to mainstream systems programming. By the early 1990s, OOP was seen as the solution to the "software crisis" — the inability to reliably build large, complex software systems.

Java was designed from the start as an object-oriented language. Key decisions:
- **Everything in a class**: No global functions — all code belongs to a class
- **Single inheritance**: Avoids C++'s complex multiple-inheritance rules
- **Interfaces**: Provides polymorphism without implementation inheritance
- **Garbage collection**: Eliminates manual memory management errors
- **Constructors**: Guarantee objects are properly initialized

The `static` keyword bridges the gap between pure OOP and practical needs (utility methods, constants, entry points). `this` provides explicit access to the current object, disambiguating fields from parameters.
