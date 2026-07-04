# Architecture

## Layers
```
[Source Systems] -> [Staging Layer] -> [Transform Engine] -> [Target Layer]
```

## Spring Batch Flow
```
Job Launcher -> Job -> Step -> ItemReader -> ItemProcessor -> ItemWriter
```
