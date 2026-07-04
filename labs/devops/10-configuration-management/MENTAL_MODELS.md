# Mental Models for Configuration Management

## 1. Recipe Analogy
- **Playbook** = Recipe book
- **Play** = Recipe for a meal
- **Task** = Step in the recipe (chop onions, boil water)
- **Module** = Kitchen tool (knife, pot, stove)
- **Inventory** = List of ingredients
- **Server** = Dinner plate (you configure what goes on it)

## 2. Idempotency as Vending Machine
Pressing the button for a soda once gives you one soda. Pressing it 10 times gives you one soda (if you already took it). Idempotency = same action, same result, no matter how many times.

## 3. Snowflake vs Phoenix
- **Snowflake servers**: Each unique, fragile, can't reproduce.
- **Phoenix servers**: Regularly destroyed and recreated from code.
- Configuration management transforms snowflake servers into phoenix-ready servers.

## 4. Desired State as Photo
Configuration management is like showing a photo (desired state) to a sculptor and saying "make it look like this." The sculptor (agent) keeps working until the sculpture matches the photo exactly.
