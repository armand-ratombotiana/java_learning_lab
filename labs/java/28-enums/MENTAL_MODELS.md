# Mental Models for Enums

## The Menu Model
An enum is like a restaurant menu with a fixed set of items. You can only order from the menu — no custom dishes. Each item has:
- A name (enum constant name)
- A description (custom fields)
- A price (custom fields)
- Preparation instructions (methods)

## The Card Deck Model
Think of a card deck. The suits form an enum (HEARTS, DIAMONDS, CLUBS, SPADES). Each suit has:
- A name ("Hearts")
- A symbol ("♥")
- A color (red/black)
You can't create new suits — the deck is complete.

## The EnumSet as Checklist
EnumSet is like a checklist of enum items. You check off which items apply, using a compact bit representation. For example, restaurant order modifiers: LETTUCE, TOMATO, ONION, CHEESE — each order has a subset.

## The EnumMap as Categorizer
EnumMap is like a filing cabinet with one drawer per enum constant. Each drawer stores the data for that category.
