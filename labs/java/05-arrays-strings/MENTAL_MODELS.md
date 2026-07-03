# Arrays & Strings — Mental Models

## Model 1: The Apartment Building

An array is an apartment building. The building number is the array variable. Each apartment has an index (0, 1, 2...). You access an apartment by its index: `apartments[2]`. The building has a fixed number of floors (`length`). A 2D array is a block of apartment buildings — you need a row and column to find an apartment.

## Model 2: The String of Pearls

A String is a string of pearls — each pearl is a character. Once the string is assembled, you cannot remove or add pearls. You can only create a new string with a different arrangement. StringBuilder is a bead loom — you can add, remove, or change beads as you work, then produce a finished string when done.

## Model 3: The String Pool Pond

String literals are like labeled buoys floating in a pond. If two labels say "hello," they share the same buoy. If you create a `new String("hello")`, you've thrown a new buoy in the pond — it looks the same but is a different object.

## Model 4: The Chessboard

A 2D array is a chessboard. `board[row][col]` — first index selects the row, second selects the column. A jagged array is like a bookshelf where shelves can have different numbers of books. A 3D array is a Rubik's cube.
