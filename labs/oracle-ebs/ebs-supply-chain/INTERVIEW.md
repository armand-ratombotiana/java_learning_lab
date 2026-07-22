# Interview Questions: EBS Supply Chain

## Oracle-Specific Questions
- Explain the EBS Inventory (INV) architecture: organization structure, subinventories, locators, and item master.
- How does the Order Management (OM) process flow work from order entry, booking, pick release, ship confirm to invoicing?
- What is Advanced Pricing in EBS and how do you configure price lists, discounts, modifiers, and qualifiers?
- Explain Available-to-Promise (ATP) — how does it check inventory and supply availability in real-time?
- How does Purchasing (PO) process: requisition to PO generation, approval workflow, receiving, and invoicing?
- What are the key Supply Chain tables: MTL_SYSTEM_ITEMS_B, OE_ORDER_HEADERS_ALL, PO_HEADERS_ALL, MTL_ONHAND_QUANTITIES?
- How do you configure sourcing rules and allocation rules in EBS Supply Chain?
- Explain lot control, serial control, and LPN tracking in EBS Inventory.

## Google Cloud / Technical
- EBS Order Management integration with Google Cloud Tasks
- Cloud IoT for EBS inventory tracking with RFID
- Google Maps API for EBS supply chain logistics

## Microsoft / Azure
- Azure Logic Apps for EBS PO approval workflows
- Dynamics 365 Supply Chain vs EBS Supply Chain
- Power BI for EBS supply chain analytics

## Amazon / AWS
- EBS OM integration with Amazon MQ for order events
- AWS IoT Core for EBS warehouse sensors
- Amazon Forecast for EBS demand planning

## Apple
- Supply chain data security for Apple supplier compliance
- iOS warehouse scanning integration with EBS INV

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 262 | Trips and Users | Hard | JOIN + Filter |
| LC 585 | Investments in 2016 | Medium | Self JOIN |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |
| LC 618 | Students Report | Hard | PIVOT |

## Production Scenarios
- Scenario 1: "Production incident — ATP check returning incorrect availability causing overselling"
- Scenario 2: "Performance tuning — Order Import concurrent program processing very slowly"
- Scenario 3: "Disaster recovery — MTL_ONHAND_QUANTITIES corrupted after miscount adjustment"
- Scenario 4: "Security breach — Unauthorized PO approval via workflow bypass"

## Interview Patterns & Tips
- EBS Supply Chain interviews cover OM, INV, PO, and Pricing deeply
- ATP calculation and sourcing rules are frequent interview topics
- OCP EBS Supply Chain certification covers the full order-to-cash cycle
- Supply Chain consultants earn $120K-$175K; architects $150K-$210K
