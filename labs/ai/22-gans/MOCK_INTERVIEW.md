# Mock Interview: GANs

## Question 1: GAN Architecture
**Q**: Explain the GAN framework. Implement a basic GAN.

**A**: GAN consists of two networks competing:
- **Generator G**: Creates fake data from random noise
- **Discriminator D**: Distinguishes real from fake

Minimax game: min_G max_D V(D,G) = E[log D(x)] + E[log(1 - D(G(z)))]

```python
class GAN:
    def __init__(self, latent_dim=100):
        self.G = nn.Sequential(
            nn.Linear(latent_dim, 256), nn.ReLU(),
            nn.Linear(256, 512), nn.ReLU(),
            nn.Linear(512, 784), nn.Tanh())  # 28x28 images
        self.D = nn.Sequential(
            nn.Linear(784, 512), nn.LeakyReLU(0.2),
            nn.Linear(512, 256), nn.LeakyReLU(0.2),
            nn.Linear(256, 1), nn.Sigmoid())

    def train_step(self, real_images):
        batch = real_images.size(0)

        # Train discriminator
        z = torch.randn(batch, 100)
        fake = self.G(z)
        real_loss = BCE(self.D(real_images), torch.ones(batch, 1))
        fake_loss = BCE(self.D(fake.detach()), torch.zeros(batch, 1))
        d_loss = (real_loss + fake_loss) / 2

        # Train generator (make discriminator believe fakes are real)
        z = torch.randn(batch, 100)
        fake = self.G(z)
        g_loss = BCE(self.D(fake), torch.ones(batch, 1))
```

## Question 2: GAN Training Challenges
**Q**: What are common GAN training problems? How do you address them?

**A**:
- **Mode collapse**: Generator produces limited varieties. Fix: minibatch discrimination, unrolled GANs, Wasserstein loss.
- **Non-convergence**: D and G oscillate. Fix: label smoothing, gradient penalties, spectral normalization.
- **Vanishing gradients**: D becomes too good, G gets no gradient. Fix: Wasserstein loss, least squares loss.
- **Training instability**: Losses fluctuate wildly. Fix: Adam optimizer, learning rate scheduling, TTUR.

**Practical tips**:
- Use LeakyReLU (not ReLU) in discriminator
- Use Adam (lr=2e-4, beta1=0.5)
- Batch normalization in both networks
- Label smoothing for discriminator
- Train D more than G (ratio 1:1 or 5:1)

## Question 3: GAN Variants
**Q**: Compare DCGAN, WGAN-GP, and StyleGAN.

**A**:
| Variant | Key Innovation | Benefit | 
|---------|---------------|---------|
| DCGAN | CNN architecture guidelines | Stable image generation |
| WGAN-GP | Wasserstein loss + gradient penalty | Solves mode collapse, stable training |
| StyleGAN | Style-based generator, AdaIN | Highest quality, controllable generation |

**WGAN loss**: Earth mover's distance instead of JS divergence.
- Remove sigmoid from D output
- Gradient penalty: E[(||nabla D(alpha*x + (1-alpha)*G(z))|| - 1)^2]
- More stable, less mode collapse

## Question 4: Conditional GANs
**Q**: How do you generate images conditioned on class labels or text?

**A**: Conditional GANs feed condition information to both G and D.

```python
class ConditionalGAN:
    def __init__(self, n_classes=10, latent_dim=100):
        self.G = nn.Sequential(
            nn.Linear(latent_dim + n_classes, 256), nn.ReLU(),
            nn.Linear(256, 784), nn.Tanh())
        self.D = nn.Sequential(
            nn.Linear(784 + n_classes, 512), nn.LeakyReLU(0.2),
            nn.Linear(512, 1), nn.Sigmoid())

    def forward(self, z, labels):
        c = F.one_hot(labels, 10)
        return self.G(torch.cat([z, c], dim=1))  # Generate conditioned on label
```

**Text-to-image (StackGAN, DALL-E)**: Use text embeddings instead of one-hot labels.

## Question 5: Evaluation of GANs
**Q**: How do you evaluate the quality and diversity of generated images?

**A**:
- **Inception Score (IS)**: Uses Inception-v3 classifier. High score = high quality + high diversity.
- **FID (Fréchet Inception Distance)**: FID = ||mu_r - mu_g||^2 + Tr(Sigma_r + Sigma_g - 2*(Sigma_r*Sigma_g)^(1/2)). Lower = better. Most commonly used.
- **Precision/Recall**: Precision = quality, Recall = diversity (mode coverage).
- **Human evaluation**: For subjective quality assessment.
- **FVD (Fréchet Video Distance)**: For video generation.

FID is the standard metric. FID < 10 is considered very good for most datasets.
