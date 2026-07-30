# Problem Walkthrough: Inventory Optimization with Min-Max Planning and Reorder Point

## Problem Statement

**Design and implement an inventory optimization system using min-max planning and reorder point methodology for a global distribution company managing 100,000 SKUs across 12 warehouses with $500M annual inventory value.**

The client faces chronic stockouts of high-value A-items (15% stockout rate for revenue-critical products) while carrying 45 days of excess inventory on C-items. The current MRP-based planning system treats all items equally, ignoring demand variability, lead time volatility, and service level targets. The supply chain director wants a demand-driven inventory model that reduces overall inventory by 20% while improving fill rates to 98%.

### Business Requirements
- Reduce total inventory value by 20% ($100M savings)
- Increase fill rate from 85% to 98% for A-class items
- Maintain 95% fill rate for B-class, 90% for C-class
- Automate replenishment calculations per SKU per warehouse
- Account for demand seasonality and lead time variability
- Support both min-max planning (for normal items) and reorder point (ROP) with safety stock (for critical items)
- Provide real-time visibility into inventory health scores per SKU

### Technical Constraints
- Oracle EBS R12.2 Inventory (INV) and Oracle Purchasing (PO)
- 100,000 SKUs across 12 warehouses (3 DCs, 9 regional)
- 5,000 active suppliers with varying lead times (2-90 days)
- 15% of SKUs are seasonal (holiday peaks, agricultural harvests)
- API integration with 3PL warehouses for real-time inventory feeds
- Data warehouse for historical demand (3 years daily)

---

## Solution Architecture

### Step 1: ABC-FSN Classification

Classify all 100,000 SKUs along two dimensions: (1) Annual Usage Value (ABC) and (2) Movement Velocity (FSN — Fast, Slow, Non-moving).

```sql
CREATE OR REPLACE PACKAGE xx_inv_classification_pkg AS

  TYPE abc_record IS RECORD (
    inventory_item_id   NUMBER,
    organization_id     NUMBER,
    segment1            VARCHAR2(50),
    description         VARCHAR2(250),
    annual_usage_value  NUMBER,
    abc_class           VARCHAR2(1),
    movement_frequency  NUMBER,
    fsn_class           VARCHAR2(1)
  );

  TYPE abc_table IS TABLE OF abc_record;

  PROCEDURE classify_all_items(
    p_organization_id NUMBER,
    p_calc_period_days NUMBER DEFAULT 365
  );

  FUNCTION get_usage_statistics(
    p_organization_id NUMBER,
    p_period_days     NUMBER DEFAULT 365
  ) RETURN abc_table PIPELINED;

END xx_inv_classification_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_inv_classification_pkg AS

  FUNCTION get_usage_statistics(
    p_organization_id NUMBER,
    p_period_days     NUMBER DEFAULT 365
  ) RETURN abc_table PIPELINED IS

    CURSOR item_usage_cur IS
      SELECT msib.inventory_item_id,
             msib.organization_id,
             msib.segment1,
             msib.description,
             SUM(NVL(mtln.transaction_quantity, 0)
                 * NVL(mtln.transaction_cost, 0)) AS annual_usage_value,
             COUNT(DISTINCT TRUNC(mtln.transaction_date)) AS active_days
      FROM   mtl_system_items_b msib
      LEFT JOIN mtl_transaction_lt_account mtln
        ON   msib.inventory_item_id = mtln.inventory_item_id
        AND  msib.organization_id = mtln.organization_id
        AND  mtln.transaction_date > SYSDATE - p_period_days
        AND  mtln.transaction_type_id IN (1, 2, 3, 33)  -- Issue types only
      WHERE  msib.organization_id = p_organization_id
      AND    msib.inventory_item_flag = 'Y'
      AND    msib.planning_active_flag = 'Y'
      GROUP  BY msib.inventory_item_id, msib.organization_id,
                msib.segment1, msib.description
      ORDER  BY annual_usage_value DESC;

    l_total_value   NUMBER := 0;
    l_cum_value     NUMBER := 0;
    l_row_count     NUMBER := 0;
    l_rec           abc_record;
    l_active_days_pct NUMBER;

  BEGIN
    -- First pass: calculate total value
    SELECT NVL(SUM(annual_usage_value), 0)
    INTO   l_total_value
    FROM   (
      SELECT SUM(NVL(mtln.transaction_quantity, 0)
                 * NVL(mtln.transaction_cost, 0)) AS annual_usage_value
      FROM   mtl_system_items_b msib
      LEFT JOIN mtl_transaction_lt_account mtln
        ON   msib.inventory_item_id = mtln.inventory_item_id
        AND  msib.organization_id = mtln.organization_id
        AND  mtln.transaction_date > SYSDATE - p_period_days
        AND  mtln.transaction_type_id IN (1, 2, 3, 33)
      WHERE  msib.organization_id = p_organization_id
      AND    msib.inventory_item_flag = 'Y'
      AND    msib.planning_active_flag = 'Y'
      GROUP  BY msib.inventory_item_id, msib.organization_id
    );

    -- Second pass: classify
    FOR rec IN item_usage_cur LOOP
      l_cum_value := l_cum_value + NVL(rec.annual_usage_value, 0);
      l_row_count := l_row_count + 1;

      l_rec.inventory_item_id := rec.inventory_item_id;
      l_rec.organization_id := rec.organization_id;
      l_rec.segment1 := rec.segment1;
      l_rec.description := rec.description;
      l_rec.annual_usage_value := rec.annual_usage_value;
      l_rec.movement_frequency := rec.active_days;

      -- ABC classification by cumulative value
      IF l_total_value > 0 THEN
        IF l_cum_value / l_total_value <= 0.70 THEN
          l_rec.abc_class := 'A';
        ELSIF l_cum_value / l_total_value <= 0.90 THEN
          l_rec.abc_class := 'B';
        ELSE
          l_rec.abc_class := 'C';
        END IF;
      ELSE
        l_rec.abc_class := 'C';
      END IF;

      -- FSN classification by movement frequency
      l_active_days_pct := rec.active_days / p_period_days;
      IF l_active_days_pct >= 0.75 THEN
        l_rec.fsn_class := 'F';  -- Fast moving
      ELSIF l_active_days_pct >= 0.25 THEN
        l_rec.fsn_class := 'S';  -- Slow moving
      ELSE
        l_rec.fsn_class := 'N';  -- Non-moving
      END IF;

      -- Store classification
      UPDATE mtl_system_items_b
      SET attribute_category = 'INV_CLASSIFICATION',
          attribute1 = l_rec.abc_class,   -- ABC class
          attribute2 = l_rec.fsn_class,   -- FSN class
          attribute3 = TO_CHAR(rec.annual_usage_value, '99999999999.99'), -- Usage value
          attribute4 = TO_CHAR(SYSDATE, 'YYYY-MM-DD')  -- Classification date
      WHERE inventory_item_id = rec.inventory_item_id
      AND organization_id = rec.organization_id;

      PIPE ROW(l_rec);
    END LOOP;

    COMMIT;
    RETURN;
  END get_usage_statistics;

  PROCEDURE classify_all_items(
    p_organization_id NUMBER,
    p_calc_period_days NUMBER DEFAULT 365
  ) IS
  BEGIN
    FOR rec IN (
      SELECT * FROM TABLE(
        xx_inv_classification_pkg.get_usage_statistics(
          p_organization_id, p_calc_period_days
        )
      )
    ) LOOP
      NULL;  -- Processing happens in the pipelined function
    END LOOP;
  END classify_all_items;

END xx_inv_classification_pkg;
/
```

### Step 2: Calculate Demand Statistics

For each SKU-warehouse combination, calculate demand parameters:

```sql
CREATE OR REPLACE PACKAGE xx_demand_calc_pkg AS

  PROCEDURE calculate_demand_parameters(
    p_organization_id NUMBER,
    p_item_id         NUMBER,
    p_lookback_months NUMBER DEFAULT 12
  );

  PROCEDURE calculate_all_items(
    p_organization_id NUMBER
  );

END xx_demand_calc_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_demand_calc_pkg AS

  PROCEDURE calculate_demand_parameters(
    p_organization_id NUMBER,
    p_item_id         NUMBER,
    p_lookback_months NUMBER DEFAULT 12
  ) IS

    TYPE monthly_demand_t IS TABLE OF NUMBER INDEX BY VARCHAR2(7);
    l_monthly_demand monthly_demand_t;
    l_month_key      VARCHAR2(7);

    l_total_demand     NUMBER := 0;
    l_month_count      NUMBER := 0;
    l_avg_monthly_demand NUMBER := 0;
    l_demand_variance  NUMBER := 0;
    l_std_dev_demand   NUMBER := 0;
    l_demand_cv        NUMBER := 0;  -- Coefficient of variation

  BEGIN
    -- Collect monthly demand
    FOR rec IN (
      SELECT TO_CHAR(TRUNC(transaction_date, 'MM'), 'YYYY-MM') AS month_key,
             SUM(NVL(transaction_quantity, 0)) AS monthly_demand
      FROM   mtl_material_transactions mmt,
             mtl_transaction_types mtt
      WHERE  mmt.transaction_type_id = mtt.transaction_type_id
      AND    mtt.transaction_action_id IN (1, 2, 3)  -- Issues
      AND    mmt.inventory_item_id = p_item_id
      AND    mmt.organization_id = p_organization_id
      AND    mmt.transaction_date > ADD_MONTHS(SYSDATE, -p_lookback_months)
      GROUP  BY TO_CHAR(TRUNC(transaction_date, 'MM'), 'YYYY-MM')
      ORDER  BY month_key
    ) LOOP
      l_monthly_demand(rec.month_key) := rec.monthly_demand;
      l_total_demand := l_total_demand + rec.monthly_demand;
      l_month_count := l_month_count + 1;
    END LOOP;

    -- Average monthly demand
    IF l_month_count > 0 THEN
      l_avg_monthly_demand := l_total_demand / l_month_count;
    END IF;

    -- Calculate standard deviation
    IF l_month_count > 1 THEN
      FOR i IN 1..l_month_count LOOP
        l_demand_variance := l_demand_variance
          + POWER(l_monthly_demand(l_month_key) - l_avg_monthly_demand, 2);
      END LOOP;
      l_std_dev_demand := SQRT(l_demand_variance / (l_month_count - 1));

      -- Coefficient of variation (demand variability)
      IF l_avg_monthly_demand > 0 THEN
        l_demand_cv := l_std_dev_demand / l_avg_monthly_demand;
      END IF;
    END IF;

    -- Store demand parameters
    MERGE INTO xx_inv_demand_params dp
    USING (SELECT p_item_id AS item_id, p_organization_id AS org_id FROM DUAL) src
    ON (dp.inventory_item_id = src.item_id
        AND dp.organization_id = src.org_id)
    WHEN MATCHED THEN
      UPDATE SET
        avg_monthly_demand    = l_avg_monthly_demand,
        demand_std_dev        = l_std_dev_demand,
        demand_cv             = l_demand_cv,
        calculation_date      = SYSDATE,
        lookback_months       = p_lookback_months,
        demand_analysis_period = l_month_count
    WHEN NOT MATCHED THEN
      INSERT (
        inventory_item_id, organization_id,
        avg_monthly_demand, demand_std_dev, demand_cv,
        calculation_date, lookback_months, demand_analysis_period
      ) VALUES (
        p_item_id, p_organization_id,
        l_avg_monthly_demand, l_std_dev_demand, l_demand_cv,
        SYSDATE, p_lookback_months, l_month_count
      );

    COMMIT;
  END calculate_demand_parameters;

  PROCEDURE calculate_all_items(
    p_organization_id NUMBER
  ) IS
    CURSOR item_cur IS
      SELECT inventory_item_id
      FROM   mtl_system_items_b
      WHERE  organization_id = p_organization_id
      AND    planning_active_flag = 'Y'
      AND    inventory_item_flag = 'Y';
  BEGIN
    FOR rec IN item_cur LOOP
      calculate_demand_parameters(
        p_organization_id => p_organization_id,
        p_item_id        => rec.inventory_item_id
      );
    END LOOP;
  END calculate_all_items;

END xx_demand_calc_pkg;
/
```

### Step 3: Calculate Lead Time Parameters

```sql
CREATE OR REPLACE PACKAGE xx_leadtime_calc_pkg AS

  PROCEDURE calculate_item_leadtime(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_lookback_months NUMBER DEFAULT 6
  );

END xx_leadtime_calc_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_leadtime_calc_pkg AS

  PROCEDURE calculate_item_leadtime(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_lookback_months NUMBER DEFAULT 6
  ) IS

    l_avg_leadtime     NUMBER := 0;
    l_leadtime_std_dev NUMBER := 0;
    l_max_leadtime     NUMBER := 0;
    l_leadtime_count   NUMBER := 0;

  BEGIN
    -- Calculate lead time from recent POs
    SELECT AVG(NVL(plh.lead_time, 0)) AS avg_lead,
           STDDEV(NVL(plh.lead_time, 0)) AS std_lead,
           MAX(NVL(plh.lead_time, 0)) AS max_lead,
           COUNT(*) AS lead_count
    INTO   l_avg_leadtime, l_leadtime_std_dev,
           l_max_leadtime, l_leadtime_count
    FROM   po_headers_all poh,
           po_lines_all pol,
           po_line_locations_all pll,
           po_distributions_all pod,
           po_lead_times_v plh
    WHERE  poh.po_header_id = pol.po_header_id
    AND    pol.po_line_id = pll.po_line_id
    AND    pll.line_location_id = pod.line_location_id
    AND    pod.inventory_item_id = p_item_id
    AND    pod.organization_id = p_organization_id
    AND    poh.creation_date > ADD_MONTHS(SYSDATE, -p_lookback_months)
    AND    poh.authorization_status = 'APPROVED';

    -- If no PO history, use item setup lead time
    IF l_leadtime_count = 0 THEN
      SELECT NVL(preprocessing_lead_time, 0)
             + NVL(processing_lead_time, 0)
             + NVL(postprocessing_lead_time, 0)
      INTO   l_avg_leadtime
      FROM   mtl_system_items_b
      WHERE  inventory_item_id = p_item_id
      AND    organization_id = p_organization_id;

      l_leadtime_std_dev := l_avg_leadtime * 0.3;  -- Assume 30% variability
    END IF;

    -- Store lead time parameters
    MERGE INTO xx_inv_leadtime_params lp
    USING (SELECT p_item_id AS item_id, p_organization_id AS org_id FROM DUAL) src
    ON (lp.inventory_item_id = src.item_id
        AND lp.organization_id = src.org_id)
    WHEN MATCHED THEN
      UPDATE SET
        avg_leadtime_days      = l_avg_leadtime,
        leadtime_std_dev        = l_leadtime_std_dev,
        max_leadtime_days       = l_max_leadtime,
        leadtime_data_points    = l_leadtime_count,
        last_calculation_date   = SYSDATE
    WHEN NOT MATCHED THEN
      INSERT (
        inventory_item_id, organization_id,
        avg_leadtime_days, leadtime_std_dev,
        max_leadtime_days, leadtime_data_points,
        last_calculation_date
      ) VALUES (
        p_item_id, p_organization_id,
        l_avg_leadtime, l_leadtime_std_dev,
        l_max_leadtime, l_leadtime_count,
        SYSDATE
      );

    COMMIT;
  END calculate_item_leadtime;

END xx_leadtime_calc_pkg;
/
```

### Step 4: Calculate Safety Stock and Reorder Point

The safety stock calculation uses service level factors from the normal distribution:

```sql
CREATE OR REPLACE PACKAGE xx_reorder_calc_pkg AS

  -- Service level to z-factor mapping
  FUNCTION get_z_factor(
    p_service_level NUMBER  -- e.g., 0.95 for 95%
  ) RETURN NUMBER;

  -- Calculate safety stock using standard formula
  -- SS = Z × σ_d × √LT  (where σ_d = demand std dev, LT = lead time)
  FUNCTION calculate_safety_stock(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_service_level   NUMBER DEFAULT 0.95
  ) RETURN NUMBER;

  -- Calculate reorder point
  -- ROP = d × LT + SS  (where d = avg daily demand, LT = lead time, SS = safety stock)
  FUNCTION calculate_reorder_point(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_service_level   NUMBER DEFAULT 0.95
  ) RETURN NUMBER;

  -- Calculate min-max levels
  -- Min = ROP, Max = Min + (EOQ or coverage days × daily demand)
  PROCEDURE calculate_min_max(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_coverage_days   NUMBER DEFAULT 30,
    p_service_level   NUMBER DEFAULT 0.95
  );

  PROCEDURE calculate_all_items(
    p_organization_id NUMBER
  );

END xx_reorder_calc_pkg;
/

CREATE OR REPLACE PACKAGE BODY xx_reorder_calc_pkg AS

  FUNCTION get_z_factor(
    p_service_level NUMBER
  ) RETURN NUMBER IS
    -- Standard normal z-values for common service levels
    -- Interpolate for intermediate values
    l_z NUMBER;
  BEGIN
    IF p_service_level >= 0.999 THEN l_z := 3.09;
    ELSIF p_service_level >= 0.99  THEN l_z := 2.33;
    ELSIF p_service_level >= 0.98  THEN l_z := 2.05;
    ELSIF p_service_level >= 0.97  THEN l_z := 1.88;
    ELSIF p_service_level >= 0.96  THEN l_z := 1.75;
    ELSIF p_service_level >= 0.95  THEN l_z := 1.64;
    ELSIF p_service_level >= 0.94  THEN l_z := 1.56;
    ELSIF p_service_level >= 0.93  THEN l_z := 1.48;
    ELSIF p_service_level >= 0.92  THEN l_z := 1.41;
    ELSIF p_service_level >= 0.91  THEN l_z := 1.34;
    ELSIF p_service_level >= 0.90  THEN l_z := 1.28;
    ELSIF p_service_level >= 0.85  THEN l_z := 1.04;
    ELSIF p_service_level >= 0.80  THEN l_z := 0.84;
    ELSE                              l_z := 0.00;
    END IF;

    RETURN l_z;
  END get_z_factor;

  FUNCTION calculate_safety_stock(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_service_level   NUMBER DEFAULT 0.95
  ) RETURN NUMBER IS

    l_avg_daily_demand   NUMBER;
    l_demand_std_dev     NUMBER;
    l_avg_leadtime       NUMBER;
    l_leadtime_std_dev   NUMBER;
    l_z_factor           NUMBER;
    l_safety_stock       NUMBER;

  BEGIN
    -- Get demand parameters
    SELECT NVL(avg_monthly_demand, 0) / 30 AS daily_demand,
           NVL(demand_std_dev, 0)
    INTO   l_avg_daily_demand, l_demand_std_dev
    FROM   xx_inv_demand_params
    WHERE  inventory_item_id = p_item_id
    AND    organization_id = p_organization_id;

    -- Get lead time parameters
    BEGIN
      SELECT NVL(avg_leadtime_days, 0),
             NVL(leadtime_std_dev, 0)
      INTO   l_avg_leadtime, l_leadtime_std_dev
      FROM   xx_inv_leadtime_params
      WHERE  inventory_item_id = p_item_id
      AND    organization_id = p_organization_id;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        l_avg_leadtime := 14;  -- Default 14 days
        l_leadtime_std_dev := 4.2;
    END;

    l_z_factor := get_z_factor(p_service_level);

    -- Safety stock with demand and lead time variability
    -- SS = Z × √(LT × σ_d² + d² × σ_LT²)
    l_safety_stock := l_z_factor * SQRT(
        (l_avg_leadtime * l_demand_std_dev * l_demand_std_dev)
      + (l_avg_daily_demand * l_avg_daily_demand * l_leadtime_std_dev * l_leadtime_std_dev)
    );

    RETURN ROUND(l_safety_stock, 0);
  END calculate_safety_stock;

  FUNCTION calculate_reorder_point(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_service_level   NUMBER DEFAULT 0.95
  ) RETURN NUMBER IS

    l_avg_daily_demand NUMBER;
    l_avg_leadtime     NUMBER;
    l_safety_stock     NUMBER;
    l_reorder_point    NUMBER;

  BEGIN
    -- Average daily demand
    SELECT NVL(avg_monthly_demand, 0) / 30
    INTO   l_avg_daily_demand
    FROM   xx_inv_demand_params
    WHERE  inventory_item_id = p_item_id
    AND    organization_id = p_organization_id;

    -- Average lead time
    BEGIN
      SELECT NVL(avg_leadtime_days, 14)
      INTO   l_avg_leadtime
      FROM   xx_inv_leadtime_params
      WHERE  inventory_item_id = p_item_id
      AND    organization_id = p_organization_id;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        l_avg_leadtime := 14;
    END;

    l_safety_stock := calculate_safety_stock(p_item_id, p_organization_id, p_service_level);

    -- ROP = Demand during lead time + Safety stock
    l_reorder_point := ROUND(l_avg_daily_demand * l_avg_leadtime + l_safety_stock, 0);

    RETURN l_reorder_point;
  END calculate_reorder_point;

  PROCEDURE calculate_min_max(
    p_item_id         NUMBER,
    p_organization_id NUMBER,
    p_coverage_days   NUMBER DEFAULT 30,
    p_service_level   NUMBER DEFAULT 0.95
  ) IS

    l_avg_daily_demand  NUMBER;
    l_reorder_point     NUMBER;
    l_max_level         NUMBER;
    l_eoq               NUMBER;
    l_ordering_cost     NUMBER := 50;     -- Default $50 per order
    l_holding_cost_pct  NUMBER := 0.25;   -- 25% annual holding cost
    l_unit_cost         NUMBER;
    l_annual_demand     NUMBER;

  BEGIN
    -- Get daily demand
    SELECT NVL(avg_monthly_demand, 0) / 30,
           NVL(avg_monthly_demand, 0) * 12 AS annual_demand
    INTO   l_avg_daily_demand, l_annual_demand
    FROM   xx_inv_demand_params
    WHERE  inventory_item_id = p_item_id
    AND    organization_id = p_organization_id;

    -- Get item cost
    SELECT NVL(unit_standard_cost, 0)
    INTO   l_unit_cost
    FROM   cst_item_costs
    WHERE  inventory_item_id = p_item_id
    AND    organization_id = p_organization_id
    AND    cost_type_id = 1;  -- Standard cost

    -- Calculate EOQ
    -- EOQ = √(2 × D × S / H) where D=annual demand, S=ordering cost, H=holding cost per unit
    IF l_unit_cost > 0 AND l_annual_demand > 0 THEN
      l_eoq := ROUND(
        SQRT(2 * l_annual_demand * l_ordering_cost
             / (l_unit_cost * l_holding_cost_pct)),
        0
      );
    ELSE
      l_eoq := l_avg_daily_demand * 30;  -- Default 30 days coverage
    END IF;

    -- Min = Reorder Point
    l_reorder_point := calculate_reorder_point(p_item_id, p_organization_id, p_service_level);

    -- Max = Min + (coverage days × daily demand) or EOQ, whichever is larger
    l_max_level := l_reorder_point + GREATEST(l_eoq, l_avg_daily_demand * p_coverage_days);

    -- Update item with min-max values
    UPDATE mtl_system_items_b
    SET min_minmax_quantity = l_reorder_point,
        max_minmax_quantity = l_max_level,
        min_max_method = 'MN',  -- Min-max planning
        planning_time_fence_days = ROUND(l_avg_daily_demand * l_reorder_point / 
                            GREATEST(l_avg_daily_demand, 1)),
        fixed_order_quantity = l_eoq
    WHERE inventory_item_id = p_item_id
    AND organization_id = p_organization_id;

    -- Store planning parameters
    MERGE INTO xx_inv_planning_params pp
    USING (SELECT p_item_id AS item_id, p_organization_id AS org_id FROM DUAL) src
    ON (pp.inventory_item_id = src.item_id
        AND pp.organization_id = src.org_id)
    WHEN MATCHED THEN
      UPDATE SET
        reorder_point         = l_reorder_point,
        safety_stock          = calculate_safety_stock(p_item_id, p_organization_id, p_service_level),
        min_quantity          = l_reorder_point,
        max_quantity          = l_max_level,
        eoq                   = l_eoq,
        service_level_target  = p_service_level,
        calculation_date      = SYSDATE
    WHEN NOT MATCHED THEN
      INSERT (
        inventory_item_id, organization_id,
        reorder_point, safety_stock, min_quantity, max_quantity,
        eoq, service_level_target, calculation_date
      ) VALUES (
        p_item_id, p_organization_id,
        l_reorder_point, calculate_safety_stock(p_item_id, p_organization_id, p_service_level),
        l_reorder_point, l_max_level, l_eoq, p_service_level, SYSDATE
      );

    COMMIT;
  END calculate_min_max;

  PROCEDURE calculate_all_items(
    p_organization_id NUMBER
  ) IS
    CURSOR item_cur IS
      SELECT msib.inventory_item_id,
             msib.organization_id,
             NVL(msib.attribute1, 'C') AS abc_class
      FROM   mtl_system_items_b msib
      WHERE  msib.organization_id = p_organization_id
      AND    msib.planning_active_flag = 'Y'
      AND    msib.inventory_item_flag = 'Y';

    l_service_level NUMBER;
  BEGIN
    FOR rec IN item_cur LOOP
      -- Assign service level based on ABC class
      IF rec.abc_class = 'A' THEN
        l_service_level := 0.98;
      ELSIF rec.abc_class = 'B' THEN
        l_service_level := 0.95;
      ELSE
        l_service_level := 0.90;
      END IF;

      calculate_min_max(
        p_item_id         => rec.inventory_item_id,
        p_organization_id => rec.organization_id,
        p_coverage_days   => CASE rec.abc_class
                              WHEN 'A' THEN 15
                              WHEN 'B' THEN 30
                              ELSE 45
                             END,
        p_service_level   => l_service_level
      );
    END LOOP;
  END calculate_all_items;

END xx_reorder_calc_pkg;
/
```

### Step 5: Generate Replenishment Recommendations

```sql
CREATE OR REPLACE PROCEDURE xx_generate_replenishment (
  p_organization_id NUMBER
) IS

  CURSOR replenishment_cur IS
    SELECT msib.inventory_item_id,
           msib.segment1 AS item_code,
           msib.description,
           msib.min_minmax_quantity,
           msib.max_minmax_quantity,
           msib.primary_uom_code,
           NVL(moq.transaction_quantity, 1) AS minimum_order_qty,
           NVL(moq.fixed_order_amount, 0) AS fixed_order_qty,
           NVL(pp.safety_stock, 0) AS safety_stock,
           NVL(pp.service_level_target, 0.95) AS service_level,
           -- Current on-hand inventory
           NVL((
             SELECT SUM(NVL(mt.onhand_quantity, 0))
             FROM   mtl_onhand_quantities mt
             WHERE  mt.inventory_item_id = msib.inventory_item_id
             AND    mt.organization_id = msib.organization_id
           ), 0) AS current_onhand,
           -- Open PO quantity
           NVL((
             SELECT SUM(NVL(pll.quantity - NVL(pll.quantity_received, 0), 0))
             FROM   po_lines_all pol,
                    po_line_locations_all pll
             WHERE  pol.po_line_id = pll.po_line_id
             AND    pol.item_id = msib.inventory_item_id
             AND    pll.organization_id = msib.organization_id
             AND    pll.closed_code = 'OPEN'
             AND    pll.cancel_flag = 'N'
           ), 0) AS open_po_quantity,
           -- Open requisition quantity
           NVL((
             SELECT SUM(NVL(prl.quantity, 0))
             FROM   po_requisition_lines_all prl
             WHERE  prl.item_id = msib.inventory_item_id
             AND    prl.source_organization_id = msib.organization_id
             AND    prl.approved_flag = 'Y'
             AND    prl.cancel_flag = 'N'
           ), 0) AS open_req_quantity
    FROM   mtl_system_items_b msib
    LEFT JOIN xx_inv_planning_params pp
      ON   msib.inventory_item_id = pp.inventory_item_id
      AND  msib.organization_id = pp.organization_id
    LEFT JOIN mtl_item_order_quantities moq
      ON   msib.inventory_item_id = moq.inventory_item_id
      AND  msib.organization_id = moq.organization_id
    WHERE  msib.organization_id = p_organization_id
    AND    msib.planning_active_flag = 'Y'
    AND    msib.inventory_item_flag = 'Y'
    AND    msib.min_max_method = 'MN'
    AND    msib.min_minmax_quantity IS NOT NULL;

  l_net_requirements   NUMBER;
  l_order_quantity     NUMBER;

BEGIN
  DELETE FROM xx_replenishment_recommendations
  WHERE organization_id = p_organization_id;

  FOR rec IN replenishment_cur LOOP
    -- Calculate net requirements
    -- Net = Max - (On Hand + Open PO + Open Req)
    l_net_requirements := rec.max_minmax_quantity
                        - (rec.current_onhand
                           + rec.open_po_quantity
                           + rec.open_req_quantity);

    -- Only recommend if inventory is below reorder point
    IF rec.current_onhand + rec.open_po_quantity + rec.open_req_quantity
       < rec.min_minmax_quantity
       AND l_net_requirements > 0
    THEN
      -- Round up to minimum order quantity
      l_order_quantity := CEIL(l_net_requirements / rec.minimum_order_qty)
                          * rec.minimum_order_qty;

      -- Insert recommendation
      INSERT INTO xx_replenishment_recommendations (
        organization_id, inventory_item_id, item_code, description,
        current_onhand, open_po_quantity, open_req_quantity,
        min_quantity, max_quantity, reorder_point, safety_stock,
        recommended_order_qty, uom_code, recommendation_date,
        recommendation_status, priority
      ) VALUES (
        p_organization_id, rec.inventory_item_id, rec.item_code,
        rec.description, rec.current_onhand, rec.open_po_quantity,
        rec.open_req_quantity, rec.min_minmax_quantity,
        rec.max_minmax_quantity, rec.min_minmax_quantity,
        rec.safety_stock, l_order_quantity, rec.primary_uom_code,
        SYSDATE, 'PENDING',  -- Pending review
        CASE
          WHEN rec.current_onhand < rec.safety_stock THEN 'CRITICAL'
          WHEN rec.current_onhand < rec.min_minmax_quantity * 0.5 THEN 'HIGH'
          WHEN rec.current_onhand < rec.min_minmax_quantity * 0.8 THEN 'MEDIUM'
          ELSE 'LOW'
        END
      );
    END IF;
  END LOOP;

  COMMIT;

  DBMS_OUTPUT.PUT_LINE('Generated replenishment recommendations for '
    || SQL%ROWCOUNT || ' items');
END;
/
```

### Step 6: Inventory Health Dashboard

```sql
CREATE OR REPLACE VIEW xx_inventory_health_dashboard AS
SELECT msib.organization_id,
       ood.organization_code,
       msib.segment1 AS item_code,
       msib.description,
       NVL(msib.attribute1, 'C') AS abc_class,
       NVL(msib.attribute2, 'N') AS fsn_class,
       NVL(pp.reorder_point, 0) AS reorder_point,
       NVL(pp.safety_stock, 0) AS safety_stock,
       NVL(pp.service_level_target, 0) AS target_service_level,
       NVL(onhand.onhand_qty, 0) AS current_onhand,
       NVL(pp.safety_stock, 0) - NVL(onhand.onhand_qty, 0) AS safety_stock_deficit,
       NVL(onhand.onhand_qty, 0) / NULLIF(NVL(pp.reorder_point, 1), 0) AS inventory_position_ratio,
       CASE
         WHEN NVL(onhand.onhand_qty, 0) = 0 THEN 'STOCKOUT'
         WHEN NVL(onhand.onhand_qty, 0) < NVL(pp.safety_stock, 0) THEN 'BELOW_SAFETY'
         WHEN NVL(onhand.onhand_qty, 0) < NVL(pp.reorder_point, 0) THEN 'BELOW_ROP'
         WHEN NVL(onhand.onhand_qty, 0) > NVL(pp.max_quantity, 0) * 1.2 THEN 'OVERSTOCK'
         ELSE 'HEALTHY'
       END AS inventory_health_status,
       DENSE_RANK() OVER (
         ORDER BY CASE
           WHEN NVL(onhand.onhand_qty, 0) = 0 THEN 0
           WHEN NVL(onhand.onhand_qty, 0) < NVL(pp.safety_stock, 0) THEN 1
           WHEN NVL(onhand.onhand_qty, 0) < NVL(pp.reorder_point, 0) THEN 2
           ELSE 3
         END
       ) AS health_priority
FROM   mtl_system_items_b msib
JOIN   org_organization_definitions ood
  ON   msib.organization_id = ood.organization_id
LEFT JOIN xx_inv_planning_params pp
  ON   msib.inventory_item_id = pp.inventory_item_id
  AND  msib.organization_id = pp.organization_id
LEFT JOIN (
  SELECT inventory_item_id, organization_id,
         SUM(NVL(onhand_quantity, 0)) AS onhand_qty
  FROM   mtl_onhand_quantities
  GROUP  BY inventory_item_id, organization_id
) onhand
  ON   msib.inventory_item_id = onhand.inventory_item_id
  AND  msib.organization_id = onhand.organization_id
WHERE  msib.planning_active_flag = 'Y'
AND    msib.inventory_item_flag = 'Y';
```

---

## Best Practices

### Classification Strategy
1. **ABC + FSN matrix**: Classify items on both value and velocity — A-F items get daily monitoring, C-N items get monthly review
2. **Recalculate quarterly**: Demand patterns shift; rerun classification every 3 months using trailing 12 months of data
3. **New item handling**: Assign default A-class for first 90 days until demand history accumulates; then reclassify
4. **Seasonal item handling**: Use 24-month lookback for seasonal items to capture full yearly cycle; calculate separate safety stock for peak vs off-peak

### Safety Stock Calculation
1. **Service level by class**: A=98%, B=95%, C=90% — higher service levels exponentially increase safety stock cost
2. **Lead time variability**: Track actual supplier lead times; a supplier with 30-day average and 15-day stddev doubles safety stock vs a supplier with consistent 30-day delivery
3. **Demand variability**: High CV (>1.0) items may need different planning approach — consider demand shaping or make-to-order rather than safety stock
4. **Minimum safety stock**: Always maintain at least 1 week of demand as safety stock regardless of calculation

### Min-Max Parameters
1. **Coverage days by class**: A=15 days, B=30 days, C=45 days — align with service level targets
2. **EOQ integration**: Use EOQ as the order-up-to quantity; for slow-movers, use min order quantity instead
3. **Multi-location planning**: Calculate min-max per warehouse; central DC carries higher max to support regional warehouses
4. **Dynamic adjustment**: Automatically increase coverage days during known peak seasons (holiday, harvest)

### Common Pitfalls
1. **Ignoring lead time variability**: 80% of stockouts are caused by lead time variability, not demand variability — always track actual vs. planned lead times
2. **Uniform service levels**: 98% service level across all items causes 40% more inventory than tiered service levels with same overall availability
3. **No demand signal filtering**: Remove abnormal demand (promotions, returns, one-time events) from demand history before calculating parameters
4. **Min-max too wide**: Large gaps between min and max cause order batching at max level — keep max no more than 2x min
5. **Not accounting for MOQ**: Supplier minimum order quantities can inflate inventory by 300% — negotiate MOQ reductions for A items

## Performance Metrics

| Metric | Target | Formula |
|--------|--------|---------|
| Fill rate (A items) | 98% | Orders shipped complete / Orders placed |
| Inventory turns | 6x/year | COGS / Average inventory value |
| Days on hand | 60 days | Current inventory / Avg daily usage |
| Safety stock accuracy | +/- 10% | Actual stockouts vs predicted stockouts |
| Order cycle time | 3 days | PO placement to receipt |
| Excess inventory | < 5% of total | Inventory > 180 days on hand |
| Stockout frequency | < 1% | Stockout occurrences / total orders |
| Replenishment lead time | 14 days | Avg supplier lead time |
