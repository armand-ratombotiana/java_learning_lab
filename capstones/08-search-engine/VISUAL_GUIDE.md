# Visual Guide: Search Engine

## Index Structure
```
[Document 1: "Java is fast"]
    |
    v
Analyzer -> Tokens: [java] [is] [fast]
    |
    v
Inverted Index:
    java  -> (doc=1, freq=1, pos=[0])
    is    -> (doc=1, freq=1, pos=[1])
    fast  -> (doc=1, freq=1, pos=[2])

[Document 2: "Java Spring Framework"]
    |
    v
Inverted Index (after doc 2):
    java    -> (doc=1,freq=1,pos=[0]), (doc=2,freq=1,pos=[0])
    is      -> (doc=1,freq=1,pos=[1])
    fast    -> (doc=1,freq=1,pos=[2])
    spring  -> (doc=2,freq=1,pos=[1])
    framework -> (doc=2,freq=1,pos=[2])
```

## Query Flow
```
Query: "java spring"
    |
QueryParser -> BooleanQuery(must=[TermQuery("java"), TermQuery("spring")])
    |
IndexSearcher -> Lookup "java" -> docs [1,2]
              -> Lookup "spring" -> docs [2]
              -> Intersection -> doc 2
              -> Compute BM25 score for doc 2
              -> Return ranked results
```

## Segment Architecture
```
Index Directory/
    +-- segment_0/
    |   +-- terms.tim (FST dictionary)
    |   +-- postings.pos
    |   +-- norms.nvm
    |   +-- fields.fld
    +-- segment_1/
    |   +-- ...
    +-- pending_segments (new writes)
    +-- segments_N (commit point)
```
