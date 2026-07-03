# Mental Models for Modules

## The Building Model
A module is like a room in a building:
- The room has walls (module boundary)
- Doors (exported packages) allow access from other rooms
- Windows (open packages) allow viewing/reflection
- You need a key (requires) to enter
- Service providers are like vending machines — anyone can use them
- The building plan (module graph) shows all rooms and connections

## The Library Model
A module is like a library:
- The catalog (module-info.java) lists available books (packages)
- Some books are in the general collection (exported)
- Some are in the reference section (internally used only)
- You need a library card (requires) to borrow books
- Inter-library loans (requires transitive) pass through

## The Package Model
Think of a module as a sealed jar:
- The jar label (module-info.java) declares what's inside
- Some packages are marked "PUBLIC" (exports)
- Some are marked "INTERNAL" (not exported)
- You can only use classes from PUBLIC packages
- The jar lists its dependencies (requires) for reliable configuration
