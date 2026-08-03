# Lab 07: Mock Interview — Time Series Analysis

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Moving averages, exponential smoothing, autocorrelation, trend, seasonality, decomposition, forecasting

---

**Interviewer**: "Walk me through the tools in this lab for understanding a time series."

**Candidate**: "A layered toolkit, each layer answering one question. The simple moving
average — `sma`, SMA(k) = average of the last k points — answers 'what's the recent
level?' and smooths noise, at the cost of lag. The exponential moving average — `ema`,
EMA_t = alpha·x_t + (1-alpha)·EMA_{t-1} — answers the same question with exponentially
decaying weights, no fixed window, and less lag for the same smoothing. Autocorrelation —
`autocorrelation(lag)` — answers 'is the past correlated with the future?' and is the
diagnostic that tells you whether any smoothing or forecasting is even worth doing.
And decomposition — `decompose`, splitting the series into trend, seasonal, and
residual components — answers 'what structure is in here?'. The lab's demo shows each on
real data: the demand forecaster prints SMA(4) and EMA(0.3) windows, autocorrelation
values at lags 1-4, the full decomposition of a constructed series, and a forecast of
84.38 for the next period."

**Interviewer**: "Derive the SMA and EMA and compare their behaviors."

**Candidate**: "SMA(k) at time t is the unweighted average of the last k observations.
Its variance shrinks like 1/k — good — but it reacts to a change only after k periods,
and every observation carries equal weight whether it's 20 minutes old or 2 minutes old.
EMA replaces the fixed window with exponential decay: EMA_t = alpha·x_t + (1-alpha)·
EMA_{t-1}, which unfolds to a weighted average where observation j periods back carries
weight alpha·(1-alpha)^j. Same smoothing role, two different lag-vs-noise dials: SMA
lets you choose the window explicitly; EMA's effective window is about 2/alpha, and the
lab's `ema` demo with alpha = 0.3 traces a middle path between the raw series and the
4-period SMA. The practical rule: SMA for simple reporting, EMA for online monitoring
where you want each new point to react immediately."

**Interviewer**: "How do you choose the window size or alpha?"

**Candidate**: "It's the bias-variance trade-off of smoothing: large windows (small alpha)
kill noise but lag the signal; small windows (large alpha) react fast but keep the
noise. Two principled routes. One: pick alpha by the effective window you want — the
rule of thumb alpha = 2/(k+1) makes the EMA's center of mass match an SMA(k). Two: tune
by out-of-sample error — for the demand forecaster's EMA(0.3) choice, hold out the last
few periods and pick alpha to minimize forecast error, which the lab's `ema` and
`forecastNext` pair makes possible. The monitoring lens matters too: for alerting on
slow drift you want a long baseline and a tight threshold; for detecting fast
degradation you want a short baseline and a wider threshold — the window is a business
decision about how fast you must detect what."

**Interviewer**: "What does autocorrelation measure, and why is lag 1 the usual starting point?"

**Candidate**: "Autocorrelation at lag k is the correlation between the series and itself
shifted k periods: r_k = corr(x_t, x_{t-k}). Positive autocorrelation means past highs
tend to follow highs — momentum or persistent level; negative means oscillation — a
high tends to follow a low. Lag 1 is the strongest and most informative because
dependence decays with distance in most real series: if the process has memory, the
nearest neighbor carries the most of it, and the lag-1 value is the summary statistic of
that memory. The lab's demo: the demand series shows autocorrelation values around
0.953-0.956 at lags 1-3 — persistent series, where the current value is a strong
predictor of the next — and the fourth lag jumps to 0.9986, which is the weekly pattern
announcing itself. That single diagnostic — check lag 1 first, then the seasonal lag —
is the fastest read of a series' structure."

**Interviewer**: "How do you detect seasonality?"

**Candidate**: "Two complementary routes. The autocorrelation signature: a seasonal series
spikes at the seasonal lag — the demo's 0.9986 at lag 4 for weekly data with daily
observations is the fingerprint. And the decomposition route: the lab's `decompose`
extracts the seasonal component directly by averaging the detrended series per
seasonal position, producing seasonal indices that show the weekly pattern's shape —
and the residual component, which is what's left after trend and seasonality are
removed. The operational test: if the seasonal component is large relative to the
residuals, the seasonality is real and the forecast must include it; if removing it
leaves the residuals looking random, the model is complete. The lab's constructed
series — trend plus seasonal plus noise — decomposes back into exactly those pieces,
which is the verification that the decomposition machinery works."

**Interviewer**: "Walk through the lab's decomposition algorithm."

**Candidate**: "Classic additive decomposition, in three passes. First estimate the trend
with a centered moving average — the lab's `sma` with a window matching the period —
which averages out the seasonality. Second, detrend: subtract the trend estimate from
the raw series; the residual is seasonal-plus-noise. Third, average those residuals at
each seasonal position — all Monday-ish positions, all Tuesday-ish, etc. — to get the
seasonal component; the noise is what remains after subtracting trend and seasonal from
the raw series. The lab's `decompose` returns all three components as arrays plus the
trend line's forecasts, and the demo runs it on the constructed series: trend recovered,
seasonal indices matching the planted pattern, residuals near the planted noise level.
The verification loop is the whole point: build a series with known structure, decompose
it, and confirm you get the structure back."

**Interviewer**: "How do you forecast the next value with these tools?"

**Candidate**: "The lab's `forecastNext` pattern: for the naive forecast use the latest
value; for the smoothed forecast use the latest EMA or SMA; and for the full
decomposition, project the trend forward and add the seasonal component — trend
forecast plus the matching seasonal index. The demand demo's forecast of 84.38 for the
next period is exactly that: the trend carries the level forward, the seasonal index
adds the expected weekly shape, and the residual is omitted because its expected value
is zero. The three forecasting levels mirror the three stages of analysis: smoothing
forecasts the level, decomposition forecasts the level plus seasonality, and the
autocorrelation diagnostics tell you which stage the series actually supports. Always
pair the point forecast with a range — the residual standard deviation is the natural
width, and the honest statement is 'next week around 84, typically within ±10'."

**Interviewer**: "What do the lab's autocorrelation results say about the demand series?"

**Candidate**: "The demo's four lags — 0.956150, 0.953152, 0.953942, 0.998624 — tell a
clear story. Lags 1-3 are all around 0.95: the series has strong short-term memory —
today's demand is a strong predictor of tomorrow's — which justifies the smoothing
models (SMA/EMA) and makes forecasting meaningful at all. Then lag 4 jumps to 0.9986:
the weekly seasonality dominates the lag-4 relationship, the fingerprint of a series
with a 4-period cycle. The interpretation ladder: strong lag-1 → smooth and forecast;
strong lag-4 on top → add seasonality to the forecast; weak autocorrelation everywhere →
the series is noise-dominated and no smoothing model will help — the honest answer is
'forecast the mean and widen the error bars'. Reading the ladder in order is the whole
method."

**Interviewer**: "What are the failure modes of these smoothing tools?"

**Candidate**: "Five, each with a signature. Lag: SMA(k) reports the past level as the
current one after a step change — the chart shows the model 'catching up'; the demo's
SMA(4) visibly trails the raw series. Oversmoothing: a huge window flattens real
structure — the decomposition then attributes to trend what is actually a level shift.
Seasonality ignored: forecasting with plain EMA on a seasonal series systematically
misses the seasonal peaks — lag-4 autocorrelation flags this before it hurts. Anchoring
on the trend: naive trend projection on a saturating series over-forecasts — the
decomposition's residuals become the canary. And the false-pattern trap: white noise
produces autocorrelation estimates that look meaningful by chance — the lab's
diagnostics are the guard, and the rule is 'no autocorrelation, no smoothing, no
forecast'."

**Interviewer**: "How do you distinguish a random walk from a mean-reverting series?"

**Candidate**: "By the autocorrelation, and the random walk is the boundary case: a random
walk's differences are white noise, so its level wanders forever and its lag-1
autocorrelation hovers near 1 — the current value is the best forecast of the next, and
error bars grow with sqrt(t). A mean-reverting series shows negative lag-1
autocorrelation — a high value tends to be followed by a lower one — and the best
forecast is pulled back toward the mean, not the last value. The practical test: check
the autocorrelation of the *differenced* series — white noise in the differences is the
random-walk signature; significant structure means there's a model beyond 'last value'.
For the lab's demand series, lag-1 autocorrelation near 0.956 says strong persistence —
a smoothing model captures it — while a random walk would demand differencing and the
seasonality would need separate handling."

**Interviewer**: "How does this lab's material connect to forecasting ML models?"

**Candidate**: "This lab is the feature-engineering layer for any forecasting model.
Autocorrelation selects the lags: the lag-4 spike tells the model to include the same
day last week, not just yesterday. The decomposition produces the target engineering:
forecast the detrended, deseasonalized residual with a model and add the components back
— or feed trend and seasonal indices in as features, which is the same information in
regression form. EMA features are the classic baseline that gradient-boosted models
rarely beat by much. And the failure-mode discipline transfers: if the residuals from
your ML forecast still show autocorrelation, your model has missed structure, and the
lab's diagnostics are how you check it. The summary: smoothing and decomposition are not
the endpoint of time-series modeling — they are the starting point of every serious
one."

**Interviewer**: "You're monitoring daily revenue with this toolkit. Design the alerting."

**Candidate**: "Three layers. Layer one: level detection — track the EMA with alpha tuned
to the detection window; a sustained deviation of the raw value from the EMA beyond
2-3 residual standard deviations is the candidate alert; layer two: seasonal guard —
revenue is weekly-seasonal, so compare against the same day last week plus the trend,
using the decomposition's seasonal indices, not against the overall mean — otherwise
every Monday 'drops' and every Friday 'spikes' and the alerts train people to ignore
them. Layer three: autocorrelation awareness — after a structural change (new feature,
price change), the residuals' autocorrelation rises, and that is itself a signal the
baseline is stale and the model must be rebuilt. The lab's tools are exactly the stack:
`ema` for the level, `decompose` for the seasonal baseline, autocorrelation for the
model-health check."
