# Common Mistakes with Modules

## Mistake 1: Split Packages
Two modules defining the same package causes startup failure. Ensure package names are unique across modules.

## Mistake 2: Missing requires transitive
If module A exports a type from module B, module A should `requires transitive B` so that consumers of A also get B.

## Mistake 3: Not Opening Packages for Reflection
Frameworks like Hibernate, Spring, Jackson use reflection to access private fields. You must `opens` packages to these frameworks.

## Mistake 4: Forgetting exports
Classes need to be both `public` and in an `exported` package to be accessible from other modules.

## Mistake 5: Classpath vs Module Path
Mixing module path and classpath can cause confusion. Automatic modules from classpath can access named modules' exported packages, but not vice versa.

## Mistake 6: Circular Dependencies
The module system does not allow circular dependencies between modules. This forces clean architecture.

## Mistake 7: Not Using jlink
jlink creates custom runtime images with only the modules you need. Without it, you're distributing a full JDK with your application.
