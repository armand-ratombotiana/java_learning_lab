# Exceptions — Mental Models

## Model 1: The Safety Net

Code execution is a tightrope walk. An exception is a fall. The `try` block is the tightrope. The `catch` block is the safety net — it catches the fall and handles it. The `finally` block is the cleanup crew that works whether you fell or not. Checked exceptions are like a harness with a mandatory clip-in — you cannot proceed without securing it.

## Model 2: The Fire Alarm System

An exception is a fire alarm. Something went wrong (fire), so an alert propagates through the building (call stack). Each floor (method) can either handle the fire (catch) or let the alarm continue up. If no one handles it, the fire department (JVM) shows up and shuts everything down.

## Model 3: The Shipping Company

A package delivery system: a checked exception is like getting a signature required sticker. You must either be there to sign (catch) or redirect to a neighbor (throws). An unchecked exception is like a fragile sticker — you should handle it carefully, but no one forces you to. If it breaks, it's your problem.

## Model 4: The Vaccine Passport

Try-with-resources is like having a vaccine passport that automatically checks your vaccination status when entering and leaving a country. You don't need to manually pull out documents — the system handles entry and exit. If there's a problem at entry or exit, both issues are recorded (suppressed exceptions).
