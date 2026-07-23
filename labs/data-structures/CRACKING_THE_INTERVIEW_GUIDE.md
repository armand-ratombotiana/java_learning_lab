# Cracking the Coding Interview — Data Structures Academy

## The Interview Process: What to Expect

### Phone Screen (30-45 min)
- **Format**: Video call with a shared code editor (CoderPad, HackerRank, Google Docs)
- **Content**: 1-2 medium problems focused on arrays, strings, hash tables
- **Goal**: Validate basic problem-solving and communication skills
- **Pass Rate**: ~40-60% advance to onsite

### Onsite (4-6 rounds, 45-60 min each)
- **Coding Rounds** (2-3): Data structure problems ranging from medium to hard
- **System Design** (1-2): How data structures scale in distributed systems
- **Behavioral** (1): Leadership principles, conflict resolution, collaboration
- **Hiring Manager** (1): Team fit, career goals, technical vision

### Virtual Onsite (Post-COVID Standard)
- All rounds conducted via Zoom/Google Meet with shared editor
- Same structure, but you'll code on your own machine
- Practice coding without autocomplete to simulate real conditions

---

## What Each Company Evaluates

| Company | Coding | System Design | Behavioral | Leadership |
|---------|--------|---------------|------------|------------|
| Google | ★★★★★ | ★★★★ | ★★★ | ★★★ |
| Meta | ★★★★★ | ★★★ | ★★ | ★★ |
| Amazon | ★★★★ | ★★★★★ | ★★★ | ★★★★★ |
| Microsoft | ★★★★ | ★★★★ | ★★★ | ★★★ |
| Apple | ★★★★ | ★★★ | ★★★ | ★★★ |
| Netflix | ★★★★ | ★★★★★ | ★★ | ★★★★ |
| Uber | ★★★★ | ★★★★ | ★★★ | ★★★ |
| Stripe | ★★★★★ | ★★★★ | ★★ | ★★ |
| Oracle | ★★★ | ★★★ | ★★★ | ★★★★ |

---

## The 5-Step Method for Any Problem

### Step 1: Understand (2-3 min)
- Restate the problem in your own words
- Ask clarifying questions: input size, negative numbers, duplicates, nulls, memory limits
- Walk through 1-2 examples manually
- Confirm edge cases: empty, single element, all same, max values

### Step 2: Brute Force (3-5 min)
- State the simplest possible solution
- Analyze time and space complexity
- **Prove you understand the problem before optimizing**
- Example: "The naive approach is to check every pair, which is O(n^2)"

### Step 3: Optimize (5-10 min)
- Identify bottlenecks in the brute force
- Consider: HashMap for O(1) lookup, two-pointer for sorted arrays, sliding window for subarrays
- Discuss trade-offs: time vs space, recursion vs iteration
- **Get buy-in** from the interviewer before coding
- Example: "We can use a HashMap to store complements, reducing to O(n)"

### Step 4: Implement (15-20 min)
- Write clean, well-named code
- Use helper methods for readability
- Handle edge cases first (null checks, empty inputs)
- Talk through your code as you write
- **Write production-quality code** — meaningful variable names, consistent formatting

### Step 5: Test (5-10 min)
- Walk through your example manually
- Test edge cases: empty, single, duplicates, negatives, overflow
- Analyze time and space complexity again
- Discuss potential follow-ups: streaming data, memory constraints, concurrency

---

## Company-Specific Interview Tips

### Google
- **Process**: Phone screen → 4-5 onsite rounds → HC (hiring committee)
- **Focus**: Problem-solving process > correct answer
- **Expect**: Hard follow-ups that change constraints
- **Tip**: Always analyze complexity unprompted; Google values clean, readable code
- **Common DS**: Trees, graphs, hash maps, tries, segment trees

### Meta
- **Process**: Phone screen → 3-4 onsite rounds (coding + behavioral)
- **Focus**: Speed and accuracy — 2 problems in 45 minutes
- **Expect**: Variations of the same core problems
- **Tip**: Practice talking while coding; Meta interviewers rarely interrupt
- **Common DS**: Arrays, hash maps, trees, recursion

### Amazon
- **Process**: Phone screen → 5 onsite rounds (including bar raiser)
- **Focus**: Scalability, leadership principles, customer obsession
- **Expect**: Design-a-data-structure questions (LRU, Bloom filter)
- **Tip**: Always discuss scale — "What if we have 1 billion items?"
- **Common DS**: Hash maps, LRU cache, Bloom filters, tries, concurrent DS

### Microsoft
- **Process**: Phone screen → 4-5 onsite rounds → "Asks" feedback
- **Focus**: Production-quality code, fundamentals
- **Expect**: Linked list manipulation, tree problems, recursion vs iteration
- **Tip**: Write clean, null-safe, production-ready code
- **Common DS**: Linked lists, trees, hash maps, stacks

### Apple
- **Process**: Phone screen → 5-6 onsite rounds → Team matching
- **Focus**: Memory efficiency, embedded systems constraints
- **Expect**: In-place algorithms, bit manipulation, memory-conscious design
- **Tip**: Consider cache lines, memory layout, recursion depth
- **Common DS**: Arrays, bit arrays, circular buffers, ropes

---

## Resume Preparation Tips

### Tailor for Each Company
- **Google**: Highlight projects with scale, optimization, complexity analysis
- **Meta**: Emphasize full-stack impact, feature ownership, data-driven decisions
- **Amazon**: Quantify results with metrics, show ownership and delivery
- **Microsoft**: Highlight cross-team collaboration, enterprise experience
- **Apple**: Emphasize performance optimization, memory management, hardware awareness

### Key Resume Rules
- **One page** unless you have 10+ years of experience
- **Quantify everything**: "Reduced latency by 40%" vs "Improved performance"
- **Use action verbs**: Designed, Implemented, Optimized, Led, Architected
- **Include relevant coursework**: Data structures, algorithms, distributed systems
- **List technologies** but focus on concepts, not just tools

---

## Behavioral Questions: STAR Method

### STAR Framework
| Component | What to Cover |
|-----------|---------------|
| **S**ituation | Context — what was the project/team/situation? |
| **T**ask | Your responsibility — what were you asked to do? |
| **A**ction | What you actually did — focus on your contribution |
| **R**esult | Outcome — quantify with metrics where possible |

### Common Behavioral Questions
- Tell me about a time you had a conflict with a teammate
- Describe a project where you had to make a trade-off
- Tell me about a time you failed
- How do you handle ambiguous requirements?
- Describe a time you mentored someone
- What's the most complex data structure you've implemented from scratch?

### Amazon Leadership Principles (Most Tested)
1. **Customer Obsession**: How does your work impact users?
2. **Ownership**: When did you go beyond your job description?
3. **Invent and Simplify**: How did you make something simpler?
4. **Deliver Results**: How did you overcome obstacles to ship?
5. **Dive Deep**: When did you analyze a problem at multiple levels?
6. **Hire and Develop the Best**: How have you helped others grow?

---

## Time Management During Interviews

### 45-Minute Coding Round
| Minute | Activity |
|--------|----------|
| 0-5 | Understand the problem, ask questions, walk through examples |
| 5-10 | Discuss approaches (brute force → optimal), get buy-in |
| 10-30 | Code the solution |
| 30-40 | Test with examples, handle edge cases |
| 40-45 | Analyze complexity, discuss follow-ups |

### If Stuck
- **5 min**: Try a different approach
- **10 min**: Ask for a hint (most interviewers will give one)
- **15 min**: Simplify the problem — solve a restricted version first
- **20 min**: State your best approach verbally, even if code is incomplete

---

## How to Handle Rejection

### Common Reasons for Rejection
- Poor communication / not talking through the approach
- Buggy code with unhandled edge cases
- Couldn't analyze time/space complexity
- Behavioral answers not aligned with company values
- Slow coding — didn't finish the problem

### Rejection Response Strategy
1. **Ask for feedback** (most companies will share general areas)
2. **Identify patterns**: Is it always coding? Always behavioral?
3. **Wait period**: Most companies require 6-12 months before re-applying
4. **Reapply stronger**: Solve 100 more problems, do 10+ mock interviews

### Mindset
- Rejection is normal — even top candidates face multiple rejections
- Each interview is practice for the next
- The difference between offer and rejection is often small (one bad round)

---

## Negotiation Tips

### Know Your Leverage
- **Multiple offers**: The strongest negotiating position
- **Current compensation**: Know your market worth (levels.fyi, Glassdoor)
- **Timeline**: If you have time, you have leverage

### What to Negotiate
1. **Base salary**: Typically 10-30% room above initial offer
2. **Equity/RSUs**: Can often be increased significantly
3. **Signing bonus**: Common for top candidates
4. **Relocation**: If moving cities
5. **Title**: Level matters for future career growth

### Script Framework
> "I'm very excited about [Company] and this role. Based on my research and other offers I'm considering, I was hoping for [specific number]. Is there flexibility in the compensation package?"

### Never
- Lie about existing offers
- Accept on the spot — ask for 24-48 hours
- Burn bridges — negotiation should be collaborative

---

## Study Plans

### 30-Day Intensive Plan
| Week | Focus | Daily | Target |
|------|-------|-------|--------|
| 1 | Arrays, Hash Tables, Strings | 4 hrs, 5 problems | Core pattern fluency |
| 2 | Linked Lists, Stacks, Trees | 4 hrs, 5 problems | Intermediate fluency |
| 3 | Heaps, Graphs, Tries | 4 hrs, 5 problems | Advanced fluency |
| 4 | Mock interviews, Company-specific | 5 hrs, 3-4 problems + mocks | Interview readiness |

### 60-Day Standard Plan
| Weeks | Focus | Problems | Milestone |
|-------|-------|----------|-----------|
| 1-2 | Arrays + Hash Tables + Strings | 30 | Core patterns mastered |
| 3-4 | Linked Lists + Stacks + Trees | 35 | Linked structures fluent |
| 5-6 | Heaps + Graphs + Tries | 30 | Advanced patterns started |
| 7-8 | Advanced DS + Company-specific + Mocks | 25 | Interview ready |

### 90-Day Comprehensive Plan
| Weeks | Focus | Problems | Detail |
|-------|-------|----------|--------|
| 1-2 | Arrays: two-pointer, sliding window, prefix sum | 20 | LC 1, 11, 15, 42, 53, 121, 238, 560 |
| 3-4 | Hash Tables + Strings | 20 | LC 3, 49, 76, 128, 242, 347, 424 |
| 5-6 | Linked Lists + Stacks | 20 | LC 19, 21, 141, 146, 155, 206, 239 |
| 7-8 | Trees (BFS, DFS, BST) | 25 | LC 94, 98, 102, 104, 124, 236, 297 |
| 9-10 | Heaps + Tries | 15 | LC 23, 208, 212, 215, 295, 347 |
| 11 | Graphs + Union-Find | 20 | LC 133, 200, 207, 269, 684, 721 |
| 12-13 | Advanced DS + Company-specific | 20 | LRU, Segment Tree, BIT, Spatial |
| 14 | Mock interviews + Review | 10+ mocks | Full-length simulations |

---

## Resources Organized by Company

### Google
- [LeetCode Google Tag](https://leetcode.com/company/google/)
- [Google Interview Prep](https://careers.google.com/how-we-hire/)
- YouTube: Life at Google, Google Engineering videos
- Books: Cracking the Coding Interview, System Design Interview

### Meta
- [LeetCode Meta Tag](https://leetcode.com/company/meta/)
- [Meta Careers Blog](https://www.metacareers.com/)
- YouTube: Meta Engineering
- Books: The Facebook Effect, System Design Interview

### Amazon
- [LeetCode Amazon Tag](https://leetcode.com/company/amazon/)
- [Amazon Leadership Principles](https://www.amazon.jobs/content/en/our-workplace/leadership-principles)
- YouTube: Amazon Engineering, AWS Re:Invent talks
- Books: Working Backwards, The Everything Store

### Microsoft
- [LeetCode Microsoft Tag](https://leetcode.com/company/microsoft/)
- [Microsoft Careers Blog](https://careers.microsoft.com/)
- YouTube: Microsoft Developer
- Books: Hit Refresh, System Design Interview

### General
- **Blind 75**: The 75 most common interview problems
- **NeetCode 150**: Curated problem list with video solutions
- **AlgoExpert**: Company-specific problem sets
- **Pramp / interviewing.io**: Free mock interviews
- **Levels.fyi**: Compensation data for all companies
- **Glassdoor**: Interview experiences by company

---

## Final Checklist

- [ ] Can implement HashMap from scratch
- [ ] Can write BFS/DFS for trees and graphs
- [ ] Can code LRU Cache in under 20 minutes
- [ ] Can solve any two-pointer problem
- [ ] Can analyze time/space complexity instantly
- [ ] Can answer behavioral questions using STAR
- [ ] Have completed 5+ mock interviews
- [ ] Have a target company study plan
- [ ] Can handle edge cases without prompting
- [ ] Can communicate clearly while coding
