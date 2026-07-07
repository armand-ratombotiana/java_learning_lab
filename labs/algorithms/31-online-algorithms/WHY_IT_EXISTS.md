# Why Online Algorithms Exist

Online algorithms exist because most real-world decisions are made without full knowledge of the future. An operating system scheduler does not know which processes will start. A stock trader does not know future prices. A cache does not know which pages will be requested next. The online model formalizes this reality.

Online algorithms exist because competitive analysis provides a rigorous framework for evaluating algorithms under uncertainty. Instead of average-case analysis (which requires distributional assumptions) or worst-case analysis (which is too pessimistic for offline settings), competitive analysis compares against an optimal offline algorithm that sees the entire input in advance.

The ski rental problem exists as a canonical example for analyzing trade-offs between commitment (buying) and flexibility (renting). It models many real decisions: subscribing to a service vs paying per use, investing in infrastructure vs renting capacity, learning a skill vs hiring.

The secretary problem exists because optimal stopping is a fundamental decision-making pattern: hiring the best candidate, selling an asset at the best price, finding the best parking spot, choosing the best apartment.

Multi-armed bandits exist because all learning systems face the exploration-exploitation dilemma. An online advertising platform must both show the best-known ads (exploitation) and try new ads (exploration). A clinical trial must allocate patients to the best-known treatment while gathering data on new treatments.

Online algorithms exist to provide a theoretical foundation for decision-making under uncertainty, bridging algorithm design, game theory, and machine learning.