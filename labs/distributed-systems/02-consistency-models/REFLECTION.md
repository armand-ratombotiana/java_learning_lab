# Consistency Models: Reflection

## Key Insights
- Consistency is a spectrum, not binary
- Stronger guarantees cost performance and availability
- Choose the weakest model that meets correctness requirements
- Client-side caching can introduce subtle consistency bugs

## Questions
1. What consistency model does your current system actually provide?
2. Do your users notice the difference between strong and eventual?
3. Could you relax consistency to improve performance?
4. What would break if consistency were weakened?

## Personal Notes
- The most expensive consistency guarantee is the one your users don't need
- Always measure - intuition about consistency overhead is often wrong
- Consider per-operation consistency rather than system-wide
