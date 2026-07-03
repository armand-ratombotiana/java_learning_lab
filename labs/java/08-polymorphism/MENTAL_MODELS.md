# Polymorphism — Mental Models

## Model 1: The Universal Remote

A universal remote has a "play" button. Pressing play on a DVD player starts the movie. On a streaming app, it starts the show. On a game console, it starts the game. The same button call produces different behavior depending on what device it controls. The remote doesn't know the details — it just sends "play."

## Model 2: The Shape Sorter Toy

A child has blocks of different shapes (Circle, Square, Triangle). The sorter has a hole called "Shape." Each block responds to `fitInto()` differently: the circle rolls, the square slides. At runtime, the actual block type determines what happens.

## Model 3: The Orchestra Conductor

The conductor raises the baton — all musicians `play()`. The violinist plays strings, the drummer hits drums, the flutist blows. The conductor doesn't know the details of each instrument. The `Musician` interface guarantees every musician has a `play()` method.

## Model 4: The Vending Machine Slot

Overloading is like a vending machine with slots for different currencies. Put in a coin (int) → coin slot. Put in a bill (double) → bill slot. Put in a card (String) → card slot. Same machine, different payment methods. The machine decides which slot at compile time based on what you put in.
