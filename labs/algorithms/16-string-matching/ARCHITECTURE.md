# Architecture — String Matching

## Layered Design

`
String Matching Library
├── Matcher Interface
│   ├── KMPMatcher
│   ├── BoyerMooreMatcher
│   ├── RabinKarpMatcher
│   ├── ZMatcher
│   └── AhoCorasickMatcher
├── Preprocessor
│   ├── PrefixFunction
│   ├── BadCharTable
│   └── GoodSuffixTable
├── Hash
│   └── RollingHash
└── Automaton
    ├── Trie
    ├── FailureLinks
    └── OutputLinks
`

## Integration Patterns

- Use as a library with a simple search(String text, String pattern) API
- Support streaming search for large files
- Provide builder pattern for configuration (choice of algorithm, hash parameters)
