# Interview Questions — Feature Flags

## Q1: What is the difference between a release toggle and an experiment toggle?
**A:** A release toggle controls rollout of a completed feature (on/off). An experiment toggle serves different variations to different user groups to measure performance (A/B test).

## Q2: How do you avoid technical debt from feature flags?
**A:** Implement flag lifecycle: when a flag is permanently ON, remove the conditional code and the flag definition. Use flag expiration dates and automated cleanup alerts.

## Q3: How does LaunchDarkly handle targeting?
**A:** Flags have rules based on user attributes (key, email, custom). Rules can target specific users, percentage buckets, or groups. The SDK evaluates rules client-side or server-side.

## Q4: What is a kill switch and how does it help?
**A:** An ops toggle that can immediately disable a problematic feature in production without a deploy. Critical for rapid incident response.
