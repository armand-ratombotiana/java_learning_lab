# Visual Guide: EBS HRMS

## 1. System Context

User Interface -> App Tier -> Database Tier

## 2. Flow Diagram

Start -> Validation -> Processing -> PostProcess -> Audit -> Complete

## 3. Component Diagram

EBS Application: Forms Server | OAF Server | Concurrent Manager | Oracle Database 19c

## 4. Sequence Diagram

User to App: Submit Request
App to DB: Validate
DB to App: Valid
App to App: Process
App to DB: Insert
DB to App: Success
App to User: Confirmation

## 5. State Diagram

Pending -> Running -> Completed
Running -> Warning
Running -> Error

## 6. Architecture Layers

Presentation: Forms, OAF, BI Publisher
Business: PL/SQL, Java Middleware
Data: Tables, Views, Materialized Views
Integration: Workflow, XML Gateway, SOA
