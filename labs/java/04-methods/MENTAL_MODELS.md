# Methods — Mental Models

## Model 1: The Factory Machine

A method is a machine in a factory. It takes raw materials (parameters), performs operations, and outputs a product (return value). The machine's blueprint (method declaration) describes what it does. Overloaded methods are different machines that share the same name but process different materials.

## Model 2: The Chef's Recipe

A method is a recipe: "chop(int quantity of vegetables), then stir(double seconds), then serve()." Each recipe step is a method call. The chef doesn't need to know how chopping works internally — just what it produces.

## Model 3: The Stack of Papers

Method calls form a stack. Each call adds a paper to the top of the stack containing the method's local variables. When the method returns, the paper is removed. Recursion keeps adding papers — if you add too many, the stack overflows (StackOverflowError).

## Model 4: The Telephone Operator

Calling a method is like dialing a phone number. The method name is the contact, parameters are the message, and the return value is the response. Varargs is like "call any number of people." Overloading is like having different numbers for voice, text, and video on the same contact.
