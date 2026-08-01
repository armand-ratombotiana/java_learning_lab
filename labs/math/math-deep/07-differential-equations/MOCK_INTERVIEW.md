# Mock Interview: Runge-Kutta 4th Order ODE Solver

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Simulation Engineer (Physics / Financial Modeling Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Numerical ODE solvers, order of accuracy, stability, adaptive stepping
**Problem**: Implement the Runge-Kutta 4th-order (RK4) solver for systems of first-order ODEs, with step-size choice, error analysis, and a convergence-order demonstration.
**Language**: Java 21+ (records, functional interfaces allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. State the RK4 tableau/stages. Where do the coefficients 1/2, 1/2, 1 come from?
2. What is the local truncation error of RK4, and what is the global error?
3. How do you verify 4th-order convergence numerically?
4. When does RK4 fail — stiffness, discontinuities, stability limits?
5. How would you implement adaptive step-size control (RK45 / Dormand-Prince)?
6. Follow-up: how does a system of ODEs map into the same solver?

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "Our option-pricing group needs an ODE solver for calibration curves, and the physics team needs one for a small mechanical system. I want a single RK4 implementation for systems of first-order ODEs. Clarify."

**Candidate**: "Two things to pin down. First, the form: I'll assume y'(t) = f(t, y) with y an n-dimensional state vector — higher-order ODEs like y'' = g(t, y, y') get reduced to first-order systems by introducing velocity states; I'll demonstrate that reduction. Second, what the deliverable is: a solver that returns the state at each time step — the trajectory — plus a convergence-order diagnostic, since '4th order' is a claim you should be able to verify numerically."

**Interviewer**: "Right. Give me the solver plus the verification."

**Candidate**: "Then the structure is: a `SystemFunction` interface f(t, y) -> dy/dt; a `StepResult` record (time, state); a `solve` method that walks t₀ → t_end with a fixed step; and a `convergenceOrder` harness that runs the solver at step sizes h, h/2, h/4 and measures how the error scales — for RK4 the error should shrink by 2⁴ = 16 when h halves."

### Part 2: Theory (10 minutes)

**Interviewer**: "Write down the four stages."

**Candidate**: "Given yₙ at tₙ with step h:

k₁ = f(tₙ, yₙ)
k₂ = f(tₙ + h/2, yₙ + (h/2)k₁)
k₃ = f(tₙ + h/2, yₙ + (h/2)k₂)
k₄ = f(tₙ + h, yₙ + h·k₃)
yₙ₊₁ = yₙ + (h/6)(k₁ + 2k₂ + 2k₃ + k₄)

The intuition: k₁ is the slope at the start; k₂ is the slope at the midpoint using k₁'s Euler prediction; k₃ is a corrected midpoint slope; k₄ is the slope at the end using k₃. The weights 1/6, 2/6, 2/6, 1/6 are Simpson's-rule-like quadrature weights."

**Interviewer**: "Why are the coefficients exactly those? Derive it."

**Candidate**: "Expand y(tₙ + h) in a Taylor series at tₙ. Compare the expansion of the RK4 step with the expansion of the true solution to order h⁴. The true solution has terms y, y', y''h²/2, y'''h³/6, y''''h⁴/24. The RK step, when you expand each kᵢ as a Taylor series in h and collect powers, matches the true expansion *through the h⁴ term* — five terms matched, four stages available. The coefficients 1/2, 1/2, 1 and the weights 1/6, 2/6, 2/6, 1/6 are the unique solution of the order-4 consistency equations. The local truncation error is then O(h⁵) — the first unmatched term — and the global error, summing LTE over 1/h steps, is O(h⁴). That's the defining property of a 4th-order method."

**Interviewer**: "And when h halves, the global error does what?"

**Candidate**: "Drops by a factor of 2⁴ = 16. That's the empirical signature: compute the error at h, h/2, h/4, ... on a problem with a known closed-form solution; the log-log slope of error vs h must be 4. If you see slope 2 or 3, there's an implementation bug — usually a mistyped stage coefficient or using the *local* error as if it were global."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the API for systems."

**Candidate**:

```java
@FunctionalInterface
public interface OdeFunction {
    double[] apply(double t, double[] y);
}
```

`RK4Solver.solve(f, y0, t0, t1, h, OutputConsumer)` — but I'd actually return the trajectory: a list of `Step(t, y)`. And a separate `ErrorAnalysis` utility that, given a closed-form solution, runs the solver at halving steps and returns the empirical order. The vector operations (add, scale, axpy) are small static helpers — no external math library, since the lab's dependency budget is zero."

**Interviewer**: "What about the initial condition and step count edge cases?"

**Candidate**: "Validate: t1 ≥ t0, h > 0, y0 non-null/non-empty, and — subtle — the step count n = ceil((t1 - t0)/h) so that the last step lands exactly on t1 rather than overshooting; I'll clip the final step to (t1 - tₙ)."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the core step."

**Candidate**:

```java
public static double[] step(OdeFunction f, double t, double[] y, double h) {
    int n = y.length;
    double[] k1 = f.apply(t, y);
    double[] k2 = f.apply(t + h / 2, axpy(y, h / 2, k1));
    double[] k3 = f.apply(t + h / 2, axpy(y, h / 2, k2));
    double[] k4 = f.apply(t + h, axpy(y, h, k3));
    double[] out = new double[n];
    for (int i = 0; i < n; i++) {
        out[i] = y[i] + h / 6.0 * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
    }
    return out;
}
```

**Interviewer**: "Good. Now the solver loop."

**Candidate**:

```java
public static List<Step> solve(OdeFunction f, double[] y0, double t0, double t1, double h) {
    List<Step> trajectory = new ArrayList<>();
    double[] y = y0.clone();
    double t = t0;
    trajectory.add(new Step(t, y.clone()));
    while (t < t1 - 1e-12) {
        double hEff = Math.min(h, t1 - t);
        y = step(f, t, y, hEff);
        t += hEff;
        trajectory.add(new Step(t, y.clone()));
    }
    return trajectory;
}
```

**Interviewer**: "What's your convergence-order harness?"

**Candidate**: "Solve a problem with known solution — the classic: y' = -2ty, y(0) = 1, solution e^{-t²}. Run with h = 0.1, 0.05, 0.025, 0.0125; compute max error at t = 1 for each; print the ratios error(h)/error(h/2) — must approach 16, and log2 of the ratio approaches 4."

### Part 5: Testing (5 minutes)

**Interviewer**: "Test cases?"

**Candidate**: "Four layers. (1) Convergence order on y' = -2ty — the slope must be 4. (2) The classic spring-mass system y'' = -y, y(0) = 1, y'(0) = 0 — as a 2-vector system, verifying the first-order reduction works and the energy stays bounded for small h. (3) The stiff-ish test y' = -50(y - cos t) — demonstrates that fixed-step RK4 needs a tiny h for stability; the exact solution is known so I can show the accuracy cliff. (4) The famous non-commutative trap — no wait, the right 4th test is the *symmetric* case: two independent ODEs sharing a single solver call, verifying the system machinery handles n = 2 correctly with zero cross-talk."

**Interviewer**: "And the last — what about stiffness, since you mentioned it?"

**Candidate**: "RK4's stability region along the negative real axis extends to about -2.78/h — with eigenvalue -50, that forces h < 0.055; the 'accuracy cliff' test makes that visible. For stiff problems the correct tool is an implicit method (backward Euler, implicit midpoint, Radau) — I'd mention that in production notes, since a fixed-step explicit method is the wrong tool there. It's the classic 'method works but user chooses h badly' failure."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "How do you go adaptive?"

**Candidate**: "Use a 5th-order companion — the Dormand-Prince (RK45) embedding: the same six stage evaluations produce both a 4th-order and 5th-order solution, and their difference estimates the local error. If the estimate exceeds tol, halve h; if it's far below tol·safety, grow h up to some cap. This is the core of modern integrators (Matlab ode45, SciPy solve_ivp's default). The efficiency point: the embedded method costs only two extra stages beyond RK4 but gives you error control."

**Interviewer**: "What about the financial application — why would you prefer an adaptive method there?"

**Candidate**: "Calibration curves have regions of rapid change (near strikes/expiries) and flat regions; a fixed step must be chosen for the worst region and wastes 90% of its work elsewhere. Adaptive stepping concentrates evaluations where the 5th-order/4th-order difference is large. Also, tolerance-based stepping gives a *certified* error bound, which risk teams like for auditability."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | Derives stage coefficients from Taylor matching; states LTE O(h⁵), global O(h⁴) | States the tableau and global order | Writes RK4 by rote |
| Implementation | Clean system API, final-step clipping, vector helpers | Scalar-only or buggy vector ops | No validation |
| Verification | Log-log slope ≈ 4; ratios ≈ 16 | Error decreases but no slope | Single happy path |
| Analysis | Stability region, stiffness discussion, adaptive extension | Mentions stiffness | No failure-mode awareness |

## Red Flags
- Reusing mutated y arrays across stages (aliasing).
- Reporting local error as global error (factor of h confusion).
- Not clipping the final step — overshooting t1.
- Claiming RK4 is suitable for stiff problems.

## Key Takeaways
- RK4 = 4 evaluations, Simpson-weighted; matches Taylor through h⁴.
- Local error O(h⁵), global error O(h⁴) — verify with halving-h ratios ≈ 16.
- Reduce higher-order ODEs to first-order systems; the API is a vector function.
- Stability limit on the real axis ≈ -2.78/h — stiffness demands implicit methods.
