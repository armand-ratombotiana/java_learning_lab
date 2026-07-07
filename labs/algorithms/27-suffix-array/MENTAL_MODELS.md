# Suffix Array — Mental Models

## Phone Book for Suffixes

A suffix array is like a phone book listing every suffix of the string in alphabetical order. Just as you find a name in a phone book by binary searching, you find a pattern in a text by binary searching the suffix array. Each suffix is an entry in the book, and the phone number is its starting position.

## LCP Array as Shared Prefix Lengths

The LCP array is like measuring how much two consecutive entries in a phone book have in common at the start. If "Smith, A" and "Smith, B" share 8 characters (the "Smith, " part), that's the LCP. The LCP array tells us how much work was saved when sorting: consecutive suffixes are placed next to each other because they share long prefixes.

## Prefix-Doubling as Zooming Out

Imagine looking at suffixes through a zoom lens. In the first iteration, you see only the first character (zoom all the way in). In the second, you see 2 characters. Then 4, then 8, etc. Each iteration doubles the context, refining the relative ordering of suffixes. After log n iterations, you have enough context to distinguish all suffixes, and the zoom is complete.

## Kasai's Algorithm as Following a Trail

Kasai's algorithm processes suffixes in order of their starting positions, not their sorted order. Each step computes the LCP between the current suffix and the next suffix in sorted order. The key observation: the LCP of a suffix cannot be less than the LCP of the previous suffix minus one. This is like following a trail where each step tells you something about the next step.

## Suffix Array as an Inverted Index for Substrings

An inverted index maps words to their positions in documents. A suffix array maps every possible substring to its occurrence positions. If you search for "cat" in a suffix array, you find all positions where any suffix starts with "cat," which is exactly all occurrences of "cat" in the text.