# Lab 08: Mock Interview — Principal Component Analysis

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Covariance matrix, eigendecomposition, power iteration, deflation, explained variance, dimensionality reduction

---

**Interviewer**: "Walk me through the PCA pipeline this lab implements, end to end."

**Candidate**: "Five stages. First, `center(X)` subtracts each column's mean —
PCA is about variance around the mean, so the data must be zero-centered. Second,
`covariances(Xc)` builds the covariance matrix Σ = (1/(n−1))·XcᵀXc — note the
n−1, the sample covariance with Bessel's correction, and the symmetry: the loop
computes `cov[i][j]` and mirrors it to `cov[j][i]`. Third, `powerIterate(A, 1000)`
finds the dominant eigenvector by repeatedly multiplying A and normalizing.
Fourth, `deflate(A, ev, lambda)` subtracts λ·vvᵀ from A so the next power
iteration finds the second component instead of the first again. Fifth,
`project(Xc, components)` dots each centered row against each component. The demo
runs this twice and projects the 10×4 data to 10×2."

**Interviewer**: "Why power iteration, of all methods, for the eigen-decomposition?"

**Candidate**: "Because it's the cheapest method that suffices when you only need
the top few components — and that's exactly the PCA use case. Power iteration
starts with a random vector, multiplies by A, and normalizes each round; the
dominant eigen-direction grows as λ₁ᵏ while the others decay as λᵢᵏ, so after
hundreds of iterations only the top eigenvector survives. It's O(d²) per
iteration and needs no matrix factorization. The tradeoff: convergence rate is
λ₂/λ₁, so it crawls when eigenvalues are close — and it only finds the top
eigenvector, which is why `deflate` peels components off one at a time. For a
4×4 covariance in the demo, a full Jacobi or QR decomposition would be
overkill."

**Interviewer**: "Explain what `deflate` is doing mathematically."

**Candidate**: "It removes the just-found component from the matrix. The rank-1
matrix λ·vvᵀ reconstructs the component's contribution to A, so
A' = A − λ·vvᵀ is the original covariance with the dominant direction zeroed
out. Mathematically it's the spectral decomposition in progress: the eigenvalues
of A' are the remaining eigenvalues of A, and the next power iteration on A'
converges to the second eigenvector. The lab's flow is elegant: iterate to find
v₁, deflate to hide v₁, iterate to find v₂. The demo's PC1 lambda is 2.045 and
PC2 is 0.061 — the second component captures almost nothing, which is exactly
what the variance math confirms."

**Interviewer**: "The demo reduces 4-D data to 2-D. How do you justify the number
of components?"

**Candidate**: "With explained variance ratio: λᵢ / Σλⱼ — each eigenvalue's share
of the total variance, where the denominator is the trace of the covariance.
The walkthrough extends the lab to compute this and gets PC1 = 95.35% and PC2 =
2.86%, a cumulative 98.21% in two components. That's the textbook justification:
choose k so cumulative explained variance crosses a threshold — commonly 95% —
or use the scree plot's elbow, exactly like the inertia elbow in the K-Means
lab. In this data, two components carry 98% of the variance, so the 4-D face
embeddings were effectively 2-D to begin with — the compression is nearly
lossless."

**Interviewer**: "The demo does NOT standardize the features before PCA. Is that a
bug?"

**Candidate**: "It's the classic PCA gotcha, and worth calling out. PCA is
sensitive to feature scale: variance is the objective, so a feature on a scale
of 0–100 dominates the covariance and the top component becomes 'that feature'
rather than the true structure. The demo's synthetic features are on comparable
scales, so it works by luck of construction — real 4-D data with one feature in
dollars would produce a garbage projection. The fix is the z-score
standardization from the interview guide before `center`: scale each feature to
unit variance, and then `center` is a no-op since z-scores are already
zero-mean. The honest answer: standardization should happen by default, and
'does it matter here' is a scale question, not a taste question."

**Interviewer**: "Why is PCA often described as 'finding directions of maximum
variance' — how does that connect to the math?"

**Candidate**: "The covariance matrix encodes how features co-vary around the
mean. Its eigenvectors are the axes where that variation is orthogonal to
everything else, and the eigenvalues are the variance along those axes. So
finding the top eigenvector literally maximizes the projected variance: for a
unit vector v, the variance of the projection X·v is vᵀΣv, and the
eigendecomposition gives vᵀΣv = λ at the eigenvectors — power iteration is
climbing exactly that quadratic. That's why the eigenvalues double as the
explained-variance numerators: the variance of the data along PC1 is λ₁, so the
fraction λ₁/trace(Σ) is the share of total variance that PC1 'explains'."

**Interviewer**: "PCA via covariance eigendecomposition vs via SVD — when do you
care?"

**Candidate**: "Numerical stability and scale. Forming XcᵀXc squares the
condition number of the data, which can lose precision on ill-conditioned
matrices; SVD of the centered data matrix — X = U·S·Vᵀ — gets the same principal
components as V's columns, the singular values squared relate to eigenvalues
(S²/(n−1) = λ), and it never forms the covariance at all. With the lab's 10×4
demo, either works — the power-iteration path is chosen for pedagogy: no external
linear-algebra library, and the pieces (iterate, deflate, project) each map to a
concept. In production at Meta scale, the embedding matrix is 10⁹×d and you'd
use randomized SVD — the same components, computed approximately, in a fraction
of the time."

**Interviewer**: "Are principal components interpretable? How would you answer a
product manager asking what PC1 'means'?"

**Candidate**: "Honestly: often not. Each component is a linear combination of
the original features — the demo's PC1 is some blend of all four embedding
dimensions — so the weights are a direction in feature space, not a named
concept. Sometimes you get lucky: a component with weights concentrated on
'facial asymmetry' features is close to interpretable. The PM answer is to
reframe: PC1 is 'the axis along which faces differ most in this embedding',
which is meaningful as a similarity axis even without a name. The interview
point: this interpretability gap is why PCA is usually a preprocessing step —
for clustering, visualization, denoising — rather than the final, explainable
model."

**Interviewer**: "How does PCA connect to the K-Means lab and this lab series?"

**Candidate**: "It's the bridge between them. Clustering in Lab 07 operates on
distance, and distance collapses in high dimension — the curse of
dimensionality from Lab 05. PCA is the standard cure: project the high-D data
onto its top components first, then cluster in the low-D space where distance is
meaningful again. The walkthrough makes the pipeline concrete: 4-D face
embeddings become 2-D points with 98% of the variance kept, and those 2-D
points are exactly what a K-Means run would consume. The caution from the
interview guide: PCA optimizes variance, not separability — two small but
important clusters living on a low-variance axis can be destroyed by the
projection, so I keep the components that carry the structure I care about, not
just the ones with the biggest lambdas."

**Interviewer**: "What does the projected data in the demo tell you, reading the
numbers?"

**Candidate**: "The projection output is the 10×2 matrix — each face's position on
the two component axes. Looking at it, the first column spans roughly −2.29 to
+2.09: that's the spread along PC1, which the explained-variance math says is
95% of the total — the row differences we see in dimension 1 are almost the
whole story of the dataset. The second column spans −0.36 to +0.48: a
narrow, nearly irrelevant axis, which is consistent with PC2's 2.86%. Reading
projections this way — spread per column versus the eigenvalues — is the
practical skill: the plotted cloud is 2-D, but one of those dimensions is
visually a line, which is the fingerprint of a dataset with effective
dimensionality 1."

**Interviewer**: "How would you use PCA as a denoiser?"

**Candidate**: "Project to the top-k components and project back: X̂ =
proj·V_kᵀ. If the noise lives in the low-variance directions, this kills it —
the reconstruction error is exactly the variance of the discarded components.
The demo's numbers make the promise concrete: keeping two components retains
98.21% of the variance, so the reconstruction from 2-D is within 1.79% of the
original 4-D data in variance terms. The caveat: this only denoises if the
signal is genuinely high-variance and the noise is low-variance, which is true
for face embeddings and photo features but not for, say, binary flags. It's the
same reason PCA-compressed embeddings power approximate nearest-neighbor search:
the storage drops by 2x with almost no recall loss."

**Interviewer**: "How do you apply a fitted PCA to a new data point at serving
time?"

**Candidate**: "The fit produces two artifacts: the per-feature means from
`center` and the component matrix. For a new point, subtract the stored means —
not the mean of the new point — then dot against each component, which is
exactly `project(Xc, components)` with a one-row matrix. The subtle bug to
avoid: recomputing the mean from the new point's batch, or re-running power
iteration at serving time. The lab's `project` is the serving function; `center`
and `powerIterate` are the training functions, and separating those roles is the
whole deployment story — which is also why the walkthrough keeps the training
numbers (lambdas, explained variance) as the artifact to review before
shipping."
