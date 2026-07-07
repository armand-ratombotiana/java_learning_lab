# Online Algorithms — Mental Models

## Driving Without GPS

An online algorithm is like driving to a destination without a GPS. You see each intersection (decision point) as you arrive, and you must choose which way to turn without knowing the road ahead. You might take a wrong turn but must live with the consequences. The competitive ratio measures how much longer your route is compared to someone with a GPS who knows the perfect route.

## Cache as a Desk with Limited Space

Working on a desk (cache) with limited space: you need reference books (pages) to work. Sometimes you need a book not on your desk (page fault), so you go to the library (disk) to get it. You must decide which book to remove from the desk to make space. LRU removes the book you used longest ago, FIFO removes the book that was on the desk the longest, and Marker marks books used in the current work session.

## Ski Rental as Subscription Dilemma

The ski rental problem is like deciding whether to subscribe to a streaming service or rent individual movies. Rent one movie for $5, subscribe for $20/month. If you watch 10+ movies, subscribing wins. But you don't know how many movies you'll watch this month. The optimal strategy: rent 3 movies (B-1 if B=4), then subscribe. This guarantees you never pay more than twice the optimal.

## Secretary Problem as Dating

The secretary problem models optimal stopping in dating: date n people one by one, after each date decide to settle or continue. You cannot go back to someone you rejected. The optimal strategy is to date the first 37% of people to establish a baseline (reject all), then marry the first person who is better than everyone seen so far.

## Epsilon-Greedy as Trying New Restaurants

You know a few restaurants you like. Most days you go to your current favorite (exploitation). But some days (with probability epsilon), you try a new random restaurant (exploration). Over time, you might discover a new favorite. The epsilon controls how often you explore vs exploit. Too much exploration wastes time at bad restaurants; too little might miss a better one.