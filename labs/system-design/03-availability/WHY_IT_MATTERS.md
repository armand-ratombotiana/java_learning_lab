# Availability - WHY IT MATTERS

## Business Impact

### Cost of Downtime
| Tier | Annual Downtime Allowed | Cost Per Minute |
|------|------------------------|-----------------|
| 99% (Two 9s) | 87.6 hours | $1,000 |
| 99.9% (Three 9s) | 8.76 hours | $5,600 |
| 99.99% (Four 9s) | 52.6 minutes | $25,000 |
| 99.999% (Five 9s) | 5.26 minutes | $250,000+ |

### Financial Impact Per Hour
- **Amazon**: $13M/hour during Prime Day
- **Google**: $6M/hour
- **Facebook**: $3.5M/hour
- **Netflix**: $3M/hour

## Key Reasons It Matters

### 1. User Retention
53% of users abandon a site that takes >3 seconds to load. Any downtime is worse.

### 2. SLA Consequences
Enterprise contracts have uptime SLAs with penalties: 10-30% monthly credit per 0.1% below target.

### 3. Regulatory Fines
Healthcare downtime exceeding 4 hours can result in HIPAA fines. Trading systems have mandated uptime.

### 4. Competitive Disadvantage
When a competitor's system is down, you win. When yours is down, they win.

### 5. Operational Morale
Frequent outages burn out engineers. On-call fatigue is a leading cause of turnover.

## Real-World Examples
- **GitLab (2017)**: Database deletion → 18 hours recovery → rebuilt backup procedures
- **Amazon S3 (2017)**: Typo caused 4-hour outage → affected half the internet
- **AWS Kinesis (2020)**: Throttling bug → 24-hour impaired service
- **Fastly CDN (2021)**: Software bug → global 1-hour outage for major sites
