# Exercises - Flyway

## Exercise 1: Create a New Migration
Add a new migration file `V6__add_product_ratings.sql` that adds a ratings column to the products table.

## Exercise 2: Configure Multiple Locations
Modify the Flyway configuration to include migrations from multiple locations:
- `classpath:db/migration`
- `filesystem:/opt/migrations`

## Exercise 3: Implement Undo Migration
Create an undo migration to reverse the V5 seed data migration.

## Exercise 4: Add Baseline
Configure Flyway to baseline an existing database at version 3.

## Exercise 5: Integrate with Spring
Add Flyway dependency to a Spring Boot project and configure via application.properties.