# Step-by-Step: Probability in Java

## Monty Hall Simulation

```java
public static double montyHall(int trials, boolean switchDoor) {
    Random rng = new Random();
    int wins = 0;
    for (int t = 0; t < trials; t++) {
        int car = rng.nextInt(3);
        int pick = rng.nextInt(3);
        // Host reveals a goat behind a door that isn't picked or car
        int reveal;
        do { reveal = rng.nextInt(3); }
        while (reveal == pick || reveal == car);
        if (switchDoor) {
            // Switch to the other unopened door
            int newPick;
            do { newPick = rng.nextInt(3); }
            while (newPick == pick || newPick == reveal);
            pick = newPick;
        }
        if (pick == car) wins++;
    }
    return (double) wins / trials;
}
```

## Bayesian Spam Filter

```java
public static double spamProbability(String email, Map<String, Double> wordProbs,
                                      double priorSpam) {
    double logOdds = Math.log(priorSpam / (1 - priorSpam));
    for (String word : email.split(" ")) {
        Double p = wordProbs.get(word);
        if (p != null && p > 0 && p < 1)
            logOdds += Math.log(p / (1 - p));
    }
    return 1 / (1 + Math.exp(-logOdds));
}
```

## Birthday Paradox

```java
public static double birthdayProbability(int people) {
    double p = 1.0;
    for (int i = 0; i < people; i++)
        p *= (365.0 - i) / 365.0;
    return 1 - p;
}
// 23 people → ~50.7% chance of shared birthday
```
