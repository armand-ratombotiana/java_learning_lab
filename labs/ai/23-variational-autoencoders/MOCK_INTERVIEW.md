# Mock Interview: Variational Autoencoders

## Question 1: VAE Architecture
**Q**: Explain the VAE architecture and its key components.

**A**: VAE = Variational Autoencoder: generative model that learns a latent representation.

Components:
- **Encoder**: Maps input x to parameters of latent distribution q(z|x) = N(mu, sigma^2)
- **Latent sampling**: z = mu + sigma * epsilon (reparameterization trick)
- **Decoder**: Reconstructs x from z: p(x|z)

```python
class VAE(nn.Module):
    def __init__(self, input_dim=784, latent_dim=20):
        super().__init__()
        self.encoder = nn.Sequential(
            nn.Linear(input_dim, 256), nn.ReLU(),
            nn.Linear(256, 128), nn.ReLU())
        self.mu = nn.Linear(128, latent_dim)
        self.log_var = nn.Linear(128, latent_dim)
        self.decoder = nn.Sequential(
            nn.Linear(latent_dim, 128), nn.ReLU(),
            nn.Linear(128, 256), nn.ReLU(),
            nn.Linear(256, input_dim), nn.Sigmoid())

    def encode(self, x):
        h = self.encoder(x)
        return self.mu(h), self.log_var(h)

    def reparameterize(self, mu, log_var):
        std = torch.exp(0.5 * log_var)
        eps = torch.randn_like(std)
        return mu + eps * std

    def forward(self, x):
        mu, log_var = self.encode(x)
        z = self.reparameterize(mu, log_var)
        return self.decoder(z), mu, log_var
```

## Question 2: VAE Loss Function
**Q**: Derive and explain the VAE loss function.

**A**: VAE loss = Reconstruction loss + KL divergence

L = E[log p(x|z)] - D_KL(q(z|x) || p(z))

**Reconstruction term**: How well does the decoder reconstruct the input?
- Bernoulli: binary cross-entropy (for MNIST)
- Gaussian: MSE (for continuous data)

**KL divergence**: How close is the approximate posterior to the prior?
D_KL(N(mu, sigma^2) || N(0,1)) = -0.5 * sum(1 + log(sigma^2) - mu^2 - sigma^2)

```python
def vae_loss(recon_x, x, mu, log_var):
    BCE = F.binary_cross_entropy(recon_x, x.view(-1, 784), reduction='sum')
    KLD = -0.5 * torch.sum(1 + log_var - mu.pow(2) - log_var.exp())
    return BCE + KLD
```

## Question 3: Reparameterization Trick
**Q**: Why is the reparameterization trick necessary in VAEs?

**A**: The sampling operation z ~ N(mu, sigma^2) is non-differentiable. Backpropagation can't flow through a random node.

**Solution**: z = mu + sigma * epsilon where epsilon ~ N(0,1). This separates the randomness (epsilon) from the learnable parameters (mu, sigma). Gradients can flow through mu and sigma.

Without reparameterization: gradient estimator has high variance (REINFORCE-style).
With reparameterization: low-variance, differentiable, standard backpropagation works.

## Question 4: VAE vs AE
**Q**: Compare autoencoders with variational autoencoders. When to use each?

**A**:
| Aspect | Autoencoder (AE) | VAE |
|--------|-----------------|-----|
| Latent space | Deterministic, may not be continuous | Probabilistic, continuous |
| Generation | Poor (holes in latent space) | Good (smooth latent space) |
| Regularization | None or sparse (denoising AE) | KL divergence to prior |
| Loss | Reconstruction only | Reconstruction + KL |
| Training | Standard | Reparameterization required |
| Use case | Dimensionality reduction, anomaly | Generation, representation learning |

Use AE for: dimensionality reduction, feature extraction, anomaly detection (reconstruction error).
Use VAE for: generating new samples, interpolating in latent space, learning smooth representations.

## Question 5: VAE Extensions
**Q**: Describe VAE improvements: beta-VAE, VQ-VAE, and conditional VAE.

**A**:
- **beta-VAE**: L = recon - beta * KL. Higher beta = more disentangled representations (but lower reconstruction quality).
- **VQ-VAE**: Uses vector quantization for discrete latent space. Better for high-quality images. Used in DALL-E.
- **Conditional VAE**: Add condition to encoder and decoder (like class label). Enables controlled generation.
- **VAE + GAN**: Use discriminator loss for better reconstruction quality (VAE-GAN).
- **Hierarchical VAE**: Multiple latent levels. Better for complex distributions (NVAE, VQ-VAE-2).

```python
# Beta-VAE
def beta_vae_loss(recon, x, mu, log_var, beta=4.0):
    recon_loss = F.binary_cross_entropy(recon, x, reduction='sum')
    kl_loss = -0.5 * torch.sum(1 + log_var - mu.pow(2) - log_var.exp())
    return recon_loss + beta * kl_loss
```
