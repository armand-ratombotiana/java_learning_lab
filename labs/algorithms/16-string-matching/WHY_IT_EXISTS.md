# Why String Matching Algorithms Exist

String matching is one of the oldest and most fundamental problems in computer science. Before efficient algorithms existed, searching for a pattern in text required naive O(nm) approaches, which became prohibitively slow as text sizes grew. The development of linear-time string matching algorithms in the 1970s revolutionized text processing.

The naive approach compares the pattern against every possible starting position in the text. For a text of length n and pattern of length m, this requires O(nm) character comparisons. As documents grew from kilobytes to megabytes to gigabytes, this quadratic behavior became unacceptable.

KMP was developed in 1970-1977 as the first linear-time string matching algorithm. It introduced the concept of using information from previous comparisons to avoid re-examining matched characters. This was a breakthrough because it showed that string matching could be solved in worst-case O(n) time without any preprocessing of the text.

Boyer-Moore (1977) took a different approach by scanning from right to left, enabling it to skip large portions of the text. For typical patterns and natural language text, Boyer-Moore examines only about n/m characters, making it dramatically faster in practice.

Rabin-Karp (1987) introduced a randomized approach using rolling hashes. While its worst-case performance matches naive, its expected linear time and ability to handle two-dimensional pattern matching make it valuable for specific applications like plagiarism detection.

The Z-algorithm (1984) provided an elegant alternative to KMP with simpler implementation. Its linear-time Z-array computation is both simple and powerful.

Aho-Corasick (1975) generalized KMP to handle multiple patterns simultaneously, which is essential for virus scanning, intrusion detection, and bioinformatics applications where many signatures must be checked simultaneously.
