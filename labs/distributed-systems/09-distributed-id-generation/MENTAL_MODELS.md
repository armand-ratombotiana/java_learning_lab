# Mental Models for ID Generation

## The License Plate Model
- UUID: Random characters, universally unique
- Snowflake: Encodes time + location + sequence in plate number
- Database sequence: Sequential from DMV office

## The Factory Order Model
- Each machine stamps unique serial numbers
- Machine ID prefix + counter = unique ID
- No need to talk to other machines

## The Barcode Model
- UUID: Full 128-bit unique identifier
- Snowflake: 64-bit like a compact barcode
- Timestamp component for sorting
